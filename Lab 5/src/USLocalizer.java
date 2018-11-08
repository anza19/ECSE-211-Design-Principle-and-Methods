

package ev3ObjectDetection;

import lejos.hardware.Sound;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.robotics.SampleProvider;

public class USLocalizer {
	
	//Set all variables that we will need in this class.
	public enum LocalizationType { FALLING_EDGE, RISING_EDGE };
	private double theta;
	
	private Odometer odo;
	private SampleProvider usSensor;
	private float[] usData;
	private LocalizationType locType;
	private EV3LargeRegulatedMotor leftMotor, rightMotor;
	private Navigation nav;
	private double error = 10;

	public USLocalizer(Odometer odo, SampleProvider usSensor, float[] usData, LocalizationType locType, Navigation nav) {
		
		this.odo = odo;
		this.usSensor = usSensor;
		this.usData = usData;
		this.locType = locType;
		this.nav = nav;
		
		//Get access to motors.
		EV3LargeRegulatedMotor[] motors = this.odo.getMotors();
		this.leftMotor = motors[0];
		this.rightMotor = motors[1];
		//Set speed.
		leftMotor.setSpeed(70);
		rightMotor.setSpeed(70);
		
		//Set acceleration.
		this.leftMotor.setAcceleration(1000);
		this.rightMotor.setAcceleration(1000);
		
		
	}
	
	//US localization method.
	public void doLocalization() {
		double [] pos = new double [3];
		double angleA, angleB;
		
		if(getData()>50){
			locType = LocalizationType.FALLING_EDGE;
		}
		else{
			locType = LocalizationType.RISING_EDGE;
		}
		
		
		if (locType == LocalizationType.FALLING_EDGE) {
			
			//rotate the robot right until it sees no wall
			while(getData() < 30 + error){
				rightMotor.backward();
				leftMotor.forward();
			}
			Sound.beep();
			
			//keep rotating right until the robot sees a wall, then latch angle
			while(getData() > 30){
				rightMotor.backward();
				leftMotor.forward();
			}
			angleA = odo.getAng();
			Sound.beep();
			
			
			//Switch direction and turn left until it sees no wall.
			while(getData() < 30 + error){
				leftMotor.backward();
				rightMotor.forward();
			}
			Sound.beep();
			
			//Keep rotating until the robot sees a wall, then latch the current
			//odometer angle; set it to AngleB
			while(getData() > 30){
				leftMotor.backward();
				rightMotor.forward();
			}
			Sound.beep();
			angleB = odo.getAng();
			
			//Stop both motors
			leftMotor.stop(true);
			rightMotor.stop(true);
			
			//Set deltaTheta 
			if(angleA > angleB) {
				theta = 225 - (angleB + angleA)/2 + odo.getAng();
			}
			else if(angleA < angleB) {
				theta = 45 - (angleB + angleA)/2 + odo.getAng();
			}
			Sound.beep();
			// update the odometer position
			odo.setPosition(new double [] {0.0, 0.0, theta}, new boolean [] {true, true, true});
			
			//Turn to 0, so that it faces the right direction.
			nav.turnTo(0, true);
			
		} else {
			/*
			 * The robot should turn until it sees the wall, then look for the
			 * "rising edges:" the points where it no longer sees the wall.
			 * This is very similar to the FALLING_EDGE routine, but the robot
			 * will face toward the wall for most of it.
			 */
			
			//While robot does not see a wall (US reading > 40), rotate left
			while(getData() > 30){
				leftMotor.backward();
				rightMotor.forward();
			}
			Sound.beep();	
			
			//rotate until the robot sees no wall, then set it to angleA
			while(getData() < 30 + error){
				leftMotor.backward();
				rightMotor.forward();
			}
			Sound.beep();
			angleA = odo.getAng();

			//rotate other direction until it sees a wall
			while(getData() > 30){
				leftMotor.forward();
				rightMotor.backward();
			}
			Sound.beep();
			
			//rotate until the robot doesnt see a wall, then latch the angle to angleB
			while(getData() < 30 + error){
				leftMotor.forward();
				rightMotor.backward();
			}
			Sound.beep();
			angleB = odo.getAng();
			
			//stop motors
			leftMotor.stop(true);
			rightMotor.stop(true);
			
			
			//Set deltaTheta depending on how angles worked out.
			if(angleA > angleB) {
				theta = 225 - (angleB + angleA)/2 + odo.getAng();
			}
			else if(angleA < angleB) {
				theta = 45 - (angleB + angleA)/2 + odo.getAng();
			}
			Sound.buzz();
			
			// update the odometer position (example to follow:)
			odo.setPosition(new double [] {-15.0, -15.0, theta}, new boolean [] {true, true, true});
			
			//Turn to 0, so that it faces the right direction.
			nav.turnTo(0, true);

		}
	}
	
	//Get US data.
	private float getData() {
		usSensor.fetchSample(usData, 0);
		//usdata contains the distance data from the US sensor
		
		float distance = 100*usData[0];
				
		return distance;
	}
}
