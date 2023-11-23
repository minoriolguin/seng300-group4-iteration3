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

import com.jjjwelectronics.IDeviceListener;
import com.jjjwelectronics.Item;
import com.thelocalmarketplace.hardware.Product;

public interface ItemControllerListener extends IDeviceListener{
	
	public void ItemHasBeenAdded(Item item);
	public void ItemHasBeenRemoved(Item item, int amount);
}
	


