package de.sfn_kassel.sound_locate.application;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class Launcher {
	public static void main(String[] args) {
		JFrame window = new JFrame("acoustic field measuring unit launcher");

		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setLayout(new GridLayout(0, 2));

		window.add(new JLabel("Width"));
		JTextField width = new JTextField();
		window.add(width);

		window.add(new JLabel("Height"));
		JTextField height = new JTextField();
		window.add(height);

		window.add(new JLabel("Sample rate"));
		JTextField sampleRate = new JTextField();
		window.add(sampleRate);

		window.add(new JLabel("Measure frequency"));
		JTextField freq = new JTextField();
		window.add(freq);

		window.add(new JLabel("Measure time"));
		JTextField time = new JTextField();
		window.add(time);

		window.add(new JLabel("Visualisation multiplyer"));
		JTextField mul = new JTextField();
		window.add(mul);

		window.add(new JLabel("File"));
		JTextField file = new JTextField();
		window.add(file);

		JButton start = new JButton("Start");
		window.add(start);

		start.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				window.setVisible(false);
				try {
					new GuiWithoutActor(Integer.parseInt(width.getText()), Integer.parseInt(height.getText()),
							Integer.parseInt(time.getText()), Integer.parseInt(freq.getText()),
							Integer.parseInt(sampleRate.getText()), file.getText(), Integer.parseInt(mul.getText()));
				} catch (Exception e2) {
					JOptionPane.showMessageDialog(new JFrame(), "An exception occured: " + e2);
					System.exit(-1);
				}
			}
		});

		window.setSize(300, 300);
		window.setResizable(false);
		window.setVisible(true);
	}
}
