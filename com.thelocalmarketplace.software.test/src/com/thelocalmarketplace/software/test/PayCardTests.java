/**
 * @author Alan Yong: 30105707
 * @author Atique Muhammad: 30038650
 * @author Ayman Momin: 30192494
 * @author Christopher Lo: 30113400
 * @author Ellen Bowie: 30191922
 * @author Emil Huseynov: 30171501
 * @author Eric George: 30173268
 * @author Kian Sieppert: 30134666
 * @author Muzammil Saleem: 30180889
 * @author Ryan Korsrud: 30173204
 * @author Sukhnaaz Sidhu: 30161587
 */
package com.thelocalmarketplace.software.test;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.thelocalmarketplace.software.CustomerStationControl;
import com.jjjwelectronics.OverloadedDevice;
import com.jjjwelectronics.card.Card;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.hardware.SelfCheckoutStationGold;
import com.thelocalmarketplace.software.test.ExampleItems.AppleJuice;
import com.thelocalmarketplace.software.test.ExampleItems.PeanutButter;
import com.thelocalmarketplace.software.test.ExampleItems.PotatoChips;

import powerutility.PowerGrid;

/**
 * This class contains tests for the payment functionality using cards in a self-checkout system.
 */
public class PayCardTests {
	// Fields to hold components for testing
	private CustomerStationControl control;
	private AbstractSelfCheckoutStation station;
	private Card debitCardLowFunds;
	private Card creditWithLowAmountHighHold;
    private Card otherCreditCard;
    private Card debitCard;

	
	/**
     * Setup method to initialize components before each test.
     * @throws OverloadedDevice Thrown if there is an issue with device initialization.
     */
	@Before
	public void setup() throws OverloadedDevice{
			// initialize station and turn on required components
			AbstractSelfCheckoutStation.resetConfigurationToDefaults();
			station = new SelfCheckoutStationGold();
			PowerGrid.engageUninterruptiblePowerSource();
			station.plugIn(PowerGrid.instance());
			station.turnOn();
			
			station.printer.addInk(1000);
			station.printer.addPaper(1000);
			
			// Initialize control and start the session
			control = new CustomerStationControl(station);
			control.startSession();
			
			// Initialize Example Item Database
			ExampleItems.updateDatabase();

			// Create and add cards with different scenarios
	        createCreditCardWithLowAmountHighHold();
	        createDebitCardWithLowFunds();
	        createOtherCreditCard();
	        createDebitCard();
	}
	
	// Helper method to create a credit card with low amount but high hold
    private void createCreditCardWithLowAmountHighHold() {
        ZonedDateTime zdt = LocalDate.now().plusYears(3).atStartOfDay(ZoneId.of("US/Mountain"));
        Calendar threeYrExpiry = GregorianCalendar.from(zdt);

        String cType = "credit";
        String cNumber = "123456789";
        String cHolder = "creditUser";
        String cCVV = "727";
        int cAmount = 5; // less than creditCardIssuer's hold amount of 10

        creditWithLowAmountHighHold = new Card(cType, cNumber, cHolder, cCVV);
        control.getPayCardController().addCardData(cType, cNumber, cHolder, threeYrExpiry, cCVV, cAmount);
    }

    // Helper method to create another credit card
    private void createOtherCreditCard() {
        ZonedDateTime zdt = LocalDate.now().plusYears(3).atStartOfDay(ZoneId.of("US/Mountain"));
        Calendar threeYrExpiry = GregorianCalendar.from(zdt);

        String cType = "credit";
        String cNumber = "987654321";
        String cHolder = "creditUser";
        String cCVV = "272";
        int cAmount = 100;

        otherCreditCard = new Card(cType, cNumber, cHolder, cCVV);
        control.getPayCardController().addCardData(cType, cNumber, cHolder, threeYrExpiry, cCVV, cAmount);
    }

    // Helper method to create a debit card
    private void createDebitCard() {
        ZonedDateTime zdt = LocalDate.now().plusYears(3).atStartOfDay(ZoneId.of("US/Mountain"));
        Calendar threeYrExpiry = GregorianCalendar.from(zdt);

        String dType = "debit";
        String dNumber = "1111222233334444";
        String dHolder = "debitUser";
        String dCVV = "333";
        int dAmount = 50;

        debitCard = new Card(dType, dNumber, dHolder, dCVV);
        control.getPayCardController().addCardData(dType, dNumber, dHolder, threeYrExpiry, dCVV, dAmount);
    }
    
