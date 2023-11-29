import com.jjjwelectronics.IDevice;
import com.jjjwelectronics.IDeviceListener;
import com.jjjwelectronics.printer.ReceiptPrinterListener;

public class Maintenance implements ReceiptPrinterListener {
    private Software software;
    public Maintenance(Software software){
        this.software = software;
    }

    // needs to be implemented and tested
    // should be called after every printed receipt, start up?
    // notify attendant
    // may need different return type
    public void needInk(){
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void thePrinterHasLowInk() {
		// TODO Auto-generated method stub
		
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
