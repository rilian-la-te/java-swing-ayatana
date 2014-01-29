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
package com.jarego.jayatana;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class FeatureManager {
	public static final String FEATURE_GMAINLOOP = "gMainLoop";
	public static final String FEATURE_SWINGGTKFIX = "swingGtkFix";
	public static final String FEATURE_SWINGGMENU = "swingGMenu";
	public static final String FEATURE_SWINGWMCLASS = "swingWMClass";
	
	private static Map<String, FeatureWrapper> features = new HashMap<String, FeatureWrapper>();
	
	static {
		// registrar carcateristicas de integración
		features.put(FEATURE_GMAINLOOP,
				new FeatureWrapper("com.jarego.jayatana.basic.GMainLoop"));
		features.put(FEATURE_SWINGGTKFIX,
				new FeatureWrapper("com.jarego.jayatana.swing.SwingGTKFixed"));
		features.put(FEATURE_SWINGGMENU,
				new FeatureWrapper("com.jarego.jayatana.swing.SwingGlobalMenu"));
		features.put(FEATURE_SWINGWMCLASS,
				new FeatureWrapper("com.jarego.jayatana.swing.SwingWMClass"));
	}
	
	public static void deployForSwing() {
		// si la libreria no existe cancelar integración
		if (!new File("/usr/lib/libjayatana.so").canRead())
			return;
		// cargar librerias para soporte swing
		System.loadLibrary("jawt");
		// cargar libreria de JAyatana
		if (System.getenv("JAYATANA_LIBPATH") != null) //opcion para desarrollo
			System.load(System.getenv("JAYATANA_LIBPATH"));
		else
			System.load("/usr/lib/libjayatana.so");
		// desplegar carcateristicas de integración
		deployOnce(FEATURE_SWINGWMCLASS);
		deployOnce(FEATURE_SWINGGTKFIX);
		deployOnce(FEATURE_SWINGGMENU);
	}
	
	public static boolean deployOnce(String featureId) {
		return features.get(featureId).deployOnce();
	}
	public static boolean isDeployed(String featureId) {
		return features.get(featureId).isDeployed();
	}
}
