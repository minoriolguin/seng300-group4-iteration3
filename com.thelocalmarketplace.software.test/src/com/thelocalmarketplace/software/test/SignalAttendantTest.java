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
