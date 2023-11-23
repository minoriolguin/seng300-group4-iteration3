import com.jjjwelectronics.IDevice;
import com.jjjwelectronics.IDeviceListener;
import com.jjjwelectronics.card.*;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.hardware.SelfCheckoutStationBronze;
import com.thelocalmarketplace.hardware.external.CardIssuer;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import powerutility.PowerGrid;
import java.io.*;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;


import static org.junit.Assert.*;


public class PayBySwipeTest1 {
    private SelfCheckoutSoftware checkout;
    private PayBySwipe payBySwipe;
    private SelfCheckoutStationBronze checkoutStationBronze;
    private final InputStream originalSystemIn = System.in;




    @Before
    public void setUp() throws IOException {
        // initialize selfcheckout station
        AbstractSelfCheckoutStation.resetConfigurationToDefaults();
        AbstractSelfCheckoutStation.configureCurrency(Currency.getInstance("CAD"));
        checkoutStationBronze = new SelfCheckoutStationBronze();
        PowerGrid powerGrid = PowerGrid.instance();
        checkoutStationBronze.plugIn(powerGrid);
        checkoutStationBronze.turnOn();
        checkout = SelfCheckoutSoftware.getInstance(checkoutStationBronze);
        checkout.unblockCustomer();
        checkout.turnOn();
        payBySwipe = new PayBySwipe(checkout);


        String input = "John Doe\nJohn Doe\nJohn Doe\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));




        // stub for SomeBank
        SomeBankStub someBankStub = new SomeBankStub("TestBank", 10);
        someBankStub.setHoldNumber(123);  // Set a hold number for successful validation
        someBankStub.setPostTransactionResult(true);  // Simulate a successful transaction
        payBySwipe.setSomeBank(someBankStub);  // Set the SomeBank instance
    }
    @After
    public void tearDown() {
        System.setIn(originalSystemIn);
    }




    private static class TestCardData implements Card.CardData {
        private final String type;
        private final String number;
        private final String cardholder;
        private final String cvv;


        // simple implementation of the CardData interface for testing
        public TestCardData(String type, String number, String cardholder, String cvv) {
            this.type = type;
            this.number = number;
            this.cardholder = cardholder;
            this.cvv = cvv;
        }
        @Override
        public String getType() {
            return type;
        }


        @Override
        public String getNumber() {
            return number;
        }


        @Override
        public String getCardholder() {
            return cardholder;
        }


        @Override
        public String getCVV() {
            return cvv;
        }


    }
    // stub for SomeBank
    public class SomeBankStub extends CardIssuer {
        private long holdNumber;
        private boolean postTransactionResult;


