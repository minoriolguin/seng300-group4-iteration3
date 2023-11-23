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
package com.thelocalmarketplace.software;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Currency;
import java.util.List;
import java.util.Map;
import com.tdc.CashOverloadException;
import com.tdc.DisabledException;
import com.tdc.IComponent;
import com.tdc.IComponentObserver;
import com.tdc.NoCashAvailableException;
import com.tdc.banknote.Banknote;
import com.tdc.banknote.BanknoteDispenserObserver;
import com.tdc.banknote.BanknoteStorageUnit;
import com.tdc.banknote.BanknoteStorageUnitObserver;
import com.tdc.banknote.BanknoteValidator;
import com.tdc.banknote.BanknoteValidatorObserver;
import com.tdc.banknote.IBanknoteDispenser;
import com.tdc.coin.Coin;
import com.tdc.coin.CoinDispenserObserver;
import com.tdc.coin.CoinStorageUnit;
import com.tdc.coin.CoinStorageUnitObserver;
import com.tdc.coin.CoinValidator;
import com.tdc.coin.CoinValidatorObserver;
import com.tdc.coin.ICoinDispenser;

/**
 * Implements various coin and banknote listeners to take action on events thrown by 
 * the various coin and banknote related hardware attached to the station.
 */
public class PayCashControl implements CoinValidatorObserver, BanknoteValidatorObserver, CoinStorageUnitObserver, BanknoteStorageUnitObserver, CoinDispenserObserver, BanknoteDispenserObserver {
	private CustomerStationControl customerStationControl;
	private List<BigDecimal> coinDenominations;
	private Map<BigDecimal, ICoinDispenser> coinDispensers;
	private BigDecimal[] banknoteDenominations;
	private Order order;
	private Map<BigDecimal, IBanknoteDispenser> banknoteDispensers;
	private BigDecimal pendingPaying = BigDecimal.ZERO;
	private BigDecimal totalChangeReturned;
	
	/**
	 * Simple constructor setting necessary references for future actions.
	 * @param customerStationControl the station to which the controller is implemented
	 */
	public PayCashControl(CustomerStationControl customerStationControl) {
		this.customerStationControl = customerStationControl;
		this.coinDenominations = customerStationControl.getCoinDenominations();
		this.coinDispensers = customerStationControl.getCoinDispensers();
		this.banknoteDenominations = customerStationControl.getBanknoteDenominations();
		this.banknoteDispensers = customerStationControl.getBanknoteDispensers();
		this.order = customerStationControl.getOrder();
		this.totalChangeReturned = new BigDecimal(0);
	}
	
	private void processPayment(BigDecimal value) {
		if(customerStationControl.paymentProcessStarted()) {
			customerStationControl.getOrder().addAmountPaid(value);
			if (customerStationControl.getOrder().getTotalUnpaid().compareTo(BigDecimal.ZERO) == 0) {
				//payment complete
			} else if (this.customerStationControl.getOrder().getTotalUnpaid().compareTo(BigDecimal.ZERO) < 0) {
				//change required for customer
				processChange();
			} else {
				//order has not yet been paid in full
				updateRemainingBalance();
			}
		}
		printReceipt();
	}
	
	/**
	 * Emits change from cash dispensers to the customer, starting from the largest available denomination.
	 */
	private void processChange() {
		//Iterate through available banknotes to give as change
		for (int i = banknoteDenominations.length - 1; i >= 0; --i) {
			perDenom:
			while(customerStationControl.getOrder().getTotalUnpaid().abs().compareTo(banknoteDenominations[i]) >= 0) {
				IBanknoteDispenser dispenser = banknoteDispensers.get(banknoteDenominations[i]);
				try {
					dispenser.emit();
				} catch (CashOverloadException e) {
					// TODO
					e.printStackTrace();
				} catch (NoCashAvailableException e) {
					break perDenom;
				} catch (DisabledException e) {
					// TODO
				}
			}
		}
		//Iterate through available coins to give as change
		for (int i = 0; i < coinDenominations.size()-1; i++) {
			perDenom:
			while(customerStationControl.getOrder().getTotalUnpaid().abs().compareTo(coinDenominations.get(i)) >= 0) {
				ICoinDispenser dispenser = coinDispensers.get(coinDenominations.get(i));
				try {
					dispenser.emit();
				} catch (CashOverloadException e) {
					// TODO
					e.printStackTrace();
				} catch (NoCashAvailableException e) {
					break perDenom;
				} catch (DisabledException e) {
					// TODO
				}
			}
		}
		if (customerStationControl.getOrder().getTotalUnpaid().compareTo(BigDecimal.ZERO) != 0) {
			customerStationControl.notifyAttendant("Not enough change to finish payment for customer", customerStationControl.notifyNotEnoughChangeCode);
		}
	}
	
