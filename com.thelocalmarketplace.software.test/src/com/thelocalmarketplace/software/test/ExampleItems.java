/**
 * @author Alan Yong: 30105707
 * @author Atique Muhammad: 30038650
 * @author Ayman Momin: 30192494
 * @author Christopher Lo: 30113400
 * @author Ellen Bowie: 30191922
 * @author Emil Huseynov: 30171501
 * @author Eric George: 30173268
 * @author Kian Sieppert: 30134666
 * @author Muzammil Saleem: 30180889
 * @author Ryan Korsrud: 30173204
 * @author Sukhnaaz Sidhu: 30161587
 */
package com.thelocalmarketplace.software.test;

import java.math.BigDecimal;

import com.jjjwelectronics.Mass;
import com.jjjwelectronics.Numeral;
import com.jjjwelectronics.scanner.Barcode;
import com.jjjwelectronics.scanner.BarcodedItem;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.external.ProductDatabases;


// Contains example items that can be used for testing
public class ExampleItems {
	public static void updateDatabase() {
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(AppleJuice.barcode, AppleJuice.barcodedProduct);
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(PotatoChips.barcode, PotatoChips.barcodedProduct);
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(PeanutButter.barcode, PeanutButter.barcodedProduct);
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(CustomerBag.barcode, CustomerBag.barcodedProduct);
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(HeavyItem.barcode, HeavyItem.barcodedProduct);
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(HeavyItem2.barcode, HeavyItem2.barcodedProduct);
	}
	
	public static class AppleJuice {
		static Numeral[] barcodeDigits = {Numeral.one, Numeral.five, Numeral.seven, Numeral.three};
		static double actualWeightGrams = 225;
		static Mass actualMass = new Mass(actualWeightGrams);
		static double expectedWeightGrams = 225;
		static String desc = "Apple Juice (225g)";
		static long price = 3;
		static BigDecimal bdPrice = new BigDecimal((double)price);
		static Barcode barcode = new Barcode(barcodeDigits);
		static BarcodedItem barcodedItem = new BarcodedItem(barcode, actualMass);
		static BarcodedProduct barcodedProduct = new BarcodedProduct(barcode, desc, price, expectedWeightGrams);
	}
	
	public static class PotatoChips {
		static Numeral[] barcodeDigits = {Numeral.seven, Numeral.three, Numeral.two, Numeral.nine};
		static double actualWeightGrams = 37;
		static Mass actualMass = new Mass(actualWeightGrams);
		static double expectedWeightGrams = 37;
		static String desc = "Potato Chips (37g)";
		static long price = 4;
		static BigDecimal bdPrice = new BigDecimal((double)price);
		static Barcode barcode = new Barcode(barcodeDigits);
		static BarcodedItem barcodedItem = new BarcodedItem(barcode, actualMass);
		static BarcodedProduct barcodedProduct = new BarcodedProduct(barcode, desc, price, expectedWeightGrams);
	}
	
	public static class PeanutButter {
		static Numeral[] barcodeDigits = {Numeral.two, Numeral.two, Numeral.two, Numeral.seven};
		static double actualWeightGrams = 289;
		static Mass actualMass = new Mass(actualWeightGrams);
		static double expectedWeightGrams = 289;
		static String desc = "Peanut Butter (289g)";
		static long price = 6;
		static BigDecimal bdPrice = new BigDecimal((double)price);
		static Barcode barcode = new Barcode(barcodeDigits);
		static BarcodedItem barcodedItem = new BarcodedItem(barcode, actualMass);
		static BarcodedProduct barcodedProduct = new BarcodedProduct(barcode, desc, price, expectedWeightGrams);
	}
	
	public static class CustomerBag {
		static Numeral[] barcodeDigits = {Numeral.one, Numeral.two, Numeral.two, Numeral.eight};
		static double actualWeightGrams = 150;
		static Mass actualMass = new Mass(actualWeightGrams);
		static double expectedWeightGrams = 150;
		static String desc = "Customer's Bag. Should not be added to item list.";
		static long price = 1;
		static BigDecimal bdPrice = new BigDecimal((double)price);
		static Barcode barcode = new Barcode(barcodeDigits);
		static BarcodedItem barcodedItem = new BarcodedItem(barcode, actualMass);
		static BarcodedProduct barcodedProduct = new BarcodedProduct(barcode, desc, price, expectedWeightGrams);
	}
	
	public static class HeavyItem {
		static Numeral[] barcodeDigits = {Numeral.four, Numeral.two, Numeral.two, Numeral.nine};
		static double actualWeightGrams = 1000000000;
		static Mass actualMass = new Mass(actualWeightGrams);
		static double expectedWeightGrams = 1000000000;
		static String desc = "Very heavy item";
		static long price = 1;
		static BigDecimal bdPrice = new BigDecimal((double)price);
		static Barcode barcode = new Barcode(barcodeDigits);
		static BarcodedItem barcodedItem = new BarcodedItem(barcode, actualMass);
		static BarcodedProduct barcodedProduct = new BarcodedProduct(barcode, desc, price, expectedWeightGrams);
	}
	public static class HeavyItem2 {
		static Numeral[] barcodeDigits = {Numeral.four, Numeral.three, Numeral.two, Numeral.nine};
		static double actualWeightGrams = 1000000000;
		static Mass actualMass = new Mass(actualWeightGrams);
		static double expectedWeightGrams = 1000000000;
		static String desc = "Very heavy item";
		static long price = 1;
		static BigDecimal bdPrice = new BigDecimal((double)price);
		static Barcode barcode = new Barcode(barcodeDigits);
		static BarcodedItem barcodedItem = new BarcodedItem(barcode, actualMass);
		static BarcodedProduct barcodedProduct = new BarcodedProduct(barcode, desc, price, expectedWeightGrams);
	}
}
