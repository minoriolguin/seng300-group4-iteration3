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

import static org.junit.Assert.*;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import com.thelocalmarketplace.software.CustomerStationControl;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.hardware.SelfCheckoutStationBronze;
import com.thelocalmarketplace.hardware.SelfCheckoutStationGold;
import com.thelocalmarketplace.hardware.SelfCheckoutStationSilver;
import com.thelocalmarketplace.software.Order;
import com.thelocalmarketplace.software.PayCashControl;
import com.thelocalmarketplace.software.test.ExampleItems.AppleJuice;
import com.thelocalmarketplace.software.test.ExampleItems.PeanutButter;

import ca.ucalgary.seng300.simulation.SimulationException;

import com.jjjwelectronics.OverloadedDevice;
import com.tdc.CashOverloadException;
import com.tdc.ComponentFailure;
import com.tdc.DisabledException;
import com.tdc.banknote.Banknote;
import com.tdc.banknote.BanknoteStorageUnit;
import com.tdc.banknote.BanknoteValidator;
import com.tdc.banknote.IBanknoteDispenser;
import com.tdc.coin.Coin;
import com.tdc.coin.CoinStorageUnit;
import com.tdc.coin.CoinValidator;
import com.tdc.coin.ICoinDispenser;

import powerutility.PowerGrid;

/**
 * This class contains tests for the payment functionality using cash in a self-checkout system.
 */
public class PayCashTests {
	// Fields to hold components for testing
	private CustomerStationControl control;
	private AbstractSelfCheckoutStation station;
	private PayCashControl payCash;
	private CoinStorageUnit coinStorageStub;
	private BanknoteStorageUnit banknoteStorageStub;
	private BigDecimal dollar = new BigDecimal(1.00);
	private List<BigDecimal> denominations;
	private Coin testCoin;
	private Banknote testBanknote;
	private Currency cad = Currency.getInstance("CAD");
	
	/**
     * Setup method to initialize common components before testing.
     * @throws CashOverloadException Thrown if there is an issue with cash overload.
     * @throws OverloadedDevice Thrown if there is an issue with device initialization.
     */
	@Before
	public void setup() throws CashOverloadException, OverloadedDevice {
		// Set denominations to reflect Canadian currencies
		AbstractSelfCheckoutStation.resetConfigurationToDefaults();
		BigDecimal[] banknoteDenominations = {new BigDecimal(5), new BigDecimal(10), new BigDecimal(20), new BigDecimal(50), new BigDecimal(100)};
        AbstractSelfCheckoutStation.configureBanknoteDenominations(banknoteDenominations);
        BigDecimal[] coinDenominations = {new BigDecimal(0.01), new BigDecimal(0.05), new BigDecimal(0.1), new BigDecimal(0.25), new BigDecimal(1), new BigDecimal(2)};
        AbstractSelfCheckoutStation.configureCoinDenominations(coinDenominations); 
        
		// initialize station and turn on required components
		station = new SelfCheckoutStationGold();
		control = new CustomerStationControl(station);
		PowerGrid.engageUninterruptiblePowerSource();
		station.plugIn(PowerGrid.instance());
		station.turnOn();

		// load cash dispensers  
		int denomToLoad = 0;
		for(IBanknoteDispenser bd : station.banknoteDispensers.values()) {
			for(int i = 0; i < 10; i++) {
				bd.load(new Banknote(cad, banknoteDenominations[denomToLoad]));
			}
			denomToLoad++;
		}
		
		denomToLoad = 0;
		for(ICoinDispenser cd : station.coinDispensers.values()) {
			for(int i = 0; i < 10; i++) {
				cd.load(new Coin(cad, coinDenominations[denomToLoad]));
			}
			denomToLoad++;
		}
                
		// Initialize control and start the session
		control.startSession();
		
		// Initialize test payment objects
		testCoin = new Coin(cad, dollar);
		testBanknote = new Banknote(cad, new BigDecimal(5));
		ExampleItems.updateDatabase();
		
		// Add ink and paper to printer
		station.printer.addInk(1000);
		station.printer.addPaper(1000);
	}
	
	
	 /**
     * Test for paying with less coin than the total order amount.
     * @throws DisabledException Thrown if a component is disabled.
     * @throws CashOverloadException Thrown if there is an issue with cash overload.
     */
	@Test
	public void payLessCoinTest() throws DisabledException, CashOverloadException {
		BigDecimal expectedPendingPayment = new BigDecimal(2);
		
		station.mainScanner.scan(AppleJuice.barcodedItem);
		station.baggingArea.addAnItem(ExampleItems.AppleJuice.barcodedItem);
		control.signalPay();
		station.coinSlot.receive(testCoin);
		assertEquals(expectedPendingPayment, control.getOrder().getTotalUnpaid());
	}
	
