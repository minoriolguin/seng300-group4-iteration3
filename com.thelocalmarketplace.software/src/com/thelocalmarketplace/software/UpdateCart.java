package com.thelocalmarketplace.software;

import com.jjjwelectronics.IDevice;
import com.jjjwelectronics.IDeviceListener;
import com.jjjwelectronics.Mass;
import com.jjjwelectronics.scale.ElectronicScaleListener;
import com.jjjwelectronics.scale.IElectronicScale;
import com.jjjwelectronics.scanner.Barcode;
import com.jjjwelectronics.scanner.BarcodeScannerListener;
import com.jjjwelectronics.scanner.IBarcodeScanner;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.PLUCodedProduct;
import com.thelocalmarketplace.hardware.Product;
import com.thelocalmarketplace.hardware.external.ProductDatabases;
import java.math.BigDecimal;

/* The UpdateCart class handles the addition and removal of items from the self-checkout cart.
* It also manages bulky items, blocking and unblocking customer interactions, and updates the expected weight and total cost.
*/
public class UpdateCart implements BarcodeScannerListener, ElectronicScaleListener {
    public WeightDiscrepancy weightDiscrepancy;
    public Software software;
    public Mass currentMassOnScanner;

    /**
     * Constructs an UpdateCart instance.
     *
     * @param software The Software instance associated with the UpdateCart.
     */
    public UpdateCart(Software software) {
        this.software = software;
        this.weightDiscrepancy = software.weightDiscrepancy;

        // register both scanners, Both are automatically dealt with
        software.handHeldScanner.register(this);
        software.mainScanner.register(this);
        software.scannerScale.register(this);
    }
    /**
     * Adds a PLU product to the cart
     *
     * @param product, An object of PLUProduct contains PLUCode, description and price
     */
    public void addPLUProduct(PLUCodedProduct product){
    	//System: Blocks the self-checkout station from further customer interaction
    	software.blockCustomer();
    	// Add product to Hashmap, with detected weight on scale.
        software.getProductsInOrder().put(product,currentMassOnScanner);
        //Dealing With Heavy Item
        if(software.touchScreen.skipBaggingItem()) {
            software.attendant.notifySkipBagging();
        }
        else {
            // System: Updates the expected weight from the bagging area.
            Mass productsWeight = currentMassOnScanner;
            software.setExpectedTotalWeight(software.getExpectedTotalWeight().sum(productsWeight));
            // System: Signals to the Customer to place the scanned item in the bagging area.
            weightDiscrepancy.notifyAddItemToScale();
            // if item is less than sensitivity limit of scale it will not notify weightDiscrepancy
            // therefore customer won't get unblocked till attendant verifies item
            if (productsWeight.compareTo(software.baggingAreaScale.getSensitivityLimit()) < 0)
                software.attendant.verifyItemInBaggingArea();
            //6.When weight on scale changes to correct weight, weightDiscrepancy will unblock Customer
            //item added to bagged products
            software.addBaggedProduct(product, productsWeight);
        }
        //Converting Mass to grams than to kg in type long
        long tempPrice = ((currentMassOnScanner.inGrams().longValue())/1000) * product.getPrice();
        // Convert price to type BigDecimal
        BigDecimal price = BigDecimal.valueOf(tempPrice);
        // Adjust pricing of current user session
        software.addToOrderTotal(price);
        // Add weight of item to current total expected weight
        software.getExpectedTotalWeight().sum(currentMassOnScanner);
    }


