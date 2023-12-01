package com.thelocalmarketplace.software;

import com.jjjwelectronics.scanner.Barcode;

public class MembershipScanner {
    private Software softwareInstance;

    public MembershipScanner(Software softwareInstance) {
        this.softwareInstance = softwareInstance;
    }

    public void handleMembershipBarcode(Barcode barcode) {
        String scannedNumber = barcode.toString(); // Extract the membership number from the barcode
        softwareInstance.handleMembershipNumber(scannedNumber);
    }
}
