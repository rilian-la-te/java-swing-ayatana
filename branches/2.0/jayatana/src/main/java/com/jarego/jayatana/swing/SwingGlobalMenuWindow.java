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
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

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

import com.jarego.jayatana.FeatureManager;
import com.jarego.jayatana.basic.GlobalMenu;

public class SwingGlobalMenuWindow extends GlobalMenu implements WindowListener, AWTEventListener,
		ContainerListener, PropertyChangeListener, ComponentListener {
	private long windowXID;
	private Window window;
	private JMenuBar menubar;
	private SwingGlobalMenuStack menuStack;
	
	public SwingGlobalMenuWindow(Window window, JMenuBar menubar) {
		this.window = window;
		this.menubar = menubar;
		menuStack = new SwingGlobalMenuStack();
	}
	
	public void tryInstall() {
		window.addWindowListener(this);
		FeatureManager.deployOnce(FeatureManager.FEATURE_GMAINLOOP);
		registerWatcher(windowXID = GlobalMenu.getWindowXID(window));
	}
	
	@Override
	protected void register() {
		try {
			EventQueue.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					for (int i=0;i<menubar.getMenuCount();i++) {
						menubar.getMenu(i).addPropertyChangeListener(SwingGlobalMenuWindow.this);
						menubar.getMenu(i).addComponentListener(SwingGlobalMenuWindow.this);
					}
					menubar.addContainerListener(SwingGlobalMenuWindow.this);
					Toolkit.getDefaultToolkit().addAWTEventListener(SwingGlobalMenuWindow.this, KeyEvent.KEY_EVENT_MASK);
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
				Toolkit.getDefaultToolkit().removeAWTEventListener(SwingGlobalMenuWindow.this);
				for (int i=0;i<menubar.getMenuCount();i++) {
					menubar.getMenu(i).removePropertyChangeListener(SwingGlobalMenuWindow.this);
					menubar.getMenu(i).removeComponentListener(SwingGlobalMenuWindow.this);
				}
				menubar.removeContainerListener(SwingGlobalMenuWindow.this);
				menubar.setVisible(true);
			}
		});
	}
	
	private void destroyMenuBarMenus() {
		removeAllMenus(windowXID);
	}
	private void createMenuBarMenus() {
		for (int i=0;i<menubar.getMenuCount();i++) {
			JMenu menu = menubar.getMenu(i);
			if (menu.isVisible() && menu.getText() != null && !"".equals(menu.getText()))
				addMenu(null, menubar.getMenu(i));
		}
	}
	private long approveRecreateMenuBarMenus = -1;
	private void recreateMenuBarMenus() {
		if (approveRecreateMenuBarMenus == -1) {
			approveRecreateMenuBarMenus = System.currentTimeMillis() + 500;
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
						menuStack.removeAll();
						destroyMenuBarMenus();
						createMenuBarMenus();
						approveRecreateMenuBarMenus = -1;
					}
				}
			}.start();
		} else {
			approveRecreateMenuBarMenus = System.currentTimeMillis() + 500;
		}
	}
	
	private void addMenu(JMenu parent, JMenu menu) {
		addMenu(windowXID, menu.hashCode(), menu.getText(), menu.isEnabled());
		menuStack.put(parent, menu);
	}
	private void addMenuItem(JMenu menu, JMenuItem menuitem) {
		if (menuitem.getText() == null || "".equals(menuitem.getText()))
			return;
		int modifiers = -1;
		int keycode = -1;
		if (menuitem.getAccelerator() != null) {
			modifiers = menuitem.getAccelerator().getModifiers();
			keycode = menuitem.getAccelerator().getKeyCode();
		}
		if (menuitem instanceof JRadioButtonMenuItem) {
			addMenuItemRadio(windowXID, menuitem.hashCode(),
					menuitem.getText(), menuitem.isEnabled(), modifiers,
					keycode, menuitem.isSelected());
			menuStack.put(menu, menuitem);
		} else if (menuitem instanceof JCheckBoxMenuItem) {
			addMenuItemCheck(windowXID, menuitem.hashCode(),
					menuitem.getText(), menuitem.isEnabled(), modifiers,
					keycode, menuitem.isSelected());
			menuStack.put(menu, menuitem);
		} else {
			addMenuItem(windowXID, menuitem.hashCode(), menuitem.getText(),
					menuitem.isEnabled(), modifiers, keycode);
			menuStack.put(menu, menuitem);
		}
	}
	
	@Override
	protected void menuActivated(int menuId) {
		final JMenuItem menuitem = menuStack.findMenuItem(menuId);
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
	@Override
	protected void menuAboutToShow(int menuId) {
		final SwingGlobalMenuStack.MenuStackEntry mse = menuStack.removeMenu(menuId);
		if (mse != null) {
			final JMenu menu = mse.getMenu();
			if (menu != null && menu.isEnabled() && menu.isVisible()) {
				try {
					EventQueue.invokeAndWait(new Runnable() {
						@Override
						public void run() {
							menu.getModel().setSelected(true);
							JPopupMenu popupMenu = menu.getPopupMenu();
							menuStack.put(menu, popupMenu);
							PopupMenuEvent pevent = new PopupMenuEvent(popupMenu);
							for (PopupMenuListener pl : menu.getPopupMenu().getPopupMenuListeners())
	                        	if (pl != null) pl.popupMenuWillBecomeVisible(pevent);
							for (Component comp : popupMenu.getComponents()) {
								if (comp.isVisible()) {
									if (comp instanceof JMenu)
										addMenu(menu, (JMenu)comp);
									else if (comp instanceof JMenuItem)
										addMenuItem(menu, (JMenuItem)comp);
									else if (comp instanceof JSeparator)
										addSeparator(windowXID);
								}
							}
						}
					});
				} catch (Exception e) {
					Logger.getLogger(SwingGlobalMenuWindow.class.getName())
						.log(Level.SEVERE, e.getMessage(), e);
				}
			}
		}
	}
	@Override
	protected void menuAfterClose(int menuId) {
		final SwingGlobalMenuStack.MenuStackEntry mse = menuStack.findMenu(menuId);
		if (mse != null) {
			final JMenu menu = mse.getMenu();
			if (menu != null && menu.isEnabled() && menu.isVisible()) {
				try {
					EventQueue.invokeAndWait(new Runnable() {
						@Override
						public void run() {
							JPopupMenu popupMenu = mse.getPopupMenu();
							PopupMenuEvent pevent = new PopupMenuEvent(popupMenu);
							for (PopupMenuListener pl : menu.getPopupMenu().getPopupMenuListeners())
	                        	if (pl != null) pl.popupMenuWillBecomeInvisible(pevent);
							menu.getModel().setSelected(false);
						}
					});
				} catch (Exception e) {
					Logger.getLogger(SwingGlobalMenuWindow.class.getName())
						.log(Level.WARNING, e.getMessage(), e);
				}
			}
		}
	}

	@Override
	public void windowOpened(WindowEvent e) {}
	@Override
	public void windowClosing(WindowEvent e) {
		unregisterWatcher(windowXID);
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
				if (getWindow((Component)e.getSource()) == window) {
					try {
						Method methodProcessKeyBinding = JMenuBar.class.getDeclaredMethod(
							"processKeyBinding", new Class<?>[] {
								KeyStroke.class, KeyEvent.class, int.class, boolean.class
						});
						if (!methodProcessKeyBinding.isAccessible())
							methodProcessKeyBinding.setAccessible(true);
						methodProcessKeyBinding.invoke(menubar, new Object[] {
							KeyStroke.getKeyStroke(e.getKeyCode(), e.getModifiers()), e, 	
							JComponent.WHEN_IN_FOCUSED_WINDOW, true
						});
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
		if ("enabled".equals(evt.getPropertyName()))
			recreateMenuBarMenus();
	}
	@Override
	public void componentHidden(ComponentEvent e) {
		if (e.getSource() instanceof JMenu)
			recreateMenuBarMenus();
	}
	@Override
	public void componentShown(ComponentEvent e) {
		if (e.getSource() instanceof JMenu)
			recreateMenuBarMenus();
	}
	@Override
	public void componentMoved(ComponentEvent e) {}
	@Override
	public void componentResized(ComponentEvent e) {}
}
