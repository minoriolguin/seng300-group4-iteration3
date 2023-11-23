/**
* Jon Mulyk (UCID: 30093143)
* Elizabeth Szentmiklossy (UCID: 30165216)
* Ahmed Ibrahim Mohamed Seifledin Hadsan (UCID: 30174024)
* Arthur Huan (UCID: 30197354)
* Jaden Myers (UCID: 30152504)
* Jane Magai (UCID: 30180119)
* Ahmed Elshabasi (UCID: 30188386)
* Jincheng Li (UCID: 30172907)
* Sina Salahshour (UCID: 30177165)
* Anthony Tolentino (UCID: 30081427) */

package com.thelocalmarketplace.software.test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Map;
import org.junit.*;

import com.tdc.CashOverloadException;
import com.tdc.DisabledException;
import com.tdc.Sink;
import com.tdc.banknote.AbstractBanknoteDispenser;
import com.tdc.banknote.Banknote;
import com.tdc.banknote.BanknoteDispensationSlot;
import com.tdc.banknote.BanknoteDispenserBronze;
import com.tdc.banknote.BanknoteDispenserGold;
import com.tdc.banknote.BanknoteInsertionSlot;
import com.tdc.banknote.IBanknoteDispenser;
import com.thelocalmarketplace.software.PayViaBanknote;

import ca.ucalgary.seng300.simulation.NullPointerSimulationException;
import ca.ucalgary.seng300.simulation.SimulationException;
import powerutility.NoPowerException;
import powerutility.PowerGrid;

public class PayViaBanknoteTest {
	private PayViaBanknote payViaBanknote;
	private BigDecimal amountOwed;
	private BanknoteDispensationSlot dispensationSlot;
	private BanknoteInsertionSlot insertionSlot;
	private List<Map.Entry<BigDecimal, AbstractBanknoteDispenser>> dispenserList;
	private PowerGrid grid;
	private AbstractBanknoteDispenser dispenser;

	
	// Stub class implementing Sink<T> interface.
	class mysink implements Sink<Banknote> {

		@Override
		public void receive(Banknote cash) throws CashOverloadException, DisabledException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean hasSpace() {
			// TODO Auto-generated method stub
			return true;
		}
		
	}
	

	
	/**
	 * Initializes objects and sets up each test case.
	 * @throws CashOverloadException 
	 */
	@Before
	public void setup() throws CashOverloadException {
		//Initializing Objects
		BigDecimal amountOwed = BigDecimal.valueOf(10);
		BigDecimal amountInserted = BigDecimal.ZERO;
		dispensationSlot = new BanknoteDispensationSlot();
		insertionSlot = new BanknoteInsertionSlot();
		dispenserList = new ArrayList<>();
		dispenser = new BanknoteDispenserBronze();
		dispenser.sink = new mysink();
		
		// Connecting components to a powerGrid.
		grid = PowerGrid.instance();
		dispenser.connect(grid);
		insertionSlot.connect(grid);
		dispensationSlot.connect(grid);
				
		// Activating and enabling components.
		dispensationSlot.activate();
		insertionSlot.activate();
		dispenser.activate();
		dispenser.enable();
		dispensationSlot.enable();
		insertionSlot.enable();
		
		// Adding a loaded dispenser to dispenser list	
		Banknote banknote1 = new Banknote( Currency.getInstance("CAD"),BigDecimal.valueOf(10));
		dispenser.load(banknote1);
		dispenserList.add(Map.entry(BigDecimal.TEN, dispenser));
		
	   // Instance of PayViaBanknote 
		payViaBanknote = new PayViaBanknote(amountOwed, dispensationSlot, insertionSlot, dispenserList);
		
	}
	
	/**
	* Test cases for makePayment(Banknote)
	* Tests for when payment is succesfull(full amount paid), unsuccesful(full amount not paid) 
	* and when the banknote is dangling (is rejected or not rejected by Insertionslot).
	*/
	
	@Test
	public void testPaymentSuccesful() throws DisabledException, CashOverloadException {
		Banknote banknote = new Banknote( Currency.getInstance("CAD"),BigDecimal.TEN); 
        assertTrue(payViaBanknote.makePayment(banknote));
      
	}

	@Test
	public void testPaymentUnsuccesful() throws DisabledException, CashOverloadException {
		Banknote banknote = new Banknote( Currency.getInstance("CAD"),BigDecimal.valueOf(5)); 
        assertEquals(false,payViaBanknote.makePayment(banknote));	//makepayment() will return false as payment is not complete	
	}
	@Test
	public void testdanglingnotes() throws DisabledException, CashOverloadException{
		Banknote banknotedangling = new Banknote( Currency.getInstance("CAD"),BigDecimal.TEN); 
		insertionSlot.emit(banknotedangling);
		assertEquals(false,payViaBanknote.makePayment(banknotedangling));
			
	}
	
	
	
	/**
	* Test cases for returnChange();
	* Tests when change is succesfully returned and when there is not enough banknotes hence not returned.
	* 
	*/
	@Test
	public void testReturnChangeSuccesful() throws CashOverloadException, DisabledException{		
		Banknote banknote = new Banknote( Currency.getInstance("CAD"),BigDecimal.valueOf(20)); 
		payViaBanknote.makePayment(banknote);
		payViaBanknote.returnChange();
		assertEquals(0, dispenser.size());
	}
	
	@Test 
	public void testReturnChangeUnsuccesful() throws CashOverloadException, DisabledException{
		dispenserList.clear();
		dispenser.unload();
		dispenserList.add(Map.entry(BigDecimal.TEN, dispenser));
		
		Banknote banknote = new Banknote( Currency.getInstance("CAD"),BigDecimal.valueOf(20)); 
		payViaBanknote.makePayment(banknote);		
		payViaBanknote.returnChange();			
		assertTrue(dispenser.isDisabled());

	}
	
	
	

}