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
import com.thelocalmarketplace.hardware.Product;
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
import java.util.ArrayList;

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
    private UpdateCart updateCart;
//    private WeightDiscrepancy weightDiscrepancy;
//    private Attendant attendant;


    @Before
    public void setUp() {
    	//initial setup variables
        PowerGrid.engageUninterruptiblePowerSource();
        SelfCheckoutStationGold.resetConfigurationToDefaults();
        station = new SelfCheckoutStationGold();
        software = Software.getInstance(station);
        software.turnOn();
        updateCart = new UpdateCart(software);
        this.updateCart = software.updateCart;
//        attendant = new Attendant(software);
//        weightDiscrepancy = software.weightDiscrepancy;
        
        //create dummy barcodedProducts
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
        
        //add barcoded products to the barcoded product database
        ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode, barcodedProduct1);
        ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode2, barcodedProduct2);
        ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode3, barcodedProduct3);

        //create dummy PLUProduts
        PriceLookUpCode PLUCode1 = new PriceLookUpCode("7162");
        PLUProduct1 = new PLUCodedProduct(PLUCode1, "chocolate", 21);

        PriceLookUpCode PLUCode2 = new PriceLookUpCode("28022");
        PLUProduct2 = new PLUCodedProduct(PLUCode2, "steel", 2);

        PriceLookUpCode PLUCode3 = new PriceLookUpCode("5168");
        PLUProduct3 = new PLUCodedProduct(PLUCode3, "coffee", 4);
        
        //add PLUProducts to the PLU product database
        ProductDatabases.PLU_PRODUCT_DATABASE.put(PLUCode1, PLUProduct1);
        ProductDatabases.PLU_PRODUCT_DATABASE.put(PLUCode2, PLUProduct2);
        ProductDatabases.PLU_PRODUCT_DATABASE.put(PLUCode3, PLUProduct3);
    }
    
    /**
     * Test adding a scanned barcode product
     * 
     * Check if it gets added to the order, and that the expected weight is correct
     */
    @Test
    public void testAddScannedProduct() 
    {
        updateCart.addScannedItem(barcode);

        //products in order and barcodedProductsInOrder
        assertTrue(software.getProductsInOrder().containsKey(barcodedProduct1));

        // Verify that the expected total weight is updated
        Mass expectedMass = new Mass(barcodedProduct1.getExpectedWeight());
        assertEquals(expectedMass, software.getExpectedTotalWeight());
        
        //Check the the software got unblocked (no weight discrepancy)
        assertFalse(software.isBlocked());
    }
    
    @Test
    public void testAddGeneralProductWithBarcodedProduct()
    {
    	updateCart.addProduct(barcodedProduct1);
    	
    	assertTrue(software.getProductsInOrder().containsKey(barcodedProduct1));
    }

    @Test
    public void testAddGeneralProductWithPLUProduct()
    {
    	//TODO: change to PLU barcodedProduct1
//    	Product prod = barcodedProduct1;
//    	updateCart.addProduct(prod);
//    	
//    	assertTrue(software.getProductsInOrder().containsKey(prod));
    }
    
    @Test
    public void testAddGeneralProducts()
    {
    	//TODO: add PLUProduct as well
    	Product bProd = barcodedProduct1;
    	//Product PLUProd = PLUProduct
    	updateCart.addProduct(bProd);
    	
    	assertTrue(software.getProductsInOrder().containsKey(bProd));
    }
    
    @Test(expected = NullPointerSimulationException.class)
    public void testAddGeneralNullProduct()
    {
    	updateCart.addProduct(null);
    }
    

    
    /**
     * Adding a PLU product to the order
     */
    @Test
    public void testAddPLUProduct()
    {
    	//add item to cart
    	updateCart.addPLUProduct(PLUProduct1);

    	//Check that the item got added to the order
        assertTrue(software.getProductsInOrder().containsKey(PLUProduct1));
        
        //Check the the software got unblocked (no weight discrepancy)
        assertFalse(software.isBlocked());
    }
    
    //expect this to fail because it can't get the product from the database
    /**
     * Add a PLU item that is not in the database
     */
    // @TODO: change the exception to be more specific to the case
    @Test(expected = NullPointerSimulationException.class)
    public void testAddPLUNotInDatabase()
    {
    	//create PLU product, but don't add it to the database
        PriceLookUpCode PLUCode = new PriceLookUpCode("6704");
    	PLUCodedProduct product = new PLUCodedProduct(PLUCode, "keyboard", 519);
    	
    	//add the PLU product to the cart
    	updateCart.addPLUProduct(product);
    }
    
    /**
     * Test the case where a null barcode is called
     */
    @Test(expected = NullPointerSimulationException.class)
    public void testAddNullBarcode()
    {
    	Barcode barcode = null;
    	updateCart.addScannedItem(barcode);
    }


    /**
     * Test the case where a barcode is mapped to a null plu product
     */
    @Test(expected = NullPointerSimulationException.class)
    public void testAddNullScannedProduct()
    {

        //create dummy barcodedProducts
        Numeral[] numBarcode = new Numeral[4];
        numBarcode[0] = Numeral.two;
        numBarcode[1] = Numeral.two;
        numBarcode[2] = Numeral.eight;
        numBarcode[3] = Numeral.six;
        barcode = new Barcode(numBarcode);
        BarcodedProduct barcodedProduct = null;
        
        //map the valid barcode to a null barcoded product
        ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode, barcodedProduct);
        

    	updateCart.addScannedItem(barcode);
    }
    
    
    /**
     * Test the case where a null PLU product is added to the cart
     */
    @Test(expected = NullPointerSimulationException.class)
    public void testAddNullPLUProduct()
    {
    	PLUCodedProduct product = null;
    	updateCart.addPLUProduct(product);
    }
    
    /**
     * Test the case where a product with a null PLU code is added to the cart
     */
    @Test(expected = NullPointerSimulationException.class)
    public void testAddPLUWithNullLookUpCode()
    {
    	PLUCodedProduct product = new PLUCodedProduct(null, "123", 123);
    	updateCart.addPLUProduct(product);
    }
    
    /**
     * Test the case where a PLU item without a description is added to the cart
     * 
     * description might be optional??
     */