	private void printReceipt() {
		Receipt receipt = new Receipt(customerStationControl, null);
        customerStationControl.printReceipt(receipt.toString());
	}

	/**
	 * notifies the customer of the unpaid amount for the order
	 * @param order	the customer's order for which they are paying
	 */
	public void updateRemainingBalance() {
		this.customerStationControl.notifyCustomer(String.format
				("Amount due: %.2f", customerStationControl.getOrder().getTotalUnpaid())
				, customerStationControl.notifyOtherCode);
	}
	
	@Override
	public void banknoteRemoved(IBanknoteDispenser dispenser, Banknote banknote) {
		order = customerStationControl.getOrder();
		order.setTotalUnpaid(order.getTotalUnpaid().add(banknote.getDenomination()));
		totalChangeReturned = totalChangeReturned.add(banknote.getDenomination());
	}

	@Override
	public void coinRemoved(ICoinDispenser dispenser, Coin coin) {
		order = customerStationControl.getOrder();
		order.setTotalUnpaid(order.getTotalUnpaid().add(coin.getValue().round(new MathContext(2))));
		totalChangeReturned = totalChangeReturned.add(coin.getValue().round(new MathContext(2)));
	}
	
	//***COIN
	@Override
	public void validCoinDetected(CoinValidator validator, BigDecimal value) {
		pendingPaying = value;
		processPayment(value);
	}

	@Override
	public void invalidCoinDetected(CoinValidator validator) {
		customerStationControl.notifyCustomer("Please insert a valid coin", customerStationControl.notifyInvalidCoinCode);
	}
	
	@Override
	public void coinsFull(CoinStorageUnit unit) {
		customerStationControl.notifyAttendant("Coin Storage Full", customerStationControl.notifyFullCoinStorageCode);
	}

	@Override
	public void coinAdded(CoinStorageUnit unit) {
		processPayment(pendingPaying);
	}
	
	@Override
	public void goodBanknote(BanknoteValidator validator, Currency currency, BigDecimal denomination) {
		pendingPaying = denomination;
	}
	@Override
	public void badBanknote(BanknoteValidator validator) {
		customerStationControl.notifyCustomer("Please insert a valid banknote", customerStationControl.notifyInvalidBanknoteCode);
		
	}
	@Override
	public void banknotesFull(BanknoteStorageUnit unit) {
		customerStationControl.notifyAttendant("Banknote Storage Full", customerStationControl.notifyFullBanknoteStorageCode);		
	}

	@Override
	public void banknoteAdded(BanknoteStorageUnit unit) {
		processPayment(pendingPaying);
	}
	
	public BigDecimal getTotalChangeReturned() {
		return totalChangeReturned;
	}
	
	//unimplemented listener methods:
	@Override
	public void enabled(IComponent<? extends IComponentObserver> component) {}
	@Override
	public void disabled(IComponent<? extends IComponentObserver> component) {}
	@Override
	public void turnedOn(IComponent<? extends IComponentObserver> component) {}
	@Override
	public void turnedOff(IComponent<? extends IComponentObserver> component) {}
	@Override
	public void coinsLoaded(CoinStorageUnit unit) {}
	@Override
	public void coinsUnloaded(CoinStorageUnit unit) {}
	@Override
	public void banknotesLoaded(BanknoteStorageUnit unit) {}
	@Override
	public void banknotesUnloaded(BanknoteStorageUnit unit) {}
	@Override
	public void moneyFull(IBanknoteDispenser dispenser) {}
	@Override
	public void banknotesEmpty(IBanknoteDispenser dispenser) {}
	@Override
	public void banknoteAdded(IBanknoteDispenser dispenser, Banknote banknote) {}
	@Override
	public void banknotesLoaded(IBanknoteDispenser dispenser, Banknote... banknotes) {}
	@Override
	public void banknotesUnloaded(IBanknoteDispenser dispenser, Banknote... banknotes) {}
	@Override
	public void coinsFull(ICoinDispenser dispenser) {}
	@Override
	public void coinsEmpty(ICoinDispenser dispenser) {}
	@Override
	public void coinAdded(ICoinDispenser dispenser, Coin coin) {}
	@Override
	public void coinsLoaded(ICoinDispenser dispenser, Coin... coins) {}
	@Override
	public void coinsUnloaded(ICoinDispenser dispenser, Coin... coins) {}
}
