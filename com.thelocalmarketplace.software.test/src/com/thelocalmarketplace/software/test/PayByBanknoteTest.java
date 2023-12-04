 /**
 *Project 3 Iteration Group 4
 *  Group Members:
 * - Julie Kim 10123567
 * - Aryaman Sandhu 30017164
 * - Arcleah Pascual 30056034
 * - Aoi Ueki 30179305
 * - Ernest Shukla 30156303
 * - Shawn Hanlon 10021510
 * - Jaimie Marchuk 30112841
 * - Sofia Rubio 30113733
 * - Maria Munoz 30175339
 * - Anne Lumumba 30171346
 * - Nathaniel Dafoe 30181948
 * - Arvin Bolbolanardestani 30165484
 * - Anthony Chan 30174703
 * - Marvellous Chukwukelu 30197270
 * - Farida Elogueil 30171114
 * - Ahmed Elshabasi 30188386
 * - Shawn Hanlon 10021510
 * - Steven Huang 30145866
 * - Nada Mohamed 30183972
 * - Jon Mulyk 30093143
 * - Althea Non 30172442
 * - Minori Olguin 30035923
 * - Kelly Osena 30074352
 * - Muhib Qureshi 30076351
 * - Sofia Rubio 30113733
 * - Muzammil Saleem 30180889
 * - Steven Susorov 30197973
 * - Lydia Swiegers 30174059
 * - Elizabeth Szentmiklossy 30165216
 * - Anthony Tolentino 30081427
 * - Johnny Tran 30140472
 * - Kaylee Xiao 30173778
 */

package com.thelocalmarketplace.software.test;

import java.math.BigDecimal;
import java.util.Currency;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.tdc.banknote.BanknoteValidator;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.hardware.SelfCheckoutStationBronze;
import com.thelocalmarketplace.software.PayByBanknote;
import com.thelocalmarketplace.software.Software;

import powerutility.PowerGrid;

public class PayByBanknoteTest {
	private Software checkout;
	private SelfCheckoutStationBronze station;

	public BanknoteValidator banknoteValidator;
	public PayByBanknote payByBanknote;
	private BigDecimal[] billDenominations;
	private static final Currency currency = Currency.getInstance("CAD");

	
	@Before
	public void setUp() {
		AbstractSelfCheckoutStation.resetConfigurationToDefaults();
		station = new SelfCheckoutStationBronze();
		checkout = Software.getInstance(station);
		PowerGrid poewerGrid = PowerGrid.instance();
		station.plugIn(poewerGrid);
		station.turnOn();
		checkout.turnOn();
		checkout.unblockCustomer();
		payByBanknote = new PayByBanknote(checkout);
		
		billDenominations = new BigDecimal[5];
       billDenominations[0] = new BigDecimal("5.00");
       billDenominations[1] = new BigDecimal("10.00");
       billDenominations[2] = new BigDecimal("20.00");
       billDenominations[3] = new BigDecimal("50.00");
       billDenominations[4] = new BigDecimal("100.00");
       banknoteValidator = new BanknoteValidator(currency, billDenominations);
	}
	
	@Test
	public void enabledTest() {
		payByBanknote.enabled(checkout.banknoteValidator);
	}
	
	@Test
	public void disabledTest() {
		payByBanknote.disabled(checkout.banknoteValidator);
}
	
	@Test
	public void turnedOnTest() {
		payByBanknote.turnedOn(checkout.banknoteValidator);
	}
	
	@Test
	public void turnedOffTest() {
		payByBanknote.turnedOff(checkout.banknoteValidator);
	}
	
	@Test
	public void goodBanknote_AmountPayedIsLess_Test() {
		BigDecimal orderTotal = new BigDecimal("10.00");
		checkout.addToOrderTotal(orderTotal);
		BigDecimal payment = new BigDecimal("5.00");
		BigDecimal amountDue = orderTotal.subtract(payment);
		payByBanknote.goodBanknote(checkout.banknoteValidator, currency, payment);
		Assert.assertEquals(amountDue, checkout.getOrderTotal());

	}

	@Test
	public void goodBanknote_AmountPayedIsMore_Test() {
		BigDecimal orderTotal = new BigDecimal("10.00");
		checkout.addToOrderTotal(orderTotal);
		BigDecimal payment = new BigDecimal("15.00");
		payByBanknote.goodBanknote(checkout.banknoteValidator, currency, payment);
		Assert.assertEquals(BigDecimal.ZERO, checkout.getOrderTotal());
	}

	@Test
	public void goodBanknote_AmountPayedIsMoreCents_Test() {
		BigDecimal orderTotal = new BigDecimal("10.00");
		checkout.addToOrderTotal(orderTotal);
		BigDecimal payment = new BigDecimal("10.01");
		payByBanknote.goodBanknote(checkout.banknoteValidator, currency, payment);
		Assert.assertEquals(BigDecimal.ZERO, checkout.getOrderTotal());
	}

	@Test
	public void goodBanknote_TotalIs0_Test() {
		BigDecimal orderTotal = new BigDecimal("0");
		checkout.addToOrderTotal(orderTotal);
		BigDecimal payment = new BigDecimal("15.00");
		payByBanknote.goodBanknote(checkout.banknoteValidator, currency, payment);
		Assert.assertEquals(BigDecimal.ZERO, checkout.getOrderTotal());
	}

	@Test 
	public void goodBanknote_MultiplePayments_Test() {
		BigDecimal orderTotal = new BigDecimal("50");
		checkout.addToOrderTotal(orderTotal);
		BigDecimal payment = new BigDecimal("15.00");
		BigDecimal amountDue = orderTotal.subtract(payment);
		payByBanknote.goodBanknote(checkout.banknoteValidator, currency, payment);
		Assert.assertEquals(amountDue, checkout.getOrderTotal());

		payByBanknote.goodBanknote(checkout.banknoteValidator, currency, payment);
		amountDue = amountDue.subtract(payment);
		Assert.assertEquals(amountDue, checkout.getOrderTotal());

	}

	@Test
	public void badBanknoteTest() {
		payByBanknote.badBanknote(checkout.banknoteValidator);
	}

	@After
	public void tearDown() {
		payByBanknote = null;
	}

}


