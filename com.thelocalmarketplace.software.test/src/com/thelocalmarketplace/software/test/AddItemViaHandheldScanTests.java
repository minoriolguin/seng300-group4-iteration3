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

import java.math.BigDecimal;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import com.jjjwelectronics.Mass;
import com.jjjwelectronics.Numeral;
import com.jjjwelectronics.scanner.Barcode;
import com.jjjwelectronics.scanner.BarcodedItem;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.hardware.SelfCheckoutStationGold;
import com.thelocalmarketplace.software.CustomerStationControl;
import com.thelocalmarketplace.software.Order;
import com.thelocalmarketplace.software.SessionItem;

import ca.ucalgary.seng300.simulation.InvalidArgumentSimulationException;
import powerutility.PowerGrid;

/**
 * Test class for adding items via handheld scan functionality.
 */
public class AddItemViaHandheldScanTests {

    // Test objects
    private AbstractSelfCheckoutStation station;
    private CustomerStationControl control;

    /**
     * Initialisation method run before each test.
     */
    
    @Before
    public void init() {
        // initialise station and turn on required components
        AbstractSelfCheckoutStation.resetConfigurationToDefaults();
        
        station = new SelfCheckoutStationGold();
        PowerGrid.engageUninterruptiblePowerSource();
        station.plugIn(PowerGrid.instance());
        station.turnOn();

        // Initialise control
        control = new CustomerStationControl(station);
        control.startSession();

        // Initialise database
        ExampleItems.updateDatabase();
        
        //instantiate mass
        control.getWeightDiscrepancyControl().instantiateMass(0);
    }

    /**
     * Test for adding an item via handheld scan with a successful scenario.
     */
    @Test
    public void testAddItemViaHandheldScan_Success() {
		int success = 0;
		int totalRuns = 1000;
		double targetSuccessRate = 0.8;
		for(int i = 0; i < totalRuns; i++) {
			init();
	        // Set up the necessary interactions for a successful item addition via handheld scan
	        Order order = control.getOrder();
	        station.handheldScanner.scan(ExampleItems.AppleJuice.barcodedItem);
	        station.baggingArea.addAnItem(ExampleItems.AppleJuice.barcodedItem);
	
	        // Assertions
	        if(1 == order.getItems().size()) success++;
		}
		assertTrue(success >= totalRuns*targetSuccessRate);
    }

    /**
     * Test for adding an item via handheld scan with a blocked station scenario.
     */
    @Test
    public void testAddItemViaHandheldScan_Fail_StationBlocked() {
		int success = 0;
		int totalRuns = 1000;
		double targetSuccessRate = 0.8;
		for(int i = 0; i < totalRuns; i++) {
			init();
	        // Set up the necessary interactions for a blocked station
	        control.block();
	        Order order = control.getOrder();
	
	        station.handheldScanner.scan(ExampleItems.AppleJuice.barcodedItem);
	
	        // Assertions
	        if( (0 == order.getItems().size()) && // No items should be added
	        (BigDecimal.valueOf(0).equals(order.getTotal())) && // Total should remain zero
	        (control.isBlocked()) &&
	        control.getLastNotification() == null) success++; // No customer notification
		}
		assertTrue(success >= totalRuns*targetSuccessRate);
    }

    /**
     * Test for adding an item via handheld scan with an invalid barcode scenario.
     */
    @Test(expected = InvalidArgumentSimulationException.class)
    public void testAddItemViaHandheldScan_Fail_InvalidBarcode() {
    	
		int success = 0;
		int totalRuns = 1000;
		double targetSuccessRate = 0.8;
		for(int i = 0; i < totalRuns; i++) {
			init();
	        control.unblock();
	        Order order = control.getOrder();
	        Numeral[] emptyNumeral = {};
	        Barcode barcode = new Barcode(emptyNumeral);
	        BarcodedItem invalidItem = new BarcodedItem(barcode, new Mass(0)); 
	        station.handheldScanner.scan(invalidItem);
	        // Assertions
	        if( (0 == order.getItems().size()) && // No items should be added
	        (BigDecimal.valueOf(0).equals(order.getTotal())) && // Total should remain zero
	        (BigDecimal.valueOf(100).equals(order.getTotalUnpaid())) && // Total unpaid should remain the same
	        (!control.isBlocked()) &&
	        ("".equals(control.getLastNotification()))) success++; // No customer notification
		}
		assertTrue(success >= totalRuns*targetSuccessRate);
    }

