package de.sfn_kassel.sound_locate.hardware;

import java.awt.AWTException;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class GondelRobots implements Closeable {
	Keyboard robot;

	static int HOME_X = 1210;
	static int HOME_Y = 1210;
	static int HOME_Z = 1230;

	int lastX = HOME_X;
	int lastY = HOME_Y;
	int lastZ = HOME_Z;
	
	final BufferedWriter logger;

	public static void main(String[] args) throws InterruptedException, AWTException, IOException {
		GondelRobots gondelRobots = new GondelRobots();

		// gondelRobots.setPosition(1210, 1075, 850); //TODO:NEVER Drive here

		for (int radius = 300; radius < 700; radius += 36) {
			for (double i = 0; i < 2 * Math.PI; i += (Math.PI * 2) / 20) {
				double x = Math.sin(i) * radius;
				double y = Math.cos(i) * radius;

				gondelRobots.setPosition((int) (x + HOME_X), (int) (y + HOME_Y), 965);
				
				Thread.sleep(1000 * 100);
			}
		}

		gondelRobots.close();
	}

	public GondelRobots() throws AWTException, InterruptedException, IOException {
		System.out.println("click in hterm!!!");
		Thread.sleep(10000);
		robot = new Keyboard();
		robot.type("102");
		robot.type("104");
		robot.type("5 " + HOME_X + " " + HOME_Y + " " + HOME_Z);
		
		logger = new BufferedWriter(new FileWriter(new File("log4.csv")));
	}

	public void setPosition(int x, int y, int z) throws InterruptedException, IOException {
		robot.type("6 " + x + " " + y + " " + lastZ + " 100");
		Thread.sleep(5000);
		if (lastZ != z) {
			robot.type("6 " + x + " " + y + " " + z + " 100");
			Thread.sleep(5000);
		}

		logger.write(System.currentTimeMillis() + "\t" + x + "\t" + y + "\t" + z + "\t" + "\n");
		
		this.lastX = x;
		this.lastY = y;
		this.lastZ = z;
	}

	@Override
	public void close() throws IOException {
		try {
			setPosition(lastX, lastY, HOME_Z);
			setPosition(HOME_X, HOME_Y, HOME_Z);
			logger.close();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
