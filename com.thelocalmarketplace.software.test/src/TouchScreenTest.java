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

import com.jjjwelectronics.Numeral;
import com.jjjwelectronics.scanner.Barcode;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.SelfCheckoutStationGold;
import com.thelocalmarketplace.hardware.external.ProductDatabases;
import org.junit.Before;
import org.junit.Test;
import powerutility.PowerGrid;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TouchScreenTest {
    private SelfCheckoutSoftware checkout;
    private SelfCheckoutStationGold station;
    private BarcodedProduct product;
    private Barcode barcode;
    private TouchScreen touchScreen;


    @Before
    public void setUp() {
        PowerGrid.engageUninterruptiblePowerSource();
        SelfCheckoutStationGold.resetConfigurationToDefaults();
        station = new SelfCheckoutStationGold();
        checkout = SelfCheckoutSoftware.getInstance(station);
        checkout.turnOn();
        touchScreen = new TouchScreen(checkout);
        Numeral[] testBarcode = new Numeral[4];
        testBarcode[0] = Numeral.nine;
        testBarcode[1] = Numeral.five;
        testBarcode[2] = Numeral.eight;
        testBarcode[3] = Numeral.eight;
        barcode = new Barcode(testBarcode);
        product = new BarcodedProduct(barcode, "test", 5, 100);
        ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode, product);
    }

    @Test
    public void payByCointest() {
        touchScreen.payByCoin();
        assertFalse(checkout.coinValidator.isDisabled());
        assertTrue(checkout.coinValidator.isActivated());
    }

    @Test
    public void payByBanknotetest() {
        touchScreen.payByBanknote();
        assertFalse(checkout.banknoteValidator.isDisabled());
        assertTrue(checkout.banknoteValidator.isActivated());
    }

    @Test
    public void payBySwipe() {
        touchScreen.payBySwipe();
        assertFalse(checkout.cardReader.isDisabled());
        assertFalse(checkout.printer.isDisabled());
    }
    
    @Test
    public void testRemoveSelectedBarcodedProduct() {
        touchScreen.removeSelectedBarcodedProduct(product);
        assertFalse(checkout.getBarcodedProductsInOrder().contains(product));
    }


    @Test
    public void testSelectAddOwnBags() {
        touchScreen.selectAddOwnBags();
        assertTrue(checkout.mainScanner.isDisabled());
        assertTrue(checkout.handHeldScanner.isDisabled());
        assertTrue(checkout.weightDiscrepancy.expectOwnBagsToBeAdded);
    }

    @Test
    public void testBagsAdded() {
        touchScreen.selectAddOwnBags();
        touchScreen.bagsAdded();
        assertFalse(checkout.mainScanner.isDisabled());
        assertFalse(checkout.handHeldScanner.isDisabled());
        assertFalse(checkout.weightDiscrepancy.expectOwnBagsToBeAdded);
    }
}