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

import java.awt.Component;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

/**
 * Clase para escuchar cambios de acceleradores de menus
 * @author Jared González
 */
final class AcceleratorsListener implements ContainerListener, PropertyChangeListener {
	private JMenuBar menubar;
	private Map<String, JMenuItem> acceleratorsmap;

	AcceleratorsListener(JMenuBar menubar, Map<String, JMenuItem> acceleratorsmap) {
		this.menubar = menubar;
		this.acceleratorsmap = acceleratorsmap;
		install();
	}
	private void install() {
		for (Component comp : menubar.getComponents())
			if (comp instanceof JMenu)
				addMenu((JMenu)comp);
	}
	void uninstall() {
		for (Component comp : menubar.getComponents())
			if (comp instanceof JMenu)
				removeMenu((JMenu)comp);
	}

	private void addMenu(JMenu menu) {
		for (Component comp : menu.getMenuComponents())
			if (comp instanceof JMenu)
				addMenu((JMenu)comp);
			else if (comp instanceof JMenuItem)
				addMenuItem((JMenuItem)comp);
		menu.addContainerListener(this);
	}
	private void removeMenu(JMenu menu) {
		for (Component comp : menu.getMenuComponents())
			if (comp instanceof JMenu)
				removeMenu((JMenu)comp);
			else if (comp instanceof JMenuItem)
				removeMenuItem((JMenuItem)comp);
		menu.removeContainerListener(this);
	}

	private void addMenuItem(JMenuItem menuitem) {
		if (menuitem.getAccelerator() != null) {
			KeyStroke stroke = menuitem.getAccelerator();
			acceleratorsmap.put(
					KeyEvent.getKeyModifiersText(stroke.getModifiers())+
					KeyEvent.getKeyText(stroke.getKeyCode()),
					menuitem);
		}
		menuitem.addPropertyChangeListener("accelerator", this);
	}
	private void removeMenuItem(JMenuItem menuitem) {
		if (menuitem.getAccelerator() != null) {
			KeyStroke stroke = menuitem.getAccelerator();
			acceleratorsmap.remove(
					KeyEvent.getKeyModifiersText(stroke.getModifiers())+
					KeyEvent.getKeyText(stroke.getKeyCode()));
		}
		menuitem.removePropertyChangeListener("accelerator", this);
	}

	@Override
	public void componentAdded(ContainerEvent e) {
		if (e.getSource() instanceof JMenu)
			addMenu((JMenu)e.getSource());
		else if (e.getSource() instanceof JMenuItem)
			addMenuItem((JMenuItem)e.getSource());
	}
	@Override
	public void componentRemoved(ContainerEvent e) {
		if (e.getSource() instanceof JMenu)
			removeMenu((JMenu)e.getSource());
		else if (e.getSource() instanceof JMenuItem)
			removeMenuItem((JMenuItem)e.getSource());
	}
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		KeyStroke aceleratorold = (KeyStroke)evt.getNewValue();
		KeyStroke aceleratornew = (KeyStroke)evt.getNewValue();
		if (aceleratorold != null)
			acceleratorsmap.remove(
					KeyEvent.getKeyModifiersText(aceleratorold.getModifiers())+
					KeyEvent.getKeyText(aceleratorold.getKeyCode()));
		if (aceleratornew != null)
			acceleratorsmap.put(
					KeyEvent.getKeyModifiersText(aceleratornew.getModifiers())+
					KeyEvent.getKeyText(aceleratornew.getKeyCode()),
					(JMenuItem)evt.getSource());
	}
}
