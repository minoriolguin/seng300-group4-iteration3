package com.thelocalmarketplace.software;

import com.jjjwelectronics.*;
import com.jjjwelectronics.bag.IReusableBagDispenser;
import com.jjjwelectronics.bag.ReusableBag;
import com.jjjwelectronics.bag.ReusableBagDispenserListener;
import com.jjjwelectronics.scanner.Barcode;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.external.ProductDatabases;

public class PurchaseBags implements ReusableBagDispenserListener{
	private Software software;
	private IReusableBagDispenser reusableBagDispenser;
	private Boolean addbag;
	private Boolean OutOfBags;
	private final Barcode reuseableBagBarcode;
	private final ReusableBag bag;

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
	// // maybe try catch?
	public void AddBagToOrder(int amount) throws EmptyDevice {
		for (int i = amount; i>0; i--) {
			if (!OutOfBags) {
				software.updateCart.addScannedItem(reuseableBagBarcode);
				reusableBagDispenser.dispense();
			}
		}
			if(OutOfBags)
				//notifies attendant
				software.attendant.reusableBagsEmpty();
	}
	// maybe try catch?
	public void addBagsToDispenser(int amount) throws OverloadedDevice {
		for(int i = amount; i>0; i--)
			reusableBagDispenser.load(bag);
		OutOfBags = false;
	}

	@Override
	public void theDispenserIsOutOfBags() {
		OutOfBags = true; 
	}

	@Override
	public void aDeviceHasBeenEnabled(IDevice<? extends IDeviceListener> device) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void aDeviceHasBeenDisabled(IDevice<? extends IDeviceListener> device) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void aDeviceHasBeenTurnedOn(IDevice<? extends IDeviceListener> device) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void aDeviceHasBeenTurnedOff(IDevice<? extends IDeviceListener> device) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void aBagHasBeenDispensedByTheDispenser() {
		// TODO Auto-generated method stub
	}
		
	@Override
	public void bagsHaveBeenLoadedIntoTheDispenser(int count) {
		// TODO Auto-generated method stub
		
	}


}

