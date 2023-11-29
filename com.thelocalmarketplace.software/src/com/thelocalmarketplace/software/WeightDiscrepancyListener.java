package com.thelocalmarketplace.software;


public interface WeightDiscrepancyListener {
    void RemoveItemFromScale();
    void AddItemToScale();
    void weightOverLimit();
    void noDiscrepancy();
    void bagsTooHeavy();
}
