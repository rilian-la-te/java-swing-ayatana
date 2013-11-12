package com.jarego.jayatana.swing;

import java.util.Map;
import java.util.TreeMap;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

public class SwingGlobalMenuStack {
	private Map<Integer, MenuStackEntry> menus;
	public SwingGlobalMenuStack() {
		menus = new TreeMap<Integer, MenuStackEntry>();
	}
	public MenuStackEntry put(JMenu menu) {
		MenuStackEntry menuStack;
		menus.put(menu.hashCode(), menuStack = new MenuStackEntry(menu));
		return menuStack;
	}
	public void put(JMenu menu, JPopupMenu item) {
		menus.get(menu.hashCode()).setPopupMenu(item);
	}
	public void put(JMenu menu, JMenuItem item) {
		menus.get(menu.hashCode()).getMenuItems().put(item.hashCode(), item);
	}
	public MenuStackEntry remove(int menuId) {
		MenuStackEntry mse = menus.get(menuId);
		if (mse.getMenu().getParent() instanceof JMenuBar) {
			mse.getMenuItems().clear();
			return mse;
		} else {
			return menus.remove(menuId);
		}
	}
	public JMenu findMenu(int menuId) {
		return menus.get(menuId).getMenu();
	}
	public JMenuItem findMenuItem(int menuId) {
		for (MenuStackEntry ms : menus.values()) {
			JMenuItem item = ms.getMenuItems().get(menuId);
			if (item != null)
				return item;
		}
		return null;
	}
	
	@Override
	public String toString() {
		String out = "";
		for (MenuStackEntry ms : menus.values()) {
			out += ms.getMenu().getText() + "\n";
			for (JMenuItem item : ms.getMenuItems().values())
				out += ">"+item.getText() + "\n";
		}
		return out;
	}
	
	public class MenuStackEntry {
		private JMenu menu;
		private JPopupMenu popupMenu;
		private Map<Integer, JMenuItem> menuItems;
		
		public MenuStackEntry(JMenu menu) {
			this.menu = menu;
			menuItems = new TreeMap<Integer, JMenuItem>();
		}
		public JMenu getMenu() {
			return menu;
		}
		public JPopupMenu getPopupMenu() {
			return popupMenu;
		}
		public void setPopupMenu(JPopupMenu popupMenu) {
			this.popupMenu = popupMenu;
		}
		public Map<Integer, JMenuItem> getMenuItems() {
			return menuItems;
		}
	}
}
