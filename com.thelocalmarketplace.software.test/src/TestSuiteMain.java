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

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)				
@Suite.SuiteClasses({
	AbstractPayByCashTest.class,
	AttendantTest.class,
	PayByBanknoteTest.class,
	PayByCoinTest.class,
//	PayBySwipeTest.class
	PrintReceiptTest.class,
	SelfCheckoutSoftwareTest.class,
	UpdateCartTest.class,
	WeightDiscrepancyTest.class,
})
public class TestSuiteMain {}
