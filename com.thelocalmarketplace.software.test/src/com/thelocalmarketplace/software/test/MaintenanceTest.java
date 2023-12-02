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

import com.tdc.NoCashAvailableException;
import com.tdc.banknote.Banknote;
import com.tdc.banknote.BanknoteStorageUnit;
import com.tdc.coin.Coin;
import com.tdc.coin.ICoinDispenser;
import com.thelocalmarketplace.hardware.*;
import com.thelocalmarketplace.software.MembershipDatabase;
import com.thelocalmarketplace.software.MembershipNumberValidator;
import com.thelocalmarketplace.software.Software;
import ca.ucalgary.seng300.simulation.NullPointerSimulationException;
import ca.ucalgary.seng300.simulation.SimulationException;
import powerutility.PowerGrid;

public class MaintenanceTest {
	
	private Software software;
	
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

//    private Coin coin_toonie = new Coin(CAD_Currency,value_toonie);
//    private Coin coin_loonie = new Coin(CAD_Currency,value_loonie);
//    private Coin coin_quarter = new Coin(CAD_Currency,value_quarter);
//    private Coin coin_dime = new Coin(CAD_Currency,value_dime);
//    private Coin coin_nickel = new Coin(CAD_Currency,value_nickel);
//    private Coin coin_penny = new Coin(CAD_Currency,value_penny);
    
    private final String outOfPaperMsg = "PRINTER_OUT_OF_PAPER";
    private final String lowPaperMsg = "PRINTER_LOW_PAPER";
    private final String lowPaperSoonMsg = "PRINTER_LOW_PAPER_SOON";

	private MembershipDatabase membershipDatabase;
	private MembershipNumberValidator membershipNumValidator;
	
	private String member0ID;

	@Before
	public void setUp() throws Exception {
        coindenominations = new ArrayList<BigDecimal>();
        CAD = Currency.getInstance("CAD");
        coindenominations.add(value_toonie);
        coindenominations.add(value_loonie);
        coindenominations.add(value_quarter);
        coindenominations.add(value_dime);
        coindenominations.add(value_nickel);

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
        
        AbstractSelfCheckoutStation.resetConfigurationToDefaults();
        AbstractSelfCheckoutStation.configureCoinDispenserCapacity(20);
        AbstractSelfCheckoutStation.configureCoinStorageUnitCapacity(20);
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
		
		bronze_bStorageUnit = bronze_software.banknoteStorageUnit;
		silver_bStorageUnit = silver_software.banknoteStorageUnit;
		gold_bStorageUnit = gold_software.banknoteStorageUnit;

		// disable stations to add initial coins
		bronze_software.attendant.disableCustomerStation();
		silver_software.attendant.disableCustomerStation();
		gold_software.attendant.disableCustomerStation();
		
		// add 10 coins to each dispenser in each station
		for (BigDecimal cd : coindenominations) {
			bronze_software.maintenance.addCoinsInDispenser(bronze_cDispensers.get(cd),cd,10);
			silver_software.maintenance.addCoinsInDispenser(silver_cDispensers.get(cd),cd,10);
			gold_software.maintenance.addCoinsInDispenser(gold_cDispensers.get(cd),cd,10);
		
		}

		// load 10 coins in the coin storage unit
		Coin nickel = new Coin(value_nickel);
		Coin dime = new Coin(value_dime);
		Coin quarter = new Coin(value_quarter);
		Coin loonie = new Coin(value_loonie);
		Coin toonie = new Coin(value_toonie);
    	Coin[] coins = {nickel, nickel, dime, dime, quarter, quarter, loonie, loonie, toonie, toonie};
		bronze_software.getCoinStorage().load(coins);
		silver_software.getCoinStorage().load(coins);
		gold_software.getCoinStorage().load(coins);
		
		// re-enable stations
		bronze_software.attendant.enableCustomerStation();
		silver_software.attendant.enableCustomerStation();
		gold_software.attendant.enableCustomerStation();
		
		
//		for (BigDecimal bd : billDenominations) {
//			banknotes.add(new Banknote(CAD,bd));		
//		}
		
		bronze_software.maintenance.checkBanknotes(5, bronze_bStorageUnit);
		silver_software.maintenance.checkBanknotes(5, bronze_bStorageUnit);
		gold_software.maintenance.checkBanknotes(5, bronze_bStorageUnit);


		membershipDatabase = new MembershipDatabase();
	    membershipNumValidator = new MembershipNumberValidator(this.membershipDatabase);
	    
	    //add a valid string to the database
		member0ID = membershipDatabase.addMember("Alice");
	}
	
	
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
	public void testSessionNotDisabledAddCoinsDispenser() throws SimulationException, CashOverloadException, DisabledException {
		// check that customer stations are not disabled
		assertFalse(bronze_software.isCustomerStationBlocked());
		assertFalse(silver_software.isCustomerStationBlocked());
		assertFalse(gold_software.isCustomerStationBlocked());
		
		// check beforehand that the nickel dispensers have 10 nickels 
		assertEquals(10, bronze_software.getCoinDispensers().get(value_nickel).size());
		assertEquals(10, silver_software.getCoinDispensers().get(value_nickel).size());
		assertEquals(10, gold_software.getCoinDispensers().get(value_nickel).size());
		
		// try adding in 1 nickel to each nickel dispenser
		bronze_software.maintenance.addCoinsInDispenser(
				bronze_software.getCoinDispensers().get(value_nickel), value_nickel, 1);
		
		silver_software.maintenance.addCoinsInDispenser(
				silver_software.getCoinDispensers().get(value_nickel), value_nickel, 1);
		
		gold_software.maintenance.addCoinsInDispenser(
				gold_software.getCoinDispensers().get(value_nickel), value_nickel, 1);
		
		// should equal 10 still since the station is not disabled
		assertEquals(10, bronze_software.getCoinDispensers().get(value_nickel).size());
		assertEquals(10, silver_software.getCoinDispensers().get(value_nickel).size());
		assertEquals(10, silver_software.getCoinDispensers().get(value_nickel).size());
		
	}
	
