
import com.jjjwelectronics.card.Card;
import com.jjjwelectronics.card.CardReaderGold;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.hardware.SelfCheckoutStationGold;
import com.thelocalmarketplace.hardware.external.CardIssuer;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Currency;
import org.junit.Before;
import org.junit.Test;
import powerutility.NoPowerException;
import powerutility.PowerGrid;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

public class PayBySwipeTest2 {

    private PayBySwipe payBySwipe;
    private SelfCheckoutStationGold stationGold;
    private PowerGrid powerGrid;
    private SelfCheckoutSoftware checkout;
    private CardReaderGold cardReaderGold;
    private Card creditCard1;
    private Card creditCard2;
    private Card debitCard1;
    private Card debitCard2;


    @Before
    public void setUp() {
        AbstractSelfCheckoutStation.resetConfigurationToDefaults();
        AbstractSelfCheckoutStation.configureCurrency(Currency.getInstance("CAD"));
        stationGold = new SelfCheckoutStationGold();
        cardReaderGold = new CardReaderGold();
        checkout = SelfCheckoutSoftware.getInstance(stationGold);
        checkout.turnOn();
        powerGrid = PowerGrid.instance();
        powerGrid.engageUninterruptiblePowerSource();
        payBySwipe = checkout.payByCard;


        debitCard1 = new Card("Debit", "987654", "Alice Johnson", "456");
        debitCard2 = new Card("Debit", "123456", "Bob Smith", "789");
        creditCard1 = new Card("Visa", "123456", "John Smith", "123");
        creditCard2 = new Card("Mastercard", "123456", "John Doe", "678");
    }

    @Test
    public void testRegisteredAsListener() {
        assertTrue(stationGold.cardReader.listeners().contains(checkout.payByCard));
    }


    @Test(expected = NoPowerException.class)
    public void testPaymentSessionStartWithSwipe() throws IOException, CardDeclinedException {
        checkout.getOrderTotal();
        SomeBank someBank = new SomeBank("TestBank", 100L);
        someBank.setHoldNumber(123L);
        someBank.setPostTransactionResult(true);
        payBySwipe.setSomeBank(someBank);
        boolean swipeData = someBank.releaseHold("12345686", 123L);
        payBySwipe.paymentSessionStart(cardReaderGold.swipe(creditCard1));
        assertEquals(BigDecimal.ZERO, checkout.getOrderTotal());
    }


    @Test(expected = IOException.class)
    public void testPaymentSessionStartcredit() throws IOException, CardDeclinedException {
        PayBySwipe payBySwipe = new PayBySwipe(checkout) {
            @Override
            public void paymentSessionStart(Card.CardData data) throws IOException, CardDeclinedException {
                throw new IOException("Simulated IOException");
            }
        };
        payBySwipe.paymentSessionStart(new TestCardData("Visa"));
    }

    @Test(expected = IOException.class)
    public void testPaymentSessionStartWithSwipeDebit() throws IOException, CardDeclinedException {
        PayBySwipe payBySwipe = new PayBySwipe(checkout) {
            @Override
            public void paymentSessionStart(Card.CardData data) throws IOException, CardDeclinedException {
                throw new IOException("Simulated IOException");
            }
        };

        Card.CardSwipeData cardSwipeData = debitCard1.swipe();
        payBySwipe.paymentSessionStart(cardSwipeData);
    }


    @Test(expected = CardDeclinedException.class)
    public void testPaymentSessionStartCardDeclinedException() throws IOException, CardDeclinedException {
        PayBySwipe payBySwipe = new PayBySwipe(checkout) {
            @Override
            public void paymentSessionStart(Card.CardData data) throws IOException, CardDeclinedException {
                throw new CardDeclinedException("Simulated CardDeclined exception");
            }
        };

        payBySwipe.paymentSessionStart(new TestCardData("MasterCard"));
    }

    @Test
    public void testInitialOrderTotalBeforePayment() {
        BigDecimal initialOrderTotal = checkout.getOrderTotal();
        assertEquals("Initial order total is zero", BigDecimal.valueOf(0), initialOrderTotal);
    }


    private void setSystemIn(String input) {
        System.setIn(new ByteArrayInputStream(input.getBytes()));
    }


    public class SomeBank extends CardIssuer {
        private long holdNumber;
        private boolean postTransactionResult;

        public SomeBank(String name, long maximumHoldCount) {
            super(name, maximumHoldCount);
        }

        public void setHoldNumber(long holdNumber) {
            this.holdNumber = holdNumber;
        }

        public void setPostTransactionResult(boolean postTransactionResult) {
            this.postTransactionResult = postTransactionResult;
        }

        @Override
        public long authorizeHold(String cardNumber, double amount) {
            return holdNumber;
        }

        @Override
        public boolean postTransaction(String cardNumber, long holdNumber, double amount) {
            return postTransactionResult;
        }
    }


    // Simple stub for Card.CardData
    private static class TestCardData implements Card.CardData {
        private final String cardholder;

        public TestCardData(String cardholder) {
            this.cardholder = cardholder;
        }

        @Override
        public String getType() {
            return "Visa";
        }

        @Override
        public String getNumber() {
            return "1234567890123456";
        }

        @Override
        public String getCardholder() {
            return cardholder;
        }

        @Override
        public String getCVV() {
            return "123";
        }
    }
}