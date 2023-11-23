package com.thelocalmarketplace.software.listeners;

import com.thelocalmarketplace.software.AbstractStateTransitionListener;
import com.thelocalmarketplace.software.logic.CentralStationLogic;

/**
 * @author Connell Reffo (10186960)
 * @author Tara Strickland (10105877)
 * @author Angelina Rochon (30087177)
 * @author Julian Fan (30235289)
 * @author Braden Beler (30084941)
 * @author Samyog Dahal (30194624)
 * @author Maheen Nizmani (30172615)
 * @author Phuong Le (30175125)
 * @author Daniel Yakimenka (10185055)
 * @author Merick Parkinson (30196225)
 * @author Farida Elogueil (30171114)
 */
public class NormalListener extends AbstractStateTransitionListener {

	public NormalListener(CentralStationLogic logic) throws NullPointerException {
		super(logic);
	}

	@Override
	public void onTransition() {
		this.logic.hardware.baggingArea.enable();
		
		this.logic.hardware.handheldScanner.enable();
		this.logic.hardware.mainScanner.enable();
		
		this.logic.hardware.coinSlot.enable();
		this.logic.hardware.coinValidator.enable();
		
		this.logic.hardware.banknoteInput.enable();
		this.logic.hardware.banknoteValidator.enable();
	}
}
