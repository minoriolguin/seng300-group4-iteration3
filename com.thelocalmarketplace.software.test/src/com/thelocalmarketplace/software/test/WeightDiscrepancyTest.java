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

import com.jjjwelectronics.*;
import com.jjjwelectronics.scale.ElectronicScaleGold;
import com.jjjwelectronics.scale.ElectronicScaleListener;
import com.jjjwelectronics.scale.IElectronicScale;
import com.jjjwelectronics.scanner.Barcode;
import com.jjjwelectronics.scanner.BarcodeScannerGold;
import com.jjjwelectronics.Numeral;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.SelfCheckoutStationGold;
import com.thelocalmarketplace.software.Software;
import com.thelocalmarketplace.software.TouchScreen;
import com.thelocalmarketplace.software.WeightDiscrepancy;
import com.thelocalmarketplace.software.WeightDiscrepancyListener;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import powerutility.PowerGrid;
import java.math.BigDecimal;

import java.util.List;


public class WeightDiscrepancyTest {

    private Software checkout;
    private AbstractSelfCheckoutStation station;
    private BarcodedProduct product;
    private Barcode barcode;
    private double expectedWeight;
    private long price;
    private String description;
    private ElectronicScaleGold scaleGold;
    private BarcodeScannerGold handHeld;
    private BarcodeScannerGold mainScanner;
    private TouchScreen touchScreen;
    private WeightDiscrepancy weightDiscrepancy;

    public class TestMockListener implements WeightDiscrepancyListener {
        public boolean removeItem;
        public boolean bagsTooHeavy;
        public boolean addItemToScale;
        public boolean noDiscrepancy;
        private boolean weightOverLimitCalled = false;

        public TestMockListener() {
            this.removeItem = false;
            this.bagsTooHeavy = false;
            this.addItemToScale = false;
            this.noDiscrepancy = false;
        }

        @Override
        public void RemoveItemFromScale() {
            this.removeItem = true;
        }

        @Override
        public void AddItemToScale() {
            this.addItemToScale = true;
        }

        @Override
        public void weightOverLimit() {
            weightOverLimitCalled = true;
        }

        public boolean isWeightOverLimitCalled() {
            return weightOverLimitCalled;
        }

        @Override
        public void noDiscrepancy() {
            this.noDiscrepancy = true;
        }

        @Override
        public void bagsTooHeavy() {
            this.bagsTooHeavy = true;
        }
    }

    private TestMockListener mockListener;

    @Before
    public void setUp() {
        scaleGold = new ElectronicScaleGold();
        handHeld = new BarcodeScannerGold();
        mainScanner = new BarcodeScannerGold();

        mockListener = new TestMockListener();

        SelfCheckoutStationGold.resetConfigurationToDefaults();
        station = new SelfCheckoutStationGold();


        checkout = Software.getInstance(station);
        checkout.setExpectedTotalWeight(new Mass(1000));

        touchScreen = new TouchScreen(checkout);


        Numeral[] riceBarcode = new Numeral[3];
        riceBarcode[0] = Numeral.zero;
        riceBarcode[1] = Numeral.zero;
        riceBarcode[2] = Numeral.one;
        barcode = new Barcode(riceBarcode);
        expectedWeight = 1000;
        description = "Bag of Rice";
        price = 10;
        product = new BarcodedProduct(barcode, description, price, expectedWeight);

        weightDiscrepancy = new WeightDiscrepancy(checkout);
        // new
        weightDiscrepancy.register(mockListener);
    }

    @Test
    public void TestRegister() {
        WeightDiscrepancy instance = new WeightDiscrepancy(checkout);

        Assert.assertEquals(0, instance.getListeners().size());

        instance.register(touchScreen);

        Assert.assertEquals(1, instance.getListeners().size());
    }

    @Test
    public void TestExpectedOwnBagsNotifiesBagsTooHeavy() {
        checkout.turnOn();

        Mass mass = new Mass(300.0);

        checkout.weightDiscrepancy.register(mockListener);

        Assert.assertEquals(false, mockListener.bagsTooHeavy);

        checkout.touchScreen.selectAddOwnBags();
        checkout.weightDiscrepancy.isWeightDiscrepancy(mass);

        Assert.assertEquals(true, mockListener.bagsTooHeavy);
    }

    @Test
    public void TestExpectedOwnBagsSetsExpectedTotalWeight() {
        checkout.turnOn();

        WeightDiscrepancy instance = new WeightDiscrepancy(checkout);

        instance.register(mockListener);

        checkout.touchScreen.selectAddOwnBags();
        Mass mass = new Mass(2000);
        instance.isWeightDiscrepancy(mass);

        Assert.assertEquals(false, mockListener.bagsTooHeavy);
    }

    @Test
    public void isWeightDiscrepancyNotifiesRemoveItemFromScale() {
        checkout.turnOn();
        checkout.blockCustomer();

        Mass mass = new Mass(123);
        WeightDiscrepancy instance = new WeightDiscrepancy(checkout);

        instance.register(mockListener);

        Assert.assertEquals(false, mockListener.removeItem);

        instance.isWeightDiscrepancy(mass);
        Assert.assertEquals(true, mockListener.removeItem);
    }

