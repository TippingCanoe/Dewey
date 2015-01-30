package com.tippingcanoe.dewey.app;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import com.tippingcanoe.dewey.Dewey;

public class MainActivity extends ActionBarActivity {
	Dewey dewey;

	@Override
	protected void onCreate ( Bundle savedInstanceState ) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		dewey = (Dewey) findViewById(R.id.dewey);
		setupDewey();
	}

	protected void setupDewey () {
		dewey.setAdapter(new DemoAdapter());
	}
}
