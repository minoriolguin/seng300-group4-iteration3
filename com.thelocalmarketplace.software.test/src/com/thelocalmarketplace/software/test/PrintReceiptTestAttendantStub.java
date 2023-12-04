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

package com.thelocalmarketplace.software.test;

import com.jjjwelectronics.IDevice;
import com.jjjwelectronics.IDeviceListener;
import com.jjjwelectronics.OverloadedDevice;
import com.jjjwelectronics.printer.*;

public class PrintReceiptTestAttendantStub implements ReceiptPrinterListener {
    // interacts with a cell on the attendant's screen that will inform them which printer is out (unless there is a hardware update)
    // for the stub only, the printer will be  remembered
    IReceiptPrinter printerGold;
    boolean outOfPaper;
    boolean outOfInk;



    public PrintReceiptTestAttendantStub(IReceiptPrinter p) {
        this.printerGold = p;
        this.outOfInk = false;
        this.outOfPaper = false;
    }

    /**
     * If the printer is out of paper the stub will add one line of paper to the printer.
     */
    @Override
    public void thePrinterIsOutOfPaper() {
        outOfPaper = true;
        try {
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
        outOfInk = true;
        try {
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
