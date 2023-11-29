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
package com.thelocalmarketplace.software.test;

import java.math.*;
import java.util.*;
import org.junit.*;
import com.tdc.CashOverloadException;
import com.tdc.DisabledException;
import com.tdc.banknote.Banknote;
import com.tdc.coin.*;
import com.thelocalmarketplace.hardware.*;
import com.thelocalmarketplace.software.AbstractPayByCash;
import com.thelocalmarketplace.software.PayByBanknote;
import com.thelocalmarketplace.software.PayByCoin;
import com.thelocalmarketplace.software.Software;

//import com.thelocalmarketplace.software.*;
import powerutility.*;

public class AbstractPayByCashTest {
    private AbstractPayByCash myAbstractPayByCoin; // test fixture
    private AbstractPayByCash myAbstractPayByBanknote; // test fixture
    private SelfCheckoutStationBronze stationBronze;
    private PowerGrid powerGrid;
    private ArrayList<BigDecimal> coindenominations;
    private BigDecimal[] billDenominations;
    private Currency CAD;
    private BigDecimal five = new BigDecimal("5.00");
    private BigDecimal ten = new BigDecimal("10.00");
    private BigDecimal nickel = new BigDecimal("0.05");
    private BigDecimal dime = new BigDecimal("0.10");
    private BigDecimal quarter = new BigDecimal("0.25");
    private BigDecimal toonie = new BigDecimal("2.00");
    private BigDecimal loonie = new BigDecimal("1.00");
    private Software station;



    @Before
    public void setUp() {
        // set up coinValidator
        coindenominations = new ArrayList<BigDecimal>();
        CAD = Currency.getInstance("CAD");
        Coin.DEFAULT_CURRENCY = CAD;
        coindenominations.add(nickel);
        coindenominations.add(dime);
        coindenominations.add(quarter);
        coindenominations.add(toonie);
        coindenominations.add(loonie);

        // set up banknoteValidator
        billDenominations = new BigDecimal[5];
        billDenominations[0] = five;
        billDenominations[1] = ten;
        billDenominations[2] = new BigDecimal("20.00");
        billDenominations[3] = new BigDecimal("50.00");
        billDenominations[4] = new BigDecimal("100.00");

        AbstractSelfCheckoutStation.resetConfigurationToDefaults();
        AbstractSelfCheckoutStation.configureCoinDenominations(coindenominations.toArray(
                new BigDecimal[coindenominations.size()]));
        AbstractSelfCheckoutStation.configureBanknoteDenominations(billDenominations);

        stationBronze = new SelfCheckoutStationBronze();
        station = Software.getInstance(stationBronze);
        powerGrid = PowerGrid.instance();
        stationBronze.plugIn(powerGrid);
        stationBronze.turnOn();
        station.turnOn();
        this.myAbstractPayByCoin = new PayByCoin(station); // myAbstractPayByCoin instance
        this.myAbstractPayByBanknote = new PayByBanknote(station); // myAbstractPayByBanknote instance
    }

    @Test
    public void testPayLess() {
        // test when payment is less than amountDue with COINS &/OR BANKNOTES
        System.out.println("Test when payment is less than amountDue:");
        station.addToOrderTotal(new BigDecimal ("6.50")); // Initialize order total to $6.50
        BigDecimal expectedLess = new BigDecimal("0.50");
        System.out.println("Order total: $" + station.getOrderTotal());
        myAbstractPayByBanknote.pay(CAD, five); // Pay with banknote, Pay only $5.00
        myAbstractPayByCoin.pay(CAD, loonie); // Pay with coin, Pay only $1.00
        BigDecimal resultLess = station.getOrderTotal();
        Assert.assertEquals(expectedLess, resultLess);

    }

    @Test
    public void testPayGreater() {
        // test when payment is greater than amountDue with BANKNOTES
        System.out.println("Test when payment is greater than amountDue:");
        station.addToOrderTotal(new BigDecimal ("6.50")); // Initialize order total to $6.50
        System.out.println("Order total: $" + station.getOrderTotal());
        myAbstractPayByBanknote.pay(CAD, ten); // Pay with banknote, Pay only $10.00
        BigDecimal resultGreater = station.getOrderTotal();
        Assert.assertEquals(BigDecimal.ZERO, resultGreater);

    }

    @Test
    public void testPayEqual() {
        // test when payment is equal to amountDue with COINS &/OR BANKNOTES
        System.out.println("Test when payment is equal to amountDue:");
        station.addToOrderTotal(new BigDecimal ("6.50")); // Initialize order total to $6.50
        System.out.println("Order total: $" + station.getOrderTotal());
        myAbstractPayByBanknote.pay(CAD, five); // Pay with banknote, Pay only $5.00
        myAbstractPayByCoin.pay(CAD, loonie); // Pay with coin, Pay only $1.00
        myAbstractPayByCoin.pay(CAD, quarter); // Pay with coin, Pay only $0.25
        myAbstractPayByCoin.pay(CAD, quarter); // Pay with coin, Pay only $0.25
        BigDecimal resultEqual = station.getOrderTotal();
        Assert.assertEquals(BigDecimal.ZERO, resultEqual);
    }

    @Test
    public void testReturnChange() {
        // test change
        System.out.println("Test for change:");
        station.addToOrderTotal(new BigDecimal("2.52"));
        System.out.println("New total = " + station.getOrderTotal());
        myAbstractPayByBanknote.pay(CAD, ten); // Pay with banknote, Pay $10.00
        System.out.println("After change = " + station.getOrderTotal());
    }

    @Test (expected = DisabledException.class)
    public void testDisabledBanknoteDispenser() throws DisabledException, CashOverloadException {
        // test when banknote dispenser is disabled
        System.out.println("Test for DisabledBanknoteDispenser:");
        station.getBanknoteDispenser().disable();
        station.getBanknoteDispenser().receive(new Banknote(CAD, five));
    }

    @Test (expected = CashOverloadException.class)
    public void testCashOverloadBanknoteDispenser() throws DisabledException, CashOverloadException{
        // test when banknote dispenser is overloaded
        System.out.println("Test for CashOverloadBanknoteDispenser:");
        for (int i = 0; i < 20; i++) { // 20 is max capacity
            station.getBanknoteDispenser().receive(new Banknote(CAD, ten)); // Add $10.00 20 times
        }
        station.getBanknoteDispenser().receive(new Banknote(CAD, ten));
    }

    @Test (expected = NoPowerException.class )
    public void testDisabledCoinTray() throws CashOverloadException, DisabledException{
        // test when coin tray is disabled
        System.out.println("Test for DisabledCoinTray:");
        station.getCoinTray().disable();
        station.getCoinTray().receive(new Coin(CAD, toonie));
    }

    @Test (expected = CashOverloadException.class)
    public void testCashOverloadCoinTray() throws CashOverloadException, DisabledException {
        // test when coin tray is overloaded
        System.out.println("Test for CashOverloadCoinTray:");
        for (int i = 0; i < 25; i++) { // 25 is max capacity
            station.getCoinTray().receive(new Coin(CAD, toonie)); // Add $2.00 25 times
        }
        station.getCoinTray().receive(new Coin(CAD, toonie));
    }

    @After
    public void tearDown() {
        myAbstractPayByCoin = null;
        myAbstractPayByBanknote = null;
    }
}