/**
* Jon Mulyk (UCID: 30093143)
* Elizabeth Szentmiklossy (UCID: 30165216)
* Ahmed Ibrahim Mohamed Seifledin Hadsan (UCID: 30174024)
* Arthur Huan (UCID: 30197354)
* Jaden Myers (UCID: 30152504)
* Jane Magai (UCID: 30180119)
* Ahmed Elshabasi (UCID: 30188386)
* Jincheng Li (UCID: 30172907)
* Sina Salahshour (UCID: 30177165)
* Anthony Tolentino (UCID: 30081427) */



package com.thelocalmarketplace.software;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.jjjwelectronics.IDevice;
import com.jjjwelectronics.IDeviceListener;
import com.tdc.CashOverloadException;
import com.tdc.DisabledException;
import com.tdc.IComponent;
import com.tdc.IComponentObserver;
import com.tdc.NoCashAvailableException;
import com.tdc.coin.AbstractCoinDispenser;
import com.tdc.coin.Coin;
import com.tdc.coin.CoinSlot;
import com.tdc.coin.CoinSlotObserver;
import com.tdc.coin.CoinStorageUnit;
import com.tdc.coin.CoinStorageUnitObserver;
import com.thelocalmarketplace.hardware.CoinTray;
/**
 * The PayviaCoin class handles payments and monitors the payment process, including weight discrepancies.
 * This class does not yet handle change.
 *
 * The class is designed to facilitate payments through the CoinSlot by inserting coins.
 * It tracks the amount owed, the amount inserted, and monitors weight discrepancies using a WeightDiscrepancy instance.
 * 
 * The class also implements the CoinStorageUnitObserver interface to observe events related to coin storage units.
 *
 *@author Elizabeth Szentmiklossy (UCID: 30165216)
 *
 * @see CoinStorageUnitObserver
 * @see WeightDiscrepancyListner
 * */

public class PayviaCoin implements CoinStorageUnitObserver, WeightDiscrepancyListner{
	private BigDecimal amount_inserted = BigDecimal.ZERO; //
	private BigDecimal amount_owed; 
	private CoinSlot coinSlot;
	private WeightDiscrepancy discrepancy;
	private CoinTray dispenced;
	private BigDecimal change;
	private List<java.util.Map.Entry < BigDecimal,AbstractCoinDispenser>> dispencer;

	/**
	 * Constructor for PayviaCoin class.
	 *
	 * @param total         The total amount owed for the payment.
	 * @param tray          The CoinTray where dispensed coins are collected. (implemented for future iterations)
	 * @param new_discrepancy The WeightDiscrepancy instance to check weight discrepancies.
	 * @param new_coinSlot  The CoinSlot for inserting coins.
	 */
	public PayviaCoin(BigDecimal total, CoinTray tray,  WeightDiscrepancy new_discrepancy, CoinSlot new_coinSlot, List<java.util.Map.Entry < BigDecimal,AbstractCoinDispenser>> dispencer){
		amount_owed = total;
		dispenced = tray;
		this.discrepancy = new_discrepancy;
		this.coinSlot = new_coinSlot;
		discrepancy.register(this);
		this.dispencer = dispencer;

	}

	/**
	 * Make a payment using a coin.
	 *
	 * @param money The coin used for payment.
	 * @return True if payment is successful, false otherwise.
	 */
	public boolean MakePayment(Coin money) {
		amount_inserted = amount_inserted.add(money.getValue());

		if (amount_inserted.compareTo(amount_owed)<0){
			return true;
		}
		else
			change = amount_inserted.subtract(amount_owed);
		return false;

	}
	
	
	 
	/**
	 * Dispenses change based on the amount paid and owed, using available coin dispensers.
	 *
	 * @throws CashOverloadException   if there is an overload in the cash system
	 * @throws DisabledException       if the system is disabled
	 * @throws NoCashAvailableException if there is not enough cash available for the payment
	 */

	public void GiveChange() throws CashOverloadException, DisabledException, NoCashAvailableException {

		for(java.util.Map.Entry< BigDecimal,AbstractCoinDispenser> i:dispencer) {
			BigDecimal exactchange = i.getKey();
			AbstractCoinDispenser coin = i.getValue();
			BigDecimal result = change.divideToIntegralValue(exactchange);

			for(int j = 0 ; j<result.intValue(); j++) {
				try {
					coin.emit();
					change = change.subtract(exactchange);
				} 
				catch (NoCashAvailableException e) {
					break ;


				} 
			}
		}
		if(change.compareTo(BigDecimal.ZERO)>0) {
			String error = change.toString();
			throw new NoCashAvailableException();
		}
	}




	// to enable the coinslot.
	@Override
	public void enabled(IComponent<? extends IComponentObserver> component) {
		component.enable();
	}

	// to disable the coinslot.
	@Override
	public void disabled(IComponent<? extends IComponentObserver> component) {
		component.disable();
	}

	@Override
	public void WeightDiscrancyOccurs() {
		disabled(coinSlot);	
	}

	@Override
	public void WeightDiscrancyResolved() {
		enabled(coinSlot);	
	}

	@Override
	public void addOwnBagsSelected() {

	}

	@Override
	public void addOwnBagDeselected() {

	}

	// Other overridden methods from CoinStorageUnitObserver (unimplemented).
	@Override
	public void turnedOn(IComponent<? extends IComponentObserver> component) {	
	}
	@Override
	public void turnedOff(IComponent<? extends IComponentObserver> component) {
	}
	@Override
	public void coinsFull(CoinStorageUnit unit) {	
	}
	@Override
	public void coinAdded(CoinStorageUnit unit) {	
	}
	@Override
	public void coinsLoaded(CoinStorageUnit unit) {
	}
	@Override
	public void coinsUnloaded(CoinStorageUnit unit) {	
	}
	@Override
	public void aDeviceHasBeenEnabled(IDevice<? extends IDeviceListener> device) {
	}
	@Override
	public void aDeviceHasBeenDisabled(IDevice<? extends IDeviceListener> device) {
	}
	@Override
	public void aDeviceHasBeenTurnedOn(IDevice<? extends IDeviceListener> device) {
	}
	@Override
	public void aDeviceHasBeenTurnedOff(IDevice<? extends IDeviceListener> device) {
	}
}

