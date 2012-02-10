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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

/**
 * Clase que conteien la descripción del archivo de escritorio
 * 
 * @author Jared González
 */
public class DesktopFile {
	private String desktopFileName;
	private String defaultName;
	private Map<Locale, String> names;
	private String defaultDescription;
	private Map<Locale, String> descriptions;
	private String command;
	private String icon;
	private String categories;
	private String startupWMClass;
	
	private String startupNotify = "true";
	private String terminal = "false";
	private String type = "Application";
	
	private boolean changed = false;
	
	public DesktopFile(String desktopFileName, String startupWMClass) throws IOException {
		if (desktopFileName == null)
			throw new NullPointerException("desktopFileName can't be null");
		this.desktopFileName = desktopFileName;
		if (!this.desktopFileName.endsWith(".desktop"))
			this.desktopFileName += ".desktop";
		this.names = new TreeMap<Locale, String>();
		this.descriptions = new TreeMap<Locale, String>();
		this.load();
		this.setStartupWMClass(startupWMClass);
	}
	
	public void setName(String name) {
		this.defaultName = name;
	}
	public void setName(String name, Locale locale) {
		String old = names.put(locale, name);
		if (old == null ? old != null : !old.equals(name))
			changed = true;
	}
	public String getName() {
		return defaultName;
	}
	public String getName(Locale locale) {
		return names.get(locale);
	}
	
	public void setDescription(String description) {
		this.defaultDescription = description;
	}
	public void setDescription(String description, Locale locale) {
		String old = descriptions.put(locale, description);
		if (old == null ? old != null : !old.equals(old))
			changed = true;
	}
	public String getDescription() {
		return defaultDescription;
	}
	public String getDescription(Locale locale) {
		return descriptions.get(locale);
	}
	
	public void setCommand(String command) {
		if (this.command == null ? this.command != command : !this.command.equals(command))
			changed = true;
		this.command = command;
	}
	public String getCommand() {
		return command;
	}
	public void setIcon(String icon) {
		if (this.icon == null ? this.icon != icon : !this.icon.equals(icon))
			changed = true;
		this.icon = icon;
	}
	public String getIcon() {
		return icon;
	}
	
	public void setCategories(String categories) {
		if (this.categories == null ? this.categories != categories : !this.categories.equals(categories))
			changed = true;
		this.categories = categories;
	}
	public String getCategories() {
		return categories;
	}
	
	public void setStartupWMClass(String startupWMClass) {
		if (this.startupWMClass == null ? this.startupWMClass != startupWMClass : !this.startupWMClass.equals(startupWMClass))
			changed = true;
		this.startupWMClass = startupWMClass;
	}
	public String getStartupWMClass() {
		return startupWMClass;
	}
	
	private String getStartupNotify() {
		return startupNotify;
	}
	private void setStartupNotify(String startupNotify) {
		if (this.startupNotify == null ? this.startupNotify != startupNotify : !this.startupNotify.equals(startupNotify))
			changed = true;
		this.startupNotify = startupNotify;
	}
	private String getTerminal() {
		return terminal;
	}
	private void setTerminal(String terminal) {
		if (this.terminal == null ? this.terminal != terminal : !this.terminal.equals(terminal))
			changed = true;
		this.terminal = terminal;
	}
	private String getType() {
		return type;
	}
	private void setType(String type) {
		if (this.type == null ? this.type != type : !this.type.equals(type))
			changed = true;
		this.type = type;
	}
	
	private Locale resolveLocale(String lang) {
		Locale locale;
		if (lang.contains("_")) {
			String param[] = lang.split("_");
			locale = new Locale(param[0], param[1]);
		} else {
			locale = new Locale(lang);
		}
		return locale;
	}
	
	public boolean load() throws IOException {
		File deskFile = new File(System.getProperty("user.home"),
				"/.local/share/applications/" + this.getDesktopFileName());
		if (!deskFile.exists()) {
			deskFile = new File("/usr/share/applications/" + this.getDesktopFileName());
		}
		if (deskFile.exists()) {
			BufferedReader reader = new BufferedReader(
					new FileReader(deskFile));
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.contains("=") && !line.startsWith("#")) {
					String param[] = line.split("=");
					String key = param[0].trim().toLowerCase();
					String value = param[1].trim().toLowerCase();
					if (key.startsWith("name")) {
						if (key.endsWith("]")) {
							String lang = key.substring(key.indexOf("[")+1, key.length()-1);
							this.setName(value, this.resolveLocale(lang));
						} else {
							this.setName(value);
						}
					} else if (key.startsWith("description")) {
						if (key.endsWith("]")) {
							String lang = key.substring(key.indexOf("[")+1, key.length()-1);
							this.setDescription(value, this.resolveLocale(lang));
						} else {
							this.setDescription(value);
						}
					} else if (key.equals("encoding")) {
						
					} else if (key.equals("exec")) {
						this.setCommand(value);
					} else if (key.equals("categories")) {
						this.setCategories(value);
					} else if (key.equals("startupwmclass")) {
						this.setStartupWMClass(value);
					} else if (key.equals("type")) {
						this.setType(value);
					} else if (key.equals("terminal")) {
						this.setTerminal(value);
					}
				}
			}
			reader.close();
			changed = false;
			return true;
		} else {
			return false;
		}
	}
	public boolean update() {
		if (changed) {
			File deskFile = new File(System.getProperty("user.home"),
				"/.local/share/applications/" + this.getDesktopFileName());
			
			changed = false;
			return true;
		} else {
			return false;
		}
	}
	
	public String getDesktopFileName() {
		return desktopFileName;
	}
}
