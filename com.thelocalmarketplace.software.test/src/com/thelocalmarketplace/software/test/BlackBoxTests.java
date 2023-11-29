package com.thelocalmarketplace.software.test;

import com.jjjwelectronics.Mass;
import com.jjjwelectronics.Numeral;
import com.jjjwelectronics.scanner.Barcode;
import com.jjjwelectronics.scanner.BarcodedItem;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.SelfCheckoutStationBronze;
import com.thelocalmarketplace.hardware.external.ProductDatabases;
import com.thelocalmarketplace.software.Software;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.*;

// Because we rely on a Bronze Scanner, some tests fail when scan fails.  They do pass most of the time.

public class BlackBoxTests {

    private SelfCheckoutStationBronze hardware;
    private Software software;
    private BarcodedItem inRange;
    private BarcodedProduct inRangeProduct;
    private BarcodedItem lessThanSensitivity;
    private BarcodedProduct LessThanSensitivityProduct;

    @Before
    public void Setup() {

        //Attach Station to software
        AbstractSelfCheckoutStation.resetConfigurationToDefaults();
        hardware = new SelfCheckoutStationBronze();
        software = Software.getInstance(hardware);

        //create barcoded products to test with
        Numeral[] InRangebar = new Numeral[3];
        InRangebar[0] = Numeral.zero;
        InRangebar[1] = Numeral.one;
        InRangebar[2] = Numeral.two;
        Barcode InRangebarcode = new Barcode(InRangebar);
        double expectedWeight = 100;
        String description = "rice";
        long price = 10;
        inRangeProduct = new BarcodedProduct(InRangebarcode, description, price, expectedWeight);

        Numeral[] LessthanSensitivityBar = new Numeral[3];
        LessthanSensitivityBar[0] = Numeral.zero;
        LessthanSensitivityBar[1] = Numeral.one;
        LessthanSensitivityBar[2] = Numeral.one;
        Barcode lessThanSensitivityBarcode = new Barcode(LessthanSensitivityBar);
        double lessThanSensitivityExpectedWeight = 4;
        String Candydescription = "candy";
        long candyPrice = 1;
        LessThanSensitivityProduct = new BarcodedProduct(lessThanSensitivityBarcode, Candydescription, candyPrice, lessThanSensitivityExpectedWeight);

        //add them to database
        ProductDatabases.BARCODED_PRODUCT_DATABASE.put(InRangebarcode,inRangeProduct);
        ProductDatabases.BARCODED_PRODUCT_DATABASE.put(lessThanSensitivityBarcode,LessThanSensitivityProduct);

        //create Item version to add to scale
        Mass inRangeMass = new Mass(inRangeProduct.getExpectedWeight());
        inRange = new BarcodedItem(inRangeProduct.getBarcode(),inRangeMass);
        Mass less = new Mass(LessThanSensitivityProduct.getExpectedWeight());
        lessThanSensitivity = new BarcodedItem(LessThanSensitivityProduct.getBarcode(),less);

        //fire it up!
        software.turnOn();

    }

    @Test
    public void AddInRangeProductToOrder() {

        //not skipping bagging
        //new session
        software.touchScreen.skip = false;
        software.startSession();
        //ensure the items from setup in Data Base
        assertEquals(2,ProductDatabases.BARCODED_PRODUCT_DATABASE.size());
        //scan an item in the baggingArea scales range (over-sensitivity, less than max)
        hardware.getHandheldScanner().scan(inRange);
        //assert customer interaction disabled
        assertTrue(hardware.getHandheldScanner().isDisabled());
        assertTrue(hardware.getMainScanner().isDisabled());
        assertTrue(hardware.getBanknoteValidator().isDisabled());
        assertTrue(hardware.getCoinValidator().isDisabled());
        assertTrue(hardware.getCardReader().isDisabled());
        //assert expected weight updated
        assertEquals(0,inRange.getMass().compareTo(software.getExpectedTotalWeight()));
        //assert product in order, in bagged item, order total updated
        assertTrue(software.getBaggedProducts().containsKey(inRangeProduct));
        assertTrue(software.getProductsInOrder().containsKey(inRangeProduct));
        assertEquals(inRangeProduct.getPrice(), software.getOrderTotal().longValue());
        //add item to bagging area scale
        hardware.getBaggingArea().addAnItem(inRange);
        //assert customer can now add items
        assertFalse(hardware.getHandheldScanner().isDisabled());
        assertFalse(hardware.getMainScanner().isDisabled());

    }
    @Test
    public void addItemAndSkipBagging() {

        software.startSession();
        //select not to bag item
        software.touchScreen.skip = true;
        hardware.getHandheldScanner().scan(inRange);
        //assert expected weight not updated
        assertEquals(new Mass(BigDecimal.ZERO), software.getExpectedTotalWeight());
        //assert product in order, in not in bagged item, order total updated
        assertFalse(software.getBaggedProducts().containsKey(inRangeProduct));
        assertTrue(software.getProductsInOrder().containsKey(inRangeProduct));
        assertEquals(inRangeProduct.getPrice(), software.getOrderTotal().longValue());
        //assert customer can now add items
        //was auto enabled by attendant
        assertFalse(hardware.getHandheldScanner().isDisabled());
        assertFalse(hardware.getMainScanner().isDisabled());
    }
    @Test
    public void addTooLightOfProductForScaleToDetect() {
        software.startSession();
        software.touchScreen.skip = false;
        hardware.getMainScanner().scan(lessThanSensitivity);

        //assert expected weight updated
        assertEquals(0,lessThanSensitivity.getMass().compareTo(software.getExpectedTotalWeight()));
        //assert product in order, in bagged item, order total updated
        assertTrue(software.getBaggedProducts().containsKey(LessThanSensitivityProduct));
        assertTrue(software.getProductsInOrder().containsKey(LessThanSensitivityProduct));
        assertEquals(LessThanSensitivityProduct.getPrice(), software.getOrderTotal().longValue());
    }
    @Test
    public void removeUnBaggedItem() {
        software.startSession();
        software.touchScreen.skip = true;
        hardware.getHandheldScanner().scan(inRange);
        //know product adds from test case above, now remove and ensure its gone
        software.touchScreen.removeSelectedBarcodedProduct(inRangeProduct);
        //assert it's removed from order
        assertTrue(software.getBarcodedProductsInOrder().isEmpty());
        assertEquals(BigDecimal.ZERO, software.getOrderTotal());
        //assert that customer can add items (attendant auto verified)
        assertFalse(hardware.getHandheldScanner().isDisabled());
        assertFalse(hardware.getMainScanner().isDisabled());
    }
    @Test
    public void removeBaggedItem() {
        //not skipping bagging
        //new session
        software.touchScreen.skip = false;
        software.startSession();
        //add item and put on scale
        hardware.getHandheldScanner().scan(inRange);
        hardware.getBaggingArea().addAnItem(inRange);
        //remove item
        software.touchScreen.removeSelectedBarcodedProduct(inRangeProduct);
        //assert customer disabled
        assertTrue(hardware.getHandheldScanner().isDisabled());
        assertTrue(hardware.getMainScanner().isDisabled());
        assertTrue(hardware.getBanknoteValidator().isDisabled());
        assertTrue(hardware.getCoinValidator().isDisabled());
        assertTrue(hardware.getCardReader().isDisabled());
        //assert it's removed from order
        assertTrue(software.getBaggedProducts().isEmpty());
        assertTrue(software.getBarcodedProductsInOrder().isEmpty());
        assertEquals(BigDecimal.ZERO, software.getOrderTotal());
        //remove from scale
        hardware.getBaggingArea().removeAnItem(inRange);
        //assert customer can now add items
        assertFalse(hardware.getHandheldScanner().isDisabled());
        assertFalse(hardware.getMainScanner().isDisabled());

    }

