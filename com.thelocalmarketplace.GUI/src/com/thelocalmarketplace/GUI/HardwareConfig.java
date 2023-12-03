package com.thelocalmarketplace.GUI;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;

import com.tdc.coin.Coin;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.hardware.PLUCodedProduct;
import com.thelocalmarketplace.hardware.PriceLookUpCode;
import com.thelocalmarketplace.hardware.SelfCheckoutStationBronze;
import com.thelocalmarketplace.hardware.SelfCheckoutStationGold;
import com.thelocalmarketplace.hardware.SelfCheckoutStationSilver;
import com.thelocalmarketplace.hardware.external.ProductDatabases;

public class HardwareConfig {
	private ArrayList<BigDecimal> coindenominations;
    private Currency CAD;
    private BigDecimal[] billDenominations;

    private static final Currency CAD_Currency = Currency.getInstance("CAD");
    private static final BigDecimal value_toonie = new BigDecimal("2.00");
    private static final BigDecimal value_loonie = new BigDecimal("1.00");
    private static final BigDecimal value_quarter = new BigDecimal("0.25");
    private static final BigDecimal value_dime = new BigDecimal("0.10");
    private static final BigDecimal value_nickel = new BigDecimal("0.05");
    private static final BigDecimal value_penny = new BigDecimal("0.01");

    private Coin coin_toonie = new Coin(CAD_Currency,value_toonie);
    private Coin coin_loonie = new Coin(CAD_Currency,value_loonie);
    private Coin coin_quarter = new Coin(CAD_Currency,value_quarter);
    private Coin coin_dime = new Coin(CAD_Currency,value_dime);
    private Coin coin_nickel = new Coin(CAD_Currency,value_nickel);
    private Coin coin_penny = new Coin(CAD_Currency,value_penny);
    private SelfCheckoutStationBronze bronze_station;
    private SelfCheckoutStationGold gold_station;
    private SelfCheckoutStationSilver silver_staiton;
    
    
    public HardwareConfig() {
    	PriceLookUpCode appleCode = new PriceLookUpCode("12345");
    	PLUCodedProduct appleProduct = new PLUCodedProduct(appleCode,"apple",2);
    	ProductDatabases.PLU_PRODUCT_DATABASE.put(appleCode, appleProduct);
    	coindenominations = new ArrayList<BigDecimal>();
        CAD = Currency.getInstance("CAD");
        coindenominations.add(value_toonie);
        coindenominations.add(value_loonie);
        coindenominations.add(value_quarter);
        coindenominations.add(value_dime);
        coindenominations.add(value_nickel);
        coindenominations.add(value_penny);
        

        billDenominations = new BigDecimal[5];
        billDenominations[0] = new BigDecimal("5.00");
        billDenominations[1] = new BigDecimal("10.00");
        billDenominations[2] = new BigDecimal("20.00");
        billDenominations[3] = new BigDecimal("50.00");
        billDenominations[4] = new BigDecimal("100.00");
        
        Currency c = Currency.getInstance("CAD");
        BigDecimal[] billDenom = { new BigDecimal("5.00"),
                new BigDecimal("10.00"),
                new BigDecimal("20.00"),
                new BigDecimal("50.00"),
                new BigDecimal("100.00")};
        BigDecimal[] coinDenom = { new BigDecimal("0.01"),
                new BigDecimal("0.05"),
                new BigDecimal("0.1"),
                new BigDecimal("0.25"),
                new BigDecimal("1"),
                new BigDecimal("2") };

        AbstractSelfCheckoutStation.resetConfigurationToDefaults();
        AbstractSelfCheckoutStation.configureCurrency(c);
        AbstractSelfCheckoutStation.configureBanknoteDenominations(billDenom);
        AbstractSelfCheckoutStation.configureCoinDenominations(coinDenom);
        AbstractSelfCheckoutStation.configureReusableBagDispenserCapacity(20);
        AbstractSelfCheckoutStation.configureBanknoteStorageUnitCapacity(20);
        AbstractSelfCheckoutStation.configureCoinStorageUnitCapacity(20);
        AbstractSelfCheckoutStation.configureCoinTrayCapacity(20);
    }
}
