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


package com.thelocalmarketplace.software;

import com.jjjwelectronics.AbstractDevice;
import com.jjjwelectronics.IDevice;
import com.jjjwelectronics.IDeviceListener;
import com.jjjwelectronics.Item;
import com.jjjwelectronics.Mass;
import com.jjjwelectronics.OverloadedDevice;
import com.jjjwelectronics.scale.AbstractElectronicScale;
import com.jjjwelectronics.scanner.Barcode;
import com.jjjwelectronics.scanner.BarcodeScannerListener;
import com.jjjwelectronics.scanner.BarcodedItem;
import com.jjjwelectronics.scanner.IBarcodeScanner;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.Product;
import com.thelocalmarketplace.hardware.external.ProductDatabases;
import com.thelocalmarketplace.software.exceptions.OrderException;

import java.util.Map;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * The ItemController class handles the addition of products to an order by scanning barcodes
 * and ensures that the expected weight matches the actual weight using a WeightDiscrepancy object.
 *
 * @author Enzo Mutiso UCID: 30182555
 * @author Abdelrahman Mohamed UCID: 30162037
 * @author Elizabeth Szentmiklossy UCID: 30165216
 */
public final class ItemController extends AbstractDevice<ItemControllerListener> implements BarcodeScannerListener, WeightDiscrepancyListner {

    /**
     * The order where products will be added.
     */
//    private Map<Barcode, BarcodedProduct> order;
	private HashMap<Item, Integer> order;

    /**
     * Variable to keep track of total cost of the order
     */
    private double totalPrice;
    
    /**
     * The WeightDiscrepancy object for weight comparison.
     */
    private WeightDiscrepancy discrepancy;
    /**
     * The ActionBlocker object to block customer interaction.
     */
    private ActionBlocker actionBlocker;
    /**
     * The ElectronicScale object to get the actual weight.
     */
    private AbstractElectronicScale scale;
    /**
     * The database of products.
     */
    
    private Map<Barcode, BarcodedProduct> database;
    /**
     * Main barcode scanner
     */
    private IBarcodeScanner scanner;
    
    /**
     * Variable to check whether aBarcodeHasBeenScanned
     */
    private boolean beenCalled;
    /**
     * Constructs an AddItemByBarcode object with the expected weight, order, WeightDiscrepancy object, ActionBlocker object, ElectronicScale object, and database.
     *
     * @param expectedWeight The expected weight to match with the actual weight.
     * @param order          The order where products will be added.
     * @param discrepancy    The WeightDiscrepancy object for weight comparison.
     * @param blocker        The ActionBlocker object to block customer interaction.
     * @param scale          The ElectronicScale object to get the actual weight.
     * @param database       The database of products.
     */
    
    public ItemController(HashMap<Item, Integer> order, WeightDiscrepancy discrepancy, ActionBlocker blocker)
    {
    	this.order = order;
    	this.actionBlocker = blocker;
    	this.discrepancy = discrepancy;
    }

    public ItemController(IBarcodeScanner scanner, HashMap<Item, Integer> order, WeightDiscrepancy discrepancy, ActionBlocker blocker, AbstractElectronicScale scale) {
        this.order = order;
        this.actionBlocker = blocker;
        this.scale = scale;
        this.discrepancy = discrepancy;
        this.scanner = scanner;
        this.database = ProductDatabases.BARCODED_PRODUCT_DATABASE;
        this.totalPrice = 0.0;
        discrepancy.register(this);
        this.scanner.register(this);
    }

    
    public void ItemHasBeenAdded(Item item){
    	for(ItemControllerListener l : listeners()){
			l.ItemHasBeenAdded(item);
			}
    }

	/**
	 * Notify listeners that an item has been removed from the order
	 * 
	 * @param item - the item that has been removed from the order
	 */
	public void itemHasBeenRemoved(Item item, int amount)
	{
		for(ItemControllerListener l : listeners())
		{
			l.ItemHasBeenRemoved(item, amount);
		}
	}

    public void scanBarcode(BarcodedItem item) {
    	while (!beenCalled) {
    		this.scanner.enable();
    		this.scanner.scan(item);
    	}
    	beenCalled = false;
    }

    /**
     * Adds a product to the order by scanning its barcode.
     *
     * @param barcodeScanner The barcode scanner.
     * @param barcode        The scanned barcode.
     */
    @Override
    public void aBarcodeHasBeenScanned(IBarcodeScanner barcodeScanner, Barcode barcode) {
        // Check if state is satisfying the precondition: The system is ready to accept customer input.
    			// Add gui to block customer interaction
                actionBlocker.blockInteraction();
                System.out.println("Checking barcode...");
                try {
                	BarcodedProduct product = getProductByBarcode(barcode);
                	totalPrice = totalPrice + product.getPrice();

                	BarcodedItem item = new BarcodedItem(barcode, new Mass(product.getExpectedWeight()));
                	addBarcodedItemToOrder(item, order, barcodeScanner);
                	ItemHasBeenAdded(item);
                	
                }
                catch (ProductNotFoundException e){
                }
                
                beenCalled = true;
                System.out.println("Item added.\nPlease add item to bagging area.\nWaiting...");
                //  Compare actual vs expected weights to check for any discrepancies (also checks if item is in bagging area)
                actionBlocker.unblockInteraction();
        }
    
    
	@Override
	public void WeightDiscrancyOccurs() {
		actionBlocker.blockInteraction();
		scanner.disable();
	}

