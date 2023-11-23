package com.thelocalmarketplace.software.test.logic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import com.jjjwelectronics.Mass;
import com.jjjwelectronics.Numeral;
import com.jjjwelectronics.scanner.Barcode;
import com.jjjwelectronics.scanner.BarcodedItem;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.Product;
import com.thelocalmarketplace.hardware.SelfCheckoutStationBronze;
import com.thelocalmarketplace.hardware.external.ProductDatabases;
import com.thelocalmarketplace.software.logic.CentralStationLogic;
import com.thelocalmarketplace.software.logic.StateLogic.States;

import ca.ucalgary.seng300.simulation.InvalidStateSimulationException;
import powerutility.PowerGrid;

/**
 * @author Daniel Yakimenka (10185055)
 * -----------------------------------
 * @author Angelina Rochon (30087177)
 * @author Connell Reffo (10186960)
 * @author Tara Strickland (10105877)
 * @author Julian Fan (30235289)
 * @author Braden Beler (30084941)
 * @author Samyog Dahal (30194624)
 * @author Maheen Nizmani (30172615)
 * @author Phuong Le (30175125)
 * @author Merick Parkinson (30196225)
 * @author Farida Elogueil (30171114)
 */
public class RemoveItemTests {

		SelfCheckoutStationBronze station;
		CentralStationLogic session;
		
		//stuff for database
		
		public Barcode barcode;
		public Barcode barcode2;
		public Numeral digits;
		public Barcode nullBarcode;
		
		public BarcodedItem bitem;
		public Mass itemMass;
		
		public BarcodedItem bitem2;
		public Mass itemMass2;
		public BarcodedItem bitem3;
		public Mass itemMass3;
		
		public BarcodedItem bitem4;
		public Mass itemMass4;
		
		public BarcodedItem bitem5;
		public Mass itemMass5;
		public Numeral[] barcode_numeral;
		public Numeral[] barcode_numeral2;
		public Numeral[] barcode_numeral3;
		public Barcode barcode3;
		public BarcodedProduct product;
		public BarcodedProduct product2;
		public BarcodedProduct product3;
		public BarcodedProduct nullProduct;
		
