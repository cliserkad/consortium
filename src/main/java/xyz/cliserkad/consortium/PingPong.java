package xyz.cliserkad.consortium;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PingPong extends Thread {

	private final NetworkedController<?> controller;

	/** Pings the controller every second */
	public PingPong(NetworkedController<?> controller) {
		this.controller = controller;
	}

	@Override
	public void run() {

		}
	}

}
