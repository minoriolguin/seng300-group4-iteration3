package com.thelocalmarketplace.software.test;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.jjjwelectronics.OverloadedDevice;
import com.tdc.CashOverloadException;
import com.tdc.DisabledException;
import com.tdc.banknote.Banknote;
import com.tdc.banknote.BanknoteStorageUnit;
import com.tdc.coin.ICoinDispenser;
import com.thelocalmarketplace.hardware.*;
import com.thelocalmarketplace.software.Software;

import ca.ucalgary.seng300.simulation.SimulationException;
import powerutility.PowerGrid;

public class MaintenanceTest {
	
	private SelfCheckoutStationBronze bronze_hardware;
	private SelfCheckoutStationGold gold_hardware;
	private SelfCheckoutStationSilver silver_hardware;
	
	private Software bronze_software;
	private Software gold_software;
	private Software silver_software;
	
	private Map<BigDecimal, ICoinDispenser> bronze_cDispensers;
	private Map<BigDecimal, ICoinDispenser> silver_cDispensers;
	private Map<BigDecimal, ICoinDispenser> gold_cDispensers;
	
	private BanknoteStorageUnit bronze_bStorageUnit;
	private BanknoteStorageUnit silver_bStorageUnit;
	private BanknoteStorageUnit gold_bStorageUnit;
	
	
    private ArrayList<BigDecimal> coindenominations;
    private ArrayList<Banknote> banknotes;
    private Currency CAD;
    private BigDecimal[] billDenominations;
    
   
    
    private static final Currency CAD_Currency = Currency.getInstance("CAD");
    private static final BigDecimal value_toonie = new BigDecimal("2.00");
    private static final BigDecimal value_loonie = new BigDecimal("1.00");
    private static final BigDecimal value_quarter = new BigDecimal("0.25");
    private static final BigDecimal value_dime = new BigDecimal("0.10");
    private static final BigDecimal value_nickel = new BigDecimal("0.05");
    private static final BigDecimal value_penny = new BigDecimal("0.01");

//    private Coin coin_toonie = new Coin(CAD_Currency,value_toonie);
//    private Coin coin_loonie = new Coin(CAD_Currency,value_loonie);
//    private Coin coin_quarter = new Coin(CAD_Currency,value_quarter);
//    private Coin coin_dime = new Coin(CAD_Currency,value_dime);
//    private Coin coin_nickel = new Coin(CAD_Currency,value_nickel);
//    private Coin coin_penny = new Coin(CAD_Currency,value_penny);

