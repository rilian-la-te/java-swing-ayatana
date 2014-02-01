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
package com.jarego.jayatana.swing;

import com.jarego.jayatana.Feature;
import com.sun.java.swing.plaf.gtk.GTKLookAndFeel;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.plaf.synth.Region;

@SuppressWarnings("restriction")
public class SwingGTKFixed implements Feature, PropertyChangeListener {
	@Override
	public void deploy() {
		UIManager.addPropertyChangeListener(new SwingGTKFixed());
		if ("com.sun.java.swing.plaf.gtk.GTKLookAndFeel".equals(
			UIManager.getLookAndFeel().getClass().getName())) {
			try {
				installGtkThikcness();
			} catch (Exception e) {
				Logger.getLogger(SwingGTKFixed.class.getName())
					.log(Level.WARNING, "can't install gtk border fixed", e);
			}
		}
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if ("lookAndFeel".equals(evt.getPropertyName())) {
			if (evt.getNewValue() != null ?
					"com.sun.java.swing.plaf.gtk.GTKLookAndFeel".equals(
							evt.getNewValue().getClass().getName()): false)
			try {
				installGtkThikcness();
			} catch (Exception e) {
				Logger.getLogger(SwingGTKFixed.class.getName())
					.log(Level.WARNING, "can't install gtk border fixed", e);
			}
		}
	}
	
	private static void installGtkThikcness() throws Exception {
		JComponent comp = new JPanel();
		changeGtkYThikcness(GTKLookAndFeel.getStyle(comp , Region.POPUP_MENU), 1);
        changeGtkXThikcness(GTKLookAndFeel.getStyle(comp, Region.POPUP_MENU), 1);
        changeGtkYThikcness(GTKLookAndFeel.getStyle(comp, Region.POPUP_MENU_SEPARATOR), 1);
	}
	private static void changeGtkYThikcness(Object style, int border)
			throws Exception {
		Field field = style.getClass().getDeclaredField("yThickness");
		field.setAccessible(true);
		field.setInt(style, Math.max(border, field.getInt(style)));
		field.setAccessible(false);
	}
	private static void changeGtkXThikcness(Object style, int border)
			throws Exception {
		Field field = style.getClass().getDeclaredField("xThickness");
		field.setAccessible(true);
		field.setInt(style, Math.max(border, field.getInt(style)));
		field.setAccessible(false);
	}
}
