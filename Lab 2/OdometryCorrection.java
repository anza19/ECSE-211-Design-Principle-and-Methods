/* 
 * OdometryCorrection.java
 */
//Anza Khan 260618490
//Ryan Chalmers 260581055
//Group 62
package ev3Odometer;

import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.Sound;

public class OdometryCorrection extends Thread 
{
	private static final long CORRECTION_PERIOD = 8;
	private Odometer odometer;
	
	//Class Variables
	private double theta;
	private double thetaLast;
	private boolean first_Y;
	private boolean first_X;
	private double odo_Y;
	private double odo_X;
	private int last_CountX;
	private int count_X;
	private int last_CountY;
	private int count_Y;
	
	
	//Class Constants
	private final double LINE_SEPARATION = 30;
	
	//Initiating a port for the light sensor
	private static final Port lsPort = LocalEV3.get().getPort("S1");
	
	@SuppressWarnings("resource")						// Because we don't bother to close this resource
	SensorModes lsSensor = new EV3ColorSensor(lsPort);	// lsSensor is the instance
	SampleProvider lsColor = lsSensor.getMode("Red");	// lsColor provides samples from this instance
	float[] lsData = new float[lsColor.sampleSize()];	// Array to store returned samples

	// constructor
	public OdometryCorrection(Odometer odometer) 
	{
		this.odometer = odometer;
	}

	// run method (required for Thread)
	public void run() 
	{
		long correctionStart, correctionEnd;

		while (true) 
		{
			correctionStart = System.currentTimeMillis();

			//Getting the latest sample from the light sensor
			lsColor.fetchSample(lsData, 0);
			
			//Getting the latest theta reading from the odometer
			theta = (odometer.getTheta())*(180/Math.PI);
			
			//Scaling the returned sample to a value between 0-100
			int value = (int)(lsData[0]*100);
			
			//A threshold value of 40 was determined through trial and error
			//to differentiate between the dark lines and lighter surface
			if(value<30)
			{
				//Check implemented to ensure the sensor works
				Sound.twoBeeps();
				
				
				//Condition to check when the robot is travelling in the y direction
				if((theta>=0 && theta<=2) ||(theta>=179 && theta<=181))
				{
					//Check when the first gridline is crossed
					count_Y++;
					if(first_Y==false)
					{
						first_Y=true;
						first_X=false;
						last_CountX=count_X;
						count_X=0;
						//Save the y reading of the odometer when the first gridline is crossed
						if((theta>=0 && theta<=2))
						{
							odometer.setY(15);
						}
						else
						{
							odometer.setY((last_CountY*LINE_SEPARATION)-15);
						}
						odo_Y=odometer.getY();
					}
					//Check when successive gridlines are crossed
					else if(first_Y==true)
					{
						//if travelling in the positive direction add 30cm*(the number of lines crossed) to the
						//odometer reading and set as the new odometer reading
						if((theta>=0 && theta<=2))
						{
								odometer.setY(odo_Y+(LINE_SEPARATION*(count_Y-1)));
								odo_Y=odometer.getY();
						}
						//if travelling in the negative direction subtract 30cm*(the number of lines crossed) from
						//the odometer reading and set as the new odometer reading
						else if((theta>=179 && theta<=181))
						{
								odometer.setY(odo_Y-(LINE_SEPARATION*(count_Y-1)));
								odo_Y=odometer.getY();
						}
					}
				}
				//Condition to check when the robot is travelling in the x direction
				else if((theta>=89 && theta<=91) ||(theta>=269 && theta<=271))
				{
					////Check when the first gridline is crossed
					count_X++;
					if(first_X==false)
					{
						first_X=true;
						first_Y=false;
						last_CountY=count_Y;
						count_Y=0;
						//Save the x reading of the odometer when the first gridline is crossed
						//odoX=odometer.getX();
						if((theta>=89 && theta<=91))
						{
							odometer.setX(15);
						}
						else
						{
							odometer.setX((last_CountX*LINE_SEPARATION)-15);
						}
						odo_X=odometer.getX();
					}
					//Check when successive gridlines are crossed
					else if(first_X==true)
					{
						//if travelling in the positive direction add 30cm to the odometer reading
						//and set as the new odometer reading
						if((theta>=89 && theta<=91))
						{
								odometer.setX(odo_X+(LINE_SEPARATION*(count_X-1)));
								odo_X=odometer.getX();
						}
						//if travelling in the negative direction subtract 30cm from the odometer reading
						//and set as the new odometer reading
						else if((theta>=269 && theta<=271))
						{
								odometer.setX(odo_X-(LINE_SEPARATION*(count_X-1)));
								odo_X=odometer.getX();
						}
					}
				}
			}
			

			// this ensure the odometry correction occurs only once every period
			correctionEnd = System.currentTimeMillis();
			if (correctionEnd - correctionStart < CORRECTION_PERIOD) {
				try {
					Thread.sleep(CORRECTION_PERIOD
							- (correctionEnd - correctionStart));
				} catch (InterruptedException e) {
					// there is nothing to be done here because it is not
					// expected that the odometry correction will be
					// interrupted by another thread
				}
			}
		}
	}
	
	public double getColorValue()
	{
		return lsData[0]*100;
	}
}