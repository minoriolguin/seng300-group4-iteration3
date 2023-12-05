 /**
 *Project, Iteration 3, Group 4
 *  Group Members:
 * - Arvin Bolbolanardestani / 30165484
 * - Anthony Chan / 30174703
 * - Marvellous Chukwukelu / 30197270
 * - Farida Elogueil / 30171114
 * - Ahmed Elshabasi / 30188386
 * - Shawn Hanlon / 10021510
 * - Steven Huang / 30145866
 * - Nada Mohamed / 30183972
 * - Jon Mulyk / 30093143
 * - Althea Non / 30172442
 * - Minori Olguin / 30035923
 * - Kelly Osena / 30074352
 * - Muhib Qureshi / 30076351
 * - Sofia Rubio / 30113733
 * - Muzammil Saleem / 30180889
 * - Steven Susorov / 30197973
 * - Lydia Swiegers / 30174059
 * - Elizabeth Szentmiklossy / 30165216
 * - Anthony Tolentino / 30081427
 * - Johnny Tran / 30140472
 * - Kaylee Xiao / 30173778 
 **/

package com.thelocalmarketplace.software.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)				
@Suite.SuiteClasses({
		AbstractPayByCashTest.class,
		AttendantTest.class,
		BlackBoxTests.class,
		MaintenanceTest.class,
		PayByBanknoteTest.class,
		PayByCardTest.class,
		PayByCoinTest.class,
		PrintReceiptTest.class,
		PurchaseBagsTest.class,
		SelectLanguageTest.class,
		SoftwareTest.class,
		SoftwareTest.class,
		UpdateCartTest.class,
		WeightDiscrepancyTest.class,
})
public class TestSuiteMain {}
