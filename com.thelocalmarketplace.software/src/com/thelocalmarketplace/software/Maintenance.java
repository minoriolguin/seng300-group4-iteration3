 /**
 *Project, Iteration 3, Group 4
 *  Group Members:
 * - Arvin Bolbolanardestani / 30165484
 * - Anthony Chan / 30174703
 * - Marvellous Chukwukelu / 30197270
 * - Farida Elogueil / 30171114
 * - Ahmed Elshabasi / 30188386
 * - Shawn Hanlon / 10021510
 * - Steven Huang / 30145866
 * - Nada Mohamed / 30183972
 * - Jon Mulyk / 30093143
 * - Althea Non / 30172442
 * - Minori Olguin / 30035923
 * - Kelly Osena / 30074352
 * - Muhib Qureshi / 30076351
 * - Sofia Rubio / 30113733
 * - Muzammil Saleem / 30180889
 * - Steven Susorov / 30197973
 * - Lydia Swiegers / 30174059
 * - Elizabeth Szentmiklossy / 30165216
 * - Anthony Tolentino / 30081427
 * - Johnny Tran / 30140472
 * - Kaylee Xiao / 30173778 
 **/

package com.thelocalmarketplace.software;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.jjjwelectronics.IDevice;
import com.jjjwelectronics.IDeviceListener;
import com.jjjwelectronics.OverloadedDevice;
import com.jjjwelectronics.printer.ReceiptPrinterListener;
import com.tdc.CashOverloadException;
import com.tdc.DisabledException;
import com.tdc.IComponent;
import com.tdc.IComponentObserver;
import com.tdc.NoCashAvailableException;
import com.tdc.banknote.Banknote;
import com.tdc.banknote.BanknoteStorageUnit;
import com.tdc.banknote.BanknoteStorageUnitObserver;
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
public class Maintenance implements ReceiptPrinterListener, CoinDispenserObserver, CoinStorageUnitObserver, BanknoteStorageUnitObserver {
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

	public int MAXIMUM_BANKNOTES;

	public int lowbanknoteslevel;
	int highbanknoteslevel;

	private int averageBanknotesUsagePerSession = 5;

	private int currentBanknotes = 0;
	

	ArrayList<String> issues;

	// String messages that simulate maintenance codes
    String outOfInkMsg = "PRINTER_OUT_OF_INK";
    String lowInkMsg = "PRINTER_LOW_INK";
    String lowInkSoonMsg = "PRINTER_LOW_INK_SOON";
    
    String lowCoinsSoonDisp = "COIN_DISPENSER_LOW_COINS";
    String dispAlmostFull = "COIN_DISPENSER_ALMOST_FULL";
    String storAlmostFull = "COIN_STORAGE_ALMOST_FULL";
    String outOfCoinsDisp = "COIN_DISPENSER_OUT_OF_COINS";
    String coinDispFull = "COIN_DISPENSER_IS_FULL";
    String coinStorFull = "COIN_STORAGE_IS_FULL";
    
    String outOfPaperMsg = "PRINTER_OUT_OF_PAPER";
    String lowPaperMsg = "PRINTER_LOW_PAPER";
    String lowPaperSoonMsg = "PRINTER_LOW_PAPER_SOON";

	String outOfBanknotesMsg = "OUT_OF_BANKNOTES";
	String lowBanknotesMsg = "LOW_BANKNOTES";
	String lowBanknotesSoonMsg = "LOW_BANKNOTES_SOON";
	String fullBanknotesSoonMsg = "BANKNOTES_ALMOST_FULL";

	String bankNotesFullMsg = "BANKNOTES_FULL";
	private boolean keepValue = false;
    
