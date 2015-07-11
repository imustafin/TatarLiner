package ru.litsey2.computertatarliner;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import lejos.pc.comm.NXTCommLogListener;
import lejos.pc.comm.NXTConnector;

public class BtConnection {

	boolean connected = false;

	DataInputStream dis;
	DataOutputStream dos;
	NXTConnector connector;

	public BtConnection() {

		connector = new NXTConnector();

		connector.addLogListener(new NXTCommLogListener() {

			@Override
			public void logEvent(Throwable throwable) {
				throwable.printStackTrace();
			}

			@Override
			public void logEvent(String message) {
				System.err.println(message);
			}
		});

		connected = connector.connectTo("btspp://00:16:53:0E:86:B0");

		if (!connected) {
			System.err.println("Couldn't connect");
			return;
		}

		dis = new DataInputStream(connector.getInputStream());
		dos = new DataOutputStream(connector.getOutputStream());

	}

	void close() {
		try {
			dis.close();
			dos.close();
			connector.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
