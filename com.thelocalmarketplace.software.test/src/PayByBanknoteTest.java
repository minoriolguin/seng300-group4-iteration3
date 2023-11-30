// Project 2 Iteration Group 3
//Julie Kim 10123567
//Aryaman Sandhu 30017164
//Arcleah Pascual 30056034
//Aoi Ueki 30179305
//Ernest Shukla 30156303
//Shawn Hanlon 10021510
//Jaimie Marchuk 30112841
//Sofia Rubio 30113733
//Maria Munoz 30175339
//Anne Lumumba 30171346
//Nathaniel Dafoe 30181948

import java.math.BigDecimal;
import java.util.Currency;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.tdc.banknote.BanknoteValidator;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.hardware.SelfCheckoutStationBronze;

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


