

import com.thelocalmarketplace.hardware.BarcodedProduct;

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
    /**
     * Flag indicating whether to skip the bagging process for the next item.
     */
    public boolean skip;

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
    }
    
    /**
     * Initiates the payment process using coins.
     * Enables and activates the coin validator and enables the printer.
     */
    public void payByCoin () {
        software.coinValidator.enable();
        software.coinValidator.activate();
        software.printer.enable();
    }
    
    /**
     * Initiates the payment process using banknotes.
     * Enables and activates the banknote validator and enables the printer.
     */
    public void payByBanknote () {
        software.banknoteValidator.enable();
        software.banknoteValidator.activate();
        software.printer.enable();
    }
    
    /**
     * Initiates the payment process using a card swipe.
     * Enables the card reader and enables the printer.
     */
    public void payByCard () {
        software.cardReader.enable();
        software.printer.enable();
    }
    
    /**
     * Prints the receipt for the current order.
     */
    public void printReceipt() {
        software.printReceipt.print();
    }

    /**
     * Removes a selected barcoded product from the order.
     *
     * @param product The barcoded product to be removed.
     */
    public void removeSelectedBarcodedProduct(BarcodedProduct product){
        software.updateCart.removeItem(product);
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
    public void bagsAdded(){
        software.weightDiscrepancy.expectOwnBagsToBeAdded = false;
        software.mainScanner.enable();
        software.handHeldScanner.enable();
    }
    
    /**
     * Displays a prompt for adding an item.
     */
    public void displayAddItem(){
        // Implementation to be done in Iteration 3
    }
    
    /**
     * Displays a prompt for removing an item.
     */
    public void displayRemoveItem(){
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
    
    
    //Listeners Below 
    
    @Override
    public void RemoveItemFromScale() {
        displayRemoveItem();
    }

    @Override
    public void AddItemToScale() {
        displayAddItem();
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
