package com.thelocalmarketplace.software.listeners;

import com.thelocalmarketplace.software.AbstractStateTransitionListener;
import com.thelocalmarketplace.software.logic.CentralStationLogic;

/**
 * Blocked Session
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
public class BlockedListener extends AbstractStateTransitionListener {

	public BlockedListener(CentralStationLogic logic) throws NullPointerException {
		super(logic);
	}

	@Override
	public void onTransition() {
		this.logic.hardware.baggingArea.disable();
		
		this.logic.hardware.handheldScanner.disable();
		this.logic.hardware.mainScanner.disable();
		
		this.logic.hardware.coinSlot.disable();
		this.logic.hardware.coinValidator.disable();
		
		this.logic.hardware.banknoteInput.disable();
		this.logic.hardware.banknoteValidator.disable();
	}
}
