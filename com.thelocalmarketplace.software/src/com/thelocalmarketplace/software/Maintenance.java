package com.thelocalmarketplace.software;

import java.math.BigDecimal;
import java.util.ArrayList;

import com.jjjwelectronics.IDevice;
import com.jjjwelectronics.IDeviceListener;
import com.jjjwelectronics.OverloadedDevice;
import com.jjjwelectronics.printer.ReceiptPrinterListener;
import com.tdc.CashOverloadException;
import com.tdc.DisabledException;
import com.tdc.IComponent;
import com.tdc.IComponentObserver;
import com.tdc.NoCashAvailableException;
import com.tdc.coin.Coin;
import com.tdc.coin.CoinDispenserObserver;
import com.tdc.coin.CoinStorageUnit;
import com.tdc.coin.CoinStorageUnitObserver;
import com.tdc.coin.ICoinDispenser;

import ca.ucalgary.seng300.simulation.NullPointerSimulationException;
import ca.ucalgary.seng300.simulation.SimulationException;

/**
 * Handles maintenance of hardware ink, paper, coins, and banknotes status
 * 
 * Any issues that requires an Attendant's attention is stored as String
 * type in the Arraylist `issues` to simulate maintenance codes.
 * 
 */
public class Maintenance implements ReceiptPrinterListener, CoinDispenserObserver, CoinStorageUnitObserver {
    private Software software;
    private int inkRemaining;
    private int averagePaperUsedPerSession;
	private int averageInkUsagePerSession;
	private int remainingPaper;
    
    // Specs
    public static final int MAXIMUM_INK = 1 << 20;
    public int lowInkLevel = (int)(MAXIMUM_INK * 0.1);
    public static final int MAXIMUM_PAPER = 1 << 10;
    public int lowPaperLevel = (int)(MAXIMUM_PAPER * 0.1);
    
    ArrayList<String> issues;

	// String messages that simulate maintenance codes
    String outOfInkMsg = "PRINTER_OUT_OF_INK";
    String lowInkMsg = "PRINTER_LOW_INK";
    String lowInkSoonMsg = "PRINTER_LOW_INK_SOON";
    String lowCoinsSoonDisp = "COIN_DISPENSER_LOW_COINS";
    String dispAlmostFull = "COIN_DISPENSER_ALMOST_FULL";
    String storAlmostFull = "COIN_STORAGE_ALMOST_FULL";
    String outOfPaperMsg = "PRINTER_OUT_OF_PAPER";
    String lowPaperMsg = "PRINTER_LOW_PAPER";
    String lowPaperSoonMsg = "PRINTER_LOW_PAPER_SOON";
    
    public Maintenance(Software software){
        this.software = software;
        // make predictions (check component statuses)
        this.inkRemaining = 0;
        this.averageInkUsagePerSession = 0;
    }
    
    /**
     * Returns arraylist of issues that require Attendant attention
     * 
     * @return Arraylist of strings which could have any of the following string objects:
     * 		"PRINTER_OUT_OF_INK", "PRINTER_LOW_INK", "PRINTER_LOW_INK_SOON",
     * 		"COIN_DISPENSER_LOW_COINS", "COIN_DISPENSER_ALMOST_FULL", "COIN_STORAGE_ALMOST_FULL",
     * 		"PRINTER_OUT_OF_PAPER", "PRINTER_LOW_PAPER", "PRINTER_LOW_PAPER_SOON"
     */
    public ArrayList<String> getIssues() {
		return issues;
	}

    /**
     * Checks ink level (empty,low,low soon) of printer and notifies Attended as needed.
     * 
     * @param averagePrintedChars, estimated average chars printed by printer
     */
    public void checkInk(int averagePrintedChars){
    	
    	this.averageInkUsagePerSession = averagePrintedChars;
   
    	try {
    		this.inkRemaining = software.printer.inkRemaining();
    	} catch (UnsupportedOperationException e) {
    		// if station type is bronze
    	}
    	
    	if (inkRemaining == 0) {
    		thePrinterIsOutOfInk();
    	} else if (inkRemaining <= lowInkLevel) {
    		thePrinterHasLowInk();
    	} else { // If no issues or prior issues has been resolved
    		// Remove if exists in issues arraylist; does nothing otherwise
    		issues.remove(lowInkMsg);
    		issues.remove(outOfInkMsg);
    		// Estimate when low ink might occur
    		predictLowInk();
    	}
    }
    
    /**
     * Predicts how many more usages before low ink using average
     * length of receipt printable characters
     * 
     */
    public void predictLowInk() {
    	if (inkRemaining <= lowInkLevel+averageInkUsagePerSession) {
    		issues.add(lowInkSoonMsg);
    		software.attendant.disableCustomerStation();
    	} else {
    		issues.remove(lowInkSoonMsg);
    	}
    }
    
