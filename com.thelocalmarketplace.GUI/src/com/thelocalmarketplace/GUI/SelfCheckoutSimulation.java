package com.thelocalmarketplace.GUI;

import java.awt.FlowLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.tdc.banknote.BanknoteValidator;
import com.tdc.coin.Coin;
import com.tdc.coin.CoinValidator;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.hardware.SelfCheckoutStationBronze;
import com.thelocalmarketplace.hardware.SelfCheckoutStationGold;
import com.thelocalmarketplace.hardware.SelfCheckoutStationSilver;
import com.thelocalmarketplace.software.Software;
import com.thelocalmarketplace.software.TouchScreen;

public class SelfCheckoutSimulation extends JFrame {
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
	    private SelfCheckoutStationSilver silver_station;
	 public SelfCheckoutSimulation(SelfCheckoutStationBronze b, SelfCheckoutStationGold g, SelfCheckoutStationSilver s ) {
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
	        
	        bronze_station = b;
	        gold_station = g;
	        silver_station = s;
	        
	        // Set frame properties
	        setTitle("Selfcheckout Station Simulation");
	        setSize(500, 500);
	        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	        // Create a panel with a FlowLayout to center the components
	        JPanel panel = new JPanel((LayoutManager) new FlowLayout(FlowLayout.CENTER));

	        // Create three buttons
	        String[] buttonTitles = {"Bronze Station",
					"Gold Station",
					"Silver Station"
					 };

	        // Array of button listeners
	        ActionListener[] buttonListeners = {
	            e -> handleButtonClick(1),
	            e -> handleButtonClick(2),
	            e -> handleButtonClick(3),
	            
	        };

	        // Add buttons to the bottom panel with titles and listeners
	        for (int i = 0; i < buttonTitles.length; i++) {
	            JButton button = new JButton(buttonTitles[i]);
	            button.addActionListener(buttonListeners[i]);
	            panel.add(button);
	        }
	   

	        // Add panel to the frame
	        add(panel);

	        // Set frame visibility
	        setVisible(true);
	    }
	 
	 private void handleButtonClick(int buttonNumber) {
	        switch (buttonNumber) {
	            case 1:
	                System.out.println("Meow");
	             
	                Software software = new Software(bronze_station);
	                software.turnOn();
	                TouchScreen touchscreen = new TouchScreen(software);
	                GUILogic guiLogic = new GUILogic(touchscreen);
	                RunGUI gui = new RunGUI(guiLogic); 
	                break;
	            case 2:
	                System.out.println("Button Clicked");
	                System.out.println("silver");
	                Software softwareS= new Software(silver_station);
	                softwareS.turnOn();
	                TouchScreen touchscreenS= new TouchScreen(softwareS);
	                GUILogic guiLogicS = new GUILogic(touchscreenS);
	                RunGUI guiS = new RunGUI(guiLogicS); 
	                break;
	            case 3:
	                System.out.println("Button Clicked");
	                System.out.println("gold");
	                Software softwareG= new Software(gold_station);
	                softwareG.turnOn();
	                TouchScreen touchscreenG= new TouchScreen(softwareG);
	                GUILogic guiLogicG = new GUILogic(touchscreenG);
	                RunGUI guiG = new RunGUI(guiLogicG);
	                break;
	            
	        }
	       }
}
