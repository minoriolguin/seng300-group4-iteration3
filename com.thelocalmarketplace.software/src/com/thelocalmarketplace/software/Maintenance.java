package com.thelocalmarketplace.software;

import java.math.BigDecimal;
import java.nio.channels.ConnectionPendingException;
import java.util.ArrayList;

import com.jjjwelectronics.IDevice;
import com.jjjwelectronics.IDeviceListener;
import com.jjjwelectronics.OverloadedDevice;
import com.jjjwelectronics.printer.ReceiptPrinterListener;

/**
 * Handles maintenance of hardware ink, paper, coins, and banknotes status
 * 
 * Any issues that requires an Attendant's attention is stored as String
 * type in the Arraylist `issues` to simulate maintenance codes.
 * 
 */
public abstract class Maintenance implements ReceiptPrinterListener {
    private Software software;
    private boolean notifyAttendant; // have to discuss with GUI and Misc teams
    private int inkRemaining;
    private int averageInkUsagePerSession;
    private int averagePaperUsedPerSession;
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
    String outOfPaperMsg = "PRINTER_OUT_OF_PAPER";
    String lowPaperMsg = "PRINTER_LOW_PAPER";
    String lowPaperSoonMsg = "PRINTER_LOW_PAPER_SOON";
    
    
    public Maintenance() throws InterruptedException {
        this.software = software;
        // make predictions (check component statuses)
        this.notifyAttendant = false;
        this.inkRemaining = 0;
        this.averageInkUsagePerSession = 0;
        
        checkInk(averageInkUsagePerSession);
    }
    
    /**
     * Returns arraylist of issues that require Attendant attention
     * 
     * @return Arraylist of strings which could have any of the following string objects:
     * 		"PRINTER_OUT_OF_INK", "PRINTER_LOW_INK", "PRINTER_LOW_INK_SOON",
     * 		"PRINTER_OUT_OF_PAPER", "PRINTER_LOW_PAPER", "PRINTER_LOW_PAPER_SOON"
     */
    public ArrayList<String> getIssues() {
		return issues;
	}

    // needs to be implemented and tested
    // should be called after every printed receipt, start up?
    // notify attendant
    // may need different return type
    public void checkInk(int averagePrintedChars) throws InterruptedException {
    	
    	this.averageInkUsagePerSession = averagePrintedChars;
   
    	try {
    		this.inkRemaining = software.printer.inkRemaining();
    	} catch (UnsupportedOperationException e) {
    		// if station type is bronze
    	}
    	
    	if (inkRemaining == 0) {
    		thePrinterIsOutOfInk();
    	} else if (inkRemaining <= lowInkLevel) thePrinterHasLowInk();
		else { // If no issues or prior issues has been resolved
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
    		//this.notifyAttendant = true; --- communicate w Miscellaneous team
    		issues.add(lowInkSoonMsg);
    		software.attendant.disableCustomerStation();
    	} else {
    		issues.remove(lowInkSoonMsg);
    	}
    }
    
    
    public void resolveInkIssue(int quantity) throws OverloadedDevice, InterruptedException {
    	if (quantity >= (MAXIMUM_INK-inkRemaining)) {
    		throw new RuntimeException("Process aborted: Quantity will overload the device.");
    	}
    	software.printer.addInk(quantity);
    	checkInk(averageInkUsagePerSession);	
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
    		//this.notifyAttendant = true; --- communicate w Miscellaneous team
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
    public void needCoins(){
    }
    // needs to be implemented and tested
    // should be called after every time change is given, startup?
    // notify attendant
    // may need different return type
    public void maintainBanknotes() throws  OverloadedDevice{
		this.notifyAttendant = false;
		try {
			//this.notifyAttendant = true; --- communicate w Miscellaneous team
			this.software.banknoteDispenser.dispense();

			//this.notifyAttendant = true; --- communicate w Miscellaneous team
			issues.add("ERROR_DISPENSING_BANKNOTES");
			BigDecimal[] banknotesAdded = software.getBanknoteDenominations();
			int banknotesRemoved = software.getBanknotesRemoved();

			adjustBanknoteDenominations(banknotesAdded, banknotesRemoved);
			boolean changesMade = software.detectBanknoteDenominationChanges();

			//this.notifyAttendant = true; --- communicate w Miscellaneous team
			issues.add("ERROR_DETECTING_BANKNOTE_CHANGES");
			if (changesMade) {
				//this.notifyAttendant = true; --- communicate w Miscellaneous team
				issues.add("ERROR_BANKNOTE_CHANGES");
			}


			for (int i = 0; i < banknotesAdded.length; i++) {
				if (banknotesAdded[i].compareTo(BigDecimal.ZERO) < 0) {
					//this.notifyAttendant = true; --- communicate w Miscellaneous team
					issues.add("ERROR_BANKNOTE_ADJUSTMENT_NOT_DETECTED");
					break;
				}

			}
			//this.notifyAttendant = true; --- communicate w Miscellaneous team


			issues.remove("ERROR_BANKNOTE_ADJUSTMENT_NOT_DETECTED");
			issues.remove("ERROR_BANKNOTE_ADJUSTMENT");
			issues.remove("ERROR_MAINTAINING_BANKNOTES");

		} catch (Exception e) {
			issues.add("ERROR_MAINTAINING_BANKNOTES");
		}
	}
	public void adjustBanknoteDenominations(BigDecimal[] banknotesAdded, int banknotesRemoved) {
		software.banknoteDispenser.adjustBanknoteDenominations(banknotesAdded, banknotesRemoved);
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

	public abstract void aDeviceHasBeenEnabled(IDevice<? extends IDeviceListener> device);

	public abstract void aDeviceHasBeenDisabled(IDevice<? extends IDeviceListener> device);

	@Override
	public void thePrinterIsOutOfPaper() {
		//this.notifyAttendant  = true; --- communicate w Miscellaneous team
		issues.add(outOfPaperMsg);
		
		// remove these elements if exists in issues; does nothing otherwise
		issues.remove(lowPaperMsg);
		issues.remove(lowPaperSoonMsg);
				
		software.attendant.disableCustomerStation();
	}

	@Override
	public void thePrinterIsOutOfInk() throws InterruptedException {
		//this.notifyAttendant  = true; --- communicate w Miscellaneous team
		issues.add(outOfInkMsg);
		
		// remove these elements if exists in issues; does nothing otherwise
		issues.remove(lowInkMsg);
		issues.remove(lowInkSoonMsg);
		
		software.attendant.disableCustomerStation();
	}

	@Override
	public void thePrinterHasLowInk() throws InterruptedException {
		//this.notifyAttendant = true;  --- communicate w Miscellaneous team
		issues.add(lowInkSoonMsg);
		
		// remove these elements if exists in issues; does nothing otherwise
		issues.remove(lowInkSoonMsg);
		
		software.attendant.disableCustomerStation();
	}

	@Override
	public void thePrinterHasLowPaper() {
		//this.notifyAttendant = true;  --- communicate w Miscellaneous team
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
}