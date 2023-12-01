package com.thelocalmarketplace.software;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jjjwelectronics.Mass;
import com.jjjwelectronics.bag.IReusableBagDispenser;
import com.jjjwelectronics.card.ICardReader;
import com.jjjwelectronics.printer.IReceiptPrinter;
import com.jjjwelectronics.scale.IElectronicScale;
import com.jjjwelectronics.scanner.*;
import com.tdc.banknote.BanknoteDispensationSlot;
import com.tdc.banknote.BanknoteValidator;
import com.tdc.coin.CoinStorageUnit;
import com.tdc.coin.CoinValidator;
import com.tdc.coin.ICoinDispenser;
import com.thelocalmarketplace.hardware.*;
import powerutility.PowerGrid;

/**
 * This Class takes the hardware from a self-checkout station the software needs to interact with
 * for the use cases we are dealing with(I may have missed something that needs to be added)
 * /n
 * all other classes we create should take this class as a param. Most likely an instance of
 * that class should be created as an attribute of this class then initialized
 * /n
 * you may need to create or modify methods in this class
 * /n
 * the enable and disable functions may mess with pay system if some things are only active
 * when paying(can modify)
 * /n
 * .
 */
public class Software {
	private BigDecimal orderTotal;
	private Mass expectedTotalWeight;
	private boolean blocked = false;
	private boolean customerStationBlock = false;
	private HashMap<Product,Mass> productsInOrder;
	private ArrayList<BarcodedProduct> barcodedProductsInOrder;
	private ArrayList<PLUCodedProduct> pluCodedProductsInOrder;
	private HashMap<Product,Mass> baggedProducts;
	private MembershipNumberValidator membershipValidator;
	private MembershipDatabase membershipDatabase;

	public final IElectronicScale baggingAreaScale;
	public final IElectronicScale scannerScale;
	public final IBarcodeScanner handHeldScanner;
	public final IBarcodeScanner mainScanner;
	public final BanknoteValidator banknoteValidator;
	public final CoinValidator coinValidator;
	public final ICardReader cardReader;
	public final IReceiptPrinter printer;
	public final IReusableBagDispenser reusableBagDispenser;
	// add instances of your class here then initialize below
	public WeightDiscrepancy weightDiscrepancy;
	public TouchScreen touchScreen;
	public Attendant attendant;
	public PayByBanknote payByBanknote;
	public PayByCoin payByCoin;
	public PrintReceipt printReceipt;
	public PayByCard payByCard;
	public UpdateCart updateCart;
	public Maintenance maintenance;
	public PurchaseBags purchaseBags;

	public Mass allowableBagWeight;
	public final BanknoteDispensationSlot banknoteDispenser;
	public final CoinTray coinTray;
	public final Map<BigDecimal, ICoinDispenser> coinDispensers;
	
	/**
     * A boolean variable that keeps track of whether a customer needs attention.
     **/
	private boolean needsAttention = false;

	private AbstractSelfCheckoutStation station;
	private SelfCheckoutStationBronze bronze;
	private SelfCheckoutStationSilver silver;
	private SelfCheckoutStationGold gold;


	public static Software getInstance(AbstractSelfCheckoutStation hardware) {
		return new Software(hardware);
	}
	
	public static Software getBronzeInstance(SelfCheckoutStationBronze bronze) {
		return new Software(bronze);
	}
	
	public static Software getInstance(SelfCheckoutStationSilver silver) {
		return new Software(silver);
	}
	
	public static Software getInstance(SelfCheckoutStationGold gold) {
		return new Software(gold);
	}
	
	public void initializeComponents() {
		expectedTotalWeight = Mass.ZERO;
		orderTotal = BigDecimal.ZERO;

		//Initialize Software Components
		weightDiscrepancy = new WeightDiscrepancy(this);
		touchScreen = new TouchScreen(this);
		attendant = new Attendant(this);
		updateCart = new UpdateCart(this);
		payByBanknote = new PayByBanknote(this);
		payByCard = new PayByCard(this);
		payByCoin = new PayByCoin(this);
		printReceipt = new PrintReceipt(this);
		maintenance = new Maintenance(this);
		purchaseBags = new PurchaseBags(this);

		//Initialize Product Lists and Weight Limit
		productsInOrder = new HashMap<>();
		barcodedProductsInOrder = new ArrayList<>();
		pluCodedProductsInOrder = new ArrayList<>();
		baggedProducts = new HashMap<>();
		allowableBagWeight = new Mass(200.0);   // default value of 200g
		this.membershipDatabase = new MembershipDatabase();
	    this.membershipValidator = new MembershipNumberValidator(this.membershipDatabase);
	}
	
