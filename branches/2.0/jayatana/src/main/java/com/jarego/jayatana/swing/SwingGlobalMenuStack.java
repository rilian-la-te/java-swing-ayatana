package com.jarego.jayatana.swing;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

public class SwingGlobalMenuStack {
	private Map<Integer, MenuStackEntry> menus;
	
	public SwingGlobalMenuStack() {
		menus = new TreeMap<Integer, MenuStackEntry>();
	}
	public MenuStackEntry put(JMenu parent, JMenu menu) {
		MenuStackEntry menuStack;
		menus.put(menu.hashCode(), menuStack = new MenuStackEntry(parent, menu));
		return menuStack;
	}
	public void put(JMenu menu, JPopupMenu item) {
		menus.get(menu.hashCode()).setPopupMenu(item);
	}
	public void put(JMenu menu, JMenuItem item) {
		menus.get(menu.hashCode()).getMenuItems().put(item.hashCode(), item);
	}
	private void removeMenu(MenuStackEntry menuEntry) {
		menuEntry.getMenuItems().clear();
		
		List<MenuStackEntry> menusToRemove = new ArrayList<MenuStackEntry>();
		for (MenuStackEntry msei : menus.values())
			if (msei.getParentMenu() == menuEntry.getMenu())
				menusToRemove.add(msei);
		
		for (MenuStackEntry msei : menusToRemove) {
			removeMenu(msei);
			menus.remove(msei.getId());
		}
	}
	public MenuStackEntry removeMenu(int menuId) {
		MenuStackEntry mse = menus.get(menuId);
		if (mse != null) {
			mse.getMenuItems().clear();
			
			List<MenuStackEntry> menusToRemove = new ArrayList<MenuStackEntry>();
			for (MenuStackEntry msei : menus.values())
				if (msei.getParentMenu() == mse.getMenu())
					menusToRemove.add(msei);
			
			for (MenuStackEntry msei : menusToRemove) {
				removeMenu(msei);
				menus.remove(msei.getId());
			}
		}
		return mse;
	}
	public MenuStackEntry findMenu(int menuId) {
		return menus.get(menuId);
	}
	public JMenuItem findMenuItem(int menuId) {
		for (MenuStackEntry ms : menus.values()) {
			JMenuItem item = ms.getMenuItems().get(menuId);
			if (item != null)
				return item;
		}
		return null;
	}
	public void removeAll() {
		for (MenuStackEntry msei : menus.values())
			msei.getMenuItems().clear();
		menus.clear();
	}
	
	@Override
	public String toString() {
		String out = "";
		int cmenus = 0;
		for (MenuStackEntry ms : menus.values()) {
			out += ms.getMenu().getText() + "\n";
			cmenus++;
			for (JMenuItem item : ms.getMenuItems().values()) {
				out += ">"+item.getText() + "\n";
				cmenus++;
			}
		}
		return out+"\nTOTAL: "+cmenus;
	}
	
	public class MenuStackEntry {
		private JMenu parentMenu;
		private JMenu menu;
		private JPopupMenu popupMenu;
		private Map<Integer, JMenuItem> menuItems;
		
		public MenuStackEntry(JMenu parentMenu, JMenu menu) {
			this.menu = menu;
			this.parentMenu = parentMenu;
			menuItems = new TreeMap<Integer, JMenuItem>();
		}
		public int getId() {
			return menu.hashCode();
		}
		public JMenu getParentMenu() {
			return parentMenu;
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
