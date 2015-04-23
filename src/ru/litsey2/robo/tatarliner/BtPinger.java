package ru.litsey2.robo.tatarliner;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import lejos.nxt.comm.BTConnection;
import lejos.nxt.comm.Bluetooth;

public class BtPinger {

	int lastMessage = 0;

	final int TIMEOUT = 10 * 1000;

	BTConnection con;

	DataInputStream dis;
	DataOutputStream dos;

	BtPinger() {
		con = Bluetooth.waitForConnection();
		System.out.println("Connected");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		dis = con.openDataInputStream();
		dos = con.openDataOutputStream();
	}

	boolean stopped = true;

	/**
	 * Send new ping and wait for a response. May be stopped with
	 * {@link #stop()}.
	 * 
	 * @return <code>true</code> if received response, <code>false</code>
	 *         otherwise
	 * @throws IOException
	 * @throws InterruptedException
	 */
	boolean sendAndWait() throws IOException, InterruptedException {
		stopped = false;
		lastMessage++;
		dos.writeInt(lastMessage);
		dos.flush();
		System.err.println("Sent " + lastMessage);
		while (true) {
			if (dis.available() == 0) {
				if (stopped) {
					return false;
				}
				Thread.sleep(50);
			}
			int x = dis.readInt();
			System.err.println("Got " + x);
			if (x == lastMessage) {
				return true;
			}
			dos.writeInt(lastMessage);
			dos.flush();
			System.err.println("Sent " + lastMessage);
			Thread.sleep(50);
		}
	}

	void stop() {
		stopped = true;
	}

	void close() throws IOException {
		if (dis != null) {
			dis.close();
		}
		if (dos != null) {
			dos.close();
		}
		if (con != null) {
			con.close();
		}
	}

}
