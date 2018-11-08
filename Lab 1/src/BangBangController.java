//Group #62 
//Ryan Chalmers 260581055
//Muhammad Anza Khan 260618490
package wallFollower;
import lejos.hardware.motor.*;

public class BangBangController implements UltrasonicController{
	private final int bandCenter, bandwidth;
	private final int motorLow, motorHigh;
	
	private int distance;
	private EV3LargeRegulatedMotor leftMotor, rightMotor, sensorMotor;
	//private float startPosition;

	private final int motorStraight = 235;
	private final int changeSpeed = 140; //determines motor speed change 
	private int nCount = 0; //no read 255 count for filter control

	
	public BangBangController(EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor,
								EV3LargeRegulatedMotor sensorMotor, int bandCenter, int bandwidth, int motorLow, int motorHigh) {
		//Default Constructor
		this.bandCenter = bandCenter;
		this.bandwidth = bandwidth;
		this.motorLow = motorLow;
		this.motorHigh = motorHigh;
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		this.sensorMotor = sensorMotor;
		
		
		
		leftMotor.setSpeed(motorStraight);				// Start robot moving forward
		rightMotor.setSpeed(motorStraight);
		
		leftMotor.forward();
		rightMotor.forward();
	
	}
	
	@Override
	public void processUSData(int distance)
	
		
	{
		//added to restart motors if stopped or in reverse
		//not currently used as motors never go below 50
		leftMotor.forward();
		rightMotor.forward();
		
		int error = distance - bandCenter;
	
		
		//update distance and error variables
        this.distance = distance;
        
        //255 distance filter implemented for bangbang controller
        if(distance == 255)
        {
        	if(nCount <6)
        		error = 0;
        	nCount++;
        } //resets 255 count if not consecutive
        else
        {
        	nCount = 0;
        }
        //to determine motor speed and orientation
        if(Math.abs(error)<=bandwidth) //within the bandwidth
        {
        	
        	rightMotor.setSpeed(motorStraight);

        }
        else if(error < 0) //too close, turn right
        {
        	leftMotor.setSpeed(motorStraight);
        	rightMotor.setSpeed(motorStraight-changeSpeed);

        }
        else //too far, turn left
        {
        	leftMotor.setSpeed(motorStraight);
        	rightMotor.setSpeed(motorStraight+changeSpeed);
        }
		
	}

	@Override
	public int readUSDistance() {
		return this.distance;
	}
}
