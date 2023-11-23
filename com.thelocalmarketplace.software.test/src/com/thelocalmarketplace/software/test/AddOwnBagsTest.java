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
package com.thelocalmarketplace.software.test;

import static org.junit.Assert.*;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.hardware.SelfCheckoutStationGold;
import com.thelocalmarketplace.software.CustomerStationControl;
import com.thelocalmarketplace.software.Order;

import powerutility.PowerGrid;

import com.jjjwelectronics.Mass;
public class AddOwnBagsTest {
	
	private AbstractSelfCheckoutStation station;
	private CustomerStationControl control;

	@Before
	public void setUp(){
		// initialize station and turn on required components
		AbstractSelfCheckoutStation.resetConfigurationToDefaults();
		station = new SelfCheckoutStationGold();
		station.plugIn(PowerGrid.instance());
		station.baggingArea.turnOn();
		station.mainScanner.turnOn();
		
		// Initialize control
		control = new CustomerStationControl(station);
		
		// Initialize database
		ExampleItems.updateDatabase();	
		
	}

	/**
	 * Checks if the customer has signaled that they wish to use their own bag. 
	 * This is followed by checking if the customer has added their bag(s) to the bagging area
	 * and no weight discrepancy is detected.
	 */
	@Test
	public void checkNoWeightDiscrepancy() {
		control.startSession();
		control.signalAddOwnBags();
		station.baggingArea.addAnItem(ExampleItems.CustomerBag.barcodedItem);
		assertEquals(ExampleItems.CustomerBag.actualMass, control.getOrder().getExpectedMass());		
	}
	
	/**
	 * Checks if the weight expected by the system is the same 
	 * as the weight as the bag added to the bagging area.
	 */
	@Test
	public void checkExpectedWeightEqualsBagWeight() {
		control.startSession();
		//customer signals to add their own bag
		control.signalAddOwnBags();
		station.baggingArea.addAnItem(ExampleItems.CustomerBag.barcodedItem);
		assertEquals(ExampleItems.CustomerBag.actualMass, control.getOrder().getExpectedMass());	
		
	}
	
	/**
	 * Checks if in the event that the customer does not signal that they wish to use their own bag, 
	 * does this result in a weight discrepancy and the station being blocked.
	 */
	@Test
	public void checkWeightDiscrepency() {
		control.startSession();
		station.baggingArea.addAnItem(ExampleItems.CustomerBag.barcodedItem); //causes a weight discrepancy
		assertTrue(control.isBlocked());
	}
	
	/**
	 * Checks if the customer is able to successfully signal that they wish to use their own bag.
	 */
	@Test
	public void checkAddOwnBag() {
		control.startSession();
		//customer has not signaled that they wish to use their own bag
		Boolean checkStatus = control.getAddingOwnBags();
		assertFalse(checkStatus);
		//customer has signaled that they wish to use their own bag
		control.signalAddOwnBags();
		checkStatus = control.getAddingOwnBags();
		assertTrue(checkStatus);
	}
	
	/**
	 * Checks if the customer is provided the correct notifications as they interact
	 * with the bagging area and further signal that they wish to add their own bag(s).
	 */
	@Test
	public void notifyCustomer() {
		
		control.startSession();
		assertEquals(control.getLastNotification(), null);	
		//customer signals to add their own bag
		control.signalAddOwnBags();
		station.baggingArea.addAnItem(ExampleItems.CustomerBag.barcodedItem);
		//check if the correct notification is displayed 
		assertEquals(control.getLastNotification(), "Customer: You may now continue.");
		//control.notifyOtherCode;
	}
	
	/**
	 * System notifies customer to add their bag to the bagging area 
	 * after customer signals their desire to add their bag 
	 */
	@Test
	public void notifyCustomerCode() {
		control.startSession();
		//customer signals to add their own bag
		control.signalAddOwnBags();
		assertEquals(control.notifyAddOwnBagCode, control.getCustomerNotified());
	};
	
	/**
	 * System notifies customer that they may now continue the session after
	 * successfully adding their own bag.
	 */
	@Test
	public void notifyCustomerCode2() {
		control.startSession();
		//customer signals to add their own bag
		control.signalAddOwnBags();
		station.baggingArea.addAnItem(ExampleItems.CustomerBag.barcodedItem);
		assertEquals(control.getCustomerNotified(), "other");		
	};

}
