package com.thelocalmarketplace.software.test;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.jjjwelectronics.OverloadedDevice;
import com.tdc.banknote.Banknote;
import com.tdc.coin.Coin;
import com.tdc.coin.ICoinDispenser;
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
	
	private Map<BigDecimal, ICoinDispenser> bronze_cDispensers;
	private Map<BigDecimal, ICoinDispenser> silver_cDispensers;
	private Map<BigDecimal, ICoinDispenser> gold_cDispensers;
	
	
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
    
    private final String outOfPaperMsg = "PRINTER_OUT_OF_PAPER";
    private final String lowPaperMsg = "PRINTER_LOW_PAPER";
    private final String lowPaperSoonMsg = "PRINTER_LOW_PAPER_SOON";

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

        billDenominations = new BigDecimal[5];
        billDenominations[0] = new BigDecimal("5.00");
        billDenominations[1] = new BigDecimal("10.00");
        billDenominations[2] = new BigDecimal("20.00");
        billDenominations[3] = new BigDecimal("50.00");
        billDenominations[4] = new BigDecimal("100.00");

        Currency c = Currency.getInstance("CAD");
        BigDecimal[] billDenom = { new BigDecimal("5.00"),
                new BigDecimal("10.00"),
                new BigDecimal("20.00"),
                new BigDecimal("50.00"),
                new BigDecimal("100.00")};
        
        AbstractSelfCheckoutStation.resetConfigurationToDefaults();
        AbstractSelfCheckoutStation.configureCurrency(c);
        AbstractSelfCheckoutStation.configureBanknoteDenominations(billDenom);
        AbstractSelfCheckoutStation.configureCoinDenominations(coindenominations.toArray(
                new BigDecimal[coindenominations.size()]));
        Coin.DEFAULT_CURRENCY = CAD;
		
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
		bronze_software.maintenance.resolvePrinterPaperIssue((int)(bronze_software.maintenance.MAXIMUM_PAPER * 0.5));
		
		silver_software.maintenance.resolveInkIssue((int)(silver_software.maintenance.MAXIMUM_INK * 0.5));
		silver_software.maintenance.resolvePrinterPaperIssue((int)(bronze_software.maintenance.MAXIMUM_PAPER * 0.5));
		
		gold_software.maintenance.resolveInkIssue((int)(gold_software.maintenance.MAXIMUM_INK * 0.5));
		gold_software.maintenance.resolvePrinterPaperIssue((int)(bronze_software.maintenance.MAXIMUM_PAPER * 0.5));
		
		bronze_cDispensers = bronze_software.getCoinDispensers();
		silver_cDispensers = silver_software.getCoinDispensers();
		gold_cDispensers = gold_software.getCoinDispensers();
		
		for (BigDecimal cd : coindenominations) {
			bronze_software.maintenance.addCoinsInDispenser(bronze_cDispensers.get(cd),cd,10);
			silver_software.maintenance.addCoinsInDispenser(silver_cDispensers.get(cd),cd,10);
			gold_software.maintenance.addCoinsInDispenser(gold_cDispensers.get(cd),cd,10);
		}
		
//		for (BigDecimal bd : billDenominations) {
//			banknotes.add(new Banknote(CAD,bd));		
//		}
		
