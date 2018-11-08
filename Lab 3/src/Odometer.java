/*
 * Odometer.java
 */

//Anza Khan 260618490
//Ryan Chalmers 260581055
//Group 62
package ev3Navigator;

import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class Odometer extends Thread {
	// robot position
	private double x, y, theta;
	private int leftMotorTachoCount, rightMotorTachoCount;
	private double wrap;
	private int last_tacho_l, last_tacho_r;
	private EV3LargeRegulatedMotor leftMotor, rightMotor;
	private double delta_X, delta_Y, delta_T;
	// odometer update period, in ms
	private static final long ODOMETER_PERIOD = 25;

	// lock object for mutual exclusion
	private Object lock;

	// default constructor
	public Odometer(EV3LargeRegulatedMotor leftMotor,EV3LargeRegulatedMotor rightMotor) {
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		this.x = 0.0;
		this.y = 0.0;
		this.theta = 0.0;
		this.leftMotorTachoCount = 0;
		this.rightMotorTachoCount = 0;
		lock = new Object();
	}

	// run method (required for Thread)
	public void run() {
		long updateStart, updateEnd;
		
		//reset tachometer values
		leftMotor.resetTachoCount();
		rightMotor.resetTachoCount();
		
		last_tacho_l = leftMotor.getTachoCount();
		last_tacho_r = rightMotor.getTachoCount();
		

		while (true) {
			updateStart = System.currentTimeMillis();	
			
			//update the tachometer counts for each motor
			leftMotorTachoCount = leftMotor.getTachoCount();
			rightMotorTachoCount = rightMotor.getTachoCount();
			
			//use the change in tachometer count to calculate the distance travelled by both the left and right wheels
			double rightDistance = (2*Math.PI*Lab3.WHEEL_RADIUS*(rightMotorTachoCount-last_tacho_r))/360;
			double leftDistance = (2*Math.PI*Lab3.WHEEL_RADIUS*(leftMotorTachoCount-last_tacho_l))/360;
			
			//save the last tacho count for the distance travelled calculation
			last_tacho_l=leftMotorTachoCount;
			last_tacho_r=rightMotorTachoCount;
			
			//calculate the change in x,y,and theta
			delta_T = (leftDistance-rightDistance)/Lab3.TRACK;
			delta_X = ((leftDistance+rightDistance)/2)*Math.sin(theta);
			delta_Y = ((leftDistance+rightDistance)/2)*Math.cos(theta);
			
			synchronized (lock) {
				
				//update the change in x,y,theta
				theta += delta_T + wrap;
				x = x + delta_X;
				y = y + delta_Y;
			}

			//this allows the theta valuse to wrap around when reaching 360 or zero
			if(theta > 2*Math.PI)
				wrap = -2*Math.PI;
			else if(theta < 0)
				wrap = 2*Math.PI;
			else
				wrap=0;
			
			// this ensures that the odometer only runs once every period
			updateEnd = System.currentTimeMillis();
			if (updateEnd - updateStart < ODOMETER_PERIOD) {
				try {
					Thread.sleep(ODOMETER_PERIOD - (updateEnd - updateStart));
				} catch (InterruptedException e) {
					// there is nothing to be done here because it is not
					// expected that the odometer will be interrupted by
					// another thread
				}
			}
		}
	}

	// accessors
	public void getPosition(double[] position, boolean[] update) {
		// ensure that the values don't change while the odometer is running
		synchronized (lock) {
			if (update[0])
				position[0] = x;
			if (update[1])
				position[1] = y;
			if (update[2])
				position[2] = theta*(180/Math.PI);
		}
	}

	public double getX() {
		double result;

		synchronized (lock) {
			result = x;
		}

		return result;
	}

	public double getY() {
		double result;

		synchronized (lock) {
			result = y;
		}

		return result;
	}

	public double getTheta() {
		double result;

		synchronized (lock) {
			result = theta;
		}

		return result;
	}

	// mutators
	public void setPosition(double[] position, boolean[] update) {
		// ensure that the values don't change while the odometer is running
		synchronized (lock) {
			if (update[0])
				x = position[0];
			if (update[1])
				y = position[1];
			if (update[2])
				theta = position[2];
		}
	}

	public void setX(double x) {
		synchronized (lock) {
			this.x = x;
		}
	}

	public void setY(double y) {
		synchronized (lock) {
			this.y = y;
		}
	}

	public void setTheta(double theta) {
		synchronized (lock) {
			this.theta = theta;
		}
	}

	/**
	 * @return the leftMotorTachoCount
	 */
	public int getLeftMotorTachoCount() {
		return leftMotorTachoCount;
	}

	/**
	 * @param leftMotorTachoCount the leftMotorTachoCount to set
	 */
	public void setLeftMotorTachoCount(int leftMotorTachoCount) {
		synchronized (lock) {
			this.leftMotorTachoCount = leftMotorTachoCount;	
		}
	}

	/**
	 * @return the rightMotorTachoCount
	 */
	public int getRightMotorTachoCount() {
		return rightMotorTachoCount;
	}

	/**
	 * @param rightMotorTachoCount the rightMotorTachoCount to set
	 */
	public void setRightMotorTachoCount(int rightMotorTachoCount) {
		synchronized (lock) {
			this.rightMotorTachoCount = rightMotorTachoCount;	
		}
	}
}