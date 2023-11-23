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

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Calendar;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.jjjwelectronics.IDevice;
import com.jjjwelectronics.IDeviceListener;
import com.jjjwelectronics.card.Card;
import com.jjjwelectronics.card.Card.CardData;
import com.jjjwelectronics.card.CardReaderBronze;
import com.jjjwelectronics.card.CardReaderGold;
import com.jjjwelectronics.card.CardReaderListener;
import com.thelocalmarketplace.hardware.external.CardIssuer;
import com.thelocalmarketplace.software.PayWithDebit;

import powerutility.PowerGrid;

public class PayWithDebitTest {
	CardIssuer cardIssuer;
	BigDecimal amountOwed;
    PayWithDebit payWithDebit;
    PayWithDebit payWithDebit2;
    Card card;
    Card.CardSwipeData cardData;
    Card card2;
    Card.CardSwipeData cardData2;
    CardReaderBronze cardReader;
    CardReaderGold cardReaderGold;
    private static int swipeCount;
    private static boolean dataHasBeenRead;
    
	
  
	@Before
    public void setUp() {
		
		 cardIssuer = new CardIssuer("SomeName", 4);
		 amountOwed = new BigDecimal(20);
         payWithDebit = new PayWithDebit(cardIssuer,amountOwed);
         payWithDebit2 = new PayWithDebit(cardIssuer,amountOwed);
         card = new Card("Visa",  "1234567890123456", "SomeName", "111");
         cardData = card.new CardSwipeData();
         card2 = new Card("Visa",  "1234567890654321", "SomeOtherName", "111");
         cardData2 = card2.new CardSwipeData();
         PowerGrid power = PowerGrid.instance();
         cardReader = new CardReaderBronze();
         cardReader.plugIn(power);
         cardReader.turnOn();
         cardReaderGold = new CardReaderGold();
         cardReaderGold.plugIn(power);
         cardReaderGold.turnOn();
         Calendar expiryDate = new Calendar.Builder().setDate(2028, Calendar.SEPTEMBER, 1).build();
 		 cardIssuer.addCardData("1234567890123456", "SomeName", expiryDate, "111",  1000);   
 		 swipeCount = 0;
 		 dataHasBeenRead = false;
	}
	
	@After
	public void tearDown() {
		swipeCount = 0;
		 dataHasBeenRead = false;
	}
 
	/*Tests to see if the method that makes sure the system is ready is working
	 * and that the variable responsible for this is changing accordingly*/
	@Test
	public void testSysReady() {
		payWithDebit.sysReady();
		boolean expected = true;
		assertEquals(expected,payWithDebit.isSysReadyForPayment());
}

	/*Tests the promptForSignature method, the method that asks for 
	 * customer's signature*/
	@Test
	public void testpromptForSignature() {
		String signature = "signature";
		payWithDebit.promptForSignature(signature);
		assertEquals(signature,payWithDebit.getCustomerSignature());
}
	/* Tests the bankAuthentication method, the method that is responsible for making 
	sure that a transaction occurs correctly by taking a card that operates correctly
	as a parameter*/
	@Test
	public void testBankAuthentication() {
		boolean expected = true;
		boolean actual = payWithDebit.bankAuthentication(cardData);
		assertEquals(expected,actual);
	
}
	
	/*Tests the bankAuthentication method to see if it acts correctly if transaction fails 
	 * by taking a card that doesn't exist in the database it takes a card that */
	@Test
	public void testBankAuthenticationFail() {
		boolean expected = false;
		boolean actual = payWithDebit2.bankAuthentication(cardData2);
		assertEquals(expected,actual);
		
	}
	
	/*
	 * Tests how payViaDebitSwipe would react if bank transaction doesn't work because card is not in card issuer database
	 */
	@Test
	public void testCardBlockedAfterFailedAttempts() throws IOException {
		int expectedFailedAttempts = 4;
		boolean expectedCardBlocked = true;
		payWithDebit.sysReady();
		payWithDebit.payViaDebitSwipe(card2, "signature",cardReader);
		payWithDebit.payViaDebitSwipe(card2, "signature",cardReader);
		payWithDebit.payViaDebitSwipe(card2, "signature",cardReader);
		payWithDebit.payViaDebitSwipe(card2, "signature",cardReader);
		assertEquals(expectedFailedAttempts,payWithDebit.getFailedAttempts());
		assertEquals(expectedCardBlocked,payWithDebit.isCardBlocked());
	}
	
	/*
	 * Tests how payViaDebitSwipe would react if the amount owed is greater than the money available on the debit card
	 */
	@Test
	public void testCardBlockedWhenAmountOwedHigher() throws IOException {
		Calendar expiryDate = new Calendar.Builder().setDate(2028, Calendar.SEPTEMBER, 1).build();
		cardIssuer.addCardData("1234567890654321", "SomeOtherName", expiryDate, "111",  1000); 
		payWithDebit2 = new PayWithDebit(cardIssuer,new BigDecimal(1100));
		
		int expectedFailedAttempts = 4;
		boolean expectedCardBlocked = true;
		payWithDebit2.sysReady();
		payWithDebit2.payViaDebitSwipe(card2, "signature",cardReader);
		payWithDebit2.payViaDebitSwipe(card2, "signature",cardReader);
		payWithDebit2.payViaDebitSwipe(card2, "signature",cardReader);
		payWithDebit2.payViaDebitSwipe(card2, "signature",cardReader);
		assertEquals(expectedFailedAttempts,payWithDebit2.getFailedAttempts());
		assertEquals(expectedCardBlocked,payWithDebit2.isCardBlocked());
	}

	/*Tests to ensure the method PayViaDebitSwipe is working correctly by comparing 
	 * the new amount owed of 0 after a successful transaction to the expected*/
	@Test
	public void testPayViaDebitSwipe() throws IOException {
		BigDecimal expectedAmount = new BigDecimal(0);
		payWithDebit.sysReady();
		payWithDebit.payViaDebitSwipe(card, "signature",cardReader);
		assertEquals(expectedAmount,payWithDebit.getAmountOwed());
	
	
}
	/*Tests the listeners that should get called once the method PayViaDebitSwipe runs*/
	@Test
	public void testPayViaDebitSwipeListeners() throws IOException {
		BigDecimal expectedAmount = new BigDecimal(0);
		payWithDebit.sysReady();
		cardReaderGold.register(new StubCardReader());
		payWithDebit.payViaDebitSwipe(card, "signature",cardReaderGold);
		assertEquals(expectedAmount,payWithDebit.getAmountOwed());
		assertEquals(1,swipeCount);
		assertEquals(true,dataHasBeenRead);
	
}
	
	
	/*
	 * Stub class that helps in implementing testing for listeners
	 */
	static class StubCardReader implements CardReaderListener{

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
			swipeCount++;
			
		}

		@Override
		public void theDataFromACardHasBeenRead(CardData data) {
			dataHasBeenRead = true;
			
		}
		
	}
	
}