    @Test
    public void addOwnBagsAllowableWeight(){
        // use inRange as own bags because it should be less than limit
        software.startSession();
        software.touchScreen.selectAddOwnBags();
        assertTrue(software.weightDiscrepancy.expectOwnBagsToBeAdded);
        assertTrue(hardware.getHandheldScanner().isDisabled());
        assertTrue(hardware.getMainScanner().isDisabled());
        //add the bag
        hardware.getBaggingArea().addAnItem(inRange);
        //make sure the mass updated in weight discrepancy
        assertEquals(inRange.getMass(),software.weightDiscrepancy.massOfOwnBags);
        //make sure expected weight in software updated
        assertEquals(inRange.getMass(),software.getExpectedTotalWeight());
        software.touchScreen.bagsAdded();
        //can continue
        assertFalse(software.weightDiscrepancy.expectOwnBagsToBeAdded);
        assertFalse(hardware.getHandheldScanner().isDisabled());
        assertFalse(hardware.getMainScanner().isDisabled());
    }

    @Test
    public void AddOwnBagsAboveAllowableLimit() {
        //change software allowable bag weight to under inRange
        software.allowableBagWeight = lessThanSensitivity.getMass();
        //select add
        software.startSession();
        software.touchScreen.selectAddOwnBags();
        assertTrue(software.weightDiscrepancy.expectOwnBagsToBeAdded);
        assertTrue(hardware.getHandheldScanner().isDisabled());
        assertTrue(hardware.getMainScanner().isDisabled());
        //add inRange to scale
        hardware.getBaggingArea().addAnItem(inRange);
        //make sure expected weight in software is still zero
        assertEquals(Mass.ZERO,software.getExpectedTotalWeight());
        hardware.getBaggingArea().removeAnItem(inRange);
        //make sure customer can't continue after removing bag
        assertTrue(hardware.getHandheldScanner().isDisabled());
        assertTrue(hardware.getMainScanner().isDisabled());
        //say they are done
        software.touchScreen.bagsAdded();
        //can continue
        assertFalse(software.weightDiscrepancy.expectOwnBagsToBeAdded);
        assertFalse(hardware.getHandheldScanner().isDisabled());
        assertFalse(hardware.getMainScanner().isDisabled());
    }
    @Test
    public void attendantOverRidesWeightDiscrepancy(){
        software.startSession();
        //create discrepancy
        hardware.getBaggingArea().addAnItem(inRange);
        //assert customer disabled
        assertTrue(hardware.getHandheldScanner().isDisabled());
        assertTrue(hardware.getMainScanner().isDisabled());
        assertTrue(hardware.getBanknoteValidator().isDisabled());
        assertTrue(hardware.getCoinValidator().isDisabled());
        assertTrue(hardware.getCardReader().isDisabled());
        //attendant overrides
        software.attendant.OverRideWeightDiscrepancy();
        //expected weight included discrepancy
        assertEquals(inRange.getMass(),software.getExpectedTotalWeight());
        //customer enabled
        assertFalse(hardware.getHandheldScanner().isDisabled());
        assertFalse(hardware.getMainScanner().isDisabled());

    }
}