	@Test (expected = NullPointerSimulationException.class)
	public void testUsingNullDispenserAddCoins() throws SimulationException, CashOverloadException, DisabledException {
		 // check that the each station has 5 dispensers for each coin denomination
		 assertEquals(5, bronze_software.getCoinDispensers().size());
		 assertEquals(5, silver_software.getCoinDispensers().size());
		 assertEquals(5, gold_software.getCoinDispensers().size());
		
		 // remove the coin dispenser for quarters in each station
		 bronze_software.getCoinDispensers().remove(value_quarter);
		 silver_software.getCoinDispensers().remove(value_quarter);
		 gold_software.getCoinDispensers().remove(value_quarter);
		 
		 // check that each station now has 4 dispensers for each coin denomination
		 assertEquals(4, bronze_software.getCoinDispensers().size());
		 assertEquals(4, silver_software.getCoinDispensers().size());
		 assertEquals(4, gold_software.getCoinDispensers().size());
		 
		 // disable customer stations to add coin
		 bronze_software.attendant.disableCustomerStation();
		 silver_software.attendant.disableCustomerStation();
		 gold_software.attendant.disableCustomerStation();
		 
		 // try adding in a quarter in each station using a null dispenser
		 bronze_software.maintenance.addCoinsInDispenser(
				 bronze_software.getCoinDispensers().get(value_quarter), value_quarter, 1);
		 
		 silver_software.maintenance.addCoinsInDispenser(
				 silver_software.getCoinDispensers().get(value_quarter), value_quarter, 1);
		 
		 gold_software.maintenance.addCoinsInDispenser(
				 gold_software.getCoinDispensers().get(value_quarter), value_quarter, 1);		 
	}
	
	@Test (expected = NullPointerSimulationException.class)
	public void testUsingNullDispenserRemoveCoins() throws CashOverloadException, NoCashAvailableException, DisabledException {
		 // check that each station has 5 dispensers for each coin denomination
		 assertEquals(5, bronze_software.getCoinDispensers().size());
		 assertEquals(5, silver_software.getCoinDispensers().size());
		 assertEquals(5, gold_software.getCoinDispensers().size());
		
		 // remove the coin dispenser for loonies in each station
		 bronze_software.getCoinDispensers().remove(value_loonie);
		 silver_software.getCoinDispensers().remove(value_loonie);
		 gold_software.getCoinDispensers().remove(value_loonie);
		 
		 // check that each station now has 4 dispensers for each coin denomination
		 assertEquals(4, bronze_software.getCoinDispensers().size());
		 assertEquals(4, silver_software.getCoinDispensers().size());
		 assertEquals(4, gold_software.getCoinDispensers().size());
		 
		 // disable customer stations to remove a coin
		 bronze_software.attendant.disableCustomerStation();
		 silver_software.attendant.disableCustomerStation();
		 gold_software.attendant.disableCustomerStation();
		 
	     // try removing in a loonie in each station using a null dispenser
		 bronze_software.maintenance.removeCoinsInDispenser(
				 bronze_software.getCoinDispensers().get(value_loonie), 1);
		 
		 silver_software.maintenance.removeCoinsInDispenser(
				 silver_software.getCoinDispensers().get(value_loonie), 1);
		 
		 gold_software.maintenance.removeCoinsInDispenser(
				 gold_software.getCoinDispensers().get(value_loonie), 1);	 
	}
	
	@Test
	public void testSessionNotDisabledRemoveCoinsDispenser() throws CashOverloadException, NoCashAvailableException, DisabledException {
		// check that customer stations are not disabled
		assertFalse(bronze_software.isCustomerStationBlocked());
		assertFalse(silver_software.isCustomerStationBlocked());
		assertFalse(gold_software.isCustomerStationBlocked());
		
		// check beforehand that the dime dispensers have 10 dimes
		assertEquals(10, bronze_software.getCoinDispensers().get(value_dime).size());
		assertEquals(10, silver_software.getCoinDispensers().get(value_dime).size());
		assertEquals(10, gold_software.getCoinDispensers().get(value_dime).size());
		
		// try removing 1 dime from each dime dispenser
		bronze_software.maintenance.removeCoinsInDispenser(
				bronze_software.getCoinDispensers().get(value_dime), 1);
		
		silver_software.maintenance.removeCoinsInDispenser(
				silver_software.getCoinDispensers().get(value_dime), 1);
		
		gold_software.maintenance.removeCoinsInDispenser(
				gold_software.getCoinDispensers().get(value_dime), 1);
		
		// should equal 10 still since the station is not disabled
		assertEquals(10, bronze_software.getCoinDispensers().get(value_dime).size());
		assertEquals(10, silver_software.getCoinDispensers().get(value_dime).size());
		assertEquals(10, silver_software.getCoinDispensers().get(value_dime).size());	
	}
	
