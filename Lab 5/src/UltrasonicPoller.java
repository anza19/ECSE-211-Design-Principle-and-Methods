//UltrasonicPoller to get distance readings.

package ev3ObjectDetection;

import lejos.robotics.SampleProvider;

//
//  Control of the wall follower is applied periodically by the 
//  UltrasonicPoller thread.  The while loop at the bottom executes
//  in a loop.  Assuming that the us.fetchSample, and cont.processUSData
//  methods operate in about 20mS, and that the thread sleeps for
//  50 mS at the end of each loop, then one cycle through the loop
//  is approximately 70 mS.  This corresponds to a sampling rate
//  of 1/70mS or about 14 Hz.
//


public class UltrasonicPoller extends Thread{
	private SampleProvider usDistance;
	private UltrasonicController cont;
	private float[] usData;
	
	public UltrasonicPoller(SampleProvider usDistance, float[] usData, UltrasonicController cont) {
		this.usDistance = usDistance;
		this.cont = cont;
		this.usData = usData;
	}

//  Sensors now return floats using a uniform protocol.
//  Need to convert US result to an integer [0,255]
	
	public void run() {
		int distance;
		while (true) {
			usDistance.fetchSample(usData,0);							// acquire data
			distance=(int)(usData[0]*100.0);					// extract from buffer, cast to int
			
			cont.processUSData(distance);						// now take action depending on value
			
			//Changed Thread.sleep() value to 25 so that the sensor would check for readings more frequently.
			try { Thread.sleep(25); } catch(Exception e){}		// Poor man's timed sampling
		}
	}

}
