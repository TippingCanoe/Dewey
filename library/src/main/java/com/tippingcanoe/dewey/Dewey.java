package com.tippingcanoe.dewey;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

public class Dewey extends RecyclerView {
	DeweyLayoutManager layoutManager;
	DeweyDecorator deweyDecorator;

	public Dewey ( Context context ) {
		super(context);

		setup(context);
	}

	public Dewey ( Context context, AttributeSet attrs ) {
		super(context, attrs);

		setup(context);
	}

	public Dewey ( Context context, AttributeSet attrs, int defStyle ) {
		super(context, attrs, defStyle);

		setup(context);
	}

	protected void setup ( Context context ) {
		layoutManager = new DeweyLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
		setLayoutManager(layoutManager);
		setHasFixedSize(true);
	}

	@Override
	public void setAdapter ( Adapter adapter ) {
		super.setAdapter(adapter);

		if ( deweyDecorator != null ) {
			removeItemDecoration(deweyDecorator);
		}

		deweyDecorator = new DeweyDecorator(this, adapter);
		addItemDecoration(deweyDecorator);
	}
}
