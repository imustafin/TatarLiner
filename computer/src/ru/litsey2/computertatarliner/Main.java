package ru.litsey2.computertatarliner;

import java.awt.Dimension;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

public class Main {

	static JFrame frame;

	static final int STATION_NUMBER = 4;

	static BtConnection con;

	static SoundPlayer soundPlayer = new SoundPlayer();

	static int currentStation = 3;

	static int lastMessage = -1;

	static void showError(String msg) {
		JOptionPane.showMessageDialog(frame, msg, "Error",
				JOptionPane.ERROR_MESSAGE);
	}

	static JLabel label;
	static WebView webView;

	static void createFrame() throws IOException, URISyntaxException {
		frame = new JFrame("ComputerTatarLiner");
		SpringLayout layout = new SpringLayout();
		JPanel panel = new JPanel(layout);
		label = new JLabel("labelasdasd");
		webView = new WebView();
		URL u = Main.class.getResource("/html/index.html");
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

	static class BtCommunicationRunnable implements Runnable {

		@Override
		public void run() {
			while (true) {
				try {
					if (con.dis.available() == 0) {
						Thread.sleep(500);
					}
					int x = con.dis.readInt();
					System.err.println("Got " + x + ". LastMessage " + lastMessage);
					if (x >= lastMessage) {
						addStationNumber(x - lastMessage);
						lastMessage = x;
						con.dos.writeInt(x);
						con.dos.flush();
						System.err.println("Sent " + x);
					}
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (UnsupportedAudioFileException e) {
					e.printStackTrace();
				} catch (LineUnavailableException e) {
					e.printStackTrace();
				}
			}
		}
	}

	static void addStationNumber(int delta)
			throws UnsupportedAudioFileException, IOException,
			LineUnavailableException {

		if (delta == 0) {
			return;
		}

		currentStation = (currentStation + delta) % STATION_NUMBER;

		soundPlayer.waitCompletion();
		URL htmlUrl = Main.class.getResource("/html/" + currentStation
				+ ".html");
		soundPlayer.play(Main.class.getResource("/sounds/" + currentStation
				+ ".wav"));
		webView.showPage(htmlUrl);
	}

	public static void main(String[] args) throws IOException,
			URISyntaxException {

		try {
			createFrame();
		} catch (Exception ex) {
			showError(ex.getMessage());
		}

		label.setText("Connecting...");
		con = new BtConnection();
		label.setText("CONNECTED!");

		new BtCommunicationRunnable().run();

	}
}
