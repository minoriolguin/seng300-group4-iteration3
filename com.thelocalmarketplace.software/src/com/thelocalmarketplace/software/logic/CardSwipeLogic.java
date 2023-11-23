package com.thelocalmarketplace.software.logic;

import com.thelocalmarketplace.hardware.external.CardIssuer;
import com.thelocalmarketplace.software.AbstractLogicDependant;
import com.thelocalmarketplace.software.logic.CentralStationLogic.PaymentMethods;

/**
 * Card Swipe Logic
 * @author Maheen Nizmani (30172615)
 * ---------------------------------
 * @author Connell Reffo (10186960)
 * @author Tara Strickland (10105877)
 * @author Angelina Rochon (30087177)
 * @author Julian Fan (30235289)
 * @author Braden Beler (30084941)
 * @author Samyog Dahal (30194624)
 * @author Phuong Le (30175125)
 * @author Daniel Yakimenka (10185055)
 * @author Merick Parkinson (30196225)
 * @author Farida Elogueil (30171114)
 */
public class CardSwipeLogic extends AbstractLogicDependant {

    public String signature;
    public CardIssuer bank;
    boolean dataRead;

    
    public CardSwipeLogic(CentralStationLogic logic, CardIssuer bank) throws NullPointerException {
    	super(logic);
    	
        this.bank = bank;
    }

    //approve the transaction
    public boolean approveTransaction(String debitCardNumber, double chargeAmount) {
        Long holdNumber = this.bank.authorizeHold(debitCardNumber, chargeAmount);
        
        if (holdNumber != -1) {
            return this.bank.postTransaction(debitCardNumber, holdNumber, chargeAmount);
        }
        
        return false;
    }


    //keeps track of whether data was read or not
    public void isDataRead(boolean read){
        dataRead = read;
    }

    //returns if data is read or not
    public boolean isDataRead(){
        return dataRead;
    }
    
    public PaymentMethods getCardPaymentType(String type) {
    	String t = type.toLowerCase();
    	
    	if (t.contains("debit")) {
    		return PaymentMethods.DEBIT;
    	}
    	else if (t.contains("credit")) {
    		return PaymentMethods.CREDIT;
    	}
    	
    	return PaymentMethods.NONE;
    }
}
