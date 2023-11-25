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

import com.jjjwelectronics.Mass;
import com.jjjwelectronics.Numeral;
import com.jjjwelectronics.scanner.Barcode;
import com.jjjwelectronics.scanner.BarcodedItem;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.SelfCheckoutStationBronze;
import com.thelocalmarketplace.hardware.external.ProductDatabases;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.*;

// Because we rely on a Bronze Scanner, some tests fail when scan fails.  They do pass most of the time.

public class BlackBoxTests {

    private SelfCheckoutStationBronze station;
    private Software software;
    private BarcodedItem inRange;
    private BarcodedProduct inRangeProduct;
    private BarcodedItem lessThanSensitivity;
    private BarcodedProduct LessThanSensitivityProduct;

    @Before
    public void Setup() {

        //Attach Station to software
        AbstractSelfCheckoutStation.resetConfigurationToDefaults();
        station = new SelfCheckoutStationBronze();
        software = Software.getInstance(station);

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
        station.handheldScanner.scan(inRange);
        //assert customer interaction disabled
        assertTrue(station.handheldScanner.isDisabled());
        assertTrue(station.mainScanner.isDisabled());
        assertTrue(station.banknoteValidator.isDisabled());
        assertTrue(station.coinValidator.isDisabled());
        assertTrue(station.cardReader.isDisabled());
        //assert expected weight updated
        assertEquals(0,inRange.getMass().compareTo(software.getExpectedTotalWeight()));
        //assert product in order, in bagged item, order total updated
        assertTrue(software.getBaggedProducts().contains(inRangeProduct));
        assertTrue(software.getBarcodedProductsInOrder().contains(inRangeProduct));
        assertEquals(inRangeProduct.getPrice(), software.getOrderTotal().longValue());
        //add item to bagging area scale
        station.baggingArea.addAnItem(inRange);
        //assert customer can now add items
        assertFalse(station.handheldScanner.isDisabled());
        assertFalse(station.mainScanner.isDisabled());

    }
    @Test
    public void addItemAndSkipBagging() {

        software.startSession();
        //select not to bag item
        software.touchScreen.skip = true;
        station.handheldScanner.scan(inRange);
        //assert expected weight not updated
        assertEquals(new Mass(BigDecimal.ZERO), software.getExpectedTotalWeight());
        //assert product in order, in not in bagged item, order total updated
        assertFalse(software.getBaggedProducts().contains(inRangeProduct));
        assertTrue(software.getBarcodedProductsInOrder().contains(inRangeProduct));
        assertEquals(inRangeProduct.getPrice(), software.getOrderTotal().longValue());
        //assert customer can now add items
        //was auto enabled by attendant
        assertFalse(station.handheldScanner.isDisabled());
        assertFalse(station.mainScanner.isDisabled());
    }
    @Test
    public void addTooLightOfProductForScaleToDetect() {
        software.startSession();
        software.touchScreen.skip = false;
        station.mainScanner.scan(lessThanSensitivity);

        //assert expected weight updated
        assertEquals(0,lessThanSensitivity.getMass().compareTo(software.getExpectedTotalWeight()));
        //assert product in order, in bagged item, order total updated
        assertTrue(software.getBaggedProducts().contains(LessThanSensitivityProduct));
        assertTrue(software.getBarcodedProductsInOrder().contains(LessThanSensitivityProduct));
        assertEquals(LessThanSensitivityProduct.getPrice(), software.getOrderTotal().longValue());
    }
    @Test
    public void removeUnBaggedItem() {
        software.startSession();
        software.touchScreen.skip = true;
        station.handheldScanner.scan(inRange);
        //know product adds from test case above, now remove and ensure its gone
        software.touchScreen.removeSelectedBarcodedProduct(inRangeProduct);
        //assert it's removed from order
        assertTrue(software.getBarcodedProductsInOrder().isEmpty());
        assertEquals(BigDecimal.ZERO, software.getOrderTotal());
        //assert that customer can add items (attendant auto verified)
        assertFalse(station.handheldScanner.isDisabled());
        assertFalse(station.mainScanner.isDisabled());
    }
    @Test
    public void removeBaggedItem() {
        //not skipping bagging
        //new session
        software.touchScreen.skip = false;
        software.startSession();
        //add item and put on scale
        station.handheldScanner.scan(inRange);
        station.baggingArea.addAnItem(inRange);
        //remove item
        software.touchScreen.removeSelectedBarcodedProduct(inRangeProduct);
        //assert customer disabled
        assertTrue(station.handheldScanner.isDisabled());
        assertTrue(station.mainScanner.isDisabled());
        assertTrue(station.banknoteValidator.isDisabled());
        assertTrue(station.coinValidator.isDisabled());
        assertTrue(station.cardReader.isDisabled());
        //assert it's removed from order
        assertTrue(software.getBaggedProducts().isEmpty());
        assertTrue(software.getBarcodedProductsInOrder().isEmpty());
        assertEquals(BigDecimal.ZERO, software.getOrderTotal());
        //remove from scale
        station.baggingArea.removeAnItem(inRange);
        //assert customer can now add items
        assertFalse(station.handheldScanner.isDisabled());
        assertFalse(station.mainScanner.isDisabled());

    }

