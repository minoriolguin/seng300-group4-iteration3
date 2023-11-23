/**
* Jon Mulyk (UCID: 30093143)
* Elizabeth Szentmiklossy (UCID: 30165216)
* Ahmed Ibrahim Mohamed Seifledin Hadsan (UCID: 30174024)
* Arthur Huan (UCID: 30197354)
* Jaden Myers (UCID: 30152504)
* Jane Magai (UCID: 30180119)
* Ahmed Elshabasi (UCID: 30188386)
* Jincheng Li (UCID: 30172907)
* Sina Salahshour (UCID: 30177165)
* Anthony Tolentino (UCID: 30081427) */

package com.thelocalmarketplace.software;

import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.hardware.Product;


import com.jjjwelectronics.AbstractDevice;
import com.jjjwelectronics.IDevice;
import com.jjjwelectronics.IDeviceListener;
import com.jjjwelectronics.Item;
import com.jjjwelectronics.Mass;
import com.jjjwelectronics.scale.AbstractElectronicScale;
import com.jjjwelectronics.scale.IElectronicScale;
import com.jjjwelectronics.scale.ElectronicScaleListener; 

public class HandleBulkyItem extends AbstractDevice<WeightDiscrepancyListner> implements IDeviceListener, ElectronicScaleListener {
    private AbstractSelfCheckoutStation station; // The self-checkout station
    WeightDiscrepancy weightDiscrepancy; // Handling weight discrepancy
    Item bulkyItem; // The bulky item that is not being bagged
    private boolean attendantApproval; // Whether the attendant approves or not
    private Product product; // The product being processed
    private IDeviceListener listener; // The listener for the device
    private ActionBlocker blocker; // The action blocker to freeze session
    private boolean bagItem; // Whether the customer tries to bag the item or not
    Mass expectedWeight; // The expected weight of the item
    private AbstractElectronicScale scale; // The scale
    private WeightDiscrepancyListner weightDiscrepancyListener; // The listener for the weight discrepancy

    /**
     * Constructor for HandleBulkyItem
     * @param station The self-checkout station
     * @param listener The listener for the device
     * @param blocker The action blocker to freeze session
     * @param weightDiscrepancy The weight discrepancy handler
     * @param scale The electronic scale
     * @param weightDiscrepancyListener The listener for weight discrepancy events
     */
    public HandleBulkyItem(AbstractSelfCheckoutStation station, IDeviceListener listener, ActionBlocker blocker, WeightDiscrepancy weightDiscrepancy, AbstractElectronicScale scale, WeightDiscrepancyListner weightDiscrepancyListener) {
        this.station = station;
        this.listener = listener;
        this.attendantApproval = false;
        this.bagItem = false;
        this.blocker = blocker;
        this.weightDiscrepancy = weightDiscrepancy;
        this.weightDiscrepancyListener = weightDiscrepancyListener;
        this.scale = scale; // Initialize the scale
    }

    /**
     * Handles the request to not bag an item.
     * If the customer does not bag the item, inputs are disabled.
     * If the customer does bag the item, it's treated as a weight discrepancy.
     * @param item The item being processed
     */
    public void requestNoBagging(Item item) {
        if (!bagItem) {
            blocker.blockInteraction();
            notifyAttendant(false);
        } else {
            setBagItem(true);
            this.bulkyItem = item;
            this.weightDiscrepancy = new WeightDiscrepancy(expectedWeight, scale); // Use the initialized scale
            // Becomes a weight discrepancy issue
            weightDiscrepancy.theMassOnTheScaleHasChanged(scale, expectedWeight);;
        }
    }

    /**
     * Notifies the attendant for approval or denial of the no-bagging request.
     * @param approval The approval status from the attendant
     * @return true if the attendant approves, false otherwise
     */
    public boolean notifyAttendant(boolean approval) {
        setAttendantApproval(true);
        if (approval) {
            blocker.unblockInteraction();
            return true;
        } else {
            // Open issue 2: what happens if the attendant denies the request?
            return false;
        }
    }

    /**
     * Fixes the weight discrepancy by adjusting the expected weight.
     * @param itemToRemove The item to remove from the expected weight calculation
     */
    public void fixDiscrepancy(Item itemToRemove) {
        weightDiscrepancy.expectedWeight = new Mass(weightDiscrepancy.expectedWeight.inGrams().subtract(itemToRemove.getMass().inGrams()));
        Mass newExpectedWeight = expectedWeight;
        weightDiscrepancy.expectedWeight = newExpectedWeight;
        
        // Notify the listener that the weight discrepancy has been resolved
        weightDiscrepancy.WeightDescrepancyEvent();
    }

    // Setter methods
    public void setAttendantApproval(boolean approval) {
        this.attendantApproval = approval;
    }
    
    public void setBagItem(boolean bagged) {
        this.bagItem = bagged;
    }

    // IDeviceListener interface methods
    @Override
    public void aDeviceHasBeenEnabled(IDevice<? extends IDeviceListener> device) {
        throw new UnsupportedOperationException("Unimplemented method 'aDeviceHasBeenEnabled'");
    }
    
    @Override
    public void aDeviceHasBeenDisabled(IDevice<? extends IDeviceListener> device) {
        throw new UnsupportedOperationException("Unimplemented method 'aDeviceHasBeenDisabled'");
    }

    @Override
    public void aDeviceHasBeenTurnedOn(IDevice<? extends IDeviceListener> device) {
        throw new UnsupportedOperationException("Unimplemented method 'aDeviceHasBeenTurnedOn'");
    }

    @Override
    public void aDeviceHasBeenTurnedOff(IDevice<? extends IDeviceListener> device) {
        throw new UnsupportedOperationException("Unimplemented method 'aDeviceHasBeenTurnedOff'");
    }

    // ElectronicScaleListener interface methods
    @Override
    public void theMassOnTheScaleHasChanged(IElectronicScale scale, Mass mass) {
        throw new UnsupportedOperationException("Unimplemented method 'theMassOnTheScaleHasChanged'");
    }

    @Override
    public void theMassOnTheScaleHasExceededItsLimit(IElectronicScale scale) {
        throw new UnsupportedOperationException("Unimplemented method 'theMassOnTheScaleHasExceededItsLimit'");
    }

    @Override
    public void theMassOnTheScaleNoLongerExceedsItsLimit(IElectronicScale scale) {
        throw new UnsupportedOperationException("Unimplemented method 'theMassOnTheScaleNoLongerExceedsItsLimit'");
    }
}
