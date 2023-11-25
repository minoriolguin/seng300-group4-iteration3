

import com.jjjwelectronics.IDevice;
import com.jjjwelectronics.IDeviceListener;
import com.jjjwelectronics.Mass;
import com.jjjwelectronics.scanner.Barcode;
import com.jjjwelectronics.scanner.BarcodeScannerListener;
import com.jjjwelectronics.scanner.IBarcodeScanner;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.external.ProductDatabases;
import java.math.BigDecimal;

/* The UpdateCart class handles the addition and removal of items from the self-checkout cart.
* It also manages bulky items, blocking and unblocking customer interactions, and updates the expected weight and total cost.
*/
public class UpdateCart implements BarcodeScannerListener {
    WeightDiscrepancy weightDiscrepancy;
    Software software;

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
            software.addBaggedProduct(product);
        }
        //7.Update the orderTotal (not part of use case for some reason)
        software.addBarcodedProduct(product);
        BigDecimal price = BigDecimal.valueOf(product.getPrice());
        software.addToOrderTotal(price);
    }
    
    /**
     * Removes an item from the cart.
     *
     * @param product The BarcodedProduct to be removed.
     */
    public void removeItem(BarcodedProduct product){
        software.blockCustomer();
        BigDecimal price = BigDecimal.valueOf(product.getPrice());
        software.subtractFromOrderTotal(price);
        software.removeBarcodedProduct(product);
        software.weightDiscrepancy.notifyRemoveItemFromScale();
        // when item is removed isWeightDiscrepancy auto called and if
        // weight corrected station enabled
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


}

