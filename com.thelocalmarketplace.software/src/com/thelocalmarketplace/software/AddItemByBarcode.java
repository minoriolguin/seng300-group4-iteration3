//Elizabeth Szentmiklossy UCID: 30165216
//Justine Mangaliman UCID: 30164741
//Enzo Mutiso UCID: 30182555
//Abdelrahman Mohamed UCID: 30162037
//Mohammad Mustafa Mehtab UCID: 30189394

package com.thelocalmarketplace.software;

import com.jjjwelectronics.AbstractDevice;
import com.jjjwelectronics.IDevice;
import com.jjjwelectronics.IDeviceListener;
import com.jjjwelectronics.Mass;
import com.jjjwelectronics.OverloadedDevice;
import com.jjjwelectronics.scale.AbstractElectronicScale;
import com.jjjwelectronics.scanner.Barcode;
import com.jjjwelectronics.scanner.BarcodeScannerListener;
import com.jjjwelectronics.scanner.IBarcodeScanner;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.Product;

import java.util.ArrayList;
import java.util.Map;


/**
 * AddItemByBarcode class handles the addition of products to an order by scanning barcodes
 * and ensures that the expected weight matches the actual weight using a WeightDiscrepancy object.
 *
 * @author Enzo Mutiso UCID: 30182555
 * @author Abdelrahman Mohamed UCID: 30162037
 * @author Elizabeth Szentmiklossy UCID: 30165216
 */
public final class AddItemByBarcode extends AbstractDevice<AddItemListner> implements BarcodeScannerListener, WeightDiscrepancyListner {

    /**
     * The order where products will be added.
     */
    private ArrayList<Product> order;
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
    
    private IBarcodeScanner barcodeScanner;
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

    public AddItemByBarcode(IBarcodeScanner barcodeScanner, ArrayList<Product> order, WeightDiscrepancy discrepancy, ActionBlocker blocker, AbstractElectronicScale scale, Map<Barcode, BarcodedProduct> database) {
        this.order = order;
        this.actionBlocker = blocker;
        this.scale = scale;
        this.database = database;
        this.discrepancy = discrepancy;
        this.barcodeScanner = barcodeScanner;
        discrepancy.register(this);
        
    }
    
    public void ItemHasBeenAdded(Product product){
    	for(AddItemListner l : listeners()){
			l.ItemHasBeenAdded(product);
			}
    }

    /**
     * Adds a product to the order by scanning its barcode.
     *
     * @param barcodeScanner The barcode scanner.
     * @param barcode        The scanned barcode.
     */
    public void aBarcodeHasBeenScanned(IBarcodeScanner barcodeScanner, Barcode barcode) {
        // Check if state is satisfying the precondition: The system is ready to accept customer input.

     
            // Add gui to block customer interaction
        	
            actionBlocker.blockInteraction();
            System.out.println("Checking barcode...");
            try {
            Product product = getProductByBarcode(barcode, database);

            addBarcodedProductToOrder(product, order, barcodeScanner);
            ItemHasBeenAdded(product);
        }
            catch (ProductNotFoundException e){
            }
            
            // implement GUI saying to add to bagging area
            
            System.out.println("Item added.\nPlease add item to bagging area.\nWaiting...");
            //  Compare actual vs expected weights to check for any discrepancies (also checks if item is in bagging area)
              
        }
    
    
	@Override
	public void WeightDiscrancyOccurs() {
		barcodeScanner.disable();
	}

	@Override
	public void WeightDiscrancyResolved() {
		barcodeScanner.enable();
		
	}


    /**
     * Exception class for handling weight discrepancy errors during barcode scanning.
     */
    public static class WeightDiscrepancyException extends RuntimeException {
        /**
         * @param message The message to be displayed when the exception is thrown.
         */
        public WeightDiscrepancyException(String message) {
            super(message);
        }
    }

    /**
     * Retrieves product information by its barcode.
     *
     * @param scannedBarcode The scanned barcode.
     * @param database       The database of products.
     * @return The BarcodedProduct associated with the barcode.
     * @throws ProductNotFoundException If the product is not found with the specified barcode.
     */
    private BarcodedProduct getProductByBarcode(Barcode scannedBarcode, Map<Barcode, BarcodedProduct> database) throws ProductNotFoundException {
        if (database.containsKey(scannedBarcode)) {
            return database.get(scannedBarcode);
        } else {
            throw new ProductNotFoundException("Product not found with specified barcode.");
        }
    }

    /**
     * Retrieves the current order.
     *
     * @return The list of products in the current order.
     */
    public ArrayList<Product> getOrder( ) {
        return order;
    }



    /**
     * Adds a barcoded product to the order and updates the expected weight.
     *
     * @param product       The barcoded product to add to the order.
     * @param order         The order where products will be added.
     * @param barcodeScanner The barcode scanner.
     */
    private void addBarcodedProductToOrder(Product product, ArrayList<Product> order, IBarcodeScanner barcodeScanner) {
        order.add(product);

        Mass weightOfProduct = new Mass(((BarcodedProduct) product).getExpectedWeight());
        discrepancy.expectedWeight = discrepancy.expectedWeight.sum(weightOfProduct);

        if(discrepancy.CompareWeight()) {
            barcodeScanner.enable();
        } else {
            barcodeScanner.disable();
        }

    }
    public Mass getExpectedWeight() {
    	Mass expectedweight = discrepancy.expectedWeight;
		return expectedweight;
    	
    }

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


}