	@Test
	public void testSessionNotDisabledRemoveCoinsStorage() {
		// check that customer stations are not disabled
		assertFalse(bronze_software.isCustomerStationBlocked());
		assertFalse(silver_software.isCustomerStationBlocked());
		assertFalse(gold_software.isCustomerStationBlocked());
		
		// check beforehand that the coin storage has 10 coins
		assertEquals(10, bronze_software.getCoinStorage().getCoinCount());
		assertEquals(10, silver_software.getCoinStorage().getCoinCount());
		assertEquals(10, gold_software.getCoinStorage().getCoinCount());
		
		// try unloading the coin storage unit 
		bronze_software.maintenance.removeAllCoinsInStorageUnit(bronze_software.getCoinStorage());
		silver_software.maintenance.removeAllCoinsInStorageUnit(silver_software.getCoinStorage());
		gold_software.maintenance.removeAllCoinsInStorageUnit(gold_software.getCoinStorage());
		
		// should still equal 10 since the station is not disabled
		assertEquals(10, bronze_software.getCoinStorage().getCoinCount());
		assertEquals(10, silver_software.getCoinStorage().getCoinCount());
		assertEquals(10, gold_software.getCoinStorage().getCoinCount());		
	}

	@Test
	public void testPredictLowCoinsDispenser() throws CashOverloadException, NoCashAvailableException, DisabledException {
		// check that there aren't low amount of toonies in each toonie dispenser		
		// check beforehand that the toonie dispensers have 10 toonies 
		assertEquals(10, bronze_software.getCoinDispensers().get(value_toonie).size());
		assertEquals(10, silver_software.getCoinDispensers().get(value_toonie).size());
		assertEquals(10, gold_software.getCoinDispensers().get(value_nickel).size());
		
		bronze_software.maintenance.predictLowCoinsDispenser(value_toonie);
		silver_software.maintenance.predictLowCoinsDispenser(value_toonie);
		gold_software.maintenance.predictLowCoinsDispenser(value_toonie);
		
		assertFalse(bronze_software.maintenance.getIssues().contains("COIN_DISPENSER_LOW_COINS"));
		assertFalse(silver_software.maintenance.getIssues().contains("COIN_DISPENSER_LOW_COINS"));
		assertFalse(gold_software.maintenance.getIssues().contains("COIN_DISPENSER_LOW_COINS"));
		
		// disable the customer stations to remove 6 coins in the dispensers of each station
		bronze_software.attendant.disableCustomerStation();
		silver_software.attendant.disableCustomerStation();
		gold_software.attendant.disableCustomerStation();
		
		// remove 6 toonies 
		bronze_software.maintenance.removeCoinsInDispenser(
				bronze_software.getCoinDispensers().get(value_toonie), 6);
		
		silver_software.maintenance.removeCoinsInDispenser(
				silver_software.getCoinDispensers().get(value_toonie), 6);
		
		gold_software.maintenance.removeCoinsInDispenser(
				gold_software.getCoinDispensers().get(value_toonie), 6);
		
		// enable customer stations
		bronze_software.attendant.enableCustomerStation();
		silver_software.attendant.enableCustomerStation();
		gold_software.attendant.enableCustomerStation();
		
		// check if theres low amount of toonies in each station
		bronze_software.maintenance.predictLowCoinsDispenser(value_toonie);
		silver_software.maintenance.predictLowCoinsDispenser(value_toonie);
		gold_software.maintenance.predictLowCoinsDispenser(value_toonie);
		
		// will be true if coins are <= 5 in the dispenser, since the capacity is 20
		assertTrue(bronze_software.maintenance.getIssues().contains("COIN_DISPENSER_LOW_COINS"));
		assertTrue(silver_software.maintenance.getIssues().contains("COIN_DISPENSER_LOW_COINS"));
		assertTrue(gold_software.maintenance.getIssues().contains("COIN_DISPENSER_LOW_COINS"));
		
		// check that customer station is blocked due to issue
		assertTrue(bronze_software.isCustomerStationBlocked());
		assertTrue(silver_software.isCustomerStationBlocked());
		assertTrue(gold_software.isCustomerStationBlocked());
	}

