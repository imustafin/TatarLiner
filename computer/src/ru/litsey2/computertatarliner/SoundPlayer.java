package ru.litsey2.computertatarliner;

import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class SoundPlayer {

	static Clip lastActive = null;

	void play(URL url) throws UnsupportedAudioFileException, IOException,
			LineUnavailableException {
		if (lastActive != null) {
			lastActive.stop();
		}

		AudioInputStream ain = AudioSystem.getAudioInputStream(url);
		lastActive = AudioSystem.getClip();
		lastActive.open(ain);
		lastActive.start();

	}

	void waitCompletion() {
		// || true because we don't want to wait
		if(lastActive == null || true) {
			return;
		}
		System.err.println("START WAITING");
		while (lastActive.isActive()) {
			System.err.println("waiting");
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.err.println("STOP WAITING");
	}

}
