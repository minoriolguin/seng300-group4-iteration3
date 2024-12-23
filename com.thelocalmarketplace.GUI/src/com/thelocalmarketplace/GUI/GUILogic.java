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

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.jjjwelectronics.EmptyDevice;
import com.jjjwelectronics.card.InvalidPINException;
import com.tdc.NoCashAvailableException;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.software.TouchScreen;

/*
 * This is where Project 3 Logic will be entered 
 * Please do not insert Logic into RunGUI (which should only contain
 * GUI code) and Panels
 */
public class GUILogic {
	public TouchScreen screen;
	public GUILogic(TouchScreen t) {
		this.screen = t;
	}
//----------------------------------------------------------------
//Start Session Panel, 
	
	//when Customer presses [Start Session] 
	public void StartSessionButtonPressed() {
		System.out.println("Start Session");
	}
	
//----------------------------------------------------------------
//Add Items Panel 
	
	// RIGHT BUTTON PANEL 
	public void buttonR1_AddMemberNoButton() {
        System.out.println("Add Member #");
        //Logic Here
	}
	
	public void buttonR2_SignUpForMembershipButton() {
        System.out.println("Sign Up For Membership");
        //Logic Here
	}
	
	public void buttonR3_CustomerCallsAttendant() {
        System.out.println("CustomerCallsAttendant");
        //Logic Here
        screen.signalForAttendant();
        AttendantFrame attendant_frame = new AttendantFrame(screen);
        attendant_frame.show();
	}
	
