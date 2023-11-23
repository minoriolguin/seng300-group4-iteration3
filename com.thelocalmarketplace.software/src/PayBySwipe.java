// Project 2 Iteration Group 3
//Julie Kim 10123567
//Aryaman Sandhu 30017164
//Arcleah Pascual 30056034
//Aoi Ueki 30179305
//Ernest Shukla 30156303
//Shawn Hanlon 10021510
//Jaimie Marchuk 30112841
//Sofia Rubio 30113733
//Maria Munoz 30175339
//Anne Lumumba 30171346
//Nathaniel Dafoe 30181948

import com.jjjwelectronics.IDevice;
import com.jjjwelectronics.IDeviceListener;
import com.jjjwelectronics.card.Card;
import com.jjjwelectronics.card.CardReaderListener;
import com.thelocalmarketplace.hardware.external.CardIssuer;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Scanner;

/**
 * Manages credit/debit card swipe transactions during a payment session.
 */
public class PayBySwipe implements CardReaderListener {

    private CardIssuer someBank;
    private SelfCheckoutSoftware checkoutSoftware;
    private BigDecimal paymentAmount;
    private String signature;
//    private PaymentMethod cardMethod;     unnecessary for now -- also should be in extended Pay class. Payment method only includes Cash/Credit/Debit

    public PayBySwipe(SelfCheckoutSoftware station){
        this.checkoutSoftware = station;
        station.cardReader.register(this);
    }

//  -------------------------------------CardReaderListener Implementation--------------------------------------
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
    public void theDataFromACardHasBeenRead(Card.CardData data) {
        // data has : card kind(visa/MC/etc), cardholder name, card number, cvv
        if(signaturePrompt()) { // this should prompt for a valid signature before starting session payment
            try { //  change to while if we need to repeat till success
                paymentSessionStart(data);
            } catch (IOException | CardDeclinedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    //  -------------------------------------x CardReaderListener Implementation x-------------------------------------

    /**
     * Initiates a payment session for the specified payment amount and card.
     * Relying on helper methods, card validation from the bank, placing holds and transaction posting occurs from this
     * method.
     *
     * @param data the information received about the used card
     * @throws IOException If an I/O error occurs during the payment session.
     * @throws CardDeclinedException If a card validation error occurs during the payment session
     */
    public void paymentSessionStart(Card.CardData data) throws IOException, CardDeclinedException {
        this.paymentAmount = checkoutSoftware.getOrderTotal();

        String cardNumber = data.getNumber();

        if (bankValidation(cardNumber, signature) != -1) {
            boolean paymentSuccessful =
                    someBank.postTransaction(cardNumber, bankValidation(cardNumber, signature), paymentAmount.doubleValue());
            if (paymentSuccessful) {
                checkoutSoftware.subtractFromOrderTotal(paymentAmount);
                System.out.println("Payment of " + paymentAmount + "successful");
                checkoutSoftware.printReceipt.print(checkoutSoftware.getBarcodedProductsInOrder());
                checkoutSoftware.endSession();
            } else {
                System.out.println("Transaction not posted");
                throw new CardDeclinedException();
            }
        } else {
            System.out.println("Cannot place hold for payment");
            throw new CardDeclinedException();
        }

    }

    /**
     * Prompts the customer for a signature through user input.
     * @return The entered signature.
     */
    private Boolean signaturePrompt() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Please enter signature");
        signature = scanner.nextLine();
        System.out.println("You entered: " + signature);

        return signature != null && !signature.isEmpty();
    }

    /**
     * Validates the credit card with the provided number and signature.
     * If the validation is successful, returns the hold number; otherwise, releases the hold and returns -1.
     * Not sure how to validate the signature yet
     * @param number    The credit card number.
     * @param signature The customer's signature.
     * @return The hold number if successful, -1 otherwise.
     */
    private long bankValidation(String number, String signature) {
        // send signature where it needs to go
        long holdNumber = someBank.authorizeHold(number, paymentAmount.doubleValue()); // use case for signalling to bank for a hold
        if (signaturePrompt() && holdNumber != -1) {
            return holdNumber;
        }
        someBank.releaseHold(number, paymentAmount.longValue());
        return holdNumber;
    }
}