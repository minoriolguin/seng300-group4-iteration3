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
public class Maintenance implements ReceiptPrinterListener {
    private Software software;
    private boolean notifyAttendant; // have to discuss with GUI and Misc teams
    private int inkRemaining;
    private int averageInkUsagePerSession;
    
    // Specs
    public static final int MAXIMUM_INK = 1 << 20;
    public int lowInkLevel = (int)(MAXIMUM_INK * 0.1);
    
    ArrayList<String> issues;

	// String messages that simulate maintenance codes
    String outOfInkMsg = "PRINTER_OUT_OF_INK";
    String lowInkMsg = "PRINTER_LOW_INK";
    String lowInkSoonMsg = "PRINTER_LOW_INK_SOON";
    
    public Maintenance(Software software){
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
     * 		"PRINTER_OUT_OF_INK", "PRINTER_LOW_INK", "PRINTER_LOW_INK_SOON"
     */
    public ArrayList<String> getIssues() {
		return issues;
	}

    // needs to be implemented and tested
    // should be called after every printed receipt, start up?
    // notify attendant
    // may need different return type
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
    		//this.notifyAttendant = true; --- communicate w Miscellaneous team
    		issues.add(lowInkSoonMsg);
    		software.blockCustomerStation();
    	} else {
    		issues.remove(lowInkSoonMsg);
    	}
    }
    
    
    public void resolveInkIssue(int quantity) throws OverloadedDevice {
    	if (quantity >= (MAXIMUM_INK-inkRemaining)) {
    		throw new RuntimeException("Process aborted: Quantity will overload the device.");
    	}
    	software.printer.addInk(quantity);
    	checkInk(averageInkUsagePerSession);	
    }
    
    // needs to be implemented and tested
    // should be called after every printed receipt, start up?
    // notify attendant
    // may need different return type
    public void needPaper(){
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void thePrinterIsOutOfInk() {
		//this.notifyAttendant  = true; --- communicate w Miscellaneous team
		issues.add(outOfInkMsg);
		
		// remove these elements if exists in issues; does nothing otherwise
		issues.remove(lowInkMsg);
		issues.remove(lowInkSoonMsg);
		
		software.blockCustomerStation();
	}

	@Override
	public void thePrinterHasLowInk() {
		//this.notifyAttendant = true;  --- communicate w Miscellaneous team
		issues.add(lowInkSoonMsg);
		
		// remove these elements if exists in issues; does nothing otherwise
		issues.remove(lowInkSoonMsg);
		
		software.blockCustomerStation();
	}

	@Override
	public void thePrinterHasLowPaper() {
		// TODO Auto-generated method stub
		
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