	/*Constructor for SelfCheckout
	 *  
	 */
	public Software(AbstractSelfCheckoutStation hardware) {
		this.baggingAreaScale = hardware.getBaggingArea();
		this.scannerScale = hardware.getScanningArea();
		this.handHeldScanner = hardware.getHandheldScanner();
		this.mainScanner = hardware.getMainScanner();
		this.banknoteValidator = hardware.getBanknoteValidator();
		this.coinValidator = hardware.getCoinValidator();
		this.cardReader = hardware.getCardReader();
		this.banknoteDispenser = hardware.getBanknoteOutput();
		this.coinTray = hardware.getCoinTray();
		this.printer = hardware.getPrinter();
		this.coinDispensers = hardware.getCoinDispensers();
		this.reusableBagDispenser = hardware.getReusableBagDispenser();
		initializeComponents();
	}
		
	public Software(SelfCheckoutStationBronze bronze) {
		this.station = this.bronze;
		this.baggingAreaScale = this.bronze.getBaggingArea();
		this.scannerScale = this.bronze.getScanningArea();
		this.handHeldScanner = this.bronze.getHandheldScanner();	
		this.mainScanner = this.bronze.getMainScanner();
		this.banknoteValidator = this.bronze.getBanknoteValidator();
		this.coinValidator = this.bronze.getCoinValidator();
		this.cardReader = this.bronze.getCardReader();
		this.banknoteDispenser = this.bronze.getBanknoteOutput();
		this.coinTray = this.bronze.getCoinTray();
		this.printer = this.bronze.getPrinter();
		this.coinDispensers = this.bronze.getCoinDispensers();				
		this.reusableBagDispenser = this.bronze.getReusableBagDispenser();
		initializeComponents();
	}
			
	public Software(SelfCheckoutStationSilver silver) {
		this.station = this.silver;
		this.baggingAreaScale = this.silver.getBaggingArea();
		this.scannerScale = this.silver.getScanningArea();
		this.handHeldScanner = this.silver.getHandheldScanner();
		this.mainScanner = this.silver.getMainScanner();
		this.banknoteValidator = this.silver.getBanknoteValidator();
		this.coinValidator = this.silver.getCoinValidator();
		this.cardReader = this.silver.getCardReader();
		this.banknoteDispenser = this.silver.getBanknoteOutput();
		this.coinTray = this.silver.getCoinTray();
		this.printer = this.silver.getPrinter();
		this.coinDispensers = this.silver.getCoinDispensers();
		this.reusableBagDispenser = this.silver.getReusableBagDispenser();
		initializeComponents();
	}	
				
	public Software(SelfCheckoutStationGold gold) {
		this.station = this.gold;
		this.baggingAreaScale = this.gold.getBaggingArea();
		this.scannerScale = this.gold.getScanningArea();
		this.handHeldScanner = this.gold.getHandheldScanner();
		this.mainScanner = this.gold.getMainScanner();
		this.banknoteValidator = this.gold.getBanknoteValidator();
		this.coinValidator = this.gold.getCoinValidator();
		this.cardReader = this.gold.getCardReader();
		this.banknoteDispenser = this.gold.getBanknoteOutput();
		this.coinTray = this.gold.getCoinTray();
		this.printer = this.gold.getPrinter();
		this.coinDispensers = this.gold.getCoinDispensers();
		this.reusableBagDispenser = this.gold.getReusableBagDispenser();
		initializeComponents();
	}
				
	/**
	 * Turns on the self-checkout system by plugging it into the power grid and activating the hardware components.
	 * This method must be called before starting a session or conducting any self-checkout operations.
	 */
	public void turnOn() {
		PowerGrid grid = PowerGrid.instance();
		station.plugIn(grid);
		station.turnOn();
	}
	
	/**
	 * Starts a new self-checkout session by enabling necessary hardware components and checking maintenance.
	 * This method should be called at the beginning of each customer interaction session.
	 * It enables handheld and main scanners, as well as the bagging area scale.
	 */
	public void startSession() {
		endSession();
		handHeldScanner.enable();
		mainScanner.enable();
		baggingAreaScale.enable();
		
		// Check for maintenance and predict issues
		maintenance.checkInk(printReceipt.getAveragePrintedChars());
        maintenance.checkPaper(printReceipt.getAveragePaperUsed());
        maintenance.predictCoinsFullStorage();
        for (BigDecimal denomination : coinDispensers.keySet()) {
        	maintenance.predictLowCoinsDispenser(denomination);
        	maintenance.predictCoinsFullDispenser(denomination);
        }
        if (maintenance.getIssues().size() != 0) {
        	notifyMaintenance(maintenance.getIssues());
        }
	}
	
