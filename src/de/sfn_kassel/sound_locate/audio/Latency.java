package de.sfn_kassel.sound_locate.audio;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

@Deprecated
public class Latency {
	private final AudioFormat af;
	private final int sampleRate;
	
	private final SourceDataLine out;
	private final TargetDataLine in;

	public Latency(int sampleRate) throws LineUnavailableException {
		this.sampleRate = sampleRate;
		af = new AudioFormat(sampleRate, 16, 1, true, true);
		
		out = AudioSystem.getSourceDataLine(af);
		in = AudioSystem.getTargetDataLine(af);
		
		
	}
	
	public static void main(String[] args) throws LineUnavailableException {
		new Latency(44100);
	}
}
