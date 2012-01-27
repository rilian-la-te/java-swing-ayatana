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

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;
import java.util.TreeMap;
import javax.swing.*;

/**
 * Clase que exporta una barra de menus a la barra de menus de aplicaciones globales
 * de Ayatana de Ubuntu
 * 
 * @author Jared González
 */
class ApplicationMenu implements WindowListener, ComponentListener, ContainerListener,
		PropertyChangeListener, AWTEventListener, ItemListener {
	native private static void initialize();
	native private static void uninitialize();
	
	private static boolean initializedApplicationMenu = false;
	private static void initializeApplicationMenu() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() { ApplicationMenu.uninitialize(); }
		});
		ApplicationMenu.initialize();
	}
	
	private JFrame frame;
	private JMenuBar menubar;
	private long menugenid = 0;
	private Map<Long, Object> menuobjectmap;
	private Map<String, Object> menusaceleratormap;
	private boolean tryinstalled = false;
	private long xid = -1;
	
	private static final int TOGGLE_TYPE_RADIO = 0;
	private static final int TOGGLE_TYPE_CHECK = 1;
	private static final int TOGGLE_STATE_CHECKED = 0;
	private static final int TOGGLE_STATE_UNCHECKED = 1;
	
	native private void registerWatcher(long xid);
	native private void unregisterWatcher(long xid);
	native private long getWindowXID(Window window);
	native private void addMenu(long xid, long pid, long mid);
	native private void addSeparator(long xid, long pid, long mid);
	native private void setMenuItemLabel(long xid, long mid, String label);
	native private void setMenuItemAccelerator(long xid, long mid, int modifiers, int keycode);
	native private void setMenuItemToggleType(long xid, long mid, int type);
	native private void setMenuItemToggleState(long xid, long mid, int state);
	native private void removeMenuItem(long xid, long mid);
	
	ApplicationMenu(JFrame frame, JMenuBar menubar) {
		this.frame = frame;
		this.menubar = menubar;
		this.menuobjectmap = new TreeMap<Long, Object>();
		this.menusaceleratormap = new TreeMap<String, Object>();
		this.frame.addWindowListener(this);
	}

	/*
	 * Control de instalación y desinstalacion
	 */
	private void tryInstall() {
		if (tryinstalled)
			return;
		LoadLibrary.load();
		if (!initializedApplicationMenu) {
			ApplicationMenu.initializeApplicationMenu();
			initializedApplicationMenu = true;
		}
		xid = this.getWindowXID(frame);
		this.registerWatcher(xid);
		tryinstalled = true;
	}
	private void tryUninstall() {
		if (!tryinstalled)
			return;
		frame.removeWindowListener(this);
		this.unregisterWatcher(xid);
		tryinstalled = false;
	}
	
	private void install() {
		Toolkit.getDefaultToolkit().addAWTEventListener(this, AWTEvent.KEY_EVENT_MASK);
		for (Component comp : menubar.getComponents())
			if (comp instanceof JMenu) {
				attachMenu(-1, (JMenu)comp);
			}
		menubar.setVisible(false);
	}
	private void uninstall() {
		Toolkit.getDefaultToolkit().removeAWTEventListener(this);
		for (Component comp : menubar.getComponents())
			if (comp instanceof JMenu) {
				unattachMenu((JMenu)comp);
			}
		menubar.setVisible(true);
	}
	private void itemActivated(long xid) {
		Object obj = menuobjectmap.get(xid);
		if (obj instanceof JMenuItem) {
			JMenuItem menuitem = (JMenuItem)obj;
			menuitem.getModel().setArmed(true);
			menuitem.getModel().setPressed(true);
			try {Thread.sleep(68);} catch (Exception e) {}
			menuitem.getModel().setPressed(false);
			menuitem.getModel().setArmed(false);
		}
	}
	private void itemAboutToShow(long xid) {
		Object obj = menuobjectmap.get(xid);
		if (obj instanceof JMenu) {
			JMenu menu = (JMenu)obj;
			menu.getModel().setArmed(true);
			menu.getModel().setPressed(true);
			try {Thread.sleep(68);} catch (Exception e) {}
			menu.getModel().setPressed(false);
			menu.getModel().setArmed(false);
		}
	}
	
	/*
	 * Control de integracion de menus
	 */
	private long getIndexOfObject(Object object) {
		for (Map.Entry<Long, Object> entry : menuobjectmap.entrySet())
			if (object.equals(entry.getValue())) {
				return entry.getKey();
			}
		return -1;
	}
	
	private void attachMenu(long pid, JMenu menu) {
		long mid = ++menugenid;
		menuobjectmap.put(mid, menu);
		this.addMenu(xid, pid, mid);
		this.setMenuItemLabel(xid, mid, menu.getText());
		menu.addContainerListener(this);
		menu.addPropertyChangeListener(this);
		for (Component comp : menu.getMenuComponents()) {
			if (comp instanceof JMenu)
				attachMenu(mid, (JMenu)comp);
			else if (comp instanceof JSeparator)
				attachSeparator(mid, (JSeparator)comp);
			else if (comp instanceof JMenuItem)
				attachMenuItem(mid, (JMenuItem)comp);
		}
	}
	private void attachMenuItem(long pid, JMenuItem menu) {
		long mid = ++menugenid;
		menuobjectmap.put(mid, menu);
		this.addMenu(xid, pid, mid);
		this.setMenuItemLabel(xid, mid, menu.getText());
		if (menu.getAccelerator() != null) {
			KeyStroke acelerator = menu.getAccelerator();
			menusaceleratormap.put(
					KeyEvent.getKeyModifiersText(acelerator.getModifiers())
					+KeyEvent.getKeyText(acelerator.getKeyCode()), menu);
			this.setMenuItemAccelerator(xid, mid,
					acelerator.getModifiers(), acelerator.getKeyCode());
		}
		if (menu instanceof JRadioButtonMenuItem) {
			this.setMenuItemToggleType(xid, mid, TOGGLE_TYPE_RADIO);
			this.setMenuItemToggleState(xid, mid, ((JRadioButtonMenuItem)menu).isSelected() ?
					TOGGLE_STATE_CHECKED : TOGGLE_STATE_UNCHECKED);
			menu.addItemListener(this);
		} else if (menu instanceof JCheckBoxMenuItem) {
			this.setMenuItemToggleType(xid, mid, TOGGLE_TYPE_CHECK);
			this.setMenuItemToggleState(xid, mid, ((JCheckBoxMenuItem)menu).isSelected() ?
					TOGGLE_STATE_CHECKED : TOGGLE_STATE_UNCHECKED);
			menu.addItemListener(this);
		}
		menu.addPropertyChangeListener(this);
	}
	private void attachSeparator(long pid, JSeparator sep) {
		long mid = ++menugenid;
		menuobjectmap.put(mid, sep);
		this.addSeparator(xid, pid, mid);
		sep.addPropertyChangeListener(this);
	}
	
	
	private void unattachMenu(JMenu menu) {
		menu.removeContainerListener(this);
		menu.removePropertyChangeListener(this);
		long mid = this.getIndexOfObject(menu);
		menuobjectmap.remove(mid);
		this.removeMenuItem(xid, mid);
		for (Component comp : menu.getMenuComponents()) {
			if (comp instanceof JMenu)
				unattachMenu((JMenu)comp);
			else if (comp instanceof JSeparator)
				unattachSeparator((JSeparator)comp);
			else if (comp instanceof JMenuItem)
				unattachMenuItem((JMenuItem)comp);
		}
	}
	private void unattachMenuItem(JMenuItem menu) {
		if (menu instanceof JRadioButtonMenuItem ||
				menu instanceof JCheckBoxMenuItem)
			menu.removePropertyChangeListener(this);
		menu.removeItemListener(this);
		long mid = this.getIndexOfObject(menu);
		menuobjectmap.remove(mid);
		if (menu.getAccelerator() != null) {
			KeyStroke acelerator = menu.getAccelerator();
			menusaceleratormap.remove(
					KeyEvent.getKeyModifiersText(acelerator.getModifiers())
					+KeyEvent.getKeyText(acelerator.getKeyCode()));
		}
		this.removeMenuItem(xid, mid);
	}
	private void unattachSeparator(JSeparator sep) {
		sep.removePropertyChangeListener(this);
		long mid = this.getIndexOfObject(sep);
		menuobjectmap.remove(mid);
		this.removeMenuItem(xid, mid);
	}
	
	/*
	 * Eventos de Objetos
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if ("accelerator".equals(evt.getPropertyName())) {
			KeyStroke aceleratorold = (KeyStroke)evt.getNewValue();
			KeyStroke aceleratornew = (KeyStroke)evt.getNewValue();
			if (aceleratorold != null) {
				menusaceleratormap.remove(
					KeyEvent.getKeyModifiersText(aceleratorold.getModifiers())
					+KeyEvent.getKeyText(aceleratorold.getKeyCode()));
			}
			if (aceleratornew != null) {
				menusaceleratormap.put(
					KeyEvent.getKeyModifiersText(aceleratornew.getModifiers())
					+KeyEvent.getKeyText(aceleratornew.getKeyCode()), evt.getSource());
			}
		}
	}
	
	private JFrame getFrame(Component comp) {
		if (comp == null)
			return null;
		else if (comp instanceof JFrame)
			return (JFrame)comp;
		else
			return getFrame(comp.getParent());
	}
	
	@Override
	public void eventDispatched(AWTEvent event) {
		if (event.getID() == KeyEvent.KEY_RELEASED) {
			KeyEvent e = (KeyEvent)event;
			if (e.getKeyCode() != KeyEvent.VK_ALT &&
						e.getKeyCode() != KeyEvent.VK_SHIFT &&
						e.getKeyCode() != KeyEvent.VK_CONTROL &&
						e.getKeyCode() != KeyEvent.VK_META &&
						e.getKeyCode() != KeyEvent.VK_ALT_GRAPH) {
				JFrame eventframe;
				if (event.getSource() instanceof Component)
					eventframe = this.getFrame((Component)event.getSource());
				else if (event.getSource() instanceof JFrame)
					eventframe = (JFrame)event.getSource();
				else 
					eventframe = null;
				if (frame.equals(eventframe) && frame.isActive()) {
					String aceleratorkey = KeyEvent.getKeyModifiersText(e.getModifiers())
							+ KeyEvent.getKeyText(e.getKeyCode());
					Object menu = menusaceleratormap.get(aceleratorkey);
					if (menu != null) {
						if (menu instanceof JMenuItem) {
							((JMenuItem)menu).doClick();
						}
					}
				}
			}
		}
	}
	
	@Override
	public void itemStateChanged(ItemEvent e) {
		if (e.getSource() instanceof JRadioButtonMenuItem ||
				e.getSource() instanceof JCheckBoxMenuItem) {
			long index = getIndexOfObject(e.getSource());
			if (index > -1) {
				this.setMenuItemToggleState(xid, index, e.getStateChange() == ItemEvent.SELECTED ?
						TOGGLE_STATE_CHECKED : TOGGLE_STATE_UNCHECKED);
			}
		}
	}
	@Override
	public void componentAdded(ContainerEvent e) {
		if (e.getChild() instanceof JMenu)
			this.attachMenu(this.getIndexOfObject(e.getContainer()), (JMenu)e.getChild());
		else if (e.getChild() instanceof JSeparator)
			this.attachSeparator(this.getIndexOfObject(e.getContainer()), (JSeparator)e.getChild());
		else if (e.getContainer() instanceof JMenu && e.getChild() instanceof JMenuItem)
			this.attachMenuItem(getIndexOfObject(e.getContainer()), (JMenuItem)e.getChild());
	}
	@Override
	public void componentRemoved(ContainerEvent e) {
		if (e.getChild() instanceof JMenu)
			this.unattachMenu((JMenu)e.getChild());
		else if (e.getChild() instanceof JSeparator)
			this.unattachSeparator((JSeparator)e.getChild());
		else if (e.getChild() instanceof JMenuItem)
			this.unattachMenuItem((JMenuItem)e.getChild());
	}
	
	@Override
	public void componentHidden(ComponentEvent e) {}
	@Override
	public void componentMoved(ComponentEvent e) {}
	@Override
	public void componentResized(ComponentEvent e) {}
	@Override
	public void componentShown(ComponentEvent e) {}
	
	@Override
	public void windowOpened(WindowEvent e) {
		tryInstall();
	}
	@Override
	public void windowClosing(WindowEvent e) {}
	@Override
	public void windowClosed(WindowEvent e) {
		tryUninstall();
	}
	@Override
	public void windowIconified(WindowEvent e) {}
	@Override
	public void windowDeiconified(WindowEvent e) {}
	@Override
	public void windowActivated(WindowEvent e) {
		tryInstall();
	}
	@Override
	public void windowDeactivated(WindowEvent e) {}
}
