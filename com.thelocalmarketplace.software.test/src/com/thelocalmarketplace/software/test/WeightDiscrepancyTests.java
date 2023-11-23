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
import org.junit.Before;
import org.junit.Test;

import com.jjjwelectronics.scanner.BarcodedItem;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.hardware.SelfCheckoutStationGold;
import com.thelocalmarketplace.software.CustomerStationControl;

import org.junit.Assert;
import powerutility.PowerGrid;

public class WeightDiscrepancyTests {
	private AbstractSelfCheckoutStation station;
	private CustomerStationControl control;
	
	@Before
	public void init() {
		// initialize station and turn on required components
		AbstractSelfCheckoutStation.resetConfigurationToDefaults();
		station = new SelfCheckoutStationGold();
		station.plugIn(PowerGrid.instance());
		PowerGrid.engageUninterruptiblePowerSource();
		station.baggingArea.turnOn();
		station.mainScanner.turnOn();
		
		// Initialize control and start the session
		control = new CustomerStationControl(station);
		control.startSession();
		
		// Initialize database
		ExampleItems.updateDatabase();
	}
	
	/**
	 * After fixing a discrepancy by adding the correct item, the station is unblocked.
	 */
	@Test
	public void correctDiscrepancyByAddingItem() {
		createDiscrepancyByRemoving(ExampleItems.PotatoChips.barcodedItem);
		station.baggingArea.addAnItem(ExampleItems.PotatoChips.barcodedItem);
		Assert.assertTrue(!control.isBlocked());
	}
	
	/**
	 * After fixing a discrepancy by removing incorrect item, the station is unblocked.
	 */
	@Test
	public void correctDiscrepancyByRemovingItem() {
		station.mainScanner.scan(ExampleItems.PotatoChips.barcodedItem);
		station.baggingArea.addAnItem(ExampleItems.PotatoChips.barcodedItem);
		station.baggingArea.addAnItem(ExampleItems.AppleJuice.barcodedItem);
		station.baggingArea.removeAnItem(ExampleItems.AppleJuice.barcodedItem);
		Assert.assertTrue(!control.isBlocked());
	}
	
	
	/**
	 * The station is blocked after adding the wrong item to the bagging area.
	 */
	@Test
	public void addWrongItem() {
		station.mainScanner.scan(ExampleItems.PotatoChips.barcodedItem);
		station.baggingArea.addAnItem(ExampleItems.AppleJuice.barcodedItem);
		Assert.assertTrue(control.isBlocked());
	}
	
	/**
	 * The station is blocked after removing the correctly added item from the bagging area.
	 */
	@Test
	public void removeCorrectlyAddedItem() {
		station.mainScanner.scan(ExampleItems.PotatoChips.barcodedItem);
		station.baggingArea.addAnItem(ExampleItems.PotatoChips.barcodedItem);
		station.baggingArea.removeAnItem(ExampleItems.PotatoChips.barcodedItem);
		Assert.assertTrue(control.isBlocked());
	}
	
	/**
	 * The station is blocked after adding an additional item after correctly adding an item to the bagging area.
	 */
	@Test
	public void addAdditionalItem() {
		station.mainScanner.scan(ExampleItems.PotatoChips.barcodedItem);
		station.baggingArea.addAnItem(ExampleItems.PotatoChips.barcodedItem);
		station.baggingArea.addAnItem(ExampleItems.AppleJuice.barcodedItem);
		Assert.assertTrue(control.isBlocked());
	}
	
	/**
	 * The attendant is notified after a weight discrepancy occurs. Currently uses
	 * a variable in control for testing due to lack signaling UI implementation.
	 */
	@Test
	public void discrepancyAttendantNotified() {
		createDiscrepancyByRemoving(ExampleItems.PotatoChips.barcodedItem);
		Assert.assertEquals(control.getAttendantNotified(), control.notifyDiscrepancyCode);
	}
	
	/**
	 * The customer is notified after a weight discrepancy occurs. Currently uses
	 * a variable in control for testing due to lack signaling UI implementation.
	 */
	@Test
	public void discrepancyCustomerNotified() {
		createDiscrepancyByRemoving(ExampleItems.PotatoChips.barcodedItem);
		Assert.assertEquals(control.getCustomerNotified(), control.notifyDiscrepancyCode);
	}
	
	/**
	 * The station is unable to add items to the order after a discrepancy occurs.
	 */
	@Test
	public void discrepancyAddItem() {
		createDiscrepancyByRemoving(ExampleItems.PotatoChips.barcodedItem);
		station.mainScanner.scan(ExampleItems.AppleJuice.barcodedItem);
		Assert.assertTrue(control.getOrder().getItems().size()==1);
	}
	
	/**
	 * Creates a weight discrepancy by removing a correctly added item from the bagging area.
	 */
	private void createDiscrepancyByRemoving(BarcodedItem item) {
		station.mainScanner.scan(item);
		station.baggingArea.addAnItem(item);
		station.baggingArea.removeAnItem(item);
	}
}
