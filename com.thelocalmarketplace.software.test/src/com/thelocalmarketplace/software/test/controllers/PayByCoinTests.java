package com.thelocalmarketplace.software.test.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Before;
import org.junit.Test;

import com.jjjwelectronics.Mass;
import com.jjjwelectronics.Numeral;
import com.jjjwelectronics.OverloadedDevice;
import com.jjjwelectronics.scanner.Barcode;
import com.jjjwelectronics.scanner.BarcodedItem;
import com.tdc.CashOverloadException;
import com.tdc.DisabledException;
import com.tdc.coin.Coin;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.SelfCheckoutStationBronze;
import com.thelocalmarketplace.hardware.external.ProductDatabases;
import com.thelocalmarketplace.software.controllers.pay.cash.CoinDispenserController;
import com.thelocalmarketplace.software.controllers.pay.cash.CoinPaymentController;
import com.thelocalmarketplace.software.logic.CentralStationLogic;
import com.thelocalmarketplace.software.logic.CentralStationLogic.PaymentMethods;
import com.thelocalmarketplace.software.logic.StateLogic.States;

import ca.ucalgary.seng300.simulation.SimulationException;
import powerutility.PowerGrid;

/**
 * @author Connell Reffo (10186960)
 * --------------------------------
 * @author Angelina Rochon (30087177)
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
public class PayByCoinTests {
	
	private SelfCheckoutStationBronze hardware;
	private CentralStationLogic logic;
	
	private Currency currency;
	
	private Coin fiveCentCoin;
	private Coin twentyFiveCentCoin;
	private Coin oneDollarCoin;
	private Coin invalidCoin;
	
	private BarcodedItem bitem;
	private Mass itemMass;

	private Numeral[] barcode_numeral;
	private BarcodedProduct product;
	private Barcode barcode;
	
	private Numeral[] barcode_numeral2;
	private BarcodedProduct product2;
	private Barcode barcode2;
	
	/**
	 * Array of available coin denominations to test with
	 * {0.05, 0.10, 0.25, 1.00, 2.00}
	 */
	private BigDecimal[] denominationsCAD = new BigDecimal[] {
			new BigDecimal(0.05),
			new BigDecimal(0.10),
			new BigDecimal(0.25),
			new BigDecimal(1.00),
			new BigDecimal(2.00)
	};
	
	
	/**
	 * Helper method for initializing coins into the coin dispensers
	 * @param hardware Is the hardware reference
	 * @param coinAmounts Is the amount of coins per denomination of coin dispenser
	 * @throws CashOverloadException 
	 * @throws SimulationException 
	 */
	public static void initCoinDispensers(AbstractSelfCheckoutStation hardware, Map<BigDecimal, Integer> coinAmounts) throws SimulationException, CashOverloadException {
		for (Entry<BigDecimal, Integer> c : coinAmounts.entrySet()) {
			for (int j = 0; j < c.getValue(); j++) {					
				hardware.coinDispensers.get(c.getKey()).load(new Coin(Currency.getInstance("CAD"), c.getKey()));;
			}
		}
	}
	
	@Before
	public void init() throws SimulationException, CashOverloadException, OverloadedDevice {
		PowerGrid.engageUninterruptiblePowerSource();
		PowerGrid.instance().forcePowerRestore();
		
		this.currency = Currency.getInstance("CAD");
		this.invalidCoin = new Coin(currency,new BigDecimal(0.35));
		this.fiveCentCoin = new Coin(currency,new BigDecimal(0.05));
		this.twentyFiveCentCoin = new Coin(currency,new BigDecimal(0.25));
		this.oneDollarCoin = new Coin(currency,new BigDecimal(1.00));
		
		this.barcode_numeral = new Numeral[] {Numeral.one, Numeral.two, Numeral.three};
		this.barcode_numeral2 = new Numeral[] {Numeral.two, Numeral.two, Numeral.one};
		
		this.barcode = new Barcode(barcode_numeral);
		this.barcode2 = new Barcode(barcode_numeral2);
		this.product = new BarcodedProduct(barcode, "some item", 6, 3.0);
		this.product2 = new BarcodedProduct(barcode2, "some item 2", 20, 3000.0);
		
		ProductDatabases.BARCODED_PRODUCT_DATABASE.clear();
		ProductDatabases.INVENTORY.clear();
		
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode, product);
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode2, product2);
		ProductDatabases.INVENTORY.put(product, 1);
		ProductDatabases.INVENTORY.put(product2, 1);
		
		AbstractSelfCheckoutStation.resetConfigurationToDefaults();
		AbstractSelfCheckoutStation.configureCoinDenominations(denominationsCAD);
		AbstractSelfCheckoutStation.configureCoinDispenserCapacity(10);
		AbstractSelfCheckoutStation.configureCoinStorageUnitCapacity(10);
		AbstractSelfCheckoutStation.configureCoinTrayCapacity(10);
		AbstractSelfCheckoutStation.configureCurrency(currency);
		
		this.hardware = new SelfCheckoutStationBronze();
		this.hardware.plugIn(PowerGrid.instance());
		this.hardware.turnOn();
		
		this.hardware.printer.addPaper(100);
		this.hardware.printer.addInk(1000);
		
		this.logic = new CentralStationLogic(hardware);
		this.logic.selectPaymentMethod(PaymentMethods.CASH);
	}
	
	/**
	 * Test for AbstractLogicDependant
	 */
	@Test(expected = NullPointerException.class)
	public void testNullLogicInConstructor() {
		new CoinPaymentController(null);
	}
	
	@Test
	public void testInsertValidCoin() throws DisabledException, CashOverloadException {
		this.logic.startSession();
		this.logic.stateLogic.gotoState(States.CHECKOUT);
		
		this.logic.cartLogic.updateBalance(new BigDecimal(6));
		
		do {
			try {
				this.hardware.coinSlot.sink.receive(oneDollarCoin);
			}
			catch (Exception e) {}
		}
		while (logic.cartLogic.getBalanceOwed().intValue() != 5);
		
		// expected price of cart 6 - 1 = 5
		assertEquals(5, this.logic.cartLogic.getBalanceOwed().intValue());
	}
	
	@Test
	public void testInsertInvalidCoin() throws SimulationException, CashOverloadException, OverloadedDevice {
		boolean canStop = false;
		
		while (!canStop) {
			this.init();
			
			this.logic.startSession();
			this.itemMass = new Mass((double) 3.0);
			this.bitem = new BarcodedItem(this.barcode, this.itemMass);
			
			this.hardware.handheldScanner.scan(this.bitem);
			this.hardware.baggingArea.addAnItem(this.bitem);
			
			// Bypass weight discrepancy
			this.hardware.coinSlot.enable();
			this.hardware.coinValidator.enable();
			
			try {				
				// Nothing should really happen here
				this.hardware.coinSlot.sink.receive(this.invalidCoin);
				canStop = true;
			}
			catch (Exception e) {
				canStop = false;
			}
		}
		
	}
	
	@Test(expected = SimulationException.class)
	public void testInsertValidCoinSessionNotStarted() throws CashOverloadException, DisabledException {
		this.logic.stopSession();
		
		this.itemMass = new Mass((double) 3.0);
		this.bitem = new BarcodedItem(this.barcode, this.itemMass);
		
		this.hardware.coinSlot.sink.receive(this.oneDollarCoin);
	}
	
	@Test(expected = SimulationException.class)
	public void insertValidCoinNotInCheckout() throws DisabledException, CashOverloadException {
		this.logic.startSession();
		
		// Not in checkout state
		this.hardware.coinSlot.sink.receive(this.oneDollarCoin);
	}
	
	@Test(expected = SimulationException.class)
	public void insertValidCoinNotSelectedCoinAsPaymentMethod() throws DisabledException, CashOverloadException {
		this.logic.startSession();
		this.logic.stateLogic.gotoState(States.CHECKOUT);
		
		// Change selected payment
		this.logic.selectPaymentMethod(PaymentMethods.NONE);
		
		this.hardware.coinSlot.sink.receive(this.oneDollarCoin);
	}
	
	@Test
	public void testInsertValidCoinTwoCoinsSame() throws DisabledException, CashOverloadException, SimulationException, OverloadedDevice {
		do {
			this.init();
			
			this.logic.startSession();
			this.logic.cartLogic.updateBalance(new BigDecimal(20));
			
			this.logic.stateLogic.gotoState(States.CHECKOUT);
			
			this.hardware.coinSlot.sink.receive(this.oneDollarCoin);
			this.hardware.coinSlot.sink.receive(this.oneDollarCoin);
		}	
		while (this.logic.cartLogic.getBalanceOwed().intValue() != 18);
		
		// Expected price of cart 20 - 1 - 1
		assertEquals(18, this.logic.cartLogic.getBalanceOwed().intValue());
	}
	
	@Test
	public void testInsertValidCoinTwoCoinsDifferent() throws DisabledException, CashOverloadException, SimulationException, OverloadedDevice {
		do {
			this.init();
			
			this.logic.startSession();
			this.logic.cartLogic.updateBalance(new BigDecimal(20));
			
			this.logic.stateLogic.gotoState(States.CHECKOUT);
			
			this.hardware.coinSlot.sink.receive(this.oneDollarCoin);
			this.hardware.coinSlot.sink.receive(this.fiveCentCoin);
		}
		while (this.logic.cartLogic.getBalanceOwed().setScale(5, RoundingMode.HALF_DOWN).compareTo(new BigDecimal(18.95).setScale(5, RoundingMode.HALF_DOWN)) != 0);
		
		//expected price of cart 20 - 1 - 0.05 = 18.95
		assertEquals(this.logic.cartLogic.getBalanceOwed().setScale(5, RoundingMode.HALF_DOWN), new BigDecimal(18.95).setScale(5, RoundingMode.HALF_DOWN));
	}
	
	@Test
	public void testInsertValidCoinsToPayWithoutChangeRequired() throws CashOverloadException, DisabledException, SimulationException, OverloadedDevice {
		do {
			this.init();
			
			this.logic.startSession();
			this.logic.cartLogic.updateBalance(new BigDecimal(6));
			
			this.logic.stateLogic.gotoState(States.CHECKOUT);
			
			for (int i = 0; i < 6; i++) {			
				this.hardware.coinSlot.sink.receive(this.oneDollarCoin);
			}
		}
		while (BigDecimal.ZERO.setScale(5, RoundingMode.HALF_DOWN).compareTo(this.logic.cartLogic.getBalanceOwed().setScale(5, RoundingMode.HALF_DOWN)) != 0);

		// Expected price of cart $6 - 6($1.00) = $0
		assertEquals(BigDecimal.ZERO.setScale(5, RoundingMode.HALF_DOWN), this.logic.cartLogic.getBalanceOwed().setScale(5, RoundingMode.HALF_DOWN));
	}
	
	@Test(expected = SimulationException.class)
	public void testIntentionalOverpayWithValidCoins() throws CashOverloadException, DisabledException, SimulationException, OverloadedDevice {		
		while (true) {
			this.init();
			
			this.logic.startSession();
			this.logic.cartLogic.updateBalance(new BigDecimal(6));
			
			this.logic.stateLogic.gotoState(States.CHECKOUT);
			
			for (int i = 0; i < 7; i++) {		
				this.hardware.coinSlot.sink.receive(this.oneDollarCoin);
			}
		}
	}
	
	@Test
	public void testRequiredChangeButNotAvailable() throws SimulationException, CashOverloadException, DisabledException, OverloadedDevice {
		do {
			this.init();
			
			this.logic.startSession();
			this.logic.stateLogic.gotoState(States.CHECKOUT);
			
			this.logic.cartLogic.updateBalance(new BigDecimal(6));
			
			// Insert $5.75
			for (int i = 0; i < 5; i++) {			
				this.hardware.coinSlot.receive(oneDollarCoin);
			}
			
			for (int i = 0; i < 3; i++) {			
				this.hardware.coinSlot.receive(twentyFiveCentCoin);
			}
			
			// Unload all 25 cent coins so none are available to be dispensed
			this.hardware.coinDispensers.get(twentyFiveCentCoin.getValue()).unload();
			
			// Insert another dollar. $5.75 + $1.00 = $6.75. So $0.75 is the change required
			this.hardware.coinValidator.receive(oneDollarCoin);
		}
		while (!this.logic.stateLogic.inState(States.SUSPENDED));
		
		assertTrue(this.logic.stateLogic.inState(States.SUSPENDED));
	}
	
	@Test
	public void testProcessChangeWithNoChange() {
		assertEquals(BigDecimal.ZERO, this.logic.coinPaymentController.processCoinChange(BigDecimal.ZERO));
	}
	
	@Test
	public void testProcessChangeWithUnavailableChange() throws SimulationException, CashOverloadException {
		Map<BigDecimal, Integer> dispensable = new HashMap<>();
		dispensable.put(new BigDecimal(0.05), 1); // 1 nickel can be dispensed
		
		initCoinDispensers(this.hardware, dispensable);
		
		BigDecimal missed = this.logic.coinPaymentController.processCoinChange(new BigDecimal(0.10));
		
		// Attempt to process change for 10 cents required but only 5 cents available. So 5 cents should be returned as missed
		assertEquals(new BigDecimal(0.05).setScale(5, RoundingMode.HALF_DOWN), missed.setScale(5, RoundingMode.HALF_DOWN));
	}
	
	@Test
	public void testProcessChangeWithAvailableChange() throws SimulationException, CashOverloadException {
		Map<BigDecimal, Integer> dispensable = new HashMap<>();
		dispensable.put(new BigDecimal(0.05), 2); // 2 nickels can be dispensed
		
		initCoinDispensers(this.hardware, dispensable);
		
		BigDecimal missed = this.logic.coinPaymentController.processCoinChange(new BigDecimal(0.10));
		
		// No change should be missed
		assertEquals(BigDecimal.ZERO.setScale(5, RoundingMode.HALF_DOWN), missed.setScale(5, RoundingMode.HALF_DOWN));
	}
	
	@Test
	public void testProcessChangeWithDisabledCoinDispenser() throws SimulationException, CashOverloadException {
		Map<BigDecimal, Integer> dispensable = new HashMap<>();
		dispensable.put(new BigDecimal(0.05), 2); // 2 nickels can be dispensed
		
		initCoinDispensers(this.hardware, dispensable);
		
		// Disable nickel dispenser
		this.hardware.coinDispensers.get(new BigDecimal(0.05)).disable();
		
		BigDecimal missed = this.logic.coinPaymentController.processCoinChange(new BigDecimal(0.10));
		
		// All change should be missed
		assertEquals(new BigDecimal(0.10).setScale(5, RoundingMode.HALF_DOWN), missed.setScale(5, RoundingMode.HALF_DOWN));
	}
	
	@Test
	public void testProcessChangeWithOverloadedCoinTray() throws SimulationException, CashOverloadException, DisabledException {
		Map<BigDecimal, Integer> dispensable = new HashMap<>();
		dispensable.put(new BigDecimal(0.05), 2); // 2 nickels can be dispensed
		
		initCoinDispensers(this.hardware, dispensable);
		
		// Overload coin tray (capacity is 10)
		for (int i = 0; i < 10; i++) {			
			this.hardware.coinTray.receive(this.fiveCentCoin);
		}
		
		BigDecimal missed = this.logic.coinPaymentController.processCoinChange(new BigDecimal(0.10));
		
		// All change should be missed
		assertEquals(new BigDecimal(0.10).setScale(5, RoundingMode.HALF_DOWN), missed.setScale(5, RoundingMode.HALF_DOWN));
	}
	
	@Test(expected = NullPointerException.class)
	public void testNullDenominationInConstructorForCoinDispenserController() {
		new CoinDispenserController(logic, null);
	}
	
	@Test
	public void testAddAndUnloadCoinsForCoinDispenserController() throws DisabledException, CashOverloadException {
		
		// Add 1 nickel to the nickel dispenser
		this.hardware.coinDispensers.get(new BigDecimal(0.05)).receive(fiveCentCoin);
		
		// Unload
		this.hardware.coinDispensers.get(new BigDecimal(0.05)).unload();
		
		// Should be 0 nickels recorded in nickel dispenser controller
		assertEquals(0, this.logic.coinDispenserControllers.get(new BigDecimal(0.05)).getAvailableChange().size());
	}
}
