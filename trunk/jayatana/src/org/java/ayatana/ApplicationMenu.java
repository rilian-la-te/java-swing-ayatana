/*
 * Copyright (c) 2012 Jared González.
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

import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

/**
 * Clase para instalar el menu de aplicaciones globales del
 * escritorio Ayatana de Ubuntu
 * 
 * @author Jared González
 */
final public class ApplicationMenu implements WindowListener, AWTEventListener, ContainerListener {
	/**
	 * Trata de instalar el menu de aplicaciones globales, si no es posible
	 * por incompatibilidad del sistema operativo o esta des habilitado el
	 * menu global retornara <code>False</code>.
	 * 
	 * @param frame Ventana que contiene la barra de menus
	 * @return retorna <code>True</code> si se puede instalar el menu global
	 * de lo contrario retorna <code>False</code>.
	 */
	public static boolean tryInstall(JFrame frame) {
		return ApplicationMenu.tryInstall(frame, frame.getJMenuBar(), new ExtraMenuActionDefault());
	}
	/**
	 * Trata de instalar el menu de aplicaciones globales, si no es posible
	 * por incompatibilidad del sistema operativo o esta des habilitado el
	 * menu global retornara <code>False</code>.
	 * 
	 * @param frame Ventana que contiene la barra de menus
	 * @param menubar La barra de menus en caso de que este contenida entro panel
	 * @return retorna <code>True</code> si se puede instalar el menu global
	 * de lo contrario retorna <code>False</code>.
	 */
	public static boolean tryInstall(JFrame frame, JMenuBar menubar) {
		return ApplicationMenu.tryInstall(frame, menubar, new ExtraMenuActionDefault());
	}
	/**
	 * Trata de instalar el menu de aplicaciones globales, si no es posible
	 * por incompatibilidad del sistema operativo o esta des habilitado el
	 * menu global retornara <code>False</code>.
	 * 
	 * @param frame Ventana que contiene la barra de menus
	 * @param additionalMenuAction interface para acciones adicionales
	 * @return retorna <code>True</code> si se puede instalar el menu global
	 * de lo contrario retorna <code>False</code>.
	 */
	public static boolean tryInstall(JFrame frame, ExtraMenuAction additionalMenuAction) {
		return ApplicationMenu.tryInstall(frame, frame.getJMenuBar(), additionalMenuAction);
	}
	/**
	 * Trata de instalar el menu de aplicaciones globales, si no es posible
	 * por incompatibilidad del sistema operativo o esta des habilitado el
	 * menu global retornara <code>False</code>.
	 * 
	 * @param frame Ventana que contiene la barra de menus
	 * @param menubar La barra de menus en caso de que este contenida entro panel
	 * @param additionalMenuAction interface para acciones adicionales
	 * @return retorna <code>True</code> si se puede instalar el menu global
	 * de lo contrario retorna <code>False</code>.
	 */
	public static boolean tryInstall(JFrame frame, JMenuBar menubar, ExtraMenuAction additionalMenuAction) {
		if (frame == null || menubar == null)
			throw new NullPointerException();
		if (!"libappmenu.so".equals(System.getenv("UBUNTU_MENUPROXY")))
			return false;
		if (AyatanaLibrary.load()) {
			new ApplicationMenu(frame, menubar, additionalMenuAction);
			return true;
		} else {
			return false;
		}
	}
	
	
	/**
	 * Inicializa el proceso de GMailLoop y cración de estrucutras de
	 * control de ventanas
	 */
	private static native void nativeInitialize();
	/**
	 * Deteiene el proceso de GMainLoop y destruye las estrucutras de
	 * control de ventanas
	 */
	private static native void nativeUninitialize();
	
	private static boolean initialized = false;
	
	/**
	 * Inicializa el ApplicationMenu para iniciar con la integración con Ayatana
	 * de Ubuntu
	 */
	private static void initialize() {
		if (!initialized) {
			ApplicationMenu.nativeInitialize();
			Thread shutdownThread = new Thread() {
				@Override
				public void run() {
					ApplicationMenu.nativeUninitialize();
				}
			};
			Runtime.getRuntime().addShutdownHook(shutdownThread);
			initialized = true;
		}
	}
	
	private JFrame frame;
	private JMenuBar menubar;
	private boolean tryInstalled = false;
	private Map<String, JMenuItem> acceleratorsmap;
	private AcceleratorsListener acceleratorsListener;
	private ExtraMenuAction extraMenuAction;
	private long windowxid;
	
	/**
	 * Obtiene el identificador de ventana del sistema ventanas X11
	 * 
	 * @param window ventana java
	 * @return identificador de ventana
	 */
	native private long getWindowXID(Window window);
	/**
	 * Registrar el observador de menu de aplicaciones para la ventana
	 * 
	 * @param windowxid identificador de ventana
	 */
	native private void registerWatcher(long windowxid);
	/**
	 * Desregistrar el observador de menu de aplicaciones para la ventana
	 * 
	 * @param windowxid identificador de ventana
	 */
	native private void unregisterWatcher(long windowxid);
	