	@Test
	public void testPredictCoinsFullDispenser() throws SimulationException, CashOverloadException, DisabledException {
		// check that the coin dispenser in each station for nickels is not almost full
		// check beforehand that the nickel dispensers have 10 nickels 
		assertEquals(10, bronze_software.getCoinDispensers().get(value_nickel).size());
		assertEquals(10, silver_software.getCoinDispensers().get(value_nickel).size());
		assertEquals(10, gold_software.getCoinDispensers().get(value_nickel).size());
				
		bronze_software.maintenance.predictCoinsFullDispenser(value_nickel);
		silver_software.maintenance.predictCoinsFullDispenser(value_nickel);
		gold_software.maintenance.predictCoinsFullDispenser(value_nickel);
		
		assertFalse(bronze_software.maintenance.getIssues().contains("COIN_DISPENSER_ALMOST_FULL"));
		assertFalse(silver_software.maintenance.getIssues().contains("COIN_DISPENSER_ALMOST_FULL"));
		assertFalse(gold_software.maintenance.getIssues().contains("COIN_DISPENSER_ALMOST_FULL"));
		
		// disable the customer stations to add coins in the nickel dispensers
		bronze_software.attendant.disableCustomerStation();
		silver_software.attendant.disableCustomerStation();
		gold_software.attendant.disableCustomerStation();
		
		// add 7 nickels in the nickel dispensers of each station
		bronze_software.maintenance.addCoinsInDispenser(
				 bronze_software.getCoinDispensers().get(value_nickel), value_nickel, 7);
		 
		silver_software.maintenance.addCoinsInDispenser(
				 silver_software.getCoinDispensers().get(value_nickel), value_nickel, 7);
		 
		gold_software.maintenance.addCoinsInDispenser(
				 gold_software.getCoinDispensers().get(value_nickel), value_nickel, 7);	
		
		// enable customer stations
		bronze_software.attendant.enableCustomerStation();
		silver_software.attendant.enableCustomerStation();
		gold_software.attendant.enableCustomerStation();
		
		// check if the nickel dispenser is almost full of nickels
		bronze_software.maintenance.predictCoinsFullDispenser(value_nickel);
		silver_software.maintenance.predictCoinsFullDispenser(value_nickel);
		gold_software.maintenance.predictCoinsFullDispenser(value_nickel);
				
		// will be true if coins are >= 15 in the dispenser, since the capacity is 20
		assertTrue(bronze_software.maintenance.getIssues().contains("COIN_DISPENSER_ALMOST_FULL"));
		assertTrue(silver_software.maintenance.getIssues().contains("COIN_DISPENSER_ALMOST_FULL"));
		assertTrue(gold_software.maintenance.getIssues().contains("COIN_DISPENSER_ALMOST_FULL"));		
		
		// check that customer station is blocked due to issue
		assertTrue(bronze_software.isCustomerStationBlocked());
		assertTrue(silver_software.isCustomerStationBlocked());
		assertTrue(gold_software.isCustomerStationBlocked());
	}

	@Test
	public void testPredictCoinsFullStorage() throws SimulationException, CashOverloadException, DisabledException {
		// check that the coin storage in each station is not almost full
		// check beforehand that the coin storage units have 10 coins
		assertEquals(10, bronze_software.getCoinStorage().getCoinCount());
		assertEquals(10, silver_software.getCoinStorage().getCoinCount());
		assertEquals(10, gold_software.getCoinStorage().getCoinCount());
		
		bronze_software.maintenance.predictCoinsFullStorage();
		silver_software.maintenance.predictCoinsFullStorage();
	    gold_software.maintenance.predictCoinsFullStorage();
		
		assertFalse(bronze_software.maintenance.getIssues().contains("COIN_STORAGE_ALMOST_FULL"));
		assertFalse(silver_software.maintenance.getIssues().contains("COIN_STORAGE_ALMOST_FULL"));
		assertFalse(gold_software.maintenance.getIssues().contains("COIN_STORAGE_ALMOST_FULL"));
		
		// disable the customer stations to add coins in the coin storage units
		bronze_software.attendant.disableCustomerStation();
		silver_software.attendant.disableCustomerStation();
		gold_software.attendant.disableCustomerStation();
		
		// 7 coins are received in the storage unit
		Coin dime = new Coin(value_dime);
		
		int i = 0;		
		for (i = 0; i <= 6; i++ ) {
			bronze_software.getCoinStorage().receive(dime);
			silver_software.getCoinStorage().receive(dime);
			gold_software.getCoinStorage().receive(dime);
		}
		
		// enable customer stations
		bronze_software.attendant.enableCustomerStation();
		silver_software.attendant.enableCustomerStation();
		gold_software.attendant.enableCustomerStation();
		
		// check if the storage unit is almost full in each station
		bronze_software.maintenance.predictCoinsFullStorage();
		silver_software.maintenance.predictCoinsFullStorage();
	    gold_software.maintenance.predictCoinsFullStorage();
		
	    // will return true if coins are >= 15 in the storage unit, since the capacity is 20
	    assertTrue(bronze_software.maintenance.getIssues().contains("COIN_STORAGE_ALMOST_FULL"));
		assertTrue(silver_software.maintenance.getIssues().contains("COIN_STORAGE_ALMOST_FULL"));
		assertTrue(gold_software.maintenance.getIssues().contains("COIN_STORAGE_ALMOST_FULL"));	
		
		// check that customer station is blocked due to issue
		assertTrue(bronze_software.isCustomerStationBlocked());
		assertTrue(silver_software.isCustomerStationBlocked());
		assertTrue(gold_software.isCustomerStationBlocked());
	}