//    @Test
//    public void testAddPLUWithEmptyDescription()
//    {
//        PriceLookUpCode PLUCode = new PriceLookUpCode("4139");
//    	PLUCodedProduct product = new PLUCodedProduct(PLUCode, "", 123);
//    	updateCart.addPLUProduct(product);
//    }
    
    /**
     * Test removing a valid PLU product from the order
     * 
     * relies on addPLUProduct to work correctly
     */
    @Test
    public void testRemovePLUProduct()
    {
    	updateCart.addPLUProduct(PLUProduct1);
//        assertTrue(software.getProductsInOrder().containsKey(PLUProduct1));

    	updateCart.removeItem(PLUProduct1);
        assertFalse(software.getProductsInOrder().containsKey(PLUProduct1));
    	
    }
    
    /**
     * Test removing a valid barcoded product from the order
     *
     * relies on addScannedItem to work correctly
     */
    @Test
    public void testRemoveScannedProduct() 
    {
        // Add an item first
        updateCart.addScannedItem(barcode);
        // Then remove it
        updateCart.removeItem(barcodedProduct1);

        // Verify that the product was removed from the cart
        assertFalse(software.getBarcodedProductsInOrder().contains(barcodedProduct1));
    }
    
    
    /**
     * Test that the order was updated correctly for a scanned product
     */
    @Test
    public void testOrderTotalUpdateWithScanned() 
    {
        updateCart.addScannedItem(barcode);
        BigDecimal expectedTotal = new BigDecimal(barcodedProduct1.getPrice());

        // Verify that the order total has been changed correctly
        assertEquals(expectedTotal, software.getOrderTotal());
    }
    
    /*
     * Test that the order was updated correctly for a PLU product
     */
    @Test
    public void testOrderTotalUpdateWithPLU() 
    {
    	updateCart.addPLUProduct(PLUProduct1);
        BigDecimal expectedTotal = new BigDecimal(PLUProduct1.getPrice());

        // Verify that the order total has been changed correctly
        assertEquals(expectedTotal, software.getOrderTotal());
    }
    
    /**
     * Test removing a scanned product that is not in the cart
     */
    @Test
    public void testRemoveScannedProductNotInCart() 
    {
        updateCart.removeItem(barcodedProduct1);
        // Verify the cart remains unchanged
        assertTrue(software.getBarcodedProductsInOrder().isEmpty());
    }
    
    /**
     * Test adding the same scanned product 3 times
     */
    @Test
    public void testAddSameScannedProuductMultipleTimes() 
    {
        int quantity = 3;
        for (int i = 0; i < quantity; i++) {
            updateCart.addScannedItem(barcode);
        }
        BigDecimal expectedTotal = new BigDecimal(15);

        assertEquals(expectedTotal, software.getOrderTotal());
        assertEquals(new Mass(barcodedProduct1.getExpectedWeight() * 3), software.getExpectedTotalWeight());

        assertTrue(software.getBarcodedProductsInOrder().contains(barcodedProduct1));
    }
    
    /**
     * Test adding 
     */
    @Test
    public void testAddMultipleDifferentScannedProducts() 
    {
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
    public void testAddingPLUAndScannedProducts()
    {
    	updateCart.addPLUProduct(PLUProduct1);
    	updateCart.addScannedItem(barcode);

//    	assertTrue(software.getPluCodedProductsInOrder().contains(PLUProduct1));
    	
    	//Check that the item got added to the order
        assertTrue(software.getProductsInOrder().containsKey(PLUProduct1));
        assertTrue(software.getProductsInOrder().containsKey(barcodedProduct1));
    }
    
    @Test
    public void testRemovingPLUAndScannedProducts()
    {
    	updateCart.addPLUProduct(PLUProduct1);
    	updateCart.addScannedItem(barcode);
    	updateCart.removeItem(PLUProduct1);
    	updateCart.removeItem(barcodedProduct1);

//    	assertTrue(software.getPluCodedProductsInOrder().contains(PLUProduct1));
    	
    	//Check that the item got added to the order
        assertFalse(software.getProductsInOrder().containsKey(PLUProduct1));
        assertFalse(software.getProductsInOrder().containsKey(barcodedProduct1));
    
    }
    
    /**
     * Test custom skipping bagging for scanned product
     */
    @Test
    public void testSkipBaggingForScannedProduct() 
    {
    	software.touchScreen.selectAddOwnBags();
        updateCart.addScannedItem(barcode);
        assertFalse(software.isBlocked());

    }
    
    /**
     * Test customer skipping bagging for PLU product
     */
    @Test 
    public void testSkipBaggingForPLUProduct()
    {
    	software.touchScreen.selectAddOwnBags();
        updateCart.addPLUProduct(PLUProduct1);        
        assertFalse(software.isBlocked());
    }
    
    /**
     * Test customer adding a light item to their order
     */
    @Test
    public void testLightItems() 
    {
        updateCart.addScannedItem(barcode3);
        assertFalse(software.isBlocked());
    }
    
    /**
     * Test barcode listener when a barcoded has been scanned
     */
    @Test
    public void testBarcodeScanEvent() 
    {
        updateCart.aBarcodeHasBeenScanned(station.getHandheldScanner(), barcode);
        assertTrue(software.getBarcodedProductsInOrder().contains(barcodedProduct1));
    }
    
    @Test
    public void testTextSearchWithBarcodedProduct()
    {
    	updateCart.addScannedItem(barcode2);
    	ArrayList<Product> matches = updateCart.textSearch("test2");
    	assertTrue(matches.size() == 1);
    	assertEquals(matches.get(0), barcodedProduct2);
    }

    @Test
    public void testTextSearchWithPLUProduct()
    {
    	//TODO: change to PLU
//    	updateCart.addScannedItem(barcode2);
//    	ArrayList<Product> matches = updateCart.textSearch("test2");
//    	assertTrue(matches.size() == 1);
//    	assertEquals(matches.get(0), barcodedProduct2);
    }

    @Test
    public void testTextSearchWithPLUAndBarcodedProduct()
    {
    	//TODO: add PLU
    	updateCart.addScannedItem(barcode2);
//    	updateCart.addScannedItem(barcode2);
    	ArrayList<Product> matches = updateCart.textSearch("test2");
    	assertTrue(matches.size() == 2);
    	assertEquals(matches.get(0), barcodedProduct2);
//    	assertEquals(matches.get(0), barcodedProduct2);
    }
    
    @Test
    public void testTexSearchWithNoMatches()
    {
    	ArrayList<Product> matches = updateCart.textSearch("");
    	assertTrue(matches.size() == 0);
    }
    
    @Test(expected = NullPointerSimulationException.class)
    public void testTextSearchWithNullStr()
    {
    	updateCart.textSearch(null);
    }
}
