//Group 62
//Anza Khan 260618490
//Ryan Chalmers 260581055
package ev3Navigator;

//imports.
import lejos.hardware.sensor.*;
import lejos.robotics.SampleProvider;
import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;
import ev3Navigator.Odometer;
import ev3Navigator.OdometryDisplay;

public class Lab3 {// Static Resources:
	
    //initialize left and right motors to ports 'A' and 'B' respectively
	private static final Port usPort = LocalEV3.get().getPort("S2");
    private static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
	private static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("B"));
	//declare robot dimensions
	public static final double TRACK = 13;
	public static final double WHEEL_RADIUS = 2.1;
	
	
	public static void main(String[] args) {
		//Initialize US sensor and SampleProvider (to get distances).
		SensorModes usSensor = new EV3UltrasonicSensor(usPort);		// usSensor is the instance
		SampleProvider usDistance = usSensor.getMode("Distance");	// usDistance provides samples from this instance
		float[] usData = new float[usDistance.sampleSize()];		// usData is the buffer in which data are returned

		//instantiate objects
		final TextLCD t = LocalEV3.get().getTextLCD();	
		Odometer odometer = new Odometer(leftMotor, rightMotor);
		OdometryDisplay odometryDisplay = new OdometryDisplay(odometer,t);
		
		//set up navigation
		Navigation navigation = new Navigation(odometer, t, leftMotor, rightMotor, 30, 60);
		//used with navigation avoidance
		USnav USNavigation = new USnav(odometer, t, leftMotor, rightMotor, 30, 60);
		//used to poll for readings from the US
		UltrasonicPoller USpoller = new UltrasonicPoller(usDistance, usData, USNavigation);
		int buttonChoice;
		do {
			// clear the display
			t.clear();

			t.drawString("< Left | Right >", 0, 0);
			t.drawString("       |        ", 0, 1);
			t.drawString(" Navi  |  Navi  ", 0, 2);
			t.drawString("       |   +    ", 0, 3);
			t.drawString("       | Avoid ", 0, 4);

			buttonChoice = Button.waitForAnyPress();
		} while (buttonChoice != Button.ID_LEFT
				&& buttonChoice != Button.ID_RIGHT);

		
		if (buttonChoice == Button.ID_RIGHT) {
			//start all threads including the USNavigation for avoiding obstacles
			odometer.start();
			odometryDisplay.start();
			USNavigation.start();
			USpoller.start();
		} else {
			//start all threads
			odometer.start();
			odometryDisplay.start();
			navigation.start();
		}
		
		while (Button.waitForAnyPress() != Button.ID_ESCAPE);
		System.exit(0);
	}

}
