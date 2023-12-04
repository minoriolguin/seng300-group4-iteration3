/**
 * This class contains JUnit tests for the UpdateCart class in the Self Checkout System.
 * Project 3 Iteration Group 4
 *
 * Group Members:
 * - Julie Kim 10123567
 * - Aryaman Sandhu 30017164
 * - Arcleah Pascual 30056034
 * - Aoi Ueki 30179305
 * - Ernest Shukla 30156303
 * - Shawn Hanlon 10021510
 * - Jaimie Marchuk 30112841
 * - Sofia Rubio 30113733
 * - Maria Munoz 30175339
 * - Anne Lumumba 30171346
 * - Nathaniel Dafoe 30181948
 * - Arvin Bolbolanardestani 30165484
 * - Anthony Chan 30174703
 * - Marvellous Chukwukelu 30197270
 * - Farida Elogueil 30171114
 * - Ahmed Elshabasi 30188386
 * - Shawn Hanlon 10021510
 * - Steven Huang 30145866
 * - Nada Mohamed 30183972
 * - Jon Mulyk 30093143
 * - Althea Non 30172442
 * - Minori Olguin 30035923
 * - Kelly Osena 30074352
 * - Muhib Qureshi 30076351
 * - Sofia Rubio 30113733
 * - Muzammil Saleem 30180889
 * - Steven Susorov 30197973
 * - Lydia Swiegers 30174059
 * - Elizabeth Szentmiklossy 30165216
 * - Anthony Tolentino 30081427
 * - Johnny Tran 30140472
 * - Kaylee Xiao 30173778
 */
package com.thelocalmarketplace.software.test;

import com.jjjwelectronics.Item;
import com.jjjwelectronics.Mass;
import com.jjjwelectronics.Numeral;
import com.jjjwelectronics.scanner.Barcode;
import com.jjjwelectronics.scanner.BarcodedItem;
import com.thelocalmarketplace.hardware.*;
import com.thelocalmarketplace.hardware.external.ProductDatabases;
import com.thelocalmarketplace.software.Software;
import com.thelocalmarketplace.software.UpdateCart;

import ca.ucalgary.seng300.simulation.NullPointerSimulationException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.junit.Assert.*;


/**
 * JUnit test class for the UpdateCart class in the Self Checkout System.
 *@author Shawn Hanlon
 *@author Jon Mulyk
 *
 **This documentation includes contributions from the following authors:
 *@author Elizabeth Szentmiklossy
 */
