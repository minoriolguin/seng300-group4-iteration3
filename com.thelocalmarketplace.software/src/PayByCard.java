
import java.math.BigDecimal;
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
public class PayByCard implements CardReaderListener {
    private Software software;
    private CardIssuer debitCardIssuer;
    private CardIssuer creditCardIssuer;
    private CardIssuer cardIssuer;

    // Hold amount for credit card issuer (default value)
    private static final int CREDIT_CARD_HOLD_AMOUNT = 10;


    /**
     * Constructs a `PayCardControl` instance.
     * @param software The software instance for the checkout station.
     */
    public PayByCard(Software software) {
        this.software = software;
        software.cardReader.register(this);

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
        if (cardType.equals("debit")) cardIssuer = debitCardIssuer;
        if (cardType.equals("credit")) cardIssuer = creditCardIssuer;
        cardIssuer.addCardData(cardNumber, cardHolder, expiryDate, cvv, amount);
    }

    /**
     * Performs a card payment.
     * @param data The card data received from the card reader.
     */
    public void makePayment(CardData data) {
        String cardNumber = data.getNumber();
        System.out.println("card number is: " + cardNumber);

        // Setting the correct CardIssuer for the given card.
        if(data.getType().equals("debit")) cardIssuer = debitCardIssuer;
        if(data.getType().equals("credit")) cardIssuer = creditCardIssuer;

        // Authorizing a hold for the payment amount. Will either be -1 (failure) or a random code (success).
        double holdAmount = software.getOrderTotal().doubleValue();
        long holdNumber = cardIssuer.authorizeHold(cardNumber, holdAmount);

        // If authorization and transaction works, payment is made.
        if(holdNumber != -1 && cardIssuer.postTransaction(cardNumber, holdNumber, holdAmount)) {
            // Payment success, release hold. Print receipt.
            cardIssuer.releaseHold(cardNumber, holdNumber);
            //software.notifyCustomer("payment succesful", software.notifyPaymentSuccess);
            software.setUpdatedOrderTotal(BigDecimal.ZERO);
            software.printReceipt.print();
        } else {
            // Payment failed, release hold.
            cardIssuer.releaseHold(cardNumber, holdNumber);
            //software.notifyCustomer("payment failed", software.notifyPaymentFailure);
        }
    }


    /**
     * The method called when the data from a card has been read by the card reader.
     * If the payment process has started, it initiates the payment; otherwise, it notifies the customer of payment failure.
     * @param data The card data received from the card reader.
     */
    @Override
    public void theDataFromACardHasBeenRead(CardData data) {
        makePayment(data);
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

    /**
     * Announces that a card has been inserted in the card reader.
     */
    @Override
    public void aCardHasBeenInserted() {

    }

    /**
     * Announces that the previously inserted card has been removed from the card
     * reader.
     */
    @Override
    public void theCardHasBeenRemoved() {

    }

    /**
     * Announces that a (tap-enabled) card has been tapped on the card reader.
     */
    @Override
    public void aCardHasBeenTapped() {

    }

    @Override
    public void aCardHasBeenSwiped() {}
}