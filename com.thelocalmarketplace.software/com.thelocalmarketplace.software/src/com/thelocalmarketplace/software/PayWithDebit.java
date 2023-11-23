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

import java.io.IOException;
import java.math.BigDecimal;

import com.jjjwelectronics.IDevice;
import com.jjjwelectronics.IDeviceListener;
import com.jjjwelectronics.card.AbstractCardReader;
import com.jjjwelectronics.card.Card;
import com.jjjwelectronics.card.Card.CardData;
import com.jjjwelectronics.card.Card.CardSwipeData;
import com.jjjwelectronics.card.CardReaderListener;
import com.thelocalmarketplace.hardware.external.CardIssuer;

public class PayWithDebit implements CardReaderListener{
	
	
	
	boolean sysReadyForPayment = false;
	

	private BigDecimal amountOwed;
	private CardSwipeData cardData;
	private CardIssuer cardIssuer;
	private String customerSignature;
	private long holdNumber;
	private int failedAttempts =0;
	private boolean cardBlocked = true;

	/*
	 * Constructor for PayWithDebit
	 * @param issuer
	 * 			The info of the card issuer
	 * @param amount
	 * 			The amount owed by the customer 
	 */
	 public PayWithDebit(CardIssuer issuer,BigDecimal amount) {
	        this.cardIssuer = issuer;
	        this.amountOwed = amount;
	    }
	 
	 /*
	  * Method responsible for making the system ready for payment
	  */
	 public void sysReady() {
			sysReadyForPayment = true;
		}

	 /*
	  * Prompts the customer for a signature
	  */
	public void promptForSignature(String signature) {
		customerSignature = signature;
	}
	
	/*
	 * Authenticates the transaction after verifying with the bank
	 */
	public boolean bankAuthentication(CardSwipeData cardData) {
		holdNumber = cardIssuer.authorizeHold(cardData.getNumber(), amountOwed.doubleValue());
        boolean transactionResult = cardIssuer.postTransaction(cardData.getNumber(), holdNumber, amountOwed.doubleValue());
        cardIssuer.releaseHold(cardData.getNumber(), holdNumber);
        if(transactionResult==false) {
        	failedAttempts+=1;
        }
        if(failedAttempts>3) {
        	transactionResult=false;
        }
        return transactionResult;
	}
	
	/*
	 * Method pays with debit using the action of swiping the card on the card reader
	 */
	public void payViaDebitSwipe(Card card, String signature,AbstractCardReader cardReader) throws IOException {
			if (sysReadyForPayment==true) {
				if(failedAttempts<3) {
					System.out.println("Amount Due: "+ amountOwed);
					boolean swipeExecuted = false;
				
					while(swipeExecuted==false) {
						try{cardData = (CardSwipeData) cardReader.swipe(card);
							swipeExecuted=true;
					
							promptForSignature(signature);
							if (bankAuthentication(cardData)==true) {
								amountOwed = new BigDecimal(0);
								System.out.println("Amount Due: " + amountOwed);
						         }
							else {if(failedAttempts<3){
								System.out.println("Transaction failed. Please try again.");
							}
								  else if(failedAttempts==3){
										System.out.println("Maximum amount of tries to reached.");
										cardBlocked = true;
							}}
						    }	
						catch(Exception e) {}
							}	
			}
				
				else{
					System.out.println("Card blocked.");
					failedAttempts+=1;
				}}
			if(amountOwed.doubleValue()== new BigDecimal(0).doubleValue()) {
				sysReadyForPayment = false;
			}}
	/*
	 * Getter methods
	 */
	
	
	public boolean isSysReadyForPayment() {
		return sysReadyForPayment;
	}
	
	public CardIssuer getCardIssuer() {
		return cardIssuer;
	}
	
	public BigDecimal getAmountOwed() {
		return amountOwed;
	}
	
	
	public String getCustomerSignature() {
		return customerSignature;
	}
	
	public int getFailedAttempts() {
		return failedAttempts;
	}
	
	public boolean isCardBlocked() {
		return cardBlocked;
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
	
	@Override
	public void aCardHasBeenSwiped() {
		 
	}
	
	@Override
	public void theDataFromACardHasBeenRead(CardData data) {
		
	}
	
	}
