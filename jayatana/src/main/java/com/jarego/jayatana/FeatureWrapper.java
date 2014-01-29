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

import java.util.logging.Level;
import java.util.logging.Logger;

public class FeatureWrapper {
	private String className;
	private Feature featrue = null;
	private boolean deployed = false;
	
	public FeatureWrapper(String className) {
		this.className = className;
	}
	
	private Feature getInstance() {
		if (featrue == null)
			try {
				featrue = (Feature)Class.forName(className).newInstance();
			} catch (Exception e) {
				Logger.getLogger(FeatureWrapper.class.getName())
					.log(Level.WARNING, "can't create feature", e);
			}
		return featrue;
	}
	
	public synchronized boolean isDeployed() {
		return deployed;
	}
	
	public synchronized boolean deployOnce() {
		if (!deployed) {
			Feature feature;
			if ((feature = getInstance()) != null) {
				feature.deploy();
				deployed = true;
				return true;
			}
		}
		return false;
	}
}
