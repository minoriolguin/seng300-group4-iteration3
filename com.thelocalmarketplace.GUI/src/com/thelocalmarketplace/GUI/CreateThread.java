package com.thelocalmarketplace.GUI;

import com.thelocalmarketplace.hardware.SelfCheckoutStationGold;
import com.thelocalmarketplace.software.Software;
import com.thelocalmarketplace.software.TouchScreen;

public class CreateThread implements Runnable {

	@Override
	public void run() {
		SelfCheckoutStationGold hardware = new SelfCheckoutStationGold();
		Software software = Software.getInstance(hardware);
		software.turnOn();
		//software.maintenance.resolveInkIssue(1000);
		//software.maintenance.resolvePrinterPaperIssue(1000);
        TouchScreen touchscreen = software.touchScreen;
        GUILogic guiLogic = new GUILogic(software);
        RunGUI gui = new RunGUI(guiLogic);
	
	}

}
