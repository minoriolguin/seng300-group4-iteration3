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

package com.thelocalmarketplace.software.test;

import com.jjjwelectronics.Numeral;
import com.jjjwelectronics.scanner.Barcode;
import com.thelocalmarketplace.hardware.*;
import com.thelocalmarketplace.software.*;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import com.jjjwelectronics.EmptyDevice;
import com.jjjwelectronics.OverloadedDevice;
import com.jjjwelectronics.bag.ReusableBag;
import com.thelocalmarketplace.hardware.external.ProductDatabases;

import powerutility.PowerGrid;

/**
 * This class contains test cases for the PurchaseBags class in the com.thelocalmarketplace.software package.
 *@author Elizabeth Szentmiklossy
 *@CoAuthor Shawn Hanlon
 *
 *This documentation includes contributions from the following authors:
 *@author Elizabeth Szentmiklossy
 */
public class PurchaseBagsTest {
	private Software software;
	private AbstractSelfCheckoutStation hardware;
	private ReusableBag bag;
	private Barcode reuseableBagBarcode;
	   /**
     * Sets up the test environment before each test case.
     *
     * @throws OverloadedDevice Thrown if the device is overloaded during setup.
     */

	@Before
	public void setUp() throws OverloadedDevice {
		PowerGrid powerGrid = PowerGrid.instance();
		AbstractSelfCheckoutStation.resetConfigurationToDefaults();
		AbstractSelfCheckoutStation.configureReusableBagDispenserCapacity(300);
		hardware = new SelfCheckoutStationGold();
		software = Software.getInstance(hardware);
		software.turnOn();


		// Define a reusable Bag Barcode, Price and put it in the database
		Numeral[] reuseableBagNumeral = new Numeral[3];
		reuseableBagNumeral[0] = Numeral.nine;
		reuseableBagNumeral[1] = Numeral.five;
		reuseableBagNumeral[2] = Numeral.three;
		reuseableBagBarcode = new Barcode(reuseableBagNumeral);

	}
	
	/**
     * Tests the addBagsToDispenser method in the PurchaseBags class.
     *
     * @throws OverloadedDevice Thrown if the dispenser is overloaded during the test.
     */
	@Test
	public void addBagsToTheDispenser() throws OverloadedDevice {
		software.purchaseBags.addBagsToDispenser(10);
		assertEquals(10, software.reusableBagDispenser.getQuantityRemaining());
	}
	
    /**
     * Tests the addReusableBag method in the PurchaseBags class.
     *
     * @throws EmptyDevice      Thrown if the dispenser is empty during the test.
     * @throws OverloadedDevice Thrown if the dispenser is overloaded during the test.
     */
	@Test
	public void addReusableBag() throws EmptyDevice, OverloadedDevice {
		software.purchaseBags.addBagsToDispenser(10);
		software.purchaseBags.AddBagToOrder(1);
		assertTrue(software.getBarcodedProductsInOrder().contains(ProductDatabases.BARCODED_PRODUCT_DATABASE.get(reuseableBagBarcode)));
		assertEquals(9,software.reusableBagDispenser.getQuantityRemaining());
	}
    /**
     * Tests the addReusableBag method in the PurchaseBags class when the dispenser is empty.
     *
     * @throws EmptyDevice      Thrown if the dispenser is empty during the test.
     * @throws OverloadedDevice Thrown if the dispenser is overloaded during the test.
     */
	@Test
	public void addReusableBagWhenDispenserEmpty() throws EmptyDevice, OverloadedDevice {
		software.purchaseBags.addBagsToDispenser(1);
		software.purchaseBags.AddBagToOrder(2);
		assertTrue(software.attendant.reusableBagsEmpty);

	}
}


