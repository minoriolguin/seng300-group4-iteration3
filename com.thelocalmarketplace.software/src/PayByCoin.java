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

import com.tdc.IComponent;
import com.tdc.IComponentObserver;
import com.tdc.coin.Coin;
import com.tdc.coin.CoinValidator;
import com.tdc.coin.CoinValidatorObserver;

import java.math.BigDecimal;

public class PayByCoin extends AbstractPayByCash implements CoinValidatorObserver {
    public PayByCoin(SelfCheckoutSoftware station){
        super(station);
        station.coinValidator.attach(this);

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