	@Before
	public void setUp() throws Exception {
        coindenominations = new ArrayList<BigDecimal>();
        CAD = Currency.getInstance("CAD");
        coindenominations.add(value_toonie);
        coindenominations.add(value_loonie);
        coindenominations.add(value_quarter);
        coindenominations.add(value_dime);
        coindenominations.add(value_nickel);
        coindenominations.add(value_penny);

        billDenominations = new BigDecimal[7];
        billDenominations[0] = new BigDecimal("5.00");
        billDenominations[1] = new BigDecimal("10.00");
        billDenominations[2] = new BigDecimal("20.00");
        billDenominations[3] = new BigDecimal("50.00");
        billDenominations[4] = new BigDecimal("100.00");
        billDenominations[5] = new BigDecimal("100.00");
        billDenominations[6] = new BigDecimal("100.00");
        
        banknotes = new ArrayList<Banknote>();

        Currency c = Currency.getInstance("CAD");
        BigDecimal[] billDenom = { new BigDecimal("5.00"),
                new BigDecimal("10.00"),
                new BigDecimal("20.00"),
                new BigDecimal("50.00"),
                new BigDecimal("100.00")};
        BigDecimal[] coinDenom = { new BigDecimal("0.01"),
                new BigDecimal("0.05"),
                new BigDecimal("0.1"),
                new BigDecimal("0.25"),
                new BigDecimal("1"),
                new BigDecimal("2") };
        
        AbstractSelfCheckoutStation.resetConfigurationToDefaults();
        AbstractSelfCheckoutStation.configureCurrency(c);
        AbstractSelfCheckoutStation.configureBanknoteDenominations(billDenom);
        AbstractSelfCheckoutStation.configureCoinDenominations(coinDenom);
		
		bronze_hardware = new SelfCheckoutStationBronze();
		gold_hardware = new SelfCheckoutStationGold();
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
		
		
		bronze_software.maintenance.resolveInkIssue((int)(bronze_software.maintenance.MAXIMUM_INK * 0.5));
		bronze_software.maintenance.resolvePrinterPaperIssue(bronze_software.maintenance.MAXIMUM_PAPER);
		
		silver_software.maintenance.resolveInkIssue((int)(silver_software.maintenance.MAXIMUM_INK * 0.5));
		silver_software.maintenance.resolvePrinterPaperIssue(silver_software.maintenance.MAXIMUM_PAPER);
		
		gold_software.maintenance.resolveInkIssue((int)(gold_software.maintenance.MAXIMUM_INK * 0.5));
		gold_software.maintenance.resolvePrinterPaperIssue(silver_software.maintenance.MAXIMUM_PAPER);
		
		bronze_cDispensers = bronze_software.coinDispensers;
		silver_cDispensers = silver_software.coinDispensers;
		gold_cDispensers = gold_software.coinDispensers;
		
		bronze_bStorageUnit = bronze_software.banknoteStorageUnit;
		silver_bStorageUnit = silver_software.banknoteStorageUnit;
		gold_bStorageUnit = gold_software.banknoteStorageUnit;
		
//		for (BigDecimal cd : coindenominations) {
//			bronze_software.maintenance.addCoinsInDispenser(bronze_cDispensers.get(cd),cd,10);
//			silver_software.maintenance.addCoinsInDispenser(silver_cDispensers.get(cd),cd,10);
//			gold_software.maintenance.addCoinsInDispenser(gold_cDispensers.get(cd),cd,10);
//		}
		
		for(BigDecimal bd:billDenominations) {
			banknotes.add(new Banknote(CAD,bd));		
		}
		
		bronze_software.maintenance.checkBanknotes(5, bronze_bStorageUnit);
		silver_software.maintenance.checkBanknotes(5, bronze_bStorageUnit);
		gold_software.maintenance.checkBanknotes(5, bronze_bStorageUnit);	}
		
		


	@Test
	public void testMaintenance() {
//		if (silver_software.printer instanceof ReceiptPrinterSilver) {
//		fail("ink: "+gold_software.printer.inkRemaining());
//		} else {
//			assertTrue(true);
//		}
		
//		System.out.println(silver_software.maintenance.getIssues());
	}

	@Test
	public void testGetIssues() {
		fail("Not yet implemented");
	}
	
	@Test
	public void testCheckInkRemainingInk() {
		assertEquals(silver_software.maintenance.getInkRemaining(),silver_software.printer.inkRemaining());
		assertEquals(gold_software.maintenance.getInkRemaining(),gold_software.printer.inkRemaining());
		
		// Check if estimated bronze ink level is as expected
		assertEquals(bronze_software.maintenance.getInkRemaining(),bronze_software.maintenance.MAXIMUM_INK);
	}

	@Test
	public void testCheckInkNoIssue() {
		assertFalse(bronze_software.maintenance.getIssues().contains("PRINTER_LOW_INK"));
		assertFalse(bronze_software.maintenance.getIssues().contains("PRINTER_LOW_INK_SOON"));
		assertFalse(bronze_software.maintenance.getIssues().contains("PRINTER_OUT_OF_INK"));
		
		assertFalse(silver_software.maintenance.getIssues().contains("PRINTER_LOW_INK"));
		assertFalse(silver_software.maintenance.getIssues().contains("PRINTER_LOW_INK_SOON"));
		assertFalse(silver_software.maintenance.getIssues().contains("PRINTER_OUT_OF_INK"));
		
		assertFalse(gold_software.maintenance.getIssues().contains("PRINTER_LOW_INK"));
		assertFalse(gold_software.maintenance.getIssues().contains("PRINTER_LOW_INK_SOON"));
		assertFalse(gold_software.maintenance.getIssues().contains("PRINTER_OUT_OF_INK"));
		
		assertFalse(bronze_software.isCustomerStationBlocked());
		assertFalse(silver_software.isCustomerStationBlocked());
		assertFalse(gold_software.isCustomerStationBlocked());
	}
	
