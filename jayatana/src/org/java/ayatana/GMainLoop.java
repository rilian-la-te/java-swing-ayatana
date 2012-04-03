package org.java.ayatana;

import java.awt.EventQueue;

class GMainLoop {
	private static boolean running = false;
	
	native private static void runGMainLoop();
	native private static void quitGMainLoop();
	
	public synchronized static void run() {
		if (!running) {
			EventQueue.invokeLater(new Runnable() {
				@Override
				public void run() {
					runGMainLoop();
				}
			});
			
			Thread threadQuit = new Thread() {
				@Override
				public void run() {
					quitGMainLoop();
				}
			};
			threadQuit.setDaemon(true);
			Runtime.getRuntime().addShutdownHook(threadQuit);
			
			running = true;
		}
	}
}
