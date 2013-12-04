package com.jarego.jayatana.swing;

import java.awt.Toolkit;
import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jarego.jayatana.Feature;

public class SwingWMClass implements Feature {
	@Override
	public void deploy() {
		String startupWMClass = null;
		
		if (System.getProperty("jayatana.startupWMClass") != null)
			startupWMClass = System.getProperty("jayatana.startupWMClass");
		if (System.getProperty("JAYATANA_STARTUPWMCLASS") != null)
			startupWMClass = System.getProperty("JAYATANA_STARTUPWMCLASS");
		
		if (startupWMClass != null) {
			try {
				System.setProperty("java.awt.WM_CLASS", startupWMClass);
				Toolkit xToolkit = Toolkit.getDefaultToolkit();
				Field awtAppClassNameField = xToolkit.getClass()
						.getDeclaredField("awtAppClassName");
				awtAppClassNameField.setAccessible(true);
				awtAppClassNameField.set(xToolkit, startupWMClass);
				awtAppClassNameField.setAccessible(false);
			} catch (Exception e) {
				Logger.getLogger(SwingWMClass.class.getName())
						.log(Level.WARNING, e.getMessage(), e);
			}
		}
	}
}
