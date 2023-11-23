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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.jjjwelectronics.Mass;
import com.jjjwelectronics.card.ICardReader;
import com.jjjwelectronics.printer.IReceiptPrinter;
import com.jjjwelectronics.scale.IElectronicScale;
import com.jjjwelectronics.scanner.*;
import com.tdc.banknote.BanknoteDispensationSlot;
import com.tdc.banknote.BanknoteValidator;
import com.tdc.coin.CoinValidator;
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
public class SelfCheckoutSoftware {
	private BigDecimal orderTotal = BigDecimal.ZERO;
	private Mass expectedTotalWeight;
	private boolean blocked = false;
	private final ArrayList<BarcodedProduct> barcodedProductsInOrder;
	private final ArrayList<BarcodedProduct> baggedProducts;

	public final IElectronicScale baggingAreaScale;
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
	public final PayBySwipe payByCard;
	public final UpdateCart updateCart;

	public Mass allowableBagWeight;
	public final BanknoteDispensationSlot banknoteDispenser;
	public final CoinTray coinTray;


	private AbstractSelfCheckoutStation station;

	public static SelfCheckoutSoftware getInstance(AbstractSelfCheckoutStation station) {
		return new SelfCheckoutSoftware(station);
	}

	/*Constructor for SelfCheckout
	 *  
	 */
	private SelfCheckoutSoftware(AbstractSelfCheckoutStation station) {
		if (station instanceof SelfCheckoutStationBronze bronze) {
			this.station = bronze;
			this.baggingAreaScale = bronze.baggingArea;
			this.handHeldScanner = bronze.handheldScanner;
			this.mainScanner = bronze.mainScanner;
			this.banknoteValidator = bronze.banknoteValidator;
			this.coinValidator = bronze.coinValidator;
			this.cardReader = bronze.cardReader;
			this.banknoteDispenser = bronze.banknoteOutput;
			this.coinTray = bronze.coinTray;
			this.printer = bronze.printer;
		} else if (station instanceof SelfCheckoutStationSilver silver) {
			this.station = silver;
			this.baggingAreaScale = silver.baggingArea;
			this.handHeldScanner = silver.handheldScanner;
			this.mainScanner = silver.mainScanner;
			this.banknoteValidator = silver.banknoteValidator;
			this.coinValidator = silver.coinValidator;
			this.cardReader = silver.cardReader;
			this.banknoteDispenser = silver.banknoteOutput;
			this.coinTray = silver.coinTray;
			this.printer = silver.printer;
		} else if (station instanceof SelfCheckoutStationGold gold) {
			this.station = gold;
			this.baggingAreaScale = gold.baggingArea;
			this.handHeldScanner = gold.handheldScanner;
			this.mainScanner = gold.mainScanner;
			this.banknoteValidator = gold.banknoteValidator;
			this.coinValidator = gold.coinValidator;
			this.cardReader = gold.cardReader;
			this.banknoteDispenser = gold.banknoteOutput;
			this.coinTray = gold.coinTray;
			this.printer = gold.printer;
		} else {
			this.baggingAreaScale = station.baggingArea;
			this.handHeldScanner = station.handheldScanner;
			this.mainScanner = station.mainScanner;
			this.banknoteValidator = station.banknoteValidator;
			this.coinValidator = station.coinValidator;
			this.cardReader = station.cardReader;
			this.banknoteDispenser = station.banknoteOutput;
			this.coinTray = station.coinTray;
			this.printer = station.printer;
		}

		expectedTotalWeight = Mass.ZERO;
		orderTotal = BigDecimal.ZERO;

		//Initialize Software Components
		weightDiscrepancy = new WeightDiscrepancy(this);
		touchScreen = new TouchScreen(this);
		attendant = new Attendant(this);
		updateCart = new UpdateCart(this);
		payByBanknote = new PayByBanknote(this);
		payByCard = new PayBySwipe(this);
		payByCoin = new PayByCoin(this);
		printReceipt = new PrintReceipt(this);

		//Initialize Product Lists and Weight Limit
		barcodedProductsInOrder = new ArrayList<>();
		baggedProducts = new ArrayList<>();
		allowableBagWeight = new Mass(200.0);		// 1000 mg in a g should be 200g
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
	 * Starts a new self-checkout session by enabling necessary hardware components.
	 * This method should be called at the beginning of each customer interaction session.
	 * It enables handheld and main scanners, as well as the bagging area scale.
	 */
	public void startSession() {
		endSession();
		handHeldScanner.enable();
		mainScanner.enable();
		baggingAreaScale.enable();
	}
	
	/**
	 * Ends the current self-checkout session, clearing the order data and resetting the expected total weight.
	 * This method should be called at the end of each customer interaction session.
	 */
	public void endSession() {
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

	/**
	 * Adds a barcoded product to the current order.
	 *
	 * @param product The barcoded product to be added to the order.
	 */
	public void addBarcodedProduct(BarcodedProduct product) {
		barcodedProductsInOrder.add(product);
	}

	/**
	 * Adds a barcoded product to the list of bagged products.
	 *
	 * @param product The barcoded product to be added to the bagged products list.
	 */
	public void addBaggedProduct(BarcodedProduct product) {
		baggedProducts.add(product);
	}
	
	/**
	 * Retrieves the list of bagged products.
	 *
	 * @return The list of bagged products.
	 */
	public ArrayList<BarcodedProduct> getBaggedProducts(){
		return baggedProducts;
	}

	/**
	 * Removes a barcoded product from the current order. If the product is also in the list of bagged products,
	 * it removes it from there as well and updates the expected total weight accordingly.
	 *
	 * @param product The barcoded product to be removed from the order.
	 */
	public void removeBarcodedProduct(BarcodedProduct product) {
		barcodedProductsInOrder.remove(product);
		if (baggedProducts.contains(product)) {
			baggedProducts.remove(product);
			//resets the expected weight to zero plus weight of added bags
			expectedTotalWeight = weightDiscrepancy.massOfOwnBags;
			for (BarcodedProduct barcodedProduct : baggedProducts) {
				Mass productsWeight = new Mass(barcodedProduct.getExpectedWeight());
				expectedTotalWeight = expectedTotalWeight.sum(productsWeight);
			}

		}
		else
			attendant.verifyItemRemovedFromOrder();
	}

	/**
	 * Retrieves the array of banknote denominations supported by the self-checkout station.
	 *
	 * @return The array of banknote denominations.
	 */
	public BigDecimal[] getBanknoteDenominations() {
		return station.banknoteDenominations;
	}

	/**
	 * Retrieves the list of coin denominations supported by the self-checkout station.
	 *
	 * @return The list of coin denominations.
	 */
	public List<BigDecimal> getCoinDenominations() {
		return station.coinDenominations;
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
}