	/**
	 * Crea un menu en el menu de aplicaciones globales
	 * 
	 * @param menu menu
	 */
	private void addMenu(JMenu menu) {
		if (menu.getText() == null || "".equals(menu.getText()))
			return;
		this.addMenu(windowxid, menu.hashCode(), menu.getText(), menu.isEnabled());
	}
	/**
	 * Crear un menu en el menu de aplicaciones globales
	 * 
	 * @param windowxid identificador de ventana
	 * @param hashcode identificador de menu
	 * @param label texto de menu
	 */
	native private void addMenu(long windowxid, int hashcode, String label, boolean enabled);
	
	/**
	 * Elimina un menu del menu de aplicaciones globales
	 * 
	 * @param menu menu
	 */
	private void removeAllMenus() {
		this.removeAllMenus(windowxid);
	}
	/**
	 * Elimina un menu del menu de aplicaciones globales
	 * 
	 * @param windowxid identificador de ventana
	 * @param hashcode identificador de menu
	 */
	native private void removeAllMenus(long windowxid);
	/**
	 * Crea un menu en el menu de aplicaciones globales
	 * 
	 * @param menuitem menu
	 */
	private void addMenuItem(JMenuItem menuitem) {
		if (menuitem.getText() == null || "".equals(menuitem.getText()))
			return;
		int modifiers = -1;
		int keycode = -1;
		if (menuitem.getAccelerator() != null) {
			modifiers = menuitem.getAccelerator().getModifiers();
			keycode = menuitem.getAccelerator().getKeyCode();
		}
		if (menuitem instanceof JMenu) {
			this.addMenu((JMenu)menuitem);
		} else if (menuitem instanceof JRadioButtonMenuItem) {
			this.addMenuItemRadio(windowxid, menuitem.hashCode(),
					menuitem.getText(), menuitem.isEnabled(), modifiers, keycode,
					menuitem.isSelected());
		} else if (menuitem instanceof JCheckBoxMenuItem) {
			this.addMenuItemCheck(windowxid, menuitem.hashCode(),
					menuitem.getText(), menuitem.isEnabled(), modifiers, keycode,
					menuitem.isSelected());
		} else {
			this.addMenuItem(windowxid, menuitem.hashCode(),
					menuitem.getText(), menuitem.isEnabled(), modifiers, keycode);
		}
	}
	/**
	 * Agrega un menu basico
	 * 
	 * @param windowxid identificador de ventana
	 * @param hashcode identificador de menu
	 * @param label etiqueta
	 * @param enabled habilitado
	 * @param modifiers modificador de accelerador
	 * @param keycode codigo de accelerador
	 */
	native private void addMenuItem(long windowxid, int hashcode, String label, boolean enabled, int modifiers, int keycode);
	/**
	 * Agrega un menu RADIO
	 * 
	 * @param windowxid identificador de ventana
	 * @param hashcode identificador de menu
	 * @param label etiqueta
	 * @param enabled habilitado
	 * @param modifiers modificador de acelerador
	 * @param keycode codigo de accelerador
	 * @param selected estado de selección
	 */
	native private void addMenuItemRadio(long windowxid, int hashcode, String label, boolean enabled, int modifiers, int keycode, boolean selected);
	/**
	 * Agrega un menu CHECK
	 * 
	 * @param windowxid identificador de ventana
	 * @param hashcode identificador de menu
	 * @param label etiqueta
	 * @param enabled habilitado
	 * @param modifiers modificador de acelerador
	 * @param keycode codigo de accelerador
	 * @param selected estado de selección
	 */
	native private void addMenuItemCheck(long windowxid, int hashcode, String label, boolean enabled, int modifiers, int keycode, boolean selected);
	/**
	 * Agrega un separador
	 */
	private void addSeparator() {
		this.addMenuItemSeparator(windowxid);
	}
	/**
	 * Agrega un separador
	 * 
	 * @param windowxid identificador de ventana
	 */
	native private void addMenuItemSeparator(long windowxid);
	
	/**
	 * Contructor de integración de Application Menu
	 * 
	 * @param frame
	 * @param menubar 
	 */
	private ApplicationMenu(JFrame frame, JMenuBar menubar, ExtraMenuAction additionalMenuAction) {
		this.frame = frame;
		this.menubar = menubar;
		this.extraMenuAction = additionalMenuAction;
		frame.addWindowListener(this);
		if (frame.isActive())
			this.tryInstall();
	}
	