	@Test
	public void testCheckInkNoInk() {
		bronze_software.maintenance.setInkRemaining(0);
		silver_software.maintenance.setInkRemaining(0);
		gold_software.maintenance.setInkRemaining(0);
		
		bronze_software.maintenance.checkInk(0);
		silver_software.maintenance.checkInk(0);
		gold_software.maintenance.checkInk(0);
		
		assertFalse(bronze_software.maintenance.getIssues().contains("PRINTER_LOW_INK"));
		assertFalse(bronze_software.maintenance.getIssues().contains("PRINTER_LOW_INK_SOON"));
		assertTrue(bronze_software.maintenance.getIssues().contains("PRINTER_OUT_OF_INK"));
		
		assertFalse(silver_software.maintenance.getIssues().contains("PRINTER_LOW_INK"));
		assertFalse(silver_software.maintenance.getIssues().contains("PRINTER_LOW_INK_SOON"));
		assertTrue(silver_software.maintenance.getIssues().contains("PRINTER_OUT_OF_INK"));
		
		assertFalse(gold_software.maintenance.getIssues().contains("PRINTER_LOW_INK"));
		assertFalse(gold_software.maintenance.getIssues().contains("PRINTER_LOW_INK_SOON"));
		assertTrue(gold_software.maintenance.getIssues().contains("PRINTER_OUT_OF_INK"));
		
		assertTrue(bronze_software.isCustomerStationBlocked());
		assertTrue(silver_software.isCustomerStationBlocked());
		assertTrue(gold_software.isCustomerStationBlocked());
	}
	
	@Test
	public void testCheckInkLowInk() {
		bronze_software.maintenance.setInkRemaining(bronze_software.maintenance.lowInkLevel);
		silver_software.maintenance.setInkRemaining(silver_software.maintenance.lowInkLevel);
		gold_software.maintenance.setInkRemaining(gold_software.maintenance.lowInkLevel);
		
		bronze_software.maintenance.checkInk(0);
		silver_software.maintenance.checkInk(0);
		gold_software.maintenance.checkInk(0);
		
		assertTrue(bronze_software.maintenance.getIssues().contains("PRINTER_LOW_INK"));
		assertFalse(bronze_software.maintenance.getIssues().contains("PRINTER_LOW_INK_SOON"));
		assertFalse(bronze_software.maintenance.getIssues().contains("PRINTER_OUT_OF_INK"));
		
		assertTrue(silver_software.maintenance.getIssues().contains("PRINTER_LOW_INK"));
		assertFalse(silver_software.maintenance.getIssues().contains("PRINTER_LOW_INK_SOON"));
		assertFalse(silver_software.maintenance.getIssues().contains("PRINTER_OUT_OF_INK"));
		
		assertTrue(gold_software.maintenance.getIssues().contains("PRINTER_LOW_INK"));
		assertFalse(gold_software.maintenance.getIssues().contains("PRINTER_LOW_INK_SOON"));
		assertFalse(gold_software.maintenance.getIssues().contains("PRINTER_OUT_OF_INK"));
		
		assertTrue(bronze_software.isCustomerStationBlocked());
		assertTrue(silver_software.isCustomerStationBlocked());
		assertTrue(gold_software.isCustomerStationBlocked());
	}
	
	@Test
	public void testCheckInkLowInkSoon() {
		// Variables for readability
		int expected_next_chars_printed = 130;
		int bronze_ink_level = bronze_software.maintenance.lowInkLevel + expected_next_chars_printed;
		int silver_ink_level = silver_software.maintenance.lowInkLevel + expected_next_chars_printed;
		int gold_ink_level = gold_software.maintenance.lowInkLevel + expected_next_chars_printed;
		
		bronze_software.maintenance.setInkRemaining(bronze_ink_level);
		silver_software.maintenance.setInkRemaining(silver_ink_level);
		gold_software.maintenance.setInkRemaining(gold_ink_level);
		
		bronze_software.maintenance.checkInk(expected_next_chars_printed);
		silver_software.maintenance.checkInk(expected_next_chars_printed);
		gold_software.maintenance.checkInk(expected_next_chars_printed);
		
		assertFalse(bronze_software.maintenance.getIssues().contains("PRINTER_LOW_INK"));
		assertTrue(bronze_software.maintenance.getIssues().contains("PRINTER_LOW_INK_SOON"));
		assertFalse(bronze_software.maintenance.getIssues().contains("PRINTER_OUT_OF_INK"));
		
		assertFalse(silver_software.maintenance.getIssues().contains("PRINTER_LOW_INK"));
		assertTrue(silver_software.maintenance.getIssues().contains("PRINTER_LOW_INK_SOON"));
		assertFalse(silver_software.maintenance.getIssues().contains("PRINTER_OUT_OF_INK"));
		
		assertFalse(gold_software.maintenance.getIssues().contains("PRINTER_LOW_INK"));
		assertTrue(gold_software.maintenance.getIssues().contains("PRINTER_LOW_INK_SOON"));
		assertFalse(gold_software.maintenance.getIssues().contains("PRINTER_OUT_OF_INK"));
		
		assertTrue(bronze_software.isCustomerStationBlocked());
		assertTrue(silver_software.isCustomerStationBlocked());
		assertTrue(gold_software.isCustomerStationBlocked());
	}

