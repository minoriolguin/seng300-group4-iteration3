//Elizabeth Szentmiklossy UCID: 30165216
//Justine Mangaliman UCID: 30164741
//Enzo Mutiso UCID: 30182555
//Abdelrahman Mohamed UCID: 30162037
//Mohammad Mustafa Mehtab UCID: 30189394
package com.thelocalmarketplace.software;
import com.jjjwelectronics.AbstractDevice;
import com.jjjwelectronics.IDevice;
import com.jjjwelectronics.IDeviceListener;
import com.jjjwelectronics.Mass;
import com.jjjwelectronics.OverloadedDevice;
import com.jjjwelectronics.Mass.MassDifference;
import com.jjjwelectronics.scale.AbstractElectronicScale;
import com.jjjwelectronics.scale.ElectronicScaleListener;
import com.jjjwelectronics.scale.IElectronicScale;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.Product;

import powerutility.NoPowerException;
/**
 * This class represents a Weight Discrepancy checker that listens to changes in weight measurements on an
 * electronic scale and compares it to the actual weight of the item. This class implements methods from ElectronicScaleListner
 * to communicate with the hardware.
 *
 *@author Written by Elizabeth Szentmiklossy (UCID: 30165216)
 */
public class WeightDiscrepancy extends AbstractDevice<WeightDiscrepancyListner> implements ElectronicScaleListener, AddItemListner {
	// Fields to store the expected and actual weights.
	Mass expectedWeight;
	Mass actualWeight;
	Mass Sensetivity;
	/**
	 * Constructor for WeightDiscrepancy class
	 * 
	 * @param eWeight  The expected weight to compare with.
     * @param listener The electronic scale listener to monitor for weight changes.
     */    
	public WeightDiscrepancy(Mass eWeight, AbstractElectronicScale listner ){
		 // Initialize the expected weight with the provided value
		expectedWeight = eWeight;
		Sensetivity = listner.getSensitivityLimit();
		try {
			// Attempt to get the current mass on the scale from the provided listener.
			actualWeight = listner.getCurrentMassOnTheScale();
		} catch (NoPowerException e) {
			 // Handle the case where there is a NoPowerException.
			actualWeight = Mass.ZERO;
		}
		catch (OverloadedDevice e) {
			 // Handle the case where there is a OverloadedDevice exception.
			actualWeight = Mass.ZERO;	
		}
		// Register this class as a listener for the provided listener.
		listner.register(this);
		
		
	}
	@Override
	public void ItemHasBeenAdded(Product product) {
	    Mass weightOfProduct = new Mass(((BarcodedProduct) product).getExpectedWeight());
		expectedWeight = expectedWeight.sum(weightOfProduct); 
		CompareWeight();
		WeightDescrepancyEvent();
		
	}
	/**
     * Method to compare the expected and actual weights.
     *
     * @return True if the expected and actual weights are equal, false otherwise.
     */
	public boolean CompareWeight() {
		MassDifference difference = actualWeight.difference(expectedWeight);		
		if(difference.compareTo(Sensetivity) <= 0) {
		return true;
	}
		return false;
	}
	
	/**
     * Handles changes in the mass on the scale.
     *
     * @param scale The electronic scale that reports the mass change.
     * @param mass  The new mass on the scale.
     */
	
	@Override
	public void theMassOnTheScaleHasChanged(IElectronicScale scale, Mass mass) {
		Sensetivity = scale.getSensitivityLimit();
		actualWeight = mass;
		WeightDescrepancyEvent();
		
		
	}
	
	public void WeightDescrepancyEvent() {
		
		for(WeightDiscrepancyListner l : listeners()) {
			if (CompareWeight()==false){
			l.WeightDiscrancyOccurs();
			
		}
		}
		for(WeightDiscrepancyListner l : listeners()) {
			if (CompareWeight()==true){
			l.WeightDiscrancyResolved();
			}
		}
		// Update the actual weight when the mass on the scale changes.
		
		
	}
	
	 // Other overridden methods from ElectronicScaleListener interface (unimplemented).

    // Other overridden methods from IDeviceListener interface (unimplemented).
	@Override
	public void theMassOnTheScaleHasExceededItsLimit(IElectronicScale scale) {	
	}
	@Override
	public void theMassOnTheScaleNoLongerExceedsItsLimit(IElectronicScale scale) {	
	}
	@Override
	public void aDeviceHasBeenEnabled(IDevice<? extends IDeviceListener> device) {
	}
	@Override
	public void aDeviceHasBeenDisabled(IDevice<? extends IDeviceListener> device) {
	}
	@Override
	public void aDeviceHasBeenTurnedOn(IDevice<? extends IDeviceListener> device) {
	}
	@Override
	public void aDeviceHasBeenTurnedOff(IDevice<? extends IDeviceListener> device) {
	}


}
