//Elizabeth Szentmiklossy UCID: 30165216
//Justine Mangaliman UCID: 30164741
//Enzo Mutiso UCID: 30182555
//Abdelrahman Mohamed UCID: 30162037
//Mohammad Mustafa Mehtab UCID: 30189394
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
 * PayviaCoin class handles payments and monitors the payment process, including weight discrepancies.
 * This class does not yet handle change.
 *
 * @author Elizabeth Szentmiklossy (UCID: 30165216)

 */

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


	public void GiveChange() throws CashOverloadException, DisabledException {

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
	}




	// to enable the coinslot.
	@Override
	public void enabled(IComponent<? extends IComponentObserver> component) {
		// TODO Auto-generated method stub
		component.enable();
	}

	// to disable the coinslot.
	@Override
	public void disabled(IComponent<? extends IComponentObserver> component) {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub

	}

	@Override
	public void aDeviceHasBeenTurnedOff(IDevice<? extends IDeviceListener> device) {
		// TODO Auto-generated method stub

	}


}