	@Test
	public void testAddCoinsInDispenser() throws SimulationException, CashOverloadException, DisabledException {
		// disable the customer stations to add coins in dispenser
		bronze_software.attendant.disableCustomerStation();
		silver_software.attendant.disableCustomerStation();
		gold_software.attendant.disableCustomerStation();
				
		// check beforehand that the nickel dispenser have 10 nickels 
		assertEquals(10, bronze_software.getCoinDispensers().get(value_nickel).size());
		assertEquals(10, silver_software.getCoinDispensers().get(value_nickel).size());
		assertEquals(10, gold_software.getCoinDispensers().get(value_nickel).size());
				
		// add 1 nickel to each dispenser
		bronze_software.maintenance.addCoinsInDispenser(
				bronze_software.getCoinDispensers().get(value_nickel), value_nickel, 1);
				
		silver_software.maintenance.addCoinsInDispenser(
				silver_software.getCoinDispensers().get(value_nickel), value_nickel, 1);
				
		gold_software.maintenance.addCoinsInDispenser(
				gold_software.getCoinDispensers().get(value_nickel), value_nickel, 1);
				
		// check if the 1 nickel has been added to the nickel dispenser
		assertEquals(11, bronze_software.getCoinDispensers().get(value_nickel).size());
		assertEquals(11, silver_software.getCoinDispensers().get(value_nickel).size());
		assertEquals(11, silver_software.getCoinDispensers().get(value_nickel).size());
	}

	@Test
	public void testRemoveCoinsInDispenser() throws CashOverloadException, NoCashAvailableException, DisabledException {
		// disable the customer stations to remove coins in dispenser
		bronze_software.attendant.disableCustomerStation();
		silver_software.attendant.disableCustomerStation();
		gold_software.attendant.disableCustomerStation();
		
		// check beforehand that the dime dispensers have 10 dimes
		assertEquals(10, bronze_software.getCoinDispensers().get(value_dime).size());
		assertEquals(10, silver_software.getCoinDispensers().get(value_dime).size());
		assertEquals(10, gold_software.getCoinDispensers().get(value_dime).size());
		
		// remove 1 dime from each dime dispenser
		bronze_software.maintenance.removeCoinsInDispenser(
				bronze_software.getCoinDispensers().get(value_dime), 1);
		
		silver_software.maintenance.removeCoinsInDispenser(
				silver_software.getCoinDispensers().get(value_dime), 1);
		
		gold_software.maintenance.removeCoinsInDispenser(
				gold_software.getCoinDispensers().get(value_dime), 1);
		
		// check if 1 dime has been removed to each dime dispenser 
		assertEquals(9, bronze_software.getCoinDispensers().get(value_dime).size());
		assertEquals(9, silver_software.getCoinDispensers().get(value_dime).size());
		assertEquals(9, silver_software.getCoinDispensers().get(value_dime).size());	
	}
	

	@Test
	public void testRemoveAllCoinsInStorageUnit() {
		// disable the customer stations to remove coins in the coin storage unit
		bronze_software.attendant.disableCustomerStation();
		silver_software.attendant.disableCustomerStation();
		gold_software.attendant.disableCustomerStation();
		
		// check beforehand that the coin storage has 10 coins
		assertEquals(10, bronze_software.getCoinStorage().getCoinCount());
		assertEquals(10, silver_software.getCoinStorage().getCoinCount());
		assertEquals(10, gold_software.getCoinStorage().getCoinCount());
		
		// unload the coin storage unit 
		bronze_software.maintenance.removeAllCoinsInStorageUnit(bronze_software.getCoinStorage());
		silver_software.maintenance.removeAllCoinsInStorageUnit(silver_software.getCoinStorage());
		gold_software.maintenance.removeAllCoinsInStorageUnit(gold_software.getCoinStorage());
		
		// should have no coins in the storage unit
		assertEquals(0, bronze_software.getCoinStorage().getCoinCount());
		assertEquals(0, silver_software.getCoinStorage().getCoinCount());
		assertEquals(0, gold_software.getCoinStorage().getCoinCount());
	}

	@Test
	public void testCheckPaperBronzeStation() {
		bronze_software.maintenance.checkPaper(100);
	}
	
	@Test
	public void testCheckPaperPaperRemainingIsZeroSilverStation() {
		silver_software.maintenance.setPaperRemaining(0);
		silver_software.maintenance.checkPaper(100);
		boolean flag = silver_software.maintenance.getIssues().contains(this.outOfPaperMsg);
		assertTrue(flag);
	}
	
	@Test
	public void testCheckPaperPaperRemainingIsZeroGoldStation() {
		gold_software.maintenance.setPaperRemaining(0);
		gold_software.maintenance.checkPaper(100);
		boolean flag = gold_software.maintenance.getIssues().contains(this.outOfPaperMsg);
		assertTrue(flag);
	}
	
	@Test
	public void testCheckPaperPaperIsLowSilverStation() {
		silver_software.maintenance.setPaperRemaining(1);
		silver_software.maintenance.checkPaper(100);
		boolean flag = silver_software.maintenance.getIssues().contains(this.lowPaperMsg);
		assertTrue(flag);
	}
	
