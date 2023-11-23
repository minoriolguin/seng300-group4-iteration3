package com.thelocalmarketplace.software.test.logic;

import static org.junit.Assert.assertTrue;
import com.thelocalmarketplace.software.logic.StateLogic.States;


import java.math.BigDecimal;
import java.util.Currency;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.tdc.CashOverloadException;
import com.tdc.DisabledException;
import com.tdc.banknote.Banknote;
import com.tdc.coin.Coin;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.hardware.SelfCheckoutStationBronze;
import com.thelocalmarketplace.software.logic.CentralStationLogic;
import com.thelocalmarketplace.software.logic.CentralStationLogic.PaymentMethods;


import ca.ucalgary.seng300.simulation.SimulationException;
import powerutility.PowerGrid;

/**
 * @author Tara Strickland (10105877)
 * ----------------------------------
 * @author Angelina Rochon (30087177)
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
public class CentralStationLogicTest {
	
	SelfCheckoutStationBronze station;
	CentralStationLogic session;
	Currency currency;

	private Coin fiveCentCoin;
	private Coin twentyFiveCentCoin;
	
	private Banknote fiveDollarBill;
	private Banknote tenDollarBill;
	private Banknote twentyDollarBill;
	Banknote[] banknoteList;
	
	private BigDecimal[] denominationsCoinCAD = new BigDecimal[] {
			new BigDecimal(0.05),
			new BigDecimal(0.25),
			new BigDecimal(1.00)
	};
	private BigDecimal[] denominationsBanknotesCAD = new BigDecimal[] {
			new BigDecimal(5.00),
			new BigDecimal(10.00),
			new BigDecimal(20.00)
	};
	@Before
	public void init() {
		PowerGrid.engageUninterruptiblePowerSource();
		PowerGrid.instance().forcePowerRestore();
		
		
		this.currency = Currency.getInstance("CAD");
		
		AbstractSelfCheckoutStation.resetConfigurationToDefaults();
		AbstractSelfCheckoutStation.configureCoinDenominations(denominationsCoinCAD);
		AbstractSelfCheckoutStation.configureCoinDispenserCapacity(10);
		AbstractSelfCheckoutStation.configureCoinStorageUnitCapacity(10);
		AbstractSelfCheckoutStation.configureCoinTrayCapacity(10);
		AbstractSelfCheckoutStation.configureCurrency(currency);
		AbstractSelfCheckoutStation.configureBanknoteDenominations(denominationsBanknotesCAD);
		AbstractSelfCheckoutStation.configureBanknoteStorageUnitCapacity(10);
		
		station = new SelfCheckoutStationBronze();
		
		currency = Currency.getInstance("CAD");
		fiveCentCoin = new Coin(currency,new BigDecimal(0.05));
		twentyFiveCentCoin = new Coin(currency,new BigDecimal(0.25));
		
		fiveDollarBill = new Banknote(currency, new BigDecimal(5.0));
		tenDollarBill = new Banknote(currency, new BigDecimal(10.0));
		twentyDollarBill = new Banknote(currency, new BigDecimal(20.0));
		
		
		station.plugIn(PowerGrid.instance());
		station.turnOn();

		session = new CentralStationLogic(station);
	}
	
	@Test public void startSessionStateNormalTest() {
		session.startSession();
		assertTrue("station did not start in noraml state",session.stateLogic.inState(States.NORMAL));
	}
	@Test public void startSessionPaymentMethodTest() {
		session.selectPaymentMethod(PaymentMethods.CASH);
		session.startSession();
		assertTrue("station did not set payment methods correctly", session.getSelectedPaymentMethod()==PaymentMethods.CASH);
	}@Test public void hasStartedTest() {
		session.startSession();
		
		assertTrue("session did not start", session.isSessionStarted());
	}@Test public void hasStoppedTest() {
		session.startSession();
		session.stopSession();
		assertTrue("session did not stop", !session.isSessionStarted());
	}@Test(expected = SimulationException.class) public void canStartWhenStarted() {
		session.startSession();
		session.startSession();
		
	}@Test(expected = NullPointerException.class) public void testNullHardware() {
		session = new CentralStationLogic(null);
	}@Test public void getAllCoinsInDispenserTest() throws CashOverloadException, DisabledException {
		session.startSession();
		session.hardware.coinDispensers.get(new BigDecimal(0.05)).receive(fiveCentCoin);
		session.hardware.coinDispensers.get(new BigDecimal(0.05)).receive(fiveCentCoin);
		session.hardware.coinDispensers.get(new BigDecimal(0.25)).receive(twentyFiveCentCoin);
		Map<BigDecimal, Integer> result = session.getAvailableCoinsInDispensers();
		assertTrue("did not get all coins in dispenser", result.get(new BigDecimal(0.05)) == 2 && result.get(new BigDecimal(0.25))==1);
	}@Test public void  getAllBanknotesInDispenserTest() throws CashOverloadException, DisabledException {
		session.startSession();
		banknoteList = new Banknote[]{tenDollarBill,tenDollarBill};
		
		Banknote[] fiveList = new Banknote[] {fiveDollarBill};
		Banknote[] twentyList = new Banknote[] {twentyDollarBill, twentyDollarBill, twentyDollarBill};
		
		session.hardware.banknoteDispensers.get(new BigDecimal(10.00)).load(banknoteList);
		session.hardware.banknoteDispensers.get(new BigDecimal(5.00)).load(fiveList);
		session.hardware.banknoteDispensers.get(new BigDecimal(20.00)).load(twentyList);
		Map<BigDecimal, Integer> result = session.getAvailableBanknotesInDispensers();
		assertTrue("did not get all coins in dispenser", result.get(new BigDecimal(5.0)) == 1 && result.get(new BigDecimal(10.0))==2 && result.get(new BigDecimal(20.0))==3);
	}
	
}
