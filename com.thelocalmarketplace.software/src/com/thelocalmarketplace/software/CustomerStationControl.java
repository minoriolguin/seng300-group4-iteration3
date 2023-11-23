/**
 * @author Alan Yong: 30105707
 * @author Atique Muhammad: 30038650
 * @author Ayman Momin: 30192494
 * @author Christopher Lo: 30113400
 * @author Ellen Bowie: 30191922
 * @author Emil Huseynov: 30171501
 * @author Eric George: 30173268
 * @author Kian Sieppert: 30134666
 * @author Muzammil Saleem: 30180889
 * @author Ryan Korsrud: 30173204
 * @author Sukhnaaz Sidhu: 30161587
 */
package com.thelocalmarketplace.software;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import com.jjjwelectronics.EmptyDevice;
import com.jjjwelectronics.OverloadedDevice;
import com.tdc.banknote.IBanknoteDispenser;
import com.tdc.coin.ICoinDispenser;
import com.thelocalmarketplace.hardware.*;

/**
 * Control structure for actions that can be performed by a customer station.
 */
public class CustomerStationControl {
	
	private AbstractSelfCheckoutStation station;
	private Order order;
	private Boolean blocked = true;
	private Boolean sessionStarted = false;
	private Boolean paymentProcessStarted = false;
	private Boolean addingOwnBags = false;
	private Boolean addingBulkyItem = false;
	private PayCashControl payCashController;
	private PayCardControl payCardController;
	private WeightDiscrepancyControl weightDiscrepancyControl;

	//Codes used to differentiate notifications without using string comparison, allowing for
	//changes to be made to the actual message given to the customer/attendant without breaking functionality
	public String notifyDiscrepancyCode =  "discrepancy";
	public String notifyInsertPaymentCode =  "insertPayment";
	public String notifyPlaceItemInBaggingAreaCode =  "placeItemInBaggingArea";
	public String notifyOtherCode =  "other";
	public String notifyInvalidCoinCode = "invalidCoin";
	public String notifyBagsTooHeavyCode = "bagsTooHeavy";
	public String notifyNotEnoughChangeCode = "notEnoughChange";
	public String notifyPaymentSuccess = "paymentSuccess";
	public String notifyPaymentFailure = "paymentFailure";
	public String notifyInvalidBanknoteCode = "invalidBanknote";
	public String notifyFullCoinStorageCode = "coinStorageFull";
	public String notifyFullBanknoteStorageCode = "banknoteStorageFull";
	public String notifyAddOwnBagCode = "notifyAddOwnBag";
			
	/**Used for testing as signaling UI will not be implemented in this iteration.
	 *Possible values: discrepancy, insertPayment, placeItemInBaggingArea, notEnoughChange 
	 */
	private String attendantNotifyCode = "";
	/**Used for testing as signaling UI will not be implemented in this iteration.
	 *Possible values: discrepancy, insertPayment, placeItemInBaggingArea 
	 */
	private String customerNotifyCode = "";
	private String lastNotification;

	//Configurable options
	private int allowableBagWeightInMicrograms = 500000000;
	
	public CustomerStationControl(AbstractSelfCheckoutStation customerStation) {
		this.station = customerStation;
		
		//initialize weightdiscrepancycontrol
		WeightDiscrepancyControl weightDiscrepancyControl = new WeightDiscrepancyControl(this);
		customerStation.baggingArea.register(weightDiscrepancyControl);
		this.weightDiscrepancyControl = weightDiscrepancyControl;
		
		
		//initial PayViaCard
		payCardController = new PayCardControl(this);
		customerStation.cardReader.register(payCardController);
		
		//initialize PayCoin control
		payCashController = new PayCashControl(this);
		customerStation.coinValidator.attach(payCashController);
		customerStation.coinStorage.attach(payCashController);
		customerStation.banknoteValidator.attach(payCashController);
		customerStation.banknoteStorage.attach(payCashController);
		for(IBanknoteDispenser bd : station.banknoteDispensers.values()) bd.attach(payCashController);
		for(ICoinDispenser cd : station.coinDispensers.values()) cd.attach(payCashController);
		
		//register listener to station's main barcode scanner
		BarcodeScanListener mainBarcodeScanListener = new BarcodeScanListener(this);
		customerStation.mainScanner.register(mainBarcodeScanListener);
		
		//register listener to station's handheld barcode scanner
		BarcodeScanListener handheldBarcodeScanListener = new BarcodeScanListener(this);
		customerStation.handheldScanner.register(handheldBarcodeScanListener);
	}
	
