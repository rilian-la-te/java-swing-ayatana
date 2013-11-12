package com.jarego.jayatana.swing;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Window;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import com.jarego.jayatana.FeatureManager;
import com.jarego.jayatana.basic.GlobalMenu;

public class SwingGlobalMenuWindow extends GlobalMenu implements WindowListener {
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
		createMenuBarMenus();
		menubar.setVisible(false);
	}
	@Override
	protected void unregister() {
		menubar.setVisible(true);
	}
	
	private void createMenuBarMenus() {
		for (int i=0;i<menubar.getMenuCount();i++) {
			JMenu menu = menubar.getMenu(i);
			if (menu.isVisible() && menu.getText() != null && !"".equals(menu.getText()))
				addMenu(menubar.getMenu(i));
		}
	}
	
	private void addMenu(JMenu menu) {
		addMenu(windowXID, menu.hashCode(), menu.getText(), menu.isEnabled());
		menuStack.put(menu);
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
		final JMenu menu = (JMenu)menuStack.findMenu(menuId);
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
									addMenu((JMenu)comp);
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
	@Override
	protected void menuAfterClose(int menuId) {
		final SwingGlobalMenuStack.MenuStackEntry mse = menuStack.remove(menuId);
		final JMenu menu = (JMenu)mse.getMenu();
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
					.log(Level.SEVERE, e.getMessage(), e);
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
}
