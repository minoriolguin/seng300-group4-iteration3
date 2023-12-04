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
