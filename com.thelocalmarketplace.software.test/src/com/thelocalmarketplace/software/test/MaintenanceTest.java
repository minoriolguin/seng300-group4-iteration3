package com.thelocalmarketplace.software.test;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.tdc.banknote.Banknote;
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
		
		bronze_software.maintenance.resolveInkIssue(1000);
		bronze_software.maintenance.resolvePrinterPaperIssue(1000);
		
		silver_software.maintenance.resolveInkIssue(1000);
		silver_software.maintenance.resolvePrinterPaperIssue(1000);
		
		gold_software.maintenance.resolveInkIssue(1000);
		gold_software.maintenance.resolvePrinterPaperIssue(1000);
		
		bronze_cDispensers = bronze_software.coinDispensers;
		silver_cDispensers = silver_software.coinDispensers;
		gold_cDispensers = gold_software.coinDispensers;
		
		for (BigDecimal cd : coindenominations) {
			bronze_software.maintenance.addCoinsInDispenser(bronze_cDispensers.get(cd),cd,10);
			silver_software.maintenance.addCoinsInDispenser(silver_cDispensers.get(cd),cd,10);
			gold_software.maintenance.addCoinsInDispenser(gold_cDispensers.get(cd),cd,10);
		}
		
		for (BigDecimal bd : billDenominations) {
			banknotes.add(new Banknote(CAD,bd));		
		}
		
		for (Banknote b : banknotes) {
			bronze_software.maintenance.resolveBanknotesIssues(b);
			silver_software.maintenance.resolveBanknotesIssues(b);
			gold_software.maintenance.resolveBanknotesIssues(b);	
		}
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
	public void testNeedBanknotes() {
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

}
