package de.sfn_kassel.sound_locate.hardware;

import java.io.IOException;

import gnu.io.PortInUseException;
import gnu.io.UnsupportedCommOperationException;

public class Test {
	public static void main(String[] args) throws PortInUseException, IOException, IllegalAccessException, UnsupportedCommOperationException, InterruptedException {
		MicTester micTester = new MicTester("COM4");
		System.out.println("opened");
		System.out.println(micTester.getEnvironmentParameter(0));
		
		for (int i = 0; i < 270; i += 10) {
			micTester.setPosition(0, i);
			Thread.sleep(1000);
		}
		
		micTester.close();
		System.out.println("closed");
	}
}