	@Override
	public void WeightDiscrancyResolved() {
		actionBlocker.unblockInteraction();  
		scanner.enable();
	}



    /**
     * Retrieves product information by its barcode.
     *
     * @param scannedBarcode The scanned barcode.
     * @param database       The database of products.
     * @return The BarcodedProduct associated with the barcode.
     * @throws ProductNotFoundException If the product is not found with the specified barcode.
     */
    private BarcodedProduct getProductByBarcode(Barcode scannedBarcode) throws ProductNotFoundException {
        if (database.containsKey(scannedBarcode)) {
            return  database.get(scannedBarcode);
        } else {
            throw new ProductNotFoundException("Product not found with specified barcode.");
        }
    }

    /**
     * Retrieves the current order.
     *
     * @return The list of products in the current order.
     */
    public HashMap<Item, Integer> getOrder( ) {
        return order;
    }
    
    /**
     * Returns the total amount of items that are in the order
     * 
     * @param order - the order to check
     * @return - the total amount of items that are in the order
     */
    public int getTotalAmountOfItemsFromOrder(HashMap<Item, Integer> order)
    {
    	if(order == null)
    	{
    		throw new NullPointerException();
    	}
    	
    	int sum = 0;
    	for(int amount : order.values())
    	{
    		sum += amount;
    	}

    	return sum;
    }
    
    /**
     * Returns the amount of 1 particular item in the order
     * if the item is not in the order, returns 0
     * 
     * @param item - item to check the amount of in the order
     * @param order - order to check from
     * @return - the number of a particular item. If the item is not in the order 0
     */
    public int getAmountOfItemInOrder(Item item, HashMap<Item, Integer> order)
    {
    	if(order == null || item == null)
    	{
    		throw new NullPointerException();
    	}
    	
    	//if the item is no in the order, return 0
    	if(!order.containsKey(item))
    	{
    		return 0;
    	}
    	
    	return order.get(item);
    }

    
    /**
     * Method that returns whether the scanner can scan
     */
    public boolean readyToScan() {
    	return actionBlocker.isInteractionBlocked() && discrepancy.CompareWeight();
    }

    /**
     * Adds a barcoded product to the order and updates the expected weight.
     *
     * @param product       The barcoded product to add to the order.
     * @param order         The order where products will be added.
     * @param barcodeScanner The barcode scanner.
     */
    private void addBarcodedItemToOrder(BarcodedItem item, HashMap<Item, Integer> order, IBarcodeScanner barcodeScanner) {

    	order.put(item, 1);
        if(discrepancy.CompareWeight()) {
            barcodeScanner.enable();
        } else {
            barcodeScanner.disable();
        }

    }
    public Mass getExpectedWeight() {
		return discrepancy.expectedWeight;
    	
    }
    
    public double getTotalPrice() {
    	return this.totalPrice;
    }

	/**
	 * Removes an item from an order 
	 * 
	 * @param item - the item to remove from the order
	 * @param amount - the number of item to remove from cart
	 * @param order - the order that the item is to be removed from
	 */
	public void removeItemFromOrder(Item item, int amount, HashMap<Item, Integer> order)
	{
		if(item == null || order == null)
		{
			throw new NullPointerException();
		}
	
		if(order.size() <= 0 || order.get(item) == null)
		{
			throw new OrderException();
		}
		
		//block customer interaction
		actionBlocker.blockInteraction();
		
		
		int itemAmount = order.get(item);
		int amountAfterRemoving = itemAmount - amount;
		int actualRemovalAmount = 0;
		

		//if it's the only item in the cart, remove all instances of that item (case of equal to 1)
		//if it's 0 or less, then the amount to remove exceeded the amount in the cart, so we can still remove
		//that item from the cart
		if(amountAfterRemoving <= 0)
		{
			order.remove(item);
			actualRemovalAmount = itemAmount;
		}
		//if there are more than one of that item in the cart than we're removing, decrement the amount by that much
		else
		{
			order.put(item, amountAfterRemoving);
			actualRemovalAmount = amount;
		}
		
		BigDecimal removalWeightBD = item.getMass().inGrams().multiply(new BigDecimal(actualRemovalAmount));
		discrepancy.expectedWeight = new Mass(discrepancy.expectedWeight.inGrams().subtract(removalWeightBD));

		itemHasBeenRemoved(item, actualRemovalAmount);

		actionBlocker.unblockInteraction();
	}

	// Unused Methods
    @Override
    public void aDeviceHasBeenEnabled(IDevice<? extends IDeviceListener> device) {
    }

    @Override
    public void aDeviceHasBeenDisabled(IDevice<? extends IDeviceListener> device) {
    }

    @Override
    public void aDeviceHasBeenTurnedOn(IDevice<? extends IDeviceListener> device) {
    }

    @Override
    public void aDeviceHasBeenTurnedOff(IDevice<? extends IDeviceListener> device) {
    }

	@Override
	public void addOwnBagsSelected() {
	}

	@Override
	public void addOwnBagDeselected() {
	}
}
