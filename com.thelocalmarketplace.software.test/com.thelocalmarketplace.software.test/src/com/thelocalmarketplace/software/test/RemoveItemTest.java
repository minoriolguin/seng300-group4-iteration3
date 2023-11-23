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

package com.thelocalmarketplace.software.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

import com.jjjwelectronics.Item;
import com.jjjwelectronics.scale.ElectronicScaleBronze;
import com.jjjwelectronics.scale.ElectronicScaleGold;
import com.jjjwelectronics.scale.ElectronicScaleSilver;
import com.jjjwelectronics.scanner.Barcode;
import com.jjjwelectronics.scanner.BarcodedItem;
import com.thelocalmarketplace.hardware.PLUCodedItem;
import com.thelocalmarketplace.hardware.PriceLookUpCode;
import com.thelocalmarketplace.software.ActionBlocker;
import com.thelocalmarketplace.software.ItemController;
import com.thelocalmarketplace.software.WeightDiscrepancy;
import com.thelocalmarketplace.software.exceptions.OrderException;
import com.jjjwelectronics.Mass;
import com.jjjwelectronics.Mass.MassDifference;
import com.jjjwelectronics.Numeral;
import com.jjjwelectronics.OverloadedDevice;

import powerutility.PowerGrid;

public class RemoveItemTest 
{
    ItemController tempItemControllerInstance;
	ElectronicScaleGold goldScale;
	ElectronicScaleSilver silverScale;
	ElectronicScaleBronze bronzeScale;
	
	PLUCodedItem orange;
//	PriceLookUpCode orangePLUCode;
	BarcodedItem shampoo;

    HashMap<Item, Integer> cart = new HashMap<Item, Integer>();
    
    int cartSize;
    
    
	HashMap<Item, Integer> order;
	WeightDiscrepancy discrepancy;
	ActionBlocker actionBlocker;
	int orderSize;
	int numOrangesInOrder;
	int numShampooInOrder;
	Mass massOfOrder;
	
	/*
	ElectronicScaleGold listner = new ElectronicScaleGold();
	WeightDiscrepancy discrepancy = new WeightDiscrepancy(Mass.ZERO, listner);
	CoinSlot coin_slot = new CoinSlot();
	ArrayList< java.util.Map.Entry < BigDecimal,AbstractCoinDispenser>> amount = new ArrayList < java.util.Map.Entry < BigDecimal,AbstractCoinDispenser>> ();
	PowerGrid grid = PowerGrid.instance();
	*/
	
	/**
	 * Setup for
	 * 	- different tiers of scales
	 * 	- items 
	 *  - order
	 *  - RemoveItem instance
	 */
	@Before
	public void setup()
	{
		//create scale objects and make sure they're functional
		this.goldScale = new ElectronicScaleGold();
		this.silverScale = new ElectronicScaleSilver();
		this.bronzeScale = new ElectronicScaleBronze();

		this.goldScale.plugIn(PowerGrid.instance());
		this.silverScale.plugIn(PowerGrid.instance());
		this.bronzeScale.plugIn(PowerGrid.instance());
		
		this.goldScale.turnOn();
		this.silverScale.turnOn();
		this.bronzeScale.turnOn();
		
		this.goldScale.enable();
		this.silverScale.enable();
		this.bronzeScale.enable();
		
		Mass orangeMass = new Mass(200000000);
		PriceLookUpCode orangePLUCode = new PriceLookUpCode("0123");
		this.orange = new PLUCodedItem(orangePLUCode, orangeMass);
		
		Numeral[] shampooBarcodeCode = {Numeral.one, Numeral.two, Numeral.one};
		Barcode shampooBarcode = new Barcode(shampooBarcodeCode);
		Mass shampooMass = new Mass(450000000);
		this.shampoo = new BarcodedItem(shampooBarcode, shampooMass);
		
		cart.put(this.orange, 1);
		cart.put(this.shampoo, 1);
		
		this.cartSize = cart.size();
		
		this.order = new HashMap<Item, Integer>();
		this.order.put(orange,2);
		this.order.put(shampoo,3);
		
		this.orderSize = 5;
		this.numOrangesInOrder = 2;
		this.numShampooInOrder = 3;
		
		this.massOfOrder = orange.getMass().sum(shampoo.getMass());

		//add 700 grams of weight to account for small weight difference factors
//		this.massOfOrder = this.massOfOrder.sum(new Mass(700000000));
		this.massOfOrder = this.massOfOrder.sum(new Mass(2100000000));
		
		this.actionBlocker = new ActionBlocker();
		this.discrepancy = new WeightDiscrepancy(this.massOfOrder, bronzeScale);
		this.tempItemControllerInstance = new ItemController(this.order, this.discrepancy, this.actionBlocker);
//		this.tempRemoveItemInstance = new RemoveItem(this.order, this.actionBlocker);
		this.tempItemControllerInstance.register(discrepancy);
		
		bronzeScale.addAnItem(orange);
		bronzeScale.addAnItem(shampoo);
		silverScale.addAnItem(orange);
		silverScale.addAnItem(shampoo);
		goldScale.addAnItem(orange);
		goldScale.addAnItem(shampoo);
	} 
	
	
	/**
	 * Test removing a PLU coded item from the order
	 */
	@Test
	public void TestRemovePLUCodedItem()
	{
		tempItemControllerInstance.removeItemFromOrder(orange, 1, order);

		assertEquals(this.orderSize - 1, tempItemControllerInstance.getTotalAmountOfItemsFromOrder(order));
		assertEquals(this.numOrangesInOrder - 1, tempItemControllerInstance.getAmountOfItemInOrder(orange, order));
	}
	
