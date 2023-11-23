package com.thelocalmarketplace.software;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import java.util.Map;
import com.tdc.CashOverloadException;
import com.tdc.DisabledException;
import com.tdc.IComponent;
import com.tdc.IComponentObserver;
import com.tdc.NoCashAvailableException;
import com.tdc.banknote.AbstractBanknoteDispenser;
import com.tdc.banknote.Banknote;
import com.tdc.banknote.BanknoteDispensationSlot;
import com.tdc.banknote.BanknoteDispensationSlotObserver;
import com.tdc.banknote.BanknoteInsertionSlot;
import com.tdc.banknote.BanknoteInsertionSlotObserver;
import com.tdc.banknote.BanknoteStorageUnit;
import com.tdc.banknote.BanknoteStorageUnitObserver;
import com.tdc.banknote.BanknoteValidator;
import com.tdc.banknote.BanknoteValidatorObserver;

/**
 * PayViaBanknote class handles payments through the use of banknotes, monitors payment process and 
 * authorizing return of change to the customer.
 * 
 * @author Jane Magai (UCID:30180119)
 * 
 */
public class PayViaBanknote implements BanknoteStorageUnitObserver,BanknoteDispensationSlotObserver,BanknoteInsertionSlotObserver,BanknoteValidatorObserver {
	private BigDecimal amountInserted;
	private BigDecimal amountOwed;
	private BanknoteDispensationSlot dispensationSlot;
	private BanknoteInsertionSlot insertionSlot;
	private List<java.util.Map.Entry < BigDecimal,AbstractBanknoteDispenser>> dispenserList;
	private BigDecimal change;
		 
	
	/**
	 * Constructor for PayviaBanknote class.
	 * 
	 * @param amountOwed      The amount owed for payment
	 * @param dispenserSlot   Represents BanknoteDispensationslot that dispenses banknotes.
	 * @param insertSlot      Represents BanknoteInsertionslot where banknotes are entered.
	 * @param dispenser       Abstract version of BanknoteDispensor to allow use of all tiers of checkout system (Bronze, Silver, Gold)
	 * 
	 */
	
	public PayViaBanknote(BigDecimal amountOwed, BanknoteDispensationSlot dispensationSlot, BanknoteInsertionSlot insertionSlot, List<java.util.Map.Entry < BigDecimal,AbstractBanknoteDispenser>> dispenserList) {
		this.amountOwed = amountOwed;
		this.dispensationSlot = dispensationSlot;
		this.insertionSlot = insertionSlot;
		this.dispenserList = dispenserList ;
	}
		
	
	/**
	 *Shows making a payment using banknote
	 *
	 *@param amountInserted. The banknote used for payment.
	 *@return True if payment is succesful meaning full amount is paid, false otherwise
	 * @throws CashOverloadException 
	 * @throws DisabledException 
	 */
	public boolean makePayment (Banknote banknoteAdded){
		amountInserted = amountInserted.add(banknoteAdded.getDenomination());
	    if(amountInserted.compareTo(amountOwed)>=0) {
	    	change = amountInserted.subtract(amountOwed);
	    	return true;	    	
	    }
	    else
	    	return false;	        
	}
	
	/**
	 * Returns change to customer, does not return chsnge if change filed is zero.
	 */			
		
	public void returnChange() throws CashOverloadException, DisabledException{
		for (Map.Entry<BigDecimal, AbstractBanknoteDispenser> entry : dispenserList) {
	        BigDecimal exactChange = entry.getKey();
	        AbstractBanknoteDispenser banknoteDispenser = entry.getValue();

	        BigDecimal requiredBanknotes = change.divideToIntegralValue(exactChange);

	        for (int i = 0; i < requiredBanknotes.intValue(); i++) {
	            try {
	                banknoteDispenser.emit();
	                change = change.subtract(exactChange);
	            } catch (NoCashAvailableException e) {
	      
	                break;
	            }
	            }
	        }
		
	}
	
	
	
	
	
	
	
	
	
	
	// enables components (Insertionslot, Dispensationslot)
	@Override
	public void enabled(IComponent<? extends IComponentObserver> component) {
		component.enable();
		
	}
	
	// disables components
	@Override
	public void disabled(IComponent<? extends IComponentObserver> component) {
		component.disable();
		
	}

	@Override
	public void turnedOn(IComponent<? extends IComponentObserver> component) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void turnedOff(IComponent<? extends IComponentObserver> component) {
		// TODO Auto-generated method stub
		
	}	
	@Override
	public void banknotesFull(BanknoteStorageUnit unit) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void banknoteAdded(BanknoteStorageUnit unit) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void banknotesLoaded(BanknoteStorageUnit unit) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void banknotesUnloaded(BanknoteStorageUnit unit) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void banknoteDispensed(BanknoteDispensationSlot slot, List<Banknote> banknotes) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void banknotesRemoved(BanknoteDispensationSlot slot) {
		// TODO Auto-generated method stub
		
	}
	

	@Override
	public void banknoteInserted(BanknoteInsertionSlot slot) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void banknoteEjected(BanknoteInsertionSlot slot) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void banknoteRemoved(BanknoteInsertionSlot slot) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void goodBanknote(BanknoteValidator validator, Currency currency, BigDecimal denomination) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void badBanknote(BanknoteValidator validator) {
		// TODO Auto-generated method stub
		
	}
	
	
	
	
	
}
