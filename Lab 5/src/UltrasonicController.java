//Ultrasonic controller 
package ev3ObjectDetection;


public interface UltrasonicController {
	
	public void processUSData(int distance);
	
	public int readUSDistance();
}
