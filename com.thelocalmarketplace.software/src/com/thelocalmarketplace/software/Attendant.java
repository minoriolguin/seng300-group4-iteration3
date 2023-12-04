/**
 * Arvin Bolbolanardestani / 30165484
Anthony Chan / 30174703
Marvellous Chukwukelu / 30197270
Farida Elogueil / 30171114
Ahmed Elshabasi / 30188386
Shawn Hanlon / 10021510
Steven Huang / 30145866
Nada Mohamed / 30183972
Jon Mulyk / 30093143
Althea Non / 30172442
Minori Olguin / 30035923
Kelly Osena / 30074352
Muhib Qureshi / 30076351
Sofia Rubio / 30113733
Muzammil Saleem / 30180889
Steven Susorov / 30197973
Lydia Swiegers / 30174059
Elizabeth Szentmiklossy / 30165216
Anthony Tolentino / 30081427
Johnny Tran / 30140472
Kaylee Xiao / 30173778
 */
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
        if (software.isPendingMaintenance()) {
            System.out.println("Maintenance pending: Station disabling after current session.");
        } else if (software.isCustomerStationBlocked()) {
            System.out.println("Station immediately disabled for maintenance.");
        } else {
            System.out.println("Station disabling request has been processed.");
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

