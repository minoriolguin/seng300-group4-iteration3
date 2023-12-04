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

import com.jjjwelectronics.Numeral;
import com.jjjwelectronics.scanner.Barcode;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.PLUCodedProduct;
import com.thelocalmarketplace.hardware.PriceLookUpCode;
import com.thelocalmarketplace.hardware.external.ProductDatabases;

public class PopulateProductDatabases {

	private PopulateProductDatabases() { }	
	private static int onesCounter = 0;
	private static int tensCounter = 0;
	
	public static void populateDatabases(){
	   // Populate databases	
	   for (int i = 1; i <= 25; i++) {
		   
		   PriceLookUpCode plu = generateRandomPLU();
		   PLUCodedProduct pluProduct = new PLUCodedProduct(plu, randomPLUProduct(), generateRandomPrice());
		   ProductDatabases.PLU_PRODUCT_DATABASE.put(plu, pluProduct);
		   Barcode barcode = generateRandomBarcode();
		   BarcodedProduct barcodedProduct = new BarcodedProduct(barcode, randomBarcodedProduct(), generateRandomPrice(), generateRandomMass());
		   ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode, barcodedProduct);
		   

		   System.out.print(onesCounter);
		   System.out.println(tensCounter);
		   incrementCounters();
		   
	    }     
	 }

	private static String randomBarcodedProduct() {
		String[] products = {"Apple", "Banana", "Orange", "Potato", "Carrot", "Broccoli", "Spinach", "Tomatoes", "Cucumber",
							"Onion", "Cucumber", "Bell peppers", "Strawberries", "Avocado", "Watermelon", "Kale", "Mango",
							"Lemon", "Lime", "Cauliflower", "Grapes", "Blackberries", "Pomegramate", "Zuchinni", "Cherry"};
		
		int randomIndex = (int) (Math.random() * products.length);
        return products[randomIndex];
	}	

	private static String randomPLUProduct() {
		String[] products = {"Milk", "Eggs", "Bread", "Chicken", "Rice","Cheese", "Pasta", "Chicken", "Coffee",
							"Ground beef", "Yogurt", "Cereal", "Salmon", "Cereal", "Juice", "Cookies", "Ice Cream",
							"Salad", "Rice", "Butter", "Candy", "Sugar", "Jam", "Ham", "Bacon"};

		int randomIndex = (int) (Math.random() * products.length);
		return products[randomIndex];	
	}
	
	private static Barcode generateRandomBarcode() {
		Numeral[] barcode = new Numeral[8]; // Assuming 8-digit barcodes, 12345000-12345024

		barcode[0] = Numeral.one;
		barcode[1] = Numeral.two;
		barcode[2] = Numeral.three;
		barcode[3] = Numeral.four;
		barcode[4] = Numeral.five;
		barcode[5] = Numeral.zero;
		
		//makes the last two digits 0-24
		barcode[6] = Numeral.valueOf((byte) tensCounter);
		barcode[7] = Numeral.valueOf((byte) onesCounter);
		
		return new Barcode(barcode);
	}   
	
	private static PriceLookUpCode generateRandomPLU() {
		String pluCode = "000"; //5 digit plu code 00000 - 00024

		pluCode = pluCode + String.valueOf(tensCounter) + String.valueOf(onesCounter);

		return new PriceLookUpCode(pluCode);
	} 
	
	private static void incrementCounters() {
		onesCounter++;
		if (onesCounter == 10) {
			onesCounter = 0;
			tensCounter++;
		}
	}
	
    private static long generateRandomPrice() {
        // Generate a random price between 10.0 and 100.0 (inclusive)
        return (long) (10.0 + (Math.random() * 90.0));
    }

    private static double generateRandomMass() {
        // Generate a random mass between 0.1 and 5.0 kg (inclusive)
        return 0.1 + (Math.random() * 4.9);
    }
 
    
//test print DO NOT SUBMIT----------------------------------------------------------------------------------------------------------
//    public static void printDatabases() {
//        System.out.println("BARCODED_PRODUCT_DATABASE:");
//        ProductDatabases.BARCODED_PRODUCT_DATABASE.forEach((barcode, product) -> {
//            System.out.println(barcode + ": " + product);
//        });
//
//        System.out.println("\nPLU_PRODUCT_DATABASE:");
//        ProductDatabases.PLU_PRODUCT_DATABASE.forEach((plu, product) -> {
//            System.out.println(plu + ": " + product);
//        });
//    }
//
//   
//
//    public static void main(String[] args) {
//        populateDatabases();
//        printDatabases();
//    }

}
