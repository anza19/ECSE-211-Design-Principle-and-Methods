package ev3ObjectDetection;

import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.utility.Timer;
import lejos.utility.TimerListener;

public class LCDInfo implements TimerListener{
	public static final int LCD_REFRESH = 100;
	private Odometer odo;
	private Identify id;
	private Timer lcdTimer;
	private USnav usNav;
	private TextLCD LCD = LocalEV3.get().getTextLCD();
	private String objDetected;
	private String isBlock;
	
	// arrays for displaying data
	private double [] pos;
	
	public LCDInfo(Odometer odo, Identify id, USnav usNav) {
		this.odo = odo;
		this.lcdTimer = new Timer(LCD_REFRESH, this);
		this.id = id;
		this.usNav = usNav;
		// initialise the arrays for displaying data
		pos = new double [3];
		
		// start the timer
		lcdTimer.start();
	}
	
	public void timedOut() { 
		//write either nothing or object detected depending on what is being seen US
		
		if(this.usNav.objDetected()){
			this.objDetected = "Object Detected";
		} else
			this.objDetected = "";
		
		
		if(!this.usNav.objDetected()|| usNav.usSensorReading() != 4)
			this.isBlock = "";
		else if(this.id.isBlock())
			this.isBlock = "Block";
		else
			this.isBlock = "Not Block";
		
		odo.getPosition(pos);
		LCD.clear();
		LCD.drawString("X: ", 0, 0);
		LCD.drawString("Y: ", 0, 1);
		LCD.drawString("H: ", 0, 2);
		LCD.drawString("SV: ", 0, 3);
		LCD.drawString("US: ", 0, 4);
		LCD.drawString(this.objDetected, 0, 5);
		LCD.drawString(this.isBlock, 0, 6);
		LCD.drawInt((int)(pos[0]), 3, 0);
		LCD.drawInt((int)(pos[1]), 3, 1);
		LCD.drawInt((int)pos[2], 3, 2);
		LCD.drawInt((int)id.sensorVal(), 4, 3);
		LCD.drawInt((int)usNav.usSensorReading(), 4, 4);
	}
}
