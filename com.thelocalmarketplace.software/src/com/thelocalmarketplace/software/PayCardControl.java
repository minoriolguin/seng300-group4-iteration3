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
package com.thelocalmarketplace.software;
import java.util.Calendar;
import com.jjjwelectronics.IDevice;
import com.jjjwelectronics.IDeviceListener;
import com.jjjwelectronics.card.Card.CardData;
import com.jjjwelectronics.card.CardReaderListener;
import com.thelocalmarketplace.hardware.external.CardIssuer;

/**
 * The `PayCardControl` class implements the `CardReaderListener` interface to handle events triggered by the card reader.
 * It manages card payments, interacts with card issuers, and initiates the printing of receipts upon successful transactions.
 */
public class PayCardControl implements CardReaderListener {
	private CustomerStationControl customerStationControl;
	private CardIssuer debitCardIssuer;
	private CardIssuer creditCardIssuer;
	private CardIssuer cardIssuer;

	// Hold amount for credit card issuer (default value)
    private static final int CREDIT_CARD_HOLD_AMOUNT = 10;
    
    
	 /**
     * Constructs a `PayCardControl` instance.
     * @param customerStationControl The control instance for the customer station.
     */
	public PayCardControl(CustomerStationControl customerStationControl) {
		this.customerStationControl = customerStationControl;

		//Simulated available card issuers. The credit card issuer has a hold amount of 10 for this simulation.
		debitCardIssuer = new CardIssuer("creditCardIssuer", Integer.MAX_VALUE);
		creditCardIssuer = new CardIssuer("debitCardIssuer", CREDIT_CARD_HOLD_AMOUNT);
	}
	
	/**
     * Allows test cases to add cards to a card issuer's database as a substitute for
     * the lack of a pre-populated card database in this simulation.
     * @param cardType The type of the card (debit or credit).
     * @param cardNumber The card number.
     * @param cardHolder The card holder's name.
     * @param expiryDate The card's expiry date.
     * @param cvv The card's CVV.
     * @param amount The card's amount.
     */
	public void addCardData(String cardType, String cardNumber, String cardHolder, Calendar expiryDate, String cvv, int amount) {
		if (cardType == "debit") cardIssuer = debitCardIssuer;
		if (cardType == "credit") cardIssuer = creditCardIssuer;
		cardIssuer.addCardData(cardNumber, cardHolder, expiryDate, cvv, amount);
	}
	
	/**
     * Performs a card payment.
     * @param data The card data received from the card reader.
     */
	public void makePayment(CardData data) {
		String cardNumber = data.getNumber();
		
		// Setting the correct CardIssuer for the given card.
		if(data.getType().equals("debit")) cardIssuer = debitCardIssuer;
		if(data.getType().equals("credit")) cardIssuer = creditCardIssuer;
		
		// Authorizing a hold for the payment amount. Will either be -1 (failure) or a random code (success).
		double holdAmount = customerStationControl.getOrder().getTotal().doubleValue();
		long holdNumber = cardIssuer.authorizeHold(cardNumber, holdAmount);
		
		// If authorization and transaction works, payment is made.
		if(holdNumber != -1 && cardIssuer.postTransaction(cardNumber, holdNumber, holdAmount)) {
			// Payment success, release hold. Print receipt.
			cardIssuer.releaseHold(cardNumber, holdNumber);
			customerStationControl.notifyCustomer("payment succesful", customerStationControl.notifyPaymentSuccess);
			printReceipt(cardNumber);
		} else {
			// Payment failed, release hold.
			cardIssuer.releaseHold(cardNumber, holdNumber);
			customerStationControl.notifyCustomer("payment failed", customerStationControl.notifyPaymentFailure);
		}
	}
	
	
	/**
     * The method called when the data from a card has been read by the card reader.
     * If the payment process has started, it initiates the payment; otherwise, it notifies the customer of payment failure.
     * @param data The card data received from the card reader.
     */
	@Override
	public void theDataFromACardHasBeenRead(CardData data) {
		if(customerStationControl.paymentProcessStarted()) makePayment(data);
		else customerStationControl.notifyCustomer("payment failed", customerStationControl.notifyPaymentFailure);

	}
	
	/**
     * Initiates the printing of a receipt for a successful transaction.
     * @param cardNumber The card number used for the payment.
     */
	public void printReceipt(String cardNumber) {
		Receipt receipt = new Receipt(customerStationControl, cardNumber);
		customerStationControl.printReceipt(receipt.toString());
	}
	
	//Unused methods from implemented observer class
	@Override
	public void aDeviceHasBeenEnabled(IDevice<? extends IDeviceListener> device) {}
	@Override
	public void aDeviceHasBeenDisabled(IDevice<? extends IDeviceListener> device) {}
	@Override
	public void aDeviceHasBeenTurnedOn(IDevice<? extends IDeviceListener> device) {}
	@Override
	public void aDeviceHasBeenTurnedOff(IDevice<? extends IDeviceListener> device) {}
	@Override
	public void aCardHasBeenSwiped() {}
}
