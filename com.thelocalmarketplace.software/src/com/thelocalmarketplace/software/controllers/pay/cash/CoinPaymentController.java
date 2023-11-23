package com.thelocalmarketplace.software.controllers.pay.cash;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Map.Entry;

import com.tdc.IComponent;
import com.tdc.IComponentObserver;
import com.tdc.coin.CoinValidator;
import com.tdc.coin.CoinValidatorObserver;
import com.thelocalmarketplace.software.AbstractLogicDependant;
import com.thelocalmarketplace.software.logic.CentralStationLogic;
import com.thelocalmarketplace.software.logic.CentralStationLogic.PaymentMethods;
import com.thelocalmarketplace.software.logic.StateLogic.States;

import ca.ucalgary.seng300.simulation.InvalidStateSimulationException;
import ca.ucalgary.seng300.simulation.SimulationException;

/**
 * Controller for payment through coins
 * 
 * Uses CurrencyLogic to calculate necessary change requirements
 * Then interacts with hardware to carry out change operations
 * 
 * @author Connell Reffo (10186960)
 * --------------------------------
 * @author Tara Strickland (10105877)
 * @author Angelina Rochon (30087177)
 * @author Julian Fan (30235289)
 * @author Braden Beler (30084941)
 * @author Samyog Dahal (30194624)
 * @author Maheen Nizmani (30172615)
 * @author Phuong Le (30175125)
 * @author Daniel Yakimenka (10185055)
 * @author Merick Parkinson (30196225)
 */
public class CoinPaymentController extends AbstractLogicDependant implements CoinValidatorObserver {
	
	/**
	 * Base constructor
	 * @param logic reference to SelfCheckoutStationLogic the controller belongs to
	 * @throws NullPointerException If logic is null
	 */
	public CoinPaymentController(CentralStationLogic logic) throws NullPointerException {
		super(logic);
	
		this.logic.hardware.coinValidator.attach(this);
	}
	
	/**
	 * Interacts with dispenser hardware to dispense coins as change to customer
	 * 
	 * Precalculates the required change from what is available using logic classes
	 * Then attempts to interact with dispenser hardware to dispense what was calculated
	 * 
	 * @param overpay Is the amount to give back (will be converted to a positive value)
	 * @return The total value that the customer did not receive back due to failures (should be 0)
	 */
	public BigDecimal processCoinChange(BigDecimal overpay) {
		BigDecimal missed = BigDecimal.ZERO;
		
		if (overpay.compareTo(BigDecimal.ZERO) == 0) {
			
			// No over pay so return 0
			return missed;
		}
		
		// Sanitize argument
		overpay = overpay.abs();
		
		// Calculate change mapping
		Map<BigDecimal, Integer> change = this.logic.coinCurrencyLogic.calculateChange(overpay, this.logic.getAvailableCoinsInDispensers(), true);
		
		System.out.println(this.logic.getAvailableCoinsInDispensers().toString());
		
		// Dispense coins
		for (Entry<BigDecimal, Integer> c : change.entrySet()) {
			final BigDecimal denomination = c.getKey();

			// Loop for each available coin
			for (int i = 0; i < c.getValue(); i++) {
				try {
					
					// Attempt to emit a coin from specific coin dispenser				
					this.logic.hardware.coinDispensers.get(denomination).emit();
				} catch (Exception e) {
					missed = missed.add(denomination);
					
					System.out.println("Failed to emit coin");
				}
			}
		}
		
		return missed;
	}
	
	@Override
	public void validCoinDetected(CoinValidator validator, BigDecimal value) throws SimulationException {
		if (!this.logic.isSessionStarted()) {
			throw new InvalidStateSimulationException("Session not started");
		}
		else if (!this.logic.stateLogic.inState(States.CHECKOUT)) {
			throw new InvalidStateSimulationException("Not ready for checkout");
		}
		else if (!this.logic.getSelectedPaymentMethod().equals(PaymentMethods.CASH)) {
			throw new InvalidStateSimulationException("Payment by coin not selected");
		}
		else if (this.logic.cartLogic.getBalanceOwed().compareTo(BigDecimal.ZERO) == 0) {
			throw new InvalidStateSimulationException("Balance already paid in full");
		}
		
		BigDecimal pay = this.logic.cartLogic.getBalanceOwed().subtract(value);
		
		// Make Payment to transaction.
		this.logic.cartLogic.modifyBalance(value.negate());
		
		//Check if balance has been paid in full
		if (pay.compareTo(BigDecimal.ZERO) <= 0) {
			pay = pay.abs();
			
			// Process change
			BigDecimal missed = this.processCoinChange(pay);
			
			// Check if some change failed to dispense
			if (missed.compareTo(BigDecimal.ZERO) > 0) {
				
				// Suspend station
				this.logic.stateLogic.gotoState(States.SUSPENDED);
				
				// Notify customer
				System.out.println("Not enough coin change is available: " + missed +  " is unavailable");
			}
			else {
				System.out.println("Payment complete. Change dispensed successfully");
				
				// Print receipt
				this.logic.receiptPrintingController.handlePrintReceipt(pay.subtract(missed));
			}
		}
		else {
			System.out.println("Balance owed: " + this.logic.cartLogic.getBalanceOwed());
		}
	}
	
	@Override
	public void invalidCoinDetected(CoinValidator validator) {
		System.out.println("Invalid Coin Detected. Balance owed: " + this.logic.cartLogic.getBalanceOwed());
	}
	
	// ---- Unused ----
	
	@Override
	public void enabled(IComponent<? extends IComponentObserver> component) {
	}
	
	@Override
	public void disabled(IComponent<? extends IComponentObserver> component) {
	}
	
	@Override
	public void turnedOn(IComponent<? extends IComponentObserver> component) {
	}
	
	@Override
	public void turnedOff(IComponent<? extends IComponentObserver> component) {
	}
}
