package com.thelocalmarketplace.software.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.thelocalmarketplace.software.StateTransition;
import com.thelocalmarketplace.software.logic.StateLogic.States;

/** Tests StateTransition
 * @author Angelina Rochon (30087177)
 * ----------------------------------
 * @author Connell Reffo (10186960)
 * @author Tara Strickland (10105877)
 * @author Julian Fan (30235289)
 * @author Braden Beler (30084941)
 * @author Samyog Dahal (30194624)
 * @author Maheen Nizmani (30172615)
 * @author Phuong Le (30175125)
 * @author Daniel Yakimenka (10185055)
 * @author Merick Parkinson (30196225) 
 * @author Farida Elogueil (30171114)
 */
public class StateTransitionTests {

	// - - - - - - - - - - Constructor tests - - - - - - - - - -
	@Test(expected = NullPointerException.class)
	public void testConstructorWithNullInitial() {
		new StateTransition(null, States.NORMAL);
	}

	
	@Test(expected = NullPointerException.class)
	public void testConstructorWithNullFinal() {
		new StateTransition(States.NORMAL, null);
	}

	
	@Test
	public void testValidConstructor() {
		StateTransition transition = new StateTransition(States.NORMAL, States.BLOCKED);
		assertNotNull("The StateTransition object should not be null", transition);
		assertEquals("Initial state should be NORMAL", States.NORMAL, transition.getInitialState());
		assertEquals("Final state should be BLOCKED", States.BLOCKED, transition.getFinalState());
	}

	// - - - - - - - - - - Equals tests - - - - - - - - - -

	@Test
	public void testEqualsWithSameObject() {
		StateTransition transition = new StateTransition(States.NORMAL, States.BLOCKED);
		assertTrue("A StateTransition object should be equal to itself", transition.equals(transition));
	}

	
	@Test
	public void testEqualsWithEqualStates() {
		StateTransition st1 = new StateTransition(States.NORMAL, States.BLOCKED);
		StateTransition st2 = new StateTransition(States.NORMAL, States.BLOCKED);
		assertTrue("Two StateTransition objects with the same states should be equal", st1.equals(st2));
	}

	
	@Test
	public void testEqualsWithDifferentStates() {
		StateTransition st1 = new StateTransition(States.NORMAL, States.BLOCKED);
		StateTransition st3 = new StateTransition(States.BLOCKED, States.NORMAL);
		assertFalse("Two StateTransition objects with different states should not be equal", st1.equals(st3));
	}

	
	@Test
	public void testEqualsWithNonStateTransitionObject() {
		StateTransition st1 = new StateTransition(States.NORMAL, States.BLOCKED);
		Object obj = new Object();
		assertFalse("A StateTransition object should not be equal to a non-StateTransition object", st1.equals(obj));
	}
}
