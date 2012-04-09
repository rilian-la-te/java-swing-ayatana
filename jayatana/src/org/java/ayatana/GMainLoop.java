package org.java.ayatana;

class GMainLoop {
	private static boolean running = false;
	
	native private static void runGMainLoop();
	native private static void quitGMainLoop();
	
	public synchronized static void run() {
		if (!running) {
			runGMainLoop();
			
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
