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

import com.jjjwelectronics.IDevice;
import com.jjjwelectronics.IDeviceListener;
import com.jjjwelectronics.Mass;
import com.jjjwelectronics.Numeral;
import com.jjjwelectronics.scale.AbstractElectronicScale;
import com.jjjwelectronics.scale.ElectronicScaleGold;
import com.jjjwelectronics.scanner.*;
import com.tdc.coin.CoinDispenserGold;
import com.thelocalmarketplace.hardware.CoinTray;
import com.thelocalmarketplace.software.Session;
import com.thelocalmarketplace.software.WeightDiscrepancy;

import org.junit.*;
import powerutility.PowerGrid;

import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Unit testing for the com.thelocalmarketplace.software.Session class.
 *
 * @author Arthur Huan: 30197354
 */

public class SessionTest {
    private static PowerGrid grid;
    private Session mySession;
    private BarcodeScannerGold myScanner;
    private static BarcodedItem myItem1;
    private static BarcodedItem myItem2;
    private static Barcode myBarcode1;
    private static Barcode myBarcode2; 
    private static Barcode scannedBarcode;
    private static ScannerListener myScannerListener;

    private static class ScannerListener implements BarcodeScannerListener {
        @Override
        public void aDeviceHasBeenEnabled(IDevice<? extends IDeviceListener> device) {}
        @Override
        public void aDeviceHasBeenDisabled(IDevice<? extends IDeviceListener> device) {}
        @Override
        public void aDeviceHasBeenTurnedOn(IDevice<? extends IDeviceListener> device) {}
        @Override
        public void aDeviceHasBeenTurnedOff(IDevice<? extends IDeviceListener> device) {}
        @Override
        public void aBarcodeHasBeenScanned(IBarcodeScanner barcodeScanner, Barcode barcode) {
            scannedBarcode = barcode;
        }
    }

    @BeforeClass
    public static void setUp() {
        Numeral[] code = new Numeral[3];
        Arrays.fill(code, Numeral.valueOf((byte)1));
        myBarcode1 = new Barcode(code);
        code = new Numeral[4];
        Arrays.fill(code, Numeral.valueOf((byte)1));
        myBarcode2 = new Barcode(code);
        myItem1 = new BarcodedItem(myBarcode1, new Mass(5.0));
        myItem2 = new BarcodedItem(myBarcode2, new Mass(100.0));
        myScannerListener = new ScannerListener();
        
        grid = PowerGrid.instance();
    }

    @Before
    public void setUpSession() {
        myScanner = new BarcodeScannerGold();
        myScanner.plugIn(grid);
        myScanner.turnOn();
        myScanner.register(myScannerListener);
        mySession = new Session(myScanner);
    }

    @Test
    public void testStartSession() {
        assertEquals(0, mySession.getStatus());
        assertTrue(myScanner.isDisabled());
        mySession.startSession();
        assertEquals(1, mySession.getStatus());
        assertFalse(myScanner.isDisabled());
    }

    @Test
    public void testSession() {
        mySession.startSession();
        myScanner.scan(myItem1);
        assertEquals(myBarcode1, scannedBarcode);
        myScanner.scan(myItem2);
        assertEquals(myBarcode2, scannedBarcode);
        mySession.freezeSession();
        myScanner.scan(myItem1);
        assertEquals(myBarcode2, scannedBarcode);
    }
    
	 @Test
	    public void weightDiscrepancy(){
         ElectronicScaleGold scale = new ElectronicScaleGold();
         CoinTray coinTray = new CoinTray(10);
         scale.plugIn(grid);
         scale.turnOn();
         mySession.addHardwareComponent(coinTray);
         mySession.addHardwareDevice(scale);
         myScanner.scan(myItem2);
	 }
}