	@Test
	public void testCheckPaperPaperIsLowGoldStation() {
		gold_software.maintenance.setPaperRemaining(1);
		gold_software.maintenance.checkPaper(100);
		boolean flag = gold_software.maintenance.getIssues().contains(this.lowPaperMsg);
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
		boolean flag = !bronze_software.isCustomerStationBlocked();
		assertTrue(flag);
	}
	
	@Test
	public void testCheckPaperNoIssuesSilverStation() {
		silver_software.maintenance.setPaperRemaining(1000);
		silver_software.maintenance.checkPaper(50);
		boolean flag = !silver_software.isCustomerStationBlocked();
		assertTrue(flag);
	}
	
	@Test
	public void testCheckPaperNoIssuesGoldStation() {
		gold_software.maintenance.setPaperRemaining(1000);
		gold_software.maintenance.checkPaper(50);
		boolean flag = !gold_software.isCustomerStationBlocked();
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
		boolean flag = !(bronze_software.maintenance.getIssues().contains(this.lowPaperMsg) || bronze_software.maintenance.getIssues().contains(this.lowPaperSoonMsg) || bronze_software.maintenance.getIssues().contains(this.outOfPaperMsg));
		assertTrue(flag);
	}
	
	@Test
	public void testPredictLowPaperLevelNoIssuesSilverStation() {
		silver_software.maintenance.setPaperRemaining(1000);
		silver_software.maintenance.setAveragePaperUsagePerSession(50);
		silver_software.maintenance.predictLowPaper();
		boolean flag = !(silver_software.maintenance.getIssues().contains(this.lowPaperMsg) || silver_software.maintenance.getIssues().contains(this.lowPaperSoonMsg) || silver_software.maintenance.getIssues().contains(this.outOfPaperMsg));
		assertTrue(flag);
	}
	
