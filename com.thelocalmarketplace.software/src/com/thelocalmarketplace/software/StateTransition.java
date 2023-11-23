package com.thelocalmarketplace.software;

import com.thelocalmarketplace.software.logic.StateLogic.States;

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
public class StateTransition {

	/**
	 * Initial state
	 */
	private States initial;
	
	/**
	 * Final state
	 */
	private States end;
	
	/**
	 * Constructor for a registerable state transition
	 * @throws NullPointerException If any argument is null
	 */
	public StateTransition(States initial, States end) throws NullPointerException {
		this.setInitialState(initial);
		this.setFinalState(end);
	}

	public States getInitialState() {
		return initial;
	}

	public void setInitialState(States initial) throws NullPointerException {
		if (initial == null) {
			throw new NullPointerException("Initial");
		}
		
		this.initial = initial;
	}

	public States getFinalState() {
		return end;
	}

	public void setFinalState(States end) throws NullPointerException {
		if (end == null) {
			throw new NullPointerException("End");
		}
		
		this.end = end;
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof StateTransition)) {
			return false;
		}
		
		StateTransition ob = (StateTransition) o;
		
		return (this.getInitialState().equals(ob.getInitialState()) && this.getFinalState().equals(ob.getFinalState()));
	}
}
