package com.jarego.jayatana.swing;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.AWTEventListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.FocusManager;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import com.jarego.jayatana.basic.GlobalMenuAdapter;

public class SwingGlobalMenuWindow extends GlobalMenuAdapter implements WindowListener,
		AWTEventListener, ContainerListener, PropertyChangeListener, ComponentListener {
	private JMenuBar menubar;
	private boolean netbeansPlatform;
	
	public SwingGlobalMenuWindow(Window window, JMenuBar menubar) {
		super(window);
		this.menubar = menubar;
	}
	
	@Override
	protected void register() {
		try {
			EventQueue.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					// Correción para Netbeans
					netbeansPlatform = "org.openide.awt.MenuBar".equals(
							menubar.getClass().getName());
					// -----------------------
					for (Component comp : menubar.getComponents()) {
						if (comp instanceof JMenu) {
							((JMenu)comp).addPropertyChangeListener(SwingGlobalMenuWindow.this);
							((JMenu)comp).addComponentListener(SwingGlobalMenuWindow.this);
						}
					}
					menubar.addContainerListener(SwingGlobalMenuWindow.this);
					Toolkit.getDefaultToolkit().addAWTEventListener(
							SwingGlobalMenuWindow.this, KeyEvent.KEY_EVENT_MASK);
					((Window)getWindow()).addWindowListener(SwingGlobalMenuWindow.this);
					menubar.setVisible(false);
					createMenuBarMenus();
				}
			});
		} catch (Exception e) {
			Logger.getLogger(SwingGlobalMenuWindow.class.getName())
					.log(Level.WARNING, e.getMessage(), e);
		}
	}
	@Override
	protected void unregister() {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				((Window)getWindow()).removeWindowListener(SwingGlobalMenuWindow.this);
				Toolkit.getDefaultToolkit().removeAWTEventListener(SwingGlobalMenuWindow.this);
				for (Component comp : menubar.getComponents()) {
					if (comp instanceof JMenu) {
						((JMenu)comp).removePropertyChangeListener(SwingGlobalMenuWindow.this);
						((JMenu)comp).removeComponentListener(SwingGlobalMenuWindow.this);
					}
				}
				menubar.removeContainerListener(SwingGlobalMenuWindow.this);
				menubar.setVisible(true);
			}
		});
	}
	
	private void destroyMenuBarMenus() {
		removeAllMenus();
	}
	private void createMenuBarMenus() {
		for (Component comp : menubar.getComponents()) {
			if (comp instanceof JMenu) {
				JMenu menu = (JMenu)comp;
				if (menu.isVisible() && menu.getText() != null && !"".equals(menu.getText()))
					addMenu(null, menu);
			}
		}
	}
	
	private long approveRecreateMenuBarMenus = -1;
	private void recreateMenuBarMenus() {
		if (approveRecreateMenuBarMenus == -1) {
			approveRecreateMenuBarMenus = System.currentTimeMillis() + 300;
			new Thread() {
				@Override
				public void run() {
					try {
						while (System.currentTimeMillis() < approveRecreateMenuBarMenus)
							Thread.sleep(100);
					} catch (InterruptedException e) {
						Logger.getLogger(SwingGlobalMenuWindow.class.getName()).log(
								Level.WARNING, "Can't wait approve rebuild", e);
					} finally {
						destroyMenuBarMenus();
						createMenuBarMenus();
						approveRecreateMenuBarMenus = -1;
					}
				}
			}.start();
		} else {
			approveRecreateMenuBarMenus = System.currentTimeMillis() + 300;
		}
	}
	
	private void addMenu(JMenu parent, JMenu menu) {
		if (parent == null)
			addMenu(menu.hashCode(), menu.getText(), menu.isEnabled(), menu.isVisible());
		else if (menu.isVisible())
			addMenu(parent.hashCode(), menu.hashCode(), menu.getText(), menu.isEnabled(), true);
	}
	
	private void addMenuItem(JMenu parent, JMenuItem menuitem) {
		if (menuitem.getText() == null || "".equals(menuitem.getText()))
			return;
		int modifiers = -1;
		int keycode = -1;
		if (menuitem.getAccelerator() != null) {
			modifiers = menuitem.getAccelerator().getModifiers();
			keycode = menuitem.getAccelerator().getKeyCode();
		}
		if (menuitem instanceof JRadioButtonMenuItem) {
			addMenuItemRadio(parent.hashCode(), menuitem.hashCode(),
					menuitem.getText(), menuitem.isEnabled(), modifiers,
					keycode, menuitem.isSelected());
		} else if (menuitem instanceof JCheckBoxMenuItem) {
			addMenuItemCheck(parent.hashCode(), menuitem.hashCode(),
					menuitem.getText(), menuitem.isEnabled(), modifiers,
					keycode, menuitem.isSelected());
		} else {
			addMenuItem(parent.hashCode(), menuitem.hashCode(), menuitem.getText(),
					menuitem.isEnabled(), modifiers, keycode);
		}
	}
	
	private JMenuItem getJMenuItem(int hashcode) {
		for (Component comp : menubar.getComponents())
			if (comp instanceof JMenuItem) {
				JMenuItem item;
				if ((item = getJMenuItem((JMenuItem) comp, hashcode)) != null)
					return item;
			}
		return null;
	}
	private JMenuItem getJMenuItem(JMenuItem menu, int hashcode) {
		if (menu.hashCode() == hashcode) {
			return menu;
		} else if (menu instanceof JMenu) {
			for (Component comp : ((JMenu) menu).getMenuComponents())
				if (comp instanceof JMenuItem) {
					JMenuItem item;
					if ((item = getJMenuItem((JMenuItem) comp, hashcode)) != null)
						return item;
				}
		}
		return null;
	}
	
	@Override
	protected synchronized void menuActivated(int menuId) {
		final JMenuItem menuitem = getJMenuItem(menuId);
		if (menuitem != null && menuitem.isEnabled() & menuitem.isVisible()) {
			EventQueue.invokeLater(new Runnable() {
				@Override
				public void run() {
					menuitem.getModel().setArmed(true);
					menuitem.getModel().setPressed(true);
					menuitem.getModel().setPressed(false);
					menuitem.getModel().setArmed(false);
				}
			});
		}
	}
	
	// Correción para Netbeans
	private void menuAboutToShowForNetbeansPlatform(JMenu menu) {
		if ("org.openide.awt.MenuBar$LazyMenu".equals(menu.getClass().getName())) {
			try {
				Method methodDoInitialize = menu.getClass().getDeclaredMethod(
						"doInitialize", new Class<?>[] {});
				if (!methodDoInitialize.isAccessible())
					methodDoInitialize.setAccessible(true);
				
				Field fieldDynaModel = menu.getClass().getDeclaredField("dynaModel");
				if (!fieldDynaModel.isAccessible())
						fieldDynaModel.setAccessible(true);

				Class<?> classDynaModel = fieldDynaModel.getType();
				Method methodDynaModel = classDynaModel.getMethod("checkSubmenu",
						new Class<?>[] {JMenu.class});
				if (!methodDynaModel.isAccessible())
						methodDynaModel.setAccessible(true);

				methodDoInitialize.invoke(menu, new Object[] {});
				Object objectDynaModel = fieldDynaModel.get(menu);
				methodDynaModel.invoke(objectDynaModel, menu);
			} catch (Exception e) {
				Logger.getLogger(SwingGlobalMenuWindow.class.getName())
						.log(Level.SEVERE, e.getMessage(), e);
			}
		}
		
		for (Class<?> clsInterface : menu.getClass().getInterfaces()) {
			if ("org.openide.awt.DynamicMenuContent".equals(clsInterface.getName())) {
				try {
					Method methodSynchMenu = clsInterface.getDeclaredMethod(
							"synchMenuPresenters", new Class<?>[] {JComponent[].class});
					methodSynchMenu.invoke(menu, new Object[] {null});
				} catch (Exception e) {
					Logger.getLogger(SwingGlobalMenuWindow.class.getName())
							.log(Level.SEVERE, e.getMessage(), e);
				}
				break;
			}
		}
	}
	// ----------------------
	
	@Override
	protected synchronized void menuAboutToShow(int menuId) {
		final JMenu menu = (JMenu)getJMenuItem(menuId);
		if (menu != null && menu.isVisible()) {
			try {
				EventQueue.invokeAndWait(new Runnable() {
					@Override
					public void run() {
						int items = 0;
						if (menu.isEnabled()) {
							menu.getModel().setSelected(true);
							
							JPopupMenu popupMenu = menu.getPopupMenu();
							PopupMenuEvent pevent = new PopupMenuEvent(popupMenu);
							for (PopupMenuListener pl : popupMenu.getPopupMenuListeners())
								if (pl != null) pl.popupMenuWillBecomeVisible(pevent);
							
							// Correción para Netbeans
							if (netbeansPlatform)
								menuAboutToShowForNetbeansPlatform(menu);
							// -----------------------
							
							for (Component comp : popupMenu.getComponents()) {
								if (comp.isVisible()) {
									if (comp instanceof JMenu) {
										addMenu(menu, (JMenu)comp);
										items++;
									} else if (comp instanceof JMenuItem) {
										addMenuItem(menu, (JMenuItem)comp);
										items++;
									} else if (comp instanceof JSeparator)
										addSeparator(menu.hashCode());
								}
							}
						}
						if (items == 0) addMenuItem(
								menu.hashCode(), -1, "(...)", false, -1, -1);
					}
				});
			} catch (Exception e) {
				Logger.getLogger(SwingGlobalMenuWindow.class.getName())
					.log(Level.SEVERE, e.getMessage(), e);
			}
		}
	}
	@Override
	protected synchronized void menuAfterClose(int menuId) {
		final JMenu menu = (JMenu)getJMenuItem(menuId);
		if (menu != null && menu.isEnabled() && menu.isVisible()) {
			try {
				EventQueue.invokeAndWait(new Runnable() {
					@Override
					public void run() {
						JPopupMenu popupMenu = menu.getPopupMenu();
						if (popupMenu != null) {
							PopupMenuEvent pevent = new PopupMenuEvent(popupMenu);
							for (PopupMenuListener pl : popupMenu.getPopupMenuListeners())
								if (pl != null) pl.popupMenuWillBecomeInvisible(pevent);
						}
						menu.getModel().setSelected(false);
					}
				});
			} catch (Exception e) {
				Logger.getLogger(SwingGlobalMenuWindow.class.getName())
					.log(Level.WARNING, e.getMessage(), e);
			}
		}
	}

	@Override
	public void windowOpened(WindowEvent e) {}
	@Override
	public void windowClosing(WindowEvent e) {
		unregisterWatcher();
	}
	@Override
	public void windowClosed(WindowEvent e) {}
	@Override
	public void windowIconified(WindowEvent e) {}
	@Override
	public void windowDeiconified(WindowEvent e) {}
	@Override
	public void windowActivated(WindowEvent e) {}
	@Override
	public void windowDeactivated(WindowEvent e) {}
	
	private Window getWindow(Component comp) {
		if (comp == null)
			return null;
		else if (comp instanceof JFrame)
			return (Window) comp;
		else if (comp instanceof JDialog)
			return (Window) comp;
		else
			return getWindow(comp.getParent());
	}
	
	@Override
	public void eventDispatched(AWTEvent event) {
		KeyEvent e = (KeyEvent) event;
		if (e.getID() == KeyEvent.KEY_PRESSED && !e.isConsumed()) {
			if (e.getKeyCode() != KeyEvent.VK_ALT
					&& e.getKeyCode() != KeyEvent.VK_SHIFT
					&& e.getKeyCode() != KeyEvent.VK_CONTROL
					&& e.getKeyCode() != KeyEvent.VK_META
					&& e.getKeyCode() != KeyEvent.VK_ALT_GRAPH) {
				if (getWindow((Component)e.getSource()) == getWindow()) {
					try {
						KeyStroke acelerator = KeyStroke.getKeyStroke(e.getKeyCode(), e.getModifiers());
						if (FocusManager.getCurrentManager().getFocusOwner() instanceof JComponent) {
							JComponent jcomp = (JComponent)FocusManager.getCurrentManager().getFocusOwner();
							if (jcomp.getActionForKeyStroke(acelerator) == null) {
								Method methodProcessKeyBinding = JMenuBar.class.getDeclaredMethod(
									"processKeyBinding", new Class<?>[] {
										KeyStroke.class, KeyEvent.class, int.class, boolean.class
								});
								if (!methodProcessKeyBinding.isAccessible())
									methodProcessKeyBinding.setAccessible(true);
								Object result = methodProcessKeyBinding.invoke(menubar, new Object[] {
									KeyStroke.getKeyStroke(e.getKeyCode(), e.getModifiers()), e, 	
									JComponent.WHEN_IN_FOCUSED_WINDOW, true
								});
								if (Boolean.TRUE.equals(result))
									e.consume();
							}
						}
					} catch (Exception err) {
						Logger.getLogger(SwingGlobalMenuWindow.class.getName())
								.log(Level.WARNING, err.getMessage(), err);
					}
				}
			}
		}
	}

	@Override
	public void componentAdded(ContainerEvent e) {
		if (e.getChild() instanceof JMenu) {
			((JMenu)e.getChild()).addPropertyChangeListener(this);
			((JMenu)e.getChild()).addComponentListener(this);
			recreateMenuBarMenus();
		}
	}
	@Override
	public void componentRemoved(ContainerEvent e) {
		if (e.getChild() instanceof JMenu) {
			((JMenu)e.getChild()).removePropertyChangeListener(this);
			((JMenu)e.getChild()).removeComponentListener(this);
			recreateMenuBarMenus();
		}
	}
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if ("enabled".equals(evt.getPropertyName())) {
			JMenu menu = (JMenu)evt.getSource();
			updateMenu(menu.hashCode(), menu.getText(), menu.isEnabled(), menu.isVisible());
		}
	}
	@Override
	public void componentHidden(ComponentEvent e) {
		if (e.getSource() instanceof JMenu) {
			JMenu menu = (JMenu)e.getSource();
			updateMenu(menu.hashCode(), menu.getText(), menu.isEnabled(), menu.isVisible());
		}
	}
	@Override
	public void componentShown(ComponentEvent e) {
		if (e.getSource() instanceof JMenu) {
			JMenu menu = (JMenu)e.getSource();
			updateMenu(menu.hashCode(), menu.getText(), menu.isEnabled(), menu.isVisible());
		}
	}
	@Override
	public void componentMoved(ComponentEvent e) {}
	@Override
	public void componentResized(ComponentEvent e) {}
}
