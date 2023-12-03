//Arvin Bolbolanardestani / 30165484
//Anthony Chan / 30174703
//Marvellous Chukwukelu / 30197270
//Farida Elogueil / 30171114
//Ahmed Elshabasi / 30188386
//Shawn Hanlon / 10021510
//Steven Huang / 30145866
//Nada Mohamed / 30183972
//Jon Mulyk / 30093143
//Althea Non / 30172442
//Minori Olguin / 30035923
//Kelly Osena / 30074352
//Muhib Qureshi / 30076351
//Sofia Rubio / 30113733
//Muzammil Saleem / 30180889
//Steven Susorov / 30197973
//Lydia Swiegers / 30174059
//Elizabeth Szentmiklossy / 30165216
//Anthony Tolentino / 30081427
//Johnny Tran / 30140472
//Kaylee Xiao / 30173778 


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
