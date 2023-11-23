package com.thelocalmarketplace.software.logic;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.hardware.external.CardIssuer;
import com.thelocalmarketplace.software.controllers.*;
import com.thelocalmarketplace.software.controllers.pay.CardReaderController;
import com.thelocalmarketplace.software.controllers.pay.cash.BanknoteDispenserController;
import com.thelocalmarketplace.software.controllers.pay.cash.CashPaymentController;
import com.thelocalmarketplace.software.controllers.pay.cash.CoinDispenserController;
import com.thelocalmarketplace.software.controllers.pay.cash.CoinPaymentController;
import com.thelocalmarketplace.software.logic.StateLogic.States;
import com.thelocalmarketplace.software.controllers.item.*;

import ca.ucalgary.seng300.simulation.InvalidStateSimulationException;
import ca.ucalgary.seng300.simulation.SimulationException;

/**
 * Represents the central session logic for the control software
 * 
 * Codebase originated from Connell's project
 * Combined idea of having session handling part of central logic unit from Braden's project
 * 
 * @author Connell Reffo (10186960)
 * @author Tara Strickland (10105877)
 * @author Angelina Rochon (30087177)
 * @author Julian Fan (30235289)
 * @author Braden Beler (30084941)
 * @author Samyog Dahal (30194624)
 * @author Maheen Nizmani (30172615)
 * @author Phuong Le (30175125)
 * @author Daniel Yakimenka (10185055)
 * @author Merick Parkinson (30196225)
 * @author Farida Elogueil (30171114)
 */
public class CentralStationLogic {
	
	/*
	 * Enumeration of possible payment methods
	 */
	public enum PaymentMethods {
		NONE, // Default
		CREDIT,
		DEBIT,
		CASH
	}
	
	
	/**
	 * Reference to physical hardware
	 */
	public AbstractSelfCheckoutStation hardware;
	
	/**
	 * Instance of the cart logic
	 */
	public CartLogic cartLogic;
	
	/**
	 * Instance of the currency logic for coin denominations
	 */
	public CurrencyLogic coinCurrencyLogic;
	
	/**
	 * Instance of the currency logic for banknote denominations
	 */
	public CurrencyLogic banknoteCurrencyLogic;
	
	/**
	 * Instance of the controller that handles payment with coin
	 */
	public CoinPaymentController coinPaymentController;
	
	/**
	 * Instance of the controller that handles payment with cash
	 */
	public CashPaymentController cashPaymentController;
	
	/**
	 * Instances of the controllers that handle coin dispensing indexed by their corresponding coin denomination
	 */
	public Map<BigDecimal, CoinDispenserController> coinDispenserControllers = new HashMap<>();
	
	/**
	 * Instances of the controllers that handle banknote dispensing indexed by their corresponding banknote denomination
	 */
	public Map<BigDecimal, BanknoteDispenserController> banknoteDispenserControllers = new HashMap<>();
	
	/**
	 * Instance of the controller that handles adding barcoded product
	 */
	public AddBarcodedItemController addBarcodedProductController;
	
	/** 
	 * Instance of weight logic 
	 */
	public WeightLogic weightLogic;
	
	/**
	 * Instance of add bags logic 
	 */
	public AddBagsLogic addBagsLogic;
	
	/*
	 * Instance of logic that handles item removal
	 */
	public RemoveItemLogic removeItemLogic;
	
	/**
	 * Instance of the controller that handles weight discrepancy detected
	 */
	public WeightDiscrepancyController weightDiscrepancyController;

	/**
	 * Instance of controller that handles swiping a card
	 */
	public CardReaderController cardReaderController;
	
	/**
	 * Instance of the controller that handles receipt printing
	 */
	public ReceiptPrintingController receiptPrintingController;
	
	/**
	 * Instance of logic for attendant
	 */
	public AttendantLogic attendantLogic;

  /**
   * Instance of logic for card payment via swipe
   */
	public CardSwipeLogic cardLogic;
	
	/**
	 * Instance of logic for states
	 */
	public StateLogic stateLogic;
	
	/**
	 * Current selected payment method
	 */
	private PaymentMethods paymentMethod;
	
