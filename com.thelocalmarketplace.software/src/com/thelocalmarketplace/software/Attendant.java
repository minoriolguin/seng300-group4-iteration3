package com.thelocalmarketplace.software;
import com.jjjwelectronics.Mass;

public class Attendant implements WeightDiscrepancyListener {

    Software software;
    public Attendant(Software software){
        this.software = software;
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
    public void OverRideWeightDiscrepancy(){
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

    @Override
    public void RemoveItemFromScale() {
    }

    @Override
    public void AddItemToScale() {

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
}
