package com.thelocalmarketplace.software;

import com.jjjwelectronics.IDeviceListener;
import com.thelocalmarketplace.hardware.Product;

public interface AddItemListner extends IDeviceListener{
	
	public void ItemHasBeenAdded(Product product);
}
	


