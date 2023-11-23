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
import java.util.Currency;
import java.util.List;
import java.util.Map;
import com.tdc.CashOverloadException;
import com.tdc.DisabledException;
import com.tdc.IComponent;
import com.tdc.IComponentObserver;
import com.tdc.NoCashAvailableException;
import com.tdc.Sink;
import com.tdc.banknote.AbstractBanknoteDispenser;
import com.tdc.banknote.Banknote;
import com.tdc.banknote.BanknoteDispensationSlot;
import com.tdc.banknote.BanknoteDispensationSlotObserver;
import com.tdc.banknote.BanknoteDispenserObserver;
import com.tdc.banknote.BanknoteInsertionSlot;
import com.tdc.banknote.BanknoteInsertionSlotObserver;
import com.tdc.banknote.BanknoteStorageUnit;
import com.tdc.banknote.BanknoteStorageUnitObserver;
import com.tdc.banknote.BanknoteValidator;
import com.tdc.banknote.BanknoteValidatorObserver;
import com.tdc.banknote.IBanknoteDispenser;

/**
 * PayViaBanknote class handles payments through the use of banknotes, monitors payment process and 
 * authorizing return of change to the customer.
 * 
 * @author Jane Magai (UCID:30180119)
 * 
 */
public class PayViaBanknote implements BanknoteValidatorObserver {
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
	 * @param dispenser       List of Abstract version of BanknoteDispensor to allow use of all tiers of checkout system (Bronze, Silver, Gold)
	 * 
	 */
	
	public PayViaBanknote(BigDecimal amountOwed, BanknoteDispensationSlot dispensationSlot, BanknoteInsertionSlot insertionSlot, List<java.util.Map.Entry < BigDecimal,AbstractBanknoteDispenser>> dispenserList) {
		this.amountOwed = amountOwed;
		this.dispensationSlot = dispensationSlot;
		this.insertionSlot = insertionSlot;
		this.dispenserList = dispenserList ;
		this.amountInserted = BigDecimal.ZERO;
	}
		
		
	
	
	
	/**
	 *Represents a customer succesfully making a payment meaning paid full amount, it keeps track of change and current amount owed by customer.
	 *Checks if InsertionSlot has any dangling notes to ensure that the banknote was validated and inserted.
	 *
	 *@param amountInserted. The banknote used for payment.
	 *@return True if payment is succesful meaning full amount is paid, false otherwise
	 * @throws CashOverloadException 
	 * @throws DisabledException 
	 */
	public Boolean makePayment (Banknote banknoteAdded){
		if (!insertionSlot.hasDanglingBanknotes()) 
		    amountInserted = amountInserted.add(banknoteAdded.getDenomination());
		if(amountInserted.compareTo(amountOwed)>=0) {
		    	change = amountInserted.subtract(amountOwed);
		    	System.out.println("Payment is complete, change due is " + change.toPlainString()); // Stimulates signalling to the customer that the payment was succesful and change is due.
		    	return true;
	           }
	    
	    else
	    	System.out.println("Payment not complete balance remaining " + amountOwed );
	    	return false;	        
	}
	
	/**
	 * Returns change to customer, does not return change if change is zero.
	 * @throws CashOverloadException 
	 * @throws DisabledException 
	 */			
		
	public void returnChange() throws CashOverloadException, DisabledException{
		for (Map.Entry<BigDecimal, AbstractBanknoteDispenser> entry : dispenserList) {
	        BigDecimal exactChange = entry.getKey();
	        AbstractBanknoteDispenser banknoteDispenser = entry.getValue();
	        BigDecimal requiredBanknotes = change.divideToIntegralValue(exactChange);

	        for (int i = 0; i < requiredBanknotes.intValue(); i++) {
	            try {
	                banknoteDispenser.emit();
	                dispensationSlot.dispense();
	                change = change.subtract(exactChange);
	            } catch (NoCashAvailableException e) {
	            	System.out.println("No Cash Available" );
	                banknoteDispenser.disable();
	            }
	            }
	        }
		
	}
	
		
	
	

	//Observor methods not implemented 
	@Override
	public void enabled(IComponent<? extends IComponentObserver> component) {
	}
	
	@Override
	public void disabled(IComponent<? extends IComponentObserver> component) {
		// TODO Auto-generated method stub
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
	public void goodBanknote(BanknoteValidator validator, Currency currency, BigDecimal denomination) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void badBanknote(BanknoteValidator validator) {
		// TODO Auto-generated method stub
		
	}


	
	
	
	
	
}
