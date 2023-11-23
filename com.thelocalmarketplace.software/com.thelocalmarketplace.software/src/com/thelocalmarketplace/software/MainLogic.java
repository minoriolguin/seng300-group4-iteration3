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


package com.thelocalmarketplace.software;

import com.jjjwelectronics.IDevice;
import com.jjjwelectronics.scanner.Barcode;
import com.tdc.IComponent;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.external.ProductDatabases;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class MainLogic {
    public AbstractSelfCheckoutStation checkoutStation;
    private ProductDatabases productDatabase;
    public Session currentSession;
    private ArrayList<Object> hardwareList;


    /**
     * Install software on checkout station
     *
     * @param station
     *              The self-checkout machine on which to install the software
     *
     * More paramters and methods will be necessary when GUI is implemented.
     */
    public static MainLogic installOn(AbstractSelfCheckoutStation station) {
        return new MainLogic(station);
    }

    /**
     * Constructor
     */
    public MainLogic(AbstractSelfCheckoutStation station) {
        checkoutStation = station;
        hardwareList = new ArrayList<>();
        for (Object field : station.getClass().getDeclaredFields()) {
            if (field instanceof IDevice || field instanceof IComponent) {
                hardwareList.add(field);
            }
        }
        currentSession = new Session(hardwareList);
        currentSession.freezeSession();
    }
}
