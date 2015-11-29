package de.sfn_kassel.sound_locate.audio;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.CountDownLatch;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;

public class Out implements Closeable {
	final Clip clip;
	final int sampleRate;
	final AudioFormat af;
	boolean runnig = false;

	public Out(int sampleRate) throws LineUnavailableException {
		af = new AudioFormat(sampleRate, 16, 1, true, true);
		DataLine.Info info = new DataLine.Info(Clip.class, af);
		clip = (Clip) AudioSystem.getLine(info);
		this.sampleRate = sampleRate;
	}

	public void playSine(double freq, int duration) throws LineUnavailableException {
		byte[] toneBuffer = createSinWaveBuffer(freq, duration);
		clip.open(af, toneBuffer, 0, toneBuffer.length);

		clip.addLineListener(new LineListener() {
			@Override
			public void update(LineEvent event) {
				if(event.getType() == LineEvent.Type.STOP) {
					runnig = false;
				}
			}
		});

		clip.start();
		runnig = true;
	}

	public void waitToFinsh() {
		while(runnig) {
			//System.out.println(clip.getMicrosecondPosition());

			Thread.yield();
		}
		clip.close();
	}

	@Override
	public void close() throws IOException {
		clip.drain();
		clip.close();
	}

	public byte[] createSinWaveBuffer(double freq, int ms) {
		int samples = (int) ((ms * sampleRate) / 1000);
		double[] output = new double[samples + 500];
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
