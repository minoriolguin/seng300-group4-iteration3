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

package com.thelocalmarketplace.software;

import com.jjjwelectronics.EmptyDevice;
import com.jjjwelectronics.card.Card;
import com.jjjwelectronics.card.InvalidPINException;
import com.jjjwelectronics.scanner.Barcode;
import com.tdc.CashOverloadException;
import com.tdc.DisabledException;
import com.tdc.NoCashAvailableException;
import com.tdc.coin.Coin;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.PLUCodedProduct;
import com.thelocalmarketplace.hardware.PriceLookUpCode;
import com.thelocalmarketplace.hardware.Product;
import com.thelocalmarketplace.hardware.external.ProductDatabases;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.List;

/**
 * The {@code TouchScreen} class represents the touch screen interface in a self-checkout system.
 * It provides methods to interact with the system, such as initiating different payment methods,
 * printing receipts, managing barcoded products, and handling weight discrepancies.
 * 
 * The touch screen acts as a listener for weight discrepancies and responds to various events
 * triggered by the self-checkout software.
 */
public class TouchScreen implements WeightDiscrepancyListener {

    /**
     * The self-checkout software instance associated with the touch screen.
     */
    private final Software software;

    private Calendar calendar;
    /**
     * Flag indicating whether to skip the bagging process for the next item.
     */
    public boolean skip;

    private Card CreditCard = new Card("credit", "234567", "John",
            "245", "1234", true, true);

    private Card DebitCard = new Card("debit", "4567890", "Jane",
            "908", "1234", true, true);
    private Currency CAD;

    private Boolean cardsRegistered = false;

