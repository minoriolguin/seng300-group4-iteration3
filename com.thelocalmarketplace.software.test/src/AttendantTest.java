// Project 2 Iteration Group 3
//Julie Kim 10123567
//Aryaman Sandhu 30017164
//Arcleah Pascual 30056034
//Aoi Ueki 30179305
//Ernest Shukla 30156303
//Shawn Hanlon 10021510
//Jaimie Marchuk 30112841
//Sofia Rubio 30113733
//Maria Munoz 30175339
//Anne Lumumba 30171346
//Nathaniel Dafoe 30181948

import com.jjjwelectronics.Mass;
import com.thelocalmarketplace.hardware.SelfCheckoutStationGold;
import org.junit.Before;
import org.junit.Test;
import powerutility.PowerGrid;


import static org.junit.Assert.*;

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
        attendant.OverRideWeightDiscrepancy();
        assertEquals(testMass, checkout.getExpectedTotalWeight());
    }
}