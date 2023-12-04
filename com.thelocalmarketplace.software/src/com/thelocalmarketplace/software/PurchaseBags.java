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

import com.jjjwelectronics.*;
import com.jjjwelectronics.bag.IReusableBagDispenser;
import com.jjjwelectronics.bag.ReusableBag;
import com.jjjwelectronics.bag.ReusableBagDispenserListener;
import com.jjjwelectronics.scanner.Barcode;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.external.ProductDatabases;
/**
 * Represents the functionality to handle the purchase and dispensing of reusable bags.
 * This class implements the ReusableBagDispenserListener interface to listen for dispenser events.
 * The bags are associated with a barcode and added to the product database.
 * 
 * @author Elizabeth Szentmiklossy
 * @CoAuthor Shawn Hanlon
 * 
 *This documentation includes contributions from the following authors:
 *@author Elizabeth Szentmiklossy
 */
public class PurchaseBags implements ReusableBagDispenserListener{
	private final Software software;
	private final IReusableBagDispenser reusableBagDispenser;
	private Boolean OutOfBags;
	private final Barcode reuseableBagBarcode;
	private final ReusableBag bag;

	 /**
     * Constructor for PurchaseBags.
     *
     * @param software The software instance associated with the purchase of bags.
     */
	public PurchaseBags(Software software){
		this.software = software;
		this.reusableBagDispenser = software.reusableBagDispenser;
		software.reusableBagDispenser.register(this);

		// define a reusable Bag Barcode, Price and put it in the database
		bag = new ReusableBag();
		Numeral[] reuseableBagNumeral = new Numeral[3];
		reuseableBagNumeral[0] = Numeral.nine;
		reuseableBagNumeral[1] = Numeral.five;
		reuseableBagNumeral[2] = Numeral.three;
		reuseableBagBarcode = new Barcode(reuseableBagNumeral);
		double expectedWeight = bag.getMass().inGrams().doubleValue();
		String description = "Reusable Bag";
		long price = 2;
		BarcodedProduct reusableBag = new BarcodedProduct(reuseableBagBarcode, description, price, expectedWeight);
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(reuseableBagBarcode,reusableBag);

		OutOfBags = false;
	}
	/**
     * Adds a specified number of bags to the order and updates the cart.
     *
     * @param amount The number of bags to add to the order.
     * @throws EmptyDevice Thrown if the dispenser is out of bags.
     */
	public void AddBagToOrder(int amount) throws EmptyDevice {
		for (int i = amount; i>0; i--) {
			if (!OutOfBags) {
				reusableBagDispenser.dispense();
				software.updateCart.addScannedProduct(reuseableBagBarcode);
			}
		}
		if(OutOfBags)
			// Notifies attendant
			software.attendant.reusableBagsEmpty();
	}
	
	   /**
     * Adds a specified number of bags to the dispenser.
     *
     * @param amount The number of bags to add to the dispenser.
     * @throws OverloadedDevice Thrown if the dispenser is overloaded.
     */
	public void addBagsToDispenser(int amount) throws OverloadedDevice {
		for(int i = amount; i>0; i--)
			reusableBagDispenser.load(bag);
		OutOfBags = false;
	}
	
	// ReusableBagDispenserListener methods
	@Override
	public void theDispenserIsOutOfBags() {
		OutOfBags = true; 
	}

	//Unused methods
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
	public void aBagHasBeenDispensedByTheDispenser() {
	}
	@Override
	public void bagsHaveBeenLoadedIntoTheDispenser(int count) {
	}


}

