package com.thelocalmarketplace.software.logic;

import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.software.AbstractLogicDependant;
import com.thelocalmarketplace.software.logic.StateLogic.States;

import ca.ucalgary.seng300.simulation.InvalidStateSimulationException;

/**
 * Logic class for the remove item use case functionality. Adapted from AddBarcodedItemTests
 * @author Daniel Yakimenka (10185055)
 * -----------------------------------
 * @author Connell Reffo (10186960)
 * @author Tara Strickland (10105877)
 * @author Angelina Rochon (30087177)
 * @author Julian Fan (30235289)
 * @author Braden Beler (30084941)
 * @author Samyog Dahal (30194624)
 * @author Maheen Nizmani (30172615)
 * @author Phuong Le (30175125)
 * @author Merick Parkinson (30196225)
 * @author Farida Elogueil (30171114)
 */
public class RemoveItemLogic extends AbstractLogicDependant{
	
	/**
	 * Base constructor
	 * @param logic Reference to central station logic
	 * @throws NullPointerException If logic is null
	 */
	public RemoveItemLogic(CentralStationLogic logic) throws NullPointerException {
		super(logic);
	}
	
	/**
	 * Removes a barcoded item from the cart and updates states and expected weight.
	 * @param product - barcoded product to be removed
	 * @throws NullPointerException
	 *  Removal of other types of items added in next iteration
	 */
	public void removeBarcodedItem(BarcodedProduct product) throws NullPointerException{
    	if (product == null) {
            throw new NullPointerException("Barcode is null");
        }
    	
    	else if (!this.logic.isSessionStarted()) {
    		throw new InvalidStateSimulationException("The session has not been started");
    	}
    	
    	
    	// When method is used to resolve weight discrepancies
    	else if (this.logic.stateLogic.inState(States.BLOCKED)) {
	    	this.logic.cartLogic.removeProductFromCart(product);
	    	this.logic.weightLogic.removeExpectedWeight(product.getBarcode());
	    	System.out.println("Item removed from cart.");
	    	logic.weightLogic.handleWeightDiscrepancy();
    	}
    	
    	// When method is used to remove unwanted items (without triggering a weight discrepancy
    	else {
    		this.logic.cartLogic.removeProductFromCart(product);
	    	this.logic.weightLogic.removeExpectedWeight(product.getBarcode());
	    	this.logic.stateLogic.gotoState(States.BLOCKED);
	    	System.out.println("Item removed from cart. Please remove the item from the bagging area");
    	}	
	}
}
