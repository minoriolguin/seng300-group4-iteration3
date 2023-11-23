package com.thelocalmarketplace.software.controllers.pay.cash;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.tdc.IComponent;
import com.tdc.IComponentObserver;
import com.tdc.coin.Coin;
import com.tdc.coin.CoinDispenserObserver;
import com.tdc.coin.ICoinDispenser;
import com.thelocalmarketplace.software.AbstractLogicDependant;
import com.thelocalmarketplace.software.logic.CentralStationLogic;

/**
 * Represents an object that will control a coin dispenser of a specific coin denomination
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
 */
public class CoinDispenserController extends AbstractLogicDependant implements CoinDispenserObserver {
	
	/**
	 * List of available coins of this denomination
	 */
	private List<Coin> available;

	
	/**
	 * Base constructor
	 * @param logic Is the reference to the logic
	 * @param denomination Is the denomination this controller will dispense
	 * @throws NullPointerException If any argument is null
	 */
	public CoinDispenserController(CentralStationLogic logic, BigDecimal denomination) throws NullPointerException {
		super(logic);
		
		if (denomination == null) {
			throw new NullPointerException("Denomination");
		}
		
		this.available = new ArrayList<>();
		
		// Attach self to specific dispenser corresponding to its denomination
		this.logic.hardware.coinDispensers.get(denomination).attach(this);
	}
	
	/**
	 * Gets a list of coins of corresponding denomination that are available as change
	 * @return The list of coins
	 */
	public List<Coin> getAvailableChange() {
		return this.available;
	}
	
	@Override
	public void coinsEmpty(ICoinDispenser dispenser) {
		this.available.clear();
	}

	@Override
	public void coinAdded(ICoinDispenser dispenser, Coin coin) {
		this.available.add(coin);
	}

	@Override
	public void coinRemoved(ICoinDispenser dispenser, Coin coin) {
		this.available.remove(coin);
	}
	
	@Override
	public void coinsLoaded(ICoinDispenser dispenser, Coin... coins) {
		for (Coin c : coins) {
			this.available.add(c);
		}
	}

	@Override
	public void coinsUnloaded(ICoinDispenser dispenser, Coin... coins) {
		for (Coin c : coins) {
			this.available.remove(c);
		}
	}
	
	// ------ Unused -------

	@Override
	public void enabled(IComponent<? extends IComponentObserver> component) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void disabled(IComponent<? extends IComponentObserver> component) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void turnedOn(IComponent<? extends IComponentObserver> component) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void turnedOff(IComponent<? extends IComponentObserver> component) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void coinsFull(ICoinDispenser dispenser) {
		// TODO Auto-generated method stub
		
	}
}
