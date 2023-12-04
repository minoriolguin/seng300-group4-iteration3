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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.jjjwelectronics.Mass;
import com.thelocalmarketplace.hardware.SelfCheckoutStationGold;
import com.thelocalmarketplace.software.Attendant;
import com.thelocalmarketplace.software.Software;

import powerutility.PowerGrid;

public class AttendantTest {
    private Software checkout;
    private SelfCheckoutStationGold station;
    private Attendant attendant;
    private Mass testMass;


    @Before
    public void setUp() {
        PowerGrid.engageUninterruptiblePowerSource();
        SelfCheckoutStationGold.resetConfigurationToDefaults();
        station = new SelfCheckoutStationGold();
        checkout = Software.getInstance(station);
        checkout.turnOn();
        attendant = new Attendant(checkout);
        testMass = new Mass(5000);
    }

    @Test
    public void testNotifySkipBagging() {
    	// Test when Customer wants to skip bagging and Attendant is notified
        attendant.notifySkipBagging();
        assertFalse(checkout.isBlocked());
    }

    @Test
    public void testVerifyItemInBaggingArea() {
    	// Test when Attendant has verified the Item(s) in Bagging Area
        attendant.verifyItemInBaggingArea();
        assertFalse(checkout.isBlocked());
    }
    @Test
    public void testOverRideWeightDiscrepancy() {
    	// Test when Attendant has overrided Weight Discrepancy
        checkout.weightDiscrepancy.overRideWeight = testMass;
        attendant.overRideWeightDiscrepancy();
        assertEquals(testMass, checkout.getExpectedTotalWeight());
    }
    
    @Test //excepted exception???
    public void testDisableCustomerStationWhileSessionActive() {
    	checkout.startSession();
    	attendant.disableCustomerStation();
    	assertTrue(checkout.isCustomerStationBlocked());
    }
    
    @Test
    public void testDisableCustomerStation() {
    	attendant.disableCustomerStation();
    	assertTrue(checkout.isCustomerStationBlocked());
    }
    
    @Test
    public void testEnableCustomerStationWhenEnabled() {
    	attendant.enableCustomerStation();
    	assertFalse(checkout.isCustomerStationBlocked()); //Nothing happens
    }
    
    @Test
    public void testEnableCustomerStationWhenDisabled() {
    	attendant.disableCustomerStation();
    	attendant.enableCustomerStation();
    	assertFalse(checkout.isCustomerStationBlocked());
    }
}
