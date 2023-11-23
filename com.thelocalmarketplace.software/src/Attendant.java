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

import com.jjjwelectronics.Mass;

public class Attendant implements WeightDiscrepancyListener {

    SelfCheckoutSoftware checkout;
    public Attendant(SelfCheckoutSoftware checkout){
        this.checkout = checkout;
    }
    public void notifySkipBagging(){
        checkout.unblockCustomer();
    }

    public void verifyItemInBaggingArea(){
        checkout.unblockCustomer();
    }
    public void verifyItemRemovedFromOrder(){
        checkout.unblockCustomer();
    }
    public void OverRideWeightDiscrepancy(){
        checkout.setExpectedTotalWeight(checkout.weightDiscrepancy.overRideWeight);
        checkout.weightDiscrepancy.isWeightDiscrepancy(checkout.getExpectedTotalWeight());
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
        checkout.weightDiscrepancy.massOfOwnBags = Mass.ZERO;
    }
}
