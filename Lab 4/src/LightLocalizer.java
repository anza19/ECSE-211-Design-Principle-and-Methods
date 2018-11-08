package ev3Localization;

import lejos.hardware.Sound;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.robotics.SampleProvider;

public class LightLocalizer {
	
	//Set variables that we will need.
	private Odometer odo;
	private SampleProvider colorSensor;
	private float[] colorData;	
	private double wheelRadius = 2.1;
	public static double ROTATION_SPEED = 80;
	final static int ACCELERATION = 1000;
	private double width = 13.0;
	private double redLine = 30;
	private Navigation nav;
	private int count = 0;
	
	private EV3LargeRegulatedMotor leftMotor, rightMotor;
	
	
	//constructor
	public LightLocalizer(Odometer odo, SampleProvider colorSensor, float[] colorData, Navigation nav) {
		
		//Set variables from constructor
		this.odo = odo;
		this.colorSensor = colorSensor;
		this.colorData = colorData;
		this.nav = nav;
		
		//get access to motors
		EV3LargeRegulatedMotor[] motors = this.odo.getMotors();
		this.leftMotor = motors[0];
		this.rightMotor = motors[1];
		
		//set acceleration
		this.leftMotor.setAcceleration(ACCELERATION);
		this.rightMotor.setAcceleration(ACCELERATION);
				
		//set speed
		leftMotor.setSpeed((int) ROTATION_SPEED);
		rightMotor.setSpeed((int) ROTATION_SPEED);
	}
	
	
	public void doLocalization() {
		
		double[] gridlines = new double[4];
		
		while(getData() > redLine){
			leftMotor.forward();
			rightMotor.forward();	
		}
		
		leftMotor.rotate(convertDistance(wheelRadius, -3), true);
		rightMotor.rotate(convertDistance(wheelRadius, -3), false);
		
		//Turn 90 degrees to the left.
		leftMotor.rotate(-convertAngle(wheelRadius, width, 90), true);
		rightMotor.rotate(convertAngle(wheelRadius, width, 90), false);
		
		//Drive forward until it sees a line.
		while(getData() > redLine){
			leftMotor.forward();
			rightMotor.forward();		
		}
		
		leftMotor.rotate(convertDistance(wheelRadius, 7), true);
		rightMotor.rotate(convertDistance(wheelRadius, 7), false);
		
		//Turn 90 degrees to the right
		leftMotor.rotate(convertAngle(wheelRadius, width, 90), true);
		rightMotor.rotate(-convertAngle(wheelRadius, width, 90), false);
		
		leftMotor.rotate(convertDistance(wheelRadius, 10), true);
		rightMotor.rotate(convertDistance(wheelRadius, 10), false);
		
		//Start rotating and clock all 4 gridlines.
		rightMotor.backward();
		leftMotor.forward();
		
		while(count<4){
			if(getData()<redLine){
				gridlines[count]=odo.getAng();
				Sound.beep();
				count++;
				try{
				Thread.sleep(500);
				} catch(Exception e){
					
				}
			}
		}
		
		
		
		//Stop the motors.
		leftMotor.stop(true);
		rightMotor.stop(true);
		
		nav.turnTo(0, true);
		
	}
	
	//Get the color sensor data.
	private float getData() {
		colorSensor.fetchSample(colorData, 0);
		float distance = 100*colorData[0];
				
		return distance;
	}
	
	//convertDistance and converAngle methods for helping the robot travel.
	private static int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}
	
	private static int convertAngle(double radius, double width, double angle) {
		return convertDistance(radius, Math.PI * width * angle / 360.0);
	}

}
