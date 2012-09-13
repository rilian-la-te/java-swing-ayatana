/*
 * Copyright (c) 2012 Jared González
 * 
 * Permission is hereby granted, free of charge, to any
 * person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the
 * Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the
 * Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice
 * shall be included in all copies or substantial portions of
 * the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY
 * KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS
 * OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.java.ayatana;

import java.awt.Window;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Pattern;
import javax.swing.JFrame;

public class RulesLoader {
	private static final String DEFAULT_MENU_ACTION_CLASS = "org.java.ayatana.DefaultExtraMenuAction";
	
	public static void rulesLoad(Window window) {
		if (AyatanaDesktop.isSupported() && ApplicationMenu.getWindowMenuBar(window) != null &&
				!System.getProperties().containsKey("jayatana.ignoreEndorsed")) {
			String menuActionClass = rolesLoad(ApplicationMenu.getWindowTitle(window),
					window instanceof JFrame);
			ExtraMenuAction extraMenuAction = null;
			try {
				extraMenuAction = (ExtraMenuAction)Class.forName(menuActionClass).newInstance();
			} catch (Exception e) {
				extraMenuAction = new DefaultExtraMenuAction();
			} finally {
				ApplicationMenu.tryInstall(window, extraMenuAction);
			}
		}
	}
	
	private static boolean testTitle(String rule, String titleWindow) {
		Pattern patter = Pattern.compile(rule, Pattern.CASE_INSENSITIVE);
		return patter.matcher(titleWindow).matches();
	}
	private static boolean testProperty(String rule) {
		String propertyRule[];
		if (rule.contains("="))
			propertyRule = new String[] {
				rule.substring(0, rule.indexOf("=")),
				rule.substring(rule.indexOf("=")+1)
			};
		else
			propertyRule = new String[] {rule};
		
		for (Object key : System.getProperties().keySet()) {
			Pattern patter = Pattern.compile(propertyRule[0], Pattern.CASE_INSENSITIVE);
			if (patter.matcher((String)key).matches()) {
				if (propertyRule.length == 1) {
					return true;
				} else {
					String value = System.getProperty((String)key);
					Pattern patterValue = Pattern.compile(propertyRule[1], Pattern.CASE_INSENSITIVE);
					if (patterValue.matcher(value).matches())
						return true;
				}
			}
		}
		return false;
	}
	
	private static String testRule(File frules, String titleWindow) {
		String menuActionClass = null;
		try {
			BufferedReader reader = new BufferedReader(
					new FileReader(frules));
			try {
				String line;
				String input;
				String param[];
				while ((line = reader.readLine()) != null) {
					if (!line.startsWith("#") && line.contains("\t")) {
						input = line.replaceAll("\t+", "\t");
						param = input.split("\t");
						if (param[0].startsWith("T:")) {
							if (testTitle(param[0].substring(2), titleWindow)) {
								if (!"*".equals(param[1]))
									menuActionClass = param[1];
								else
									menuActionClass = DEFAULT_MENU_ACTION_CLASS;
								if (param.length == 3)
									DesktopFile.setStartupWMClassToToolKit(param[2]);
								return menuActionClass;
							}
						} else if (param[0].startsWith("P:")) {
							if (testProperty(param[0].substring(2))) {
								if (!"*".equals(param[1]))
									menuActionClass = param[1];
								else
									menuActionClass = DEFAULT_MENU_ACTION_CLASS;
								if (param.length == 3)
									DesktopFile.setStartupWMClassToToolKit(param[2]);
								return menuActionClass;
							}
						}
					}
				}
			} finally {
				reader.close();
			}
		} catch (IOException e) {
			// ignorar
		}
		return menuActionClass;
	}
	
	private static String rolesLoad(String titleWindow, boolean updateStartupWMClass) {
		String menuActionClass = System.getenv("JAYATANA_MENUACTIONCLASS");
		if (menuActionClass == null)
			System.getProperty("jayatana.menuActionClass");
		
		String startupWMClass = null;
		if (updateStartupWMClass) {
			startupWMClass = System.getenv("JAYATANA_STARTUPWMCLASS");
			if (startupWMClass == null)
				System.getProperty("jayatana.startupWMClass");
		}
		
		if (startupWMClass == null && menuActionClass == null) {
			if (titleWindow == null)
				titleWindow = "unknow";
			File frulesDir = new File(System.getProperty("user.home"),".jayatana.rules.d");
			if (frulesDir.exists() && frulesDir.isDirectory() && frulesDir.canRead())
				for (File fr : frulesDir.listFiles()) {
					if (fr.isFile() && fr.canRead()) {
						if ((menuActionClass = testRule(fr, titleWindow)) != null)
							return menuActionClass;
					}
				}
			frulesDir = new File("/etc/jayatana.rules.d");
			if (frulesDir.exists() && frulesDir.isDirectory() && frulesDir.canRead())
				for (File fr : frulesDir.listFiles()) {
					if (fr.isFile() && fr.canRead()) {
						if ((menuActionClass = testRule(fr, titleWindow)) != null)
							return menuActionClass;
					}
				}
			File frules = new File("/etc/jayatana.rules");
			if (frules.exists() && frules.isFile() && frules.canRead()) {
				if ((menuActionClass = testRule(frules, titleWindow)) != null)
					return menuActionClass;
			}
		} else {
			if (startupWMClass != null)
				DesktopFile.setStartupWMClassToToolKit(startupWMClass);
		}
		if (menuActionClass == null)
			menuActionClass = DEFAULT_MENU_ACTION_CLASS;
		return menuActionClass;
	}
}
