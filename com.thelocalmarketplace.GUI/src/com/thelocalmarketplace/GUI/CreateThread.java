package com.thelocalmarketplace.GUI;

import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.hardware.SelfCheckoutStationBronze;
import com.thelocalmarketplace.hardware.SelfCheckoutStationGold;
import com.thelocalmarketplace.hardware.SelfCheckoutStationSilver;
import com.thelocalmarketplace.software.Software;
import com.thelocalmarketplace.software.TouchScreen;

public class CreateThread implements Runnable {
	AbstractSelfCheckoutStation hardware;
	int num;
	public CreateThread(int i){
		num =i;
	}
	@Override
	public void run() {
		if (num==1) {
			hardware = new SelfCheckoutStationBronze();
		}
		else if(num==2) {
			hardware = new SelfCheckoutStationGold();
		}
		else {
			 hardware = new SelfCheckoutStationSilver();
		}
		Software software = Software.getInstance(hardware);
		software.turnOn();
		//software.maintenance.resolveInkIssue(1000);
		//software.maintenance.resolvePrinterPaperIssue(1000);
        TouchScreen touchscreen = software.touchScreen;
        GUILogic guiLogic = new GUILogic(software);
        RunGUI gui = new RunGUI(guiLogic);
	
	}

}
