package com.thelocalmarketplace.software.logic;

import java.util.ArrayList;
import java.util.HashMap;

import com.thelocalmarketplace.software.AbstractLogicDependant;
import com.thelocalmarketplace.software.AbstractStateTransitionListener;
import com.thelocalmarketplace.software.StateTransition;
import com.thelocalmarketplace.software.exceptions.InvalidStateTransitionException;
import com.thelocalmarketplace.software.listeners.AddBagsListener;
import com.thelocalmarketplace.software.listeners.BlockedListener;
import com.thelocalmarketplace.software.listeners.CheckoutListener;
import com.thelocalmarketplace.software.listeners.NormalListener;
import com.thelocalmarketplace.software.listeners.SuspendedListener;

/**
 * Handles all state transition logic
 * 
 * Effectively represents a finite automaton that handles transitions between states
 * NOTE: Not all transitions have to be defined
 * 
 * I guess CPSC 351 was useful in the end
 * 
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
 * @author Merick Parkinson (30196225)
 * @author Farida Elogueil (30171114)
 */
public class StateLogic extends AbstractLogicDependant {
	
	/**
	 * Enumeration of possible states
	 */
	public enum States {
		NORMAL, // Default
		BLOCKED,
		ADDBAGS,
		CHECKOUT,
		SUSPENDED
	}
	

	/**
	 * Tracks all registered listeners indexed by a state
	 * Multiple listeners can be mapped to a single state change event
	 */
	private HashMap<States, ArrayList<AbstractStateTransitionListener>> events;
	
	/**
	 * Transition function for the state machine
	 */
	private ArrayList<StateTransition> transitions;
	
	/**
	 * Current state of the machine
	 */
	private States state;

	
	/**
	 * Base constructor
	 */
	public StateLogic(CentralStationLogic logic) {
		super(logic);
		
		this.state = States.NORMAL;
		
		this.events = new HashMap<>();
		this.transitions = new ArrayList<>();
		
		this.registerAll();
	}
	
	/**
	 * Register allowable state transitions here
	 */
	private void registerAll() {
		
		// NORMAL <==> [ANY STATE]
		for (States s : States.values()) {
			if (!s.equals(States.NORMAL)) {				
				this.registerTransition(States.NORMAL, s);
				this.registerTransition(s, States.NORMAL);
			}
		}
		
		// Other transitions
		this.registerTransition(States.CHECKOUT, States.SUSPENDED);
		
		// Attach listeners
		this.registerListener(States.NORMAL, new NormalListener(this.logic));
		this.registerListener(States.BLOCKED, new BlockedListener(this.logic));
		this.registerListener(States.SUSPENDED, new SuspendedListener(this.logic));
		this.registerListener(States.ADDBAGS, new AddBagsListener(this.logic));
		this.registerListener(States.CHECKOUT, new CheckoutListener(this.logic));
	}
	
	/**
	 * Registers a new transition to a transition listener
	 * @param initial Is the initial state
	 * @param end Is the final state
	 * @throws NullPointerException If transition is null
	 */
	public void registerTransition(States initial, States end) throws NullPointerException {
		if (initial == null || end == null) {
			throw new NullPointerException();
		}
		
		this.transitions.add(new StateTransition(initial, end));
	}
	
	/**
	 * Registers a new event listener
	 * @param state Is the state to listen for
	 * @param listener The listener to register
	 * @throws NullPointerException If any argument is null
	 */
	public void registerListener(States state, AbstractStateTransitionListener listener) throws NullPointerException {
		if (state == null || listener == null) {
			throw new NullPointerException();
		}
		
		if (this.events.containsKey(state)) {
			this.events.get(state).add(listener);
		}
		else {
			ArrayList<AbstractStateTransitionListener> l = new ArrayList<>();
			l.add(listener);
						
			this.events.put(state, l);
		}
		
	}
	
	/**
	 * Transitions to a different state
	 * If next = current, then stay
	 * @param next The new state to go to
	 * @throws InvalidStateTransitionException if that transition is not allowed
	 */
	public void gotoState(States next) throws InvalidStateTransitionException {
		if (!(this.transitions.contains(new StateTransition(this.getState(), next)) || this.getState().equals(next))) {
			throw new InvalidStateTransitionException();
		}
		
		this.state = next;
		
		for (AbstractStateTransitionListener listener : this.events.get(this.state)) {
			listener.onTransition();
		}
	}
	
	/**
	 * Checks if in a given state
	 * @param state The state to check
	 * @return If in the state or not
	 */
	public boolean inState(States state) {
		return this.getState().equals(state);
	}
	
	/**
	 * Gets the current state
	 * @return the state
	 */
	public States getState() {
		return this.state;
	}
}