	/**
	 * Test removing a barcoded item from the order
	 */
	@Test
	public void TestRemoveBarcodedItem()
	{
		tempItemControllerInstance.removeItemFromOrder(shampoo, 1, order);

		assertEquals(this.orderSize - 1, tempItemControllerInstance.getTotalAmountOfItemsFromOrder(order));
		assertEquals(this.numShampooInOrder - 1, tempItemControllerInstance.getAmountOfItemInOrder(shampoo, order));

	}

	/**
	 * Test removing more than 1 item from the order
	 */
	@Test
	public void TestRemoveMultipleItems()
	{
		tempItemControllerInstance.removeItemFromOrder(shampoo, 2, order);
		assertEquals(this.orderSize - 2, tempItemControllerInstance.getTotalAmountOfItemsFromOrder(order));
		assertEquals(this.numShampooInOrder - 2, tempItemControllerInstance.getAmountOfItemInOrder(shampoo, order));
	}

	/**
	 * Test removing an item completely from the order
	 */
	@Test
	public void TestCompletelyRemoveItemFromOrder()
	{
		tempItemControllerInstance.removeItemFromOrder(shampoo, 3, order);
		assertEquals(this.orderSize - 3, tempItemControllerInstance.getTotalAmountOfItemsFromOrder(order));
		assertEquals(this.numShampooInOrder - 3, tempItemControllerInstance.getAmountOfItemInOrder(shampoo, order));
	}

	/**
	 * Test removing an amount that is greater than the number of items in an order
	 * expecting this to just remove all of the items from the order
	 */
	@Test
	public void TestRemoveMoreThanAnItemHasFromOrder()
	{
		tempItemControllerInstance.removeItemFromOrder(shampoo, 100, order);
		assertEquals(this.orderSize - 3, tempItemControllerInstance.getTotalAmountOfItemsFromOrder(order));
		assertEquals(this.numShampooInOrder - 3, tempItemControllerInstance.getAmountOfItemInOrder(shampoo, order));
	}
	
	
	/**
	 * Test removing an item that is not in the order
	 */
	@Test (expected = OrderException.class)
	public void TestRemoveItemNotInOrder()
	{
		Mass appleMass = new Mass(220000000);
		PriceLookUpCode applePLUCode = new PriceLookUpCode("0132");
		PLUCodedItem apple = new PLUCodedItem(applePLUCode, appleMass);

		tempItemControllerInstance.removeItemFromOrder(apple, 1, order);
		assertEquals(this.orderSize - 1, 0);
	}
	
	/**
	 * Test removing an item when the order is empty
	 */
	@Test (expected = OrderException.class)
	public void TestRemoveFromEmptyOrder()
	{
		Mass appleMass = new Mass(220000000);
		PriceLookUpCode applePLUCode = new PriceLookUpCode("0132");
		PLUCodedItem apple = new PLUCodedItem(applePLUCode, appleMass);

		tempItemControllerInstance.removeItemFromOrder(orange, 1, order);
		tempItemControllerInstance.removeItemFromOrder(shampoo, 1, order);
		tempItemControllerInstance.removeItemFromOrder(apple, 1, order);
	}
	
	/**
	 * Test removing a null item from the order
	 */
	@Test (expected = NullPointerException.class)
	public void TestRemoveNullItem()
	{
		Item item = null;
		tempItemControllerInstance.removeItemFromOrder(item, 1, order);
	}
	
	/**
	 * Test removing an item with a null order
	 */
	@Test (expected = NullPointerException.class)
	public void TestRemoveWhenOrderNull()
	{
		HashMap<Item,Integer> nullOrder = null;
		tempItemControllerInstance.removeItemFromOrder(orange, 1, nullOrder);
	}

}
