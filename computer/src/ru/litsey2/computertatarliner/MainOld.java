package ru.litsey2.computertatarliner;

import java.awt.Dimension;
import java.io.IOException;
import java.net.URL;

import javax.bluetooth.BluetoothConnectionException;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

public class MainOld {

	static JFrame frame;

	int mod = 0;

	static BtConnection con;

	static SoundPlayer soundPlayer = new SoundPlayer();

	static int lastNumber = 4;

	int getNum(int n) {
		int x = n + mod;
		if (x < 0) {
			x = 4 - x;
		}
		return x;
	}

	static void showError(String msg) {
		JOptionPane.showMessageDialog(frame, msg, "Error",
				JOptionPane.ERROR_MESSAGE);
	}

	void setSlide(int n) {

	}

	public static void main(String[] args) throws BluetoothConnectionException {

		frame = new JFrame("ComputerTatarLiner");
		SpringLayout layout = new SpringLayout();
		JPanel panel = new JPanel(layout);
		JLabel label = new JLabel("labelasdasd");
		final WebView webView = new WebView();
		try {
			URL u = MainOld.class.getResource("/html/index.html");
			System.out.println(u.toURI().toString());
			webView.showPage(u);
		} catch (Exception e) {
			e.printStackTrace();
		}
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

		label.setText("Connecting...");
		con = new BtConnection();
		label.setText("CONNECTED!");

		new Runnable() {

			@Override
			public void run() {
				while (lastNumber == -1) {
				}
				try {
					while (true) {
						con.dos.writeInt(lastNumber);
						con.dos.flush();
						Thread.sleep(100);
					}
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}.run();

		while (true) {
			try {
				int n = con.dis.readInt();
				System.out.println("GOT " + n);
				if (n == lastNumber) {
					continue;
				}
				soundPlayer.waitCompletion();
				lastNumber = n;
				URL htmlUrl = MainOld.class.getResource("/html/" + n + ".html");
				soundPlayer.play(MainOld.class
						.getResource("/sounds/" + n + ".wav"));
				webView.showPage(htmlUrl);
			} catch (Exception e) {
				showError(e.getMessage());
				e.printStackTrace();
				System.exit(0);
			}
		}

	}
}
