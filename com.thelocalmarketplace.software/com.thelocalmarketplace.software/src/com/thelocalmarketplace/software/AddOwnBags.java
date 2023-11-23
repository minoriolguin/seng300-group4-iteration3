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

import java.math.BigInteger;
import java.util.List;

import com.jjjwelectronics.AbstractDevice;
import com.jjjwelectronics.IDevice;
import com.jjjwelectronics.IDeviceListener;
import com.jjjwelectronics.Mass;
import com.jjjwelectronics.OverloadedDevice;
import com.jjjwelectronics.Mass.MassDifference;
import com.jjjwelectronics.scale.AbstractElectronicScale;
import com.jjjwelectronics.scale.ElectronicScaleListener;
import com.jjjwelectronics.scale.IElectronicScale;

import powerutility.NoPowerException;

/**
 * 
 */
public class AddOwnBags extends AbstractDevice<WeightDiscrepancyListner> implements ElectronicScaleListener{
	Mass bagWeight;
	Mass expectedWeight;
	Mass actualWeight;
	Mass Sensetivity;
	boolean addOwnBagsSelection;
	Mass bagLimit = new Mass(BigInteger.valueOf(500 * Mass.MICROGRAMS_PER_GRAM)); // limit for the bag is 500g
	public AddOwnBags(Mass expectedWeight, Mass bagWeight, boolean addOwnBagsSelection, AbstractElectronicScale listner) {
		this.addOwnBagsSelection = addOwnBagsSelection;
		Sensetivity = listner.getSensitivityLimit();
		this.expectedWeight = expectedWeight;
		try {
			// Attempt to get the current mass on the scale from the provided listener.
			 this.actualWeight = listner.getCurrentMassOnTheScale();
		} catch (NoPowerException e) {
			 // Handle the case where there is a NoPowerException.
			this.actualWeight = Mass.ZERO;
		}
		catch (OverloadedDevice e) {
			 // Handle the case where there is a OverloadedDevice exception.
			this.actualWeight = Mass.ZERO;	
		}
		this.actualWeight = this.expectedWeight.sum(bagWeight);
		if(addOwnBagsSelection) {
			// if the bag weight Overloaded return true;
			// Initializing the bag weight, if the add own bags is selected.
			this.bagWeight = bagWeight;
			}else {
				this.bagWeight = Mass.ZERO;
			}
		// register the class into listener 
		listner.register(this);

		
	
	}
	// See if the bag weight is out of the allowable range we set
	public boolean bagWeightOverloaded() {
		if (bagWeight.compareTo(bagLimit)>0) {
			boolean addOwnBagsSelection = false;
			for(WeightDiscrepancyListner l : listeners()){
				// Make the weight Discrepancy occurs since the weight of the bag is overloaded
				l.WeightDiscrancyOccurs();
				return true;
			}
		}
		return false;
	}
	
	// Notify the listener the choice of the user
	public void notifyListner() {
		for(WeightDiscrepancyListner l : listeners()) {
			if (addOwnBagsSelection==false){
			l.addOwnBagDeselected();
		}else{
			l.addOwnBagsSelected();
			l.WeightDiscrancyResolved();
			}
		}
	}
	// add the weight of the bag if the user chooses the add bag selection.
	public Mass addBagWeight() {
		if(addOwnBagsSelection) {
		Mass expectedWeight = actualWeight.sum(bagWeight);
		return expectedWeight;
		}else {
		return null;
		}
	}
		// we use this method to see if the bag weight is added into the expected weight.
	public boolean CompareWeight() {
		MassDifference difference = actualWeight.difference(expectedWeight);		
		if(difference.compareTo(Sensetivity) <= 0) {
		return true;
	}
		return false;
	}	
		
	
	
	
	
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


	@Override
	public void theMassOnTheScaleHasExceededItsLimit(IElectronicScale scale) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void theMassOnTheScaleNoLongerExceedsItsLimit(IElectronicScale scale) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void theMassOnTheScaleHasChanged(IElectronicScale scale, Mass mass) {
		// TODO Auto-generated method stub
		
	}
	
}
