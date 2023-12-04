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

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.jjjwelectronics.card.InvalidPINException;
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
	
	public void buttonR4_CustomerAddsOwnBag() {
        System.out.println("buttonR4_CustomerAddsOwnBag"); 
        //Logic Here
	}
	
	public void buttonR5_CustomerWantstoRemoveItem() {
        System.out.println("buttonR5_CustomerWantstoRemoveItem"); 
        //Logic Here
	}
	
	public void buttonR6_BLANK() {
        System.out.println("buttonR6_BLANK");       
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
	public void PayBanknoteValFive() {

	}

	public void PayBanknoteValTen() {
	}

	public void PayBanknoteValTwenty() {
	}

	public void PayBanknoteValFifty() {
	}

	public void PayBanknoteValHundred() {
	}
}

