package com.jarego.jayatana.basic;

import com.jarego.jayatana.Feature;

public class GetAppContext implements Feature {
	@SuppressWarnings("restriction")
	@Override
	public void deploy() {
		sun.awt.AppContext.getAppContext();
	}
}