	@Test
	public void testPredictLowInkNoIssue() {
		// Variables for readability
		int expected_next_chars_printed = 130;
		int bronze_ink_level = (bronze_software.maintenance.lowInkLevel*2)+expected_next_chars_printed;
		int silver_ink_level = (silver_software.maintenance.lowInkLevel*2)+expected_next_chars_printed;
		int gold_ink_level = (gold_software.maintenance.lowInkLevel*2)+expected_next_chars_printed;
		
		bronze_software.maintenance.setInkRemaining(bronze_ink_level);
		silver_software.maintenance.setInkRemaining(silver_ink_level);
		gold_software.maintenance.setInkRemaining(gold_ink_level);
		
		bronze_software.maintenance.setAverageInkUsagePerSession(expected_next_chars_printed);
		silver_software.maintenance.setAverageInkUsagePerSession(expected_next_chars_printed);
		gold_software.maintenance.setAverageInkUsagePerSession(expected_next_chars_printed);
		
		bronze_software.maintenance.predictLowInk();
		silver_software.maintenance.predictLowInk();
		gold_software.maintenance.predictLowInk();
		
		assertFalse(bronze_software.maintenance.getIssues().contains("PRINTER_LOW_INK_SOON"));
		assertFalse(silver_software.maintenance.getIssues().contains("PRINTER_LOW_INK_SOON"));
		assertFalse(gold_software.maintenance.getIssues().contains("PRINTER_LOW_INK_SOON"));
		
		assertFalse(bronze_software.isCustomerStationBlocked());
		assertFalse(silver_software.isCustomerStationBlocked());
		assertFalse(gold_software.isCustomerStationBlocked());
	}
	
	@Test
	public void testPredictLowInkWhenLowInkPredictedSoon() {
		// Variables for readability
		int expected_next_chars_printed = 130;
		int bronze_ink_level = bronze_software.maintenance.lowInkLevel + expected_next_chars_printed;
		int silver_ink_level = silver_software.maintenance.lowInkLevel + expected_next_chars_printed;
		int gold_ink_level = gold_software.maintenance.lowInkLevel + expected_next_chars_printed;
		
		bronze_software.maintenance.setInkRemaining(bronze_ink_level);
		silver_software.maintenance.setInkRemaining(silver_ink_level);
		gold_software.maintenance.setInkRemaining(gold_ink_level);
		
		bronze_software.maintenance.setAverageInkUsagePerSession(expected_next_chars_printed);
		silver_software.maintenance.setAverageInkUsagePerSession(expected_next_chars_printed);
		gold_software.maintenance.setAverageInkUsagePerSession(expected_next_chars_printed);
		
		bronze_software.maintenance.predictLowInk();
		silver_software.maintenance.predictLowInk();
		gold_software.maintenance.predictLowInk();
		
		assertFalse(bronze_software.maintenance.getIssues().contains("PRINTER_LOW_INK"));
		assertTrue(bronze_software.maintenance.getIssues().contains("PRINTER_LOW_INK_SOON"));
		assertFalse(bronze_software.maintenance.getIssues().contains("PRINTER_OUT_OF_INK"));
		
		assertFalse(silver_software.maintenance.getIssues().contains("PRINTER_LOW_INK"));
		assertTrue(silver_software.maintenance.getIssues().contains("PRINTER_LOW_INK_SOON"));
		assertFalse(silver_software.maintenance.getIssues().contains("PRINTER_OUT_OF_INK"));
		
		assertFalse(gold_software.maintenance.getIssues().contains("PRINTER_LOW_INK"));
		assertTrue(gold_software.maintenance.getIssues().contains("PRINTER_LOW_INK_SOON"));
		assertFalse(gold_software.maintenance.getIssues().contains("PRINTER_OUT_OF_INK"));
		
		assertTrue(bronze_software.isCustomerStationBlocked());
		assertTrue(silver_software.isCustomerStationBlocked());
		assertTrue(gold_software.isCustomerStationBlocked());
	}

