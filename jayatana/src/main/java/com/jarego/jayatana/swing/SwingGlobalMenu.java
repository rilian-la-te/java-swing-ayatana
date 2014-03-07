/*
 * Copyright (c) 2014 Jared González
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
package com.jarego.jayatana.swing;

import java.awt.AWTEvent;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.AWTEventListener;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenuBar;

import com.jarego.jayatana.Feature;
import com.jarego.jayatana.FeatureManager;
import com.jarego.jayatana.basic.GlobalMenu;

/**
 * Clase de caráteristica para desplegar la integración con el menú global
 * de Ubuntu para aplicaciones Java Swing.
 * 
 * @author Jared González
 */
public class SwingGlobalMenu implements Feature, AWTEventListener {
	/**
	 * Iniciar despliege de característica para la integración del menú global
	 * de Ubuntu.
	 */
	@Override
	public void deploy() {
		GlobalMenu.nativeInitialize();
		Toolkit.getDefaultToolkit().addAWTEventListener(
				SwingGlobalMenu.this, AWTEvent.WINDOW_EVENT_MASK);
	}
	
	/**
	 * Escucha de evento de apertura de ventana para lazar integración
	 * para cada ventana nueva lanzada.
	 */
	@Override
	public void eventDispatched(AWTEvent event) {
		if (event.getID() == WindowEvent.WINDOW_OPENED) {
			if (event.getSource() instanceof JFrame)
				installOnWindow((JFrame)event.getSource());
			else if (event.getSource() instanceof JDialog)
				installOnWindow((JDialog)event.getSource());
		}
	}
	
	/**
	 * Instalar sobre ventana de <code>JFrame</code>.
	 * 
	 * @param jframe ventana
	 */
	protected void installOnWindow(JFrame jframe) {
		JMenuBar menubar;
		if ((menubar = retriveMenuBar(jframe)) != null)
			tryInstallGlobalMenu(jframe, menubar);
	}
	/**
	 * Instalar sobre ventana de <code>JDialog</code>.
	 * 
	 * @param jdialog ventana.
	 */
	protected void installOnWindow(JDialog jdialog) {
		JMenuBar menubar;
		if ((menubar = retriveMenuBar(jdialog)) != null)
			tryInstallGlobalMenu(jdialog, menubar);
	}
	
	/**
	 * Recuperar barra de menús de la ventana.
	 * 
	 * @param jframe ventana.
	 * @return Barra de menús.
	 */
	protected JMenuBar retriveMenuBar(JFrame jframe) {
		JMenuBar menuBar = null;
		if (jframe.getRootPane().getClientProperty("jayatana.menubar") instanceof JMenuBar)
			menuBar = (JMenuBar)jframe.getRootPane().getClientProperty("jayatana.menubar");
		else
			menuBar = jframe.getJMenuBar();
		return menuBar;
	}
	/**
	 * Recuperar barra de menús de la ventana.
	 * 
	 * @param jdialog ventana.
	 * @return Barra de menús.
	 */
	protected JMenuBar retriveMenuBar(JDialog jdialog) {
		JMenuBar menuBar = null;
		if (jdialog.getRootPane().getClientProperty("jayatana.menubar") instanceof JMenuBar)
			menuBar = (JMenuBar)jdialog.getRootPane().getClientProperty("jayatana.menubar");
		else
			menuBar = jdialog.getJMenuBar();
		return menuBar;
	}
	
	/**
	 * Tratar de instalar siempre y cuando este en ejecución el bus del menú global.
	 * 
	 * @param window ventana.
	 * @param menubar barra de menús.
	 */
	private void tryInstallGlobalMenu(Window window, JMenuBar menubar) {
		FeatureManager.deployOnce(FeatureManager.FEATURE_GMAINLOOP);
		new SwingGlobalMenuWindow(window, menubar).tryInstall();
	}
}
