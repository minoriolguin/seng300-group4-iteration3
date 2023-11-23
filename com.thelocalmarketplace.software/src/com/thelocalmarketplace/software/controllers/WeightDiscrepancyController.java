package com.thelocalmarketplace.software.controllers;

import com.jjjwelectronics.IDevice;
import com.jjjwelectronics.IDeviceListener;
import com.jjjwelectronics.Mass;
import com.jjjwelectronics.scale.ElectronicScaleListener;
import com.jjjwelectronics.scale.IElectronicScale;
import com.thelocalmarketplace.software.AbstractLogicDependant;
import com.thelocalmarketplace.software.logic.CentralStationLogic;
import com.thelocalmarketplace.software.logic.StateLogic.States;

/**
 * @author Tara Strickland (10105877)
 * @author Angelina Rochon (30087177)
 * ----------------------------------
 * @author Connell Reffo (10186960)
 * @author Julian Fan (30235289)
 * @author Braden Beler (30084941)
 * @author Samyog Dahal (30194624)
 * @author Maheen Nizmani (30172615)
 * @author Phuong Le (30175125)
 * @author Daniel Yakimenka (10185055)
 * @author Farida Elogueil (30171114)
 */
public class WeightDiscrepancyController extends AbstractLogicDependant implements ElectronicScaleListener {
	
	/**
	 * WeightDiscrepancyController Constructor
	 * @param logic A reference to the logic instance
	 * @throws NullPointerException If logic is null
	 **/
	public WeightDiscrepancyController(CentralStationLogic logic) throws NullPointerException {
		super(logic);
		
		// Register self to bagging area scale
		this.logic.hardware.baggingArea.register(this);
	}
	
	/** Triggered when mass on bagging area scale is changed
	 * If a weight discrepancy is detected, system is updated accordingly */
	@Override
	public void theMassOnTheScaleHasChanged(IElectronicScale scale, Mass mass) {
		
		// Weight discrepancies are ignored when in ADDBAGS state
		if (!this.logic.stateLogic.inState(States.ADDBAGS)) {
			this.logic.weightLogic.updateActualWeight(mass);
			this.logic.weightLogic.handleWeightDiscrepancy();	
		} else {
			
			// The actual mass now is whatever was on the scale before this change
			Mass one_bag = mass.difference(this.logic.weightLogic.getActualWeight()).abs();
			
			if (mass.compareTo(this.logic.weightLogic.getActualWeight()) > 0) {
				
				// Add the bag to the bag mass
				this.logic.weightLogic.updateTotalBagMass(this.logic.weightLogic.getTotalBagMass().sum(one_bag));

			} else {
				
				// Remove the bag from the bag mass
				this.logic.weightLogic.updateTotalBagMass(this.logic.weightLogic.getTotalBagMass().difference(one_bag).abs());
			}
			
			// Update actual weight of the scale
			this.logic.weightLogic.updateActualWeight(mass);
		}
	}
	
	/** Triggered when actual weight is over expected weight */
	public void notifyOverload() {
		System.out.println("Weight discrepancy detected. Please remove item(s)");
	}
	
	/** Triggered when actual weight is under expected weight */
	public void notifyUnderload() {
		System.out.println("Weight discrepancy detected. Please add item(s)");
	}
	
	@Override
	public void theMassOnTheScaleHasExceededItsLimit(IElectronicScale scale) {
		this.logic.weightLogic.scaleOperational = false;
	}

	@Override
	public void theMassOnTheScaleNoLongerExceedsItsLimit(IElectronicScale scale) {
		this.logic.weightLogic.scaleOperational = true;
	}
	
	// ---- Unused -----

	@Override
	public void aDeviceHasBeenEnabled(IDevice<? extends IDeviceListener> device) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void aDeviceHasBeenDisabled(IDevice<? extends IDeviceListener> device) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void aDeviceHasBeenTurnedOn(IDevice<? extends IDeviceListener> device) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void aDeviceHasBeenTurnedOff(IDevice<? extends IDeviceListener> device) {
		// TODO Auto-generated method stub
		
	}
}	