    /**
     * Enables adding ink to the printer. Checks ink level after.
     * 
     * @param quantity of ink that user wants to add
     * @throws OverloadedDevice if quantity added causes ink level to be more than allowable
     */
    public void resolveInkIssue(int quantity) throws OverloadedDevice {
    	if (quantity >= (MAXIMUM_INK-inkRemaining)) {
    		throw new RuntimeException("Process aborted: Quantity will overload the device.");
    	}
    	software.printer.addInk(quantity);
    	checkInk(averageInkUsagePerSession);	
    }
    
    /**
     * predicts if the dispenser has low coins
     * @param denomination - the denomination associated with the dispenser
     */
    public void predictLowCoinsDispenser(BigDecimal denomination) {
		int maxCapacity = software.getCoinDispensers().get(denomination).getCapacity();
		int coinsInDispenser = software.getCoinDispensers().get(denomination).size();
		// 25% of max capacity
		int twentyFivePer = Math.round(maxCapacity/4);
		
		// if the coins in the dispenser is less than or equal to 25% of max capacity
		if (coinsInDispenser <= twentyFivePer) {
			issues.add(lowCoinsSoonDisp);
			software.blockCustomerStation();
		} else {
			issues.remove(lowCoinsSoonDisp);
		}	
    }
    
    /**
     * predicts if the dispenser is almost full
     * @param denomination - the denomination associated with the dispenser
     */
    public void predictCoinsFullDispenser(BigDecimal denomination) {
    	int maxCapacity = software.getCoinDispensers().get(denomination).getCapacity();
    	int coinsInDispenser = software.getCoinDispensers().get(denomination).size();
    	// 75% of max capacity
    	int seventyFivePer = Math.round((maxCapacity * 3)/4);
    	//if coins in the dispenser is greater than or equal to 75% of max capacity
    	if (coinsInDispenser >= seventyFivePer) {
    		// notify attendant
    		issues.add(dispAlmostFull);
    		software.blockCustomerStation();
    	} else {
    		issues.remove(dispAlmostFull);
    	}
    }
    
    /**
     * predicts if the coin storage unit is almost full
     */
    public void predictCoinsFullStorage() {
    	int maxCapacity = software.getCoinStorage().getCapacity();
    	int coinsInStorage = software.getCoinStorage().getCoinCount();
    	// 75% of max capacity
    	int seventyFivePer = Math.round((maxCapacity * 3)/4);
    	//if coins in the storage unit is greater than or equal to 75% of max capacity
    	if (coinsInStorage >= seventyFivePer) {
    		// notify attendant
    		issues.add(storAlmostFull);
    		software.blockCustomerStation();
    	} else {
    		issues.remove(storAlmostFull);
    	}
    }
    
    /** 
     * Simulates adding coins to a dispenser of its associated denomination
     * @param dispenser - the dispenser that needs coins to be added to
     * @param denomination - the type of denomination for that dispenser
     * @param amount - the amount of coins to be placed in the dispenser 
     */
    public void addCoinsInDispenser(ICoinDispenser dispenser, BigDecimal denomination, int amount) throws SimulationException, CashOverloadException {
    	if (dispenser == null) {
    		throw new NullPointerSimulationException();   		
    	}
    	
    	if (!software.isBlocked()) {
    		System.out.println("Station must be disabled");
    	} else {
    		int i;
    		Coin coin = new Coin(denomination);
    		Coin[] coins = new Coin[amount];
    		
    		for (i = 0; i <= amount - 1; i++ ) {
    			coins[i] = coin;
    		}
    		
    		dispenser.load(coins);
    		
    	}
   	}
    	    
    /**
     * Simulates removing coins in a dispenser of its associated 
     * @param dispenser - the dispenser that needs coins to be removed from
     * @param amount - the amount of coins to be placed in the dispenser 
     */
    public void removeCoinsInDispenser(ICoinDispenser dispenser, int amount) throws CashOverloadException, NoCashAvailableException, DisabledException {
    	if (dispenser == null) {
    		throw new NullPointerSimulationException();   		
    	}
    	
    	if (!software.isBlocked()) {
    		System.out.println("Station must be disabled");
    	} else {
    		int i;
    		for (i = 0; i <= amount - 1; i++) {
    			dispenser.emit();
    		}
    		
    	}
    }
    		
    
    /**
     * Simulates removing all coins from the coin storage unit
     * @param unit - the storage unit to remove all coins from
     */
    public void removeAllCoinsInStorageUnit(CoinStorageUnit unit) {
    	
    	if (!software.isBlocked()) {
    		System.out.println("Station must be disabled");
    	} else {
    		unit.unload();		
    	}
    }
    
   
    /**
     * Checks the varying amounts of paper
     * When there is no more paper
     * When is there is low paper
     * @param averagePaperUsed
     */
    public void checkPaper(int averagePaperUsed) { 
    	this.averagePaperUsedPerSession = averagePaperUsed;
    	
    	try {
    		this.remainingPaper = software.printer.paperRemaining();
    	} catch (UnsupportedOperationException e) {
    		// if station type is bronze
    	}
    	
    	if (remainingPaper == 0) {
    		thePrinterIsOutOfPaper();
    	} else if (remainingPaper <= lowPaperLevel) {
    		thePrinterHasLowPaper();
    	} else { // If no issues or prior issues has been resolved
    		// Remove if exists in issues arraylist; does nothing otherwise
    		issues.remove(lowPaperMsg);
    		issues.remove(outOfPaperMsg);
    		// Estimate when low paper might occur
    		predictLowPaper();
    	}				 
	}
    