    @Test
    public void addOwnBagsAllowableWeight(){
        // use inRange as own bags because it should be less than limit
        software.startSession();
        software.touchScreen.selectAddOwnBags();
        assertTrue(software.weightDiscrepancy.expectOwnBagsToBeAdded);
        assertTrue(station.handheldScanner.isDisabled());
        assertTrue(station.mainScanner.isDisabled());
        //add the bag
        station.baggingArea.addAnItem(inRange);
        //make sure the mass updated in weight discrepancy
        assertEquals(inRange.getMass(),software.weightDiscrepancy.massOfOwnBags);
        //make sure expected weight in software updated
        assertEquals(inRange.getMass(),software.getExpectedTotalWeight());
        software.touchScreen.bagsAdded();
        //can continue
        assertFalse(software.weightDiscrepancy.expectOwnBagsToBeAdded);
        assertFalse(station.handheldScanner.isDisabled());
        assertFalse(station.mainScanner.isDisabled());
    }

    @Test
    public void AddOwnBagsAboveAllowableLimit() {
        //change software allowable bag weight to under inRange
        software.allowableBagWeight = lessThanSensitivity.getMass();
        //select add
        software.startSession();
        software.touchScreen.selectAddOwnBags();
        assertTrue(software.weightDiscrepancy.expectOwnBagsToBeAdded);
        assertTrue(station.handheldScanner.isDisabled());
        assertTrue(station.mainScanner.isDisabled());
        //add inRange to scale
        station.baggingArea.addAnItem(inRange);
        //make sure expected weight in software is still zero
        assertEquals(Mass.ZERO,software.getExpectedTotalWeight());
        station.baggingArea.removeAnItem(inRange);
        //make sure customer can't continue after removing bag
        assertTrue(station.handheldScanner.isDisabled());
        assertTrue(station.mainScanner.isDisabled());
        //say they are done
        software.touchScreen.bagsAdded();
        //can continue
        assertFalse(software.weightDiscrepancy.expectOwnBagsToBeAdded);
        assertFalse(station.handheldScanner.isDisabled());
        assertFalse(station.mainScanner.isDisabled());
    }
    @Test
    public void attendantOverRidesWeightDiscrepancy(){
        software.startSession();
        //create discrepancy
        station.baggingArea.addAnItem(inRange);
        //assert customer disabled
        assertTrue(station.handheldScanner.isDisabled());
        assertTrue(station.mainScanner.isDisabled());
        assertTrue(station.banknoteValidator.isDisabled());
        assertTrue(station.coinValidator.isDisabled());
        assertTrue(station.cardReader.isDisabled());
        //attendant overrides
        software.attendant.OverRideWeightDiscrepancy();
        //expected weight included discrepancy
        assertEquals(inRange.getMass(),software.getExpectedTotalWeight());
        //customer enabled
        assertFalse(station.handheldScanner.isDisabled());
        assertFalse(station.mainScanner.isDisabled());

    }




}