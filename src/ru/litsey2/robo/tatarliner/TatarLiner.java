package ru.litsey2.robo.tatarliner;

import java.io.IOException;

import lejos.nxt.Button;
import lejos.nxt.ColorSensor;
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.SensorPort;
import lejos.nxt.Sound;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.robotics.subsumption.Arbitrator;
import lejos.robotics.subsumption.Behavior;

public class TatarLiner {

	int lastMessage = 0;

	boolean noBluetooth;

	static final int STATION_DIST = 10 * 10; // mm

	BtPinger btClient;

	static final int LIGHT_VAL_THRESHOLD = 450; // 0..1023

	static final int TRAVEL_SPEED = 200; // mm/sec
	static final int ROTATE_SPEED = 100; // mm/sec

	DifferentialPilot pilot;

	static final int WHEEL_DIAMETER = 56; // mm
	static final int TRACK_WIDTH = 145; // mm

	ColorSensor leftColorSensor = new ColorSensor(SensorPort.S4);
	ColorSensor rightColorSensor = new ColorSensor(SensorPort.S1);

	NXTRegulatedMotor leftMotor = Motor.C;
	NXTRegulatedMotor rightMotor = Motor.A;

	boolean isWhite(ColorSensor colorSensor) {
		return colorSensor.getRawLightValue() > LIGHT_VAL_THRESHOLD;
	}

	class GoForwardBehavior implements Behavior {

		boolean suppressed = true;

		@Override
		public boolean takeControl() {
			return (isWhite(leftColorSensor) && isWhite(rightColorSensor));
		}

		@Override
		public void action() {
			suppressed = false;
			System.out.println("GO_FORWARD");
			pilot.setTravelSpeed(TRAVEL_SPEED);
			pilot.forward();
			while (!suppressed && takeControl())
				;
			pilot.stop();
			suppressed = true;
		}

		@Override
		public void suppress() {
			suppressed = true;
		}
	}

	class TurnRightBehavior implements Behavior {

		boolean suppressed = true;

		@Override
		public boolean takeControl() {
			return (isWhite(leftColorSensor) && !isWhite(rightColorSensor));
		}

		@Override
		public void action() {
			suppressed = false;
			System.out.println("TURN_RIGHT");
			pilot.setTravelSpeed(ROTATE_SPEED);
			pilot.rotateRight();
			while (!suppressed && takeControl())
				;
			pilot.stop();
			suppressed = true;
		}

		@Override
		public void suppress() {
			suppressed = true;
		}
	}

	class TurnLeftBehavior implements Behavior {

		boolean suppressed = true;

		@Override
		public boolean takeControl() {
			return (!isWhite(leftColorSensor) && isWhite(rightColorSensor));
		}

		@Override
		public void action() {
			suppressed = false;
			System.out.println("TURN_LEFT");
			pilot.setTravelSpeed(ROTATE_SPEED);
			pilot.rotateLeft();
			while (!suppressed && takeControl())
				;
			pilot.stop();
			suppressed = true;
		}

		@Override
		public void suppress() {
			suppressed = true;
		}
	}

	class ExitOnEnter implements Behavior {

		@Override
		public boolean takeControl() {
			return Button.ENTER.isDown();
		}

		@Override
		public void action() {
			if (btClient != null) {
				try {
					btClient.close();
				} catch (IOException e) {
				}
			}
			System.exit(0);
		}

		@Override
		public void suppress() {
		}

	}

	class NextSegmentBehavior implements Behavior {

		boolean suppressed = true;

		@Override
		public boolean takeControl() {
			return (!isWhite(leftColorSensor) && !isWhite(rightColorSensor));
		}

		@Override
		public void action() {
			suppressed = false;
			System.out.println("NEXT_SEGMENT");
			pilot.setTravelSpeed(TRAVEL_SPEED);
			Sound.playTone(600, 100, 100);

			if (!noBluetooth) {
				try {
					btClient.sendAndWait();
				} catch (IOException e) {
					System.out.println("IOException");
				} catch (InterruptedException e) {
					System.out.println("InterruptedException");
				}
			}
			if (suppressed) {
				return;
			}
			pilot.travel(STATION_DIST);
			if (pilot.isMoving()) {
				pilot.stop();
			}
			suppressed = true;
		}

		@Override
		public void suppress() {
			suppressed = true;
			btClient.stop();
		}
	}

	void start() throws InterruptedException {

		System.out.println("Hi!");
		Thread.sleep(500);

		if (Button.ESCAPE.isDown()) {
			System.out.println("No Bluetooth mode");
			noBluetooth = true;
		} else {
			noBluetooth = false;
			try {
				btClient = new BtPinger();
			} catch (Exception e) {
				System.out.println("CONNECTION ERROR");
			}
		}

		System.out.println("Connected");

		pilot = new DifferentialPilot(WHEEL_DIAMETER, TRACK_WIDTH, leftMotor,
				rightMotor);
		pilot.setTravelSpeed(TRAVEL_SPEED);

		Behavior[] behaviors = { new GoForwardBehavior(),
				new TurnRightBehavior(), new TurnLeftBehavior(),
				new NextSegmentBehavior(), new ExitOnEnter() };

		Arbitrator arbitrator = new Arbitrator(behaviors);
		arbitrator.start();
	}

	public static void main(String[] args) throws InterruptedException {
		TatarLiner ex = new TatarLiner();
		ex.start();
	}

}
