package ev3ObjectDetection;

import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;

public class Identify {
	
	private double error, woodVal = 10, color;

	private boolean isBlock = false;
	
	private float[] colorData;
	SampleProvider colorValue;
	
	public Identify(float[] colorData,SampleProvider colorValue){
		this.colorData=colorData;
		this.colorValue=colorValue;
	}
	
	public void testObject(){
		//fetch a value from color sensor
		this.colorValue.fetchSample(colorData, 0);
		this.color=this.colorData[0]*100;
								
		//if color sensor value is within a predetermined range, 
		//then the robot is looking at styrofoam 
		if(this.color < woodVal && this.color > 6)
			this.isBlock = true;
		//otherwise we are looking at a wood block
		else if (this.color >10 && this.color < 25){
			this.isBlock = false;
		}
	}
	
	public boolean isBlock(){
		return this.isBlock;
	}
	
	public double sensorVal(){
		return this.color;
	}
}
	


