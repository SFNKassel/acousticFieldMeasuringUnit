package de.sfn_kassel.sound_locate.audio;

import java.io.Closeable;
import java.io.IOException;

import javax.sound.sampled.LineUnavailableException;

public class Measure implements Closeable{
	final Out out;
	final Input in;
	
	public static void main(String[] args) throws LineUnavailableException, IOException {
		int SAMPLE_RATE = 44100;
		Measure m = new Measure(SAMPLE_RATE);
		System.out.println(m.measure(1000, 1000));
		m.close();
	}
	
	public Measure(int sampleRate) throws LineUnavailableException, IOException {
		this.out = new Out(sampleRate);
		this.in = new Input(sampleRate);
	}

	public double measure(int freq, int time) {
		in.startRecording();
		out.playSine(freq, time);
		out.waitToFinsh();
		return in.stopRecordingGiveRms();
	}
	
	@Override
	public void close() throws IOException {
		out.close();
	}
}