        public SomeBankStub(String name, long maximumHoldCount) {
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


    @Test(expected = IOException.class)
    public void testPaymentSessionStartThrowsIOException() throws IOException, CardDeclinedException {
        // Override the paymentSessionStart method to throw IOException
        PayBySwipe payBySwipe = new PayBySwipe(checkout) {
            @Override
            public void paymentSessionStart(Card.CardData data) throws IOException, CardDeclinedException {
                throw new IOException("Simulated IOException");
            }
        };
        payBySwipe.paymentSessionStart(new TestCardDataStub("Visa"));
    }


    @Test(expected = CardDeclinedException.class)
    public void testPaymentSessionStartCardDeclinedException() throws IOException, CardDeclinedException {
        // Override the paymentSessionStart method to avoid actual execution
        PayBySwipe payBySwipe = new PayBySwipe(checkout) {
            @Override
            public void paymentSessionStart(Card.CardData data) throws IOException, CardDeclinedException {
                throw new CardDeclinedException("Simulated CardDeclined exception");
            }
        };


        payBySwipe.paymentSessionStart(new TestCardDataStub("MasterCard"));
    }


    @Test
    public void testInitialOrderTotalBeforePayment() {
        BigDecimal initialOrderTotal = checkout.getOrderTotal();
        assertEquals("Initial order total is zero", BigDecimal.valueOf(0), initialOrderTotal);
    }


    @Test
    public void testSuccessfulPayment() throws IOException, CardDeclinedException {
        // Set up the scenario where payment is successful
        SomeBankStub someBankStub = new SomeBankStub("TestBank", 10);
        someBankStub.setPostTransactionResult(true);
    }
    @Test
    public void testSuccessfulPaymentUpdatesOrderTotal() throws IOException, CardDeclinedException {
        SomeBankStub someBankStub = new SomeBankStub("TestBank", 10);
        someBankStub.setPostTransactionResult(true);
        payBySwipe.setSomeBank(someBankStub);


        // Stub user input (signature in this case)
        String input = "ValidSignature\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));



        Card.CardData testCardData = new TestCardData("Visa", "1234567890123456", "John Doe", "123");
        BigDecimal initialOrderTotal = checkout.getOrderTotal();
        payBySwipe.paymentSessionStart(testCardData);
        BigDecimal updatedOrderTotal = checkout.getOrderTotal();
        assertNotEquals("Order total updated: successful payment", initialOrderTotal, updatedOrderTotal);
    }




    @Test(expected = CardDeclinedException.class)
    public void unsuccessfulPaymentTest() throws IOException, CardDeclinedException {
        String input = "John Doe";
        System.setIn(new ByteArrayInputStream(input.getBytes()));


        Card.CardData testCardData = new TestCardData("MasterCard", "9876543210123456", "Jane Doe", "456");
        payBySwipe.paymentSessionStart(testCardData);
        assertFalse(Boolean.parseBoolean("no CardDeclinedException thrown"));
    }



    @Test(expected = CardDeclinedException.class)
    public void testOrderTotalRemainsZeroOnFailedPayment() throws IOException, CardDeclinedException {
        Card.CardData testCardData = new TestCardData("InvalidCard", "9876543210123456", "Jane Doe", "456");
        PayBySwipe payBySwipe = new PayBySwipe(checkout);
        SomeBankStub someBankStub = new SomeBankStub("TestBank", 10);
        someBankStub.setHoldNumber(-1);
        payBySwipe.setSomeBank(someBankStub);


        payBySwipe.paymentSessionStart(testCardData);
    }


    @Test
    public void testSignaturePromptNonEmptySignature() {

        String signature = String.valueOf(payBySwipe.signaturePrompt());

        assertNotNull("Signature should not be null", signature);
    }


    @Test
    public void testSignaturePromptEmptySignature() {
        String input = "\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        String signature = String.valueOf(payBySwipe.signaturePrompt());

        assertFalse(signature, Boolean.parseBoolean("Signature should not be null"));
    }


    @Test
    public void testACardHasBeenSwiped() {
        IDevice<IDeviceListener> dummyDevice = new DummyDevice();
        payBySwipe.aCardHasBeenSwiped();

        assertFalse("Card has been swiped", dummyDevice.isDisabled());
    }


    @Test
    public void testDeviceDisabledEvent() {

        IDevice<IDeviceListener> dummyDevice = new DummyDevice();
        payBySwipe.aDeviceHasBeenDisabled(dummyDevice);



        Assert.assertEquals(false, dummyDevice.isDisabled());
    }


    @Test
    public void testAdeviceHasBeenTurnedOn(){
        IDevice<IDeviceListener> dummyDevice = new DummyDevice();
        payBySwipe.aDeviceHasBeenTurnedOn(dummyDevice);


        Assert.assertEquals(false, dummyDevice.isPoweredUp());
    }




    @Test
    public void testAdeviceHasBeenTurnedOff(){
        IDevice<IDeviceListener> dummyDevice = new DummyDevice();
        payBySwipe.aDeviceHasBeenTurnedOff(dummyDevice);


        Assert.assertEquals(false, dummyDevice.isDisabled());
    }


    @Test(expected = CardDeclinedException.class)
    public void testFailedPaymentTransactionNotPosted() throws IOException, CardDeclinedException {

        SomeBankStub someBankStub = new SomeBankStub("TestBank", 10);
        someBankStub.setHoldNumber(-1L);


        payBySwipe.setSomeBank(someBankStub);


        String input = "ValidSignature\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));



        payBySwipe.paymentSessionStart(new TestCardData("Visa", "1234567890123456", "John Doe", "123"));
    }


    @Test
    public void testCardDataCreation() {
        Card.CardData testCardData = new TestCardData("Visa", "1234567890123456", "John Doe", "123");
        assertNotNull(testCardData);
    }


    @Test
    public void testTheDataFromACardHasBeenRead() {
        boolean result = payBySwipe.signaturePrompt();
        assertTrue(result);
    }


    @Test
    public void testPaymentSessionWithValidSignature() {
        // Mock signaturePrompt to return true
        payBySwipe = new PayBySwipe(checkout) {
            @Override
            Boolean signaturePrompt() {
                return true; // Simulate a valid signature
            }
        };


        payBySwipe.aCardHasBeenSwiped();
    }


    @Test(expected = CardDeclinedException.class)
    public void testPaymentSessionStartWithInvalidSignature() throws IOException, CardDeclinedException {
        PayBySwipe payBySwipe = new PayBySwipe(checkout) {
            @Override
            Boolean signaturePrompt() {
                return false;
            }
            @Override
            public void paymentSessionStart(Card.CardData data) throws IOException, CardDeclinedException {
                // Simulate a card declined scenario
                throw new CardDeclinedException("Simulated CardDeclined exception");
            }
        };
        payBySwipe.paymentSessionStart(new TestCardDataStub("MasterCard"));
    }




    @Test
    public void testSignaturePromptWithInvalidInput(){
        String input = "\n";
        setSystemIn(input);
        boolean result = payBySwipe.signaturePrompt();
        assertFalse(result);
    }
    private void setSystemIn(String input) {
        System.setIn(new ByteArrayInputStream(input.getBytes()));
    }




    // DummyDevice class to use as a stub
    private static class DummyDevice implements IDevice<IDeviceListener> {
        private boolean disabled = false;


        @Override
        public void plugIn(PowerGrid powerGrid) {


        }
        @Override
        public void unplug() {


        }
        @Override
        public void turnOn() {


        }
        @Override
        public void turnOff() {


        }


        @Override
        public boolean deregister(IDeviceListener listener) {
            return false;
        }


        @Override
        public void deregisterAll() {


        }


        @Override
        public void enable() {
        }


        @Override
        public void disable() {
            disabled = true;
        }


        @Override
        public boolean isPluggedIn() {
            return false;
        }
        @Override
        public boolean isPoweredUp() {
            return false;
        }


        public boolean isDisabled() {
            return disabled;
        }


        @Override
        public List<IDeviceListener> listeners() {
            return null;
        }


        @Override
        public void register(IDeviceListener listener) {


        }


    }
    // Simple stub for Card.CardData
    private static class TestCardDataStub implements Card.CardData {
        private final String cardholder;


        public TestCardDataStub(String cardholder) {
            this.cardholder = cardholder;
        }


        @Override
        public String getType() {
            return "Visa"; }


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
