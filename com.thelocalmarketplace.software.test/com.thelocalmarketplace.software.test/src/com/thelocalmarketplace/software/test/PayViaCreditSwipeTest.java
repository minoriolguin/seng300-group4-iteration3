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

package com.thelocalmarketplace.software.test;

import com.thelocalmarketplace.software.PayViaCreditSwipe;
import com.thelocalmarketplace.hardware.external.*;
import com.jjjwelectronics.card.Card.*;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class PayViaCreditSwipeTest {
	PayViaCreditSwipe payViaCreditSwipe;
	CardDataStub cardData;
	CardIssuerStub issuer;
	
	@Before
	public void setup() {
		issuer = new CardIssuerStub();
		BigDecimal amountDue = new BigDecimal(1);
		payViaCreditSwipe = new PayViaCreditSwipe(issuer, amountDue);
		
		cardData = new CardDataStub();
	}
	
	@Test
	public void testCardReadValidCard() {
		payViaCreditSwipe.theDataFromACardHasBeenRead(cardData);
		double expected = 0;
		double actual = payViaCreditSwipe.getAmountDue().doubleValue();
		Assert.assertTrue(actual == expected);
	}
	
	@Test
	public void testCardReadInvalidCreditCard() {
		issuer.postTransactionSuccess = false;
		double amountDueBefore = payViaCreditSwipe.getAmountDue().doubleValue();
		payViaCreditSwipe.theDataFromACardHasBeenRead(cardData);
		double actual = payViaCreditSwipe.getAmountDue().doubleValue();
		Assert.assertTrue(actual == amountDueBefore);
	}
	
	@Test
	public void testCardReadNonCreditCard() {
		cardData.type = "Debit";
		double amountDueBefore = payViaCreditSwipe.getAmountDue().doubleValue();
		payViaCreditSwipe.theDataFromACardHasBeenRead(cardData);
		double actual = payViaCreditSwipe.getAmountDue().doubleValue();
		Assert.assertTrue(actual == amountDueBefore);
	}
	
	class CardDataStub implements CardData {
		String type = "Visa";
		String number = "10";
		String cardholder = "Bob";
		String CVV = "123";

		public String getType() {
			return type;
		}
		
		public String getNumber() {
			return number;
		}

		public String getCardholder() {
			return cardholder;
		}

		public String getCVV() {
			return CVV;
		}
		
	}
	
	class CardIssuerStub extends CardIssuer {
		long holdNumber = 1;
		boolean postTransactionSuccess = true;
		boolean releaseHoldsuccess = true;
		

		public CardIssuerStub() {
			super("RBC", 1);
		}
		
		public long authorizeHold(String cardNumber, double amount) {
			return holdNumber;
		}
		
		public boolean postTransaction(String cardNumber, long holdNumber, double actualAmount) {
			return postTransactionSuccess;
		}
		
		public boolean releaseHold(String cardNumber, long holdNumber) {
			return releaseHoldsuccess;
		}
		
	}
}
