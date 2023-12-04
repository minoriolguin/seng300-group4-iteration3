package com.thelocalmarketplace.GUI;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;

import javax.swing.SwingUtilities;

import com.tdc.coin.Coin;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.hardware.SelfCheckoutStationBronze;
import com.thelocalmarketplace.hardware.SelfCheckoutStationGold;
import com.thelocalmarketplace.hardware.SelfCheckoutStationSilver;

public class test {

	public static void main(String[] args) {
		HardwareConfig hardwareConfig = new HardwareConfig();
		SelfCheckoutStationBronze bronze = new SelfCheckoutStationBronze();
		SelfCheckoutStationGold	gold = new SelfCheckoutStationGold();
		SelfCheckoutStationSilver silver= new SelfCheckoutStationSilver();
	    	//To open GUI 
        SwingUtilities.invokeLater(() -> {
        	
            SelfCheckoutSimulation simulation = new SelfCheckoutSimulation(bronze,gold,silver);
    });
	}
}
