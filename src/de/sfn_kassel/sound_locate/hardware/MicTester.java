package de.sfn_kassel.sound_locate.hardware;

import java.io.*;
import java.util.*;

import gnu.io.*;

public class MicTester implements Closeable {
	@SuppressWarnings("rawtypes")
	static Enumeration portList;
	static CommPortIdentifier portId;
	static SerialPort serialPort;
	static OutputStream outputStream;
	static BufferedReader inputStream;

	public MicTester(String comPortName) throws IOException {
		portList = CommPortIdentifier.getPortIdentifiers();

		while (portList.hasMoreElements()) {

			portId = (CommPortIdentifier) portList.nextElement();
			if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {

				if (portId.getName().equals(comPortName)) {

					try {
						serialPort = (SerialPort) portId.open("MicTester", 2000);
					} catch (PortInUseException e) {
						e.printStackTrace();
					}
					try {
						outputStream = serialPort.getOutputStream();
						inputStream = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
					} catch (IOException e) {
						e.printStackTrace();
					}
					try {
						serialPort.setSerialPortParams(115200, SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
								SerialPort.PARITY_NONE);
					} catch (UnsupportedCommOperationException e) {
						e.printStackTrace();
					}
				}
			}
		}
		sendToDisplay("");
	}

	public double getEnvironmentParameter(int number) throws IOException {
		if (number > 10) {
			throw new IllegalArgumentException("Number cannot be bigger than 9!");
		}
		String msg = sendToDisplay("env" + number);
		return Double.parseDouble(msg.split("env" + number + " ")[1].split("\n")[0]);
	}
	
	public void setPosition(int number, int position) throws IOException {
		if (number > 10) {
			throw new IllegalArgumentException("Number cannot be bigger than 9!");
		}
		String msg = sendToDisplay("pos" + number + " " + position);
		if(!msg.equals("pos" + number + " " + position)) {
			throw new IOException("Setting of the new position failed!");
		}
	}
	
	private synchronized String sendToDisplay(String command) throws IOException {
		outputStream.write((command + "\n").getBytes());
		String line;
		while (true) {
			try {
				line = inputStream.readLine();
				break;
			} catch (IOException e) {
			}
		}
		return line;
	}

	@Override
	public void close() throws IOException {
		outputStream.close();
		inputStream.close();
		serialPort.close();
	}
}