public class UpdateCartTest {
    private Software software;
    private SelfCheckoutStationGold hardware;
    private BarcodedProduct barcodedProduct1;
    private BarcodedProduct barcodedProduct2;
    private BarcodedProduct barcodedProduct3;
    private Barcode barcode1;
    private BarcodedItem bar1Item;
    private BarcodedItem bar1ItemCopied;
    private BigDecimal bar1price;
    private PLUCodedProduct PLUProduct1;
    private PLUCodedItem PLUProduct1item;
    private PLUCodedItem PLUProduct1itemCopy;
    private BigDecimal plu1Price;
    private PLUCodedProduct PLUProduct3;
    private UpdateCart updateCart;

    
    /**
     * Setup method to initialize test variables and objects.
     */
    @Before
    public void setUp() {
    	// Creating instances for testing
        hardware = new SelfCheckoutStationGold();
        software = Software.getInstance(hardware);
        this.updateCart = software.updateCart;
        software.turnOn();
        
        //create test barcodedProducts
        Numeral[] testBarcode = new Numeral[4];
        testBarcode[0] = Numeral.nine;
        testBarcode[1] = Numeral.five;
        testBarcode[2] = Numeral.eight;
        testBarcode[3] = Numeral.eight;
        barcode1 = new Barcode(testBarcode);
        barcodedProduct1 = new BarcodedProduct(barcode1, "lamp", 5, 100);
        Mass bar1mass = new Mass((double)100);
        bar1Item = new BarcodedItem(barcode1,bar1mass);
        bar1price = new BigDecimal(barcodedProduct1.getPrice());
        bar1ItemCopied = new BarcodedItem(barcode1,bar1mass);
        Numeral[] testBarcode2 = new Numeral[2];
        testBarcode2[0] = Numeral.zero;
        testBarcode2[1] = Numeral.one;
        Barcode barcode2 = new Barcode(testBarcode2);
        barcodedProduct2 = new BarcodedProduct(barcode2, "batteries", 100, 10);
        Numeral[] testBarcode3 = new Numeral[2];
        testBarcode3[0] = Numeral.nine;
        testBarcode3[1] = Numeral.one;
        Barcode barcode3 = new Barcode(testBarcode3);
        barcodedProduct3 = new BarcodedProduct(barcode3, "sticky notes", 100, 0.01);
        
        //add barcoded products to the barcoded product database
        ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode1, barcodedProduct1);
        ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode2, barcodedProduct2);
        ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode3, barcodedProduct3);

        //create dummy PLUProduts
        PriceLookUpCode PLUCode1 = new PriceLookUpCode("7162");
        PLUProduct1 = new PLUCodedProduct(PLUCode1, "chocolate", 21);
        Mass plu1 = new Mass(1000000);
        Mass plu2 = new Mass(2000000);
        PLUProduct1item = new PLUCodedItem(PLUCode1,plu1);
        PLUProduct1itemCopy = new PLUCodedItem(PLUCode1,plu2);
        long tempplu1price = ((PLUProduct1item.getMass().inGrams().longValue())/1000) * PLUProduct1.getPrice();
        
        // Convert price to type BigDecimal
        plu1Price = BigDecimal.valueOf(tempplu1price);
        

        PriceLookUpCode PLUCode2 = new PriceLookUpCode("28022");
        PLUCodedProduct PLUProduct2 = new PLUCodedProduct(PLUCode2, "steel", 2);

        PriceLookUpCode PLUCode3 = new PriceLookUpCode("5168");
        PLUProduct3 = new PLUCodedProduct(PLUCode3, "coffee", 4);

        //add PLUProducts to the PLU product database
        ProductDatabases.PLU_PRODUCT_DATABASE.put(PLUCode1, PLUProduct1);
        ProductDatabases.PLU_PRODUCT_DATABASE.put(PLUCode2, PLUProduct2);
        ProductDatabases.PLU_PRODUCT_DATABASE.put(PLUCode3, PLUProduct3);
    }
    

    /**
     * JUnit test for the listener registration of the UpdateCart class in the Self Checkout System.
     * This test ensures that the UpdateCart instance is correctly registered as a listener with the hardware components.
     */
    @Test
    public void listenerRegistration(){
    	// Check if the UpdateCart instance is a listener for the scanning area, handheld scanner, and main scanner
        assertTrue(hardware.getScanningArea().listeners().contains(software.updateCart));
        assertTrue(hardware.getHandheldScanner().listeners().contains(software.updateCart));
        assertTrue(hardware.getMainScanner().listeners().contains(software.updateCart));
    }
    /**
     * JUnit test case for adding a PLU product to the cart along with bagging.
     */
    @Test
    public void testAddPLUProductWithPLUProduct(){
    	// Set up the initial conditions
    	software.startSession();
        software.touchScreen.skip = false;
        // Ensure the expected mass is zero before adding
        assertEquals(0,software.getExpectedTotalWeight().compareTo(Mass.ZERO));
        assertFalse(hardware.getScanningArea().isDisabled());
        
        // Add a PLU-coded item to the scanning area
        hardware.getScanningArea().addAnItem(PLUProduct1item);
        assertEquals(0,PLUProduct1item.getMass().compareTo(software.updateCart.currentMassOnScanner));
        
        // Add the PLU product to the cart
        software.updateCart.addPLUProduct(PLUProduct1);
        
        // Ensure the customer gets blocked
        assertTrue(software.isBlocked());
        
        // Verify that the item is added to the products hash map and has the correct weight
        assertTrue(software.getProductsInOrder().containsKey(PLUProduct1));
        assertEquals(0,PLUProduct1item.getMass().compareTo(software.getExpectedTotalWeight()));
        assertEquals(0,PLUProduct1item.getMass().compareTo(software.getProductsInOrder().get(PLUProduct1)));
        
        // Verify that the item is added to the PLU products array list
        assertTrue(software.getPluCodedProductsInOrder().contains(PLUProduct1));
        // Verify that the order total is correct
        assertEquals(plu1Price,software.getOrderTotal());
        // Remove the item from the main scale and put it in the bagging area
        hardware.getScanningArea().removeAnItem(PLUProduct1item);
        hardware.getBaggingArea().addAnItem(PLUProduct1item);
        // Ensure the customer is unblocked
        assertFalse(software.isBlocked());
    }

    /**
     * JUnit test case for adding a PLU product to the cart and skipping bagging.
     * The test verifies that when a PLU product is added, the customer gets unblocked by the attendant,
     * the product is added to the order, and the expected weight is not affected.
     */
    @Test
    public void testAddPLUProductWithPLUProductSkipBagging(){
    	// Set up the initial conditions
    	software.startSession();
        software.touchScreen.skip = true;
        
        // Ensure the expected mass is zero before adding
        assertEquals(0,software.getExpectedTotalWeight().compareTo(Mass.ZERO));
        assertFalse(hardware.getScanningArea().isDisabled());
        
        // Add a PLU-coded item to the scanning area
        hardware.getScanningArea().addAnItem(PLUProduct1item);
        assertEquals(0,PLUProduct1item.getMass().compareTo(software.updateCart.currentMassOnScanner));
        
        // Add the PLU product to the cart
        software.updateCart.addPLUProduct(PLUProduct1);
        
        /// Ensure the customer gets unblocked by the attendant
        assertFalse(software.isBlocked());
        
        // Verify that the item is added to the products hash map, not added to the expected weight
        assertTrue(software.getProductsInOrder().containsKey(PLUProduct1));
        assertEquals(0,Mass.ZERO.compareTo(software.getExpectedTotalWeight()));
        assertEquals(0,PLUProduct1item.getMass().compareTo(software.getProductsInOrder().get(PLUProduct1)));
        
        // Verify that the item is added to the PLU products array list
        assertTrue(software.getPluCodedProductsInOrder().contains(PLUProduct1));
        // Verify that the order total is correct
        assertEquals(plu1Price,software.getOrderTotal());
    }
    
    /**
     * JUnit test case for adding a scanned barcode product to the cart.
     * The test checks if the scanned product is added to the order, and the expected weight is correct.
     * Additionally, the test verifies that the customer gets blocked initially, and after placing the item
     * in the bagging area, the customer gets unblocked.
     */
    @Test
    public void testAddScannedProduct() {
    	// Set up the initial conditions
        software.startSession();
        software.touchScreen.skip = false;
        
        // Ensure the expected mass is zero before adding
        assertEquals(0,software.getExpectedTotalWeight().compareTo(Mass.ZERO));
        // Add a scanned product to the cart
        software.updateCart.addScannedProduct(barcode1);
        // Ensure the customer gets blocked initially
        assertTrue(software.isBlocked());
        
        // Verify that the item is added to the products hash map, and the weight is correct
        assertTrue(software.getProductsInOrder().containsKey(barcodedProduct1));
        assertEquals(0,bar1Item.getMass().compareTo(software.getExpectedTotalWeight()));
        assertEquals(0,bar1Item.getMass().compareTo(software.getProductsInOrder().get(barcodedProduct1)));
        
        // Verify that the item is added to the barcoded products array list
        assertTrue(software.getBarcodedProductsInOrder().contains(barcodedProduct1));
        
        // Verify that the order total is correct
        assertEquals(bar1price,software.getOrderTotal());
        // Put the item in the Bagging Area
        hardware.getBaggingArea().addAnItem(bar1Item);
        // Ensure the customer gets unblocked
        assertFalse(software.isBlocked());
    }
    
    /**
     * JUnit test case for adding a scanned barcode product to the cart with skip bagging.
     * The test checks if the scanned product is added to the order, and the expected weight is correct.
     * Additionally, the test verifies that the customer gets unblocked by the attendant.
     */
    @Test
    public void testAddScannedProductSkipBagging() {
    	// Set up the initial conditions
        software.startSession();
        software.touchScreen.skip = true;
        
        // Ensure the expected mass is zero before adding
        assertEquals(0,software.getExpectedTotalWeight().compareTo(Mass.ZERO));
        
        // Add a scanned product to the cart
        software.updateCart.addScannedProduct(barcode1);
        
        // Ensure the customer gets unblocked by the attendant
        assertFalse(software.isBlocked());
        
        // Verify that the item is added to the products hash map, and the weight is correct
        assertTrue(software.getProductsInOrder().containsKey(barcodedProduct1));
        assertEquals(0,Mass.ZERO.compareTo(software.getExpectedTotalWeight()));
        assertEquals(0,bar1Item.getMass().compareTo(software.getProductsInOrder().get(barcodedProduct1)));
        
        // Verify that the item is added to the barcoded products array list
        assertTrue(software.getBarcodedProductsInOrder().contains(barcodedProduct1));
        
        // Verify that the order total is correct
        assertEquals(bar1price,software.getOrderTotal());
    }
    
    /**
     * JUnit test case for adding a barcoded product to the cart using the addProduct method.
     * The test checks if the product is added to the order and the barcoded products array list.
     */
    @Test
    public void testAddProductSendsToAddScannedProduct() {
    	// Add a barcoded product to the cart using the addProduct method
    	software.updateCart.addProduct(barcodedProduct1);
    	// Verify that the item is added to the products hash map
        assertTrue(software.getProductsInOrder().containsKey(barcodedProduct1));
        // Verify that the item is added to the barcoded products array list
        assertTrue(software.getBarcodedProductsInOrder().contains(barcodedProduct1));
    }
    
    /**
     * JUnit test case for adding a PLU-coded product to the cart using the addProduct method.
     * The test checks if the PLU-coded product is added to the order and the PLU-coded products array list.
     */
    @Test
    public void testAddProductSendsToAddPLUProduct(){
    	// Add a PLU-coded product to the cart using the addProduct method
        software.updateCart.addProduct(PLUProduct1);
        // Verify that the PLU-coded product is added to the PLU-coded products array list
    	assertTrue(software.getPluCodedProductsInOrder().contains(PLUProduct1));
    	// Verify that the PLU-coded product is added to the products hash map
    	assertTrue(software.getProductsInOrder().containsKey(PLUProduct1));
    }

    /**
     * JUnit test case for removing a PLU-coded product from the cart.
     * The test checks the removal process, including updating the order total, expected weight, and unblocking the customer.
     */
    @Test
    public void removePLUCodedProduct() {
    	// Set up the initial conditions
        software.startSession();
        software.touchScreen.skip = false;
        hardware.getScanningArea().addAnItem(PLUProduct1item);
        software.updateCart.addPLUProduct(PLUProduct1);
        
        // Ensure the PLU-coded product is added to the products hash map and the PLU-coded products array list
        assertTrue(software.getProductsInOrder().containsKey(PLUProduct1));
        assertEquals(0, PLUProduct1item.getMass().compareTo(software.getExpectedTotalWeight()));
        assertEquals(0, PLUProduct1item.getMass().compareTo(software.getProductsInOrder().get(PLUProduct1)));
        //Verify the item has been added to PLU products array list
        assertTrue(software.getPluCodedProductsInOrder().contains(PLUProduct1));
        // Verify the order total is correct
        assertEquals(plu1Price, software.getOrderTotal());
        
        // Remove the item from the main scale and put it in the Bagging Area
        hardware.getScanningArea().removeAnItem(PLUProduct1item);
        hardware.getBaggingArea().addAnItem(PLUProduct1item);
        
        // Verify the customer is unblocked
        assertFalse(software.isBlocked());

        // Remove the PLU-coded product from the cart
        software.updateCart.removeItem(PLUProduct1);
        // Verify the customer is blocked, the product is removed from the PLU-coded products array list,
        // the order total is updated, and the product is removed from the weight map
        assertTrue(software.isBlocked());
        assertFalse(software.getPluCodedProductsInOrder().contains(PLUProduct1));
        assertEquals(BigDecimal.ZERO, software.getOrderTotal());
        assertFalse(software.getProductsInOrder().containsKey(PLUProduct1));
        assertEquals(0, Mass.ZERO.compareTo(software.getExpectedTotalWeight()));
        
        // Remove the product from the bagging area
        hardware.getBaggingArea().removeAnItem(PLUProduct1item);
        
        // Verify the customer is unblocked
        assertFalse(software.isBlocked());
    }
    
    /**
     * JUnit test case for adding the same scanned product twice to the cart.
     * The test checks if the weights are correctly aggregated for identical scanned products.
     */
    @Test
    public void testAddSameScannedProuductTwice() {
    	// Set up the initial conditions
    	software.startSession();
        software.touchScreen.skip = false;
        
        // Add the same scanned product twice
        software.updateCart.addScannedProduct(barcode1);
        hardware.getBaggingArea().addAnItem(bar1Item);
        software.updateCart.addScannedProduct(barcode1);
        hardware.getBaggingArea().addAnItem(bar1ItemCopied);
        
        // Verify that the weights are correctly aggregated for the identical scanned products
        assertEquals(bar1Item.getMass().sum(bar1ItemCopied.getMass()), software.getProductsInOrder().get(barcodedProduct1));
        assertEquals(bar1Item.getMass().sum(bar1ItemCopied.getMass()), software.getBaggedProducts().get(barcodedProduct1));
    }
    
    /**
     * JUnit test case for adding the same PLU-coded product twice to the cart.
     * The test checks if the weights are correctly aggregated for identical PLU-coded products.
     */
    @Test
    public void testAddSamePLUproductTwice() {
    	// Set up the initial conditions
        software.startSession();;
        software.touchScreen.skip = false;
        
        // Add the same PLU-coded product twice
        hardware.getScanningArea().addAnItem(PLUProduct1item);
        software.updateCart.addPLUProduct(PLUProduct1);
        hardware.getScanningArea().removeAnItem(PLUProduct1item);
        hardware.getBaggingArea().addAnItem(PLUProduct1item);

        hardware.getScanningArea().addAnItem(PLUProduct1itemCopy);
        software.updateCart.addPLUProduct(PLUProduct1);
        hardware.getScanningArea().removeAnItem(PLUProduct1itemCopy);
        hardware.getBaggingArea().addAnItem(PLUProduct1itemCopy);

        // Verify that the weights are correctly aggregated for the identical PLU-coded products
        assertEquals(PLUProduct1item.getMass().sum(PLUProduct1itemCopy.getMass()), software.getBaggedProducts().get(PLUProduct1));
        assertEquals(PLUProduct1item.getMass().sum(PLUProduct1itemCopy.getMass()), software.getProductsInOrder().get(PLUProduct1));
    }
    
    /**
     * JUnit test case for text search functionality with barcoded products.
     * The test checks if the text search returns the expected match for a given barcoded product name.
     */
    @Test
    public void testTextSearchForBarcodedProduct()
    {
    	ArrayList<Product> matches = updateCart.textSearch("batteries");
        assertEquals(1, matches.size());
    	assertEquals(matches.get(0), barcodedProduct2);
    }
    
    /**
     * JUnit test case for text search functionality with PLU-coded products.
     * The test checks if the text search returns the expected match for a given PLU-coded product name.
     */
    @Test
    public void testTextSearchForPLUProduct()
    {
    	ArrayList<Product> matches = updateCart.textSearch("coffee");
        assertEquals(1, matches.size());
    	assertEquals(matches.get(0), PLUProduct3);
    }
    
    /**
     * JUnit test case for text search functionality with both PLU-coded and barcoded products.
     * The test checks if the text search returns the expected matches for products with a common keyword.
     */
    @Test
    public void testTextSearchForPLUAndBarcodedProduct()
    {
    	// Create products that both have "apple" in the name
        Numeral[] testBarcode = new Numeral[4];
        testBarcode[0] = Numeral.six;
        testBarcode[1] = Numeral.two;
        testBarcode[2] = Numeral.two;
        testBarcode[3] = Numeral.two;
        barcode1 = new Barcode(testBarcode);
        BarcodedProduct bP1 = new BarcodedProduct(barcode1, "bundle of apples", 30, 100);

        PriceLookUpCode PLUCode = new PriceLookUpCode("9028");
        PLUCodedProduct PLUP1 = new PLUCodedProduct(PLUCode, "singular wrapped apple", 26);
        
        // Add barcodedProduct and PLUCodedProduct to the database
        ProductDatabases.PLU_PRODUCT_DATABASE.put(PLUCode, PLUP1);
        ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode1, bP1);

        // Perform text search and verify the results
    	ArrayList<Product> matches = updateCart.textSearch("apple");
        assertEquals(2, matches.size());
        assertNotSame(matches.get(0), matches.get(1));
    	assertTrue((matches.get(0) == PLUP1) || matches.get(0) == bP1);
    	assertTrue((matches.get(1) == PLUP1) || matches.get(1) == bP1);
    }
    
    /**
     * JUnit test case for text search functionality with no matches.
     * The test checks if the text search returns an empty list when there are no matches for the given search term.
     */
    @Test
    public void testTexSearchWithNoMatches(){
    	ArrayList<Product> matches = updateCart.textSearch("corn");
        assertEquals(0, matches.size());
    }
    
    /**
     * JUnit test case for text search functionality with a null search string.
     * The test checks if the text search throws a NullPointerSimulationException when a null search string is provided.
     * @throws NullPointerSimulationException - Expected exception for a null search string.
     */
    @Test(expected = NullPointerSimulationException.class)
    public void testTextSearchWithNullStr() {
    	updateCart.textSearch(null);
    }
}
