package de.sfn_kassel.sound_locate.autonomous;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import javax.sound.sampled.LineUnavailableException;

import de.sfn_kassel.sound_locate.audio.MapWriter;
import de.sfn_kassel.sound_locate.audio.Measure;
import de.sfn_kassel.sound_locate.hardware.MicTester;

public class NoGuiRotate {
	
	static int SLEEP_TIME = 1000;
	static int MEASURE_TIME = 1000;
	static int MEASURE_FREQ = 1000;
	static int SAMPLE_RATE = 44100;
	static int STEP_SIZE = 10;
	static String FILENAME = "first.csv";
	
	public static void main(String[] args) throws LineUnavailableException, IOException, InterruptedException {
		MicTester hw = new MicTester("COM4");
		Measure m = new Measure(SAMPLE_RATE);

		Map<Integer, Double> map = new TreeMap<>();

		for (int i = 0; i < 270; i += STEP_SIZE) {
			hw.setPosition(0, i);
			Thread.sleep(SLEEP_TIME);
			double value = m.measure(MEASURE_FREQ, MEASURE_TIME);
			map.put(i, value);
			System.out.println(i + ", " + value);
//			Thread.sleep(SLEEP_TIME);
		}
		hw.setPosition(0, 0);

		MapWriter writer = new MapWriter(map);
		writer.writeToCsv(new File("first.csv"), ", ", "\n");
		
		m.close();
		hw.close();
	}
}
