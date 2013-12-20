package com.jarego.jayatana.basic;

import java.awt.Window;

public abstract class GlobalMenuAdapter extends GlobalMenu {
	private Object window;
	private long windowXID;
	
	public GlobalMenuAdapter(Object window) {
		if (window instanceof Window)
			this.window = window;
		else
			throw new IllegalArgumentException();
	}
	
	public void tryInstall() {
		if (window instanceof Window)
			registerWatcher(windowXID = getWindowXID((Window)window));
	}
	
	protected void registerWatcher() {
		registerWatcher(windowXID);
	}
	protected void unregisterWatcher() {
		unregisterWatcher(windowXID);
	}
	public void refreshWatcher() {
		refreshWatcher(windowXID);
	}
	
	protected void addMenu(int menuId, String label, boolean enabled, boolean visible) {
		addMenu(windowXID, -1, menuId, label, enabled, visible);
	}
	protected void addMenu(int menuParentId, int menuId, String label, boolean enabled,
			boolean visible) {
		addMenu(windowXID, menuParentId, menuId, label, enabled, visible);
	}
	protected void addMenuItem(int menuParentId, int menuId, String label, boolean enabled,
			int modifiers, int keycode) {
		addMenuItem(windowXID, menuParentId, menuId, label, enabled, modifiers, keycode);
	}
	protected void addMenuItemCheck(int menuParentId, int menuId, String label, boolean enabled,
			int modifiers, int keycode, boolean selected) {
		addMenuItemCheck(windowXID, menuParentId, menuId, label, enabled,modifiers, keycode, selected);
	}
	protected void addMenuItemRadio(int menuParentId, int menuId, String label, boolean enabled,
			int modifiers, int keycode, boolean selected) {
		addMenuItemRadio(windowXID, menuParentId, menuId, label, enabled, modifiers, keycode, selected);
	}
	protected void addSeparator(int menuParentId) {
		addSeparator(windowXID, menuParentId);
	}
	protected void updateMenu(int menuId, String label, boolean enabled, boolean visible) {
		updateMenu(windowXID, menuId, label, enabled, visible);
	}
	
	protected Object getWindow() {
		return window;
	}
	protected long getWindowXID() {
		return windowXID;
	}
}
