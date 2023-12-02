package com.thelocalmarketplace.software.test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.thelocalmarketplace.hardware.*;
import com.thelocalmarketplace.software.Software;

import powerutility.PowerGrid;

public class MaintenanceTest {
	
	private SelfCheckoutStationBronze bronze_hardware;
	private SelfCheckoutStationGold gold_hardware;
	private SelfCheckoutStationSilver silver_hardware;
	
	private Software bronze_software;
	private Software gold_software;
	private Software silver_software;

	@Before
	public void setUp() throws Exception {
		bronze_hardware.resetConfigurationToDefaults();
		bronze_hardware = new SelfCheckoutStationBronze();
		gold_hardware.resetConfigurationToDefaults();
		gold_hardware = new SelfCheckoutStationGold();
		silver_hardware.resetConfigurationToDefaults();
		silver_hardware = new SelfCheckoutStationSilver();
		
		bronze_software = new Software(bronze_hardware);
		gold_software = new Software(gold_hardware);
		silver_software = new Software(silver_hardware);
		
		bronze_hardware.plugIn(PowerGrid.instance());
		bronze_hardware.turnOn();
		
		gold_hardware.plugIn(PowerGrid.instance());
		gold_hardware.turnOn();
		
		silver_hardware.plugIn(PowerGrid.instance());
		silver_hardware.turnOn();
		
		PowerGrid.engageUninterruptiblePowerSource();
		PowerGrid.instance().forcePowerRestore();

	}

	@Test
	public void testMaintenance() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetIssues() {
		fail("Not yet implemented");
	}

	@Test
	public void testCheckInk() {
		fail("Not yet implemented");
	}

	@Test
	public void testPredictLowInk() {
		fail("Not yet implemented");
	}

	@Test
	public void testResolveInkIssue() {
		fail("Not yet implemented");
	}

	@Test
	public void testPredictLowCoinsDispenser() {
		fail("Not yet implemented");
	}

	@Test
	public void testPredictCoinsFullDispenser() {
		fail("Not yet implemented");
	}

	@Test
	public void testPredictCoinsFullStorage() {
		fail("Not yet implemented");
	}

	@Test
	public void testAddCoinsInDispenser() {
		fail("Not yet implemented");
	}

	@Test
	public void testRemoveCoinsInDispenser() {
		fail("Not yet implemented");
	}

	@Test
	public void testRemoveAllCoinsInStorageUnit() {
		fail("Not yet implemented");
	}

	@Test
	public void testCheckPaper() {
		fail("Not yet implemented");
	}

	@Test
	public void testPredictLowPaper() {
		fail("Not yet implemented");
	}

	@Test
	public void testResolvePrinterPaperIssue() {
		fail("Not yet implemented");
	}

	@Test
	public void testThePrinterIsOutOfPaper() {
		fail("Not yet implemented");
	}

	@Test
	public void testThePrinterIsOutOfInk() {
		fail("Not yet implemented");
	}

	@Test
	public void testThePrinterHasLowInk() {
		fail("Not yet implemented");
	}

	@Test
	public void testThePrinterHasLowPaper() {
		fail("Not yet implemented");
	}

	@Test
	public void testPaperHasBeenAddedToThePrinter() {
		fail("Not yet implemented");
	}

	@Test
	public void testInkHasBeenAddedToThePrinter() {
		fail("Not yet implemented");
	}

	@Test
	public void testCoinsFullICoinDispenser() {
		fail("Not yet implemented");
	}

	@Test
	public void testCoinsEmpty() {
		fail("Not yet implemented");
	}

	@Test
	public void testCoinsFullCoinStorageUnit() {
		fail("Not yet implemented");
	}
	
	@Test
	public void testCheckBanknotes() {
		fail("Not yet implemented");
	}

	@Test
	public void testPredictLowBanknotes() {
		fail("Not yet implemented");
	}
	
	@Test
	public void testPredictBanknotesFull() {
		fail("Not yet implemented");
	}

	@Test
	public void testResolveBanknotesLow() {
		fail("Not yet implemented"); 
	}
	
	@Test
	public void testResolveBanknotesFull() {
		fail("Not yet implemented"); 
	}

}
