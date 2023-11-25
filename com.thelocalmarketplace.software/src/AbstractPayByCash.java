
import com.tdc.CashOverloadException;
import com.tdc.DisabledException;
import com.tdc.banknote.Banknote;
import com.tdc.banknote.BanknoteDispensationSlot;
import com.tdc.coin.Coin;
import com.thelocalmarketplace.hardware.CoinTray;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Arrays;
import java.util.Currency;

/**
 * An abstract class that supports methods for paying and returning change, reducing redundancy.
 * Paying by banknote and paying by coin will use the methods implemented here
 * because the calculation process is identical
 */
public abstract class AbstractPayByCash {
    // The amount due by the customer
    private BigDecimal amountDue;
    // The main control software
    private final Software software;

    public AbstractPayByCash(Software software) {
        this.software = software;
    }

    /**
     * Updates the amount still due by the customer after each insertion of a banknote.
     * If the amount due is greater than 0, announce the amount still due by the customer
     * If the amount due is less than 0, dispense the amount of change due to the customer
     * If the amount due is equal to the total payment, payment has completed with exact amount
     * @param denomination The value of the banknote just inserted
     */
    public void pay(Currency currency, BigDecimal denomination) {
        // The current total price of the order
        amountDue = software.getOrderTotal();
        amountDue = amountDue.subtract(denomination);

        if (amountDue.compareTo(BigDecimal.ZERO) > 0) {
            // The payment has not completed, there is still amount due by the customer
            // Signal to the customer the amount still needed to complete payment
            System.out.println("Amount still due for payment: " + amountDue);
            software.setUpdatedOrderTotal(amountDue);
        } else if (amountDue.compareTo(BigDecimal.ZERO) < 0) {
            // The payment is completed, with change due to the customer
            // Signal to the customer that the payment is completed, with the change due
            System.out.println("Payment completed.\nChange due to customer: " + (amountDue.negate()));
            software.setUpdatedOrderTotal(amountDue);
            returnChange(currency);
            software.printReceipt.print();
            software.endSession();

            // Support for printing receipt will come in later iterations
        } else {
            // The payment is completed, with no change due; completed with exact amount
            // Signal to the customer that the payment is completed
            System.out.println("Payment complete with exact amount.");
            software.setUpdatedOrderTotal(amountDue);
            software.printReceipt.print();
            software.endSession();

            // Support for printing receipt will come in later iterations
        }
    }

    /**
     * Return the amount of change due to the customer
     * @param currency The amount of change due to the customer
     */
    public void returnChange(Currency currency) {
        amountDue = amountDue.negate();

        // List of banknotes to be returned
        ArrayList<Banknote> banknoteChangeList = new ArrayList<>();

        // List of coins to be returned
        ArrayList<Coin> coinChangeList = new ArrayList<>();

        // The denominations of banknotes
        BigDecimal[] banknoteDenominations = software.getBanknoteDenominations();

        // The denomination of coins
        List<BigDecimal> coinDenominations = software.getCoinDenominations();

        // Sort both denominations from greatest to least
        Arrays.sort(banknoteDenominations, Collections.reverseOrder());
        Collections.sort(coinDenominations, Collections.reverseOrder());

        // Return change is done using larger value banknotes as much as possible
        // Loop through each denomination from greatest to least
        for (BigDecimal denomination : banknoteDenominations) {
            // For each banknote, loop through until before the difference between
            // the amount due and the denomination becomes negative
            while (amountDue.subtract(denomination).compareTo(BigDecimal.ZERO) >= 0) {
                // Subtract the amount due by the denomination
                amountDue = amountDue.subtract(denomination);
                // Add the banknote to the list of banknotes to be returned
                banknoteChangeList.add(new Banknote(currency, denomination));
            }
        }

        // Return change is done using larger value coins as much as possible
        // Loop through each denomination from greatest to least
        for (BigDecimal denomination : coinDenominations) {
            // For each coin, loop through until before the difference between
            // the amount due and the denomination becomes negative
            while (amountDue.subtract(denomination).compareTo(BigDecimal.ZERO) >= 0) {
                // Subtract the amount due by the denomination
                amountDue = amountDue.subtract(denomination);
                // Add the coin to the list of coins to be returned
                coinChangeList.add(new Coin(currency, denomination));
            }
        }

        software.setUpdatedOrderTotal(amountDue);

        BanknoteDispensationSlot banknoteDispenser = software.getBanknoteDispenser();
        for (Banknote banknote : banknoteChangeList) {
            try {
                // For each banknote in the list of banknotes to be returned,
                // add them to the banknote dispenser
                banknoteDispenser.receive(banknote);
            } catch (DisabledException e) {
                // Throw exception when the dispenser is disabled
                // Should be handled by hardware
                System.out.println("DisabledException: " + e);
            } catch (CashOverloadException e) {
                // Throw exception when the dispenser is overloaded
                // Should be handled by hardware
                System.out.println("CashOverloadException: " + e);
            }
        }

        // Dispense the list of banknotes inserted into the dispenser
        banknoteDispenser.dispense();

        CoinTray coinTray = software.getCoinTray();
        for (Coin coin : coinChangeList) {
            try {
                // For each coin in the list of coins to be returned,
                // dispense that coin to the coin dispenser
                coinTray.receive(coin);
            } catch (DisabledException e) {
                // Throw exception when the dispenser is disabled
                // Should be handled by hardware
                System.out.println("DisabledException: " + e);
            } catch (CashOverloadException e) {
                // Throw exception when the dispenser is overloaded
                // Should be handled by hardware
                System.out.println("CashOverloadException: " + e);
            }
        }

    }
}