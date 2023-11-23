package com.thelocalmarketplace.software.logic;

import com.jjjwelectronics.Mass;
import com.jjjwelectronics.Mass.MassDifference;
import com.jjjwelectronics.scanner.Barcode;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.external.ProductDatabases;
import com.thelocalmarketplace.software.AbstractLogicDependant;
import com.thelocalmarketplace.software.logic.StateLogic.States;

import ca.ucalgary.seng300.simulation.InvalidArgumentSimulationException;
import ca.ucalgary.seng300.simulation.InvalidStateSimulationException;
import ca.ucalgary.seng300.simulation.SimulationException;

/**
 * Handles all logic operations related to weight for a self checkout station
 * @author Angelina Rochon (30087177)
 * ----------------------------------
 * @author Connell Reffo (10186960)
 * @author Tara Strickland (10105877)
 * @author Julian Fan (30235289)
 * @author Braden Beler (30084941)
 * @author Samyog Dahal (30194624)
 * @author Maheen Nizmani (30172615)
 * @author Phuong Le (30175125)
 * @author Daniel Yakimenka (10185055)
 * @author Merick Parkinson (30196225)
 * @author Farida Elogueil (30171114)
 */
public class WeightLogic extends AbstractLogicDependant {
	
	/** expected weight change on software side */
	private Mass expectedWeight;
	
	/** actual weight on scale */
	private Mass actualWeight;
	
	/** Tolerance for weight difference before scale blocks due to discrepancy */
	private Mass sensitivity; 
	
	/** True if the bagging area scale is not over weight capacity; false otherwise */
	public boolean scaleOperational; 
	
	/** mass of all bags that have been added */
	private Mass bagMassTotal;

	
	public WeightLogic(CentralStationLogic logic) throws NullPointerException {
		super(logic);
		
		this.logic = logic;
		this.expectedWeight = Mass.ZERO;
		this.actualWeight = Mass.ZERO;
		this.sensitivity = logic.hardware.baggingArea.getSensitivityLimit();
		this.bagMassTotal = Mass.ZERO;
		this.scaleOperational = true;
	}
	
	public Mass getActualWeight() {
		return this.actualWeight;
	}
	
	/** sets total mass of current bags
	 * 
	 * @param m new mass at last event
	 **/
	public void updateTotalBagMass(Mass m) {
		this.bagMassTotal = m;
		
	}
	
	/** gets total mass of current bags
	 * 
	 * @param m new mass at last event
	 **/
	public Mass getTotalBagMass() {
		return this.bagMassTotal;
	}
	
	/** Getter for expected mass
	 * */
	public Mass getExpectedWeight(){
		return this.expectedWeight;
	}
	
	/** Adds the expected weight of the product with given barcode to the expectedWeight
	 * @param barcode barcode of the item for which to add the expected weight */
	public void addExpectedWeight(Barcode barcode) {
		if (!ProductDatabases.BARCODED_PRODUCT_DATABASE.containsKey(barcode)) {
			throw new InvalidStateSimulationException("Barcode not registered to product database");
		}
		BarcodedProduct product = ProductDatabases.BARCODED_PRODUCT_DATABASE.get(barcode);
		Mass mass = new Mass(product.getExpectedWeight());
		this.expectedWeight = this.expectedWeight.sum(mass);
	}
	
	/** Removes the weight of the product given from expectedWeight
	 * @param barcode - barcode of item to remove weight of */
	public void removeExpectedWeight(Barcode barcode) {
		if (!ProductDatabases.BARCODED_PRODUCT_DATABASE.containsKey(barcode)) {
			throw new InvalidStateSimulationException("Barcode not registered to product database");
		}
		BarcodedProduct product = ProductDatabases.BARCODED_PRODUCT_DATABASE.get(barcode);
		Mass mass = new Mass(product.getExpectedWeight());
		MassDifference difference = this.expectedWeight.difference(mass);
		if (difference.compareTo(Mass.ZERO) < 0) throw new InvalidStateSimulationException("Expected weight cannot be negative");
		this.expectedWeight = difference.abs();
	}
	
	/** UPdates actual weight to the mass passed
	 * @param mass - Mass to change the actual weight to */
	public void updateActualWeight(Mass mass) {
		this.actualWeight = mass;
	}
	
	/** Indicates that an item should not be bagged
	 * @param barcode - barcode of item to skip bagging 
	 * @throws InvalidArgumentSimulationException - when skipBagging is called on a product not in the cart */
	public void skipBaggingRequest(Barcode barcode) {
		if (!this.logic.cartLogic.getCart().containsKey(ProductDatabases.BARCODED_PRODUCT_DATABASE.get(barcode))) throw new InvalidArgumentSimulationException("Cannot skip bagging an item that has not been added to cart");
		logic.attendantLogic.requestApprovalSkipBagging(barcode);
		
	}
	
	/** Checks if there is a weight discrepancy 
	 * @return True if there is a discrepancy; False otherwise
	 * @throws SimulationException If session not started
	 * @throws SimulationException If the scale is not operational */
	public boolean checkWeightDiscrepancy() {
		// Handles exceptions 
		if (!this.logic.isSessionStarted()) throw new InvalidStateSimulationException("Session not started");
		 else if (!this.scaleOperational) throw new InvalidStateSimulationException("Scale not operational");
		
		// Checks for discrepancy and calls notifier if needed 
		if (actualWeight.difference(expectedWeight).abs().compareTo(this.sensitivity) <= 0 ) {
			return false;
		}
	
		if (actualWeight.compareTo(expectedWeight) > 0) this.logic.weightDiscrepancyController.notifyOverload();
		else this.logic.weightDiscrepancyController.notifyUnderload();
		return true;
	}
	
	/** If there is a weight discrepancy, enters blocking state; otherwise, goes back to normal */
	public void handleWeightDiscrepancy() {
		if (this.logic.weightLogic.checkWeightDiscrepancy()) {
			if (!this.logic.stateLogic.inState(States.BLOCKED)) {
				this.logic.stateLogic.gotoState(States.BLOCKED);
			}
		} else {
			this.logic.stateLogic.gotoState(States.NORMAL);
		}
	}
	
	/** Sets expected weight to actual weight */
	public void overrideDiscrepancy() {
		//TODO: Require attendant authentication/verification
		
		
		this.expectedWeight = this.actualWeight;
	}
}