		@Before public void setUp() {
			PowerGrid.engageUninterruptiblePowerSource();
			PowerGrid.instance().forcePowerRestore();
			
			AbstractSelfCheckoutStation.resetConfigurationToDefaults();

			this.station = new SelfCheckoutStationBronze();
			
			//initialize database
			barcode_numeral = new Numeral[] {Numeral.one, Numeral.two, Numeral.three};
			barcode_numeral2 = new Numeral[] {Numeral.three, Numeral.two, Numeral.three};
			barcode_numeral3 = new Numeral[] {Numeral.three, Numeral.three, Numeral.three};
			barcode = new Barcode(barcode_numeral);
			barcode2 = new Barcode(barcode_numeral2);
			barcode3 = new Barcode(barcode_numeral3);

			
			product = new BarcodedProduct(barcode, "some item",(long)5.99,(double)400.0);
			product2 = new BarcodedProduct(barcode2, "some item 2",(long)1.00,(double)300.0);

			ProductDatabases.BARCODED_PRODUCT_DATABASE.clear();
			ProductDatabases.INVENTORY.clear();
			
			ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode, product);
			ProductDatabases.INVENTORY.put(product, 1);
			ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode2, product2);
			ProductDatabases.INVENTORY.put(product2, 1);	


			itemMass = new Mass((double) 400.0);
			bitem = new BarcodedItem(barcode, itemMass);
			itemMass2 = new Mass((double) 300.0);//300.0 grams
			bitem2 = new BarcodedItem(barcode2, itemMass2);
			itemMass3 = new Mass((double) 300.0);//3.0 grams
			bitem3 = new BarcodedItem(barcode3, itemMass3);
			//bitem4 = new BarcodedItem(b_test, itemMass3);
			itemMass5 = new Mass((double) 300.0);
			bitem5 = new BarcodedItem(barcode2,itemMass5);
			
			//Initialize station
			station.plugIn(PowerGrid.instance());
			station.turnOn();
			
			session = new CentralStationLogic(station);
			session.startSession();
		}
		@Rule
		public TestName name = new TestName();
		
		@After
		public void displayTest() {
			System.out.println(name.getMethodName() + " has completed. \n");
		}
		
		//the following function was taken mainly from Angelina's tests for bulkyitems
		public void scanUntilAdded(Product p, BarcodedItem b) {
			int cnt = 0;
			
			while(cnt< 10000 && !session.cartLogic.getCart().containsKey(p)) {
				station.handheldScanner.scan(b);
				cnt++;
			}
		}
		
		/** Tests if the method actually removes an item from the cart when called
		 * 
		 */
		@Test
		public void testSuccessfulRemoval() {
			this.scanUntilAdded(product, bitem);
			assertTrue(session.cartLogic.getCart().size() == 1);
			
			station.baggingArea.addAnItem(bitem);
			
			session.removeItemLogic.removeBarcodedItem(product);
			assertEquals(0, session.cartLogic.getCart().size());
			
			System.out.println("Test 1 end\n");
			
		}
		/** Tests if the station is blocked while the correct item isn't removed, and unblocked when it is removed
		 * 
		 */
		@Test
		public void testPostRemovalBlock() {
			this.scanUntilAdded(product, bitem);
			station.baggingArea.addAnItem(bitem);
			
			session.removeItemLogic.removeBarcodedItem(product);
			assertTrue(session.stateLogic.getState() == States.BLOCKED);
			
			station.baggingArea.removeAnItem(bitem);
			assertTrue(session.stateLogic.getState() == States.NORMAL);
			System.out.println("Test 2 end\n");
		}
		/**
		 * Tests if removing the wrong item from bagging area causes a weight discrepancy
		 */
		@Test
		public void testIncorrectRemoval() {
			this.scanUntilAdded(product, bitem);
			station.baggingArea.addAnItem(bitem);
			
			
			this.scanUntilAdded(product2, bitem2);
			station.baggingArea.addAnItem(bitem2);
			
			session.removeItemLogic.removeBarcodedItem(product);
			
			station.baggingArea.removeAnItem(bitem2);
			assertTrue(session.stateLogic.getState() == States.BLOCKED);
			
			station.baggingArea.addAnItem(bitem2);
			station.baggingArea.removeAnItem(bitem);
			assertTrue(session.stateLogic.getState() == States.NORMAL);	
			
			System.out.println("Test 3 end\n");
		}
		/**
		 * Tests if method fails on a null item.
		 */
		
		@Test (expected = NullPointerException.class)
		public void failOnNullItem() {
			session.removeItemLogic.removeBarcodedItem(null);
			
			System.out.println("Test 4 end\n");
			
		}
		
		/**
		 * Tests if method fails on removal request of an item not in cart.
		 */
		@Test (expected = InvalidStateSimulationException.class)
		public void failOnItemNotInCart() {
			session.removeItemLogic.removeBarcodedItem(product);
			
			System.out.println("Test 5 end\n");
		}
		
		/**
		 * Tests if method can resolve a weight discrepancy event caused by not putting an added item into the bagging area
		 */
		@Test
		public void resolveWeightDescrepancyByRemoval() {
			this.scanUntilAdded(product, bitem);
			assertTrue(session.stateLogic.getState() == States.BLOCKED);

			session.removeItemLogic.removeBarcodedItem(product);
			assertTrue(session.stateLogic.getState() == States.NORMAL);	
			
			System.out.println("Test 6 end\n");
			
		}
		
		/**
		 * Tests if the method fails when a session hasn't been started
		 */
		@Test (expected = InvalidStateSimulationException.class)
		public void failOnNullSession() {
			session.stopSession();
			session.removeItemLogic.removeBarcodedItem(product);
		}
}
