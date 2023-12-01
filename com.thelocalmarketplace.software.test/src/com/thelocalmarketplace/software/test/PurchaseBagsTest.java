package com.thelocalmarketplace.software.test;

import com.jjjwelectronics.Numeral;
import com.jjjwelectronics.scanner.Barcode;
import com.thelocalmarketplace.hardware.*;
import com.thelocalmarketplace.software.*;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import com.jjjwelectronics.EmptyDevice;
import com.jjjwelectronics.OverloadedDevice;
import com.jjjwelectronics.bag.ReusableBag;
import com.thelocalmarketplace.hardware.external.ProductDatabases;

import powerutility.PowerGrid;

public class PurchaseBagsTest {
	private Software software;
	private AbstractSelfCheckoutStation hardware;
	private ReusableBag bag;
	private Barcode reuseableBagBarcode;


	@Before
	public void setUp() throws OverloadedDevice {
		PowerGrid powerGrid = PowerGrid.instance();
		AbstractSelfCheckoutStation.resetConfigurationToDefaults();
		AbstractSelfCheckoutStation.configureReusableBagDispenserCapacity(300);
		hardware = new SelfCheckoutStationGold();
		software = Software.getInstance(hardware);
		software.turnOn();


		// define a reusable Bag Barcode, Price and put it in the database
		Numeral[] reuseableBagNumeral = new Numeral[3];
		reuseableBagNumeral[0] = Numeral.nine;
		reuseableBagNumeral[1] = Numeral.five;
		reuseableBagNumeral[2] = Numeral.three;
		reuseableBagBarcode = new Barcode(reuseableBagNumeral);

	}

	@Test
	public void addBagsToTheDispenser() throws OverloadedDevice {
		software.purchaseBags.addBagsToDispenser(10);
		assertEquals(10, software.reusableBagDispenser.getQuantityRemaining());
	}
	@Test
	public void addReusableBag() throws EmptyDevice, OverloadedDevice {
		software.purchaseBags.addBagsToDispenser(10);
		software.purchaseBags.AddBagToOrder(1);
		assertTrue(software.getBarcodedProductsInOrder().contains(ProductDatabases.BARCODED_PRODUCT_DATABASE.get(reuseableBagBarcode)));
		assertEquals(9,software.reusableBagDispenser.getQuantityRemaining());
	}
	//if no bags were ever added then we would get Empty device exception, but when it goes empty we handle it
	@Test
	public void addReusableBagWhenDispenserEmpty() throws EmptyDevice, OverloadedDevice {
		software.purchaseBags.addBagsToDispenser(1);
		software.purchaseBags.AddBagToOrder(2);
		assertTrue(software.attendant.reusableBagsEmpty);

	}
}