	 /**
     * Test for paying with exact coin amount as the total order amount.
     * @throws DisabledException Thrown if a component is disabled.
     * @throws CashOverloadException Thrown if there is an issue with cash overload.
	 * @throws OverloadedDevice 
     */
	@Test
	public void payExactCoinTest() throws DisabledException, CashOverloadException, OverloadedDevice {
		int success = 0;
		int totalRuns = 1000;
		double targetSuccessRate = 0.9;
		for(int i = 0; i < totalRuns; i++) {
			setup();
			BigDecimal expectedPendingPayment = new BigDecimal(0);
			
			station.mainScanner.scan(AppleJuice.barcodedItem);
			station.baggingArea.addAnItem(ExampleItems.AppleJuice.barcodedItem);
			control.signalPay();
			station.coinSlot.receive(testCoin);
			station.coinSlot.receive(testCoin);
			station.coinSlot.receive(testCoin);
			if(expectedPendingPayment.equals(control.getOrder().getTotalUnpaid())) success++;
		}
		assertTrue(success >= totalRuns*targetSuccessRate);
	}
	
	/**
     * Test for paying with less cash (banknote) than the total order amount.
     * @throws DisabledException Thrown if a component is disabled.
     * @throws CashOverloadException Thrown if there is an issue with cash overload.
     */
	@Test
	public void payLessCashTest() throws DisabledException, CashOverloadException {
		BigDecimal expectedPendingPayment = new BigDecimal(1);
		
		station.mainScanner.scan(PeanutButter.barcodedItem);
		station.baggingArea.addAnItem(ExampleItems.PeanutButter.barcodedItem);
		control.signalPay();
		station.banknoteInput.receive(new Banknote(cad, new BigDecimal(5)));
		assertEquals(expectedPendingPayment, control.getOrder().getTotalUnpaid());	
	}
	
    /**
     * Test for paying with exact cash (banknote and coin) amount as the total order amount.
     * @throws DisabledException Thrown if a component is disabled.
     * @throws CashOverloadException Thrown if there is an issue with cash overload.
     */
	@Test
	public void payExactCashTest() throws DisabledException, CashOverloadException {
		BigDecimal expectedPendingPayment = new BigDecimal(0);

		station.mainScanner.scan(PeanutButter.barcodedItem);
		station.baggingArea.addAnItem(ExampleItems.PeanutButter.barcodedItem);
		control.signalPay();
		station.banknoteInput.receive(testBanknote);
		station.coinSlot.receive(testCoin);
		assertEquals(expectedPendingPayment, control.getOrder().getTotalUnpaid());	
	}
	
