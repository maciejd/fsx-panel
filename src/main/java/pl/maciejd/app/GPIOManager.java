package pl.maciejd.app;

import java.util.ArrayList;
import java.util.List;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: Java Examples
 * FILENAME      :  TriggerGpioExample.java  
 * 
 * This file is part of the Pi4J project. More information about 
 * this project can be found here:  http://www.pi4j.com/
 * **********************************************************************
 * %%
 * Copyright (C) 2012 - 2015 Pi4J
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

import java.util.concurrent.Callable;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.trigger.GpioCallbackTrigger;
import com.pi4j.io.gpio.trigger.GpioPulseStateTrigger;
import com.pi4j.io.gpio.trigger.GpioSetStateTrigger;
import com.pi4j.io.gpio.trigger.GpioSyncStateTrigger;

/**
 * This example code demonstrates how to setup simple triggers for GPIO pins on
 * the Raspberry Pi.
 * 
 * @author Robert Savage
 */
public class GPIOManager {

	final GpioController gpio = GpioFactory.getInstance();

	final GpioPinDigitalInput button1 = gpio.provisionDigitalInputPin(RaspiPin.GPIO_02, PinPullResistance.PULL_DOWN);
	// final GpioPinDigitalInput button2 =
	// gpio.provisionDigitalInputPin(RaspiPin.GPIO_02,
	// PinPullResistance.PULL_DOWN);
	final GpioPinDigitalOutput led1 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_04, "Green LED", PinState.LOW);
	final GpioPinDigitalOutput led2 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_05, "Yellow LED", PinState.LOW);
	final GpioPinDigitalOutput led3 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_06, "Red LED", PinState.LOW);
	// final GpioPinDigitalOutput led4 =
	// gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01, "MyLED", PinState.LOW);
	private List<GpioPinDigitalOutput> leds = new ArrayList<GpioPinDigitalOutput>();

	public void setup() throws Exception {

		System.out.println("<--Pi4J--> GPIO Trigger Example ... started.");

		leds.add(led1);
		leds.add(led2);
		leds.add(led3);
		// leds.add(led4);

		button1.addTrigger(new GpioCallbackTrigger(new Callable<Void>() {
			public Void call() throws Exception {
				System.out.println(" --> GPIO TRIGGER CALLBACK RECEIVED ");
				return null;
			}
		}));

	}

	public boolean buttonPressed() {
		return button1.isHigh();
	}

	public void green() {
		singleLedOn(led1);
	}

	public void yellow() {
		singleLedOn(led2);
	}

	public void red() {
		singleLedOn(led3);
	}

	public void kill() {
		gpio.shutdown();
	}

	public void ledOff() {
		for (GpioPinDigitalOutput led : leds) {
			led.setState(PinState.LOW);
		}

	}

	private void singleLedOn(GpioPinDigitalOutput selectedLed) {
		for (GpioPinDigitalOutput led : leds) {
			if (led.equals(selectedLed)) {
				led.setState(PinState.HIGH);
			} else {
				led.setState(PinState.LOW);
			}
		}
	}
}