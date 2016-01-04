package de.sfn_kassel.sound_locate.audio;

import java.io.Closeable;
import java.io.IOException;

import javax.sound.sampled.LineUnavailableException;

public class Measurer implements Closeable{
	final Out out;
	final Input in;
	
	public static void main(String[] args) throws LineUnavailableException, IOException, InterruptedException {
		int SAMPLE_RATE = 44100;
		Measurer m = new Measurer(SAMPLE_RATE);

		while(true) {
			System.out.println(m.measure(1000, 2000, SAMPLE_RATE));
		}

		//m.close();
	}
	
	public Measurer(int sampleRate) throws LineUnavailableException, IOException {
		this.out = new Out(sampleRate);
		this.in = new Input(sampleRate);
	}

	public double measure(int freq, int time, int samples) throws LineUnavailableException, InterruptedException {
		try {
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		double ret = 0;
		//out.playSine(freq, time);
		in.startRecording(time, samples);

		Thread.sleep(time);

		ret = in.stopRecordingGiveRms();
		return ret;
	}
	
	@Override
	public void close() throws IOException {
		out.close();
	}
}