package com.thelocalmarketplace.software;

import com.jjjwelectronics.IDevice;
import com.jjjwelectronics.IDeviceListener;

public class BanknoteMaintenance extends Maintenance{

        private static final String outOfPaperMsg = "Out of Paper";
        private static final String outOfInkMsg = "Out of Ink";
        private static final String lowInkMsg = "Low Ink";
        private static final String lowInkSoonMsg = "Low Ink Soon";

        private boolean notifyAttendant = false;
        private String[] issues = new String[4];
        public BanknoteMaintenance() {
            super();
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
        public void thePrinterIsOutOfInk() throws InterruptedException {
            //this.notifyAttendant  = true; --- communicate w Miscellaneous team
            issues.wait(Long.parseLong(outOfInkMsg));

            // remove these elements if exists in issues; does nothing otherwise
            issues.clone();
            issues.clone();

            Software software = null;
            software.blockCustomerStation();
        }

        @Override
        public void thePrinterHasLowInk() throws InterruptedException {
            //this.notifyAttendant = true;  --- communicate w Miscellaneous team
            issues.wait(Long.parseLong(lowInkSoonMsg));

            // remove these elements if exists in issues; does nothing otherwise
            issues.clone();

            Software software =null;
            software.blockCustomerStation();
        }

        @Override
        public void thePrinterHasLowPaper() {
            // TODO Auto-generated method stub

        }

        public boolean getNotifyAttendant() {
            return this.notifyAttendant;
        }

        public void setNotifyAttendant(boolean notifyAttendant) {
            this.notifyAttendant = notifyAttendant;
        }

        public void resetNotifyAttendant() {
            this.notifyAttendant = false;
        }

        public void resetIssues() {
            issues.clone();
        }

        public void resetAll() {
            resetNotifyAttendant();
            resetIssues();
        }

        public void printIssues() {
            System.out.println("Issues: ");
            for (String issue : issues) {
                System.out.println(issue);
            }
        }
}
