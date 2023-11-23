package com.thelocalmarketplace.software.test.controllers;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Before;
import org.junit.Test;

import com.tdc.CashOverloadException;
import com.tdc.DisabledException;
import com.tdc.banknote.Banknote;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.hardware.SelfCheckoutStationBronze;
import com.thelocalmarketplace.software.logic.CentralStationLogic;
import com.thelocalmarketplace.software.logic.CentralStationLogic.PaymentMethods;
import com.thelocalmarketplace.software.logic.StateLogic.States;

import ca.ucalgary.seng300.simulation.SimulationException;
import powerutility.PowerGrid;


public class PayByCashTests {
	private CentralStationLogic logicBronze;
	
	private BigDecimal tenDollar;
	private BigDecimal fiveDollar;
	private BigDecimal twoDollar;
	private BigDecimal dollar;
	private BigDecimal quarter;
	private BigDecimal halfDollar;
	private BigDecimal[] coinList;
	private BigDecimal[] noteList;
	
	private SelfCheckoutStationBronze stationBronze;
	
	private Currency cad;
	private Banknote fiveNote;
	private Banknote tenNote;
	
	@Before
	public void setup() {
		PowerGrid.engageUninterruptiblePowerSource();
		PowerGrid.instance().forcePowerRestore();
		
		dollar = new BigDecimal(1);
		halfDollar = new BigDecimal(0.50);
		quarter = new BigDecimal(0.25);
		twoDollar = new BigDecimal(2);
		fiveDollar = new BigDecimal(5);
		tenDollar = new BigDecimal(10);
		coinList = new BigDecimal[] {quarter, halfDollar, dollar};
		noteList = new BigDecimal[] {twoDollar, fiveDollar, tenDollar};
		
		cad = Currency.getInstance("CAD"); 
		
		fiveNote = new Banknote(cad, fiveDollar);
		tenNote = new Banknote(cad, tenDollar);
		
		AbstractSelfCheckoutStation.configureCurrency(cad);
		AbstractSelfCheckoutStation.configureCoinDenominations(coinList);
		AbstractSelfCheckoutStation.configureBanknoteDenominations(noteList);
		AbstractSelfCheckoutStation.configureBanknoteStorageUnitCapacity(10);
		AbstractSelfCheckoutStation.configureCoinStorageUnitCapacity(10);
		AbstractSelfCheckoutStation.configureCoinTrayCapacity(5);
		AbstractSelfCheckoutStation.configureCoinDispenserCapacity(5);
		
		stationBronze = new SelfCheckoutStationBronze();
		logicBronze = new CentralStationLogic(stationBronze);
		
		stationBronze.plugIn(PowerGrid.instance());
		stationBronze.turnOn();
		
		//Map<BigDecimal, Integer> dispensable = new HashMap<>();
		//dispensable.put(new BigDecimal(0.05), 1); // 1 nickel can be dispensed
		
		//PayByCoinTests.initCoinDispensers(stationBronze, null);
	}
	
	public static void initBanknoteDispensers(AbstractSelfCheckoutStation hardware, Map<BigDecimal, Integer> banknoteAmounts) throws SimulationException, CashOverloadException {
		for (Entry<BigDecimal, Integer> c : banknoteAmounts.entrySet()) {
			for (int j = 0; j < c.getValue(); j++) {	
				hardware.banknoteDispensers.get(c.getKey()).load(new Banknote(Currency.getInstance("CAD"), c.getKey()));;
			}
		}
	}
	
	@Test
	public void processZeroChange() {
		BigDecimal expected = new BigDecimal(0);
		BigDecimal actual1 = this.logicBronze.cashPaymentController.processCashChange(expected);
		
		assertEquals(expected, actual1);
	}
	
	
	@Test(expected=SimulationException.class)
	public void testInsertBanknoteSessionNotStarted() throws DisabledException, CashOverloadException {
		while (true) {
			setup();
			
			logicBronze.stopSession();
			logicBronze.cartLogic.updateBalance(new BigDecimal(50));
			stationBronze.banknoteInput.receive(fiveNote);
		}
	}
	
	@Test(expected=SimulationException.class)
	public void testGoodBanknoteInvalidState() throws DisabledException, CashOverloadException {
		while (true) {
			setup();
			
			logicBronze.startSession();
			logicBronze.stateLogic.gotoState(States.NORMAL);
			logicBronze.cartLogic.updateBalance(new BigDecimal(50));
			stationBronze.banknoteInput.receive(fiveNote);
		}
	}
	
	@Test(expected=SimulationException.class)
	public void testGoodBanknoteInvalidPaymentMethod() throws DisabledException, CashOverloadException {
		while (true) {
			setup();
			
			logicBronze.startSession();
			logicBronze.stateLogic.gotoState(States.CHECKOUT);
			logicBronze.selectPaymentMethod(PaymentMethods.DEBIT);
			logicBronze.cartLogic.updateBalance(new BigDecimal(50));
			stationBronze.banknoteInput.receive(fiveNote);
		}
	}
	

