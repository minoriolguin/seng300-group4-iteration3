package com.thelocalmarketplace.software.test;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.Before;
import org.junit.Test;

import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.hardware.SelfCheckoutStationBronze;
import com.thelocalmarketplace.software.Software;
import com.thelocalmarketplace.software.TouchScreen;

import powerutility.PowerGrid;

public class SignalAttendantTest {
	
	private Software software;
	private AbstractSelfCheckoutStation station;
	private TouchScreen touchScreen;
    
    @Before
    public void setUp() {
    	AbstractSelfCheckoutStation.resetConfigurationToDefaults();
    	
    	station = new SelfCheckoutStationBronze();
    	PowerGrid.engageUninterruptiblePowerSource();
    	station.plugIn(PowerGrid.instance());
    	software = new Software(station);
    	touchScreen = new TouchScreen(software);
     	
        software.turnOn();
        
       
    }
    
    /**
     * So, this relies on input from the Attendant.
     * Meaning you have to run it to see how it works.
     **/
    @Test
    public void testSignalAttendants() {
    	touchScreen.signalForAttendant();
    }

}