    /**
     * Constructs a new {@code TouchScreen} instance associated with the provided self-checkout software.
     * Registers the touch screen as a listener for weight discrepancies.
     *
     * @param software The self-checkout software instance.
     */
    public TouchScreen(Software software){
        software.weightDiscrepancy.register(this);
        this.software = software;
        skip = false;
        calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR,2);

    }

    /**
     * GUI displays message No items in Cart
     */
    public void displayNoItemsInCart(){

    }
    
    
    private List<BigDecimal> coins = new ArrayList<>();

    /** 
     * Stores each coin that the customer adds to this list
     * @param List of coin denominations.
     */
    public void addCoinToList (BigDecimal denomination) {
        if (!software.getProductsInOrder().isEmpty()){
        	coins.add(denomination);
        }
        else
            displayNoItemsInCart();
    }
    
    /**
     * Should Display this option during session
     * Initiates the payment process using coins.
     * Enables and activates the coin validator and enables the printer.
     */
    
    public void payByCoin() {
    	for (BigDecimal coin : coins) {
    		Coin coinType = new Coin(coin);
    		software.coinValidator.enable();
    		software.coinValidator.activate();
    		software.printer.enable();
    		
    		try {
				software.getCoinSlot().receive(coinType);
			} catch (DisabledException e) {
				System.out.println("Component is disabled " + e);
			} catch (CashOverloadException e) {
				System.out.println("Component is overfilled with cash " + e);
			}
    	}
    }
    
    /**
     * Initiates the payment process using banknotes.
     * Enables and activates the banknote validator and enables the printer.
     */
    private void payByBanknote () {
        CAD = Currency.getInstance("CAD");
        Coin.DEFAULT_CURRENCY = CAD;
        if (!software.getProductsInOrder().isEmpty()){
            software.banknoteValidator.enable();
            software.banknoteValidator.activate();
            software.printer.enable();
        }
        else
            displayNoItemsInCart();
    }
    //TODO: finish implementing banknote and coin payment
    public void insertBanknote(BigDecimal denomination) throws NoCashAvailableException {
        payByBanknote();
        software.payByBanknote.pay(CAD,denomination);

    }
    
    /**
     * Initiates the payment process using a card swipe.
     * Enables the card reader and enables the printer.
     */
    private void payByCard () {
        if (!cardsRegistered) {
            software.payByCard.addCardData("credit", "234567", "John", calendar, "245", 1000);
            software.payByCard.addCardData("debit", "4567890", "Jane", calendar, "908", 1000);
            cardsRegistered = true;
        }
        if (!software.getProductsInOrder().isEmpty()) {
            software.cardReader.enable();
            software.printer.enable();
        }
        else
            displayNoItemsInCart();
    }

    public void payViaSwipe(String type) throws IOException {
        payByCard();
        if (type.equals("credit"))
            software.cardReader.swipe(CreditCard);
        else
            software.cardReader.swipe(DebitCard);
    }

    public void payViaTap(String type) throws IOException {
        payByCard();
        if (type.equals("credit"))
            software.cardReader.tap(CreditCard);
        else
            software.cardReader.tap(DebitCard);
    }

    public void payViaInsert(String type, String pin) throws IOException{
        payByCard();
        if (type.equals("credit"))
            try {
                software.cardReader.insert(CreditCard, pin);
            }
            catch (Exception e)
            {
                software.cardReader.remove();
            }
        else
            try {
                software.cardReader.insert(DebitCard, pin);
            }
            catch (Exception e)
            {
                software.cardReader.remove();
            }
    }


    /**
     * Displays message when product not found in DataBase
     */
    public void displayProductNotInDataBase(){
    }

    /**
     * @param code of Product enter on touchscreen sends product to updated cart
     * think about making this class ScannerScale listener so when item is on
     * scale the "key in PLU code buttons are displayed"
     */
    public void selectAddPLUProduct(PriceLookUpCode code){
        if (ProductDatabases.PLU_PRODUCT_DATABASE.containsKey(code)) {
            PLUCodedProduct product = ProductDatabases.PLU_PRODUCT_DATABASE.get(code);
            software.updateCart.addProduct(product);
        }
        else
            displayProductNotInDataBase();

    }

    /**
     * @param barcode entered on GUI
     *            adds barcoded product to order
     */
    public void manuallyEnterBarcode(Barcode barcode){
        if (ProductDatabases.BARCODED_PRODUCT_DATABASE.containsKey(barcode)){
            BarcodedProduct product = ProductDatabases.BARCODED_PRODUCT_DATABASE.get(barcode);
            software.updateCart.addProduct(product);
        }
        else
            displayProductNotInDataBase();
    }

    /**
     * Prints the receipt for the current order. Is this auto called when payment
     * happens?  Does products in order get cleared preventing this from printing
     * current order?
     */
    public void printReceipt() {
        software.printReceipt.print();
    }

    /**
     * Removes a selected product from the order.
     *
     * @param product The product to be removed.
     */
    public void removeProduct(Product product){
        software.updateCart.removeItem(product);
    }


    /**
     * This is to pop up after every barcode is scanned or plu product is added
     * one button to call another to move back to order or displaying additem to
     * bagging area
     */
    public void SelectSkipBaggingItem(){
        skip = true;
    }
    
    /**
     * Returns whether the bagging process for the next item should be skipped.
     *
     * @return {@code true} if bagging should be skipped, {@code false} otherwise.
     */
    public boolean skipBaggingItem(){
        return skip;
    }
 
    /**
     * Initiates the process to add the customer's own bags.
     * Disables scanners and expects the addition of own bags.
     */
    public void selectAddOwnBags(){
        software.mainScanner.disable();
        software.handHeldScanner.disable();
        software.weightDiscrepancy.expectOwnBagsToBeAdded = true;
    }
    
    /**
     * Displays a prompt for the customer to add their own bags.
     */
    private void displayAddOwnBags(){
        // Implementation to be done in Iteration 3
    }
    
    /**
     * Signals that the bags have been added, enabling scanners.
     */
    public void selectBagsAdded(){
        software.weightDiscrepancy.expectOwnBagsToBeAdded = false;
        software.mainScanner.enable();
        software.handHeldScanner.enable();
    }


    /**
     * @param amount number of bags they want
     * @throws EmptyDevice if no bag in dispenser
     *
     */
    public void purchaseBags(int amount) throws EmptyDevice {
        software.purchaseBags.AddBagToOrder(amount);
    }

    /**
     * Made with assumption that GUI will be presenting Database of items
     * Item on GUI clicked and calls this method passing in a Product param
     */
    public void visualProductClicked(Product itemClicked) {
        software.updateCart.addProduct(itemClicked);
    }
    /**
     * Displays a prompt for adding an item to bagging area.
     */
    public void displayAddItemToBaggingArea(){
        // Implementation to be done in Iteration 3
    }
    
    /**
     * Displays a prompt for removing an item.
     */
    public void displayRemoveItemFromBaggingArea(){
        // Implementation to be done in Iteration 3
    }
    
    /**
     * Displays a prompt indicating too much weight in the bagging area.
     */
    public void displayToMuchWeightInBaggingArea(){
        // Implementation to be done in Iteration 3
    }
    
    /**
     * Displays the normal screen without any prompts.
     */
    public void displayNormal(){
        // Implementation to be done in Iteration 3
    }
    
    /**
     * Initiates the process of a customer asking for an attendant's help.
     **/
    public void signalForAttendant() {
    	software.setNeedsAttentionToTrue();
    	software.notifyAttendant();
    }
    
    /**
     * get software associated with touchscreen, need for gui
     */
    public Software getSoftware() {
    	return this.software;
    }
    //Listeners Below 
    
    @Override
    public void RemoveItemFromScale() {
        software.blockCustomer();
        displayRemoveItemFromBaggingArea();
    }

    @Override
    public void AddItemToScale() {
        software.blockCustomer();
        displayAddItemToBaggingArea();
    }

    @Override
    public void weightOverLimit() {
        displayToMuchWeightInBaggingArea();
    }

    @Override
    public void noDiscrepancy() {
        displayNormal();
    }

    @Override
    public void bagsTooHeavy() {
    }
    
}
