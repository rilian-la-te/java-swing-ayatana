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

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.SwingUtilities;

/**
 * Esta clase permite gestionar todas la integraciones sobre el escritorio Ayatana
 * de Ubuntu
 * 
 * @author Jared González
 */
public class Ayatana {
	/**
	 * Validar el menu de aplicaciones global esta presente
	 * 
	 * @return retorna <code>true</code> si el menu de aplicaciones global esta presente
	 * de lo contrario retorna <code>false</code>
	 */
	private static boolean isApplicationMenuPresent() {
		if (!"Linux".equals(System.getProperty("os.name")))
			return false;
		if (!"libappmenu.so".equals(System.getenv("UBUNTU_MENUPROXY")))
			return false;
		return true;
	}
	
	/**
	 * Instala el menu global de aplicación, requiere que la ventana tenga una barra
	 * de menus predeterminada <code>frame.setJMenuBar</code>
	 * 
	 * @param frame Ventana que contiene la barra de menus
	 */
	public static boolean tryInstallApplicationMenu(final JFrame frame) {
		return tryInstallApplicationMenu(frame, frame.getJMenuBar());
	}
	/**
	 * Instala el menu global de aplicación, requiere que la ventana tenga una barra
	 * de menus contendia en esta
	 * 
	 * @param frame Ventana que contiene la barra de menus
	 * @param menubar Barra de menus que será exportada al menu global de aplicación
	 */
	public static boolean tryInstallApplicationMenu(final JFrame frame, final JMenuBar menubar) {
		if (frame == null || menubar == null)
			return false;
		
		if (isApplicationMenuPresent()) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					new ApplicationMenu(frame, menubar);
				}
			});
			return true;
		}
		return false;
	}
}
