package de.sfn_kassel.sound_locate.audio;

import java.io.Closeable;
import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

public class Input implements Closeable{
	private final TargetDataLine line;
	private boolean running = false;
	private ArrayList<Double> doubles;
	private Thread thread;

	public Input(int sampleRate) throws LineUnavailableException {
		final AudioFormat af = new AudioFormat(sampleRate, 16, 1, true, true);
		DataLine.Info info = new DataLine.Info(TargetDataLine.class, af);
		line = (TargetDataLine) AudioSystem.getLine(info);
		line.open(af);
	}

	public void startRecording(int ms, int samples, Out out) {
		running = true;
		thread = new Thread(new Runnable() {
			public void run() {
				line.start();

				int parts = (line.getBufferSize() / 50) % 2 == 0 ? 50 : 100;

				byte[] data = new byte[line.getBufferSize() / 50];
				doubles = new ArrayList<>();

				while (out.runnig) {
					line.read(data, 0, data.length);
					ByteBuffer buffer = ByteBuffer.allocate(data.length);
					buffer.put(data);
					buffer.rewind();

					while (true) {
						try {
							double d = buffer.getShort() / (double) Short.MAX_VALUE;
							doubles.add(d);
						} catch (BufferUnderflowException ignore) {
							break;
						}
					}
				}
				for (int i = 0; i < line.getBufferSize() / 50; i++) {
					doubles.remove(doubles.size() - 1);
				}
			}
		});
		thread.start();
	}

	public ArrayList<Double> stopRecording() {
		while (thread.isAlive());
		line.stop();
		return doubles;
	}

	public double stopRecordingGiveRms() {
		ArrayList<Double> doubles = stopRecording();
		double amplitude = 0;
		for (Double d : doubles) {
			amplitude += Math.abs(d);
		}
		amplitude /= doubles.size();
		return amplitude;
	}

	@Override
	public void close() throws IOException {
		line.close();
	}
}
