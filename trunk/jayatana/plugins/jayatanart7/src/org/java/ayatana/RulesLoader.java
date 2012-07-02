package org.java.ayatana;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Pattern;

public class RulesLoader {
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
	public static String load(String titleWindow) {
		if (titleWindow == null)
			titleWindow = "java-lang-Thread";
		String menuActionClass = "org.java.ayatana.DefaultExtraMenuAction";
		File frules = new File(System.getProperty("java.home"),
						"lib/endorsed/jayatana.rules");
		if (frules.exists()) {
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
									if (param.length == 3) {
										if (!"*".equals(param[2]))
											DesktopFile.setStartupWMClassToToolKit(param[2]);
									}
									return menuActionClass;
								}
							} else if (param[0].startsWith("P:")) {
								if (testProperty(param[0].substring(2))) {
									if (!"*".equals(param[1])) {
										menuActionClass = param[1];
									}
									if (param.length == 3) {
										if (!"*".equals(param[2]))
											DesktopFile.setStartupWMClassToToolKit(param[2]);
									}
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
		return menuActionClass;
	}
}
