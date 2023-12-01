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

import com.jjjwelectronics.Mass;
import com.jjjwelectronics.Numeral;
import com.jjjwelectronics.scanner.Barcode;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.PLUCodedProduct;
import com.thelocalmarketplace.hardware.PriceLookUpCode;
import com.thelocalmarketplace.hardware.SelfCheckoutStationGold;
import com.thelocalmarketplace.hardware.external.ProductDatabases;
import com.thelocalmarketplace.software.Attendant;
import com.thelocalmarketplace.software.Software;
import com.thelocalmarketplace.software.TouchScreen;
import com.thelocalmarketplace.software.UpdateCart;
import com.thelocalmarketplace.software.WeightDiscrepancy;

import ca.ucalgary.seng300.simulation.NullPointerSimulationException;

import org.junit.Before;
import org.junit.Test;
import powerutility.PowerGrid;

import java.math.BigDecimal;

import static org.junit.Assert.*;

public class UpdateCartTest {
    private Software software;
    private SelfCheckoutStationGold station;
    private BarcodedProduct barcodedProduct1;
    private BarcodedProduct barcodedProduct2;
    private BarcodedProduct barcodedProduct3;
    private Barcode barcode;
    private Barcode barcode2;
    private Barcode barcode3;
    private PLUCodedProduct PLUProduct1;
    private PLUCodedProduct PLUProduct2;
    private PLUCodedProduct PLUProduct3;
    private TouchScreen touchScreen;
    private UpdateCart updateCart;
    private WeightDiscrepancy weightDiscrepancy;
    private Attendant attendant;


