package com.thelocalmarketplace.software.controllers.pay;

import ca.ucalgary.seng300.simulation.InvalidStateSimulationException;
import com.jjjwelectronics.IDevice;
import com.jjjwelectronics.IDeviceListener;
import com.jjjwelectronics.card.Card.CardData;
import com.jjjwelectronics.card.CardReaderListener;
import com.thelocalmarketplace.software.AbstractLogicDependant;
import com.thelocalmarketplace.software.logic.CentralStationLogic;
import com.thelocalmarketplace.software.logic.CentralStationLogic.PaymentMethods;
import com.thelocalmarketplace.software.logic.StateLogic.States;

/**
 * Card Reader Controller
 * @author Maheen Nizmani (30172615)
 * --------------------------------
 * @author Connell Reffo (10186960)
 * @author Tara Strickland (10105877)
 * @author Angelina Rochon (30087177)
 * @author Julian Fan (30235289)
 * @author Braden Beler (30084941)
 * @author Samyog Dahal (30194624)
 * @author Phuong Le (30175125)
 * @author Daniel Yakimenka (10185055)
 * @author Merick Parkinson (30196225)
 */
public class CardReaderController extends AbstractLogicDependant implements CardReaderListener{
    
	/**
     * Base constructor
     * @param logic Reference to the central station logic
     * @throws NullPointerException If logic is null
     */
    public CardReaderController(CentralStationLogic logic) throws NullPointerException {
        super(logic);

        this.logic.hardware.cardReader.register(this);
    }


    //Ask for signature when card is swiped
    @Override
    public void aCardHasBeenSwiped() {
        System.out.println("A card has been swiped");
        this.logic.cardLogic.isDataRead(false);
    }

    @Override
    public void theDataFromACardHasBeenRead(CardData data) {
    	PaymentMethods t = this.logic.cardLogic.getCardPaymentType(data.getType());
   
        this.logic.cardLogic.isDataRead(true);

        if (!this.logic.isSessionStarted()) {
            throw new InvalidStateSimulationException("Session not started");
        }
        else if (!this.logic.stateLogic.inState(States.CHECKOUT)) {
            throw new InvalidStateSimulationException("Not ready for checkout");
        }
        else if (!this.logic.getSelectedPaymentMethod().equals(t)) {
        	throw new InvalidStateSimulationException("Pay by " + t.toString() + " not selected");
        }

        //check if transaction successful
        if(this.logic.cardLogic.approveTransaction(data.getNumber(),this.logic.cartLogic.getBalanceOwed().doubleValue())){

            //if successful reduce amount owed by customer otherwise do nothing
            this.logic.cartLogic.modifyBalance(logic.cartLogic.getBalanceOwed().negate());

        }

        System.out.println("Total owed: " + this.logic.cartLogic.getBalanceOwed());

    }
    
    // ---- Unused ----

    @Override
    public void aDeviceHasBeenEnabled(IDevice<? extends IDeviceListener> device) {

    }

    @Override
    public void aDeviceHasBeenDisabled(IDevice<? extends IDeviceListener> device) {

    }

    @Override
    public void aDeviceHasBeenTurnedOn(IDevice<? extends IDeviceListener> device) {

    }

    @Override
    public void aDeviceHasBeenTurnedOff(IDevice<? extends IDeviceListener> device) {

    }
}
