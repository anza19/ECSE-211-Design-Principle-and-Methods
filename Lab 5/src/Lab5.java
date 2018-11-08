//Ryan Chalmers 260581055
//Anza Khan 260618490
package ev3ObjectDetection;

import lejos.hardware.*;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.*;
import lejos.robotics.SampleProvider;

public class Lab5 {

	// Static Resources:
	// Left motor connected to output A
	// Right motor connected to output D
	// Ultrasonic sensor port connected to input S1
	// Color sensor port connected to input S2
	private static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
	private static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
	private static final EV3LargeRegulatedMotor armMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("B"));
	private static final Port usPort = LocalEV3.get().getPort("S4");		
	private static final Port colorPort = LocalEV3.get().getPort("S1");
	public static final double TRACK = 13;
	public static final double WHEEL_RADIUS = 2.1;
	public static final double USDISTANCE = 7;

	
	public static void main(String[] args) {
		
		//Setup ultrasonic sensor
		// 1. Create a port object attached to a physical port (done above)
		// 2. Create a sensor instance and attach to port
		// 3. Create a sample provider instance for the above and initialize operating mode
		// 4. Create a buffer for the sensor data
		@SuppressWarnings("resource")							    	// Because we don't bother to close this resource
		SensorModes usSensor = new EV3UltrasonicSensor(usPort);
		SampleProvider usValue = usSensor.getMode("Distance");			// colorValue provides samples from this instance
		float[] usData = new float[usValue.sampleSize()];				// colorData is the buffer in which data are returned
		
		//Setup color sensor
		// 1. Create a port object attached to a physical port (done above)
		// 2. Create a sensor instance and attach to port
		// 3. Create a sample provider instance for the above and initialize operating mode
		// 4. Create a buffer for the sensor data
		
		SensorModes colorSensor = new EV3ColorSensor(colorPort);
		SampleProvider colorValue = colorSensor.getMode("RGB");			// colorValue provides samples from this instance
		float[] colorData = new float[colorValue.sampleSize()];	
		
				
		// setup the odometer and display and navigator
		Odometer odo = new Odometer(leftMotor, rightMotor, 30, true);
		
		Identify id = new Identify(colorData, colorValue);
		
		
		//used for localization
		Navigation nav = new Navigation(odo);
		
		//set up USnav to be used when searching for objects
		USnav usNav = new USnav(odo, id);
		UltrasonicPoller USpoller = new UltrasonicPoller(usValue, usData, usNav);
		
		final TextLCD t = LocalEV3.get().getTextLCD();
		
		int buttonChoice;
		do {
			// clear the display
			t.clear();

			t.drawString("< Left | Right >", 0, 0);
			t.drawString("       |        ", 0, 1);
			t.drawString("Search |Identify", 0, 2);
			t.drawString("       |        ", 0, 3);
			t.drawString("       |        ", 0, 4);

			buttonChoice = Button.waitForAnyPress();
		} while (buttonChoice != Button.ID_LEFT
				&& buttonChoice != Button.ID_RIGHT);

		
		if (buttonChoice == Button.ID_LEFT) {
			// perform the ultrasonic localization
			
			//start up the lcd screen info
			LCDInfo lcd = new LCDInfo(odo, id, usNav);
			//localize
			USLocalizer usl = new USLocalizer(odo, usValue, usData, USLocalizer.LocalizationType.FALLING_EDGE, nav);
			usl.doLocalization();
			//usNav is the search program
			usNav.start();
			USpoller.start();
		} else if(buttonChoice == Button.ID_RIGHT){
			LCDInfo lcd = new LCDInfo(odo, id, usNav);
			USpoller.start();

		} else 
			System.exit(0);
		
		while (Button.waitForAnyPress() != Button.ID_ESCAPE);
		System.exit(0);	
		
		
	}

}
