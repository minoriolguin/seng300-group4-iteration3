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

import com.jjjwelectronics.IDevice;
import com.jjjwelectronics.IDeviceListener;
import com.jjjwelectronics.OverloadedDevice;
import com.jjjwelectronics.printer.ReceiptPrinterBronze;
import com.jjjwelectronics.printer.ReceiptPrinterGold;
import com.jjjwelectronics.printer.ReceiptPrinterListener;
import com.jjjwelectronics.printer.ReceiptPrinterSilver;

public class PrintReceiptTestAttendantStub implements ReceiptPrinterListener {
    // interacts with a cell on the attendant's screen that will inform them which printer is out (unless there is a hardware update)
    // for the stub only, the printer will be  remembered
    ReceiptPrinterBronze printerBronze;
    ReceiptPrinterSilver printerSilver;
    ReceiptPrinterGold printerGold;
    int outOfPaperCounter = 0;
    int outOfInkCounter = 0;

    public PrintReceiptTestAttendantStub(ReceiptPrinterBronze p) {
    	this.printerBronze = p;
    }
    public PrintReceiptTestAttendantStub(ReceiptPrinterSilver p) {
        this.printerSilver = p;
    }
    public PrintReceiptTestAttendantStub(ReceiptPrinterGold p) {
        this.printerGold = p;
    }

    /**
     * If the printer is out of paper the stub will add one line of paper to the printer.
     */
    @Override
    public void thePrinterIsOutOfPaper() {
        outOfPaperCounter++;
        try {
            if (printerBronze != null)
                printerBronze.addPaper(1);
            else if (printerSilver != null)
                printerSilver.addPaper(1);
            else if (printerGold != null)
                printerGold.addPaper(1);
        } catch (OverloadedDevice e) {
            System.out.println("Cannot add more paper, the printer is full.");
        }
    }

    /**
     * If the printer is out of ink, the stub will add one character of ink.
     */
    @Override
    public void thePrinterIsOutOfInk() {
        outOfInkCounter++;
        try {
            if (printerBronze != null)
                printerBronze.addInk(1);
            else if (printerSilver != null)
                printerSilver.addInk(1);
            else if (printerGold != null)
                printerGold.addInk(1);
        } catch (OverloadedDevice e) {
            System.out.println("Cannot add more ink, the printer is full.");
        }
    }

    @Override
    public void thePrinterHasLowInk() {
    }

    @Override
    public void thePrinterHasLowPaper() {
    }

    @Override
    public void paperHasBeenAddedToThePrinter() {
    }

    @Override
    public void inkHasBeenAddedToThePrinter() {
    }

    @Override
    public void aDeviceHasBeenEnabled(IDevice<? extends IDeviceListener> device) {
    }

    @Override
    public void aDeviceHasBeenDisabled(IDevice<? extends IDeviceListener> device) {
    }

    @Override
    public void aDeviceHasBeenTurnedOn(IDevice<? extends IDeviceListener> device) {
    }

    @Override
    public void aDeviceHasBeenTurnedOff(IDevice<? extends IDeviceListener> device) {
    }

}