	/**
	 * Tracks if the customer session is active
	 */
	private boolean sessionStarted;
	
	
	/**
	 * Base constructor for a new CentralStationLogic instance
	 * @throws NullPointerException If hardware is null
	 */
	public CentralStationLogic(AbstractSelfCheckoutStation hardware) throws NullPointerException {
		if (hardware == null) {
			throw new NullPointerException("Hardware");
		}
		
		this.hardware = hardware;
		
		this.sessionStarted = false;
		this.paymentMethod = PaymentMethods.NONE;
		
		// Reference to logic objects
		this.cartLogic = new CartLogic();
		this.weightLogic = new WeightLogic(this);
		this.stateLogic = new StateLogic(this);

		// Instantiate each controller
		this.coinPaymentController = new CoinPaymentController(this);
		this.cashPaymentController = new CashPaymentController(this);
		this.addBarcodedProductController = new AddBarcodedItemController(this);
		this.weightDiscrepancyController = new WeightDiscrepancyController(this);
		this.cardReaderController = new CardReaderController(this);
		this.receiptPrintingController = new ReceiptPrintingController(this);
		this.attendantLogic = new AttendantLogic(this);
		this.addBagsLogic = new AddBagsLogic(this);
		this.removeItemLogic = new RemoveItemLogic(this);
		
		this.coinCurrencyLogic = new CurrencyLogic(this.hardware.coinDenominations);
		this.banknoteCurrencyLogic = new CurrencyLogic(this.hardware.banknoteDenominations);
		
		this.setupCoinDispenserControllers(this.coinCurrencyLogic.getDenominationsAsList());
		this.setupBanknoteDispenserControllers(this.banknoteCurrencyLogic.getDenominationsAsList());
	}
	
	/**
	 * Gets the current selected payment method
	 * @return the payment method
	 */
	public PaymentMethods getSelectedPaymentMethod() {
		return this.paymentMethod;
	}
	
	/**
	 * Sets the desired payment method for the customer
	 * @param method Is the payment method to use
	 */
	public void selectPaymentMethod(PaymentMethods method) {
		this.paymentMethod = method;
	}
	
	/**
	 * Helper method to setup coin dispenser controllers
	 * @param denominations Is the list of coin denominations supported by the hardware
	 */
	private void setupCoinDispenserControllers(List<BigDecimal> denominations) {
		for (BigDecimal d : denominations) {
			this.coinDispenserControllers.put(d, new CoinDispenserController(this, d));
		}
	}
	/**
	 * helper method to setup the bank details
	 * @param bank is the details of the customer's bank
	 */
	public void setupBankDetails(CardIssuer bank) {
		this.cardLogic=new CardSwipeLogic(this,bank);
	}
	
	/**
	 * Helper method to setup banknote dispenser controllers
	 * @param denominations Is the list of coin denominations supported by the hardware
	 */
	private void setupBanknoteDispenserControllers(List<BigDecimal> denominations) {
		for (BigDecimal d : denominations) {
			this.banknoteDispenserControllers.put(d, new BanknoteDispenserController(this, d));
		}
	}
	
	/**
	 * Gets all of the available coins in each coin dispenser
	 * @return A mapping of coin counts indexed by their denomination
	 */
	public Map<BigDecimal, Integer> getAvailableCoinsInDispensers() {
		Map<BigDecimal, Integer> available = new HashMap<>();
		
		// Assume all coins in each dispenser are of the same denomination (they should be)
		for (Entry<BigDecimal, CoinDispenserController> e : this.coinDispenserControllers.entrySet()) {
			final BigDecimal d = e.getKey();
			final CoinDispenserController c = e.getValue();
			
			available.put(d, c.getAvailableChange().size());
		}
		
		return available;
	}
	
	/**
	 * Gets all of the available banknotes tracked in each banknote dispenser
	 * @return A mapping of banknote counts indexed by their denomination
	 */
	public Map<BigDecimal, Integer> getAvailableBanknotesInDispensers() {
	 	Map<BigDecimal, Integer> available = new HashMap<>();

	    for (Entry<BigDecimal, BanknoteDispenserController> entry : this.banknoteDispenserControllers.entrySet()) {
	        final BigDecimal denomination = entry.getKey();
	        final BanknoteDispenserController controller = entry.getValue();
	        
	        available.put(denomination, controller.getAvailableBanknotes().size());
	    }
	    
	    return available;
	}

	/**
	 * Checks if the session is started
	 * @return True if the session is active; false otherwise
	 */
	public boolean isSessionStarted() {
		return this.sessionStarted;
	}
	
	/**
	 * Marks the current self checkout session as active
	 * @throws SimulationException If the session is already active
	 */
	public void startSession() throws SimulationException {
		if (this.isSessionStarted()) {
			throw new InvalidStateSimulationException("Session already started");
		}
		
		System.out.println("Session started");
		
		this.stateLogic.gotoState(States.NORMAL);
		this.sessionStarted = true;
	}
	
	/**
	 * Marks the current self checkout session as inactive
	 */
	public void stopSession() {
		System.out.println("Session ended");
		
		this.sessionStarted = false;
	}
}
