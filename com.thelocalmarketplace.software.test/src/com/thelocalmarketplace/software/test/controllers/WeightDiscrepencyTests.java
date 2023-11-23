package com.thelocalmarketplace.software.test.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.jjjwelectronics.Mass;
import com.jjjwelectronics.Numeral;
import com.jjjwelectronics.scanner.Barcode;
import com.jjjwelectronics.scanner.BarcodedItem;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.Product;
import com.thelocalmarketplace.hardware.SelfCheckoutStationBronze;
import com.thelocalmarketplace.hardware.external.ProductDatabases;
import com.thelocalmarketplace.software.logic.CentralStationLogic;
import com.thelocalmarketplace.software.logic.StateLogic.States;

import ca.ucalgary.seng300.simulation.SimulationException;
import powerutility.NoPowerException;
import powerutility.PowerGrid;

/**
 * @author Angelina Rochon (30087177)
 * ----------------------------------
 * @author Tara Strickland (10105877)
 * @author Connell Reffo (10186960)
 * @author Julian Fan (30235289)
 * @author Braden Beler (30084941)
 * @author Samyog Dahal (30194624)
 * @author Maheen Nizmani (30172615)
 * @author Phuong Le (30175125)
 * @author Daniel Yakimenka (10185055)
 * @author Merick Parkinson (30196225)
 * @author Farida Elogueil (30171114)
 */
public class WeightDiscrepencyTests {
	
	SelfCheckoutStationBronze station;
	CentralStationLogic session;
	
	//stuff for database
	public ProductDatabases database_one_item;
	public Barcode barcode;
	public Numeral digits;
	
	public BarcodedItem bitem;
	public Mass itemMass;
	
	public BarcodedItem bitem2;
	public Mass itemMass2;
	public BarcodedItem bitem3;
	public Mass itemMass3;
	public Numeral[] barcode_numeral;
	public BarcodedProduct product;
	
	
	//the following function was taken mainly from Angelina's tests for bulkyitems
	public void scanUntilAdded(Product p, BarcodedItem b) {
		while(!session.cartLogic.getCart().containsKey(p)) {
			station.handheldScanner.scan(b);
		}
	}
	
	@Before public void setUp() {
		PowerGrid.engageUninterruptiblePowerSource();
		PowerGrid.instance().forcePowerRestore();
		
		AbstractSelfCheckoutStation.resetConfigurationToDefaults();
		
		//d1 = new dummyProductDatabaseWithOneItem();
		//d2 = new dummyProductDatabaseWithNoItemsInInventory();
		station = new SelfCheckoutStationBronze();
		
		//initialize database
		barcode_numeral = new Numeral[]{Numeral.one,Numeral.two, Numeral.three};
		barcode = new Barcode(barcode_numeral);
		product = new BarcodedProduct(barcode, "some item",5,(double)300.0);
		ProductDatabases.BARCODED_PRODUCT_DATABASE.clear();
		ProductDatabases.INVENTORY.clear();
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode, product);
		ProductDatabases.INVENTORY.put(product, 1);


