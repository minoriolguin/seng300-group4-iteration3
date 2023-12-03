package com.thelocalmarketplace.software;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Scanner;

import com.jjjwelectronics.Mass;
import com.tdc.CashOverloadException;
import com.tdc.DisabledException;
import com.tdc.banknote.Banknote;

public class Attendant implements WeightDiscrepancyListener {

    public Software software;
    public ArrayList<String> notifs;
    public boolean reusableBagsEmpty;
    /**
     * A boolean variable that keeps track of whether a customer has been attended to.
     * 
     * Is true if no customer has requested it.
     * Becomes false when a customer requests for an attendant.
     * Goes back to true after the customer has gotten their help.
     **/
    private boolean attended = true;
    
    public Attendant(Software software){
        this.software = software;
        reusableBagsEmpty = false;
        notifs = new ArrayList<>();
    }
	
	/**
	 * Adds messages to notifs list that requires the attendant's attention
	 * @param issues, Arraylist of string
	 */
	public void addressMaintenanceIssues(ArrayList<String> issues) {
		for (String s : issues) {
			notifs.add(s);
		}
	}
	
    public void reusableBagsEmpty(){
        reusableBagsEmpty = true;
    }
    
	/**
	 * Method for getting notifications for Attendant
	 * @return Arraylist of strings
	 */
    public ArrayList<String> getNotifs() {
		return notifs;
	}

	public void notifySkipBagging(){
        software.unblockCustomer();
        software.touchScreen.skip = false;
    }

    public void verifyItemInBaggingArea(){
        software.unblockCustomer();
    }
    public void verifyItemRemovedFromOrder(){
        software.unblockCustomer();
    }
    public void overRideWeightDiscrepancy(){
        software.setExpectedTotalWeight(software.weightDiscrepancy.overRideWeight);
        software.weightDiscrepancy.isWeightDiscrepancy(software.getExpectedTotalWeight());
    }
    
    
    public void disableCustomerStation() {
    	software.blockCustomerStation();
        // Handle the scenario when disabling is immediate or pending
        if (software.isPendingMaintenance()) {
            System.out.println("Disabling is set to pending until the current session ends.");
        } else {
            System.out.println("Station is disabled for maintenance.");
        }
    }
    
    /**
     *  Enables the customer station software and hardware after being blocked
     *  Precondition: Customer station must be blocked
     **/
    public void enableCustomerStation() {
    	if (software.isCustomerStationBlocked()) {
    		software.unblockCustomerStation();
    	}
    	else { System.out.println("\nCustomer station is not currently blocked.\n"); }
    }


    @Override
    public void weightOverLimit() {

    }

    @Override
    public void noDiscrepancy() {

    }

    @Override
    public void bagsTooHeavy() {
        software.weightDiscrepancy.massOfOwnBags = Mass.ZERO;
    }
    
    /**
     * Prompts the attendant to respond to the customer who signaled for help
     * 
     * For testing: while the respondent has not attended to the customer, the boolean
     * "attended" remains false.
     * 
     * Unclear point: I am not sure attendant has their own touch screen or will be using
     * a command line kind of interface. 
     * For this code, I just used if attended == true until I have the concrete version
     * of how the attendant will be interacted with.
     * 
     * Update: After seeing the message from the GUI Team, I have updated the code and
     * added a scanner.
     **/
    public void respondToCustomer() {
    	Scanner scanner = new Scanner(System.in);
    	notifs.add("A customer needs your help");    	
    	
    	
    	while (true) {
    		System.out.println("\nHave you attended to them?\n");
            String response = scanner.nextLine();

            if (response.equalsIgnoreCase("Yes")) {
            	attended = true;
        		software.setNeedsAttentionToFalse();
        		break;
            } else if (response.equalsIgnoreCase("No")) {
            	System.out.println("Please attend to the customer and "
            			+ "then enter 'Yes' to continue.");
            } else {
                System.out.println("Invalid response. Please enter Yes or No.");
            }
        }
    }
    
    public void setAttendedToFalse() {
    	attended = false;
    }
    
	@Override
	public void RemoveItemFromScale() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void AddItemToScale() {
		// TODO Auto-generated method stub
		
	}
	/** 
	 * Method for attendant to refill bank notes till full capacity
	 * **/
	public void refillBankNotes() {
		Banknote banknote = new Banknote(Currency.getInstance("CAD"),new BigDecimal("5.00"));
		while(software.getBankNoteStorage().hasSpace()) {
			try {
				software.getBankNoteStorage().receive(banknote);
			} catch (DisabledException | CashOverloadException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	/** 
	 * Method for attendant to empty all bank notes in the station storage
	 * **/
	public void emptyBankNotes() {
		software.getBankNoteStorage().unload();
		System.out.println(software.getBankNoteStorage().getBanknoteCount());
	}
}
