package org.jftone.listener;

public class JFToneApp {
	public static void initialized() {
		JFToneLauncher.start(null);
	}
	
	public static void destroyed() {
		JFToneLauncher.stop();
	}
}
