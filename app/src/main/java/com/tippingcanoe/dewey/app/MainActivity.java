package com.tippingcanoe.dewey.app;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.*;
import android.widget.ArrayAdapter;
import com.tippingcanoe.dewey.Dewey;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

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
