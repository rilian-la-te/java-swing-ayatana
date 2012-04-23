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

import java.awt.Toolkit;
import java.io.*;
import java.lang.reflect.Field;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

/**
 * Clase que conteien la descripción del archivo de escritorio
 * 
 * @author Jared González
 */
final public class DesktopFile {
	public static final String BOOLEAN_TRUE = "true";
	public static final String BOOLEAN_FALSE = "false";
	public static final String TYPE_APPLICATION = "Application";
	public static final String TYPE_TERMINAL = "Terminal";
	
	private static DesktopFile desktopFile = null;
	
	/**
	 * Inicializa el desktopFile para generar un archivo lanzador .desktop además de establecer el
	 * nombre de ventana (startupWMClass).
	 * 
	 * @param desktopFileName nombre del archivo
	 * @param startupWMClass nombre de la ventana
	 * @return retorna la instancia única DesktopFile
	 * @throws IOException 
	 */
	public static DesktopFile initialize(String desktopFileName, String startupWMClass) throws IOException {
		if (desktopFileName == null)
			throw new NullPointerException("desktopFileName can't be null");
		desktopFile = new DesktopFile(desktopFileName, startupWMClass);
		return desktopFile;
	}
	/**
	 * Obtiene una instancia DesktopFile para interacturar.
	 * 
	 * @return retorna la instancia única DesktopFile
	 */
	public static DesktopFile getInstance() {
		if (desktopFile == null)
			throw new IllegalAccessError("DesktopFile is not initialized");
		return desktopFile;
	}
	
	private String desktopFileName;
	private String defaultName;
	private Map<Locale, String> names;
	private String defaultComment;
	private Map<Locale, String> comments;
	private String command;
	private String icon;
	private String categories[];
	private String startupWMClass;
	
	private String startupNotify = BOOLEAN_TRUE;
	private String terminal = BOOLEAN_FALSE;
	private String type = TYPE_APPLICATION;
	
	private boolean changed = false;
	
	/**
	 * Contructor del archivo Desktop
	 * 
	 * @param desktopFileName nombre del archivo .desktop
	 * @param startupWMClass nombre de la ventana
	 * @throws IOException en caso de 
	 */
	private DesktopFile(String desktopFileName, String startupWMClass) throws IOException {
		this.desktopFileName = desktopFileName;
		if (!this.desktopFileName.endsWith(".desktop"))
			this.desktopFileName += ".desktop";
		names = new TreeMap<Locale, String>();
		comments = new TreeMap<Locale, String>();
		load();
		setStartupWMClass(startupWMClass);
	}
	
	/**
	 * Establece el nombre del programa
	 * @param name nombre
	 */
	public void setName(String name) {
		defaultName = name;
	}
	/**
	 * Establece el nombre del programa
	 * @param name nombre
	 * @param locale region de lenguaje
	 */
	public void setName(String name, Locale locale) {
		String old = names.put(locale, name);
		if (old == null ? old != null : !old.equals(name))
			changed = true;
	}
	/**
	 * Obtiene el nombre del programa
	 * @return nombre
	 */
	public String getName() {
		return defaultName;
	}
	/**
	 * Obtiene el nombre del programa
	 * @param locale region de lenguaje
	 * @return 
	 */
	public String getName(Locale locale) {
		return names.get(locale);
	}
	
	/**
	 * Establece la descripción del programa
	 * @param comment descripción
	 */
	public void setComment(String comment) {
		defaultComment = comment;
	}
	/**
	 * Establece la descripción del programa
	 * @param comment descripción
	 * @param locale region de lenguaje
	 */
	public void setComment(String comment, Locale locale) {
		String old = comments.put(locale, comment);
		if (old == null ? old != null : !old.equals(old))
			changed = true;
	}
	/**
	 * Obtiene la descripción del programa
	 * @return descripción
	 */
	public String getComment() {
		return defaultComment;
	}
	/**
	 * Obtiene la descripción del programa
	 * @param locale region de lenguaje
	 * @return descripción
	 */
	public String getComment(Locale locale) {
		return comments.get(locale);
	}
	
