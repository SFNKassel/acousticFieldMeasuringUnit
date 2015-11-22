package de.sfn_kassel.sound_locate.audio;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;

public class StepperMotor implements Closeable {

	final Clip clip;
	final int sampleRate;
	final AudioFormat af;
	boolean runnig = false;
	private double position;

	public static void main(String[] args) throws LineUnavailableException, IOException {
		StepperMotor motor = new StepperMotor(44100);
		motor.moveTo(360, 50);
		motor.moveTo(-360, 50);
		motor.moveTo(0, 50);
		motor.close();
	}
	
	public void moveTo(double pos, double speed) throws LineUnavailableException {
		if (pos == position) {
			return;
		}
		
		doSteps(pos - position, speed, position);
	}

	public StepperMotor(int sampleRate) throws LineUnavailableException {
		af = new AudioFormat(sampleRate, 16, 2, true, true);
		DataLine.Info info = new DataLine.Info(Clip.class, af);
		clip = (Clip) AudioSystem.getLine(info);
		this.sampleRate = sampleRate;
	}

	private void startplay(byte[] toneBuffer) throws LineUnavailableException {
		clip.open(af, toneBuffer, 0, toneBuffer.length);
		clip.addLineListener(new LineListener() {

			@Override
			public void update(LineEvent event) {
//				System.out.println(event);
				if (event.getType() == LineEvent.Type.STOP) {
					runnig = false;
				}
			}
		});
		clip.start();
		runnig = true;
	}

	public void doSteps(double steps, double speed, double start) throws LineUnavailableException {
		byte[] toneBuffer = createSinWaveBuffer(speed, Math.abs(steps), start,steps < 0 ? .5  * Math.PI : -.5  * Math.PI);
		startplay(toneBuffer);
		waitToFinsh();
		position += steps;
	}

	private void waitToFinsh() {
		while (runnig) {
			Thread.yield();
		}
		clip.close();
	}

	@Override
	public void close() throws IOException {
		clip.drain();
		clip.close();
	}

	private byte[] createSinWaveBuffer(double freq, double steps, double start, double relativeAngle) {
		int samples = (int) Math.ceil((steps / freq) * 2 * sampleRate);
		double[] output = new double[samples];
		for (int i = 0; i < output.length; i += 2) {
			double time = ((double)i / sampleRate) / 2;
			double angle = 2 * Math.PI * time * freq;
			output[i] = Math.sin(angle + start);
			output[i + 1] = Math.sin(angle + relativeAngle + start);
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