    @Test
    public void isWeightDiscrepancyNotifiesAddItemToScale() {
        checkout.turnOn();
        checkout.blockCustomer();

        Mass mass = new Mass(10000);
        WeightDiscrepancy instance = new WeightDiscrepancy(checkout);

        instance.register(mockListener);

        Assert.assertEquals(false, mockListener.addItemToScale);


        instance.isWeightDiscrepancy(mass);

        Assert.assertEquals(true, mockListener.addItemToScale);
    }

    @Test
    public void isWeightDiscrepancyNotifiesNoDiscrepancy() {
        checkout.turnOn();
        checkout.unblockCustomer();
        Mass mass = new Mass(1000);
        WeightDiscrepancy instance = new WeightDiscrepancy(checkout);

        instance.register(mockListener);

        Assert.assertEquals(false, mockListener.noDiscrepancy);


        instance.isWeightDiscrepancy(mass);

        Assert.assertEquals(true, mockListener.noDiscrepancy);
    }

    @Test
    public void testNotifyWeightOverLimit() {
        checkout.turnOn();
        Mass mass = new Mass(999999);
        WeightDiscrepancy instance = new WeightDiscrepancy(checkout);

        instance.register(mockListener);
        
//        Needs to be fixed
//        weightDiscrepancy.notifyWeightOverLimit();

        Assert.assertEquals(true, mockListener.isWeightOverLimitCalled());

    }

    @Test
    public void testTheMassOnTheScaleHasChanged() {
        checkout.turnOn();

        IDevice<IDeviceListener> mockDevice = new MockDevice();
        IElectronicScale mockScale = new MockElectronicScale();

        weightDiscrepancy.theMassOnTheScaleHasChanged(mockScale, new Mass(BigDecimal.ZERO));
    }

    @Test
    public void testTheMassOnTheScaleHasExceededItsLimit() {
        IElectronicScale mockScale = new MockElectronicScale();

        weightDiscrepancy.theMassOnTheScaleHasExceededItsLimit(mockScale);

        Assert.assertTrue(mockListener.isWeightOverLimitCalled());
    }

    @Test
    public void testTheMassNoLongerExceedsItsLimit(){
        IElectronicScale mockScale = new MockElectronicScale();
        weightDiscrepancy.theMassOnTheScaleNoLongerExceedsItsLimit(mockScale);

    }
    @Test
    public void testADeviceHasBeenEnabled(){
        IDevice<IDeviceListener> mockDevice = new MockDevice();
        weightDiscrepancy.aDeviceHasBeenEnabled(mockDevice);

        Assert.assertEquals(false, mockDevice.isDisabled());
    }

    @Test
    public void testADeviceHasBeenDisabled() {
        IDevice<IDeviceListener> mockDevice = new MockDevice();
        weightDiscrepancy.aDeviceHasBeenDisabled(mockDevice);

        Assert.assertEquals(false, mockDevice.isDisabled());
    }

    @Test
    public void testADeviceHasBeenTurnedOff(){
        IDevice<IDeviceListener> mockDevice = new MockDevice();
        weightDiscrepancy.aDeviceHasBeenTurnedOff(mockDevice);

        Assert.assertEquals(false, mockDevice.isDisabled());

    }

//MockDevice to use as Stub
private static class MockDevice implements IDevice<IDeviceListener>{
    private boolean disabled = false;


    @Override
    public boolean isPluggedIn() {
        return false;
    }

    @Override
    public boolean isPoweredUp() {
        return false;
    }

    @Override
    public void plugIn(PowerGrid grid) {
        // Implement if needed
    }

    @Override
    public void unplug() {
        // Implement if needed
    }

    @Override
    public void turnOn() {
        // Implement if needed
    }

    @Override
    public void turnOff() {
        // Implement if needed
    }

    @Override
    public boolean deregister(IDeviceListener listener) {
        return false;
    }

    @Override
    public void deregisterAll() {

    }

    @Override
    public void register(IDeviceListener listener) {

    }

    @Override
    public void disable() {
        // Implement if needed
    }

    @Override
    public void enable() {
        // Implement if needed
    }

    @Override
    public boolean isDisabled() {
        return false;
    }

    @Override
    public List<IDeviceListener> listeners() {
        return null;
    }
}

// Stub #2
private static class MockElectronicScale implements IElectronicScale{

    @Override
    public boolean isPluggedIn() {
        return false;
    }

    @Override
    public boolean isPoweredUp() {
        return false;
    }

    @Override
    public void plugIn(PowerGrid grid) {

    }

    @Override
    public void unplug() {

    }

    @Override
    public void turnOn() {

    }

    @Override
    public void turnOff() {

    }

    @Override
    public boolean deregister(ElectronicScaleListener listener) {
        return false;
    }

    @Override
    public void deregisterAll() {

    }

    @Override
    public void register(ElectronicScaleListener listener) {

    }

    @Override
    public void disable() {

    }

    @Override
    public void enable() {

    }

    @Override
    public boolean isDisabled() {
        return false;
    }

    @Override
    public List<ElectronicScaleListener> listeners() {
        return null;
    }

    @Override
    public Mass getMassLimit() {
        return null;
    }

    @Override
    public Mass getSensitivityLimit() {
        return null;
    }

    @Override
    public void addAnItem(Item item) {

    }

    @Override
    public void removeAnItem(Item item) {

    }
}}