    /**
     * Adds a scanned item to the cart.
     *
     * @param barcode The barcode of the scanned item.
     */
    public void addScannedItem(Barcode barcode) {
        //2. System: Blocks the self-checkout station from further customer interaction.
        software.blockCustomer();
        //3. System: Determines the characteristics (weight and cost) of the product associated with the
        //barcode.
        BarcodedProduct product = ProductDatabases.BARCODED_PRODUCT_DATABASE.get(barcode);
        //deals with heavy item
        if(software.touchScreen.skipBaggingItem()) {
            software.attendant.notifySkipBagging();
        }
        else {
            //4. System: Updates the expected weight from the bagging area.
            Mass productsWeight = new Mass(product.getExpectedWeight());
            software.setExpectedTotalWeight(software.getExpectedTotalWeight().sum(productsWeight));
            //5. System: Signals to the Customer to place the scanned item in the bagging area.
            weightDiscrepancy.notifyAddItemToScale();
            // if item is less than sensitivity limit of scale it will not notify weightDiscrepancy
            // therefore customer won't get unblocked till attendant verifies item
            if (productsWeight.compareTo(software.baggingAreaScale.getSensitivityLimit()) < 0)
                software.attendant.verifyItemInBaggingArea();
            //6.When weight on scale changes to correct weight, weightDiscrepancy will unblock Customer
            //item added to bagged products
            software.addBaggedProduct(product, productsWeight);
        }
        //7.Update the orderTotal (not part of use case for some reason)
        software.getBarcodedProductsInOrder().add(product);
        Mass productsWeight = new Mass(product.getExpectedWeight());
        software.getProductsInOrder().put(product, productsWeight);
        BigDecimal price = BigDecimal.valueOf(product.getPrice());
        software.addToOrderTotal(price);
    }
    
    /**
     * Removes an item from the cart.
     *
     * @param product The BarcodedProduct to be removed.
     */
    public void removeItem(Product product){
        software.blockCustomer();
        BigDecimal price;
        if (product.isPerUnit()) {
            price = BigDecimal.valueOf(product.getPrice());
            software.getBarcodedProductsInOrder().remove(product);
        }
        else {
            price = BigDecimal.valueOf(product.getPrice() * ((software.getProductsInOrder().get(product).inGrams().longValue()) / 1000));
            software.getPluCodedProductsInOrder().remove(product);
        }
        software.subtractFromOrderTotal(price);
        software.getProductsInOrder().remove(product);
        software.weightDiscrepancy.notifyRemoveItemFromScale();
        // when item is removed isWeightDiscrepancy auto called and if
        // weight corrected station enabled
        if (software.getBaggedProducts().containsKey(product)) {
            software.getBaggedProducts().remove(product);
            //resets the expected weight to zero plus weight of added bags
            software.setExpectedTotalWeight(software.weightDiscrepancy.massOfOwnBags);
            for (Product products : software.getBaggedProducts().keySet()) {
                Mass productsWeight = software.getProductsInOrder().get(products);
                software.setExpectedTotalWeight(software.getExpectedTotalWeight().sum(productsWeight));
            }
        }
        else
            software.attendant.verifyItemRemovedFromOrder();
    }

    /**
     * An event announcing that the indicated barcode has been successfully scanned.
     *
     * @param barcodeScanner The device on which the event occurred.
     * @param barcode        The barcode that was read by the scanner.
     */
    //1. System: Detects a barcode from the handheld scanner.
    @Override
    public void aBarcodeHasBeenScanned(IBarcodeScanner barcodeScanner, Barcode barcode) {
        addScannedItem(barcode);
    }
    
    // The following methods are inherited from the BarcodeScannerListener interface

    @Override
    public void aDeviceHasBeenEnabled(IDevice<? extends IDeviceListener> device) {}
    @Override
    public void aDeviceHasBeenDisabled(IDevice<? extends IDeviceListener> device) {}
    @Override
    public void aDeviceHasBeenTurnedOn(IDevice<? extends IDeviceListener> device) {}
    @Override
    public void aDeviceHasBeenTurnedOff(IDevice<? extends IDeviceListener> device) {}


    /**
     * Announces that the mass on the indicated scale has changed.
     *
     * @param scale The scale where the event occurred.
     * @param mass  The new mass.
     */
    @Override
    public void theMassOnTheScaleHasChanged(IElectronicScale scale, Mass mass) {
        currentMassOnScanner = mass;

    }

    /**
     * Announces that excessive mass has been placed on the indicated scale.
     *
     * @param scale The scale where the event occurred.
     */
    @Override
    public void theMassOnTheScaleHasExceededItsLimit(IElectronicScale scale) {

    }

    /**
     * Announces that the former excessive mass has been removed from the indicated
     * scale, and it is again able to measure mass.
     *
     * @param scale The scale where the event occurred.
     */
    @Override
    public void theMassOnTheScaleNoLongerExceedsItsLimit(IElectronicScale scale) {

    }
}
