/**
* Jon Mulyk (UCID: 30093143)
* Elizabeth Szentmiklossy (UCID: 30165216)
* Ahmed Ibrahim Mohamed Seifledin Hadsan (UCID: 30174024)
* Arthur Huan (UCID: 30197354)
* Jaden Myers (UCID: 30152504)
* Jane Magai (UCID: 30180119)
* Ahmed Elshabasi (UCID: 30188386)
* Jincheng Li (UCID: 30172907)
* Sina Salahshour (UCID: 30177165)
* Anthony Tolentino (UCID: 30081427) */


package com.thelocalmarketplace.software.test;

import com.jjjwelectronics.Item;
import com.jjjwelectronics.Mass;
import com.jjjwelectronics.Numeral;
import com.jjjwelectronics.scale.AbstractElectronicScale;
import com.jjjwelectronics.scale.ElectronicScaleBronze;
import com.jjjwelectronics.scale.ElectronicScaleGold;
import com.jjjwelectronics.scale.ElectronicScaleSilver;
import com.jjjwelectronics.scanner.Barcode;
import com.jjjwelectronics.scanner.BarcodeScannerBronze;
import com.jjjwelectronics.scanner.BarcodeScannerGold;
import com.jjjwelectronics.scanner.BarcodeScannerListener;
import com.jjjwelectronics.scanner.BarcodedItem;
import com.jjjwelectronics.scanner.IBarcodeScanner;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.Product;
import com.thelocalmarketplace.hardware.external.ProductDatabases;
import com.thelocalmarketplace.software.ActionBlocker;
import com.thelocalmarketplace.software.ItemController;
import com.thelocalmarketplace.software.WeightDiscrepancy;

import ca.ucalgary.seng300.simulation.NullPointerSimulationException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import powerutility.NoPowerException;
import powerutility.PowerGrid;
import com.thelocalmarketplace.hardware.external.ProductDatabases;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * AddItemByBarcodeTest class handles test cases relating to the AddItemByBarcode class.
 *
 * @author Enzo Mutiso UCID: 30182555
 */
public class AddItemByBarcodeTest {

    /**
     * The AddItemByBarcode object to be tested.
     */
    private ItemController itemController;
    /**
     * The order where products will be added.
     */
//    private Map<Barcode, BarcodedProduct> order = new  HashMap<Barcode, BarcodedProduct>();
    private HashMap<Item, Integer> order = new HashMap<Item, Integer>();
     
    /**
     * Intialize the Barcodes
     */
    Numeral[] numeralArray1 = new Numeral[] {Numeral.zero, Numeral.one, Numeral.zero};
    Numeral[] numeralArray2 = new Numeral[] {Numeral.zero, Numeral.one, Numeral.one};
    private Barcode barcode1 = new Barcode(numeralArray1);
    private Barcode barcode2 = new Barcode(numeralArray2);
    /**
     * The first BarcodedProduct object to be added to the order.
     */
    private BarcodedProduct barcodedProduct1;
    /**
     * The second BarcodedProduct object to be added to the order.
     */
    private BarcodedProduct barcodedProduct2;
    /**
     * The expected weight to match with the actual weight.
     */
    private Mass expectedWeight;
    /**
     * The WeightDiscrepancy object for weight comparison.
     */
    private WeightDiscrepancy discrepancy;
    /**
     * The ActionBlocker object to block customer interaction.
     */
    private ActionBlocker blocker;
    /**
     * The ElectronicScale object to get the actual weight.
     */
    
	private BarcodeScannerGold barcodescanner;
	private ElectronicScaleBronze electronicScale;
	private BarcodedItem item1;
	private BarcodedItem item2;