	@Test(expected = SimulationException.class)
	public void testGoodBanknotePayFullAmount() throws DisabledException, CashOverloadException {
		while (true) {
			setup();
			
			logicBronze.startSession();
			logicBronze.stateLogic.gotoState(States.CHECKOUT);
			logicBronze.selectPaymentMethod(PaymentMethods.CASH);
			logicBronze.cartLogic.updateBalance(new BigDecimal(0));
			stationBronze.banknoteInput.receive(fiveNote);
		}
	}
	
	@Test
	public void testInsertGoodBanknoteToPayNotFull() throws DisabledException, CashOverloadException {
		do {
			setup();
			
			logicBronze.startSession();
			logicBronze.stateLogic.gotoState(States.CHECKOUT);
			logicBronze.selectPaymentMethod(PaymentMethods.CASH);
		    logicBronze.cartLogic.updateBalance(new BigDecimal(50));
		    
		    stationBronze.banknoteInput.receive(fiveNote);
		    stationBronze.banknoteInput.receive(fiveNote);
		}
		while (logicBronze.cartLogic.getBalanceOwed().intValue() != 40);
		
	    assertEquals(40, logicBronze.cartLogic.getBalanceOwed().intValue());
	}
	
	@Test
	public void testBanknoteEmitSuccess() throws DisabledException, CashOverloadException {
		do {
			setup();
			
			Map<BigDecimal, Integer> dispensable = new HashMap<>();
			dispensable.put(new BigDecimal(5), 1);
			
			initBanknoteDispensers(stationBronze, dispensable);
			
			logicBronze.startSession();
			logicBronze.stateLogic.gotoState(States.CHECKOUT);
			logicBronze.selectPaymentMethod(PaymentMethods.CASH);
			logicBronze.cartLogic.updateBalance(new BigDecimal(5));
			
			stationBronze.banknoteInput.receive(tenNote);
			
		}
		while (logicBronze.cartLogic.getBalanceOwed().intValue() != 0);
			
		assertEquals(0, logicBronze.cartLogic.getBalanceOwed().intValue());
	}
	
	@Test
	public void testBanknoteEmitFail() throws DisabledException, CashOverloadException {
		do {
			setup();
			
			Map<BigDecimal, Integer> dispensable = new HashMap<>();
			dispensable.put(new BigDecimal(2), 1);
			
			initBanknoteDispensers(stationBronze, dispensable);
			
			logicBronze.startSession();
			logicBronze.stateLogic.gotoState(States.CHECKOUT);
			logicBronze.selectPaymentMethod(PaymentMethods.CASH);
			logicBronze.cartLogic.updateBalance(new BigDecimal(5));
			
			// Overload banknote output channel
			for (int i = 0; i < 20; i++) {
				stationBronze.banknoteOutput.receive(fiveNote);
			}
			
			stationBronze.banknoteInput.receive(tenNote);
		}
		while (logicBronze.cartLogic.getBalanceOwed().intValue() != 0);
	    
	    assertEquals(0, logicBronze.cartLogic.getBalanceOwed().intValue());
	}
	
	@Test
	public void testGoodBanknoteNoChangeNeeded() throws SimulationException, CashOverloadException, DisabledException {
		do {
			setup();
			
			logicBronze.startSession();
			logicBronze.stateLogic.gotoState(States.CHECKOUT);
			logicBronze.selectPaymentMethod(PaymentMethods.CASH);
			logicBronze.cartLogic.updateBalance(new BigDecimal(5));
			stationBronze.banknoteInput.receive(fiveNote);
		}
		while (0 != logicBronze.cartLogic.getBalanceOwed().intValue());
		
		assertEquals(0, logicBronze.cartLogic.getBalanceOwed().intValue());
	}
	
	@Test
	public void testGoodBanknoteOverpaymentWithInsufficientChange() throws DisabledException, CashOverloadException {
		do {
			setup();
			
			logicBronze.startSession();
			logicBronze.stateLogic.gotoState(States.CHECKOUT);
			logicBronze.selectPaymentMethod(PaymentMethods.CASH);
			logicBronze.cartLogic.updateBalance(new BigDecimal(5));
			stationBronze.banknoteInput.receive(tenNote);
		}
		while (0 != logicBronze.cartLogic.getBalanceOwed().intValue());
		
		assertEquals(0, logicBronze.cartLogic.getBalanceOwed().intValue());
	}

	
	@Test
	public void badBanknoteTest() {
		this.logicBronze.cashPaymentController.badBanknote(stationBronze.banknoteValidator);
	}
}	
