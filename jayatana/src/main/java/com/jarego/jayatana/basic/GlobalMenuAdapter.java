/*
 * Copyright (c) 2014 Jared González
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

/**
 * Clase de adatador de GlobalMenu que permite encapsular el controlador de ventana
 * junto con los controles de menú nativo.
 * 
 * @author Jared González
 */
public abstract class GlobalMenuAdapter extends GlobalMenu {
	private Object window;
	private long windowXID;
	
	/**
	 * Iniciar controlador de menú global basado en un objeto ventana.
	 * 
	 * @param window ventana
	 */
	public GlobalMenuAdapter(Object window) {
		if (window instanceof Window)
			this.window = window;
		else
			throw new IllegalArgumentException();
	}
	
	/**
	 * Tratar de iniciar la integración, esto dependera si existe un bus o no.
	 */
	public void tryInstall() {
		if (window instanceof Window)
			registerWatcher(windowXID = getWindowXID((Window)window));
	}
	
	/**
	 * Registra visualizador de bus de menu global. En caso de que el bus
	 * exista se invocará el método <code>register</code>.
	 */
	protected void registerWatcher() {
		registerWatcher(windowXID);
	}
	/**
	 * Elimina el visualizador de bus de menu global. En cas de que el bus
	 * exista se invocará el método <code>unregister</code>.
	 */
	protected void unregisterWatcher() {
		unregisterWatcher(windowXID);
	}
	/**
	 * Este método regenera el visualizador de Bus, y debe ser usado si algun
	 * menu (de nivel 0) agregado directamente a la barra de menus cambia,
	 * puesto que Ubuntu tiene problemas con los métodos de agregar o eliminar estos.
	 */
	protected void refreshWatcher() {
		refreshWatcher(windowXID);
	}
	
	/**
	 * Agrega un nuevo menú de folder nativo sobre la barra de menús.
	 * 
	 * @param menuId identificador del menú.
	 * @param label etiqueta del menú.
	 * @param enabled estado de habilitación del menú.
	 * @param visible estado de visibulidad del menú.
	 */
	protected void addMenu(int menuId, String label, boolean enabled, boolean visible) {
		addMenu(windowXID, -1, menuId, label, enabled, visible);
	}
	/**
	 * Agrega un nuevo menú de folder nativo.
	 * 
	 * @param menuParentId identificador del menu padre, para especificar un menu directamente en la barra
	 * de menú el identificador del padre debe ser <code>-1</code>.
	 * @param menuId identificador del menú.
	 * @param label etiqueta del menú.
	 * @param enabled estado de habilitación del menú.
	 * @param visible estado de visibulidad del menú.
	 */
	protected void addMenu(int menuParentId, int menuId, String label, boolean enabled,
			boolean visible) {
		addMenu(windowXID, menuParentId, menuId, label, enabled, visible);
	}
	/**
	 * Agrega un elemento de menú nativo.
	 * 
	 * @param menuParentId identificador del menú padre.
	 * @param menuId identificador del menú.
	 * @param label etiqueta del menú.
	 * @param enabled estado de habilitación del menú.
	 * @param modifiers modificador del acelerador del menú (CTRL, ALT o SHIFT).
	 * @param keycode acelerador del menú.
	 */
	protected void addMenuItem(int menuParentId, int menuId, String label, boolean enabled,
			int modifiers, int keycode) {
		addMenuItem(windowXID, menuParentId, menuId, label, enabled, modifiers, keycode);
	}
	/**
	 * Agrega un elemento de menú check nativo.
	 * 
	 * @param menuParentId identificador del menú padre.
	 * @param menuId identificador del menú.
	 * @param label etiqueta del menú.
	 * @param enabled estado de habilitación del menú.
	 * @param modifiers modificador del acelerador del menú (CTRL, ALT o SHIFT).
	 * @param keycode acelerador del menú.
	 * @param selected estado de selección del menú.
	 */
	protected void addMenuItemCheck(int menuParentId, int menuId, String label, boolean enabled,
			int modifiers, int keycode, boolean selected) {
		addMenuItemCheck(windowXID, menuParentId, menuId, label, enabled,modifiers, keycode, selected);
	}
	/**
	 * Agrega un elemento de menú radio nativo.
	 * 
	 * @param menuParentId identificador del menú padre.
	 * @param menuId identificador del menú.
	 * @param label etiqueta del menú.
	 * @param enabled estado de habilitación del menú.
	 * @param modifiers modificador del acelerador del menú (CTRL, ALT o SHIFT).
	 * @param keycode acelerador del menú.
	 * @param selected estado de selección del menú.
	 */
	protected void addMenuItemRadio(int menuParentId, int menuId, String label, boolean enabled,
			int modifiers, int keycode, boolean selected) {
		addMenuItemRadio(windowXID, menuParentId, menuId, label, enabled, modifiers, keycode, selected);
	}
	/**
	 * Agrega un elmemento de menú de separador nativo.
	 * 
	 * @param menuParentId identificador del menú padre.
	 */
	protected void addSeparator(int menuParentId) {
		addSeparator(windowXID, menuParentId);
	}
	/**
	 * Actualización de estado del menú nativo.
	 * 
	 * @param menuId identificador de menu
	 * @param label nuevo valor de etiqueta
	 * @param enabled nuevo valor de estado de habilitación del menú.
	 * @param visible nuevo valor de estado de visibilidad del menú.
	 */
	protected void updateMenu(int menuId, String label, boolean enabled, boolean visible) {
		updateMenu(windowXID, menuId, label, enabled, visible);
	}
	
	/**
	 * Obtener la ventana.
	 * 
	 * @return objeto ventana.
	 */
	protected Object getWindow() {
		return window;
	}
	/**
	 * Obtener el identificador de ventana.
	 * 
	 * @return identificador de ventana.
	 */
	protected long getWindowXID() {
		return windowXID;
	}
}
