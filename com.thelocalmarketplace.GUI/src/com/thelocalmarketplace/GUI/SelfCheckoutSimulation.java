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

import com.jjjwelectronics.OverloadedDevice;
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

		private SelfCheckoutStationGold hardware;
		private Software software;

	 public SelfCheckoutSimulation() {
		 	

	        
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
	            e -> {
					try {
						handleButtonClick(1);
					} catch (OverloadedDevice ex) {
						throw new RuntimeException(ex);
					}
				},
	            e -> {
					try {
						handleButtonClick(2);
					} catch (OverloadedDevice ex) {
						throw new RuntimeException(ex);
					}
				},
	            e -> {
					try {
						handleButtonClick(3);
					} catch (OverloadedDevice ex) {
						throw new RuntimeException(ex);
					}
				},
	            
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
	 
	 private void handleButtonClick(int buttonNumber) throws OverloadedDevice {
	        switch (buttonNumber) {
	            case 1:
	                System.out.println("Start Session");
					CreateThread thread = new CreateThread(1);
					Thread newThread = new Thread(thread);
					newThread.start();
					//bronze_station.getScreen().getFrame().setContentPane(gui);
	            case 2:
	                System.out.println("Button Clicked");
	                CreateThread thread2 = new CreateThread(2);
					Thread newThread2 = new Thread(thread2);
					newThread2.start();
	                break;
	            case 3:
	                System.out.println("Button Clicked");
	                CreateThread thread3 = new CreateThread(3);
					Thread newThread3 = new Thread(thread3);
					newThread3.start();
	                break;
	            
	        }
	       }
}