	/**
	 * Establece el comando de invocación
	 * @param command comando
	 */
	public void setCommand(String command) {
		if (this.command == null ? command != null : !this.command.equals(command))
			changed = true;
		this.command = command;
	}
	/**
	 * Obtiene el comando de invocación
	 * @return comando
	 */
	public String getCommand() {
		return command;
	}
	
	/**
	 * Establece el nombre del icono
	 * @param icon nombre icono
	 */
	public void setIcon(String icon) {
		if (this.icon == null ? icon != null : !this.icon.equals(icon))
			changed = true;
		this.icon = icon;
	}
	/**
	 * Obtiene el nombre del icono
	 * @return nombre icono
	 */
	public String getIcon() {
		return icon;
	}
	
	/**
	 * Convierte una arreglo a una cadena separada por ","
	 * @param categories lista de ctegorias
	 * @return cadena única
	 */
	private String toStringCategories(String ...categories) {
		if (categories == null)
			return null;
		if (categories.length == 0)
			return null;
		String out = "";
		for (String c : categories)
			out += c + ",";
		out = out.substring(0, out.length()-1);
		return out;
	}
	/**
	 * Establece Los nombres de las categorias
	 * @param categories lista de categorias
	 */
	public void setCategories(String ...categories) {
		if (categories == null)
			throw new NullPointerException();
		if (this.categories == null ? categories.length > 0 :
				this.categories.length != categories.length)
			changed = true;
		this.categories = categories;
	}
	/**
	 * Obtiene los nombres de las categories
	 * @return lista de categorias
	 */
	public String[] getCategories() {
		if (categories == null && categories.length == 0)
			return null;
		else
			return categories;
	}
	
