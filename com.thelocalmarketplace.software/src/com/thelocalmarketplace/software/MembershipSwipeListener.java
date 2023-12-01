

import com.jjjwelectronics.IDevice;
import com.jjjwelectronics.IDeviceListener;
import com.jjjwelectronics.card.*;
import com.jjjwelectronics.card.Card.CardData;

public class MembershipSwipeListener implements CardReaderListener {
    private Software softwareInstance;

    public MembershipSwipeListener(Software softwareInstance) {
        this.softwareInstance = softwareInstance;
    }

    @Override
    public void theDataFromACardHasBeenRead(CardData data) {
        String membershipNumber = data.getNumber();
        softwareInstance.handleMembershipNumber(membershipNumber);
    }

	@Override
	public void aDeviceHasBeenEnabled(IDevice<? extends IDeviceListener> device) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void aDeviceHasBeenDisabled(IDevice<? extends IDeviceListener> device) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void aDeviceHasBeenTurnedOn(IDevice<? extends IDeviceListener> device) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void aDeviceHasBeenTurnedOff(IDevice<? extends IDeviceListener> device) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void aCardHasBeenInserted() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void theCardHasBeenRemoved() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void aCardHasBeenTapped() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void aCardHasBeenSwiped() {
		// TODO Auto-generated method stub
		
	}
}
