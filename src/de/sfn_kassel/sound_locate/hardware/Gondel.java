package de.sfn_kassel.sound_locate.hardware;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.*;

import gnu.io.*;

public class Gondel implements Closeable {
	@SuppressWarnings("rawtypes")
	static Enumeration portList;
	static CommPortIdentifier portId;
	static SerialPort serialPort;
	static OutputStream outputStream;
	static BufferedReader inputStream;
	
	public static void main(String[] args) throws IOException, InterruptedException, TooManyListenersException {
		Gondel gondel = new Gondel("COM14");
		gondel.setPosition((short)1400, (short)1100, (short)1500);
		Thread.sleep(10000);
		gondel.close();
	}

	public Gondel(String comPortName) throws IOException, InterruptedException, TooManyListenersException {
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
						serialPort.addEventListener(new SerialPortEventListener() {
							@Override
							public void serialEvent(SerialPortEvent arg0) {
								System.out.println(arg0);
							}
						});
					} catch (IOException e) {
						e.printStackTrace();
					}
					try {
						serialPort.setSerialPortParams(115200, SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
								SerialPort.PARITY_NONE);
					} catch (UnsupportedCommOperationException e) {
						e.printStackTrace();
					}
					ByteBuffer buffer = ByteBuffer.allocate(Short.BYTES * 6).order(ByteOrder.LITTLE_ENDIAN);
					buffer.putShort((short) 102);
					buffer.putShort((short) 104);
					buffer.putShort((short) 5);
					buffer.putShort((short) 1400);
					buffer.putShort((short) 1300);
					buffer.putShort((short) 1500);
					getValueByKey(buffer.array(), "\r\n" + "\r\n");
					Thread.sleep(100);
					readToFinish();
				}
			}
		}
	}

	public void setPosition(short x, short y, short z) throws IOException {
		ByteBuffer buffer = ByteBuffer.allocate(Short.BYTES * 5).order(ByteOrder.LITTLE_ENDIAN);
		buffer.putShort((short) 6);
		buffer.putShort(x);
		buffer.putShort(y);
		buffer.putShort(z);
		buffer.putShort((short)100);
		getValueByKey(buffer.array(), "DONE");
	}

	private synchronized String getValueByKey(byte[] key, String contains) throws IOException {
		outputStream.write(key);
		String line;
		while (true) {
			try {
				line = inputStream.readLine();
				System.out.println(line);
				if (line.contains(contains)) {
					break;
				}
			} catch (IOException e) {
			}
		}
		return line;
	}
	
	private void readToFinish() throws IOException {
		while (inputStream.ready()) {
			System.out.print((char)inputStream.read());
		}
	}

	@Override
	public void close() throws IOException {
		setPosition((short) 1400, (short) 1300, (short) 1500);
		outputStream.close();
		inputStream.close();
		serialPort.close();
	}
}