	/**
	 * Establece el nombre de clase de ventana
	 * @param startupWMClass nombre de clase de ventana
	 */
	public void setStartupWMClass(String startupWMClass) {
		if (startupWMClass == null)
			throw new NullPointerException("startupWMClass can't be null");
		if (this.startupWMClass == null ? startupWMClass != null : !this.startupWMClass.equals(startupWMClass))
			changed = true;
		this.startupWMClass = startupWMClass;
		if (changed) {
			try {
				Toolkit xToolkit = Toolkit.getDefaultToolkit();
				Field awtAppClassNameField = xToolkit.getClass().getDeclaredField("awtAppClassName");
				awtAppClassNameField.setAccessible(true);
				awtAppClassNameField.set(xToolkit, startupWMClass);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
	/**
	 * Obtiene el nombre de clase de ventana
	 * @return 
	 */
	public String getStartupWMClass() {
		return startupWMClass;
	}
	
	/**
	 * Obtiene el modo de notificación de inicio
	 * @return notificación de inicio
	 */
	private String getStartupNotify() {
		return startupNotify;
	}
	/**
	 * Establece el modo de notificación de inicio
	 * @param startupNotify notificación de inicio
	 */
	private void setStartupNotify(String startupNotify) {
		if (this.startupNotify == null ? startupNotify != null : !this.startupNotify.equals(startupNotify))
			changed = true;
		this.startupNotify = startupNotify;
	}
	
	/**
	 * Obtiene la visibilidad de terminal
	 * @return visibilidad de terminal
	 */
	private String getTerminal() {
		return terminal;
	}
	/**
	 * Establece la visivilidad de terminal
	 * @param terminal visibilidad de terminal
	 */
	private void setTerminal(String terminal) {
		if (this.terminal == null ? terminal != null : !this.terminal.equals(terminal))
			changed = true;
		this.terminal = terminal;
	}
	
	/**
	 * Obtiene el tipo
	 * @return tipo
	 */
	public String getType() {
		return type;
	}
	/**
	 * Establece el tipo
	 * @param type tipo
	 */
	public void setType(String type) {
		if (this.type == null ? type != null : !this.type.equals(type))
			changed = true;
		this.type = type;
	}
	
	/**
	 * Resulve el objeto Locale del alias de lenguaje
	 * @param lang
	 * @return 
	 */
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
	
	/**
	 * Lee la información del archivo Desktop
	 * @return retorna <code>True</code> si puede leer el archivo
	 * @throws IOException 
	 */
	public boolean load() throws IOException {
		File deskFile = getLocalFile();
		if (!deskFile.exists())
			deskFile = new File("/usr/share/applications/" + getDesktopFileName());
		if (deskFile.exists()) {
			BufferedReader reader = new BufferedReader(
					new FileReader(deskFile));
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.contains("=") && !line.startsWith("#")) {
					String param[] = line.split("=");
					String key = param[0].trim().toLowerCase();
					String value = param[1].trim();
					if (key.startsWith("name")) {
						if (key.endsWith("]")) {
							String lang = key.substring(key.indexOf("[")+1, key.length()-1);
							setName(value, resolveLocale(lang));
						} else {
							setName(value);
						}
					} else if (key.startsWith("description")) {
						if (key.endsWith("]")) {
							String lang = key.substring(key.indexOf("[")+1, key.length()-1);
							setComment(value, resolveLocale(lang));
						} else {
							setComment(value);
						}
					} else if (key.equals("encoding")) {
						
					} else if (key.equals("exec")) {
						setCommand(value);
					} else if (key.equals("icon")) {
						setIcon(value);
					} else if (key.equals("categories")) {
						setCategories(value.split(","));
					} else if (key.equals("startupwmclass")) {
						setStartupWMClass(value);
					} else if (key.equals("startupnotify")) {
						setStartupNotify(value);
					} else if (key.equals("type")) {
						setType(value);
					} else if (key.equals("terminal")) {
						setTerminal(value);
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
	/**
	 * Actualiza o crea el archivo .desktop
	 * @return retorna <code>True</code> si puede crear o actualizar el archivo
	 * @throws IOException 
	 */
	public boolean update() throws IOException {
		if (changed) {
			File df = getLocalFile();
			
			BufferedWriter writer = new BufferedWriter(
					new FileWriter(df));
			writer.write("[Desktop Entry]");
			writer.newLine();
			
			if (getName() != null) {
				writer.write("Name="+getName());
				writer.newLine();
			}
			for (Map.Entry<Locale, String> entry : names.entrySet()) {
				if (entry.getValue() != null) {
					writer.write("Name["+entry.getKey().toString()+"]="+entry.getValue());
					writer.newLine();
				}
			}
			if (getComment() != null) {
				writer.write("Comment="+getComment());
				writer.newLine();
			}
			for (Map.Entry<Locale, String> entry : comments.entrySet()) {
				if (entry.getValue() != null) {
					writer.write("Comment["+entry.getKey().toString()+"]="+entry.getValue());
					writer.newLine();
				}
			}
			if (getCommand() != null) {
				writer.write("Exec="+getCommand());
				writer.newLine();
			}
			if (getIcon() != null) {
				writer.write("Icon="+getIcon());
				writer.newLine();
			}
			if (getCategories() != null) {
				writer.write("Categories=");
				String cats[] = getCategories();
				for (int i=0;i<cats.length;i++) {
					if (i < cats.length-1)
						writer.write(cats[i]+",");
					else
						writer.write(cats[i]);
				}
				writer.newLine();
			}
			if (getStartupWMClass() != null) {
				writer.write("StartupWMClass="+getStartupWMClass());
				writer.newLine();
			}
			if (getStartupNotify() != null) {
				writer.write("StartupNotify="+getStartupNotify());
				writer.newLine();
			}
			if (getTerminal() != null) {
				writer.write("Terminal="+getTerminal());
				writer.newLine();
			}
			if (getType() != null) {
				writer.write("Type="+getType());
				writer.newLine();
			}
			writer.close();
			
			try {
				Runtime.getRuntime().exec("xdg-desktop-menu forceupdate");
			} catch (IOException e) {
				//ignorar
			}
			
			changed = false;
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Elimina el archivo .desktop
	 * @return 
	 */
	public boolean delete() {
		return getLocalFile().delete();
	}
	
	/**
	 * Obtiene el nombre del archivo .desktop
	 * @return 
	 */
	public String getDesktopFileName() {
		return desktopFileName;
	}
	
	/**
	 * Obtiene el objeto File del archivo .desktop
	 * @return 
	 */
	File getLocalFile() {
		return new File(System.getProperty("user.home"),
				"/.local/share/applications/" + getDesktopFileName());
	}
}
