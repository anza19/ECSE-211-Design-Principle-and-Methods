package ev3Navigator;

import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.Sound;

public class USnav extends Thread implements UltrasonicController {
	private Odometer odometer;
	private TextLCD t;
	private double currentx, currenty, currenttheta, final_X, final_Y, final_theta;
	private double dx, dy, dtheta;
	private double USSensorReading;
	private EV3LargeRegulatedMotor leftMotor, rightMotor;
	private static final int FORWARD_SPEED = 180;
	

	// constructor
	public USnav(Odometer odometer, TextLCD t, EV3LargeRegulatedMotor leftMotor,
			EV3LargeRegulatedMotor rightMotor, double final_X, double final_Y) {
		//Set constructor inputs to be values of variables declared above.
		this.odometer = odometer;
		this.t = t;
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		this.final_X = final_X;
		this.final_Y = final_Y;

	}
	
	//travel to the designated positions
	public void run() {
		travelTo(0, 60);
		travelTo(60, 0);

	}

	void travelTo(double final_X, double final_Y) {
		
		//Get x an y from odometer.
		currentx = odometer.getX();
		currenty = odometer.getY();
		//calculate the distance needed to travel in both x and y by taking the difference in
				//x, y (where the robot is trying to go) and currentX, currentY (where the robot is currently)
		dx = final_X - currentx;
		dy = final_Y - currenty;
		
		//set final_X and final_Y to be the passed variables
		this.final_X = final_X;
		this.final_Y = final_Y;

		//calculate theta needed to point towards the destination
		final_theta = Math.atan(dx / dy);

		//make sure we turn thr rihght way
		if (dx < 0) {
			final_theta = final_theta - Math.PI;
		} else if (dy < 0 && dx > 0) {
			final_theta += Math.PI;
		}

		//turn towards destination
		turnTo(final_theta);

		//set motor speed
		leftMotor.setSpeed(FORWARD_SPEED);
		rightMotor.setSpeed(FORWARD_SPEED);
		
		//go to destination
		leftMotor.rotate(convertDistance(Lab3.WHEEL_RADIUS, Math.sqrt(dx * dx + dy * dy)), true);
		rightMotor.rotate(convertDistance(Lab3.WHEEL_RADIUS, Math.sqrt(dx * dx + dy * dy)), false);

	}

	boolean isNavigating() {
		//if at destination return false, otherwise true
		currentx = odometer.getX();
		currenty = odometer.getY();

		if (currentx > final_X - 1 && currenty > final_Y - 1 && currentx < final_X + 1 && currenty < final_Y + 1) {
			return false;
		} else {
			return true;
		}
	}

	void turnTo(double finaltheta) {

		//get theta from odometer.
		currenttheta = odometer.getTheta();

		//change in theta required based on currenttheta and finaltheta.
		dtheta = finaltheta - currenttheta;

		//make sure we face the right way
		if (dtheta > Math.PI) {
			dtheta = dtheta - 2 * Math.PI;

		} else if (dtheta < -Math.PI) {
			dtheta = dtheta + 2 * Math.PI;

		}
		//turn
		leftMotor.rotate(convertAngle(Lab3.WHEEL_RADIUS, Lab3.TRACK, dtheta * 360 / (2 * Math.PI)), true);
		rightMotor.rotate(-convertAngle(Lab3.WHEEL_RADIUS, Lab3.TRACK, dtheta * 360 / (2 * Math.PI)), false);

	}

	private static int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}

	private static int convertAngle(double radius, double width, double angle) {
		return convertDistance(radius, Math.PI * width * angle / 360.0);
	}

	@Override
	public void processUSData(int distance) {
		USSensorReading = distance;
		
		//if robot is near wall, go through the following movements to get past it
		if (USSensorReading < 10) {

			//turn right
			leftMotor.rotate(convertAngle(Lab3.WHEEL_RADIUS, Lab3.TRACK, 90.0), true);
			rightMotor.rotate(-convertAngle(Lab3.WHEEL_RADIUS, Lab3.TRACK, 90.0), false);
			//move forward
			leftMotor.setSpeed(FORWARD_SPEED);
			rightMotor.setSpeed(FORWARD_SPEED);
			leftMotor.rotate(convertDistance(Lab3.WHEEL_RADIUS, 25.0), true);
			rightMotor.rotate(convertDistance(Lab3.WHEEL_RADIUS, 25.0), false);

			//turn left
			leftMotor.rotate(-convertAngle(Lab3.WHEEL_RADIUS, Lab3.TRACK, 90.0), true);
			rightMotor.rotate(convertAngle(Lab3.WHEEL_RADIUS, Lab3.TRACK, 90.0), false);
			//move forward
			leftMotor.rotate(convertDistance(Lab3.WHEEL_RADIUS, 47.0), true);
			rightMotor.rotate(convertDistance(Lab3.WHEEL_RADIUS, 47.0), false);
			
			//turn left
			leftMotor.rotate(-convertAngle(Lab3.WHEEL_RADIUS, Lab3.TRACK, 90.0), true);
			rightMotor.rotate(convertAngle(Lab3.WHEEL_RADIUS, Lab3.TRACK, 90.0), false);
			//move forward
			leftMotor.rotate(convertDistance(Lab3.WHEEL_RADIUS, 25.0), true);
			rightMotor.rotate(convertDistance(Lab3.WHEEL_RADIUS, 25.0), false);
			
			//finish travelling the destination
			travelTo(final_X, final_Y);

		}

	}

	@Override
	public int readUSDistance() {
		// TODO Auto-generated method stub
		return 0;
	}

}
