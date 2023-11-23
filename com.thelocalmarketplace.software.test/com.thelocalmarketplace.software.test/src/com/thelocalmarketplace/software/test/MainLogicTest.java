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

import com.jjjwelectronics.Mass;
import com.jjjwelectronics.Numeral;
import com.jjjwelectronics.scanner.*;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.hardware.SelfCheckoutStationBronze;
import com.thelocalmarketplace.software.MainLogic;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import powerutility.PowerGrid;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.Assert.*;

public class MainLogicTest {
    private static SelfCheckoutStationBronze checkoutStation;
    public static PowerGrid grid = PowerGrid.instance();

    @BeforeClass
    public static void setUpStaticStuff() {
        BigDecimal[] myDenominations = new BigDecimal[2];
        myDenominations[0] = new BigDecimal(1);
        myDenominations[1] = new BigDecimal(2);
        AbstractSelfCheckoutStation.configureCurrency(Currency.getInstance(Locale.CANADA));
        AbstractSelfCheckoutStation.configureCoinDenominations(myDenominations);
        AbstractSelfCheckoutStation.configureBanknoteDenominations(myDenominations);
        AbstractSelfCheckoutStation.configureBanknoteStorageUnitCapacity(5);
        AbstractSelfCheckoutStation.configureCoinDispenserCapacity(5);
        AbstractSelfCheckoutStation.configureCoinStorageUnitCapacity(5);
        AbstractSelfCheckoutStation.configureCoinTrayCapacity(5);
        AbstractSelfCheckoutStation.configureReusableBagDispenserCapacity(5);
    }

    @Before
    public void setUpInstallation() {
        checkoutStation = new SelfCheckoutStationBronze();
        MainLogic.installOn(checkoutStation);
        checkoutStation.plugIn(grid);
        checkoutStation.turnOn();
    } 

    @Test
    public void testStation() {
        Numeral[] code = new Numeral[3];
        Arrays.fill(code, Numeral.valueOf((byte)1));
        Barcode barcode = new Barcode(code);
        BarcodedItem myItem = new BarcodedItem(barcode, new Mass(10));
        assertTrue(checkoutStation.baggingArea.isPoweredUp());
        assertFalse(checkoutStation.banknoteValidator.isDisabled());
    }
}
