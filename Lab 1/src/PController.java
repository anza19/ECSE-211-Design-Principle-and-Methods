//Group #62 
//Ryan Chalmers 260581055
//Muhammad Anza Khan 260618490

package wallFollower;
import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class PController implements UltrasonicController {
	
	private final int bandCenter, bandwidth;
	private final int motorStraight = 200, FILTER_OUT = 20;
	private EV3LargeRegulatedMotor leftMotor, rightMotor;
	private int filterControl, distance;
	private double error;
	
	public PController(EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor,
					   int bandCenter, int bandwidth) {
		//Default Constructor
		filterControl = 0;
		this.bandCenter = 36;
		this.rightMotor = rightMotor;
		this.leftMotor = leftMotor;
		this.bandwidth = bandwidth;
		leftMotor.setSpeed(motorStraight);					// Initialize motor rolling forward
		rightMotor.setSpeed(motorStraight);
		leftMotor.forward();
		rightMotor.forward();
		
	}
	
	@Override
	public void processUSData(int distance) {
		
		
		
		// rudimentary filter - toss out invalid samples corresponding to null signal.
		// (n.b. this was not included in the Bang-bang controller, but easily could have).
		if (distance == 255 && filterControl < FILTER_OUT) {
			// bad value, do not set the distance var, however do increment the filter value
			filterControl ++;
		} else if (distance == 255){
			// true 255, therefore set distance to 255
			this.distance = distance;
		} else {
			// distance went below 255, therefore reset everything.
			filterControl = 0;
			this.distance = distance;
		}
		
		
		//filter out high distanced
		if(distance >=225) 
		{
			distance =200;
		}
	
		error= bandCenter-distance;	
		
		//if robot is in band range, move forward  
		if(Math.abs(error)<= bandwidth)
		{
			leftMotor.setSpeed(motorStraight);					
			rightMotor.setSpeed(motorStraight);
			leftMotor.forward();
			rightMotor.forward();
		}
		
		
		//if robot is too close, turn right
		else if(error>0)
		{
			if(error>10)
			{
				leftMotor.setSpeed((int)((motorStraight+error*10)));		
				rightMotor.setSpeed((int)((motorStraight-error*8)));		
				leftMotor.forward();										
				rightMotor.backward();
			}
			else
			{
				leftMotor.setSpeed((int)((motorStraight+error*18)));		
				rightMotor.setSpeed((int)((motorStraight-error*3)));		
				leftMotor.forward();										
				rightMotor.forward();
			}
		}
		
		//if robot is too far, left turn
		else if(error<0 && distance<=120)  
		{				
			rightMotor.setSpeed((int)(motorStraight-(error-75)));
			leftMotor.setSpeed((int)((motorStraight+(error/1.25))));	
			rightMotor.forward();
			leftMotor.forward();										
		}
		
		//sharp left turn
		else if(distance >120)
		{
			rightMotor.setSpeed((int)(motorStraight-(error-75)));
			leftMotor.setSpeed((int)((motorStraight+(error+180))));		
			rightMotor.forward();
			leftMotor.forward();										
		}	
	}

	
	@Override
	public int readUSDistance() {
		return this.distance;
	}

}