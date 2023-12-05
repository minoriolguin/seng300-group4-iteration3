 /**
 *Project, Iteration 3, Group 4
 *  Group Members:
 * - Arvin Bolbolanardestani / 30165484
 * - Anthony Chan / 30174703
 * - Marvellous Chukwukelu / 30197270
 * - Farida Elogueil / 30171114
 * - Ahmed Elshabasi / 30188386
 * - Shawn Hanlon / 10021510
 * - Steven Huang / 30145866
 * - Nada Mohamed / 30183972
 * - Jon Mulyk / 30093143
 * - Althea Non / 30172442
 * - Minori Olguin / 30035923
 * - Kelly Osena / 30074352
 * - Muhib Qureshi / 30076351
 * - Sofia Rubio / 30113733
 * - Muzammil Saleem / 30180889
 * - Steven Susorov / 30197973
 * - Lydia Swiegers / 30174059
 * - Elizabeth Szentmiklossy / 30165216
 * - Anthony Tolentino / 30081427
 * - Johnny Tran / 30140472
 * - Kaylee Xiao / 30173778 
 **/

package com.thelocalmarketplace.software.test;

import com.jjjwelectronics.Numeral;
import com.jjjwelectronics.scanner.Barcode;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.SelfCheckoutStationGold;
import com.thelocalmarketplace.hardware.external.ProductDatabases;
import com.thelocalmarketplace.software.Software;
import com.thelocalmarketplace.software.TouchScreen;

import org.junit.Before;
import org.junit.Test;
import powerutility.PowerGrid;

import java.io.IOException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TouchScreenTest {
    private Software checkout;
    private SelfCheckoutStationGold station;
    private BarcodedProduct product;
    private Barcode barcode;
    private TouchScreen touchScreen;


    @Before
    public void setUp() {
        PowerGrid.engageUninterruptiblePowerSource();
        SelfCheckoutStationGold.resetConfigurationToDefaults();
        station = new SelfCheckoutStationGold();
        checkout = Software.getInstance(station);
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
        touchScreen.insertBanknote();
        assertFalse(checkout.banknoteValidator.isDisabled());
        assertTrue(checkout.banknoteValidator.isActivated());
    }

    @Test
    public void payBySwipe() throws IOException {
        touchScreen.payViaSwipe("debit");
        assertFalse(checkout.cardReader.isDisabled());
        assertFalse(checkout.printer.isDisabled());
    }
    
    @Test
    public void testRemoveSelectedBarcodedProduct() {
        touchScreen.removeProduct(product);
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
        touchScreen.selectBagsAdded();
        assertFalse(checkout.mainScanner.isDisabled());
        assertFalse(checkout.handHeldScanner.isDisabled());
        assertFalse(checkout.weightDiscrepancy.expectOwnBagsToBeAdded);
    }
}