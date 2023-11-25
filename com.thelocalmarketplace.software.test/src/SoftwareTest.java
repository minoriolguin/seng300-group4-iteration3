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
import com.jjjwelectronics.card.ICardReader;
import com.jjjwelectronics.scale.IElectronicScale;
import com.jjjwelectronics.scanner.Barcode;
import com.jjjwelectronics.scanner.IBarcodeScanner;
import com.tdc.banknote.BanknoteDispensationSlot;
import com.tdc.banknote.BanknoteValidator;
import com.tdc.coin.Coin;
import com.tdc.coin.CoinValidator;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.CoinTray;
import com.thelocalmarketplace.hardware.PLUCodedItem;
import com.thelocalmarketplace.hardware.SelfCheckoutStationBronze;
import com.thelocalmarketplace.hardware.SelfCheckoutStationSilver;
import com.thelocalmarketplace.hardware.external.ProductDatabases;
import com.thelocalmarketplace.hardware.SelfCheckoutStationGold;
import com.thelocalmarketplace.hardware.PriceLookUpCode;
import com.thelocalmarketplace.hardware.PLUCodedProduct;
import org.junit.Before;
import org.junit.Test;
import powerutility.PowerGrid;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Currency;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class SoftwareTest {
    private SelfCheckoutStationBronze station;
    private SelfCheckoutStationSilver station_silver;
    private SelfCheckoutStationGold station_gold;
    private AbstractSelfCheckoutStation station_abstract;
    
    private Software checkout;
    private Software checkout_silver;
    private Software checkout_gold;
    
    //    private static SelfCheckoutStationGold station;
    private BarcodedProduct product;
    private Barcode barcode;
    private double expectedWeight;
    private long price;
    private String description;


    public IElectronicScale baggingAreaScale;
    public IBarcodeScanner handHeldScanner;
    public IBarcodeScanner mainScanner;
    public BanknoteValidator banknoteValidator;
    public CoinValidator coinValidator;
    public ICardReader cardReader;
    public WeightDiscrepancy weightDiscrepancy;
    public TouchScreen touchScreen;
    public Attendant attendant;
    public PayByBanknote payByBanknote;

    public UpdateCart updateCart;

    public Mass allowableBagWeight;

	private ArrayList<BarcodedProduct> barcodedProductsInOrder;
	private ArrayList<BarcodedProduct> baggedProducts;

    private ArrayList<BigDecimal> coindenominations;
    private Currency CAD;
    private BigDecimal[] billDenominations;

    private static final Currency CAD_Currency = Currency.getInstance("CAD");
    private static final BigDecimal value_toonie = new BigDecimal("2.00");
    private static final BigDecimal value_loonie = new BigDecimal("1.00");
    private static final BigDecimal value_quarter = new BigDecimal("0.25");
    private static final BigDecimal value_dime = new BigDecimal("0.10");
    private static final BigDecimal value_nickel = new BigDecimal("0.05");
    private static final BigDecimal value_penny = new BigDecimal("0.01");

    private Coin coin_toonie = new Coin(CAD_Currency,value_toonie);
    private Coin coin_loonie = new Coin(CAD_Currency,value_loonie);
    private Coin coin_quarter = new Coin(CAD_Currency,value_quarter);
    private Coin coin_dime = new Coin(CAD_Currency,value_dime);
    private Coin coin_nickel = new Coin(CAD_Currency,value_nickel);
    private Coin coin_penny = new Coin(CAD_Currency,value_penny);

    @Before
    public void Setup(){
        coindenominations = new ArrayList<BigDecimal>();
        CAD = Currency.getInstance("CAD");
        coindenominations.add(value_toonie);
        coindenominations.add(value_loonie);
        coindenominations.add(value_quarter);
        coindenominations.add(value_dime);
        coindenominations.add(value_nickel);
        coindenominations.add(value_penny);
        coinValidator = new CoinValidator(CAD, coindenominations);

        billDenominations = new BigDecimal[5];
        billDenominations[0] = new BigDecimal("5.00");
        billDenominations[1] = new BigDecimal("10.00");
        billDenominations[2] = new BigDecimal("20.00");
        billDenominations[3] = new BigDecimal("50.00");
        billDenominations[4] = new BigDecimal("100.00");
        banknoteValidator = new BanknoteValidator(CAD, billDenominations);

        Numeral[] bar = new Numeral[3];
        bar[0] = Numeral.zero;
        bar[1] = Numeral.one;
        bar[2] = Numeral.two;
        barcode = new Barcode(bar);
        expectedWeight = 100;
        description = "rice";
        price = 10;
        product = new BarcodedProduct(barcode,description,price,expectedWeight);

        Currency c = Currency.getInstance("CAD");
        BigDecimal[] billDenom = { new BigDecimal("5.00"),
                new BigDecimal("10.00"),
                new BigDecimal("20.00"),
                new BigDecimal("50.00"),
                new BigDecimal("100.00")};
        BigDecimal[] coinDenom = { new BigDecimal("0.01"),
                new BigDecimal("0.05"),
                new BigDecimal("0.1"),
                new BigDecimal("0.25"),
                new BigDecimal("1"),
                new BigDecimal("2") };

        AbstractSelfCheckoutStation.resetConfigurationToDefaults();
        AbstractSelfCheckoutStation.configureCurrency(c);
        AbstractSelfCheckoutStation.configureBanknoteDenominations(billDenom);
        AbstractSelfCheckoutStation.configureCoinDenominations(coinDenom);
        station = new SelfCheckoutStationBronze();
        checkout = Software.getInstance(station);

        //Constructor
        this.baggingAreaScale = station.baggingArea;
        this.handHeldScanner = station.handheldScanner;
        this.mainScanner = station.mainScanner;
        this.banknoteValidator = station.banknoteValidator;
        this.coinValidator = station.coinValidator;
        this.cardReader = station.cardReader;

//		PLU Coded Product that has consistent price per kg
        PriceLookUpCode plu1 = new PriceLookUpCode("00001");
        PLUCodedProduct pluProduct1 = new PLUCodedProduct(plu1, "Can of Chicken Broth", new BigDecimal("1.78").longValue());
        ProductDatabases.PLU_PRODUCT_DATABASE.put(plu1, pluProduct1);

        // PLU ITEMS = Individual Items that need to be weighed and sold independently (1 Apple can weight 2-3 kg)
        // PLU 3 - Apple that weighs 2 kg
        PriceLookUpCode plu98 = new PriceLookUpCode("00098");
        Mass pluItem98_mass = new Mass(2);
        PLUCodedItem pluItem98 = new PLUCodedItem(plu98, pluItem98_mass);

        // Barcode A (Manually set up in only Setup) for Testing
        Barcode b1 = new Barcode(new Numeral[] { Numeral.zero, Numeral.one });
        BarcodedProduct barcodeProduct1 = new BarcodedProduct(b1, "Ben Jerry Ice Cream", new BigDecimal("6.50").longValue(), 500);
        ProductDatabases.BARCODED_PRODUCT_DATABASE.put(b1, barcodeProduct1);
    }
    
    //Test to ensure that a Silver Station can be established
    @Test
    public void SetUpToTestSilver() {
        AbstractSelfCheckoutStation.resetConfigurationToDefaults();
        station_silver = new SelfCheckoutStationSilver();
        checkout_silver = Software.getInstance(station_silver);
	
        //constructor
		this.baggingAreaScale = station_silver.baggingArea;
		this.handHeldScanner = station_silver.handheldScanner;
		this.mainScanner = station_silver.mainScanner;
		this.banknoteValidator = station_silver.banknoteValidator;
		this.coinValidator = station_silver.coinValidator;
		this.cardReader = station_silver.cardReader;
    }
    
    //Test to ensure that a Gold Station can be established
    @Test
    public void SetUpToTestGold() {
        AbstractSelfCheckoutStation.resetConfigurationToDefaults();
        station_gold = new SelfCheckoutStationGold();
        checkout_gold = Software.getInstance(station_gold);
	
        //constructor
		this.baggingAreaScale = station_gold.baggingArea;
		this.handHeldScanner = station_gold.handheldScanner;
		this.mainScanner = station_gold.mainScanner;
		this.banknoteValidator = station_gold.banknoteValidator;
		this.coinValidator = station_gold.coinValidator;
		this.cardReader = station_gold.cardReader;
    }
    
    //Test that Bronze station is turned on (Bronze will be used for testing from here on)
    @Test
    public void turnOnSelfCheckoutSoftwareTest(){
        PowerGrid grid = PowerGrid.instance();
        station.plugIn(grid);
        station.turnOn();

        // Assert that each component is plugged in and turned on
        assertTrue(baggingAreaScale.isPluggedIn());
        assertTrue(handHeldScanner.isPluggedIn());
        assertTrue(mainScanner.isPluggedIn());
        assertTrue(banknoteValidator.isConnected());
        assertTrue(banknoteValidator.isActivated());
        assertTrue(coinValidator.isConnected());
        assertTrue(coinValidator.isActivated());
        assertTrue(cardReader.isPluggedIn());
    }
    
    // Test that Start Session runs correctly
    @Test
    public void testStartSession(){
        checkout.turnOn();
        checkout.startSession();
        assertFalse(handHeldScanner.isDisabled());
        assertFalse(mainScanner.isDisabled());
        assertFalse(baggingAreaScale.isDisabled());
    }
    
    // Test that End Session runs correctly
    @Test
    public void testEndSession(){
        checkout.turnOn();
        checkout.unblockCustomer();
        List<BarcodedProduct> baggedProducts = new ArrayList<BarcodedProduct>();
        checkout.endSession();
        assertTrue(baggedProducts.isEmpty());
    }
    
    // Test blocked variable returns false (default)
    @Test
    public void testIsBlocked(){
    	assertFalse(checkout.isBlocked());
    }
   
    //Test block Customer method work
    @Test
    public void blockCustomerTest(){
        checkout.turnOn();
        checkout.blockCustomer();
        assertTrue(mainScanner.isDisabled());
        assertTrue(coinValidator.isDisabled());
        assertTrue(banknoteValidator.isDisabled());
    }
    
    //Test unblock Customer method work
    @Test
    public void unblockCustomerTest(){
        checkout.turnOn();
        checkout.blockCustomer();
        checkout.unblockCustomer();
        assertFalse(handHeldScanner.isDisabled());
        assertFalse(mainScanner.isDisabled());
    }

    // Test getOrder works
    @Test
    public void getOrderTotal() {
        checkout.turnOn();
        checkout.unblockCustomer();

        BigDecimal initialOrderTotal = checkout.getOrderTotal();
        // Expected: Zero
        assertEquals(BigDecimal.ZERO, initialOrderTotal);
    }

    // Test adding $10.00 to order results in Order Total of $10.00
    @Test
    public void addToOrderTotalTest() {
        checkout.turnOn();
        checkout.unblockCustomer();

        BigDecimal initialOrderTotal = checkout.getOrderTotal();
        assertEquals(BigDecimal.ZERO, initialOrderTotal);
        BigDecimal orderPrice = new BigDecimal("10.00");
        checkout.addToOrderTotal(orderPrice);
        BigDecimal updatedOrderTotal = checkout.getOrderTotal();
        assertEquals(orderPrice, updatedOrderTotal);
    }
    
    // Test that subtracting $5.00 from $10.00 order results in $5.00
    @Test
    public void subtractFromOrderTotalTest() {
        checkout.turnOn();
        checkout.unblockCustomer();

        BigDecimal initialOrderTotal = checkout.getOrderTotal();
        assertEquals(BigDecimal.ZERO, initialOrderTotal);
        BigDecimal step1Price = new BigDecimal("10.00");
        checkout.addToOrderTotal(step1Price);
        BigDecimal updatedOrderTotal_after_add = checkout.getOrderTotal();
        assertEquals(step1Price, updatedOrderTotal_after_add);
        BigDecimal expectedresult = new BigDecimal("5.00");
        checkout.subtractFromOrderTotal(expectedresult);
        BigDecimal updatedOrderTotal_after_sub = checkout.getOrderTotal();
        assertEquals(expectedresult, updatedOrderTotal_after_sub);
    }

    // Test that getExpectedTotalWeight works with initial weight of 0
    @Test
    public void getExpectedTotalWeight() {
        checkout.turnOn();
        checkout.unblockCustomer();

        Mass initialExpectedWeight = checkout.getExpectedTotalWeight();
        assertEquals(Mass.ZERO, initialExpectedWeight);
    }

    // Test that initial weight = 0, and expected weight = 30 
    @Test
    public void setExpectedTotalWeight() {
        checkout.turnOn();
        checkout.unblockCustomer();

        Mass initialExpectedWeight = checkout.getExpectedTotalWeight();
        Mass newExpectedTotalWeight = new Mass(30);
        checkout.setExpectedTotalWeight(newExpectedTotalWeight);
        Mass updatedExpectedTotalWeight = checkout.getExpectedTotalWeight();

        assertEquals(Mass.ZERO, initialExpectedWeight);
        assertEquals(newExpectedTotalWeight, updatedExpectedTotalWeight);
    }
    
    // Test that BarcodedProductList is empty at the start 
    @Test
    public void addBarcodedProductsInOrderTest_InitiallyEmpty() {
        checkout.turnOn();
        checkout.unblockCustomer();

        List<BarcodedProduct> initialProducts = checkout.getBarcodedProductsInOrder();
        assertEquals(0, initialProducts.size());
    }
    // Test that BarcodedProductList contains the test Product when added
    @Test
    public void addBarcodedProductsInOrderTest() {
        checkout.turnOn();
        checkout.unblockCustomer();

        Barcode b1 = new Barcode(new Numeral[] { Numeral.zero, Numeral.one });
        BarcodedProduct testProduct = new BarcodedProduct(b1, "Ben Jerry Ice Cream", new BigDecimal("6.50").longValue(), 500);

        List<BarcodedProduct> initialProducts = checkout.getBarcodedProductsInOrder();
        assertTrue(initialProducts.isEmpty());
        checkout.addBarcodedProduct(testProduct);
        List<BarcodedProduct> updatedProducts = checkout.getBarcodedProductsInOrder();

        assertEquals(1, updatedProducts.size());
        assertEquals(testProduct, updatedProducts.get(0));
    }

    // Test that BaggedProduct List is empty at the start 
    @Test
    public void addBaggedProductTest_InitiallyEmpty() {
        checkout.turnOn();
        checkout.unblockCustomer();

        Barcode b1 = new Barcode(new Numeral[] { Numeral.zero, Numeral.one });
        BarcodedProduct testProduct = new BarcodedProduct(b1, "Ben Jerry Ice Cream", new BigDecimal("6.50").longValue(), 500);

        checkout.addBaggedProduct(testProduct);
    }
    // Test that BaggedProduct List contains Test Product 
    @Test
    public void addBaggedProductTest() {
        checkout.turnOn();
        checkout.unblockCustomer();

        Barcode b1 = new Barcode(new Numeral[] { Numeral.zero, Numeral.one });
        BarcodedProduct testProduct = new BarcodedProduct(b1, "Ben Jerry Ice Cream", new BigDecimal("6.50").longValue(), 500);
        checkout.addBaggedProduct(testProduct);
        List<BarcodedProduct> result = checkout.getBaggedProducts();
        assertTrue(result.contains(testProduct));
    }
    
    // Test remove Barcoded Product when product is added to BarcodedProducts only 
    // and not to Bagged Product
    @Test
    public void removeBarcodedProductTest_BarcodedProductOnly() {
        checkout.turnOn();
        checkout.unblockCustomer();
        Barcode b1 = new Barcode(new Numeral[] { Numeral.zero, Numeral.one });
        BarcodedProduct testProduct = new BarcodedProduct(b1, "Ben Jerry Ice Cream", new BigDecimal("6.50").longValue(), 500);

        //Add to barcodedProductsinOrder
        checkout.addBarcodedProduct(testProduct);
        assertTrue(checkout.getBarcodedProductsInOrder().contains(testProduct));

        //Do not add to bagged Products
        
        checkout.removeBarcodedProduct(testProduct);
        //Assert that test product has been removed from barcodedProductsInOrder
        assertFalse(checkout.getBarcodedProductsInOrder().contains(testProduct));
    }
    
    // Test remove Barcoded Product when product is added to BarcodedProducts only 
    // AND to Bagged Product
    @Test
    public void removeBarcodedProductTest_Barcoded_and_Bagged() {
        checkout.turnOn();
        checkout.unblockCustomer();
        Barcode b1 = new Barcode(new Numeral[] { Numeral.zero, Numeral.one });
        BarcodedProduct testProduct = new BarcodedProduct(b1, "Ben Jerry Ice Cream", new BigDecimal("6.50").longValue(), 500);

        //Add to barcodedProductsinOrder AND Bagged Product
        checkout.addBarcodedProduct(testProduct);
        checkout.addBaggedProduct(testProduct);
        //Assert that it was added to BarcodedProductsinOrder
        assertTrue(checkout.getBarcodedProductsInOrder().contains(testProduct));
        assertTrue(checkout.getBaggedProducts().contains(testProduct));
        //Call
        checkout.removeBarcodedProduct(testProduct);
        //Assert that test product has been removed
        assertFalse(checkout.getBarcodedProductsInOrder().contains(testProduct));
        assertFalse(checkout.getBaggedProducts().contains(testProduct));
        assertEquals(Mass.ZERO,checkout.getExpectedTotalWeight());
    }

    // Test the for loop in removeBarcoded Products regarding weight
    @Test
    public void removeBarcodedProductTest_weight() {
        checkout.turnOn();
        checkout.unblockCustomer();
        Barcode b1 = new Barcode(new Numeral[] { Numeral.zero, Numeral.one });
        BarcodedProduct testProduct1 = new BarcodedProduct(b1, "Ben Jerry Ice Cream", new BigDecimal("6.50").longValue(), 5);
        Barcode b2 = new Barcode(new Numeral[] { Numeral.zero, Numeral.one });
        BarcodedProduct testProduct2 = new BarcodedProduct(b2, "Ben Jerry Ice Cream", new BigDecimal("6.50").longValue(), 10);

        Mass product1_weight = new Mass(testProduct1.getExpectedWeight()); //5
        Mass product2_weight = new Mass(testProduct2.getExpectedWeight()); //10
        
        //Add to barcodedProductsinOrder AND Bagged Product
        checkout.addBarcodedProduct(testProduct1);
        checkout.addBaggedProduct(testProduct1);
        checkout.addBarcodedProduct(testProduct2);
        checkout.addBaggedProduct(testProduct2);
        //Call
        checkout.removeBarcodedProduct(testProduct1);
        // Verify that the expectedTotalWeight is updated correctly / should be 10
        Mass result = checkout.getExpectedTotalWeight();
        assertEquals(result, product2_weight);
    }

    //Test that methods returns Banknote Denominations back
    @Test
    public void testGetBanknoteDenominations () {
        BigDecimal[] billDenom = { new BigDecimal("5.00"),
                new BigDecimal("10.00"),
                new BigDecimal("20.00"),
                new BigDecimal("50.00"),
                new BigDecimal("100.00")};
        BigDecimal[] expected_result = checkout.getBanknoteDenominations();

        assertEquals(billDenom[0], expected_result[0]);
        assertEquals(billDenom[4], expected_result[4]);
    }
    //Test that methods returns Coin Denominations back
    @Test
    public void testGetCoinDenominations () {
        BigDecimal[] expectedCoinDenominations = {
                new BigDecimal("0.01"),
                new BigDecimal("0.05"),
                new BigDecimal("0.1"),
                new BigDecimal("0.25"),
                new BigDecimal("1"),
                new BigDecimal("2")		        };
        List<BigDecimal> actualCoinDenominations = checkout.getCoinDenominations();
        // Assert that the lists have the same elements in the same order
        assertEquals(Arrays.asList(expectedCoinDenominations), actualCoinDenominations);
    }

    //Test that methods returns Banknote Dispenser back
    @Test
    public void testGetBanknoteDispenser () {
        BanknoteDispensationSlot dispenser = checkout.getBanknoteDispenser();
        // Assert that the dispenser is not null
        assertNotNull("Banknote dispenser should not be null",dispenser);
    }

    //Test that methods returns Coin Tray back
    @Test
    public void testGetCoinTray () {
        CoinTray coinTray = checkout.getCoinTray();

        // Assert that the dispenser is not null
        assertNotNull("Coin Tray should not be null",coinTray);
    }

    //Test that methods returns Updated Order Total
    @Test
    public void testSetUpdatedOrderTotal() {
        BigDecimal before_value = checkout.getOrderTotal();
        assertEquals(before_value,BigDecimal.ZERO);

        BigDecimal add_value = new BigDecimal("10.00");
        checkout.setUpdatedOrderTotal(add_value);
        BigDecimal after_value = checkout.getOrderTotal();
        assertEquals(after_value, add_value);
    }
}