    /**
     * Predicts how many more usages before low paper using average
     * amount of paper used
     */
    public void predictLowPaper() {
    	if (remainingPaper <= lowPaperLevel+averagePaperUsedPerSession) {
    		issues.add(lowPaperSoonMsg);
    		software.attendant.disableCustomerStation();
    	} else {
    		issues.remove(lowPaperSoonMsg);
    	}
    }
	
    /**
     * Add the specified amount of paper to printer
     * @param amount
     * @throws OverloadedDevice
     */
	public void resolvePrinterPaperIssue(int amount) throws OverloadedDevice {
		if (amount >= (MAXIMUM_PAPER-remainingPaper)) {
    		throw new RuntimeException("Process aborted: Quantity will overload the device.");
    	}
    	software.printer.addPaper(amount);
    	checkPaper(averagePaperUsedPerSession);	
    }
		  

    // needs to be implemented and tested
    // should be called after every time change is given, startup?
    // notify attendant
    // may need different return type
    public void needBanknotes(){
    }

	@Override
	public void aDeviceHasBeenEnabled(IDevice<? extends IDeviceListener> device) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void aDeviceHasBeenDisabled(IDevice<? extends IDeviceListener> device) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void aDeviceHasBeenTurnedOn(IDevice<? extends IDeviceListener> device) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void aDeviceHasBeenTurnedOff(IDevice<? extends IDeviceListener> device) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void thePrinterIsOutOfPaper() {
		issues.add(outOfPaperMsg);
		
		// remove these elements if exists in issues; does nothing otherwise
		issues.remove(lowPaperMsg);
		issues.remove(lowPaperSoonMsg);
				
		software.attendant.disableCustomerStation();
	}

	@Override
	public void thePrinterIsOutOfInk() {
		issues.add(outOfInkMsg);
		
		// remove these elements if exists in issues; does nothing otherwise
		issues.remove(lowInkMsg);
		issues.remove(lowInkSoonMsg);
		
		software.attendant.disableCustomerStation();
	}

	@Override
	public void thePrinterHasLowInk() {
		issues.add(lowInkSoonMsg);
		
		// remove these elements if exists in issues; does nothing otherwise
		issues.remove(lowInkSoonMsg);
		
		software.attendant.disableCustomerStation();
	}

	@Override
	public void thePrinterHasLowPaper() {
		issues.add(lowPaperSoonMsg);
				
		// remove these elements if exists in issues; does nothing otherwise
		issues.remove(lowPaperSoonMsg);
				
		software.attendant.disableCustomerStation();
		
	}

	@Override
	public void paperHasBeenAddedToThePrinter() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void inkHasBeenAddedToThePrinter() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void coinsFull(ICoinDispenser dispenser) {
		// notifyAttendant
	}

	@Override
	public void coinsEmpty(ICoinDispenser dispenser) {
		// notifyAttendant
	}


	@Override
	public void coinsFull(CoinStorageUnit unit) {
		// notifyAttendant
		
	}
	
	@Override
	public void enabled(IComponent<? extends IComponentObserver> component) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void disabled(IComponent<? extends IComponentObserver> component) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void turnedOn(IComponent<? extends IComponentObserver> component) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void turnedOff(IComponent<? extends IComponentObserver> component) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void coinAdded(CoinStorageUnit unit) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void coinsLoaded(CoinStorageUnit unit) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void coinsUnloaded(CoinStorageUnit unit) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void coinAdded(ICoinDispenser dispenser, Coin coin) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void coinRemoved(ICoinDispenser dispenser, Coin coin) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void coinsLoaded(ICoinDispenser dispenser, Coin... coins) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void coinsUnloaded(ICoinDispenser dispenser, Coin... coins) {
		// TODO Auto-generated method stub
		
	}
}
