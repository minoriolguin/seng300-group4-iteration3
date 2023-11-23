/**
* Jon Mulyk (UCID: 30093143)
* Elizabeth Szentmiklossy (UCID: 30165216)
* Ahmed Ibrahim Mohamed Seifledin Hadsan (UCID: 30174024)
* Arthur Huan (UCID: 30197354)
* Jaden Myers (UCID: 30152504)
* Jane Magai (UCID: 30180119)
* Ahmed Elshabasi (UCID: 30188386)
* Jincheng Li (UCID: 30172907)
* Sina Salahshour (UCID: 30177165)
* Anthony Tolentino (UCID: 30081427) */

package com.thelocalmarketplace.software;

import com.jjjwelectronics.AbstractDevice;
import com.jjjwelectronics.IDevice;
import com.jjjwelectronics.IDeviceListener;
import com.jjjwelectronics.Mass;
import com.jjjwelectronics.scale.AbstractElectronicScale;
import com.tdc.AbstractComponent;
import com.tdc.IComponent;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * Session class includes all
 * A Session object is created for each new user session
 * Constructed with arguments of a list of devices or components controlled by the software
 */

public class Session implements WeightDiscrepancyListner {
	private int status;  // 0 for not started, 1 for started and running, 2 for frozen (due to weight discrepency)
	private ArrayList<AbstractDevice> hardwareDevices = new ArrayList<AbstractDevice>();
	private ArrayList<AbstractComponent> hardwareComponents = new ArrayList<AbstractComponent>();

	public Session (Object... args) {
		for (Object arg : args) {
			if (arg instanceof AbstractDevice) {
				AbstractDevice temp_device = (AbstractDevice) arg;
				hardwareDevices.add(temp_device);
			} else if (arg instanceof AbstractComponent) {
				AbstractComponent temp_component = (AbstractComponent) arg;
				hardwareComponents.add(temp_component);
			} else {
				System.out.printf("Argument %s is neither a device or component.", arg);
			}
			this.freezeSession();
			status = 0;
			if (arg instanceof AbstractElectronicScale) {
				WeightDiscrepancy weight = new WeightDiscrepancy(Mass.ZERO,(AbstractElectronicScale) arg);
				weight.register(this);
			}
		}
	}

	public void setHardwareComponents(ArrayList<AbstractComponent> hardwareComponents) {
		this.hardwareComponents = hardwareComponents;
	}

	public void setHardwareDevices(ArrayList<AbstractDevice> hardwareDevices) {
		this.hardwareDevices = hardwareDevices;
	}

	public void addHardwareComponent(AbstractComponent... components) {
		for (AbstractComponent component : components) {
			hardwareComponents.add(component);
		}
	}

	public void addHardwareDevice(AbstractDevice... devices) {
		for (AbstractDevice device : devices) {
			hardwareDevices.add(device);
		}
	}

	public int getStatus() {
		return this.status;
	}

	public void startSession() {
		if (status == 0) {
			unfreezeSession();
		}
	}

	public void freezeSession() {
		status = 2;
		for (IDevice device : hardwareDevices) {
			if (!(device instanceof AbstractElectronicScale)) {
				device.disable();
			}
		}
		for (IComponent component : hardwareComponents) {
			component.disable();
		}
	}

	public void unfreezeSession() {
		status = 1;
		for (IDevice device : hardwareDevices) {
			device.enable();
		}
		for (IComponent component : hardwareComponents) {
			component.enable();
		}
	}

	@Override
	public void aDeviceHasBeenEnabled(IDevice<? extends IDeviceListener> device) {

	}

	@Override
	public void aDeviceHasBeenDisabled(IDevice<? extends IDeviceListener> device) {

	}

	@Override
	public void aDeviceHasBeenTurnedOn(IDevice<? extends IDeviceListener> device) {

	}

	@Override
	public void aDeviceHasBeenTurnedOff(IDevice<? extends IDeviceListener> device) {

	}

	@Override
	public void WeightDiscrancyOccurs() {
		if (status == 1) {
			this.freezeSession();
		}
	}

	@Override
	public void WeightDiscrancyResolved() {
		if (status == 2) {
			this.unfreezeSession();
		}
	}

	@Override
	public void addOwnBagsSelected() {

	}

	@Override
	public void addOwnBagDeselected() {

	}
}