package com.thelocalmarketplace.software;
import java.util.HashMap;
import java.util.Map;

import com.jjjwelectronics.EmptyDevice;
import com.jjjwelectronics.IDevice;
import com.jjjwelectronics.IDeviceListener;
import com.jjjwelectronics.bag.AbstractReusableBagDispenser;
import com.jjjwelectronics.bag.ReusableBagDispenserListener;
import com.thelocalmarketplace.hardware.PLUCodedProduct;
import com.thelocalmarketplace.hardware.PriceLookUpCode;
import com.thelocalmarketplace.hardware.external.ProductDatabases;

public class PurchaseBags implements WeightDiscrepancyListener, ReusableBagDispenserListener{
	UpdateCart cart;
	PriceLookUpCode code;
	AbstractReusableBagDispenser ReusableBagDispenser;
	Boolean addbag;
	Boolean OutOfBags;

	public PurchaseBags(WeightDiscrepancy weight, UpdateCart cart, PriceLookUpCode code, AbstractReusableBagDispenser ReusableBagDispenser){
		this.cart = cart; 
		this.code = code;
		this.ReusableBagDispenser = ReusableBagDispenser;
		ProductDatabases.PLU_PRODUCT_DATABASE.get(code);	
		addbag = false; 
		OutOfBags = false;
		weight.register(this);
		ReusableBagDispenser.register(this);
		
	}
	public void AddBagToOrder() throws EmptyDevice {
		if(OutOfBags == false) {
			cart.addPLUProduct(ProductDatabases.PLU_PRODUCT_DATABASE.get(code));
			addbag = true; 
			ReusableBagDispenser.dispense();
		}
		else {
			//doesn't specify what to do if out of bags

		}
	}
	@Override
	public void noDiscrepancy() {
		//GUI Should be implemented here
		if(addbag == true) {
			System.out.println("Item has been added");	
			addbag = false; 
		}
	}

	@Override
	public void theDispenserIsOutOfBags() {
		OutOfBags = true; 
	}
	@Override
	public void RemoveItemFromScale() {	
	}
	@Override
	public void AddItemToScale() {	
	}
	@Override
	public void weightOverLimit() {	
	}
	@Override
	public void bagsTooHeavy() {	
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

