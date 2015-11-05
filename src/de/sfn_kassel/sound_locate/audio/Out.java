package de.sfn_kassel.sound_locate.audio;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class Out implements Closeable {
	final SourceDataLine line;
	final int sampleRate;

	public Out(int sampleRate) throws LineUnavailableException {
		final AudioFormat af = new AudioFormat(sampleRate, 16, 1, true, true);
		line = AudioSystem.getSourceDataLine(af);
		line.open(af, sampleRate);
		line.start();
		this.sampleRate = sampleRate;
	}

	public void playSine(double freq, int duration) {
		byte[] toneBuffer = createSinWaveBuffer(freq, duration);
		line.write(toneBuffer, 0, toneBuffer.length);
	}
	
	public void waitToFinsh() {
		line.drain();
	}

	@Override
	public void close() throws IOException {
		line.drain();
		line.close();
	}

	public byte[] createSinWaveBuffer(double freq, int ms) {
		int samples = (int) ((ms * sampleRate) / 1000);
		double[] output = new double[samples];
		double period = (double) sampleRate / freq;
		for (int i = 0; i < output.length; i++) {
			double angle = 2.0 * Math.PI * i / period;
			output[i] = Math.sin(angle);
		}
		return shortsToBytes(doublesToShorts(output));
	}

	private byte[] shortsToBytes(short[] shorts) {
		ByteBuffer buffer = ByteBuffer.allocate(shorts.length * 2);
		for (int i = 0; i < shorts.length; i++) {
			buffer.putShort(shorts[i]);
		}
		return buffer.array();
	}

	private short[] doublesToShorts(double[] doubles) {
		short[] shorts = new short[doubles.length];
		for (int i = 0; i < shorts.length; i++) {
			shorts[i] = (short) (doubles[i] * Short.MAX_VALUE);
		}
		return shorts;
	}
}
