package com.thelocalmarketplace.software.test.controllers;

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
import com.thelocalmarketplace.hardware.SelfCheckoutStationGold;
import com.thelocalmarketplace.hardware.external.ProductDatabases;
import com.thelocalmarketplace.software.logic.AttendantLogic;
import com.thelocalmarketplace.software.logic.CentralStationLogic;
import com.thelocalmarketplace.software.logic.StateLogic.States;

import ca.ucalgary.seng300.simulation.InvalidArgumentSimulationException;
import powerutility.PowerGrid;

/**
 * @author Angelina Rochon (30087177)
 * ----------------------------------
 * @author Connell Reffo (10186960)
 * @author Tara Strickland (10105877)
 * @author Julian Fan (30235289)
 * @author Braden Beler (30084941)
 * @author Samyog Dahal (30194624)
 * @author Maheen Nizmani (30172615)
 * @author Phuong Le (30175125)
 * @author Daniel Yakimenka (10185055)
 * @author Merick Parkinson (30196225)
 * @author Farida Elogueil (30171114)
 */
public class HandleBulkyItemTests {
	
	private SelfCheckoutStationGold station;
	private CentralStationLogic session;
	private BarcodedItem barcodedItem;
	private BarcodedProduct product;
	
	
	/** Ensures failures do not occur from scanner failing to scan item, thus isolating test cases */
	public void scanUntilAdded() {
		do {
			station.handheldScanner.scan(barcodedItem);
		} while (!session.cartLogic.getCart().containsKey(product));
	}
	
	@Before
	public void setUp() {
		PowerGrid.engageUninterruptiblePowerSource();
		PowerGrid.instance().forcePowerRestore();
		AbstractSelfCheckoutStation.resetConfigurationToDefaults();
		
		station = new SelfCheckoutStationGold();
		station.plugIn(PowerGrid.instance());
		station.turnOn();
		
		session = new CentralStationLogic(station);
		session.startSession();
		
		Barcode barcode = new Barcode(new Numeral[] {Numeral.one});
		barcodedItem = new BarcodedItem(barcode, Mass.ONE_GRAM);
		product = new BarcodedProduct(barcode, "item", 5, barcodedItem.getMass().inGrams().doubleValue());
		
		ProductDatabases.BARCODED_PRODUCT_DATABASE.clear();
		ProductDatabases.INVENTORY.clear();
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode, product);
		ProductDatabases.INVENTORY.put(product, 1);
	}
	
	@After 
	public void tearDown() {
		PowerGrid.engageFaultyPowerSource();
	}
	
	@Test 
	public void testSkipBaggingNotifiesAttendant() {
		AttendantLogicStub attendantLogic = new AttendantLogicStub(session);
		session.attendantLogic = attendantLogic;
		scanUntilAdded();
		session.weightLogic.skipBaggingRequest(barcodedItem.getBarcode());
		assertTrue(attendantLogic.requestApprovalCalled);
	}
	
	@Test
	public void testSkipBaggingBlocksStation() {
		scanUntilAdded();
		session.weightLogic.skipBaggingRequest(barcodedItem.getBarcode());
		assertTrue(this.session.stateLogic.inState(States.BLOCKED));
	}
	
	@Test (expected = InvalidArgumentSimulationException.class)
	public void testSkipBaggingNullBarcode() {
		session.weightLogic.skipBaggingRequest(null);
	}
	
	// Expected reaction not clear; we have therefore assumed unblocking when weight discrepancy removed is expected
	@Test 
	public void testSkipBaggingAddsAnyways() {
		scanUntilAdded();
		session.weightLogic.skipBaggingRequest(barcodedItem.getBarcode());
		station.baggingArea.addAnItem(barcodedItem);
		assertFalse(this.session.stateLogic.inState(States.BLOCKED));
	}
	
	@Test
	public void testAttendantApprovalReducesExceptedWeight() {
		scanUntilAdded();
		session.weightLogic.skipBaggingRequest(barcodedItem.getBarcode());
		session.attendantLogic.grantApprovalSkipBagging(barcodedItem.getBarcode());
		assertFalse(session.weightLogic.checkWeightDiscrepancy());
	}
	
	@Test 
	public void testAttendantApprovalUnblocksStation() {
		scanUntilAdded();
		session.weightLogic.skipBaggingRequest(barcodedItem.getBarcode());
		session.attendantLogic.grantApprovalSkipBagging(barcodedItem.getBarcode());
		assertFalse(this.session.stateLogic.inState(States.BLOCKED)); // Ensures no longer blocked
	}
	
	@Test 
	public void testAttendantApprovalStaysBlockedIfDiscrepancyRemains() {
		scanUntilAdded();
		session.weightLogic.skipBaggingRequest(barcodedItem.getBarcode());
		session.attendantLogic.grantApprovalSkipBagging(barcodedItem.getBarcode());
	}
	
	@Test (expected = InvalidArgumentSimulationException.class)
	public void testAttendantApprovalNullBarcode() {
		session.attendantLogic.grantApprovalSkipBagging(null);
	}

	
	private class AttendantLogicStub extends AttendantLogic {
		public boolean requestApprovalCalled = false;
		
		public AttendantLogicStub(CentralStationLogic l) {super(l);}
		@Override 
		public void requestApprovalSkipBagging(Barcode barcode) {
			requestApprovalCalled = true;
		}
		
		
	}
	

}
