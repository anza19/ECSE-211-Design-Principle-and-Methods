package ev3ObjectDetection;

import lejos.hardware.Sound;
import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class USnav extends Thread implements UltrasonicController {
	
	//everything below same as nav until processUSData
	final static int FAST = 200, SLOW = 20, ACCELERATION = 4000;
	final static double DEG_ERR = 3.0, CM_ERR = 1.0;
	private Odometer odometer;
	private Identify id;
	private EV3LargeRegulatedMotor leftMotor, rightMotor;
	private boolean isWood;
	private double USSensorReading;
	private double destx, desty;
	private boolean objDetected, isBlue;
	private boolean isSearching = false;
	
	public USnav(Odometer odo, Identify id) {
	this.odometer = odo;
	this.id = id;

	EV3LargeRegulatedMotor[] motors = this.odometer.getMotors();
	this.leftMotor = motors[0];
	this.rightMotor = motors[1];

	// set acceleration
	this.leftMotor.setAcceleration(ACCELERATION);
	this.rightMotor.setAcceleration(ACCELERATION);
	}
	
	//searching path
	public void run(){
		this.isSearching = true;
		travelTo(0,0);
		
		travelTo(60,60);
		
	}

	/*
	 * Functions to set the motor speeds jointly
	 */
	public void setSpeeds(float lSpd, float rSpd) {
		this.leftMotor.setSpeed(lSpd);
		this.rightMotor.setSpeed(rSpd);
		if (lSpd < 0)
			this.leftMotor.backward();
		else
			this.leftMotor.forward();
		if (rSpd < 0)
			this.rightMotor.backward();
		else
			this.rightMotor.forward();
	}

	public void setSpeeds(int lSpd, int rSpd) {
		this.leftMotor.setSpeed(lSpd);
		this.rightMotor.setSpeed(rSpd);
		if (lSpd < 0)
			this.leftMotor.backward();
		else
			this.leftMotor.forward();
		if (rSpd < 0)
			this.rightMotor.backward();
		else
			this.rightMotor.forward();
	}

	/*
	 * Float the two motors jointly
	 */
	public void setFloat() {
		this.leftMotor.stop();
		this.rightMotor.stop();
		this.leftMotor.flt(true);
		this.rightMotor.flt(true);
	}

	/*
	 * TravelTo function which takes as arguments the x and y position in cm Will travel to designated position, while
	 * constantly updating it's heading
	 */
	public void travelTo(double x, double y) {
		double minAng;
		while (Math.abs(x - odometer.getX()) > CM_ERR || Math.abs(y - odometer.getY()) > CM_ERR) {
			minAng = (Math.atan2(y - odometer.getY(), x - odometer.getX())) * (180.0 / Math.PI);
			if (minAng < 0)
				minAng += 360.0;
			this.turnTo(minAng, false);
			this.setSpeeds(FAST, FAST);
		}
		this.setSpeeds(0, 0);
	}

	/*
	 * TurnTo function which takes an angle and boolean as arguments The boolean controls whether or not to stop the
	 * motors when the turn is completed
	 */
	public void turnTo(double angle, boolean stop) {

		double error = angle - this.odometer.getAng();

		while (Math.abs(error) > DEG_ERR) {

			error = angle - this.odometer.getAng();

			if (error < -180.0) {
				this.setSpeeds(-SLOW, SLOW);
			} else if (error < 0.0) {
				this.setSpeeds(SLOW, -SLOW);
			} else if (error > 180.0) {
				this.setSpeeds(SLOW, -SLOW);
			} else {
				this.setSpeeds(-SLOW, SLOW);
			}
		}

		if (stop) {
			this.setSpeeds(0, 0);
		}
	}
	

	//if an object is detected process it the following way
	@Override
	public void processUSData(int distance) {
		
		this.USSensorReading = distance;
		this.objDetected = false;
		
		//if we find a block, something closer then 10cm
		if(this.USSensorReading < 10){
			this.objDetected = true;
			
			//this is the condition indicating wether or not we are currently searching
			if(this.isSearching == true){
				//move closer to the block to get  good color reading
				while((int)distance > 5){
					leftMotor.setSpeed(SLOW);
					rightMotor.setSpeed(SLOW);
					leftMotor.forward();
					rightMotor.forward();
				}
				leftMotor.setSpeed(FAST);
				rightMotor.setSpeed(FAST);
			}
			//identify the object
			id.testObject();
			this.isBlue = id.isBlock();
			
			//if we find a blue block
			if(this.isBlue && objDetected){
				//move to to right
				
				
			}
			//avoid if we see a wooden block
			else if (!this.isBlue && this.objDetected) {
				//Sound.beep();
				//Sound.beep();
				
				if(this.isSearching){
				//turn right
				leftMotor.rotate(convertAngle(Lab5.WHEEL_RADIUS, Lab5.TRACK, 90.0), true);
				rightMotor.rotate(-convertAngle(Lab5.WHEEL_RADIUS, Lab5.TRACK, 90.0), false);
				//move forward
				leftMotor.setSpeed(FAST);
				rightMotor.setSpeed(FAST);
				leftMotor.rotate(convertDistance(Lab5.WHEEL_RADIUS, 25.0), true);
				rightMotor.rotate(convertDistance(Lab5.WHEEL_RADIUS, 25.0), false);
			
				//turn left
				leftMotor.rotate(-convertAngle(Lab5.WHEEL_RADIUS, Lab5.TRACK, 90.0), true);
				rightMotor.rotate(convertAngle(Lab5.WHEEL_RADIUS, Lab5.TRACK, 90.0), false);
				//move forward
				leftMotor.rotate(convertDistance(Lab5.WHEEL_RADIUS, 47.0), true);
				rightMotor.rotate(convertDistance(Lab5.WHEEL_RADIUS, 47.0), false);
			
				//turn left
				leftMotor.rotate(-convertAngle(Lab5.WHEEL_RADIUS, Lab5.TRACK, 90.0), true);
				rightMotor.rotate(convertAngle(Lab5.WHEEL_RADIUS, Lab5.TRACK, 90.0), false);
				//move forward
				leftMotor.rotate(convertDistance(Lab5.WHEEL_RADIUS, 25.0), true);
				rightMotor.rotate(convertDistance(Lab5.WHEEL_RADIUS, 25.0), false);
				}
			}
		}
		else
		{
			this.objDetected=false;
		}	
	}
	public double usSensorReading(){
		return this.USSensorReading;
	}
	public boolean objDetected(){
		return this.objDetected;
	}
	@Override
	public int readUSDistance() {
		// TODO Auto-generated method stub
		return 0;
	}
	/*
	 * Go foward a set distance in cm
	 */
	public void goForward(double distance) {
		this.travelTo(Math.cos(Math.toRadians(this.odometer.getAng())) * distance, Math.cos(Math.toRadians(this.odometer.getAng())) * distance);

	}
	//convertDistance and converAngle methods for helping the robot travel.
	private static int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}
		
	private static int convertAngle(double radius, double width, double angle) {
		return convertDistance(radius, Math.PI * width * angle / 360.0);
	}


}