	/**
	 * Ends the current self-checkout session, clearing the order data, checking maintenance,
	 * and resetting the expected total weight.
	 * This method should be called at the end of each customer interaction session.
	 */
	public void endSession() {
		// Check for maintenance and predict issues
		maintenance.checkInk(printReceipt.getAveragePrintedChars());
        maintenance.checkPaper(printReceipt.getAveragePaperUsed());
        maintenance.predictCoinsFullStorage();
        for (BigDecimal denomination : coinDispensers.keySet()) {
        	maintenance.predictLowCoinsDispenser(denomination);
        	maintenance.predictCoinsFullDispenser(denomination);
        }
        if (maintenance.getIssues().size() != 0) {
        	notifyMaintenance(maintenance.getIssues());
        }
        
		baggedProducts.clear();
		barcodedProductsInOrder.clear();
		expectedTotalWeight = Mass.ZERO;
		orderTotal = BigDecimal.ZERO;
	}

	/**
	 * Blocks customer interactions by disabling various hardware components.
	 * This method is used to prevent unwanted interactions during specific conditions.
	 */
	public void blockCustomer() {
		handHeldScanner.disable();
		mainScanner.disable();
		coinValidator.disable();
		banknoteValidator.disable();
		cardReader.disable();
		blocked = true;
	}
	
	/**
	 * Unblocks customer interactions by enabling necessary hardware components.
	 * This method is used to restore customer interactions after being blocked.
	 */
	public void unblockCustomer() {
		handHeldScanner.enable();
		mainScanner.enable();
		blocked = false;
	}
	
	/**
	 * Checks if customer interactions are currently blocked.
	 *
	 * @return True if interactions are blocked, false otherwise.
	 */
	public boolean isBlocked() {
		return blocked;
	}
	
	/**
	 * Blocks customer station from any type of interaction.
	 * This method is used to prevent unwanted interactions during maintenance or when
	 * the hardware or software is out of order.
	 */
	public void blockCustomerStation() {
		baggingAreaScale.disable();
		scannerScale.disable();
		handHeldScanner.disable();
		mainScanner.disable();
		banknoteValidator.disable();
		coinValidator.disable();
		cardReader.disable();
		banknoteDispenser.disable();
		printer.disable();
		customerStationBlock = true;
	}

	/**
	 * Unblocks customer interactions by enabling necessary hardware and software components.
	 * This method is used to restore system interaction after being blocked.
	 */
	public void unblockCustomerStation() {
		baggingAreaScale.enable();
		scannerScale.enable();
		handHeldScanner.enable();;
		mainScanner.enable();;
		banknoteValidator.enable();
		coinValidator.enable();;
		cardReader.enable();;
		banknoteDispenser.enable();
		printer.enable();
		customerStationBlock = false;
	}
	
	/**
	 * Checks if the customer station is currently blocked.
	 *
	 * @return True if interactions are blocked, false otherwise.
	 */
	public boolean isCustomerStationBlocked() {
		return customerStationBlock;
	}

	public HashMap<Product, Mass> getProductsInOrder() {
		return productsInOrder;
	}

	public ArrayList<PLUCodedProduct> getPluCodedProductsInOrder(){
		return pluCodedProductsInOrder;
	}

	/**
	 * Retrieves the current order total amount.
	 *
	 * @return The current order total amount.
	 */


	public BigDecimal getOrderTotal() {
		return orderTotal;
	}
	
	/**
	 * Adds a specified price to the current order total.
	 *
	 * @param price The price to be added to the order total.
	 */
	public void addToOrderTotal(BigDecimal price) {
		orderTotal = orderTotal.add(price);
	}

	/**
	 * Subtracts a specified price from the current order total.
	 *
	 * @param price The price to be subtracted from the order total.
	 */
	public void subtractFromOrderTotal(BigDecimal price) {
		orderTotal = orderTotal.subtract(price);
	}

	/**
	 * Retrieves the expected total weight of the products in the order.
	 *
	 * @return The expected total weight.
	 */
	public Mass getExpectedTotalWeight() {
		return expectedTotalWeight;
	}

	/**
	 * Sets the expected total weight of the products in the order.
	 *
	 * @param expectedTotalWeight The new expected total weight.
	 */
	public void setExpectedTotalWeight(Mass expectedTotalWeight) {
		this.expectedTotalWeight = expectedTotalWeight;
	}

