
import com.tdc.IComponent;
import com.tdc.IComponentObserver;
import com.tdc.coin.Coin;
import com.tdc.coin.CoinValidator;
import com.tdc.coin.CoinValidatorObserver;

import java.math.BigDecimal;

public class PayByCoin extends AbstractPayByCash implements CoinValidatorObserver {
    public PayByCoin(Software software){
        super(software);
        software.coinValidator.attach(this);

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
     * An event announcing that the indicated coin has been detected and
     * determined to be valid.
     *
     * @param validator    The component on which the event occurred.
     * @param value        The value of the inserted coin.
     */

    @Override
    public void validCoinDetected(CoinValidator validator, BigDecimal value) {
        pay(Coin.DEFAULT_CURRENCY, value);
    }

    /**
     * An event announcing that the indicated coin has been detected and
     * determined to be invalid.
     *
     * @param validator The component on which the event occurred.
     */
    @Override
    public void invalidCoinDetected(CoinValidator validator) {

    }
}