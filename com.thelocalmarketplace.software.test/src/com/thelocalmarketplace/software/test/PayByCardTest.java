package com.thelocalmarketplace.software.test;
import java.io.IOException;
import java.math.*;
import java.util.*;

import com.jjjwelectronics.card.*;
import com.thelocalmarketplace.hardware.external.CardIssuer;
import com.thelocalmarketplace.software.PayByCard;
import com.thelocalmarketplace.software.Software;

import ca.ucalgary.seng300.simulation.InvalidArgumentSimulationException;

import org.junit.*;
import com.thelocalmarketplace.hardware.*;
//import com.thelocalmarketplace.software.*;
import powerutility.*;

public class PayByCardTest {

    private SelfCheckoutStationBronze stationBronze;
    private PowerGrid powerGrid;
    //private Currency CAD;
    private Software station;
    private Card tapCreditCard = new Card("credit", "12345", "Jack",
                                                "123", "8960", true, true);

    private Card swipeCreditCard = new Card("credit", "234567", "John",
            "245", "7429", false, false);

    private Card insertCreditCard = new Card("credit", "123456", "Jill",
            "980", "2321", false, true);
    
    private Card tapDebitCard = new Card("credit", "12345", "Jack",
            "123", "8960", true, true);

    private Card swipeDebitCard = new Card("credit", "234567", "John",
    			"245", "7429", false, false);

    private Card insertDebitCard = new Card("credit", "123456", "Jill",
    			"980", "4321", false, true);
    
    // A simple stub for CardIssuer
    private static class CardIssuerStub {

        private CardIssuer debitCardIssuer;
		private CardIssuer creditCardIssuer;

		public void setCardIssuersForTest(CardIssuer creditCardIssuer, CardIssuer debitCardIssuer) {
            this.debitCardIssuer = debitCardIssuer;
            this.creditCardIssuer = creditCardIssuer;
        }
    }

    
    @Before
    public void setUp() {
        // set up coinValidator

        stationBronze = new SelfCheckoutStationBronze();
        station = Software.getInstance(stationBronze);
        powerGrid = PowerGrid.instance();
        stationBronze.plugIn(powerGrid);
        stationBronze.turnOn();
        station.turnOn();

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR,2);
        station.payByCard.addCardData("credit","12345", "jack", calendar,"123",420 );
        station.payByCard.addCardData("credit","234567", "John", calendar,"245",120 );
        station.payByCard.addCardData("credit", "123456", "Jill", calendar, "980", 456);
        
        new PayByCard(station);
        
        
        
    }

    @Test
    public void testCreditTap() throws IOException {
        System.out.println("Test when payment with Credit Card tap: ");
        station.addToOrderTotal(new BigDecimal ("6.50"));

        station.cardReader.tap(tapCreditCard);

        BigDecimal result = station.getOrderTotal();
        Assert.assertEquals(BigDecimal.ZERO, result);
    }
    
    @Test
    public void testDebitTap() throws IOException {
        System.out.println("Test when payment with Debit Card tap: ");
        station.addToOrderTotal(new BigDecimal ("10.75"));

        station.cardReader.tap(tapDebitCard);

        BigDecimal result = station.getOrderTotal();
        Assert.assertEquals(BigDecimal.ZERO, result);
    }
    
    @Test
    public void testDebitCardSwipe() throws IOException {
        System.out.println("Test when payment with Debit Card swipe: ");
        station.addToOrderTotal(new BigDecimal("10"));

        station.cardReader.swipe(swipeDebitCard);

        BigDecimal result = station.getOrderTotal();
        Assert.assertEquals(BigDecimal.ZERO, result);
    }

    @Test
    public void testCreditSwipe() throws IOException {
        System.out.println("Test when payment with Credit Card swipe: ");
        station.addToOrderTotal(new BigDecimal ("15"));

        station.cardReader.swipe(swipeCreditCard);

        BigDecimal result = station.getOrderTotal();
        Assert.assertEquals(BigDecimal.ZERO, result);
    }

    @Test
    public void testCreditInsert() throws IOException {
        System.out.println("Test when payment with Credit Card insert: ");
        station.addToOrderTotal(new BigDecimal ("6.50"));

        station.cardReader.insert(insertCreditCard, "2321");

        BigDecimal result = station.getOrderTotal();
        Assert.assertEquals(BigDecimal.ZERO, result);
    }
    
    @Test
    public void testDebitInsert() throws IOException {
        System.out.println("Test when payment with Debit Card insert: ");
        station.addToOrderTotal(new BigDecimal ("11.32"));

        station.cardReader.insert(insertDebitCard, "4321");

        BigDecimal result = station.getOrderTotal();
        Assert.assertEquals(BigDecimal.ZERO, result);
    }
    
    @Test (expected = InvalidArgumentSimulationException.class)
    public void testHoldFailure() throws IOException {
        System.out.println("Test hold failure in PayByCard:");
        CardIssuerStub cardIssuerStub = new CardIssuerStub();
        cardIssuerStub.setCardIssuersForTest(cardIssuerStub.creditCardIssuer = new CardIssuer("creditCardIssuer", 0), 
        		 								cardIssuerStub.debitCardIssuer = new CardIssuer("debitCardIssuer", 0));
        
        station.cardReader.swipe(swipeCreditCard);
        station.cardReader.swipe(swipeDebitCard);
   
    }
}