//		for (Banknote b : banknotes) {
//			bronze_software.maintenance.resolveBanknotesIssues(b);
//			silver_software.maintenance.resolveBanknotesIssues(b);
//			gold_software.maintenance.resolveBanknotesIssues(b);	
//		}
	}


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
	public void testCheckPaperBronzeStation() {
		bronze_software.maintenance.checkPaper(100);
	}
	
	@Test
	public void testCheckPaperPaperRemainingIsZeroSilverStation() {
		silver_software.maintenance.setPaperRemaining(0);
		silver_software.maintenance.checkPaper(100);
		boolean flag = silver_software.maintenance.getIssues().contains(this.outOfPaperMsg) && (silver_software.maintenance.getIssues().size() == 1);
		assertTrue(flag);
	}
	
	@Test
	public void testCheckPaperPaperRemainingIsZeroGoldStation() {
		gold_software.maintenance.setPaperRemaining(0);
		gold_software.maintenance.checkPaper(100);
		boolean flag = gold_software.maintenance.getIssues().contains(this.outOfPaperMsg) && (gold_software.maintenance.getIssues().size() == 1);
		assertTrue(flag);
	}
	
	@Test
	public void testCheckPaperPaperIsLowSilverStation() {
		silver_software.maintenance.setPaperRemaining(1);
		silver_software.maintenance.checkPaper(100);
		boolean flag = silver_software.maintenance.getIssues().contains(this.lowPaperMsg) && (silver_software.maintenance.getIssues().size() == 1);
		assertTrue(flag);
	}
	
	@Test
	public void testCheckPaperPaperIsLowGoldStation() {
		gold_software.maintenance.setPaperRemaining(1);
		gold_software.maintenance.checkPaper(100);
		boolean flag = gold_software.maintenance.getIssues().contains(this.lowPaperMsg) && (gold_software.maintenance.getIssues().size() == 1);
		assertTrue(flag);
	}
	
	@Test
	public void testCheckPaperStationIsBlockedSilverStation() {
		silver_software.maintenance.setPaperRemaining(1);
		silver_software.maintenance.checkPaper(100);
		boolean flag = silver_software.isCustomerStationBlocked();
		assertTrue(flag);
	}
	
	@Test
	public void testCheckPaperStationIsBlockedGoldStation() {
		gold_software.maintenance.setPaperRemaining(1);
		gold_software.maintenance.checkPaper(100);
		boolean flag = gold_software.isCustomerStationBlocked();
		assertTrue(flag);
	}
	
	@Test
	public void testCheckPaperNoIssuesBronzeStation() {
		bronze_software.maintenance.setPaperRemaining(1000);
		bronze_software.maintenance.checkPaper(50);
		boolean flag = (bronze_software.maintenance.getIssues().size() == 0) && (!bronze_software.isCustomerStationBlocked());
		assertTrue(flag);
	}
	
	@Test
	public void testCheckPaperNoIssuesSilverStation() {
		silver_software.maintenance.setPaperRemaining(1000);
		silver_software.maintenance.checkPaper(50);
		boolean flag = (silver_software.maintenance.getIssues().size() == 0) && (!silver_software.isCustomerStationBlocked());
		assertTrue(flag);
	}
	
	@Test
	public void testCheckPaperNoIssuesGoldStation() {
		gold_software.maintenance.setPaperRemaining(1000);
		gold_software.maintenance.checkPaper(50);
		boolean flag = (gold_software.maintenance.getIssues().size() == 0) && (!gold_software.isCustomerStationBlocked());
		assertTrue(flag);
	}
	
	@Test
	public void testCheckPaperLowPaperSoonBronzeStation() {
		bronze_software.maintenance.setPaperRemaining(1000);
		bronze_software.maintenance.checkPaper(5000);
		boolean flag = bronze_software.maintenance.getIssues().contains(this.lowPaperSoonMsg) && (bronze_software.isCustomerStationBlocked());
		assertTrue(flag);
	}
	
	@Test
	public void testCheckPaperLowPaperSoonSilverStation() {
		silver_software.maintenance.setPaperRemaining(1000);
		silver_software.maintenance.checkPaper(5000);
		boolean flag = silver_software.maintenance.getIssues().contains(this.lowPaperSoonMsg) && (silver_software.isCustomerStationBlocked());
		assertTrue(flag);
	}
	
	@Test
	public void testCheckPaperLowPaperSoonGoldStation() {
		gold_software.maintenance.setPaperRemaining(1000);
		gold_software.maintenance.checkPaper(5000);
		boolean flag = gold_software.maintenance.getIssues().contains(this.lowPaperSoonMsg) && (gold_software.isCustomerStationBlocked());
		assertTrue(flag);
	}

	@Test
	public void testPredictLowPaperLevelBronzeStation() {
		bronze_software.maintenance.setPaperRemaining(1000);
		bronze_software.maintenance.setAveragePaperUsagePerSession(5000);
		bronze_software.maintenance.predictLowPaper();
		boolean flag = bronze_software.maintenance.getIssues().contains(this.lowPaperSoonMsg) && (bronze_software.isCustomerStationBlocked());
		assertTrue(flag);
	}
	
	@Test
	public void testPredictLowPaperLevelSilverStation() {
		silver_software.maintenance.setPaperRemaining(1000);
		silver_software.maintenance.setAveragePaperUsagePerSession(5000);
		silver_software.maintenance.predictLowPaper();
		boolean flag = silver_software.maintenance.getIssues().contains(this.lowPaperSoonMsg) && (silver_software.isCustomerStationBlocked());
		assertTrue(flag);
	}
	
	@Test
	public void testPredictLowPaperLevelGoldStation() {
		gold_software.maintenance.setPaperRemaining(1000);
		gold_software.maintenance.setAveragePaperUsagePerSession(5000);
		gold_software.maintenance.predictLowPaper();
		boolean flag = gold_software.maintenance.getIssues().contains(this.lowPaperSoonMsg) && (gold_software.isCustomerStationBlocked());
		assertTrue(flag);
	}
	
	@Test
	public void testPredictLowPaperLevelNoIssuesBronzeStation() {
		bronze_software.maintenance.setPaperRemaining(1000);
		bronze_software.maintenance.setAveragePaperUsagePerSession(50);
		bronze_software.maintenance.predictLowPaper();
		boolean flag = bronze_software.maintenance.getIssues().size() == 0;
		assertTrue(flag);
	}
	
	@Test
	public void testPredictLowPaperLevelNoIssuesSilverStation() {
		silver_software.maintenance.setPaperRemaining(1000);
		silver_software.maintenance.setAveragePaperUsagePerSession(50);
		silver_software.maintenance.predictLowPaper();
		boolean flag = silver_software.maintenance.getIssues().size() == 0;
		assertTrue(flag);
	}
	
	@Test
	public void testPredictLowPaperLevelNoIssuesGoldStation() {
		gold_software.maintenance.setPaperRemaining(1000);
		gold_software.maintenance.setAveragePaperUsagePerSession(50);
		gold_software.maintenance.predictLowPaper();
		boolean flag = gold_software.maintenance.getIssues().size() == 0;
		assertTrue(flag);
	}

	@Test(expected = RuntimeException.class)
	public void testResolvePrinterPaperIssueOverloadBronzeStation() throws OverloadedDevice {
		bronze_software.maintenance.resolvePrinterPaperIssue(10000);
	}
	
	@Test(expected = RuntimeException.class)
	public void testResolvePrinterPaperIssueOverloadSilverStation() throws OverloadedDevice {
		silver_software.maintenance.resolvePrinterPaperIssue(10000);
	}
	
	@Test(expected = RuntimeException.class)
	public void testResolvePrinterPaperIssueOverloadGoldStation() throws OverloadedDevice {
		gold_software.maintenance.resolvePrinterPaperIssue(10000);
	}
	
	@Test
	public void testResolvePrinterPaperIssueSilverStation() throws OverloadedDevice {
		silver_software.maintenance.resolvePrinterPaperIssue((int)(silver_software.maintenance.MAXIMUM_PAPER * 0.2));
		assertEquals((int)(silver_software.maintenance.MAXIMUM_PAPER * 0.7), silver_software.maintenance.getPaperRemaining());
	}
	
	@Test
	public void testResolvePrinterPaperIssueGoldStation() throws OverloadedDevice {
		gold_software.maintenance.resolvePrinterPaperIssue((int)(gold_software.maintenance.MAXIMUM_PAPER * 0.2));
		assertEquals((int)(gold_software.maintenance.MAXIMUM_PAPER * 0.7), gold_software.maintenance.getPaperRemaining());
	}

	@Test
	public void testNeedBanknotes() {
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

}