	// NEED TO UPDATE THIS 
	@Test
	public void testResolveInkIssue() throws OverloadedDevice {
		bronze_software.maintenance.setInkRemaining(0);
		silver_software.maintenance.setInkRemaining(0);
		gold_software.maintenance.setInkRemaining(0);
		
		bronze_software.maintenance.resolveInkIssue((int)(bronze_software.maintenance.MAXIMUM_INK * 0.5));
		silver_software.maintenance.resolveInkIssue((int)(silver_software.maintenance.MAXIMUM_INK * 0.5));
		gold_software.maintenance.resolveInkIssue((int)(gold_software.maintenance.MAXIMUM_INK * 0.5));
		
		assertEquals(silver_software.maintenance.getInkRemaining(),silver_software.printer.inkRemaining());
		assertEquals(gold_software.maintenance.getInkRemaining(),gold_software.printer.inkRemaining());
		
		// Check if estimated bronze ink level is as expected
		assertEquals(bronze_software.maintenance.getInkRemaining(),bronze_software.maintenance.MAXIMUM_INK);
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
		bronze_software.maintenance.setInkRemaining(0);
		silver_software.maintenance.setInkRemaining(0);
		gold_software.maintenance.setInkRemaining(0);
		
		bronze_software.maintenance.checkInk(0);
		silver_software.maintenance.checkInk(0);
		gold_software.maintenance.checkInk(0);
		
		assertTrue(bronze_software.maintenance.getIssues().contains("PRINTER_OUT_OF_INK"));
		assertTrue(silver_software.maintenance.getIssues().contains("PRINTER_OUT_OF_INK"));
		assertTrue(gold_software.maintenance.getIssues().contains("PRINTER_OUT_OF_INK"));
		
		assertTrue(bronze_software.isCustomerStationBlocked());
		assertTrue(silver_software.isCustomerStationBlocked());
		assertTrue(gold_software.isCustomerStationBlocked());
	}

