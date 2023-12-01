package com.thelocalmarketplace.software;
import java.util.ArrayList;

import com.jjjwelectronics.Mass;

public class Attendant implements WeightDiscrepancyListener {

    Software software;
    ArrayList<String> notifs;
    
    public Attendant(Software software){
        this.software = software;
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
    	
    }
    
    //Enables the customer station software and hardware after being blocked
    //Precondition: Customer station must be blocked
    public void enableCustomerStation() {
    	if (software.isCustomerStationBlocked()) {
    		software.unblockCustomerStation();
    	}
    	else { System.out.println("\nCustomer station is not currently blocked.\n"); }
    }

    public void removeItemFromScale() {
    }

    public void addItemToScale() {

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
	@Override
	public void RemoveItemFromScale() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void AddItemToScale() {
		// TODO Auto-generated method stub
		
	}
}
