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

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;

import org.junit.Test;

import com.jjjwelectronics.Item;
import com.jjjwelectronics.Mass;
import com.jjjwelectronics.Numeral;
import com.jjjwelectronics.scale.AbstractElectronicScale;
import com.jjjwelectronics.scanner.Barcode;
import com.jjjwelectronics.scanner.BarcodedItem;
import com.tdc.coin.Coin;
import com.tdc.coin.CoinSlot;
import com.thelocalmarketplace.hardware.Product;
import com.thelocalmarketplace.software.PayviaCoin;
import com.thelocalmarketplace.software.WeightDiscrepancy;
import com.jjjwelectronics.scale.ElectronicScaleBronze;
import com.jjjwelectronics.scale.ElectronicScaleGold;
import com.jjjwelectronics.scale.ElectronicScaleSilver;

import powerutility.PowerGrid;

/**
 * This class contains JUnit test cases for the WeightDiscrepancy class to verify its functionality.
 * @author Elizabeth Szentmiklossy (UCID: 30165216)
 */
public class WeightDiscrepancyTest {
	
		PowerGrid grid = PowerGrid.instance();
		Numeral[] numerals = new Numeral[]{Numeral.valueOf((byte) 2)};
		
		AbstractElectronicScale listner = new ElectronicScaleBronze();
		// Create an instance of WeightDiscrepancy with an expected weight of one gram and the electronic scale listener.
    	WeightDiscrepancy discrepancy = new WeightDiscrepancy(new Mass(5*Mass.MICROGRAMS_PER_GRAM), listner);
		Barcode barcode = new Barcode(numerals);
		BarcodedItem item;
		
		

		/**
	     * Test case to check if the actual weight is equal to the expected weight.
	     */
	    @Test 
	    public void EqualTO() {
	    	listner.plugIn(grid);
	    	listner.turnOn();
	    	// Create a BarcodedItem with the specified barcode and one gram of weight.
	    	item = new BarcodedItem(barcode, new Mass(5*Mass.MICROGRAMS_PER_GRAM));
	    	listner.addAnItem(item);
	    	// Verify that the expected and actual weights are equal.
	        assertEquals(discrepancy.CompareWeight(),true);
	    }
	    
	    /**
	     * Test case to check if the actual weight is more than the expected weight.
	     */
	    @Test
	    public void Sensetivity() {
	    	listner.plugIn(grid);
	    	listner.turnOn();
	    	Mass mass = new Mass(3*Mass.MICROGRAMS_PER_GRAM/2);
	    	
	    	// Create a BarcodedItem with the specified barcode and a weight of 20 grams.
	    	item = new BarcodedItem(barcode, mass);
	    	listner.addAnItem(item);
	    	Mass Sensetivity = listner.getSensitivityLimit();
	    	item = new BarcodedItem(barcode, Sensetivity);
	    	listner.addAnItem(item);
	    	
	    	// Verify that the expected and actual weights are not equal.
	        assertEquals(discrepancy.CompareWeight(),true);   	
					
		}
	    /**
	     * Test case to check if the actual weight is more than the expected weight.
	     */
	    @Test
	    public void MoreThan() {
	    	listner.plugIn(grid);
	    	listner.turnOn();
	    	Mass mass = new Mass(20*Mass.MICROGRAMS_PER_GRAM);
	    	// Create a BarcodedItem with the specified barcode and a weight of 20 grams.
	    	item = new BarcodedItem(barcode, mass);
	    	listner.addAnItem(item);
	    	// Verify that the expected and actual weights are not equal.
	        assertEquals(discrepancy.CompareWeight(),false);   	
			
		
		}


}
