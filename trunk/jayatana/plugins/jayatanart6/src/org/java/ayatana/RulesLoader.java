package org.java.ayatana;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Pattern;

public class RulesLoader {
	private static final String DEFAULT_MENU_ACTION_CLASS = "org.java.ayatana.DefaultExtraMenuAction";
	
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
		String menuActionClass = System.getProperty("jayatana.menuActionClass");
		String startupWMClass = System.getProperty("jayatana.startupWMClass");
		if (startupWMClass == null && menuActionClass == null) {
			menuActionClass = DEFAULT_MENU_ACTION_CLASS;
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
									if (!"*".equals(param[1])) {
										menuActionClass = param[1];
									}
									if (param.length == 3)
										startupWMClass = param[2];
									return menuActionClass;
								}
							} else if (param[0].startsWith("P:")) {
								if (testProperty(param[0].substring(2))) {
									if (!"*".equals(param[1])) {
										menuActionClass = param[1];
									}
									if (param.length == 3)
										startupWMClass = param[2];
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
		}
		if (startupWMClass != null)
			DesktopFile.setStartupWMClassToToolKit(startupWMClass);
		return menuActionClass;
	}
	
	public static String load(String titleWindow) {
		if (titleWindow == null)
			titleWindow = "unknow";
		String menuActionClass = null;
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
		if (menuActionClass == null)
			menuActionClass = DEFAULT_MENU_ACTION_CLASS;
		return menuActionClass;
	}
}
