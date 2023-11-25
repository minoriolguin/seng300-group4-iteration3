
import com.jjjwelectronics.*;
import com.jjjwelectronics.scale.ElectronicScaleListener;
import com.jjjwelectronics.scale.IElectronicScale;
import java.util.ArrayList;

/**
 * The WeightDiscrepancy class listens to the bagging area scale of the checkout and notifies listeners of discrepancies.
 * It manages the type of discrepancy, such as weight over the limit, items added or removed from the scale,
 * and bags being too heavy.
 * <p>
 * Speakers, touchscreen, and attendant should be listeners.
 */
public class WeightDiscrepancy implements ElectronicScaleListener {
    private final ArrayList<WeightDiscrepancyListener>listeners;
    private final Software software;

    /**
     * Flag indicating whether the system expects the customer to add their own bags.
     */
    public boolean expectOwnBagsToBeAdded;
    /**
     * The total mass of the customer's own bags added to the bagging area.
     */
    public Mass massOfOwnBags;
    /**
     * The overridden weight to be used for comparison in case of discrepancies.
     */
    public Mass overRideWeight;

    /**
     * Constructs a WeightDiscrepancy instance to listen to the Bagging Area Scale.
     *
     * @param software The Software instance associated with the WeightDiscrepancy.
     */
    public WeightDiscrepancy(Software software){
        this.software = software;
        software.baggingAreaScale.register(this);
        listeners = new ArrayList<>();
        expectOwnBagsToBeAdded = false;
        massOfOwnBags = Mass.ZERO;
    }
    
    /**
     * Register a listener for weight discrepancies.
     *
     * @param listener The WeightDiscrepancyListener to be registered.
     */
    public void register(WeightDiscrepancyListener listener){
        listeners.add(listener);
    }
    
    /**
     * Get the list of listeners registered for weight discrepancies.
     *
     * @return The list of WeightDiscrepancyListeners.
     */
    public ArrayList<WeightDiscrepancyListener> getListeners() {
        return listeners;
    }


    /**
     * Check for weight discrepancies and notify listeners accordingly.
     *
     * @param currentWeight The current weight on the scale.
     */
    public void isWeightDiscrepancy(Mass currentWeight) {
        overRideWeight = currentWeight;
        if(expectOwnBagsToBeAdded){
            long bagWeight = currentWeight.inMicrograms().longValue() - software.getExpectedTotalWeight().inMicrograms().longValue();
            Mass bagMass = new Mass(bagWeight);
            massOfOwnBags = massOfOwnBags.sum(bagMass);
            if(massOfOwnBags.compareTo(software.allowableBagWeight) > 0)
                notifyBagsTooHeavy();
            if(massOfOwnBags.compareTo(software.allowableBagWeight) < 0)
                software.setExpectedTotalWeight(software.getExpectedTotalWeight().sum(bagMass));
        }
        //find out if it is greater than or equal to the expected
        // compareTo returns an int value of 1,0 or -1
        if(software.getExpectedTotalWeight().compareTo(currentWeight) > 0) {
            software.blockCustomer();
            notifyRemoveItemFromScale();
        } else if (software.getExpectedTotalWeight().compareTo(currentWeight) < 0) {
            software.blockCustomer();
            notifyAddItemToScale();
        }
        else
            //Project Iteration 3: Potentially update attendant as there may be situations where you don't want to unblock
            if(!expectOwnBagsToBeAdded)
                software.unblockCustomer();
            notifyNoDiscrepancy();
    }

    /**
     * Notify listeners that the weight on the scale has exceeded its limit.
     */
    protected void notifyWeightOverLimit(){
        for(WeightDiscrepancyListener l : listeners)
            l.weightOverLimit();
    }
    /**
     * Notify listeners that an item has been removed from the scale.
     */
    protected void notifyRemoveItemFromScale(){
        for(WeightDiscrepancyListener l : listeners)
            l.RemoveItemFromScale();
    }
    /**
     * Notify listeners that an item has been added to the scale.
     */
    protected void notifyAddItemToScale(){
        for(WeightDiscrepancyListener l : listeners)
            l.AddItemToScale();
    }
    /**
     * Notify listeners that there is no discrepancy in weight.
     */
    protected void notifyNoDiscrepancy() {
        for (WeightDiscrepancyListener l : listeners)
            l.noDiscrepancy();
    }
    /**
     * Notify listeners that the bags are too heavy.
     */
    protected void notifyBagsTooHeavy() {
        for (WeightDiscrepancyListener l : listeners)
            l.bagsTooHeavy();
    }

    /**
     * @param scale The scale where the event occurred.
     * @param mass  The new mass.
     *
     *Implemented interface methods for listening
     */
    @Override
    public void theMassOnTheScaleHasChanged(IElectronicScale scale, Mass mass) {
        isWeightDiscrepancy(mass);
    }
    /**
     * Event handler for when the mass on the scale has exceeded its limit.
     *
     * @param scale The scale where the event occurred.
     */
    @Override
    public void theMassOnTheScaleHasExceededItsLimit(IElectronicScale scale) {
        notifyWeightOverLimit();
    }
    @Override
    public void theMassOnTheScaleNoLongerExceedsItsLimit(IElectronicScale scale) {}
    @Override
    public void aDeviceHasBeenEnabled(IDevice<? extends IDeviceListener> device) {}
    @Override
    public void aDeviceHasBeenDisabled(IDevice<? extends IDeviceListener> device) {}
    @Override
    public void aDeviceHasBeenTurnedOn(IDevice<? extends IDeviceListener> device) {}
    @Override
    public void aDeviceHasBeenTurnedOff(IDevice<? extends IDeviceListener> device) {}

}
