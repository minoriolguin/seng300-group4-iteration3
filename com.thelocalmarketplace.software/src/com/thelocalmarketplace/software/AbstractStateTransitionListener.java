package com.thelocalmarketplace.software;

import com.thelocalmarketplace.software.logic.CentralStationLogic;

/**
 * @author Connell Reffo (10186960)
 * --------------------------------
 * @author Tara Strickland (10105877)
 * @author Angelina Rochon (30087177)
 * @author Julian Fan (30235289)
 * @author Braden Beler (30084941)
 * @author Samyog Dahal (30194624)
 * @author Maheen Nizmani (30172615)
 * @author Phuong Le (30175125)
 * @author Daniel Yakimenka (10185055)
 * @author Farida Elogueil (30171114)
 */
public abstract class AbstractStateTransitionListener extends AbstractLogicDependant {

	public AbstractStateTransitionListener(CentralStationLogic logic) throws NullPointerException {
		super(logic);
	}

	/**
	 * Triggered when a specific transition occurs
	 */
	public abstract void onTransition();
}
