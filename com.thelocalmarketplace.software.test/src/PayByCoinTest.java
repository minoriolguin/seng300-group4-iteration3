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

import static org.junit.Assert.assertEquals;

import java.math.*;
import java.util.*;
import org.junit.*;

import com.tdc.IComponent;
import com.tdc.IComponentObserver;
import com.tdc.coin.*;
import com.thelocalmarketplace.hardware.*;
//import com.thelocalmarketplace.software.*;
import powerutility.*;

public class PayByCoinTest {
    private PayByCoin myPayByCoin; // test fixture
    private SelfCheckoutStationBronze stationBronze;
    private PowerGrid powerGrid;
    private ArrayList<BigDecimal> coindenominations;
    private Currency CAD;
    private BigDecimal nickel = new BigDecimal("0.05");
    private BigDecimal dime = new BigDecimal("0.10");
    private BigDecimal quarter = new BigDecimal("0.25");
    private BigDecimal toonie = new BigDecimal("2.00");
    private BigDecimal loonie = new BigDecimal("1.00");
    private SelfCheckoutSoftware station;
    private IComponent<IComponentObserver> myFakeComponent;

    @Before
    public void setUp() {
        // Set up CoinValidator
        coindenominations = new ArrayList<BigDecimal>();
        CAD = Currency.getInstance("CAD");
        Coin.DEFAULT_CURRENCY = CAD;
        coindenominations.add(nickel);
        coindenominations.add(dime);
        coindenominations.add(quarter);
        coindenominations.add(toonie);
        coindenominations.add(loonie);

        AbstractSelfCheckoutStation.resetConfigurationToDefaults();
        AbstractSelfCheckoutStation.configureCoinDenominations(coindenominations.toArray(
                new BigDecimal[coindenominations.size()]));

        stationBronze = new SelfCheckoutStationBronze();
        station = SelfCheckoutSoftware.getInstance(stationBronze);
        powerGrid = PowerGrid.instance();
        stationBronze.plugIn(powerGrid);
        stationBronze.turnOn();
        station.turnOn();
        myPayByCoin = new PayByCoin(station); // myPaybyCoin instance
        myFakeComponent = new FakeComponent();
    }

    @Test
    public void testEnabled() {
    	//Test Enabled Status
        myFakeComponent.enable();
        myPayByCoin.enabled(myFakeComponent);
        // Device should be enabled so false
        Assert.assertFalse(myFakeComponent.isDisabled());
    }

    @Test
    public void testDisabled() {
    	// Test Disabled Status
        myFakeComponent.disable();
        myPayByCoin.disabled(myFakeComponent);
        // Device should be disabled so true
        Assert.assertTrue(myFakeComponent.isDisabled());
    }
    @Test
    public void testTurnedOn() {
    	// Test Device is On
        myFakeComponent.activate();
        myPayByCoin.turnedOn(myFakeComponent);
        // Device should be activated (on) so True
        Assert.assertTrue(myFakeComponent.isActivated());
    }
    @Test
    public void testTurnedOff() {
    	// Test Device is Off
        myFakeComponent.connect(powerGrid);
        myFakeComponent.disactivate();
        myPayByCoin.turnedOff(myFakeComponent);
        // Device should be disactivated (off) so False
        Assert.assertFalse(myFakeComponent.isActivated());
    }

    @Test
    public void testValidCoinDetectedLess() {
        // test when payment is less than amountDue
        System.out.println("Test when payment is less than amountDue:");
        station.addToOrderTotal(new BigDecimal ("4.50")); // Initialize order total to $4.50
        BigDecimal expectedLess = new BigDecimal("3.50");
        System.out.println("Order total: $" + station.getOrderTotal());
        myPayByCoin.validCoinDetected(station.coinValidator, loonie); // Pay with coin, Pay only $1.00
        BigDecimal resultLess = station.getOrderTotal();
        Assert.assertEquals(expectedLess, resultLess);
    }

    @Test
    public void testValidCoinDetectedGreater() {
        // test when payment is greater than amountDue
        System.out.println("Test when payment is greater than amountDue:");
        station.addToOrderTotal(new BigDecimal ("4.50")); // Initialize order total to $4.50
        System.out.println("Order total: $" + station.getOrderTotal());
        myPayByCoin.validCoinDetected(station.coinValidator, toonie); // Pay with coin, Pay $2.00
        myPayByCoin.validCoinDetected(station.coinValidator, toonie); // Pay with coin, Pay $2.00
        myPayByCoin.validCoinDetected(station.coinValidator, toonie); // Pay with coin, Pay $2.00
        BigDecimal resultGreater = station.getOrderTotal();
        assertEquals(BigDecimal.ZERO, resultGreater);
    }

    @Test
    public void testValidCoinDetectedEqual() {
        // test when payment is equal to amountDue
        System.out.println("Test when payment is equal to amountDue:");
        station.addToOrderTotal(new BigDecimal ("4.50")); // Initialize order total to $4.50
        System.out.println("Order total: $" + station.getOrderTotal());
        myPayByCoin.validCoinDetected(station.coinValidator, toonie); // Pay with coin, Pay $2.00
        myPayByCoin.validCoinDetected(station.coinValidator, toonie); // Pay with coin, Pay $2.00
        myPayByCoin.validCoinDetected(station.coinValidator, quarter); // Pay with coin, Pay $0.25
        myPayByCoin.validCoinDetected(station.coinValidator, quarter); // Pay with coin, Pay $0.25
        BigDecimal resultEqual = station.getOrderTotal();
        assertEquals(BigDecimal.ZERO, resultEqual);
    }

    @Test
    public void testInvalidCoinDetected() {
    	//Test when invalid Coin is detected
        myPayByCoin.invalidCoinDetected(station.coinValidator);
    }

    // fake component stub
    private static class FakeComponent implements IComponent<IComponentObserver>{
        private boolean disabled;
        private boolean turnedOn;

        @Override
        public boolean isConnected() {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean isActivated() {
            // TODO Auto-generated method stub
            return turnedOn;
        }

        @Override
        public boolean hasPower() {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public void connect(PowerGrid grid) {
            // TODO Auto-generated method stub

        }

        @Override
        public void disconnect() {
            // TODO Auto-generated method stub

        }

        @Override
        public void activate() {
            // TODO Auto-generated method stub
            turnedOn = true;
        }

        @Override
        public void disactivate() {
            // TODO Auto-generated method stub
            turnedOn = false;
        }

        @Override
        public boolean detach(IComponentObserver observer) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public void detachAll() {
            // TODO Auto-generated method stub

        }

        @Override
        public void attach(IComponentObserver observer) {
            // TODO Auto-generated method stub

        }

        @Override
        public void disable() {
            // TODO Auto-generated method stub
            disabled = true;

        }

        @Override
        public void enable() {
            // TODO Auto-generated method stub
        }

        @Override
        public boolean isDisabled() {
            // TODO Auto-generated method stub
            return disabled;
        }

    }

    @After
    public void tearDown() {
        myPayByCoin = null;
    }
}