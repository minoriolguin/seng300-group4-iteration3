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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;

import javax.swing.SwingUtilities;

import com.tdc.coin.Coin;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.hardware.SelfCheckoutStationBronze;
import com.thelocalmarketplace.hardware.SelfCheckoutStationGold;
import com.thelocalmarketplace.hardware.SelfCheckoutStationSilver;
import com.thelocalmarketplace.software.PopulateProductDatabases;

public class test {

	public static void main(String[] args) {
		HardwareConfig hardwareConfig = new HardwareConfig();
		PopulateProductDatabases.populateDatabases();
		SelfCheckoutStationBronze bronze = new SelfCheckoutStationBronze();
		SelfCheckoutStationGold	gold = new SelfCheckoutStationGold();
		SelfCheckoutStationSilver silver= new SelfCheckoutStationSilver();
	    	//To open GUI 
        SwingUtilities.invokeLater(new Runnable()  {
        	
			@Override
			public void run() {
				// TODO Auto-generated method stub
				SelfCheckoutSimulation simulation = new SelfCheckoutSimulation();
			}
    });
	}
}
