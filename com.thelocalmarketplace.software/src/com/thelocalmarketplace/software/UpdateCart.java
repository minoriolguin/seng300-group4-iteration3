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

import ca.ucalgary.seng300.simulation.NullPointerSimulationException;

import java.math.BigDecimal;
import java.util.ArrayList;

/* The UpdateCart class handles the addition and removal of items from the self-checkout cart.
* It also manages bulky items, blocking and unblocking customer interactions, and updates the expected weight and total cost.
*/
public class UpdateCart implements BarcodeScannerListener, ElectronicScaleListener {
    public WeightDiscrepancy weightDiscrepancy;
    public Software software;
    public Mass currentMassOnScanner;
    private MembershipScanner membershipScanner;

    /**
     * Constructs an UpdateCart instance.
     *
     * @param software The Software instance associated with the UpdateCart.
     */
    public UpdateCart(Software software) {
        this.software = software;
        this.weightDiscrepancy = software.weightDiscrepancy;
        this.membershipScanner = new MembershipScanner(software);
        this.currentMassOnScanner = Mass.ZERO;

        // register both scanners, Both are automatically dealt with
        software.handHeldScanner.register(this);
        software.mainScanner.register(this);
        software.scannerScale.register(this);
    }

    /**
     * Add a generalized product type
     *
     * @param product - generalized product to add
     */
    public void addProduct(Product product)
    {
        if(product instanceof BarcodedProduct)
        {
            this.addScannedProduct(((BarcodedProduct) product).getBarcode());
        }
        else if(product instanceof PLUCodedProduct)
        {
            this.addPLUProduct((PLUCodedProduct) product);
        }
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
        if(software.getProductsInOrder().containsKey(product))
            software.getProductsInOrder().replace(product,software.getProductsInOrder().get(product).sum(currentMassOnScanner));
        else
            software.getProductsInOrder().put(product,currentMassOnScanner);
        software.getPluCodedProductsInOrder().add(product);
        //Dealing With Heavy Item
        if(software.touchScreen.skipBaggingItem()) {
            software.attendant.notifySkipBagging();
        }
        else {
            // System: Updates the expected weight from the bagging area.
            software.setExpectedTotalWeight(software.getExpectedTotalWeight().sum(currentMassOnScanner));
            // System: Signals to the Customer to place the scanned item in the bagging area.
            weightDiscrepancy.notifyAddItemToScale();
            // if item is less than sensitivity limit of scale it will not notify weightDiscrepancy
            // therefore customer won't get unblocked till attendant verifies item
            if (currentMassOnScanner.compareTo(software.baggingAreaScale.getSensitivityLimit()) < 0)
                software.attendant.verifyItemInBaggingArea();
            //6.When weight on scale changes to correct weight, weightDiscrepancy will unblock Customer
            //item added to bagged products
            if(software.getBaggedProducts().containsKey(product))
                software.getBaggedProducts().replace(product,software.getBaggedProducts().get(product).sum(currentMassOnScanner));
            else
                software.getBaggedProducts().put(product,currentMassOnScanner);
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
    public void addScannedProduct(Barcode barcode) {
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
            if(software.getBaggedProducts().containsKey(product))
                software.getBaggedProducts().replace(product,software.getBaggedProducts().get(product).sum(productsWeight));
            else
                software.getBaggedProducts().put(product,productsWeight);
        }
        //7.Update the orderTotal (not part of use case for some reason)
        software.getBarcodedProductsInOrder().add(product);
        Mass productsWeight = new Mass(product.getExpectedWeight());
        if(software.getProductsInOrder().containsKey(product))
            software.getProductsInOrder().replace(product,software.getProductsInOrder().get(product).sum(productsWeight));
        else
            software.getProductsInOrder().put(product,productsWeight);
        BigDecimal price = BigDecimal.valueOf(product.getPrice());
        software.addToOrderTotal(price);
    }
    
    /**
     * Removes an item from the cart.
     *
     * @param product The BarcodedProduct to be removed.
     */
    public void removeItem(Product product) {
        if(software.getProductsInOrder().containsKey(product)){
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
    }
    
    /**
     * Perform a text search for a product description
     * 
     * case insensitive
     * 
     * @param searchStr - the string/substring to search for a product's description with
     * @return - An array list containing all of the products with matching descriptions. This Array list
     * 			 will be empty if there are no matches
     */
    public ArrayList<Product> textSearch(String searchStr)
    {
    	if(searchStr == null)
    	{
    		throw new NullPointerSimulationException();
    	}

    	ArrayList<Product> productMatches = new ArrayList<>();
   
    	//iterate through barcoded products
    	for(BarcodedProduct product : ProductDatabases.BARCODED_PRODUCT_DATABASE.values())
    	{
			if(product != null && product.getDescription().contains(searchStr.toLowerCase()))
			{
				productMatches.add(product);
			}
    	}
    	
    	//iterate through PLUCoded products
    	for(PLUCodedProduct product : ProductDatabases.PLU_PRODUCT_DATABASE.values())
    	{
			if(product != null && product.getDescription().contains(searchStr.toLowerCase()))
			{
				productMatches.add(product);
			}
    	}
    	

		return productMatches;
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
    	 if (isMembershipBarcode(barcode)) {
             membershipScanner.handleMembershipBarcode(barcode);
         } else {
             addScannedProduct(barcode);
         }
    }
    	 private boolean isMembershipBarcode(Barcode barcode) {
        	 // Convert the barcode to its string representation
            String barcodeString = barcode.toString();
            // Check if the barcode string is exactly 8 digits long
            if (barcodeString.length() == 8) {
                // Further check if all characters are digits
                return barcodeString.matches("\\d{8}");
            }
            return false;
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
