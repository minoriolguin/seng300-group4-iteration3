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