    public Maintenance(Software software){
        this.software = software;
        
        // attach this class as an observer to each coin dispenser
        if (software.getCoinDenominations() == null) {
        	throw new NullPointerSimulationException("coin denominations");   		
        }
       
        // attach this class as an observer to each coin dispenser
        for (BigDecimal coinDenomination : software.getCoinDenominations()) {
        	software.getCoinDispensers().get(coinDenomination).attach(this);
        }
        
        // attach this class as an observer to the coin storage unit
        software.getCoinStorage().attach(this);

        // make predictions (check component statuses)
        this.inkRemaining = 0;
        this.averageInkUsagePerSession = 0;
		issues = new ArrayList<>();
		
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
    		software.attendant.enableCustomerStation();
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
    	if (quantity > (MAXIMUM_INK-inkRemaining)) {
    		throw new RuntimeException("Process aborted: Quantity will overload the device.");
    	}
    	software.printer.addInk(quantity);
    	this.inkRemaining += quantity;
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
			software.attendant.disableCustomerStation();
		} else {
			issues.remove(lowCoinsSoonDisp);
			software.attendant.enableCustomerStation();
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
    		software.attendant.disableCustomerStation();
    	} else {
    		issues.remove(dispAlmostFull);
    		software.attendant.enableCustomerStation();
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
    		software.attendant.disableCustomerStation();
    	} else {
    		issues.remove(storAlmostFull);
    		software.attendant.enableCustomerStation();
    	}
    }
    
    /** 
     * Simulates adding coins to a dispenser of its associated denomination
     * @param dispenser - the dispenser that needs coins to be added to
     * @param denomination - the type of denomination for that dispenser
     * @param amount - the amount of coins to be placed in the dispenser 
     */
    public void addCoinsInDispenser(ICoinDispenser dispenser, BigDecimal denomination, int amount) throws SimulationException, CashOverloadException, DisabledException {
    	if (dispenser == null) {
    		throw new NullPointerSimulationException("dispenser");   		
    	}
    	
    	if (!software.isCustomerStationBlocked()) {
    		System.out.println("Station must be disabled");
    	} else {
    		int i;
    		Coin coin = new Coin(denomination);
    		
    		for (i = 0; i <= amount - 1; i++ ) {
    			dispenser.receive(coin);
    		}  		
    	}
   	}
    	    
    /**
     * Simulates removing coins in a dispenser of its associated 
     * @param dispenser - the dispenser that needs coins to be removed from
     * @param amount - the amount of coins to be placed in the dispenser 
     */
    public void removeCoinsInDispenser(ICoinDispenser dispenser, int amount) throws CashOverloadException, NoCashAvailableException, DisabledException {
    	if (dispenser == null) {
    		throw new NullPointerSimulationException("dispenser");   		
    	}
    	
    	if (!software.isCustomerStationBlocked()) {
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
    	if (!software.isCustomerStationBlocked()) {
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
    		software.attendant.enableCustomerStation();
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
		if (amount > (MAXIMUM_PAPER-remainingPaper)) {
    		throw new RuntimeException("Process aborted: Quantity will overload the device.");
    	}
    	software.printer.addPaper(amount);
    	this.remainingPaper += amount;
    	checkPaper(averagePaperUsedPerSession);	
    }

	/**
     * Checks to see the count of banknotes and send the appropriate message.
     * @param averageBanknotesUsagePerSession, banknoteStorageUnit
     */
	public void checkBanknotes(int averageBanknotesUsagePerSession, BanknoteStorageUnit banknoteStorageUnit){

		this.averageBanknotesUsagePerSession = averageBanknotesUsagePerSession;

		try {
			if(keepValue == false) {this.MAXIMUM_BANKNOTES = banknoteStorageUnit.getCapacity();}
			if (currentBanknotes==0){this.currentBanknotes = banknoteStorageUnit.getBanknoteCount();}
			lowbanknoteslevel = (int)(MAXIMUM_BANKNOTES * 0.1);
			highbanknoteslevel = Math.round(( MAXIMUM_BANKNOTES* 3)/4);
			
		} catch (UnsupportedOperationException e) {
			// if station type is bronze
		}

		if (currentBanknotes == 0) {
			issues.add(outOfBanknotesMsg);

			// remove these elements if exists in issues; does nothing otherwise
			issues.remove(lowBanknotesMsg);
			issues.remove(lowBanknotesSoonMsg);

			software.attendant.disableCustomerStation();
		} else if (currentBanknotes <= lowbanknoteslevel) {
			issues.add(lowBanknotesMsg);

			// remove these elements if exists in issues; does nothing otherwise

			software.attendant.disableCustomerStation();
		} else if (currentBanknotes >= MAXIMUM_BANKNOTES) {
			issues.add(bankNotesFullMsg);

			software.attendant.disableCustomerStation();
		}

		else {
			while(issues.contains(lowBanknotesMsg) || issues.contains(outOfBanknotesMsg) || issues.contains(bankNotesFullMsg) || issues.contains(lowBanknotesSoonMsg)) {
			issues.remove(lowBanknotesMsg);
			issues.remove(outOfBanknotesMsg);
			issues.remove(bankNotesFullMsg);
			issues.remove(lowBanknotesSoonMsg);}
			
			software.attendant.enableCustomerStation();

			predictLowBanknotes(banknoteStorageUnit);
			predictBanknotesFull(banknoteStorageUnit);
		}
	}


	/**
     * Predicts if the storage unit is running out of banknotes
     * @param banknoteStorageUnit
     */
	public void predictLowBanknotes(BanknoteStorageUnit banknoteStorageUnit) {
		if (currentBanknotes <= lowbanknoteslevel+averageBanknotesUsagePerSession) {
			issues.add(lowBanknotesSoonMsg);
			software.attendant.disableCustomerStation();
		} else {
			issues.remove(lowBanknotesSoonMsg);
		}
	}
	
	/**
     * Predicts if the storage unit is almost full of banknotes
     * @param banknoteStorageUnit
     */
	public void predictBanknotesFull(BanknoteStorageUnit banknoteStorageUnit) {
		if (currentBanknotes >= highbanknoteslevel) {
			issues.add(fullBanknotesSoonMsg);
			software.attendant.disableCustomerStation();
		} else {
			issues.remove(fullBanknotesSoonMsg);
		}
	}
	
	/**
     * resolves low amount of banknotes issue
     * @param banknoteStorageUnit, banknotes
     * @throws CashOverloadException, DisabledException
     */
	public void resolveBanknotesLow(BanknoteStorageUnit banknoteStorageUnit, ArrayList<Banknote> banknotes) throws CashOverloadException, DisabledException {
		if (banknotes.size() >= (MAXIMUM_BANKNOTES-currentBanknotes)) {
			throw new RuntimeException("Process aborted: Quantity will overload the device.");
		}
		for(Banknote banknote : banknotes)
			if(banknote == null)
				throw new NullPointerSimulationException("banknote instance");
			else
				banknoteStorageUnit.load(banknote);
		checkBanknotes(averageBanknotesUsagePerSession, banknoteStorageUnit);
	}
	
	/**
     * resolves the isssue of having the storage unit reach maximum capacity of banknotes
     * @param banknoteStorageUnit
     * @throws CashOverloadException, DisabledException
     */
	public void resolveBanknotesFull(BanknoteStorageUnit banknoteStorageUnit) throws CashOverloadException, DisabledException {
		currentBanknotes = banknoteStorageUnit.getBanknoteCount();
		if (currentBanknotes < (lowbanknoteslevel)) {
			throw new RuntimeException("Process aborted: Quantity will be lower than minimum.");
		}
		
		List<Banknote> unloaded = new ArrayList<>(banknoteStorageUnit.unload());
		
		while(currentBanknotes+1>highbanknoteslevel) {
			unloaded.remove(unloaded.size()-1);
			currentBanknotes = unloaded.size();
		}
		
		for(Banknote banknote : unloaded)
			if(banknote != null)
				banknoteStorageUnit.load(banknote);
		checkBanknotes(averageBanknotesUsagePerSession, banknoteStorageUnit);
	}
	
    public int getInkRemaining() {
		return inkRemaining;
	}
    
    public int getPaperRemaining() {
    	return remainingPaper;
    }
    
    public void setInkRemaining(int amount) {
		inkRemaining = amount;
	}

	public void setCurrentBanknotes(int currentBanknotes) {
		this.currentBanknotes = currentBanknotes;
	}
	
	public void setMAXIMUM_BANKNOTES(int mAXIMUM_BANKNOTES) {
		MAXIMUM_BANKNOTES = mAXIMUM_BANKNOTES;
		highbanknoteslevel = Math.round(( MAXIMUM_BANKNOTES* 3)/4);
		lowbanknoteslevel = (int)(MAXIMUM_BANKNOTES * 0.1);
		keepValue = true;
	}
    
    public void setPaperRemaining(int amount) {
    	remainingPaper = amount;
    }
    
	public int getAverageInkUsagePerSession() {
		return averageInkUsagePerSession;
	}
	
	public int getAveragePaperUsagePerSession() {
		return averagePaperUsedPerSession;
	}

	public void setAverageInkUsagePerSession(int averageInkUsagePerSession) {
		this.averageInkUsagePerSession = averageInkUsagePerSession;
	}
	
	public void setAveragePaperUsagePerSession(int averagePaperUsage) {
		this.averagePaperUsedPerSession = averagePaperUsage;
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
		issues.add(lowInkMsg);
		
		// remove these elements if exists in issues; does nothing otherwise
		issues.remove(lowInkSoonMsg);
		
		software.attendant.disableCustomerStation();
	}

	@Override
	public void thePrinterHasLowPaper() {
		issues.add(lowPaperMsg);
				
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
		issues.add(coinDispFull);
		
		// remove these elements if exists in issues; does nothing otherwise
		issues.remove(dispAlmostFull);
		
		software.attendant.disableCustomerStation();
	}

	@Override
	public void coinsEmpty(ICoinDispenser dispenser) {
		issues.add(outOfCoinsDisp);
		
		// remove these elements if exists in issues; does nothing otherwise
		issues.remove(lowCoinsSoonDisp);
		
		software.attendant.disableCustomerStation();
	}


	@Override
	public void coinsFull(CoinStorageUnit unit) {
		issues.add(coinStorFull);
		
		// remove these elements if exists in issues; does nothing otherwise
		issues.remove(storAlmostFull);
		
		software.attendant.disableCustomerStation();
		
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

	@Override
	public void banknotesFull(BanknoteStorageUnit unit) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void banknoteAdded(BanknoteStorageUnit unit) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void banknotesLoaded(BanknoteStorageUnit unit) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void banknotesUnloaded(BanknoteStorageUnit unit) {
		// TODO Auto-generated method stub
		
	}
}
