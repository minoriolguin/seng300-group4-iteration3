package com.thelocalmarketplace.software.test;

import org.junit.Before;
import org.junit.Test;

import com.jjjwelectronics.scanner.Barcode;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.hardware.SelfCheckoutStationGold;
import com.thelocalmarketplace.software.CustomerStationControl;
import com.thelocalmarketplace.software.Order;

import powerutility.PowerGrid;

import static org.junit.Assert.*;

import java.math.BigDecimal;

public class RemoveItemTests {

    private Order order;
	private AbstractSelfCheckoutStation station;
	private CustomerStationControl control;

    @Before
    public void setUp() {
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
		control.startSession();
		order = control.getOrder();
    }

    @Test
    public void testRemoveValidIndex() {
        // Test removing an item with a valid index
    	station.mainScanner.scan(ExampleItems.AppleJuice.barcodedItem);
        station.baggingArea.addAnItem(ExampleItems.AppleJuice.barcodedItem); // Add a sample item
        order.remove(0); // Remove the item
        assertTrue(order.getItems().isEmpty()); // Check if the items list is empty
        assertEquals(BigDecimal.ZERO, order.getTotal()); // Check if the total price is zero
        assertEquals(BigDecimal.ZERO, order.getTotalUnpaid()); // Check if the total unpaid is zero
    }

    @Test
    public void testRemoveInvalidIndex() {
        // Test removing an item with an invalid index
    	station.mainScanner.scan(ExampleItems.AppleJuice.barcodedItem);
        station.baggingArea.addAnItem(ExampleItems.AppleJuice.barcodedItem); // Add a sample item
        order.remove(1); // Attempt to remove an item with an invalid index (should be ignored)
    }

    @Test
    public void testRemoveFromEmptyOrder() {
        // Test removing from an empty order (should not throw an exception)
        order.remove(0);
        assertTrue(order.getItems().isEmpty()); // Check if the items list is still empty
        assertEquals(BigDecimal.ZERO, order.getTotal()); // Check if the total price is still zero
        assertEquals(BigDecimal.ZERO, order.getTotalUnpaid()); // Check if the total unpaid is still zero
    }
}
