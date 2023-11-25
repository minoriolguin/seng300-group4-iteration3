
import com.tdc.IComponent;
import com.tdc.IComponentObserver;
import com.tdc.banknote.BanknoteValidator;
import com.tdc.banknote.BanknoteValidatorObserver;

import java.math.BigDecimal;
import java.util.Currency;
/**
 * A class that supports methods for paying with banknotes
 */
public class PayByBanknote extends AbstractPayByCash implements BanknoteValidatorObserver {
    public PayByBanknote(Software software){
        super(software);
        software.banknoteValidator.attach(this);
    }

    /**
     * Announces that the indicated component has been enabled.
     *
     * @param component The component that has been enabled.
     */
    @Override
    public void enabled(IComponent<? extends IComponentObserver> component) {

    }

    /**
     * Announces that the indicated component has been disabled.
     *
     * @param component The component that has been disabled.
     */
    @Override
    public void disabled(IComponent<? extends IComponentObserver> component) {

    }

    /**
     * Announces that the indicated component has been turned on.
     *
     * @param component The component that has been turned on.
     */
    @Override
    public void turnedOn(IComponent<? extends IComponentObserver> component) {

    }

    /**
     * Announces that the indicated component has been turned off.
     *
     * @param component The component that has been turned off.
     */
    @Override
    public void turnedOff(IComponent<? extends IComponentObserver> component) {

    }

    /**
     * An event announcing that the indicated banknote has been detected and
     * determined to be valid.
     *
     * @param validator    The component on which the event occurred.
     * @param currency     The kind of currency of the inserted banknote.
     * @param denomination The value of the inserted banknote.
     */
    @Override
    public void goodBanknote(BanknoteValidator validator, Currency currency, BigDecimal denomination) {
        pay(currency, denomination);
    }

    /**
     * An event announcing that the indicated banknote has been detected and
     * determined to be invalid.
     *
     * @param validator The component on which the event occurred.
     */
    @Override
    public void badBanknote(BanknoteValidator validator) {

    }
}