	/**
	 * Tratar de instalar el applicationmenu, ya que depende que
	 * este el servicio de applicationmenu
	 */
	private synchronized void tryInstall() {
		if (tryInstalled)
			return;
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				ApplicationMenu.initialize();
				windowxid = getWindowXID(frame);
				registerWatcher(windowxid);
			}
		});
		tryInstalled = true;
	}
	/**
	 * Tratar de desinstalar el applicationmenu, ya que depende que
	 * este el servicio de applicationmenu
	 */
	private synchronized void tryUninstall() {
		if (!tryInstalled)
			return;
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				unregisterWatcher(windowxid);
				frame.removeWindowListener(ApplicationMenu.this);
			}
		});
		tryInstalled = false;
	}
	
	/**
	 * Este método es invocado por la interface nativa en caso de que
	 * existe un applicationmenu registrado
	 */
	private void install() {
		acceleratorsmap = new TreeMap<String, JMenuItem>();
		acceleratorsListener = new AcceleratorsListener(
				menubar, acceleratorsmap);
		Toolkit.getDefaultToolkit()
				.addAWTEventListener(ApplicationMenu.this, AWTEvent.KEY_EVENT_MASK);
		for (Component comp : menubar.getComponents())
			if (comp instanceof JMenu && comp.isVisible())
				addMenu((JMenu)comp);
		menubar.setVisible(false);
		//menubar.addContainerListener(ApplicationMenu.this);
	}
	/**
	 * Este método es invocado por la interface nativa en caso de que se
	 * deshabilite al applicationmenu registrado
	 */
	private void uninstall() {
		//menubar.removeContainerListener(ApplicationMenu.this);
		menubar.setVisible(true);
		Toolkit.getDefaultToolkit()
				.removeAWTEventListener(ApplicationMenu.this);
		acceleratorsListener.uninstall();
		acceleratorsmap.clear();
	}
	
	/**
	 * Obtene un menu del hashcode
	 * 
	 * @param hashcode identificador
	 * @return menu
	 */
	private JMenuItem getJMenuItem(int hashcode) {
		for (Component comp : menubar.getComponents())
			if (comp instanceof JMenuItem) {
				JMenuItem item;
				if ((item = getJMenuItem((JMenuItem)comp, hashcode)) != null)
					return item;
			}
		return null;
	}
	/**
	 * Obtene un menu del hashcode
	 * 
	 * @param menu menu padre
	 * @param hashcode identificador
	 * @return menu
	 */
	private JMenuItem getJMenuItem(JMenuItem menu, int hashcode) {
		if (menu.hashCode() == hashcode) {
			return menu;
		} else if (menu instanceof JMenu) {
			for (Component comp : ((JMenu)menu).getMenuComponents())
				if (comp instanceof JMenuItem) {
					JMenuItem item;
					if ((item = getJMenuItem((JMenuItem)comp, hashcode)) != null)
						return item;
				}
		}
		return null;
	}
	/**
	 * Invoca el evento de menu basado en el identificador de menu
	 * 
	 * @param hashcode identificador de menu
	 */
	private void itemActivated(int hashcode) {
		this.invokeMenuItem(this.getJMenuItem(hashcode));
	}
	/**
	 * Invoca el evento de menu antes de mostrarse
	 * 
	 * @param hashcode identificador de menu
	 */
	private void itemAboutToShow(int hashcode) {
		this.invokeSelectMenu((JMenu)this.getJMenuItem(hashcode));
	}
	/**
	 * Invoca el evento de menu después de mostrarse
	 * @param hashcode 
	 */
	private void itemAfterShow(int hashcode) {
		this.invokeDeselectMenu((JMenu)this.getJMenuItem(hashcode));
	}
	
	/**
	 * Invoca la accion de un elemento de menu
	 * 
	 * @param menuitem menu
	 */
	private void invokeMenuItem(final JMenuItem menuitem) {
		if (menuitem != null)
			if (menuitem.isEnabled() && menuitem.isVisible()) {
				EventQueue.invokeLater(new Runnable() {
					@Override
					public void run() {
						boolean enabled = true;
		
						if (extraMenuAction != null)
							enabled = extraMenuAction.invokeMenu(frame, menubar, menuitem, true);

						if (enabled) {
							menuitem.getModel().setArmed(true);
							menuitem.getModel().setPressed(true);
							menuitem.getModel().setPressed(false);
							menuitem.getModel().setArmed(false);
						}
					}
				});
			}
	}
	
	/**
	 * Invokar menu
	 * 
	 * @param menu 
	 */
	private void invokeSelectMenu(final JMenu menu) {
		if (menu != null) 
			if (menu.isEnabled() && menu.isVisible()) {
				try {
					EventQueue.invokeAndWait(new Runnable() {
						@Override
						public void run() {
							boolean enabled = true;
		
							if (extraMenuAction != null)
								enabled = extraMenuAction.invokeMenu(frame, menubar, menu, true);

							if (enabled) {
								JPopupMenu popupMenu = menu.getPopupMenu();
								
								menu.getModel().setSelected(true);

								PopupMenuEvent pevent = new PopupMenuEvent(popupMenu);
								for (PopupMenuListener pl : menu.getPopupMenu().getPopupMenuListeners())
									if (pl != null) pl.popupMenuWillBecomeVisible(pevent);
								
								for (Component comp : popupMenu.getComponents()) {
									if (comp instanceof JMenu)
										ApplicationMenu.this.addMenu((JMenu)comp);
									else if (comp instanceof JMenuItem)
										ApplicationMenu.this.addMenuItem((JMenuItem)comp);
									else if (comp instanceof JSeparator)
										ApplicationMenu.this.addSeparator();
								}
							}
						}
					});
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				} catch (InvocationTargetException e) {
					throw new RuntimeException(e);
				}
			}
	}
	
	private void invokeDeselectMenu(final JMenu menu) {
		if (menu != null) 
			if (menu.isEnabled() && menu.isVisible()) {
				EventQueue.invokeLater(new Runnable() {
					@Override
					public void run() {
						boolean enabled = true;

						if (extraMenuAction != null)
							enabled = extraMenuAction.invokeMenu(frame, menubar, menu, false);

						if (enabled) {
							PopupMenuEvent pevent = new PopupMenuEvent(menu.getPopupMenu());
							for (PopupMenuListener pl : menu.getPopupMenu().getPopupMenuListeners())
								if (pl != null) pl.popupMenuWillBecomeInvisible(pevent);

							menu.getModel().setSelected(false);
						}
					}
				});
			}
	}
	
	/**
	 * Invokar eventos de acceleradores de menus
	 * 
	 * @param modifiers
	 * @param keycode 
	 */
	private void invokeAccelerator(int modifiers, int keycode) {
		JMenuItem menuitem = acceleratorsmap.get(
				KeyEvent.getKeyModifiersText(modifiers)+
				KeyEvent.getKeyText(keycode));
		this.invokeMenuItem(menuitem);
	}
	
	/**
	 * Buscar el JFrame contenedor
	 * 
	 * @param comp componente
	 * @return JFrame contenedor
	 */
	private JFrame getFrame(Component comp) {
		if (comp == null)
			return null;
		else if (comp instanceof JFrame)
			return (JFrame)comp;
		else
			return this.getFrame(comp.getParent());
	}
	/*
	 * Control de eventos de acceleradores
	 */
	@Override
	public void eventDispatched(AWTEvent event) {
		if (event.getID() == KeyEvent.KEY_RELEASED) {
			KeyEvent e = (KeyEvent)event;
			if (e.getKeyCode() != KeyEvent.VK_ALT &&
					e.getKeyCode() != KeyEvent.VK_SHIFT &&
					e.getKeyCode() != KeyEvent.VK_CONTROL &&
					e.getKeyCode() != KeyEvent.VK_META &&
					e.getKeyCode() != KeyEvent.VK_ALT_GRAPH &&
					frame.isActive()) {
				JFrame currentframe;
				if (event.getSource() instanceof Component)
					currentframe = this.getFrame((Component)event.getSource());
				else if (event.getSource() instanceof JFrame)
					currentframe = (JFrame)event.getSource();
				else 
					currentframe = null;
				if (frame.equals(currentframe))
					this.invokeAccelerator(e.getModifiers(), e.getKeyCode());
			}
		}
	}
	
	/*
	 * Eventos sobre la barra de menus
	 */
	@Override
	public void componentAdded(ContainerEvent e) {
		for (Component comp : menubar.getComponents())
			if (comp instanceof JMenu && comp.isVisible())
				this.addMenu((JMenu)comp);
		Logger.getLogger(ApplicationMenu.class.getName())
				.log(Level.INFO, "ADDMENU: " + e.getChild());
	}
	@Override
	public void componentRemoved(ContainerEvent e) {
		this.removeAllMenus();
		Logger.getLogger(ApplicationMenu.class.getName())
				.log(Level.INFO, "REMOVE: " + e.getChild());
	}
	
	/*
	 * Controles de evento de ventana
	 */
	@Override
	public void windowActivated(WindowEvent e) { tryInstall(); }
	@Override
	public void windowClosed(WindowEvent e) { tryUninstall(); }
	@Override
	public void windowClosing(WindowEvent e) {}
	@Override
	public void windowDeactivated(WindowEvent e) {}
	@Override
	public void windowDeiconified(WindowEvent e) {}
	@Override
	public void windowIconified(WindowEvent e) {}
	@Override
	public void windowOpened(WindowEvent e) { tryInstall(); }
}