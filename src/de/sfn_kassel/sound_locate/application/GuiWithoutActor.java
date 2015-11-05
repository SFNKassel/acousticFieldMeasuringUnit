package de.sfn_kassel.sound_locate.application;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import javax.sound.sampled.LineUnavailableException;
import javax.swing.JButton;
import javax.swing.JFrame;

import de.sfn_kassel.sound_locate.audio.MapWriter;
import de.sfn_kassel.sound_locate.audio.Measure;

public class GuiWithoutActor {
	
	final double visualMultiplyer;

	public static void main(String[] args) throws LineUnavailableException, IOException {
		new GuiWithoutActor(10, 10, 1000, 1000, 44100, "measurement.csv", 3);
	}

	public GuiWithoutActor(int width, int height, int measureTime, int measureFreq, int sampleRate, String fileName, int visualMultiplyer)
			throws LineUnavailableException, IOException {
		this.visualMultiplyer = visualMultiplyer;
		
		Map<ComparablePoint, Double> map = new TreeMap<>();
		@SuppressWarnings("resource")
		Measure m = new Measure(sampleRate);
		MapWriter writer = new MapWriter(map);
		
		if(new File(fileName).exists()) {
			BufferedReader in = new BufferedReader(new FileReader(new File(fileName)));
			try {
				while (true) {
					String line = in.readLine();
					String[] params = line.split(",");
					map.put(new ComparablePoint(Integer.parseInt(params[0].trim()), Integer.parseInt(params[1].trim())), Double.parseDouble(params[2].trim()));
				}
			} catch (Exception e) {
				in.close();
			}
		}

		JFrame window = new JFrame("acoustic field measuring unit");
		window.setLayout(new GridLayout(width, height));
		JButton[][] buttons = new JButton[width][height];

		for (int y = 0; y < width; y++) {
			for (int x = 0; x < height; x++) {
				JButton button = new JButton("(" + x + "|" + y + ")");
				button.setBackground(Color.LIGHT_GRAY);
				button.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						int x = Integer.parseInt(e.getActionCommand().split("\\(")[1].split("\\|")[0]);
						int y = Integer.parseInt(e.getActionCommand().split("\\|")[1].split("\\)")[0]);
//						System.out.println("(" + x + "|" + y + ")");
						map.put(new ComparablePoint(x, y), m.measure(measureFreq, measureTime));
						try {
							writer.writeToCsv(new File(fileName), ",", "\n");
						} catch (IOException e1) {
							e1.printStackTrace();
						}
						repaintButtons(buttons, map);
					}
				});
				buttons[x][y] = button;
				window.add(button);
			}
		}

		repaintButtons(buttons, map);

		window.setSize(width * 70, height * 70);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setExtendedState(window.getExtendedState() | JFrame.MAXIMIZED_BOTH);
		window.setVisible(true);
	}

	private Color valToColor(double value) {
		try {
			return new Color((int) (value * visualMultiplyer * 255), (int) (255 - value * visualMultiplyer * 255), 0);
		} catch (Exception ignore) {
		}
		return Color.RED;
	}

	private void repaintButtons(JButton[][] buttons, Map<ComparablePoint, Double> map) {
		map.forEach((p, v) -> {
			buttons[p.x][p.y].setBackground(valToColor(v));
			buttons[p.x][p.y].setText("(" + p.x + "|" + p.y + ") " + v);
		});
	}
}