    // Helper method to create a debit card
    private void createDebitCardWithLowFunds() {
        ZonedDateTime zdt = LocalDate.now().plusYears(3).atStartOfDay(ZoneId.of("US/Mountain"));
        Calendar threeYrExpiry = GregorianCalendar.from(zdt);

        String dType = "debit";
        String dNumber = "1111222233334445";
        String dHolder = "debitUser";
        String dCVV = "333";
        int dAmount = 1;

        debitCardLowFunds = new Card(dType, dNumber, dHolder, dCVV);
        control.getPayCardController().addCardData(dType, dNumber, dHolder, threeYrExpiry, dCVV, dAmount);
    }

	
    /**
     * Behavior when a successful credit card payment has been made.
     * @throws IOException
     */
	@Test
	public void creditCardPaymentTest() throws IOException {
        station.mainScanner.scan(AppleJuice.barcodedItem);
        station.baggingArea.addAnItem(ExampleItems.AppleJuice.barcodedItem);
        control.signalPay();
        station.cardReader.swipe(creditWithLowAmountHighHold);
        Assert.assertTrue(control.getCustomerNotified() == control.notifyPaymentSuccess);
	}
	
	/**
     * Behavior when a successful credit card payment has been made with another credit card.
     * @throws IOException
     */
    @Test
    public void otherCreditCardPaymentTest() throws IOException {
        station.mainScanner.scan(PeanutButter.barcodedItem);
        station.baggingArea.addAnItem(ExampleItems.PeanutButter.barcodedItem);
        control.signalPay();
        station.cardReader.swipe(otherCreditCard);
        Assert.assertTrue(control.getCustomerNotified() == control.notifyPaymentSuccess);
    }
	
	
	/** Behavior when a successful debit card payment has been made
	 * @throws IOException
	 */
	@Test
	public void debitCardPaymentTest() throws IOException {
		station.mainScanner.scan(PotatoChips.barcodedItem);
        station.baggingArea.addAnItem(ExampleItems.PotatoChips.barcodedItem);
        control.signalPay();
        station.cardReader.swipe(debitCard);
        Assert.assertTrue(control.getCustomerNotified() == control.notifyPaymentSuccess);
	}
	

	
	/** Behavior when a card that is not in the cardIssuer's database was used
	 * @throws IOException
	 */
	@Test
	public void cardNotInDatabasePaymentTest() throws IOException {
		station.mainScanner.scan(AppleJuice.barcodedItem);
        station.baggingArea.addAnItem(ExampleItems.AppleJuice.barcodedItem);
        control.signalPay();
        station.cardReader.swipe(new Card("debit", "111111111", "CardUser", "777"));
        Assert.assertTrue(control.getCustomerNotified() == control.notifyPaymentFailure);
	}
	
	
	/** Behavior when a card swipe occurs before the customer declares that they are ready to pay
	 * @throws IOException
	 */
	@Test
	public void swipeBeforeSignalPayTest() throws IOException {
		// Scan an item before signaling to pay
	    station.mainScanner.scan(AppleJuice.barcodedItem);
	    station.baggingArea.addAnItem(ExampleItems.AppleJuice.barcodedItem);

	    // Swipe a card before signaling to pay
	    station.cardReader.swipe(new Card("debit", "111111111", "CardUser", "777"));

	    // Ensure that the customer is notified of payment failure
	    Assert.assertTrue(control.getCustomerNotified() == control.notifyPaymentFailure);
	}
	
	
	/**
     * Behavior when a credit card with low amount but high hold can purchase an item that costs more than the amount.
     * @throws IOException
     */
    @Test
    public void creditCardLowAmountHighHoldCanBuyExpensiveItemTest() throws IOException {
        station.mainScanner.scan(PotatoChips.barcodedItem);
        station.baggingArea.addAnItem(ExampleItems.PotatoChips.barcodedItem);
        control.signalPay();
        station.cardReader.swipe(creditWithLowAmountHighHold);
        Assert.assertTrue(control.getCustomerNotified() == control.notifyPaymentSuccess);
    }
	
	/** Behavior when a card with not enough funds is used to attempt a purchase
	 * @throws IOException
	 */
	@Test
	public void notEnoughCardPaymentTest() throws IOException {
		station.mainScanner.scan(AppleJuice.barcodedItem);
		station.baggingArea.addAnItem(ExampleItems.AppleJuice.barcodedItem);
		station.mainScanner.scan(PotatoChips.barcodedItem);
		station.baggingArea.addAnItem(ExampleItems.PotatoChips.barcodedItem);
		station.mainScanner.scan(PeanutButter.barcodedItem);
		station.baggingArea.addAnItem(ExampleItems.PeanutButter.barcodedItem);
		control.signalPay();
		station.cardReader.swipe(debitCardLowFunds);
		Assert.assertTrue(control.getCustomerNotified() == control.notifyPaymentFailure);
	}
}