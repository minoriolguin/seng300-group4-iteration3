package com.thelocalmarketplace.software.test;

import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.hardware.SelfCheckoutStationGold;
import com.thelocalmarketplace.software.AbstractStateTransitionListener;
import com.thelocalmarketplace.software.logic.CentralStationLogic;
import com.thelocalmarketplace.software.logic.StateLogic;
import com.thelocalmarketplace.software.logic.StateLogic.States;

import powerutility.PowerGrid;

/** Tests AbstractStateTransitionListener
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
public class AbstractStateTransitionListenerTests {


    // Creates listener stub for testing 
    private class StateTransitionListenerStub extends AbstractStateTransitionListener {
        public boolean onTransitionCalled = false;
    	public StateTransitionListenerStub(CentralStationLogic logic) {
            super(logic);
        }

        @Override
        public void onTransition() {
            onTransitionCalled = true;
        }
    }
    
    private StateLogic stateLogic;
    private StateTransitionListenerStub listener;
    
    @Before 
    public void setup() {
    	AbstractSelfCheckoutStation.resetConfigurationToDefaults();
    	SelfCheckoutStationGold station = new SelfCheckoutStationGold();
        CentralStationLogic logic = new CentralStationLogic(station);
        stateLogic = logic.stateLogic;
        listener = new StateTransitionListenerStub(logic);
        PowerGrid.engageUninterruptiblePowerSource();
        
        station.plugIn(PowerGrid.instance());
        station.turnOn();
        
        stateLogic.registerListener(States.BLOCKED, listener);
    }
    
    @After
    public void teardown() {
    	PowerGrid.engageFaultyPowerSource();
    	AbstractSelfCheckoutStation.resetConfigurationToDefaults();
    }

    @Test(expected = NullPointerException.class)
    public void testConstructorWithNullLogic() {
        new StateTransitionListenerStub(null);
    }

    @Test
    public void testOnTransitionMethod() throws Exception {
        stateLogic.gotoState(States.BLOCKED);
        assertTrue("onTransition should be called during the state transition", listener.onTransitionCalled);
    }
    
    @Test
    public void testOnTransitionMethodSameState() throws Exception {
    	stateLogic.registerListener(States.NORMAL, listener);
        stateLogic.gotoState(States.NORMAL);
        assertTrue("onTransition should be called during the state transition", listener.onTransitionCalled);
    }
}