		//initialize barcoded item
		itemMass = new Mass((long) 1000000);
		bitem = new BarcodedItem(barcode, itemMass);
		itemMass2 = new Mass((double) 300.0);//300.0 grams
		bitem2 = new BarcodedItem(barcode, itemMass2);
		itemMass3 = new Mass((double) 300.0);//3.0 grams
		bitem3 = new BarcodedItem(barcode, itemMass3);
	}
	
	@After 
	public void tearDown() {
		PowerGrid.engageFaultyPowerSource();
	}
	
	/** Ensures failures do not occur from scanner failing to scan item, thus isolating test cases */
	public void scanUntilAdded(BarcodedItem item) {
		do {
			station.handheldScanner.scan(item);
		} while (!session.cartLogic.getCart().containsKey(product));
	}
	
	
	@Test (expected = NoPowerException.class) public void testWeightDiscrepencyWithNoPower() {
		station.plugIn(PowerGrid.instance());
		station.turnOff();
		session = new CentralStationLogic(station);
		session.startSession();
		this.scanUntilAdded(product, bitem);
		station.handheldScanner.scan(bitem);
		station.baggingArea.addAnItem(bitem);
		
	}
	@Test (expected = SimulationException.class)public void testWeightDiscrepencyWithoutPowerTurnOn() {
		station.turnOn();
		session = new CentralStationLogic(station);
		this.scanUntilAdded(product, bitem2);
		session.startSession();
		station.baggingArea.addAnItem(bitem2);
	}
	@Test public void testWeightDiscrepencyWithPowerTurnOnNoDiscrepency() {
		station.plugIn(PowerGrid.instance());
		station.turnOn();
		session = new CentralStationLogic(station);
		session.startSession();
		this.scanUntilAdded(product, bitem3);
		station.baggingArea.addAnItem(bitem3);
		assertTrue("weight discrepency detected", !this.session.stateLogic.inState(States.BLOCKED));
	}

	
	@Test 
	public void testWeightDiscrepencyWithPowerTurnOnHasDiscrepencyDifferentThanItem() {

		station.plugIn(PowerGrid.instance());
		station.turnOn();
		
		session = new CentralStationLogic(station);
		session.startSession();
		
		this.scanUntilAdded(product, bitem2);
		station.baggingArea.addAnItem(bitem);

		assertTrue("weight discrepancy not detected", session.weightLogic.checkWeightDiscrepancy());
		assertTrue("station not blocked", this.session.stateLogic.inState(States.BLOCKED));
	}
	
	@Test public void testWeightDiscrepencyWithPowerTurnOnHasDiscrepencyNoItem() {
		station.plugIn(PowerGrid.instance());
		station.turnOn();
		
		session = new CentralStationLogic(station);
		session.startSession();
		
		//station.scanner.scan(bitem2);
		station.baggingArea.addAnItem(bitem2);
		assertTrue("weight discrepency not detected", this.session.stateLogic.inState(States.BLOCKED));
	}@Test public void testWeightDiscrepencyWithPowerTurnOnNoDiscrepencyOnSensativity() {
		station.plugIn(PowerGrid.instance());
		station.turnOn();
		
		session = new CentralStationLogic(station);;
		session.startSession();
		
		//sensativity of the scale is 100mg
		Mass  sensativity = station.baggingArea.getSensitivityLimit();
		Mass m = sensativity.sum(itemMass2);
		BarcodedItem i= new BarcodedItem(barcode, m);
		this.scanUntilAdded(product, bitem2);
		station.baggingArea.addAnItem(i);
		assertTrue("weight discrepency tedected", !this.session.stateLogic.inState(States.BLOCKED));
	}@Test public void testWeightDiscrepencyWithPowerTurnOnNoDiscrepencyWithinSensativity() {
		station.plugIn(PowerGrid.instance());
		station.turnOn();
		
		session = new CentralStationLogic(station);
		session.startSession();
		
		//sensativity of the scale is 100mg
		Mass sensativity = station.baggingArea.getSensitivityLimit();
		Mass m = sensativity.sum(itemMass2.difference(new Mass(1)).abs());
		BarcodedItem i= new BarcodedItem(barcode, m);
		this.scanUntilAdded(product, bitem2);
		
		station.baggingArea.addAnItem(i);
		assertTrue("weight discrepency tedected", !this.session.stateLogic.inState(States.BLOCKED));
	}
	
	@Test
	public void testWeightDiscrepencyWithPowerTurnOnHasDiscrepency() {
		station.plugIn(PowerGrid.instance());
		station.turnOn();
		
		session = new CentralStationLogic(station);
		session.startSession();
		
		this.scanUntilAdded(product, bitem2);
		station.baggingArea.addAnItem(bitem);

		assertTrue("weight discrepancy detected", this.session.stateLogic.inState(States.BLOCKED));
	}
	
	
	@Test public void testWeightDiscrepencyWithPowerTurnOnNoDiscrepencyItemPlacedBack() {
		station.plugIn(PowerGrid.instance());
		station.turnOn();
		
		session = new CentralStationLogic(station);
		session.startSession();
		
		//station.scanner.scan(bitem2);
		station.baggingArea.addAnItem(bitem);
		station.baggingArea.removeAnItem(bitem);
		assertTrue("weight discrepency tedected", !this.session.stateLogic.inState(States.BLOCKED));

	}
	
	@Test 
	public void testWeightDiscrepencyWithPowerTurnOnNoDiscrepencyItemPlacedBackAfterOther() {
		station.plugIn(PowerGrid.instance());
		station.turnOn();
		
		session = new CentralStationLogic(station);
		session.startSession();
		
		this.scanUntilAdded(product, bitem3);
		station.baggingArea.addAnItem(bitem);
		station.baggingArea.removeAnItem(bitem);
		station.baggingArea.addAnItem(bitem3);
		assertFalse("weight discrepancy detected when shouldn't be", this.session.stateLogic.inState(States.BLOCKED));
	}
	
	@Test public void testWeightDiscrepencyWithPowerTurnOnHasDiscrepencyRescan() {
		station.plugIn(PowerGrid.instance());
		station.turnOn();
		
		session = new CentralStationLogic(station);
		session.startSession();
		
		this.scanUntilAdded(product, bitem);
		this.scanUntilAdded(product, bitem);
		
		assertEquals(1, session.cartLogic.getCart().size());
	}
	
	@Test(expected = SimulationException.class)
	public void testAddExpectedWeightNonExistentBarcode() {
		station.plugIn(PowerGrid.instance());
		station.turnOn();
		
		session = new CentralStationLogic(station);
		session.startSession();
		
		this.session.weightLogic.addExpectedWeight(new Barcode(new Numeral[] {Numeral.one}));
	}
	
	@Test(expected = SimulationException.class)
	public void testRemoveExpectedWeightNonExistentBarcode() {
		station.plugIn(PowerGrid.instance());
		station.turnOn();
		
		session = new CentralStationLogic(station);
		session.startSession();
		
		this.session.weightLogic.removeExpectedWeight(new Barcode(new Numeral[] {Numeral.one}));
	}
}