	@Test
	public void testThePrinterHasLowInk() {
		bronze_software.maintenance.setInkRemaining(bronze_software.maintenance.lowInkLevel);
		silver_software.maintenance.setInkRemaining(silver_software.maintenance.lowInkLevel);
		gold_software.maintenance.setInkRemaining(gold_software.maintenance.lowInkLevel);
		
		bronze_software.maintenance.checkInk(0);
		silver_software.maintenance.checkInk(0);
		gold_software.maintenance.checkInk(0);
		
		assertTrue(bronze_software.maintenance.getIssues().contains("PRINTER_LOW_INK"));
		assertTrue(silver_software.maintenance.getIssues().contains("PRINTER_LOW_INK"));
		assertTrue(gold_software.maintenance.getIssues().contains("PRINTER_LOW_INK"));
		
		assertTrue(bronze_software.isCustomerStationBlocked());
		assertTrue(silver_software.isCustomerStationBlocked());
		assertTrue(gold_software.isCustomerStationBlocked());
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
	public void testOutOfBanknotes() {
		bronze_software.maintenance.checkBanknotes(5, bronze_bStorageUnit);
		silver_software.maintenance.checkBanknotes(5, silver_bStorageUnit);
		gold_software.maintenance.checkBanknotes(5, gold_bStorageUnit);
		
		assertTrue(bronze_software.maintenance.getIssues().contains("OUT_OF_BANKNOTES"));
		assertTrue(silver_software.maintenance.getIssues().contains("OUT_OF_BANKNOTES"));
		assertTrue(gold_software.maintenance.getIssues().contains("OUT_OF_BANKNOTES"));
		
		assertTrue(bronze_software.isCustomerStationBlocked());
		assertTrue(silver_software.isCustomerStationBlocked());
		assertTrue(gold_software.isCustomerStationBlocked());
	}
	
	@Test
	public void testLowBanknotes() throws SimulationException, CashOverloadException {
		
		bronze_software.maintenance.setCurrentBanknotes(5);
		silver_software.maintenance.setCurrentBanknotes(5);
		gold_software.maintenance.setCurrentBanknotes(5);
		
		bronze_software.maintenance.checkBanknotes(5, bronze_bStorageUnit);
		silver_software.maintenance.checkBanknotes(5, silver_bStorageUnit);
		gold_software.maintenance.checkBanknotes(5, gold_bStorageUnit);
		
		assertTrue(bronze_software.maintenance.getIssues().contains("LOW_BANKNOTES"));
		assertTrue(silver_software.maintenance.getIssues().contains("LOW_BANKNOTES"));
		assertTrue(gold_software.maintenance.getIssues().contains("LOW_BANKNOTES"));
		
		assertTrue(bronze_software.isCustomerStationBlocked());
		assertTrue(silver_software.isCustomerStationBlocked());
		assertTrue(gold_software.isCustomerStationBlocked());
	}
	
	@Test
	public void testBanknotesFull() throws SimulationException, CashOverloadException {
		
		bronze_software.maintenance.setCurrentBanknotes(1000);
		silver_software.maintenance.setCurrentBanknotes(1000);
		gold_software.maintenance.setCurrentBanknotes(1000);
		
		bronze_software.maintenance.checkBanknotes(5, bronze_bStorageUnit);
		silver_software.maintenance.checkBanknotes(5, silver_bStorageUnit);
		gold_software.maintenance.checkBanknotes(5, gold_bStorageUnit);
		
		assertTrue(bronze_software.maintenance.getIssues().contains("BANKNOTES_FULL"));
		assertTrue(silver_software.maintenance.getIssues().contains("BANKNOTES_FULL"));
		assertTrue(gold_software.maintenance.getIssues().contains("BANKNOTES_FULL"));
		
		assertTrue(bronze_software.isCustomerStationBlocked());
		assertTrue(silver_software.isCustomerStationBlocked());
		assertTrue(gold_software.isCustomerStationBlocked());
	}

	@Test
	public void testPredictLowBanknotes() {
		bronze_software.maintenance.setCurrentBanknotes(5);
		silver_software.maintenance.setCurrentBanknotes(5);
		gold_software.maintenance.setCurrentBanknotes(5);
		
		bronze_software.maintenance.predictLowBanknotes(bronze_bStorageUnit);
		silver_software.maintenance.predictLowBanknotes(silver_bStorageUnit);
		gold_software.maintenance.predictLowBanknotes(gold_bStorageUnit);
		
		assertTrue(bronze_software.maintenance.getIssues().contains("LOW_BANKNOTES_SOON"));
		assertTrue(silver_software.maintenance.getIssues().contains("LOW_BANKNOTES_SOON"));
		assertTrue(gold_software.maintenance.getIssues().contains("LOW_BANKNOTES_SOON"));
		
		assertTrue(bronze_software.isCustomerStationBlocked());
		assertTrue(silver_software.isCustomerStationBlocked());
		assertTrue(gold_software.isCustomerStationBlocked());
	}
	
	@Test
	public void testPredictBanknotesFull() {
		bronze_software.maintenance.setCurrentBanknotes(750);
		silver_software.maintenance.setCurrentBanknotes(750);
		gold_software.maintenance.setCurrentBanknotes(750);
		
		bronze_software.maintenance.predictBanknotesFull(bronze_bStorageUnit);
		silver_software.maintenance.predictBanknotesFull(silver_bStorageUnit);
		gold_software.maintenance.predictBanknotesFull(gold_bStorageUnit);
		
		assertTrue(bronze_software.maintenance.getIssues().contains("BANKNOTES_ALMOST_FULL"));
		assertTrue(silver_software.maintenance.getIssues().contains("BANKNOTES_ALMOST_FULL"));
		assertTrue(gold_software.maintenance.getIssues().contains("BANKNOTES_ALMOST_FULL"));
		
		assertTrue(bronze_software.isCustomerStationBlocked());
		assertTrue(silver_software.isCustomerStationBlocked());
		assertTrue(gold_software.isCustomerStationBlocked());
	}

	@Test
	public void testResolveBanknotesLow() throws CashOverloadException, DisabledException {
		
		bronze_software.maintenance.setCurrentBanknotes(0);
		silver_software.maintenance.setCurrentBanknotes(0);
		gold_software.maintenance.setCurrentBanknotes(0);
		
		bronze_software.maintenance.setMAXIMUM_BANKNOTES(12);
		silver_software.maintenance.setMAXIMUM_BANKNOTES(12);
		gold_software.maintenance.setMAXIMUM_BANKNOTES(12);
	
		bronze_software.maintenance.resolveBanknotesLow(bronze_bStorageUnit, banknotes);
		silver_software.maintenance.resolveBanknotesLow(silver_bStorageUnit, banknotes);
		gold_software.maintenance.resolveBanknotesLow(gold_bStorageUnit, banknotes);
		
		assertFalse(bronze_software.maintenance.getIssues().contains("LOW_BANKNOTES"));
		assertFalse(silver_software.maintenance.getIssues().contains("LOW_BANKNOTES"));
		assertFalse(gold_software.maintenance.getIssues().contains("LOW_BANKNOTES"));
		
		assertFalse(bronze_software.maintenance.getIssues().contains("LOW_BANKNOTES_SOON"));
		assertFalse(silver_software.maintenance.getIssues().contains("LOW_BANKNOTES_SOON"));
		assertFalse(gold_software.maintenance.getIssues().contains("LOW_BANKNOTES_SOON"));
		
		assertFalse(bronze_software.isCustomerStationBlocked());
		assertFalse(silver_software.isCustomerStationBlocked());
		assertFalse(gold_software.isCustomerStationBlocked());
	}
	
	@Test
	public void testResolveBanknotesFull() throws SimulationException, CashOverloadException, DisabledException {
		
		for(BigDecimal bd:billDenominations) {
			bronze_bStorageUnit.load(new Banknote(CAD,bd));
			silver_bStorageUnit.load(new Banknote(CAD,bd));	
			gold_bStorageUnit.load(new Banknote(CAD,bd));	
		}
		for(BigDecimal bd:billDenominations) {
			bronze_bStorageUnit.load(new Banknote(CAD,bd));
			silver_bStorageUnit.load(new Banknote(CAD,bd));	
			gold_bStorageUnit.load(new Banknote(CAD,bd));	
		}
		for(BigDecimal bd:billDenominations) {
			bronze_bStorageUnit.load(new Banknote(CAD,bd));
			silver_bStorageUnit.load(new Banknote(CAD,bd));	
			gold_bStorageUnit.load(new Banknote(CAD,bd));	
		}
		
		bronze_software.maintenance.setMAXIMUM_BANKNOTES(16);
		silver_software.maintenance.setMAXIMUM_BANKNOTES(16);
		gold_software.maintenance.setMAXIMUM_BANKNOTES(16);
		
		
		bronze_software.maintenance.resolveBanknotesFull(silver_bStorageUnit);
		silver_software.maintenance.resolveBanknotesFull(silver_bStorageUnit);
		gold_software.maintenance.resolveBanknotesFull(gold_bStorageUnit);
		
		assertFalse(bronze_software.maintenance.getIssues().contains("BANKNOTES_FULL"));
		assertFalse(silver_software.maintenance.getIssues().contains("BANKNOTES_FULL"));
		assertFalse(gold_software.maintenance.getIssues().contains("BANKNOTES_FULL"));
		
		assertFalse(bronze_software.maintenance.getIssues().contains("BANKNOTES_ALMOST_FULL"));
		assertFalse(silver_software.maintenance.getIssues().contains("BANKNOTES_ALMOST_FULL"));
		assertFalse(gold_software.maintenance.getIssues().contains("BANKNOTES_ALMOST_FULL"));
		
		assertFalse(bronze_software.isCustomerStationBlocked());
		assertFalse(silver_software.isCustomerStationBlocked());
		assertFalse(gold_software.isCustomerStationBlocked());
	}

}