    /**
     * Test to evaluate the quality of the self-checkout station based on the success rate.
     * @throws DisabledException Thrown if a component is disabled.
     * @throws CashOverloadException Thrown if there is an issue with cash overload.
     * @throws SimulationException Thrown if there is an issue with the simulation.
     * @throws OverloadedDevice Thrown if there is an issue with device initialization.
     */
	@Test
	public void stationGradeQuality() throws DisabledException, CashOverloadException, SimulationException, OverloadedDevice {
		int marginError = 100;
		
		int goldSuccess = testNormalPayments(10000, "gold");
		int silverSuccess = testNormalPayments(10000, "silver");
		int bronzeSuccess = testNormalPayments(10000, "bronze");
		
		assertTrue(goldSuccess+marginError > silverSuccess);
		assertTrue(silverSuccess+marginError > bronzeSuccess);
		
	}
	
    /**
     * Test for normal payments with a specified number of iterations and station grade.
     * @param n Number of iterations.
     * @param stationGrade Grade of the self-checkout station.
     * @return The number of successful payments.
     * @throws DisabledException Thrown if a component is disabled.
     * @throws CashOverloadException Thrown if there is an issue with cash overload.
     * @throws SimulationException Thrown if there is an issue with the simulation.
     * @throws OverloadedDevice Thrown if there is an issue with device initialization.
     */
	public int testNormalPayments(int n, String stationGrade) throws DisabledException, CashOverloadException, SimulationException, OverloadedDevice {
		int success = 0;
		for(int i = 0; i < n; i++) {
			initializeStationOfGrade(stationGrade);
			BigDecimal expectedPendingPayment = new BigDecimal(0);
			station.mainScanner.scan(PeanutButter.barcodedItem);
			station.baggingArea.addAnItem(PeanutButter.barcodedItem);
			control.signalPay();
			station.banknoteInput.receive(testBanknote);
			station.coinSlot.receive(testCoin);
			if(expectedPendingPayment.equals(control.getOrder().getTotalUnpaid())) success++;
		}
		return success;
	}
	
	/**
	 * Initializes the self-checkout station based on the specified grade.
	 * 
	 * @param grade The grade of the self-checkout station ("gold", "silver", or "bronze").
	 * @throws OverloadedDevice Thrown if there is an issue with device initialization.
	 * @throws SimulationException Thrown if there is an issue with the simulation.
	 * @throws CashOverloadException Thrown if there is an issue with cash overload.
	 */
	public void initializeStationOfGrade(String grade) throws OverloadedDevice, SimulationException, CashOverloadException {
		// Set denominations to reflect Canadian currencies
		AbstractSelfCheckoutStation.resetConfigurationToDefaults();
		BigDecimal[] banknoteDenominations = {new BigDecimal(5), new BigDecimal(10), new BigDecimal(20), new BigDecimal(50), new BigDecimal(100)};
        AbstractSelfCheckoutStation.configureBanknoteDenominations(banknoteDenominations);
        BigDecimal[] coinDenominations = {new BigDecimal(0.01), new BigDecimal(0.05), new BigDecimal(0.1), new BigDecimal(0.25), new BigDecimal(1), new BigDecimal(2)};
        AbstractSelfCheckoutStation.configureCoinDenominations(coinDenominations); 
        
		// initialize station and turn on required components
        if (grade == "gold") station = new SelfCheckoutStationGold();
        else if (grade == "silver") station = new SelfCheckoutStationSilver();
        else station = new SelfCheckoutStationBronze();
		control = new CustomerStationControl(station);
		PowerGrid.engageUninterruptiblePowerSource();
		station.plugIn(PowerGrid.instance());
		station.turnOn();

		// load cash dispensers  
		for(IBanknoteDispenser bd : station.banknoteDispensers.values())
            bd.load(new Banknote(cad, new BigDecimal(5)));
            
		for(ICoinDispenser cd : station.coinDispensers.values())
            cd.load(new Coin(cad, new BigDecimal(1)));
                
		// Initialize control and start the session
		control.startSession();
		
		// Initialize test payment objects
		testCoin = new Coin(cad, dollar);
		testBanknote = new Banknote(cad, new BigDecimal(5));
		ExampleItems.updateDatabase();
		
		// Add ink and paper to printer
		station.printer.addInk(1000);
		station.printer.addPaper(1000);
	}
}