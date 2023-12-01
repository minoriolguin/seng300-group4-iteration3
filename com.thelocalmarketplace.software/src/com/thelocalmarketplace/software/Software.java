package com.thelocalmarketplace.software;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jjjwelectronics.Mass;
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
	private final HashMap<Product,Mass> productsInOrder;
	private final ArrayList<BarcodedProduct> barcodedProductsInOrder;
	private final ArrayList<PLUCodedProduct> pluCodedProductsInOrder;
	private final HashMap<Product,Mass> baggedProducts;
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
	// add instances of your class here then initialize below
	public final WeightDiscrepancy weightDiscrepancy;
	public TouchScreen touchScreen;
	public final Attendant attendant;
	public final PayByBanknote payByBanknote;
	public final PayByCoin payByCoin;
	public final PrintReceipt printReceipt;
	public final PayByCard payByCard;
	public final UpdateCart updateCart;
	public final Maintenance maintenance;

	public Mass allowableBagWeight;
	public final BanknoteDispensationSlot banknoteDispenser;
	public final CoinTray coinTray;
	public final Map<BigDecimal, ICoinDispenser> coinDispensers;


	private AbstractSelfCheckoutStation station;
	
	public static Software getInstance(AbstractSelfCheckoutStation hardware) {
		return new Software(hardware);
	}

	/*Constructor for SelfCheckout
	 *  
	 */
	public Software(AbstractSelfCheckoutStation hardware) {
		if (hardware instanceof SelfCheckoutStationBronze bronze) {
			this.station = bronze;
			this.baggingAreaScale = bronze.getBaggingArea();
			this.scannerScale = bronze.getScanningArea();
			this.handHeldScanner = bronze.getHandheldScanner();
			this.mainScanner = bronze.getMainScanner();
			this.banknoteValidator = bronze.getBanknoteValidator();
			this.coinValidator = bronze.getCoinValidator();
			this.cardReader = bronze.getCardReader();
			this.banknoteDispenser = bronze.getBanknoteOutput();
			this.coinTray = bronze.getCoinTray();
			this.printer = bronze.getPrinter();
			this.coinDispensers = bronze.getCoinDispensers();
		} else if (hardware instanceof SelfCheckoutStationSilver silver) {
			this.station = silver;
			this.baggingAreaScale = silver.getBaggingArea();
			this.scannerScale = silver.getScanningArea();
			this.handHeldScanner = silver.getHandheldScanner();
			this.mainScanner = silver.getMainScanner();
			this.banknoteValidator = silver.getBanknoteValidator();
			this.coinValidator = silver.getCoinValidator();
			this.cardReader = silver.getCardReader();
			this.banknoteDispenser = silver.getBanknoteOutput();
			this.coinTray = silver.getCoinTray();
			this.printer = silver.getPrinter();
			this.coinDispensers = silver.getCoinDispensers();
		} else if (hardware instanceof SelfCheckoutStationGold gold) {
			this.station = gold;
			this.baggingAreaScale = gold.getBaggingArea();
			this.scannerScale = gold.getScanningArea();
			this.handHeldScanner = gold.getHandheldScanner();
			this.mainScanner = gold.getMainScanner();
			this.banknoteValidator = gold.getBanknoteValidator();
			this.coinValidator = gold.getCoinValidator();
			this.cardReader = gold.getCardReader();
			this.banknoteDispenser = gold.getBanknoteOutput();
			this.coinTray = gold.getCoinTray();
			this.printer = gold.getPrinter();
			this.coinDispensers = gold.getCoinDispensers();
		} else {
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
		}

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

		//Initialize Product Lists and Weight Limit
		productsInOrder = new HashMap<>();
		barcodedProductsInOrder = new ArrayList<>();
		pluCodedProductsInOrder = new ArrayList<>();
		baggedProducts = new HashMap<>();
		allowableBagWeight = new Mass(200.0);   // default value of 200g
		this.membershipDatabase = new MembershipDatabase();
	    this.membershipValidator = new MembershipNumberValidator(this.membershipDatabase);
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
		handHeldScanner.disable();;
		mainScanner.disable();;
		banknoteValidator.disable();
		coinValidator.disable();;
		cardReader.disable();;
		banknoteDispenser.disable();
		coinTray.disable();
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
		coinTray.enable();
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
		setNeedsAttentionToTrue(); // To be implemented by Misc team
	}
}
