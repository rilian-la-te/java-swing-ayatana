/*
 * Copyright (c) 2013 Jared González
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
package com.jarego.jayatana.basic;

import java.awt.Window;

public abstract class GlobalMenu {
	public static final Thread thread;
	public static final int REGISTER_STATE_INITIAL = 0;
	public static final int REGISTER_STATE_REFRESH = 1;
	
	static {
		Runtime.getRuntime().addShutdownHook(thread = new Thread() {
			{
				setDaemon(true);
				setName("JAyatana GlobalMenu Shutdown");
			}
			@Override
			public void run() {
				GlobalMenu.uninitialize();
			}
		});
		GlobalMenu.initialize();
	}
	
	native private static void initialize();
	native private static void uninitialize();
	
	native public static long getWindowXID(Window window);
	
	native synchronized public void registerWatcher(long windowXID);
	native synchronized public void unregisterWatcher(long windowXID);
	native synchronized public void refreshWatcher(long windowXID);
	
	abstract protected void register(int state);
	abstract protected void unregister();
	
	native public void addMenu(long windowXID, int menuParentId, int menuId,
			String label, boolean enabled, boolean visible);
	native public void addMenuItem(long windowXID, int menuParentId, int menuId,
			String label, boolean enabled, int modifiers, int keycode);
	native public void addMenuItemRadio(long windowXID, int menuParentId, int menuId,
			String label, boolean enabled, int modifiers, int keycode, boolean selected);
	native public void addMenuItemCheck(long windowXID, int menuParentId, int menuId,
			String label, boolean enabled, int modifiers, int keycode, boolean selected);
	native public void addSeparator(long windowXID, int menuParentId);
	native public void updateMenu(long windowXID, int menuId, String label,
			boolean enabled, boolean visible);
	
	abstract protected void menuActivated(int menuId);
	abstract protected void menuAboutToShow(int menuId);
	abstract protected void menuAfterClose(int menuId);
}