	/**
	 * Retrieves the list of barcoded products in the current order.
	 *
	 * @return The list of barcoded products in the order.
	 */
	public ArrayList<BarcodedProduct> getBarcodedProductsInOrder() {
		return barcodedProductsInOrder;
	}


	public void setAllowableBagWeight(Mass allowableBagWeight) {
		this.allowableBagWeight = allowableBagWeight;
	}

	/**
	 * Adds a barcoded product to the list of bagged products.
	 *
	 * @param product The barcoded product to be added to the bagged products list.
	 */
	public void addBaggedProduct(Product product, Mass mass) {
		baggedProducts.put(product, mass);
	}
	
	/**
	 * Retrieves the list of bagged products.
	 *
	 * @return The list of bagged products.
	 */
	public HashMap<Product, Mass> getBaggedProducts(){
		return baggedProducts;
	}



	/**
	 * Retrieves the array of banknote denominations supported by the self-checkout station.
	 *
	 * @return The array of banknote denominations.
	 */
	public BigDecimal[] getBanknoteDenominations() {
		return station.getBanknoteDenominations();
	}

	/**
	 * Retrieves the list of coin denominations supported by the self-checkout station.
	 *
	 * @return The list of coin denominations.
	 */
	public List<BigDecimal> getCoinDenominations() {
		return station.getCoinDenominations();
	}

	/**
	 * Retrieves the banknote dispenser of the self-checkout station.
	 *
	 * @return The banknote dispenser.
	 */
	public BanknoteDispensationSlot getBanknoteDispenser() {
		return banknoteDispenser;
	}

	/**
	 * Retrieves the coin tray of the self-checkout station.
	 *
	 * @return The coin tray.
	 */
	public CoinTray getCoinTray() {
		return coinTray;
	}

	/**
	 * Sets the updated order total amount.
	 *
	 * @param newAmountDue The new amount due for the order.
	 */
	public void setUpdatedOrderTotal(BigDecimal newAmountDue) {
		orderTotal = newAmountDue;
	}
	
	/**
	 * For testing purposes, sets the touch screen of the self-checkout system to a specified touch screen instance.
	 *
	 * @param touchScreen The touch screen instance for testing.
	 */
	public void setTestTouchScreen (TouchScreen touchScreen) {
		this.touchScreen = touchScreen;
	}
	
	public MembershipNumberValidator getMembershipValidator() {
        return membershipValidator;
	}
	/// Handy for GUI team
	public void handleMembershipNumber(String membershipNumber) {
		        // First, validate the format of the membership number
		        if (membershipValidator.isValid(membershipNumber)) {
		            // Convert the string to an integer for database lookup
		            int memberId = Integer.parseInt(membershipNumber);

		            // Check if the member exists in the database
		            if (membershipDatabase.memberExists(memberId)) {
		                // Process the valid membership number
		                System.out.println("Membership number is valid and found in the database.");
		                // TODO: Link to customer session, update points, etc.
		            } else {
		                // Valid format, but not found in the database
		                System.out.println("Membership number not found in the database.");
		                // TODO: Handle this case, potentially send feedback to GUI
		            }
		        } else {
		            // Invalid format
		            System.out.println("Invalid membership number format.");
		            // TODO: Send invalid format feedback to GUI
		        }	
		 }
	/**
	 * Retrieves the coin dispensers of the self-checkout station.
	 *
	 * @return The coin dispensers.
	 */
	public Map<BigDecimal, ICoinDispenser> getCoinDispensers() {
		return coinDispensers;
	}
	
	/**
	 * Retrieves the coin storage unit of the self-checkout station.
	 *
	 * @return The coin storage unit.
	 */
	public CoinStorageUnit getCoinStorage() {
		return station.getCoinStorage();
	}
	
	/**
	 * Notifcation method specifically for addressing maintenance issues
	 * @param issues, Arraylist of string
	 */
	public void notifyMaintenance(ArrayList<String> issues) {
		attendant.addressMaintenanceIssues(issues);
		setNeedsAttentionToTrue();
	}
	
	/**
	 * Notifies the attendant that they should attend to the customer
	 **/
	public void notifyAttendant() {
	    if (needsAttention == true) {
	    	attendant.setAttendedToFalse();
	        attendant.respondToCustomer();
	    } else {
	        // Nothing should happen here since this should never happen
	    }
	}
	
	/**
	 * Sets the needs attention field to true
	 **/
	public void setNeedsAttentionToTrue() {
		needsAttention = true;
	}
	
	/**
	 * Sets need attention field to false
	 **/
	public void setNeedsAttentionToFalse() {
		needsAttention = false;
	}
}
