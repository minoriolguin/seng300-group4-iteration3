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

    @Before
    public void setUp() {
    	//initial setup variables
        hardware = new SelfCheckoutStationGold();
        software = Software.getInstance(hardware);
        this.updateCart = software.updateCart;
        software.turnOn();
        
        //create dummy barcodedProducts
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

    @Test
    public void listenerRegistration(){
        assertTrue(hardware.getScanningArea().listeners().contains(software.updateCart));
        assertTrue(hardware.getHandheldScanner().listeners().contains(software.updateCart));
        assertTrue(hardware.getMainScanner().listeners().contains(software.updateCart));
    }
    /**
     * Add Plu product and bag
     */
    @Test
    public void testAddPLUProductWithPLUProduct()
    {
        software.startSession();
        software.touchScreen.skip = false;
        //Make sure expected Mass is zero before adding
        assertEquals(0,software.getExpectedTotalWeight().compareTo(Mass.ZERO));
        assertFalse(hardware.getScanningArea().isDisabled());
        hardware.getScanningArea().addAnItem(PLUProduct1item);
        assertEquals(0,PLUProduct1item.getMass().compareTo(software.updateCart.currentMassOnScanner));
        // add the item
        software.updateCart.addPLUProduct(PLUProduct1);
        //make sure customer gets blocked
        assertTrue(software.isBlocked());
        //item get added products hash map that tracks weight
        assertTrue(software.getProductsInOrder().containsKey(PLUProduct1));
        assertEquals(0,PLUProduct1item.getMass().compareTo(software.getExpectedTotalWeight()));
        assertEquals(0,PLUProduct1item.getMass().compareTo(software.getProductsInOrder().get(PLUProduct1)));
        //item added to PLU products array list
        assertTrue(software.getPluCodedProductsInOrder().contains(PLUProduct1));
        //orderTotalCorrect
        assertEquals(plu1Price,software.getOrderTotal());
        //remove item from main scale and put in Bagging Area
        hardware.getScanningArea().removeAnItem(PLUProduct1item);
        hardware.getBaggingArea().addAnItem(PLUProduct1item);
        //customer unblocked?
        assertFalse(software.isBlocked());
    }

    /**
     * Add PLU product and Skip Bagging
     */
    @Test
    public void testAddPLUProductWithPLUProductSkipBagging()
    {
        software.startSession();
        software.touchScreen.skip = true;
        //Make sure expected Mass is zero before adding
        assertEquals(0,software.getExpectedTotalWeight().compareTo(Mass.ZERO));
        assertFalse(hardware.getScanningArea().isDisabled());
        hardware.getScanningArea().addAnItem(PLUProduct1item);
        assertEquals(0,PLUProduct1item.getMass().compareTo(software.updateCart.currentMassOnScanner));
        // add the item
        software.updateCart.addPLUProduct(PLUProduct1);
        //make sure customer gets unblocked by attendant
        assertFalse(software.isBlocked());
        //item get added products hash map that tracks weight and not added to expected
        assertTrue(software.getProductsInOrder().containsKey(PLUProduct1));
        assertEquals(0,Mass.ZERO.compareTo(software.getExpectedTotalWeight()));
        assertEquals(0,PLUProduct1item.getMass().compareTo(software.getProductsInOrder().get(PLUProduct1)));
        //item added to PLU products array list
        assertTrue(software.getPluCodedProductsInOrder().contains(PLUProduct1));
        //orderTotalCorrect
        assertEquals(plu1Price,software.getOrderTotal());
    }
    
    /**
     * Test adding a scanned barcode product
     * 
     * Check if it gets added to the order, and that the expected weight is correct
     */
    @Test
    public void testAddScannedProduct() 
    {
        software.startSession();
        software.touchScreen.skip = false;
        //Make sure expected Mass is zero before adding
        assertEquals(0,software.getExpectedTotalWeight().compareTo(Mass.ZERO));
        software.updateCart.addScannedProduct(barcode1);
        //make sure customer gets blocked
        assertTrue(software.isBlocked());
        //item get added products hash map that tracks weight
        assertTrue(software.getProductsInOrder().containsKey(barcodedProduct1));
        assertEquals(0,bar1Item.getMass().compareTo(software.getExpectedTotalWeight()));
        assertEquals(0,bar1Item.getMass().compareTo(software.getProductsInOrder().get(barcodedProduct1)));
        //item added to barcoded products array list
        assertTrue(software.getBarcodedProductsInOrder().contains(barcodedProduct1));
        //orderTotalCorrect
        assertEquals(bar1price,software.getOrderTotal());
        //put in Bagging Area
        hardware.getBaggingArea().addAnItem(bar1Item);
        //customer unblocked?
        assertFalse(software.isBlocked());
    }

    @Test
    public void testAddScannedProductSkipBagging()
    {
        software.startSession();
        software.touchScreen.skip = true;
        //Make sure expected Mass is zero before adding
        assertEquals(0,software.getExpectedTotalWeight().compareTo(Mass.ZERO));
        software.updateCart.addScannedProduct(barcode1);
        //make sure customer gets unblocked by attendant
        assertFalse(software.isBlocked());
        //item get added products hash map that tracks weight
        assertTrue(software.getProductsInOrder().containsKey(barcodedProduct1));
        assertEquals(0,Mass.ZERO.compareTo(software.getExpectedTotalWeight()));
        assertEquals(0,bar1Item.getMass().compareTo(software.getProductsInOrder().get(barcodedProduct1)));
        //item added to barcoded products array list
        assertTrue(software.getBarcodedProductsInOrder().contains(barcodedProduct1));
        //orderTotalCorrect
        assertEquals(bar1price,software.getOrderTotal());
    }
    
    @Test
    public void testAddProductSendsToAddScannedProduct()
    {
    	software.updateCart.addProduct(barcodedProduct1);
        assertTrue(software.getProductsInOrder().containsKey(barcodedProduct1));
        assertTrue(software.getBarcodedProductsInOrder().contains(barcodedProduct1));
    }

    @Test
    public void testAddProductSendsToAddPLUProduct()
    {
        software.updateCart.addProduct(PLUProduct1);
    	assertTrue(software.getPluCodedProductsInOrder().contains(PLUProduct1));
        assertTrue(software.getProductsInOrder().containsKey(PLUProduct1));
    }

    @Test
    public void removePLUCodedProduct() {
        software.startSession();
        software.touchScreen.skip = false;
        hardware.getScanningArea().addAnItem(PLUProduct1item);
        software.updateCart.addPLUProduct(PLUProduct1);
        //item get added products hash map that tracks weight
        assertTrue(software.getProductsInOrder().containsKey(PLUProduct1));
        assertEquals(0, PLUProduct1item.getMass().compareTo(software.getExpectedTotalWeight()));
        assertEquals(0, PLUProduct1item.getMass().compareTo(software.getProductsInOrder().get(PLUProduct1)));
        //item added to PLU products array list
        assertTrue(software.getPluCodedProductsInOrder().contains(PLUProduct1));
        //orderTotalCorrect
        assertEquals(plu1Price, software.getOrderTotal());
        //remove item from main scale and put in Bagging Area
        hardware.getScanningArea().removeAnItem(PLUProduct1item);
        hardware.getBaggingArea().addAnItem(PLUProduct1item);
        //customer unblocked?
        assertFalse(software.isBlocked());

        //now we can remove
        software.updateCart.removeItem(PLUProduct1);
        //customer blocked
        assertTrue(software.isBlocked());
        //removed from PLU products
        assertFalse(software.getPluCodedProductsInOrder().contains(PLUProduct1));
        //price updated
        assertEquals(BigDecimal.ZERO, software.getOrderTotal());
        //removed from weight map
        assertFalse(software.getProductsInOrder().containsKey(PLUProduct1));
        //expected weight updated
        assertEquals(0, Mass.ZERO.compareTo(software.getExpectedTotalWeight()));
        //remove from bagging
        hardware.getBaggingArea().removeAnItem(PLUProduct1item);
        //unblocked?
        assertFalse(software.isBlocked());
    }

    /**
     * Test adding the same scanned product 3 times
     */
    @Test
    public void testAddSameScannedProuductTwice() {
        software.startSession();
        software.touchScreen.skip = false;

        software.updateCart.addScannedProduct(barcode1);
        hardware.getBaggingArea().addAnItem(bar1Item);

        software.updateCart.addScannedProduct(barcode1);
        hardware.getBaggingArea().addAnItem(bar1ItemCopied);

        assertEquals(bar1Item.getMass().sum(bar1ItemCopied.getMass()), software.getProductsInOrder().get(barcodedProduct1));
        assertEquals(bar1Item.getMass().sum(bar1ItemCopied.getMass()), software.getBaggedProducts().get(barcodedProduct1));
    }
    @Test
    public void testAddSamePLUproductTwice() {
        software.startSession();;
        software.touchScreen.skip = false;

        hardware.getScanningArea().addAnItem(PLUProduct1item);
        software.updateCart.addPLUProduct(PLUProduct1);
        hardware.getScanningArea().removeAnItem(PLUProduct1item);
        hardware.getBaggingArea().addAnItem(PLUProduct1item);

        hardware.getScanningArea().addAnItem(PLUProduct1itemCopy);
        software.updateCart.addPLUProduct(PLUProduct1);
        hardware.getScanningArea().removeAnItem(PLUProduct1itemCopy);
        hardware.getBaggingArea().addAnItem(PLUProduct1itemCopy);

        assertEquals(PLUProduct1item.getMass().sum(PLUProduct1itemCopy.getMass()), software.getBaggedProducts().get(PLUProduct1));
        assertEquals(PLUProduct1item.getMass().sum(PLUProduct1itemCopy.getMass()), software.getProductsInOrder().get(PLUProduct1));
    }

    @Test
    public void testTextSearchForBarcodedProduct()
    {
    	ArrayList<Product> matches = updateCart.textSearch("batteries");
        assertEquals(1, matches.size());
    	assertEquals(matches.get(0), barcodedProduct2);
    }

    @Test
    public void testTextSearchForPLUProduct()
    {
    	ArrayList<Product> matches = updateCart.textSearch("coffee");
        assertEquals(1, matches.size());
    	assertEquals(matches.get(0), PLUProduct3);
    }

    @Test
    public void testTextSearchForPLUAndBarcodedProduct()
    {
    	//create products that both have apple in the name
        Numeral[] testBarcode = new Numeral[4];
        testBarcode[0] = Numeral.six;
        testBarcode[1] = Numeral.two;
        testBarcode[2] = Numeral.two;
        testBarcode[3] = Numeral.two;
        barcode1 = new Barcode(testBarcode);
        BarcodedProduct bP1 = new BarcodedProduct(barcode1, "bundle of apples", 30, 100);

        PriceLookUpCode PLUCode = new PriceLookUpCode("9028");
        PLUCodedProduct PLUP1 = new PLUCodedProduct(PLUCode, "singular wrapped apple", 26);
        
        //add barcodedProduct and PLUCodedProduct to the database
        ProductDatabases.PLU_PRODUCT_DATABASE.put(PLUCode, PLUP1);
        ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode1, bP1);


    	ArrayList<Product> matches = updateCart.textSearch("apple");
        assertEquals(2, matches.size());
        assertNotSame(matches.get(0), matches.get(1));
    	assertTrue((matches.get(0) == PLUP1) || matches.get(0) == bP1);
    	assertTrue((matches.get(1) == PLUP1) || matches.get(1) == bP1);
    }
    
    @Test
    public void testTexSearchWithNoMatches()
    {
    	ArrayList<Product> matches = updateCart.textSearch("corn");
        assertEquals(0, matches.size());
    }
    
    @Test(expected = NullPointerSimulationException.class)
    public void testTextSearchWithNullStr()
    {
    	updateCart.textSearch(null);
    }
}
