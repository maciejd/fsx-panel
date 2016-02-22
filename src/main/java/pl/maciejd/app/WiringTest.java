package pl.maciejd.app;

import java.util.ArrayList;
import java.util.List;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

public class WiringTest {

	public static void main(String[] args) throws InterruptedException {
		
		final GpioController gpio = GpioFactory.getInstance();
		
		final GpioPinDigitalInput b1 = gpio.provisionDigitalInputPin(RaspiPin.GPIO_21, "Button #1",
				PinPullResistance.PULL_UP);
		final GpioPinDigitalInput b2 = gpio.provisionDigitalInputPin(RaspiPin.GPIO_22, "Button #2",
				PinPullResistance.PULL_UP);
		final GpioPinDigitalInput b3 = gpio.provisionDigitalInputPin(RaspiPin.GPIO_23, "Button #3",
				PinPullResistance.PULL_UP);
		final GpioPinDigitalInput b4 = gpio.provisionDigitalInputPin(RaspiPin.GPIO_24, "Button #4",
				PinPullResistance.PULL_UP);
		final GpioPinDigitalInput b5 = gpio.provisionDigitalInputPin(RaspiPin.GPIO_25, "Button #5",
				PinPullResistance.PULL_UP);
		
		List<GpioPinDigitalInput> buttons = new ArrayList<GpioPinDigitalInput>();
		
		buttons.add(b1);
		buttons.add(b2);
		buttons.add(b3);
		buttons.add(b4);
		buttons.add(b5);
		
		while (true) {
			for (GpioPinDigitalInput button: buttons) {
				isPressed(button);
			}
		}
	}
	
	public static void isPressed(GpioPinDigitalInput input) throws InterruptedException {
		if (input.getState().isLow()) {
			System.out.println(input.getName() + " is pressed!");
			Thread.sleep(200);
		}
	}
}
