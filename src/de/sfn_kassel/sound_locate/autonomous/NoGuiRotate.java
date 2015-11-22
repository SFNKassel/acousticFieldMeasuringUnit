package de.sfn_kassel.sound_locate.autonomous;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import javax.sound.sampled.LineUnavailableException;

import de.sfn_kassel.sound_locate.audio.MapWriter;
import de.sfn_kassel.sound_locate.audio.Measurer;
import de.sfn_kassel.sound_locate.audio.StepperMotor;

public class NoGuiRotate {
	
	static int SLEEP_TIME = 100;
	static int MEASURE_TIME = 2000;
	static int MEASURE_FREQ = 1000;
	static int SAMPLE_RATE = 48000;
	static int STEP_SIZE = 5;
	static String FILENAME = "01_06.11.2015_mic.csv";
	
	public static void main(String[] args) throws LineUnavailableException, IOException, InterruptedException {
		StepperMotor motor = new StepperMotor(44100);
		Measurer m = new Measurer(SAMPLE_RATE);

		Map<Double, Double> map = new TreeMap<>();

		for (double i = 0; i < 360; i += STEP_SIZE) {
			motor.moveTo(-i, 50);
			Thread.sleep(SLEEP_TIME);
			double value = m.measure(MEASURE_FREQ, MEASURE_TIME);
			map.put(i, value);
			System.out.println(i + ", " + value);
//			Thread.sleep(SLEEP_TIME);
		}
		motor.moveTo(0, 50);

		MapWriter writer = new MapWriter(map);
		writer.writeToCsv(new File("first.csv"), ", ", "\n");
		
		m.close();
		motor.close();
	}
}
