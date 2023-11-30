import java.io.IOException;
import java.math.*;
import java.util.*;

import com.jjjwelectronics.card.*;
import com.thelocalmarketplace.hardware.external.CardIssuer;
import org.junit.*;
import com.tdc.CashOverloadException;
import com.tdc.DisabledException;
import com.tdc.banknote.Banknote;
import com.tdc.coin.*;
import com.thelocalmarketplace.hardware.*;
//import com.thelocalmarketplace.software.*;
import powerutility.*;

public class PayByCardTest {

    private SelfCheckoutStationBronze stationBronze;
    private PowerGrid powerGrid;
    //private Currency CAD;
    private Software station;
    private Card tapCreditCard = new Card("credit", "12345", "Jack",
                                                "123", "8960", true, true);

    private Card swipeCreditCard = new Card("credit", "234567", "John",
            "245", "7429", false, false);

    private Card insertCreditCard = new Card("credit", "123456", "Jill",
            "980", "2321", false, true);

    @Before
    public void setUp() {
        // set up coinValidator

        stationBronze = new SelfCheckoutStationBronze();
        station = Software.getInstance(stationBronze);
        powerGrid = PowerGrid.instance();
        stationBronze.plugIn(powerGrid);
        stationBronze.turnOn();
        station.turnOn();

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR,2);
        station.payByCard.addCardData("credit","12345", "jack", calendar,"123",420 );
        station.payByCard.addCardData("credit","234567", "John", calendar,"245",120 );
        station.payByCard.addCardData("credit", "123456", "Jill", calendar, "980", 456);

    }

    @Test
    public void testCreditTap() throws IOException
    {
        System.out.println("Test when payment with Credit Card tap: ");
        station.addToOrderTotal(new BigDecimal ("6.50"));


        station.cardReader.tap(tapCreditCard);

        BigDecimal result = station.getOrderTotal();
        Assert.assertEquals(BigDecimal.ZERO, result);

    }

    @Test
    public void testCreditSwipe() throws IOException
    {
        System.out.println("Test when payment with Credit Card swipe: ");
        station.addToOrderTotal(new BigDecimal ("15"));

        station.cardReader.swipe(swipeCreditCard);

        BigDecimal result = station.getOrderTotal();
        Assert.assertEquals(BigDecimal.ZERO, result);
    }

    @Test
    public void testCreditInsert() throws IOException
    {
        System.out.println("Test when payment with Credit Card insert: ");
        station.addToOrderTotal(new BigDecimal ("6.50"));

        station.cardReader.insert(insertCreditCard, "2321");

        BigDecimal result = station.getOrderTotal();
        Assert.assertEquals(BigDecimal.ZERO, result);

    }
}
