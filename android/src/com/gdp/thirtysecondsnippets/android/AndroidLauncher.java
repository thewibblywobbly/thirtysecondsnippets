package com.gdp.thirtysecondsnippets.android;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.gdp.thirtysecondsnippets.TSS;
import com.gdp.thirtysecondsnippets.ThirtySecondSnippets;

public class AndroidLauncher extends AndroidApplication {
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
                config.useImmersiveMode = true;
                initialize(new TSS(), config);
	}
}
