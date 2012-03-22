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

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

/**
 * Interface para agregar acciones adicionales en la invocación de menu
 * 
 * @author Jared González
 */
public interface ExtraMenuAction {
	/**
	 * Indica si soporta menus sobre la barra dinamicos, es decir, si se crean menus después
	 * de haber cargado el programa.
	 * @return 
	 */
	public boolean allowDynamicMenuBar();
	/**
	 * Indica si la accion invokada se permite.
	 * @param frame ventana
	 * @param menubar barra de menus
	 * @param menuitem menu
	 * @param selected si el objeto es <code>JMenu</code> entonces retorna <code>True</code>
	 * cuando se muestra y <code>False</code> cuando se oculta.
	 * @return 
	 */
	public boolean allowMenuAction(JFrame frame, JMenuBar menubar, JMenuItem menuitem, boolean selected);
	/**
	 * Es lanzado antes de ejecutar la accion
	 * @param frame ventana
	 * @param menubar barra de menus
	 * @param menuitem menu
	 * @param selected si el objeto es <code>JMenu</code> entonces retorna <code>True</code>
	 * cuando se muestra y <code>False</code> cuando se oculta.
	 */
	public void beforInvokeMenu(JFrame frame, JMenuBar menubar, JMenuItem menuitem, boolean selected);
	/**
	 * Es lanzado durante la accion
	 * @param frame ventana
	 * @param menubar barra de menus
	 * @param menuitem menu
	 * @param selected si el objeto es <code>JMenu</code> entonces retorna <code>True</code>
	 * cuando se muestra y <code>False</code> cuando se oculta.
	 */
	public void invokeMenu(JFrame frame, JMenuBar menubar, JMenuItem menuitem, boolean selected);
	/**
	 * Es lanzado después de ejecutar la accion
	 * @param frame ventana
	 * @param menubar barra de menus
	 * @param menuitem menu
	 * @param selected si el objeto es <code>JMenu</code> entonces retorna <code>True</code>
	 * cuando se muestra y <code>False</code> cuando se oculta.
	 */
	public void afterInvokeMenu(JFrame frame, JMenuBar menubar, JMenuItem menuitem, boolean selected);
}
