//Ultrasonic controller 
package ev3Navigator;


public interface UltrasonicController {
	
	public void processUSData(int distance);
	
	public int readUSDistance();
}
