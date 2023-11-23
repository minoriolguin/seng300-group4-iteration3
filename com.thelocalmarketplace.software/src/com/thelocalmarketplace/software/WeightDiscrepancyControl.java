/**
 * @author Alan Yong: 30105707
 * @author Atique Muhammad: 30038650
 * @author Ayman Momin: 30192494
 * @author Christopher Lo: 30113400
 * @author Ellen Bowie: 30191922
 * @author Emil Huseynov: 30171501
 * @author Eric George: 30173268
 * @author Kian Sieppert: 30134666
 * @author Muzammil Saleem: 30180889
 * @author Ryan Korsrud: 30173204
 * @author Sukhnaaz Sidhu: 30161587
 */
package com.thelocalmarketplace.software;

import com.jjjwelectronics.IDevice;
import com.jjjwelectronics.IDeviceListener;
import com.jjjwelectronics.Mass;
import com.jjjwelectronics.scale.ElectronicScaleListener;
import com.jjjwelectronics.scale.IElectronicScale;

/**
 * Implements the ElectronicScaleListener to allow the control software to react to various events
 * involving the bagging area. 
 * Checks if the change in mass on the scale should result in a weight discrepancy, or resolves a weight
 * discrepancy, then blocks or unblocks the station as necessary and notifies the concerned parties.
 */
public class WeightDiscrepancyControl implements ElectronicScaleListener {
	/**
	 * CustomerStationControl object which sends order data and can be blocked if a Weight Discrepancy occurs
	 */
	protected CustomerStationControl customerStationControl; 
	
	/**
	 * Expected Mass on the scale based on items
	 */
	protected Mass expectedMass;
	/**
	 * Actual Mass on scale announced by the scale
	 */
	protected Mass massOnScale;
	/**
	 * Scale sensitivity limit, differences in mass below this limit cannot be detected
	 */
	protected Mass sensLimit;
	/**
	 * Difference between actual and expected mass on scale
	 */
	protected Mass delta;
	
	/**
	 * Simple Constructor for WeightDiscrepancyControl, allows a CustomerStationControl object.
	 * @param CSC CustomerStationControl object to interact with
	 */				
	public WeightDiscrepancyControl(CustomerStationControl CSC){
		customerStationControl = CSC;
	}

	/**
	 * Method called by scale when mass on it changes. Checks if the mass difference (delta) between expected and actual is higher than sensitivity.
	 * If it is, triggers a new WeightDiscrepancyEvent to occur. Does nothing if it is already active.
	 * If it isn't, stops an active WeightDiscrepancyEvent or does nothing if one is not active. 
	 */
	public void theMassOnTheScaleHasChanged(IElectronicScale scale, Mass massOnScale){
		sensLimit = scale.getSensitivityLimit();
		expectedMass = customerStationControl.getOrder().getExpectedMass();
		this.massOnScale = massOnScale;
		delta = massOnScale.difference(expectedMass).abs();
		// Address Scenario: Weight change added to the scale that causes expected mass to differ from actual mass
		if (delta.compareTo(Mass.ZERO) == 1 && !customerStationControl.getAddingOwnBags()) {
	        customerStationControl.block();
	        customerStationControl.notifyCustomer("Weight discrepancy detected. \nPlease adjust items in the bagging area before paying or adding new items \n", customerStationControl.notifyDiscrepancyCode);
	        customerStationControl.notifyAttendant("Weight Discrepancy at Customer Station", customerStationControl.notifyDiscrepancyCode);
		} 
		// Address Scenario: Weight change added to the scale that causes expected mass to differ from actual mass is due to 
		// customer adding their own bag, but the bag weight surpasses allowable bag weight.
		else if (delta.compareTo(Mass.ZERO) == 1 && customerStationControl.getAddingOwnBags()) {
			Mass allowableBagWeight = new Mass(customerStationControl.getAllowableBagWeightInGrams());
			//If weight change added is over allowable bag weight configured.
			if (delta.compareTo(allowableBagWeight) == 1) {
				//This iteration will not block the station in this scenario, 
				//as there is no interface for an attendant to unblock the station.
				//customerStationControl.block();
			    customerStationControl.notifyCustomer("Bag is too heavy. Please wait for attendant.", customerStationControl.notifyBagsTooHeavyCode);
			    customerStationControl.notifyAttendant("Customers bag is too heavy.", customerStationControl.notifyBagsTooHeavyCode);
			} else {
				//Sets the customer bag weight recorded in the order class.
		        customerStationControl.getOrder().setCustomerBagWeight(delta);
		        customerStationControl.setAddingOwnBags(false);
		        customerStationControl.notifyCustomer("You may now continue.", customerStationControl.notifyOtherCode);
			}
		}
		// Address Scenario: Weight change causes the expected mass to match the actual mass, resolving any blocks in controller
		else if (delta.equals(Mass.ZERO)) {
			customerStationControl.unblock();
		}
	}
	
	@Override
	public void theMassOnTheScaleHasExceededItsLimit(IElectronicScale scale) {
		customerStationControl.block();
		customerStationControl.notifyAttendant("The weight in the bagging are is too heavy", customerStationControl.notifyBagsTooHeavyCode);
		//when the problem is corrected, the station will be unblocked through theMassOnTheScaleHasChanged
	}

	public void instantiateMass(int m) {
		this.massOnScale = new Mass(m);
	}
	public Mass getMassOnScale() {
		return massOnScale;
	}
	
	@Override
	public void aDeviceHasBeenEnabled(IDevice<? extends IDeviceListener> device) {}
	@Override
	public void aDeviceHasBeenDisabled(IDevice<? extends IDeviceListener> device) {}
	@Override
	public void aDeviceHasBeenTurnedOn(IDevice<? extends IDeviceListener> device) {}
	@Override
	public void aDeviceHasBeenTurnedOff(IDevice<? extends IDeviceListener> device) {}
	@Override
	public void theMassOnTheScaleNoLongerExceedsItsLimit(IElectronicScale scale) {}

}
