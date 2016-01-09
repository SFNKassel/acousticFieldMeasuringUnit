package de.sfn_kassel.sound_locate.application;

import java.awt.AWTException;
import java.io.IOException;

import javax.sound.sampled.LineUnavailableException;

import de.sfn_kassel.sound_locate.audio.Measurer;
import de.sfn_kassel.sound_locate.hardware.Gondel;
import de.sfn_kassel.sound_locate.hardware.GondelRobots;

public class GondelMeasurer {
	static int FREQ = 1000;
	static int TIME = 1000;
	
	static int AUSPENDELN_IN_MS = 1000 * 100;
	
	static int OFFSET_X = 10;
	static int OFFSET_Y = 10;
	static int Z = 10;
	
	static int RANGE_X = 10;
	static int RANGE_y = 10;
	
	
	
	public static void main(String[] args) throws IOException, LineUnavailableException, InterruptedException, AWTException {
		GondelRobots gondel = new GondelRobots();
		Measurer measurer = new Measurer(44100);
		
		for (int x = OFFSET_X; x < RANGE_X + OFFSET_X; x++) {
			for (int y = OFFSET_Y; y < RANGE_y + OFFSET_Y; y++) {
				gondel.setPosition(x, y, Z);
				Thread.sleep(AUSPENDELN_IN_MS);
				double value = measurer.measure(FREQ, TIME);
				System.out.println(x + ", " + y + ", " + value + ", ");
			}
		}
		
		measurer.close();
		gondel.close();
	}
}
