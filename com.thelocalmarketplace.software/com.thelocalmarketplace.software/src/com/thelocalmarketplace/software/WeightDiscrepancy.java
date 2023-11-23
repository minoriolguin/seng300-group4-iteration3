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
import java.math.BigDecimal;

import com.jjjwelectronics.AbstractDevice;
import com.jjjwelectronics.IDevice;
import com.jjjwelectronics.IDeviceListener;
import com.jjjwelectronics.Item;
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
 * The WeightDiscrepancy class represents a weight discrepancy checker that listens to changes in weight measurements on an
 * electronic scale and compares it to the actual weight of the item. This class implements methods from ElectronicScaleListener
 * to communicate with the hardware.
 *
 * The class also implements AddItemListener to handle events when a product is added.
 *
 * @author Elizabeth Szentmiklossy (UCID: 30165216)
 *
 * @see ElectronicScaleListener
 * @see AddItemListener
 */
public class WeightDiscrepancy extends AbstractDevice<WeightDiscrepancyListner> implements ElectronicScaleListener, ItemControllerListener  {
	// Fields to store the expected and actual weights.
	Mass expectedWeight;
	Mass actualWeight;
	Mass Sensetivity;
	/**
	 * Constructor for WeightDiscrepancy class
	 * 
	 * @param zero  The expected weight to compare with.
     * @param listener The electronic scale listener to monitor for weight changes.
     */    
	public WeightDiscrepancy(Mass zero, AbstractElectronicScale listner){
		 // Initialize the expected weight with the provided value
		expectedWeight = zero;
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
	 /**
     * Handles events when a product has been added.
     *
     * @param product The product that has been added.
     */
	@Override
	public void ItemHasBeenAdded(Item item) {
		Mass weightOfProduct = item.getMass();

//	    Mass weightOfProduct = new Mass(((BarcodedProduct) product).getExpectedWeight());
		expectedWeight = expectedWeight.sum(weightOfProduct);
		CompareWeight();
		WeightDescrepancyEvent();
		
	}

	@Override
	public void ItemHasBeenRemoved(Item item, int amount) 
	{
		// no direct way to multiply Mass, so create 
		// turn it into BigDecimal, multiply it and change back into mass
		Mass weightOfItem = new Mass(item.getMass().inGrams().multiply(new BigDecimal(amount)));

	    //Doesen't seem like there's a way to convert MassDifference to Mass
		expectedWeight = new Mass(expectedWeight.inGrams().subtract(weightOfItem.inGrams()));
//	    expectedWeight = new Mass (weightOfItem.inGrams().subtract(expectedWeight.inGrams()));
	    
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
	  /**
     * Triggers weight discrepancy events for registered listeners.
     */
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
