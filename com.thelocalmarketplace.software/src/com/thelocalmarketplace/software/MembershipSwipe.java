

import com.jjjwelectronics.card.*;

public class MembershipSwipe {
    private ICardReader cardReader;
    private Software softwareInstance;

    public MembershipSwipe(Software softwareInstance, ICardReader cardReader) {
        this.softwareInstance = softwareInstance;
        this.cardReader = cardReader;
        initializeCardReader();
    }

    private void initializeCardReader() {
    	MembershipSwipeListener listener = new MembershipSwipeListener(softwareInstance);
        cardReader.register(listener); // Register the listener to the card reader
    }
}
