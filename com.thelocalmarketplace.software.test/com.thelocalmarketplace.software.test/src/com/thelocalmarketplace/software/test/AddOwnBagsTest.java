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
// Jincheng Li: 30172907
import static org.junit.Assert.assertEquals;

import java.math.BigInteger;

import org.junit.Test;

import com.jjjwelectronics.Mass;
import com.jjjwelectronics.Numeral;
import com.jjjwelectronics.scale.AbstractElectronicScale;
import com.jjjwelectronics.scale.ElectronicScaleBronze;
import com.jjjwelectronics.scale.ElectronicScaleGold;
import com.jjjwelectronics.scanner.Barcode;
import com.jjjwelectronics.scanner.BarcodedItem;
import com.thelocalmarketplace.software.AddOwnBags;
import com.thelocalmarketplace.software.WeightDiscrepancy;

import powerutility.PowerGrid;

public class AddOwnBagsTest {
	AbstractElectronicScale listner = new ElectronicScaleBronze();
	PowerGrid grid = PowerGrid.instance();
	Numeral[] numerals = new Numeral[]{Numeral.valueOf((byte) 2)};
	Barcode barcode = new Barcode(numerals);
	BarcodedItem item;
	@Test
	public void testAddBagWeightSelected() {
		// 5g for the weight of bag
		// 20g for the item you want to purchase
		Mass bagWeight1 = new Mass(BigInteger.valueOf(5 * Mass.MICROGRAMS_PER_GRAM));
		Mass expectedWeight1 = new Mass(BigInteger.valueOf(20 * Mass.MICROGRAMS_PER_GRAM));
		// If the user selects the addOwnBagsSelection
		boolean addOwnBagsSelection1 = true;
		// Create an instance of WeightDiscrepancy with an expected weight of one gram and the electronic scale listener.
		listner.plugIn(grid);
    	listner.turnOn();
    	Mass mass = new Mass(25*Mass.MICROGRAMS_PER_GRAM);
    	// Create a BarcodedItem with the specified barcode and a weight of 20 grams.
    	item = new BarcodedItem(barcode, mass);
    	listner.addAnItem(item);
		AddOwnBags bag1 = new AddOwnBags(expectedWeight1, bagWeight1, addOwnBagsSelection1, listner);
		Mass expectedWeight2 = bag1.addBagWeight();
		AddOwnBags bag2 = new AddOwnBags(expectedWeight2, bagWeight1, addOwnBagsSelection1, listner);
		bag2.notifyListner();
		assertEquals(bag2.CompareWeight(), true);
	}
	@Test
	public void testAddBagWeightDeselected() {
		// 5g for the weight of bag
		// 20g for the item you want to purchase
		Mass bagWeight1 = new Mass(BigInteger.valueOf(200 * Mass.MICROGRAMS_PER_GRAM));
		Mass expectedWeight1 = new Mass(BigInteger.valueOf(200 * Mass.MICROGRAMS_PER_GRAM));
		// If the user does not select the addOwnBags selection
		boolean addOwnBagsSelection1 = false;
		// Create an instance of WeightDiscrepancy with an expected weight of one gram and the electronic scale listener.
		listner.plugIn(grid);
    	listner.turnOn();
    	Mass mass = new Mass(220*Mass.MICROGRAMS_PER_GRAM);
    	// Create a BarcodedItem with the specified barcode and a weight of 20 grams.
    	item = new BarcodedItem(barcode, mass);
    	listner.addAnItem(item);
		AddOwnBags bag1 = new AddOwnBags(expectedWeight1, bagWeight1, addOwnBagsSelection1, listner);
		// Since you deselect the add own bag selection and try to add bag on the scale, return false
		bag1.notifyListner();
		assertEquals(bag1.CompareWeight(), false);
	}
	
	@Test
	public void testBagOverloaded() {
		// 800g for the weight of bag
		// 20g for the item you want to purchase
		Mass bagWeight1 = new Mass(BigInteger.valueOf(800 * Mass.MICROGRAMS_PER_GRAM));
		Mass expectedWeight1 = new Mass(BigInteger.valueOf(20 * Mass.MICROGRAMS_PER_GRAM));
		// If the user does not select the addOwnBags selection
		boolean addOwnBagsSelection1 = true;
		// Create an instance of WeightDiscrepancy with an expected weight of one gram and the electronic scale listener.
		listner.plugIn(grid);
    	listner.turnOn();
    	Mass mass = new Mass(820*Mass.MICROGRAMS_PER_GRAM);
    	// Create a BarcodedItem with the specified barcode and a weight of 20 grams.
    	item = new BarcodedItem(barcode, mass);
    	listner.addAnItem(item);
		AddOwnBags bag1 = new AddOwnBags(expectedWeight1, bagWeight1, addOwnBagsSelection1, listner);
		bag1.bagWeightOverloaded();
		bag1.notifyListner();
		assertEquals(bag1.CompareWeight(), false);
	}









}

