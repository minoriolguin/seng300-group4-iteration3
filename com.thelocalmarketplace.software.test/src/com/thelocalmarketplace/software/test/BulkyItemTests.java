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

import static org.junit.Assert.assertTrue;


import java.math.BigDecimal;
import org.junit.Before;
import org.junit.Test;
import com.jjjwelectronics.Mass;
import org.junit.Before;
import org.junit.Test;
import com.jjjwelectronics.OverloadedDevice;
import com.tdc.CashOverloadException;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.hardware.SelfCheckoutStationGold;
import com.thelocalmarketplace.software.CustomerStationControl;
import com.thelocalmarketplace.software.Order;
import com.thelocalmarketplace.software.test.ExampleItems.AppleJuice;
import powerutility.PowerGrid;

public class BulkyItemTests {
	private CustomerStationControl control;
	private AbstractSelfCheckoutStation station;
	private Order order;

	// Setup to initialize common components before testing
	@Before
	public void setup() throws CashOverloadException, OverloadedDevice {
		AbstractSelfCheckoutStation.resetConfigurationToDefaults();
		station = new SelfCheckoutStationGold();
		station.plugIn(PowerGrid.instance());
		station.turnOn();
		
		// Initialize control
		control = new CustomerStationControl(station);
		
		// Initialize database
		ExampleItems.updateDatabase();
		
		control.startSession();
		this.order = control.getOrder();
	}

	@Test
	public void addBulkyNormalUse() {
		control.signalBulkyItem();
		station.mainScanner.scan(ExampleItems.AppleJuice.barcodedItem);
		assertTrue(order.getItems().size() == 1);
		assertTrue(order.getBulkyItems().size() == 1);
	}

	@Test
	public void addBulkyMidway() {
		station.mainScanner.scan(ExampleItems.AppleJuice.barcodedItem);
		station.baggingArea.addAnItem(ExampleItems.AppleJuice.barcodedItem);
		control.signalBulkyItem();
		station.mainScanner.scan(ExampleItems.PeanutButter.barcodedItem);
		assertTrue(order.getItems().size() == 2);
		assertTrue(order.getBulkyItems().size() == 1);
	}

	@Test
	public void addTwoBulky() {
		control.signalBulkyItem();
		station.mainScanner.scan(ExampleItems.AppleJuice.barcodedItem);

		control.signalBulkyItem();
		station.mainScanner.scan(ExampleItems.PeanutButter.barcodedItem);
		
		assertTrue(order.getItems().size() == 2);
		assertTrue(order.getBulkyItems().size() == 2);
	}

	@Test
	public void addBulkyWhileStationBlocked() {
		control.block();
		control.signalBulkyItem();
		station.mainScanner.scan(ExampleItems.PeanutButter.barcodedItem);

		assertTrue(order.getItems().size() == 0);
		assertTrue(order.getBulkyItems().size() == 0);
	}

	@Test
	public void addBulkyExpectedWeight() {
		station.mainScanner.scan(ExampleItems.AppleJuice.barcodedItem);
		station.baggingArea.addAnItem(ExampleItems.AppleJuice.barcodedItem);

		control.signalBulkyItem();
		station.mainScanner.scan(ExampleItems.PeanutButter.barcodedItem);
		assertTrue(order.getExpectedMass().compareTo(ExampleItems.AppleJuice.actualMass) == 0);

	}

	@Test
	public void addOnlyBulkyExpectedWeight() {
		control.signalBulkyItem();
		station.mainScanner.scan(ExampleItems.PeanutButter.barcodedItem);
		assertTrue(order.getExpectedMass().compareTo(new Mass(BigDecimal.ZERO)) == 0);

	}

}
