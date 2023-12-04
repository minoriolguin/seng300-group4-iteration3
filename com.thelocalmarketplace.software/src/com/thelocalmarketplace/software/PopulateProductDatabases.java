package com.thelocalmarketplace.software;

import com.jjjwelectronics.Numeral;
import com.jjjwelectronics.scanner.Barcode;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.PLUCodedProduct;
import com.thelocalmarketplace.hardware.PriceLookUpCode;
import com.thelocalmarketplace.hardware.external.ProductDatabases;

public class PopulateProductDatabases {

	private PopulateProductDatabases() {}	
	
	public static void populateDatabases(){
	   // Populate databases	
	   for (int i = 1; i <= 20; i++) {
		   Barcode barcode = generateRandomBarcode();
		   BarcodedProduct barcodedProduct = new BarcodedProduct(barcode, randomBarcodedProduct(), generateRandomPrice(), generateRandomMass());
		   ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode, barcodedProduct);
		   
		   PriceLookUpCode plu = generateRandomPLU();
		   PLUCodedProduct pluProduct = new PLUCodedProduct(plu, randomPLUProduct(), generateRandomPrice());
		   ProductDatabases.PLU_PRODUCT_DATABASE.put(plu, pluProduct); // Assuming 100 items in inventory for each product
	    }     
	 }

	private static String randomBarcodedProduct() {
		String[] products = {"Apple", "Banana", "Orange", "Potato", "Carrot", "Broccoli", "Spinach", "Tomatoes", "Cucumber",
							"Onion", "Cucumber", "Bell peppers", "Strawberries", "Avocado", "Watermelon", "Kale", "Mango",
							"Lemon", "Lime", "Cauliflower", "Grapes", "Blackberries", "Pomegramate", "Zuchinni", "Eggplant"};
		
		int randomIndex = (int) (Math.random() * products.length);
        return products[randomIndex];
	}	

	private static String randomPLUProduct() {
		String[] products = {"Milk", "Eggs", "Bread", "Chicken", "Rice","Cheese", "Pasta", "Pants", "Chicken", "Coffee",
							"Ground beef", "Yogurt", "Cereal", "Salmon", "Cereal", "Juice", "Cookies", "Ice Cream",
							"Salad", "Rice", "Butter", "Candy", "Sugar", "Jam", "Ham"};

		int randomIndex = (int) (Math.random() * products.length);
		return products[randomIndex];	
	}
	
	private static Barcode generateRandomBarcode() {
		Numeral[] randomDigits = new Numeral[12]; // Assuming 12-digit barcodes

		for (int i = 0; i < 12; i++) {
			// Generate a random numeral between 0 and 9 (inclusive)
			int randomValue = (int) (Math.random() * 10);
			Numeral numeral = Numeral.values()[randomValue];
			randomDigits[i] = numeral;
		}

		return new Barcode(randomDigits);
	}   
	
	private static PriceLookUpCode generateRandomPLU() {
		String randomDigits = ""; // Assuming 12-digit barcodes

		for (int i = 0; i < 12; i++) {
			// Generate a random numeral between 0 and 9 (inclusive)
			int randomValue = (int) (Math.random() * 10);
			randomDigits = randomDigits + String.valueOf(randomValue);
		}	

		return new PriceLookUpCode(randomDigits);
	} 
	
    private static long generateRandomPrice() {
        // Generate a random price between 10.0 and 100.0 (inclusive)
        return (long) (10.0 + (Math.random() * 90.0));
    }

    private static double generateRandomMass() {
        // Generate a random mass between 0.1 and 5.0 kg (inclusive)
        return 0.1 + (Math.random() * 4.9);
    }

}