	public void buttonR4_CustomerAddsBag() {
        System.out.println("buttonR4_CustomerAddsBag"); 
        //Logic Here
        screen.selectAddOwnBags();
        try {
			screen.purchaseBags(total);
		} catch (EmptyDevice e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        screen.selectBagsAdded();
        BagPanel bag_panel = new BagPanel(screen);
        bag_panel.show();
	}
	
	public void buttonR5_CustomerWantstoRemoveItem() {
        System.out.println("buttonR5_CustomerWantstoRemoveItem"); 
        //Logic Here
	}
	
	public void buttonR6_CustomerSelectsLanguage() {
        System.out.println("buttonR6_CustomerSelectsLanguage");       
        //Logic Here
	}
	
	/*
	 * return the string that will be displayed in GUI "Receipt"
	 */
	public String buttonR7_CustomerAddsItem_PLUCode() {
        System.out.println("buttonR7_CustomerAddsItem_PLUCode");
        //Logic Here
        //Example Code Here

        String addItemPLU_result = "New Item thru PLU Code";
		return addItemPLU_result;
	}
	
	/*
	 * return the string that will be displayed in GUI "Receipt"
	 */
	public String buttonR8_CustomerAddsItem_VisualCatalogue() {
        System.out.println("buttonR8_CustomerAddsItem_VisualCatalogue");
        //Logic Here
        //Example Code Here 
        String addItemVC_result = "New Item thru Visual Catalogue";
		return addItemVC_result;
	}
	
	// This will switch to the Payment Panel
	public void buttonR9_CustomerWantsToPay(int total) {
        System.out.println("buttonR9_CustomerWantsToPay!");




	}
	
//----------------------------------------------------------------
// Add Items Panel Pop Up 
	
	public String addItemPopUp_button1_CustomersAddsToBaggingArea() {
        System.out.println("addItemPopUp_button1_CustomersAddsToBaggingArea");
        //Example Code Here 
        // Logic Here 
        String addItemB1_result = "addItemPopUp_button1_CustomersAddsToBaggingArea";
		return addItemB1_result;
	}
	
	public String addItemPopUp_button2_CustomersDOESNOTAddsToBaggingArea() {
        System.out.println("addItemPopUp_button1_CustomersAddsToBaggingArea");
        //Example Code Here 
        // Logic Here 
        String addItemB1_result = "addItemPopUp_button1_CustomersAddsToBaggingArea";
		return addItemB1_result;
	}
	
	public String addItemPopUp_button3_BLANK() {
        System.out.println("Extra Button");
        //Example Code Here 
        // Logic Here 
        String addItemB1_result = "addItemPopUp_button3_BLANK - Extra Button";
		return addItemB1_result;
	}

//----------------------------------------------------------------
// Attendant LookUp Product Panel

	public String buttonL1_AttendantLookUpProduct_PLUCode() {
		System.out.println("buttonL1_AttendantLookUpProduct_PLUCode");
		// Example Code Here
		// Logic Here
		String addItemB1_result = "Attendant Look Up Product thru PLU Code";
		return addItemB1_result;
	}
	
//----------------------------------------------------------------
//Add Items Panel 	
	
	//BOTTOM PANEL
	public String buttonB1_CustomerScansBarcodedProduct_MainScanner() {
        System.out.println("buttonB1_CustomerScansBarcodedProduct_MainScanner");
        //Example Code Here 
        //Logic Here
		//for testing
		screen.getSoftware().addToOrderTotal(BigDecimal.TEN);

        String addItemB1_result = Integer.toString(this.getTotal());
		return addItemB1_result;
	}
	
	public String buttonB2_CustomerScansBarcodedProduct_HandheldScanner() {
        System.out.println("buttonB2_CustomerScansBarcodedProduct_HandheldScanner");
        //Example Code Here 
        // Logic Here 
        String addItemB2_result = "New Barcoded Product thru Handheld Scanner";
		return addItemB2_result;
	}
	
	public String buttonB3_CustomerScansBarcodedProduct_RFIDTag() {
        System.out.println("buttonB3_CustomerScansBarcodedProduct_RFIDTag");
        //Example Code Here 
        // Logic Here 
        String addItemB3_result = "New Barcoded Product thru RFID Tag";
		return addItemB3_result;
	}
	
	public void payment_buttonB1_CustomerPaysWithDebitSwipe(int total) throws IOException {
		screen.payViaSwipe("debit");
	}

	public void payment_buttonB2_CustomerPaysWithCreditSwipe(int total) throws IOException {
		screen.payViaSwipe("credit");
	}

	public void payment_buttonB4_CustomerPaysWithDebitTap(int total) throws IOException {
		screen.payViaTap("debit");
	}

	public void payment_buttonB5_CustomerPaysWithCreditTap(int total) throws IOException {
		screen.payViaTap("credit");
	}

	public boolean payment_CustomerPaysWithCreditInsert(int total, String PIN) throws IOException {
		try {
			screen.payViaInsert("credit", PIN);
		}
		catch (InvalidPINException e)
		{
			return false;
		}
		if (getTotal() == 0)
			return true;
		return false;
	}

	public boolean payment_CustomerPaysWithDebitInsert(int total, String PIN) throws IOException {
		screen.payViaInsert("debit", PIN);

		if (getTotal() == 0 )
			return true;
		return false;
	}

	public void payment_CustomerPaysWithCoin(BigDecimal denomination) {
		screen.addCoinToList(denomination);
	}
	
	public void payment_CustomerCompletesCoinPayment() {
		screen.payByCoin();
	}
	
	
//----------------------------------------------------------------
// FOR TESTING PURPOSES - DO NOT SUBMIT FOR PROJECT 3 

	public int total = 0; 
	
	//Observer Design 
	private List<logicObserver> observers = new ArrayList<>();
	
	// Observer Methods Below 
    public void addObserver(logicObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(logicObserver observer) {
        observers.remove(observer);
    }

    private void notifyObservers() {
        for (logicObserver observer : observers) {
            observer.updateTotal(total);
        }
    }
	
	
	// Logic Methods Below 
	public void printMeow(){
		System.out.println("Meow");
	}
	
	public void initTotal() {
		total = 0;
		System.out.println("The total is "+total);
	}
	
	public int addOneTotal() {
		total = total +1;
		System.out.println("The total is "+total);
		return total;
	}
	
	public int getTotal() {
		//System.out.println("The total is "+total);
		//return total;
		return screen.getSoftware().getOrderTotal().intValue();
	}
	
	public void addtoTotal(int meow) {
		total = total + meow;
		System.out.println("The total is "+total);
	}
	
	public void subtractTotal(int meow) {
		total = total - meow;
		System.out.println("The total is "+total);
	}

	//TODO: finish implementing banknote and coin payment
	public void PayBanknoteValFive() throws NoCashAvailableException {
		screen.insertBanknote(BigDecimal.valueOf(5));
	}

	public void PayBanknoteValTen() throws NoCashAvailableException {
		screen.insertBanknote(BigDecimal.valueOf(10));
	}

	public void PayBanknoteValTwenty() throws NoCashAvailableException {
		screen.insertBanknote(BigDecimal.valueOf(20));
	}

	public void PayBanknoteValFifty() throws NoCashAvailableException {
		screen.insertBanknote(BigDecimal.valueOf(50));
	}

	public void PayBanknoteValHundred() throws NoCashAvailableException {
		screen.insertBanknote(BigDecimal.valueOf(100));
	}

}

