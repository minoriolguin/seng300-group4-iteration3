package com.thelocalmarketplace.software.test;

import com.jjjwelectronics.Numeral;
import com.jjjwelectronics.bag.IReusableBagDispenser;
import com.jjjwelectronics.scanner.Barcode;
import com.thelocalmarketplace.hardware.*;
import com.thelocalmarketplace.software.PurchaseBags;
import com.thelocalmarketplace.software.*;
import com.thelocalmarketplace.software.UpdateCart;
import com.thelocalmarketplace.software.WeightDiscrepancy;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import com.jjjwelectronics.EmptyDevice;
import com.jjjwelectronics.OverloadedDevice;
import com.jjjwelectronics.bag.AbstractReusableBagDispenser;
import com.jjjwelectronics.bag.ReusableBag;
import com.jjjwelectronics.bag.ReusableBagDispenserGold;
import com.thelocalmarketplace.hardware.external.ProductDatabases;

import powerutility.PowerGrid;

public class PurchaseBagsTest {
	private Software software;
	private AbstractSelfCheckoutStation hardware;
	private PurchaseBags bags;
	private IReusableBagDispenser ReusableBagDispenser;
	private PLUCodedProduct product;

	private ReusableBag bag;

	private Barcode reuseableBagBarcode;

	private BarcodedProduct reusableBag;

	@Before
	public void setUp() throws OverloadedDevice {
		PowerGrid powerGrid = PowerGrid.instance();
		AbstractSelfCheckoutStation.resetConfigurationToDefaults();
		AbstractSelfCheckoutStation.configureReusableBagDispenserCapacity(300);
		hardware = new SelfCheckoutStationGold();
		software = Software.getInstance(hardware);
		software.turnOn();
		this.ReusableBagDispenser = software.reusableBagDispenser;

		// define a reusable Bag Barcode, Price and put it in the database
		bag = new ReusableBag();
		Numeral[] reuseableBagNumeral = new Numeral[3];
		reuseableBagNumeral[0] = Numeral.nine;
		reuseableBagNumeral[1] = Numeral.five;
		reuseableBagNumeral[2] = Numeral.three;
		reuseableBagBarcode = new Barcode(reuseableBagNumeral);
		double expectedWeight = bag.getMass().inGrams().doubleValue();
		String description = "Reusable Bag";
		long price = 2;
		reusableBag = new BarcodedProduct(reuseableBagBarcode, description, price, expectedWeight);

	}
	@Test
	public void addReusableBag() throws EmptyDevice, OverloadedDevice {
		software.purchaseBags.addBagsToDispenser(10);
		software.purchaseBags.AddBagToOrder(1);
		assertTrue(software.getBarcodedProductsInOrder().isEmpty());
	}






//
//	@Test
//	public void BagHasBeenAdded() throws OverloadedDevice, EmptyDevice {
//		ReusableBag bag1 = new ReusableBag();
//		ReusableBag bag2 = new ReusableBag();
//		ReusableBagDispenser.unload();
//		System.out.println(ReusableBagDispenser.getQuantityRemaining());
//		ReusableBagDispenser.load(bag1);
//		ReusableBagDispenser.load(bag1);
//		System.out.println(ReusableBagDispenser.getQuantityRemaining());
//		bags.AddBagToOrder();
//		//System.out.println(ReusableBagDispenser.unload().length);
//		System.out.println(ReusableBagDispenser.getQuantityRemaining());
//		assert(ReusableBagDispenser.getQuantityRemaining()== 1);
//		assert(software.getProductsInOrder().containsKey(product));
//
//
//	}
//	@Test
//	public void NobagsAvalible() throws EmptyDevice, OverloadedDevice {
//		ReusableBag bag1 = new ReusableBag();
//		ReusableBag bag2 = new ReusableBag();
//		ReusableBagDispenser.load(bag1);
//		ReusableBagDispenser.load(bag2);
//		bags.AddBagToOrder();
//		bags.AddBagToOrder();
//		bags.AddBagToOrder();
//		assert(ReusableBagDispenser.getQuantityRemaining() == 0);
//	}

}