    @Before
    public void setUp() {
        PowerGrid.engageUninterruptiblePowerSource();
        SelfCheckoutStationGold.resetConfigurationToDefaults();
        station = new SelfCheckoutStationGold();
        software = Software.getInstance(station);
        software.turnOn();
        attendant = new Attendant(software);
        touchScreen = new TouchScreen(software);
        updateCart = new UpdateCart(software);
        weightDiscrepancy = software.weightDiscrepancy;

        Numeral[] testBarcode = new Numeral[4];
        testBarcode[0] = Numeral.nine;
        testBarcode[1] = Numeral.five;
        testBarcode[2] = Numeral.eight;
        testBarcode[3] = Numeral.eight;
        barcode = new Barcode(testBarcode);
        barcodedProduct1 = new BarcodedProduct(barcode, "test", 5, 100);

        Numeral[] testBarcode2 = new Numeral[2];
        testBarcode2[0] = Numeral.zero;
        testBarcode2[1] = Numeral.one;
        barcode2 = new Barcode(testBarcode2);
        barcodedProduct2 = new BarcodedProduct(barcode2, "test2", 100, 10);
        Numeral[] testBarcode3 = new Numeral[2];
        testBarcode3[0] = Numeral.nine;
        testBarcode3[1] = Numeral.one;
        barcode3 = new Barcode(testBarcode3);
        barcodedProduct3 = new BarcodedProduct(barcode3, "lightTest", 100, 0.01);
        
//	public PLUCodedProduct(PriceLookUpCode pluCode, String description, long price) {
        PriceLookUpCode PLUCode1 = new PriceLookUpCode("7162");
        PLUProduct1 = new PLUCodedProduct(PLUCode1, "chocolate", 21);

        PriceLookUpCode PLUCode2 = new PriceLookUpCode("28022");
        PLUProduct2 = new PLUCodedProduct(PLUCode2, "steel", 2);

        PriceLookUpCode PLUCode3 = new PriceLookUpCode("5168");
        PLUProduct3 = new PLUCodedProduct(PLUCode3, "coffee", 4);
        
        ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode, barcodedProduct1);
        ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode2, barcodedProduct2);
        ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode3, barcodedProduct3);
        
        ProductDatabases.PLU_PRODUCT_DATABASE.put(PLUCode1, PLUProduct1);
        ProductDatabases.PLU_PRODUCT_DATABASE.put(PLUCode2, PLUProduct2);
        ProductDatabases.PLU_PRODUCT_DATABASE.put(PLUCode3, PLUProduct3);
    }

    @Test
    public void testAddScannedItem() {
        updateCart.addScannedItem(barcode);

        // Verify that the product was added to the cart
        assertTrue(software.getBarcodedProductsInOrder().contains(barcodedProduct1));

        // Verify that the expected total weight is updated
        assertEquals(new Mass(barcodedProduct1.getExpectedWeight()),
                software.getExpectedTotalWeight());

        // Verify that the scanned item was added to the cart
        assertTrue(software.getBarcodedProductsInOrder().contains(barcodedProduct1));
        
        //products in order and barcodedProductsInOrder
        assertTrue(software.getProductsInOrder().containsKey(barcodedProduct1));

    }

    /* Potential test cases:
     		PLUCodedLookUpPrice - null
     		Empty string as description / null description
     		///// negative price (maybe 0 as well) / null price -> handled in hardware
    */
    
    @Test
    public void testAddPLUProduct()
    {
    	updateCart.addPLUProduct(PLUProduct1);

//    	assertTrue(software.getPluCodedProductsInOrder().contains(PLUProduct1));
        assertTrue(software.getProductsInOrder().containsKey(PLUProduct1));
        
        
//        // Verify that the expected total weight is updated
//        assertEquals(new Mass(product.getExpectedWeight()),
//                software.getExpectedTotalWeight());

//        // Verify that the scanned item was added to the cart
//        assertTrue(software.getPluCodedProductsInOrder().contains(PLUProduct1));

    }
    
    //expect this to fail because it can't get the product from the database
    @Test
    public void testAddPLUNotInDatabase()
    {
        PriceLookUpCode PLUCode = new PriceLookUpCode("6704");
    	PLUCodedProduct product = new PLUCodedProduct(PLUCode, "keyboard", 519);
    	updateCart.addPLUProduct(product);
    }
    
    @Test(expected = NullPointerSimulationException.class)
    public void testAddNullPLUProduct()
    {
    	PLUCodedProduct product = null;
    	updateCart.addPLUProduct(product);
    }

    @Test(expected = NullPointerSimulationException.class)
    public void testAddPLUWithNullLookUpCode()
    {
    	PLUCodedProduct product = new PLUCodedProduct(null, "123", 123);
    	updateCart.addPLUProduct(product);
    }
    
    //don't know what to expect for error
    @Test
    public void testAddPLUWithEmptyDescription()
    {
        PriceLookUpCode PLUCode = new PriceLookUpCode("4139");
    	PLUCodedProduct product = new PLUCodedProduct(PLUCode, "", 123);
    	updateCart.addPLUProduct(product);
    }
    
    //relies on addPLUProduct to work
    @Test
    public void testRemovePLUProduct()
    {
    	updateCart.addPLUProduct(PLUProduct1);
//        assertTrue(software.getProductsInOrder().containsKey(PLUProduct1));

    	updateCart.removeItem(PLUProduct1);
        assertFalse(software.getProductsInOrder().containsKey(PLUProduct1));
    	
    }

    @Test
    public void testRemoveItem() {
        // Add an item first
        updateCart.addScannedItem(barcode);
        // Then remove it
        updateCart.removeItem(barcodedProduct1);

        // Verify that the product was removed from the cart
        assertFalse(software.getBarcodedProductsInOrder().contains(barcodedProduct1));
    }
    
    

    @Test
    public void testOrderTotalUpdate() {
        updateCart.addScannedItem(barcode);
        BigDecimal expectedTotal = new BigDecimal(5);
        // Verify that the order total has been changed correctly
        assertEquals(expectedTotal, software.getOrderTotal());
    }

    @Test
    public void testOrderTotalUpdateWithPLU() {
    	updateCart.addPLUProduct(PLUProduct1);
        BigDecimal expectedTotal = new BigDecimal(21);
        // Verify that the order total has been changed correctly
        assertEquals(expectedTotal, software.getOrderTotal());
    }


    @Test
    public void testRemoveNonExistentItem() {
        updateCart.removeItem(barcodedProduct1);
        // Verify the cart remains unchanged
        assertTrue(software.getBarcodedProductsInOrder().isEmpty());
    }

    @Test
    public void testAddSameItemMultipleTimes() {
        int quantity = 3;
        for (int i = 0; i < quantity; i++) {
            updateCart.addScannedItem(barcode);
        }
        BigDecimal expectedTotal = new BigDecimal(15);

        assertEquals(expectedTotal, software.getOrderTotal());
        assertEquals(new Mass(barcodedProduct1.getExpectedWeight() * 3),
                software.getExpectedTotalWeight());
        assertTrue(software.getBarcodedProductsInOrder().contains(barcodedProduct1));


    }

    @Test
    public void testAddMultipleDifferentItems() {
        // Add multiple different items
        updateCart.addScannedItem(barcode); // First item
        updateCart.addScannedItem(barcode2); // Second item
        BigDecimal expectedTotal = new BigDecimal(105);
        assertEquals(expectedTotal, software.getOrderTotal());
        assertEquals(new Mass(barcodedProduct1.getExpectedWeight() + barcodedProduct2.getExpectedWeight()),
                software.getExpectedTotalWeight());
        assertTrue(software.getBarcodedProductsInOrder().contains(barcodedProduct1));
        assertTrue(software.getBarcodedProductsInOrder().contains(barcodedProduct2));
    }

    @Test
    public void testSkipBaggingItem() {
        MockTouchScreen mockTouchScreen = new MockTouchScreen(software);
        software.setTestTouchScreen(mockTouchScreen);
        updateCart.addScannedItem(barcode);
        assertFalse(software.isBlocked());

    }
    
    @Test 
    public void testSkipBaggingWithPLU()
    {
        MockTouchScreen mockTouchScreen = new MockTouchScreen(software);
        software.setTestTouchScreen(mockTouchScreen);
        updateCart.addPLUProduct(PLUProduct1);        
        assertFalse(software.isBlocked());
    }

    @Test
    public void testLightItems() {
        updateCart.addScannedItem(barcode3);
        assertFalse(software.isBlocked());
    }

    @Test
    public void testBarcodeScanEvent() {
        updateCart.aBarcodeHasBeenScanned(station.getHandheldScanner(), barcode);
        assertTrue(software.getBarcodedProductsInOrder().contains(barcodedProduct1));
    }


    private static class MockTouchScreen extends TouchScreen {

        public MockTouchScreen(Software checkout) {
            super(checkout);
        }

        @Override
        public boolean skipBaggingItem() {
            return true;
        }
    }
}





