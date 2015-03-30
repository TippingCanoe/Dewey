package com.tippingcanoe.dewey.app;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.tippingcanoe.dewey.Dewey;

public class MainActivity extends ActionBarActivity {
	Dewey dewey;
	Button setPositionButton;
	EditText setPositionEditText;

	@Override
	protected void onCreate ( Bundle savedInstanceState ) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		dewey = (Dewey) findViewById(R.id.dewey);
		setupDewey();

		setPositionButton = (Button) findViewById(R.id.position_button);
		setPositionEditText = (EditText) findViewById(R.id.position_text);

		setupButtons();
	}

	protected void setupDewey () {
		dewey.setAdapter(new DemoAdapter());
		dewey.setOnFocusedPositionChangedListener(new Dewey.OnFocusedPositionChangedListener() {
			@Override
			public void onFocusedPositionChanged ( int previousFocusedPosition, int newFocusedPosition ) {
				Toast.makeText(MainActivity.this, "Now focusing on " + newFocusedPosition + ".", Toast.LENGTH_SHORT).show();
			}
		});
	}

	protected void setupButtons () {
		setPositionButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick ( View v ) {
				try {
					int position = Integer.parseInt(setPositionEditText.getText().toString());

					if (position < 0 || position > dewey.getAdapter().getItemCount() - 1) {
						throw new NumberFormatException();
					}

					dewey.setFocusedPosition(position, true);
				} catch (NumberFormatException e) {
					Toast.makeText(MainActivity.this, "Could not parse position.", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}
}
