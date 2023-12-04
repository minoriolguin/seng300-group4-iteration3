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

import java.util.ArrayList;
import java.util.List;


import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.hardware.PLUCodedProduct;
import com.thelocalmarketplace.hardware.PriceLookUpCode;
import com.thelocalmarketplace.hardware.external.ProductDatabases;
import com.thelocalmarketplace.software.Attendant;
import com.thelocalmarketplace.software.TouchScreen;

/*
 * This is where Project 3 Logic will be entered 
 * Please do not insert Logic into RunGUI (which should only contain
 * GUI code) and Panels
 */
public class GUILogic {

	public TouchScreen screen;
	private Attendant attendant;
	private RunGUI gui;
	public GUILogic(TouchScreen t) {
		this.screen = t;
		attendant = new Attendant(t.getSoftware());
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
	
	//signal for attendant to help the customer and set attended to false--> attendant window opens
	public void buttonR5_CustomerWantstoRemoveItem() {
        System.out.println("buttonR5_CustomerWantstoRemoveItem"); 
        screen.signalForAttendant();
        attendant.setAttendedToFalse();
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
        PriceLookUpCode pluCode = new PriceLookUpCode(EnterPLU.textPLUcode);
       
        PLUCodedProduct found = ProductDatabases.PLU_PRODUCT_DATABASE.get(pluCode);
        long price = found.getPrice();
        String description = found.getDescription();
        screen.selectAddPLUProduct(pluCode);
        
        return description;
		
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
	public void buttonR9_CustomerWantsToPay() {
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
        // Logic Here 
        String addItemB1_result = "New Barcoded Product thru Main Scanner";
		return addItemB1_result;
	}
	
	public String buttonB2_CustomerScansBarcodedProduct_HandheldScanner() {
        System.out.println("buttonB2_CustomerScansBarcodedProduct_HandheldScanner");
        //Example Code Here 
        // Logic Here 
        String addItemB2_result = "New Barcoded Product thru Handheld Scanner";
		return addItemB2_result;
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
		System.out.println("The total is "+total);
		return total;
	}
	
	public void addtoTotal(int meow) {
		total = total + meow;
		System.out.println("The total is "+total);
	}
	
	public void subtractTotal(int meow) {
		total = total - meow;
		System.out.println("The total is "+total);
	}
}

