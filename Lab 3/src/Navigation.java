package ev3Navigator;

import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.Port;
import ev3Navigator.Odometer;

public class Navigation extends Thread {
	private Odometer odometer;
	private TextLCD t;
	private double currentX, currentY, currentTheta, final_X, final_Y, final_theta;
	private Object lock;
	private double dx, dy, dtheta;
	private EV3LargeRegulatedMotor leftMotor, rightMotor;
	private static final int FORWARD_SPEED = 175;
	private static final int ROTATE_SPEED = 125;
	

	public Navigation(Odometer odometer, TextLCD t, EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor, 
			double final_X, double final_Y) {
		this.odometer = odometer;
		this.t = t;
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		this.final_X = final_X;
		this.final_Y = final_Y;
	}
	
	//start running robot
	public void run(){

		//travel to each position as defined below
		travelTo(60,30);
		travelTo(30,30);
		travelTo(30,60);
		travelTo(60,0);
		
	}
	
void travelTo(double x, double y){
		
		//Get x and y from odometer
		currentX = odometer.getX();
		currentY = odometer.getY();
		
		//calculate the distance needed to travel in both x and y by taking the difference in
		//x, y (where the robot is trying to go) and currentX, currentY (where the robot is currently)
		dx= x-currentX;
		dy= y-currentY;
		
		//calculate theta needed to point towards the destination
		final_theta = Math.atan(dx/dy);
		
		//make sure we turn the right way
		if (dx < 0){
			final_theta = final_theta - Math.PI;
		}
		
		else if(dy < 0 && dx > 0){
			final_theta += Math.PI;
		}
		
		//adjusts to face the correct spot
		turnTo(final_theta);
		
		//Set motor speeds
		leftMotor.setSpeed(FORWARD_SPEED);
		rightMotor.setSpeed(FORWARD_SPEED);
		
		//move to destination
	    leftMotor.rotate(convertDistance(Lab3.WHEEL_RADIUS, Math.sqrt(dx*dx + dy*dy)), true);
		rightMotor.rotate(convertDistance(Lab3.WHEEL_RADIUS, Math.sqrt(dx*dx + dy*dy)), false);
		
	
}

void turnTo(double final_theta){
		 //get theta from odometer 
		 currentTheta = odometer.getTheta();	 
		 
		 //required change in theta calculated using final_theta and currentTheta
		 dtheta = final_theta-currentTheta;
		 
		 //conditions to ensure that dtheta faces the right way
		 if(dtheta > Math.PI ){
			 dtheta = dtheta - 2*Math.PI;	 
		 }
		 else if(dtheta < -Math.PI){
			 dtheta = dtheta + 2*Math.PI;
			 
		 }
		 
		 //set motor speeds
		 leftMotor.setSpeed(ROTATE_SPEED);
		 rightMotor.setSpeed(ROTATE_SPEED);
		 
		 //turn robot by delta theta
		 leftMotor.rotate(convertAngle(Lab3.WHEEL_RADIUS, Lab3.TRACK, dtheta*360/(2*Math.PI)), true);
		 rightMotor.rotate(-convertAngle(Lab3.WHEEL_RADIUS, Lab3.TRACK, dtheta*360/(2*Math.PI)), false);
		 
	}
	boolean isNavigating(){
		//if at destination return false, otherwise true
		currentX = odometer.getX();
		currentY = odometer.getY();
	
		if(currentX == final_X && currentY == final_Y){
			return false;
		}
		else {
			return true;
		}
	}
	
	private static int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}

	private static int convertAngle(double radius, double width, double angle) {
		return convertDistance(radius, Math.PI * width * angle / 360.0);
	}
	
}