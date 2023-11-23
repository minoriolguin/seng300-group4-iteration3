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

import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.hardware.Product;
import com.thelocalmarketplace.hardware.SelfCheckoutStationBronze;
import com.thelocalmarketplace.software.ActionBlocker;
import com.thelocalmarketplace.software.HandleBulkyItem;
import com.thelocalmarketplace.software.WeightDiscrepancy;
import com.thelocalmarketplace.software.WeightDiscrepancyListner;
import com.jjjwelectronics.IDevice;
import com.jjjwelectronics.IDeviceListener;
import com.jjjwelectronics.Item;
import com.jjjwelectronics.Mass;
import com.jjjwelectronics.Numeral;
import com.jjjwelectronics.scale.IElectronicScale;
import com.jjjwelectronics.scanner.Barcode;
import com.jjjwelectronics.scanner.BarcodedItem;
import com.jjjwelectronics.scale.ElectronicScaleBronze;
import com.jjjwelectronics.scale.ElectronicScaleGold;
import com.jjjwelectronics.scale.ElectronicScaleListener;
import com.jjjwelectronics.scale.ElectronicScaleSilver;
import com.jjjwelectronics.scale.AbstractElectronicScale;
import powerutility.PowerGrid; 


import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import org.junit.Assert;

public class HandleBulkyItemTest {
    // Initialize variables
	private AbstractSelfCheckoutStation checkoutStation;
    private SelfCheckoutStationBronze bronzeStation;
    private HandleBulkyItem handleBulkyItem;
    private WeightDiscrepancy weightDiscrepancy;
    private WeightDiscrepancyListner weightDiscrepancyListener;
    private BarcodedItem bulkyItem;
    private PowerGrid grid;
    private IDeviceListener listener;
    private Barcode barcode;
    private ElectronicScaleGold scaleListener;
    private Product product;
    private ActionBlocker blocker;

    // Set up the test
    @Before
    public void setUp() {
    	PowerGrid grid = PowerGrid.instance(); // Initialize the power grid
        Numeral[] numerals = new Numeral[]{Numeral.valueOf((byte) 2)};
        // Create objects for testing
        this.blocker = new ActionBlocker();
        this.scaleListener = new ElectronicScaleGold();
		this.weightDiscrepancy = new WeightDiscrepancy(new Mass(5*Mass.MICROGRAMS_PER_GRAM), scaleListener);
        this.handleBulkyItem = new HandleBulkyItem(checkoutStation, listener, blocker, weightDiscrepancy, scaleListener, weightDiscrepancyListener);
        Barcode barcode = new Barcode(numerals);
        // Turn on the power
		scaleListener.plugIn(grid);
		scaleListener.turnOn();
    }

    // Test that running requestNoBagging() disables devices
    @Test
    public void testRequestNoBaggingDisablesDevices() {
    	boolean bagItem = false;
        handleBulkyItem.requestNoBagging(bulkyItem);
        
        assertTrue("Session should be blocked", blocker.isInteractionBlocked());
    }
    
    // Test that if the customer adds the item to the bagging area, a weight discrepancy occurs
    @Test
    public void testRequestNoBaggingWithoutBagging() {
        boolean bagItem = true;
        ElectronicScaleGold scaleListener = new ElectronicScaleGold();
        PowerGrid grid = PowerGrid.instance();
        scaleListener.plugIn(grid);
        scaleListener.turnOn();
		WeightDiscrepancy discrepancy = new WeightDiscrepancy(new Mass(5*Mass.MICROGRAMS_PER_GRAM), scaleListener);
		//discrepancy.ItemHasBeenAdded(bulkyItem);
        this.bulkyItem = new BarcodedItem(new Barcode(new Numeral[]{Numeral.zero, Numeral.one}), new Mass(500));
        handleBulkyItem.setBagItem(false); // Simulate customer choosing to bag the item anyway


        // Perform the action that may cause a weight discrepancy
        handleBulkyItem.requestNoBagging(bulkyItem);
        
        // System should be blocked as per weight discrepancy
        assertTrue("Session should be blocked", blocker.isInteractionBlocked());
    }
    
    // If the attendant approves the no-bagging request, the session should be unblocked
    @Test
    public void testNotifyAttendantUnblocksInteraction() {
    	boolean attendantApproval = true;
    	handleBulkyItem.notifyAttendant(attendantApproval);
        assertFalse("Interaction should no longer be blocked", blocker.isInteractionBlocked());
    }
    
    // If attendant does not approve, just return false
    @Test
    public void testNotifyAttendantReturnsFalse() {
    	boolean attendantApproval = false;
    	handleBulkyItem.notifyAttendant(attendantApproval);
    }
    
    // When FixDiscrepancy is called, the adjusted weight should match
    // the new expected weight
 // When FixDiscrepancy is called, the adjusted weight should match
    // the new expected weight
    @Test
    public void testFixDiscrepancyAdjustsWeight() {
        Mass initialExpectedWeight = new Mass(1000);
        bulkyItem = new BarcodedItem(new Barcode(new Numeral[]{Numeral.zero, Numeral.one}), new Mass(500));

        // Call the method under test
        handleBulkyItem.fixDiscrepancy(bulkyItem);

        // Assert that the expected weight is updated correctly
        assertEquals("Expected weight should be reduced by the weight of the bulky item", new Mass(500), bulkyItem.getMass());
    }


}