	public void startSession() {
		order = new Order(this);
		sessionStarted = true;
		unblock();
	}
	
	public void notifyAttendant(String message, String code) {
		//this is presented to the attendant in the UI.
		lastNotification = ("Attendant: " + message);
		//For testing purpose while allowing formatting changes to the message given to the customer
		attendantNotifyCode = code;
	}
	
	public void notifyCustomer(String message, String code) {
		//this is presented to the customer in the UI.
		lastNotification = ("Customer: " + message);
		//For testing purpose while allowing formatting changes to the message given to the customer
		customerNotifyCode = code;
	}
	
	public void printReceipt(String receipt) {
		for(char c : receipt.toCharArray())
			try {
				station.printer.print(c);
			} catch (EmptyDevice | OverloadedDevice e) {
				e.printStackTrace();
			}
	}

	/**
	 * Programmatic interface substituting for UI implementation of 
	 * allowing customer to signal that they want to pay
	 */
	public void signalPay() {
		if(!blocked) paymentProcessStarted = true;
	}
	
	/**
	 * Programmatic interface substituting for UI implementation of 
	 * allowing customer to signal that they want to add a bulky item
	 */
	public void signalBulkyItem() {
		if(!blocked) addingBulkyItem = true;
	}
	
    /**
     * Programmatic interface substituting for UI implementation of 
     * allowing customer to signal that they want to add their own bags
     */
    public void signalAddOwnBags() {
        // Step 1: Customer signals the desire to add their own bags
        // implemented programmatically due to lack of UI (CustomerStationControl.signalBulkyItem());
        
        // flag to allow for weight discrepancies to be ignored during bag adding process
        addingOwnBags = true;
    	
        // Step 2: System indicates that the customer should add their own bags now
    	// this will be done through UI in the future
    	notifyCustomer("Please add your own bags to the bagging area.", notifyAddOwnBagCode);
        
        // Step 3: Customer signals that the bags have been added
    	// this is detected automatically by the weight change on the scale in the bagging area
       
        // Step 4: System detects the weight change
        // Step 5: Bags Too Heavy extension point
        // Step 6: System signals to the customer that they may now continue
    	// All of the above are handled by theMassOnTheScaleHasChanged event in the WeightDiscrepancyControl
    }

	
    //Setters and getters for various fields
	public Order getOrder() {
		return order;
	}
    
	public BigDecimal[] getBanknoteDenominations() {
		return station.banknoteDenominations;
	}

	public List<BigDecimal> getCoinDenominations() {
		return station.coinDenominations;
	}

	public Map<BigDecimal, ICoinDispenser> getCoinDispensers() {
		return station.coinDispensers;
	} 

	public Map<BigDecimal, IBanknoteDispenser> getBanknoteDispensers() {
		return station.banknoteDispensers;

	} 
	
	public String getCustomerNotified() {
	    return customerNotifyCode;
	}
	
	public String getAttendantNotified() {
	    return attendantNotifyCode;
	}
	
	public String getLastNotification() {
		return lastNotification;
	}
	
	public void block() {
		blocked = true;
	}
	
	public void unblock() {
		blocked = false;
	}
	
	public Boolean isBlocked() {
		return blocked;
	}

	public Boolean getSessionStarted() {
		return sessionStarted;
	}
	
	public Boolean paymentProcessStarted() {
		return paymentProcessStarted;
	}
	
	public WeightDiscrepancyControl getBaggingAreaScaleListener() {
		return weightDiscrepancyControl;
	}
	
	public PayCashControl getPayCashController() {
		return payCashController;
	}

	public PayCardControl getPayCardController() {
		return payCardController;
	}
	
	public int getAllowableBagWeightInGrams() {
		return allowableBagWeightInMicrograms;
	}

	public void setAllowableBagWeightInGrams(int allowableBagWeightInGrams) {
		this.allowableBagWeightInMicrograms = allowableBagWeightInGrams;
	}

	public Boolean getAddingOwnBags() {
		return addingOwnBags;
	}

	public void setAddingOwnBags(Boolean addingOwnBags) {
		this.addingOwnBags = addingOwnBags;
	}

	public Boolean getAddingBulkyItem() {
		return addingBulkyItem;
	}

	public void setAddingBulkyItem(Boolean addingBulkyItem) {
		this.addingBulkyItem = addingBulkyItem;
	}

	public WeightDiscrepancyControl getWeightDiscrepancyControl() {
		return weightDiscrepancyControl;
	}


}



