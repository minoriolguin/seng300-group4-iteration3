// Project 2 Iteration Group 3
//Julie Kim 10123567
//Aryaman Sandhu 30017164
//Arcleah Pascual 30056034
//Aoi Ueki 30179305
//Ernest Shukla 30156303
//Shawn Hanlon 10021510
//Jaimie Marchuk 30112841
//Sofia Rubio 30113733
//Maria Munoz 30175339
//Anne Lumumba 30171346
//Nathaniel Dafoe 30181948

package com.thelocalmarketplace.GUI;

//
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;

import javax.swing.SwingUtilities;

import com.jjjwelectronics.Mass;
import com.jjjwelectronics.card.ICardReader;
import com.jjjwelectronics.scale.IElectronicScale;
import com.jjjwelectronics.scanner.IBarcodeScanner;
import com.tdc.banknote.BanknoteValidator;
import com.tdc.coin.CoinValidator;
import com.thelocalmarketplace.hardware.BarcodedProduct;

public class DemoHere {

    public IElectronicScale baggingAreaScale;
    public IBarcodeScanner handHeldScanner;
    public IBarcodeScanner mainScanner;
    public BanknoteValidator banknoteValidator;
    public CoinValidator coinValidator;
    public ICardReader cardReader;
    // add instances of your class here then initialize below
//    public WeightDiscrepancy weightDiscrepancy;
//    public TouchScreen touchScreen;
//    public Attendant attendant;
//    public PayByBanknote payByBanknote;
//
//    public UpdateCart updateCart;

    public Mass allowableBagWeight;

	private ArrayList<BarcodedProduct> barcodedProductsInOrder;
	private ArrayList<BarcodedProduct> baggedProducts;

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
	
	
    //For Testing Purposes - to run GUI (main)
    public static void main(String[] args) {
    	
    	//To open GUI 
        SwingUtilities.invokeLater(() -> {
            RunGUI GUIframe = new RunGUI();
            GUIframe.setTitle("Welcome Screen");
        });
    }

}