    /**
     * Sets up the test fixture. Called before every test case method.
     */
    @Before
    public void SetUp() {
        electronicScale = new ElectronicScaleBronze();
        barcodedProduct1 = new BarcodedProduct(barcode1, "Apple", 50, 5);
        barcodedProduct2 = new BarcodedProduct(barcode2, "Toast", 100, 100);
        expectedWeight = Mass.ZERO;
        discrepancy = new WeightDiscrepancy(expectedWeight, electronicScale);
        blocker = new ActionBlocker();
        barcodescanner  = new BarcodeScannerGold();
        item1 = new BarcodedItem(barcode1, new Mass(barcodedProduct1.getExpectedWeight()));
        item2 = new BarcodedItem(barcode2, new Mass(barcodedProduct2.getExpectedWeight()));
        itemController = new ItemController(barcodescanner, order, discrepancy, blocker, electronicScale);
        itemController.register(discrepancy);
        // add product to database
        ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode1, barcodedProduct1);
        ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode2, barcodedProduct2);

        // try catch to turn everything on
        try {
        	barcodescanner.plugIn(PowerGrid.instance());
        	barcodescanner.turnOn();
            this.electronicScale.plugIn(PowerGrid.instance());
            this.electronicScale.turnOn();
            this.electronicScale.enable();
        } catch (Exception e) {
            PowerGrid.instance().forcePowerRestore();
            SetUp();
        }

    }
    
    @After
    public void tearDown() {
    	itemController = null;
    	order = null;
    	ProductDatabases.BARCODED_PRODUCT_DATABASE.clear();
    }

    /**
     * Tests the functionality of adding a barcode to the order. Verifies if the barcode has been successfully added to the order list.
     *
     */
    @Test
    public void addBarcodeToOrder() {
        electronicScale.addAnItem(item1); // use stub to simulate weight change
        itemController.scanBarcode(item1);
//        assertEquals(1, itemController.getOrder().size());
        assertEquals(1, itemController.getTotalAmountOfItemsFromOrder(order));
    }

    /**
     * Tests the scenario when a product is not found in the database. Expects the interaction to be blocked.
     *
     */
    @Test
    public void testProductNotFoundException() {
        Numeral[] numeralTest = {Numeral.one, Numeral.seven};
        Barcode notInDatabaseBarcode = new Barcode(numeralTest);
        BarcodedItem item = new BarcodedItem(notInDatabaseBarcode, new Mass(barcodedProduct1.getExpectedWeight()));
        itemController.scanBarcode(item);
        assertEquals(0, itemController.getTotalAmountOfItemsFromOrder(order));
    }

    /**
     * Tests the retrieval of the expected weight. Verifies if the retrieved weight matches the expected weight.
     */
    @Test
    public void testGetExpectedWeight() {
        assertEquals(expectedWeight, itemController.getExpectedWeight());
        electronicScale.addAnItem(item1); // use stub to simulate weight change
        itemController.scanBarcode(item1);
        assertEquals(new Mass(barcodedProduct1.getExpectedWeight()),itemController.getExpectedWeight());
    }

    /**
     * Test to check whether proper exception is thrown when weight mismatch occurs.
     */
    @Test
    public void testWrongWeight() {
        electronicScale.addAnItem(item2);
        assertTrue(barcodescanner.isDisabled());
    }

    /**
     * Tests the functionality of adding multiple barcoded products to the order. Verifies if the product has been successfully added to the order list and if the barcode scanner has been disabled.
     */
    @Test
    public void testAddBarcodedProductsToOrder() {
        electronicScale.addAnItem(item1); // use stub to simulate weight change
        itemController.scanBarcode(item1);
        assertEquals(1, itemController.getTotalAmountOfItemsFromOrder(order));
        electronicScale.addAnItem(item2); // use stub to simulate weight change
        itemController.scanBarcode(item2);
        assertEquals(2, itemController.getTotalAmountOfItemsFromOrder(order));
    }

    
    /**
     *  Tests whether price is correctly maintained
     */
    @Test
    public void testTotalPrice() {
    	assertEquals(0.0, itemController.getTotalPrice(), 0.01);
    	electronicScale.addAnItem(item1); // use stub to simulate weight change
        itemController.scanBarcode(item1);
        assertEquals(50, itemController.getTotalPrice(), 0.01);
        electronicScale.addAnItem(item2); // use stub to simulate weight change
        itemController.scanBarcode(item2);
        assertEquals(150, itemController.getTotalPrice(), 0.01);
    }

}
