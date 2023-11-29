import java.util.ArrayList;

import com.jjjwelectronics.IDevice;
import com.jjjwelectronics.IDeviceListener;
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
    boolean notifyAttendant; // have to discuss with GUI and Misc teams
    int inkRemaining;
    boolean lowInk, lowInkSoon;
    int averageInkUsagePerSession;
    
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
        
        checkInk();
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
    public void checkInk(){
    	try {
    		this.inkRemaining = software.printer.inkRemaining();
    	} catch (UnsupportedOperationException e) {
    		// if station type is bronze
    	}
    	
    	if (inkRemaining == 0) {
    		thePrinterIsOutOfInk();
    	} else if (lowInk) {
    		thePrinterHasLowInk();
    	} else {
    		predictLowInk();
    	}
    }
    
    /**
     * Predicts how many more usages before low ink using average
     * length of receipt printable characters
     * 
     */
    public void predictLowInk() {
    	if (inkRemaining < averageInkUsagePerSession) {
    		this.lowInkSoon = true;
    		//this.notifyAttendant = true;
    		this.issues.add(lowInkSoonMsg);
    	} else {
    		this.lowInkSoon = false;
    	}
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
		//this.notifyAttendant  = true;
		this.issues.add(outOfInkMsg);
	}

	@Override
	public void thePrinterHasLowInk() {
		//this.notifyAttendant = true;
		this.issues.add(lowInkSoonMsg);
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
