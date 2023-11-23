package com.thelocalmarketplace.software;

import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.hardware.external.*;

import ca.ucalgary.seng300.simulation.SimulationException;

import com.jjjwelectronics.IDevice;
import com.jjjwelectronics.IDeviceListener;
import com.jjjwelectronics.Item;
import com.jjjwelectronics.Mass;
import com.jjjwelectronics.scale.ElectronicScaleSilver;
import com.jjjwelectronics.scale.ElectronicScaleGold;
import com.jjjwelectronics.scale.ElectronicScaleBronze;
import com.jjjwelectronics.scale.AbstractElectronicScale;
import com.jjjwelectronics.scale.IElectronicScale;
import com.jjjwelectronics.scale.ElectronicScaleListener; 

public class HandleBulkyItem implements IDeviceListener {
    private AbstractSelfCheckoutStation station; // the self checkout station
    private WeightDiscrepancy weightDiscrepancy; // handling weight discrepancy
    private Item bulkyItem; // the bulky item that is not being bagged

    public HandleBulkyItem(AbstractSelfCheckoutStation station, Item bulkyItem) {
        this.station = station;
        this.weightDiscrepancy = weightDiscrepancy;
        this.bulkyItem = bulkyItem;
    }


    // This method is called when the customer requests to not bag an item
    // If the customer does not bag the item, disable inputs from being received
    // If the customer does bag the item, handle this as a weight discrepancy
    public boolean requestNoBagging(boolean bagItem) {
        // if the item is indicated that it will not be bagged
        // then the main scanner and handheld scanner will be disabled from further input
        if (!bagItem) {
            // Logic to disable relevant devices like scanners from receiving input
            ((IDevice) station.mainScanner).disable();
            ((IDevice) station.handheldScanner).disable();
            notifyAttendant();
            return true; // Indicate that the request was processed
        } else {
            // customer adds item to bagging area anyway
            // handle this as a weight discrepancy
            weightDiscrepancy.WeightDescrepancyEvent();
            return false; // indicate item was attempted to be bagged
        }
    }

    public boolean notifyAttendant() {
        if (requestNoBagging(true)) {
            // Notify the attendant
            // Assuming the attendant approves the request
            boolean attendantApproval = true;

            if (attendantApproval) {
                // Code to unblock the station to allow further input
                ((IDevice) station.mainScanner).enable();
                ((IDevice) station.handheldScanner).enable();
                return true;
            } else{
                // open issue 2: what happens if the attendant does not want to
                // approve of the request?
                return false;
            }
        }
        return false;
    }

    public void fixDiscrepancy() {
        // Get the electronic scale from the self-checkout station
        IElectronicScale baggingAreaScale = station.baggingArea;

        try {
            // Remove the bulky item from the scale
            baggingAreaScale.removeAnItem(bulkyItem);

            // Assuming the scale automatically updates the expected weight and notifies listeners
            // No additional code is required to manually update the weight
        } catch (SimulationException e) {
            // Handle the exception, e.g., the item is not on the scale
        }
    }
    

    @Override
    public void aDeviceHasBeenEnabled(IDevice<? extends IDeviceListener> device) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'aDeviceHasBeenEnabled'");
    }

    // Define the getBaggingAreaScale() method in the AbstractSelfCheckoutStation class
    public IElectronicScale getBaggingAreaScale() {
        // Implementation goes here
    }

    @Override
    // Define the getWeightOfBulkyItem() method in the HandleBulkyItem class
    public Mass getWeightOfBulkyItem() {
        // Implementation goes here
    }

    @Override
    // Define the getCurrentMassOnTheScale() method in the IElectronicScale interface
    public Mass getCurrentMassOnTheScale() {
        // Implementation goes here
    }

    // Define the setExpectedWeight(Mass) method in the IElectronicScale interface
    public void setExpectedWeight(Mass expectedWeight) {
        // Implementation goes here
    }

    @Override
    public void aDeviceHasBeenDisabled(IDevice<? extends IDeviceListener> device) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'aDeviceHasBeenDisabled'");
    }

    @Override
    public void aDeviceHasBeenTurnedOn(IDevice<? extends IDeviceListener> device) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'aDeviceHasBeenTurnedOn'");
    }

    @Override
    public void aDeviceHasBeenTurnedOff(IDevice<? extends IDeviceListener> device) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'aDeviceHasBeenTurnedOff'");
    }

}
