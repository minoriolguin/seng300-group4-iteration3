package com.thelocalmarketplace.software.test.controllers;

import com.jjjwelectronics.card.Card;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.hardware.SelfCheckoutStationGold;
import com.thelocalmarketplace.hardware.external.CardIssuer;
import com.thelocalmarketplace.software.logic.CentralStationLogic;
import com.thelocalmarketplace.software.logic.CentralStationLogic.PaymentMethods;
import com.thelocalmarketplace.software.logic.StateLogic.States;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import powerutility.PowerGrid;

import ca.ucalgary.seng300.simulation.SimulationException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Calendar;

import static org.junit.Assert.assertEquals;

/**
 * @author Maheen Nizmani (30172615)
 * ---------------------------------
 * @author Angelina Rochon (30087177)
 * @author Connell Reffo (10186960)
 * @author Tara Strickland (10105877)
 * @author Julian Fan (30235289)
 * @author Braden Beler (30084941)
 * @author Samyog Dahal (30194624)
 * @author Phuong Le (30175125)
 * @author Daniel Yakimenka (10185055)
 * @author Merick Parkinson (30196225)
 * @author Farida Elogueil (30171114)
 */
public class PayBySwipeTests {

    SelfCheckoutStationGold station;
    CentralStationLogic session;

    CardIssuer bank;

    Card card;


    /*ensures failure is not a result of magnetic strip failure*/
    public void swipeUntilCardAccepted() throws IOException {

        do{
            session.hardware.cardReader.swipe(this.card);
        } while(!session.cardLogic.isDataRead());

    }
    @Before
    public void setup() {

        PowerGrid.engageUninterruptiblePowerSource();
        PowerGrid.instance().forcePowerRestore();

        AbstractSelfCheckoutStation.resetConfigurationToDefaults();

        station=new SelfCheckoutStationGold();
        station.plugIn(PowerGrid.instance());
        station.turnOn();


        session = new CentralStationLogic(station);
        session.startSession();

        //set up bank details
        CardIssuer bank= new CardIssuer("Scotia Bank",3);
        session.setupBankDetails(bank);
        this.card = new Card("DEBIT","123456789","John","329");
        Calendar expiry = Calendar.getInstance();
        expiry.set(2025,Calendar.JANUARY,24);
        bank.addCardData("123456789", "John",expiry,"329",32.00);


        this.session.selectPaymentMethod(PaymentMethods.DEBIT);
    }

    @After
    public void tearDown() {
        PowerGrid.engageFaultyPowerSource();
    }

    @Test(expected=SimulationException.class)
    public void testInValidState() throws IOException {
        session.cartLogic.updateBalance(BigDecimal.valueOf(10.00));
        session.hardware.cardReader.enable();
        swipeUntilCardAccepted();

    }

    @Test
    public void testValidTransaction() throws IOException {
        session.cartLogic.updateBalance(BigDecimal.valueOf(10.00));
        session.hardware.cardReader.enable();
        session.stateLogic.gotoState(States.CHECKOUT);
        swipeUntilCardAccepted();
        assertEquals(BigDecimal.valueOf(0.0),session.cartLogic.getBalanceOwed());

    }

    @Test(expected=SimulationException.class)
    public void testSessionNotStartedSwipe() throws IOException{
        session.stopSession();
        session.hardware.cardReader.enable();
        session.stateLogic.gotoState(States.CHECKOUT);
        swipeUntilCardAccepted();
    }

    @Test
    public void testDeclinedTransaction() throws IOException {
        session.cartLogic.updateBalance(BigDecimal.valueOf(50.00));
        session.hardware.cardReader.enable();
        session.stateLogic.gotoState(States.CHECKOUT);
        swipeUntilCardAccepted();
        assertEquals(BigDecimal.valueOf(50.0),session.cartLogic.getBalanceOwed());
    }
    
    @Test(expected=SimulationException.class)
    public void testWrongSwipeMethodSelected() throws IOException {
        this.session.selectPaymentMethod(PaymentMethods.CREDIT);
        session.cartLogic.updateBalance(BigDecimal.valueOf(10.00));
        session.hardware.cardReader.enable();
        session.stateLogic.gotoState(States.CHECKOUT);
        swipeUntilCardAccepted();
    }

    @Test(expected=SimulationException.class)
    public void testStationBlockedSwipe()throws IOException{
        session.cartLogic.updateBalance(BigDecimal.valueOf(10.00));
        session.stateLogic.gotoState(States.BLOCKED);
        session.hardware.cardReader.enable();
        swipeUntilCardAccepted();
    }

    @Test(expected=SimulationException.class)
    public void tesSessionNotStartedSwipe() throws IOException {
        session.cartLogic.updateBalance(BigDecimal.valueOf(10.00));
        session.hardware.cardReader.enable();
        swipeUntilCardAccepted();

    }
    
    @Test
    public void testGetCardPaymentTypeDebit() {
    	Card c = new Card("deBiT","123456789","John","329");
    	
    	assertEquals(PaymentMethods.DEBIT, session.cardLogic.getCardPaymentType(c.kind));
    }
    
    @Test
    public void testGetCardPaymentTypeCredit() {
    	Card c = new Card("CreDIt","123456789","John","329");
    	
    	assertEquals(PaymentMethods.CREDIT, session.cardLogic.getCardPaymentType(c.kind));
    }
    
    @Test
    public void testGetCardPaymentTypeNone() {
    	Card c = new Card("fdsgds","123456789","John","329");
    	
    	assertEquals(PaymentMethods.NONE, session.cardLogic.getCardPaymentType(c.kind));
    }
}
