package com.thelocalmarketplace.software.controllers.item;

import com.jjjwelectronics.IDevice;
import com.jjjwelectronics.IDeviceListener;
import com.jjjwelectronics.scanner.Barcode;
import com.jjjwelectronics.scanner.BarcodeScannerListener;
import com.jjjwelectronics.scanner.IBarcodeScanner;
import com.thelocalmarketplace.software.AbstractLogicDependant;
import com.thelocalmarketplace.software.logic.CentralStationLogic;
import com.thelocalmarketplace.software.logic.StateLogic.States;

import ca.ucalgary.seng300.simulation.InvalidStateSimulationException;
import ca.ucalgary.seng300.simulation.SimulationException;

/**
 * Represents the software controller for adding a barcoded items
 * @author Connell Reffo (10186960)
 * @author Tara Strickland (10105877)
 * @author Angelina Rochon (30087177)
 * @author Julian Fan (30235289)
 * @author Braden Beler (30084941)
 * @author Samyog Dahal (30194624)
 * @author Maheen Nizmani (30172615)
 * @author Phuong Le (30175125)
 * @author Daniel Yakimenka (10185055)
 * @author Farida Elogueil (30171114)
 */
public class AddBarcodedItemController extends AbstractLogicDependant implements BarcodeScannerListener {    
    
    /**
     * AddBarcodedProductController Constructor
     * @param logic A reference to the logic instance
     * @throws NullPointerException If logic is null
     */
    public AddBarcodedItemController(CentralStationLogic logic) throws NullPointerException {
    	super(logic);
        
        // Register self to main and hand held barcode scanners
        this.logic.hardware.mainScanner.register(this);
        this.logic.hardware.handheldScanner.register(this);
    }
    
    /**
     * Adds a new barcode
     * If a weight discrepancy is detected, then station is blocked
     * @param barcodedItem The item to be scanned and added
     * @throws SimulationException If session not started
     * @throws SimulationException If station is blocked
     * @throws SimulationException If barcode is not registered in database
     * @throws NullPointerException If barcode is null
     */
    public void addBarcode(Barcode barcode) throws SimulationException, NullPointerException {
    	if (barcode == null) {
            throw new NullPointerException("Barcode is null");
        }
    	else if (!this.logic.isSessionStarted()) {
    		throw new InvalidStateSimulationException("The session has not been started");
    	}
    	else if (this.logic.stateLogic.inState(States.BLOCKED)) {
    		throw new InvalidStateSimulationException("Station is blocked");
    	}
    	
    	this.logic.cartLogic.addBarcodedProductToCart(barcode);
    	this.logic.weightLogic.addExpectedWeight(barcode);
    	
		this.logic.stateLogic.gotoState(States.BLOCKED);
		System.out.println("Item added to cart. Please place scanned item in bagging area");
    }
    
    @Override
	public void aBarcodeHasBeenScanned(IBarcodeScanner barcodeScanner, Barcode barcode) throws SimulationException, NullPointerException {
    	System.out.println("A barcoded item has been scanned");
    	
    	this.addBarcode(barcode);
    	
    	System.out.println("Place item in bagging area");
	}
    
    // ---- Unused ----

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