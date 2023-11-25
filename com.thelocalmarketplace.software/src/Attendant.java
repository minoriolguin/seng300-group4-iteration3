

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