    /**
     * Test to check if item is successfully added when scanned by handheld scanner
     */
    @Test
    public void itemAddedWhenScanned() {
    	
		int success = 0;
		int totalRuns = 1000;
		double targetSuccessRate = 0.8;
		for(int i = 0; i < totalRuns; i++) {
			init();
	        control.startSession();
	        Order order = control.getOrder();
	        ArrayList<SessionItem> items = order.getItems();
	
	        station.handheldScanner.scan(ExampleItems.AppleJuice.barcodedItem);
	        // Check if the expected mass of the added item matches the item that was added
	        
	        try {
				if( (items.get(0).getMass().equals(new Mass(ExampleItems.AppleJuice.expectedWeightGrams))) &&
				!(items.get(0).getMass().equals(new Mass(ExampleItems.PotatoChips.expectedWeightGrams)))) success++;
			} catch (Exception e) {}
		}
		assertTrue(success >= totalRuns*targetSuccessRate);
    }

    /**
     * Test for scanning items without adding them to the bagging area
     */
    @Test
    public void systemBlockedWhenAdded() {
    	int success = 0;
		int totalRuns = 1000;
		double targetSuccessRate = 0.8;
		for(int i = 0; i < totalRuns; i++) {
			init();
	        control.startSession();
	        Order order = control.getOrder();
	        ArrayList<SessionItem> items = order.getItems();
	
	        // Scan an item
	        station.handheldScanner.scan(ExampleItems.PeanutButter.barcodedItem);
	        // system is now blocked
	
	        // Scan an item after the system blocked, should be no items added.
	        station.handheldScanner.scan(ExampleItems.PotatoChips.barcodedItem);
	        if(1 == items.size()) success++;
		}
        assertTrue(success >= totalRuns*targetSuccessRate);
    }
    
    /**
	 * Test to check if the total price updates correctly as a new item is added
	 */
	@Test
	public void totalPriceUpdatedCorrectly() {
		int success = 0;
		int totalRuns = 1000;
		double targetSuccessRate = 0.8;
		for (int i = 0; i < totalRuns; i++) {
			init();
			control.startSession();
			Order order = control.getOrder();
			BigDecimal expectedTotal = BigDecimal.ZERO;

			assertEquals(expectedTotal, order.getTotal());
			boolean a = expectedTotal.equals(order.getTotal());

			// Scan an item and check if the price is updated
			station.handheldScanner.scan(ExampleItems.AppleJuice.barcodedItem);
			station.baggingArea.addAnItem(ExampleItems.AppleJuice.barcodedItem);
			expectedTotal = expectedTotal.add(ExampleItems.AppleJuice.bdPrice);
			if (a && expectedTotal.equals(order.getTotal())) ++success;
		}
		assertTrue(success >= targetSuccessRate);
	}

	/**
	 * Test to check if total unpaid price is updated correctly as new items are
	 * added
	 */
	@Test
	public void totalUnpaidPriceUpdatedCorrectly() {
		int success = 0;
		int totalRuns = 1000;
		double targetSuccessRate = 0.8;
		for (int i = 0; i < totalRuns; i++) {
			init();
			control.startSession();
			Order order = control.getOrder();
			BigDecimal expectedUnpaid = BigDecimal.ZERO;

			boolean a = expectedUnpaid.compareTo(order.getTotalUnpaid().abs()) == 0;

			// Scan an item and check if the price is updated
			station.handheldScanner.scan(ExampleItems.AppleJuice.barcodedItem);
			expectedUnpaid = expectedUnpaid.add(ExampleItems.AppleJuice.bdPrice);

			if (expectedUnpaid.equals(order.getTotalUnpaid()) && a)
				++success;
		}
		assertTrue(success >= targetSuccessRate);
	}

	/**
	 * Test for system unblocked when customer adds an item correctly
	 */
	@Test
	public void systemUnblocked() {
		int success = 0;
		int totalRuns = 1000;
		double targetSuccessRate = 0.8;
		for (int i = 0; i < totalRuns; i++) {
			init();
			control.startSession();
			// Scan an item
			station.handheldScanner.scan(ExampleItems.AppleJuice.barcodedItem);
			boolean b = control.isBlocked();

			// Place an item on the scale
			station.baggingArea.addAnItem(ExampleItems.AppleJuice.barcodedItem);
			boolean c = !control.isBlocked();

			if (b && c)
				++success;
		}
		assertTrue(success >= targetSuccessRate);
	}

	/**
	 * Test to check if customer is notified correctly to bag their item
	 */
	@Test
	public void notifiesCustomerToBagItem() {
		int success = 0;
		int totalRuns = 1000;
		double targetSuccessRate = 0.8;
		for (int i = 0; i < totalRuns; i++) {
			init();
			control.startSession();
			station.handheldScanner.scan(ExampleItems.AppleJuice.barcodedItem);
			if (control.getCustomerNotified().equals(control.notifyPlaceItemInBaggingAreaCode)) ++success;
		}
		assertTrue(success >= targetSuccessRate);
	}
}