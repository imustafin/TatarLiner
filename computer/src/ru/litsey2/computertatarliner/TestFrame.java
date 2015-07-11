package ru.litsey2.computertatarliner;

import java.awt.Dimension;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

public class TestFrame {
	public static void main(String[] args)
			throws UnsupportedAudioFileException, IOException,
			LineUnavailableException, URISyntaxException {

		JFrame frame = new JFrame("ComputerTatarLiner");
		SpringLayout layout = new SpringLayout();
		JPanel panel = new JPanel(layout);
		JLabel label = new JLabel("labelasdasd");
		WebView webView = new WebView();
		URL u = Main.class.getResource("/html/1.html");
		System.out.println(u.toURI().toString());
		webView.showPage(u);
		panel.add(webView);
		panel.add(label);

		layout.putConstraint(SpringLayout.NORTH, webView, 0,
				SpringLayout.NORTH, panel);
		layout.putConstraint(SpringLayout.SOUTH, label, 0, SpringLayout.SOUTH,
				panel);
		layout.putConstraint(SpringLayout.SOUTH, webView, 0,
				SpringLayout.NORTH, label);
		layout.putConstraint(SpringLayout.EAST, webView, 0, SpringLayout.EAST,
				panel);
		layout.putConstraint(SpringLayout.WEST, webView, 0, SpringLayout.WEST,
				panel);

		frame.add(panel);
		frame.setMinimumSize(new Dimension(640, 480));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	
	}
}
