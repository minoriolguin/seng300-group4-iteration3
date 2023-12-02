package com.thelocalmarketplace.software;
import java.util.ArrayList;

import com.jjjwelectronics.Mass;

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
     **/
    public void respondToCustomer() {
    	notifs.add("A customer needs your help");    	
    	System.out.println("\nHave you attended to them?\n");
    	
    	if (attended == true) {
    		attended = true;
    		software.setNeedsAttentionToFalse();
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
}
