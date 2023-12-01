package com.thelocalmarketplace.software;
import com.jjjwelectronics.Mass;
import com.jjjwelectronics.OverloadedDevice;

public class Attendant implements WeightDiscrepancyListener {

    private final Software software;
    public boolean reusableBagsEmpty;
    public Attendant(Software software){
        this.software = software;
        reusableBagsEmpty = false;
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

    public void reusableBagsEmpty() {
        reusableBagsEmpty = true;
    }
    public void fillReusableBags(int amount) throws OverloadedDevice {
        software.purchaseBags.addBagsToDispenser(amount);
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