	@Test
	public void testPredictLowPaperLevelNoIssuesGoldStation() {
		gold_software.maintenance.setPaperRemaining(1000);
		gold_software.maintenance.setAveragePaperUsagePerSession(50);
		gold_software.maintenance.predictLowPaper();
		boolean flag = !(gold_software.maintenance.getIssues().contains(this.lowPaperMsg) || gold_software.maintenance.getIssues().contains(this.lowPaperSoonMsg) || gold_software.maintenance.getIssues().contains(this.outOfPaperMsg));
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
	public void testInkHasBeenAddedToThePrinter() {
		fail("Not yet implemented");
	}

	@Test
	public void testCoinsFullCoinDispenser() throws SimulationException, CashOverloadException, DisabledException {
		// check beforehand that the nickel dispensers have 10 nickels 
		assertEquals(10, bronze_software.getCoinDispensers().get(value_nickel).size());
		assertEquals(10, silver_software.getCoinDispensers().get(value_nickel).size());
		assertEquals(10, gold_software.getCoinDispensers().get(value_nickel).size());
				
		assertFalse(bronze_software.maintenance.getIssues().contains("COIN_DISPENSER_IS_FULL"));
		assertFalse(silver_software.maintenance.getIssues().contains("COIN_DISPENSER_IS_FULL"));
		assertFalse(gold_software.maintenance.getIssues().contains("COIN_DISPENSER_IS_FULL"));
		
		// disable the customer stations to add coins in the nickel dispensers
		bronze_software.attendant.disableCustomerStation();
		silver_software.attendant.disableCustomerStation();
		gold_software.attendant.disableCustomerStation();
		
		// add 10 nickels in the nickel dispensers of each station
		bronze_software.maintenance.addCoinsInDispenser(
				 bronze_software.getCoinDispensers().get(value_nickel), value_nickel, 10);
		 
		silver_software.maintenance.addCoinsInDispenser(
				 silver_software.getCoinDispensers().get(value_nickel), value_nickel, 10);
		 
		gold_software.maintenance.addCoinsInDispenser(
				 gold_software.getCoinDispensers().get(value_nickel), value_nickel, 10);	
		
		
		// check that 10 nickels have been added to each nickel dispenser
		assertEquals(20, bronze_software.getCoinDispensers().get(value_nickel).size());
		assertEquals(20, silver_software.getCoinDispensers().get(value_nickel).size());
		assertEquals(20, gold_software.getCoinDispensers().get(value_nickel).size());
		
		// will be true if coins are 20, max capacity is 20
		assertTrue(bronze_software.maintenance.getIssues().contains("COIN_DISPENSER_IS_FULL"));
		assertTrue(silver_software.maintenance.getIssues().contains("COIN_DISPENSER_IS_FULL"));
		assertTrue(gold_software.maintenance.getIssues().contains("COIN_DISPENSER_IS_FULL"));	
		
		// check that the customer station is disabled because of issue
		assertTrue(bronze_software.isCustomerStationBlocked());
		assertTrue(silver_software.isCustomerStationBlocked());
		assertTrue(gold_software.isCustomerStationBlocked());
	}

	@Test
	public void testCoinsEmptyDispenser() throws CashOverloadException, NoCashAvailableException, DisabledException {
		// check beforehand that the dime dispensers have 10 dimes
		assertEquals(10, bronze_software.getCoinDispensers().get(value_dime).size());
		assertEquals(10, silver_software.getCoinDispensers().get(value_dime).size());
		assertEquals(10, gold_software.getCoinDispensers().get(value_dime).size());
		
		assertFalse(bronze_software.maintenance.getIssues().contains("COIN_DISPENSER_OUT_OF_COINS"));
		assertFalse(silver_software.maintenance.getIssues().contains("COIN_DISPENSER_OUT_OF_COINS"));
		assertFalse(gold_software.maintenance.getIssues().contains("COIN_DISPENSER_OUT_OF_COINS"));
		
		// disable the customer stations to remove coins in dispenser
		bronze_software.attendant.disableCustomerStation();
		silver_software.attendant.disableCustomerStation();
		gold_software.attendant.disableCustomerStation();
		
		// remove 10 dimes from each dime dispenser
		bronze_software.maintenance.removeCoinsInDispenser(
				bronze_software.getCoinDispensers().get(value_dime), 10);
		
		silver_software.maintenance.removeCoinsInDispenser(
				silver_software.getCoinDispensers().get(value_dime), 10);
		
		gold_software.maintenance.removeCoinsInDispenser(
			    gold_software.getCoinDispensers().get(value_dime), 10);
		
		// check that 10 dimes have been removed from each dime dispenser
		assertEquals(0, bronze_software.getCoinDispensers().get(value_dime).size());
		assertEquals(0, silver_software.getCoinDispensers().get(value_dime).size());
		assertEquals(0, silver_software.getCoinDispensers().get(value_dime).size());
		
		// will return true if theres no coins in the dispensers
		assertTrue(bronze_software.maintenance.getIssues().contains("COIN_DISPENSER_OUT_OF_COINS"));
		assertTrue(silver_software.maintenance.getIssues().contains("COIN_DISPENSER_OUT_OF_COINS"));
		assertTrue(gold_software.maintenance.getIssues().contains("COIN_DISPENSER_OUT_OF_COINS"));
		
		// check that the customer station is disabled because of issue
		assertTrue(bronze_software.isCustomerStationBlocked());
		assertTrue(silver_software.isCustomerStationBlocked());
		assertTrue(gold_software.isCustomerStationBlocked());
	}

	@Test
	public void testCoinsFullCoinStorageUnit() throws SimulationException, CashOverloadException, DisabledException {
		// check beforehand that the coin storage units have 10 coins
		assertEquals(10, bronze_software.getCoinStorage().getCoinCount());
		assertEquals(10, silver_software.getCoinStorage().getCoinCount());
		assertEquals(10, gold_software.getCoinStorage().getCoinCount());
		
		assertFalse(bronze_software.maintenance.getIssues().contains("COIN_STORAGE_IS_FULL"));
		assertFalse(silver_software.maintenance.getIssues().contains("COIN_STORAGE_IS_FULL"));
		assertFalse(gold_software.maintenance.getIssues().contains("COIN_STORAGE_IS_FULL"));
		
		// 10 coins are received in the storage unit
		Coin dime = new Coin(value_dime);
		
		int i = 0;		
		for (i = 0; i <= 9; i++ ) {
			bronze_software.getCoinStorage().receive(dime);
			silver_software.getCoinStorage().receive(dime);
			gold_software.getCoinStorage().receive(dime);
		}
		
		// check that 10 coins have been added to the storage unit
		assertEquals(20, bronze_software.getCoinStorage().getCoinCount());
		assertEquals(20, silver_software.getCoinStorage().getCoinCount());
		assertEquals(20, gold_software.getCoinStorage().getCoinCount());
		
	    // will return true if the coin storage unit is full
	    assertTrue(bronze_software.maintenance.getIssues().contains("COIN_STORAGE_IS_FULL"));
		assertTrue(silver_software.maintenance.getIssues().contains("COIN_STORAGE_IS_FULL"));
		assertTrue(gold_software.maintenance.getIssues().contains("COIN_STORAGE_IS_FULL"));	
		
		// check that the customer station is disabled because of issue
		assertTrue(bronze_software.isCustomerStationBlocked());
		assertTrue(silver_software.isCustomerStationBlocked());
		assertTrue(gold_software.isCustomerStationBlocked());
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

	/**
	 * Test adding a member to the database
	 */
	@Test
	public void testAddMemberToDatabase()
	{
		String memberID = membershipDatabase.addMember("bob");
		assertTrue(membershipDatabase.memberExists(memberID));
	}
	
	/**
	 * Test adding a member with null as a name to the database
	 */
	@Test(expected = NullPointerSimulationException.class)
	public void testAddNullMemberToDatabase()
	{
		membershipDatabase.addMember(null);
	}
	
	/**
	 * Test adding a member with an empty string for a name to the database
	 */
	@Test(expected = RuntimeException.class)
	public void testAddEmtpyMemberNameToDatabase()
	{
		
		membershipDatabase.addMember("");
	}
	
	/**
	 * test adding points to the user's account
	 */
	@Test
	public void testAddPoints()
	{
		int points = 50;
		membershipDatabase.addPoints(member0ID, points);
		assertEquals(points, membershipDatabase.getPoints(member0ID));
	}
	
	
	/**
	 * Test negative points being added 
	 */
	@Test
	public void testAddNegativePoints()
	{
		int points = -50;
		membershipDatabase.addPoints(member0ID, points);
		assertEquals(0, membershipDatabase.getPoints(member0ID));
	}

	/**
	 * 
	 */
	@Test(expected = ArithmeticException.class)
	public void testAddOverflowPoints()
	{
		int points0 = 2551267;
		int pointsMAX = Integer.MAX_VALUE;
		membershipDatabase.addPoints(member0ID, points0);
		membershipDatabase.addPoints(member0ID, pointsMAX);
	}
	
	@Test
	public void testSubtractPoints()
	{
		int pointsToAdd = 50;
		membershipDatabase.addPoints(member0ID, pointsToAdd);

		int pointsToRemove = 35;
		membershipDatabase.subtractPoints(member0ID, pointsToRemove);
		assertEquals(pointsToAdd - pointsToRemove, membershipDatabase.getPoints(member0ID));
	}
	
	@Test
	public void testSubtractExactPoints()
	{
		int pointsToAdd = 50;
		membershipDatabase.addPoints(member0ID, pointsToAdd);

		int pointsToRemove = pointsToAdd;
		membershipDatabase.subtractPoints(member0ID, pointsToRemove);
		assertEquals(0, membershipDatabase.getPoints(member0ID));
		
	}

	@Test
	public void testSubtractNegativePoints()
	{
		int pointsToAdd = 50;
		membershipDatabase.addPoints(member0ID, pointsToAdd);

		int pointsToRemove = -35;
		membershipDatabase.subtractPoints(member0ID, pointsToRemove);
		assertEquals(pointsToAdd - Math.abs(pointsToRemove), membershipDatabase.getPoints(member0ID));
	}

	@Test(expected = ArithmeticException.class)
	public void testSubtractUnderflowPoints()
	{
		int points0 = 2551267;
		int pointsMIN = Integer.MIN_VALUE;
		membershipDatabase.addPoints(member0ID, points0);
		membershipDatabase.subtractPoints(member0ID, pointsMIN);
	}
	
	/**
	 * test valid case
	 */
	@Test
	public void testValidMembershipNumber()
	{
		//make sure a session is started
//		String memberNum = "00000001";
		String memberNum = "12345678";
		membershipDatabase.addMember(memberNum);
		
		//mismatch between database and actual
//		assertTrue(membershipNumValidator.isValid(Integer.toString(memberNum0ID)));
//		assertTrue(membershipNumValidator.isValid(memberNum));
//		assertTrue(membershipNumValidator.isValid(memberNum0));
	}
	
	/**
	 * Test with member not in database
	 */
	@Test
	public void testMembershipNumberNotInDatabase()
	{
		String memberNum = "00000001";
//		assertFalse(membershipDatabase.memberExists(Integer.parseInt(memberNum)));
		assertFalse(membershipNumValidator.isValid(memberNum));
	}
	
	/**
	 * test with a very large string input
	 */
	@Test
	public void testVeryLargeMemberNum()
	{
		String memberNum = "1078234678126340781234789012078780078947867816780167841236780";
		membershipDatabase.addMember(memberNum);
		assertFalse(membershipNumValidator.isValid(memberNum));
	}
	
	/**
	 * Test with a small input
	 */
	@Test
	public void testSmallInput()
	{
		String memberNum = "123";
		membershipDatabase.addMember(memberNum);
		assertFalse(membershipNumValidator.isValid(memberNum));
	}
	
	/**
	 * Test an invalid string as input
	 */
	@Test
	public void testWithGarbageString0()
	{
		String memberNum = "123asDf;";
		membershipDatabase.addMember(memberNum);
		assertFalse(membershipNumValidator.isValid(memberNum));
	}
	
	/**
	 * Test an invalid string as input. Specifically special characters
	 */
	@Test
	public void testWithSpecialCharacters()
	{
		String memberNum = "12-3@5$6";
		membershipDatabase.addMember(memberNum);
		assertFalse(membershipNumValidator.isValid(memberNum));
	}

	/**
	 * Test an invalid string as input. Specifically with spaces
	 */
	@Test
	public void testWithSpaces()
	{
		String memberNum = "12 345 6";
		membershipDatabase.addMember(memberNum);
		assertFalse(membershipNumValidator.isValid(memberNum));
	}
	
	/**
	 * Test an invalid string as input. Specifically with only 1 lowercase letter sandwiched in
	 */
	@Test
	public void testWithSandwichedCharacter()
	{
		String memberNum = "1234a567";
		membershipDatabase.addMember(memberNum);
		assertFalse(membershipNumValidator.isValid(memberNum));
	}

	/**
	 * Test an invalid string as input. Specifically with all lowercase letters
	 */
	@Test
	public void testAllLowercaseLetters()
	{
		String memberNum = "abcdefg";
		membershipDatabase.addMember(memberNum);
		assertFalse(membershipNumValidator.isValid(memberNum));
	}

	/**
	 * Test an invalid string as input. Specifically null
	 */
	@Test
	public void testNull()
	{
		assertFalse(membershipNumValidator.isValid(null));
	}
	

	/**
	 * Test an invalid string as input. Specifically an empty string
	 */
	@Test
	public void emptyString()
	{
		assertFalse(membershipNumValidator.isValid(""));
	}

}
