package com.tippingcanoe.dewey;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;

public class Dewey extends RecyclerView {
	public static int INVALID_RESOURCE = -1;

	int focusedPosition;

	DeweyLayoutManager layoutManager;
	DeweyDecorator deweyDecorator;

	OnFocusedPositionChangedListener onFocusedPositionChangedListener;

	// Style attributes.

	int cloakColor = Color.argb((int) (255f / 2f), 255, 255, 255);
	float minCloakPercentage = 50f;
	Interpolator cloakCurveInterpolator = new AccelerateInterpolator();

	int stripHeight = 10;
	int stripColor = Color.RED;
	Interpolator stripAnimationInterpolator = new AccelerateDecelerateInterpolator();

	int animationDurationMs = (int) (0.3f * 1000f);

	public Dewey ( Context context ) {
		super(context);

		setup(context, null);
	}

	public Dewey ( Context context, AttributeSet attrs ) {
		super(context, attrs);

		setup(context, attrs);
	}

	public Dewey ( Context context, AttributeSet attrs, int defStyle ) {
		super(context, attrs, defStyle);

		setup(context, attrs);
	}

	protected void setup ( Context context, @Nullable AttributeSet attrs ) {
		layoutManager = new DeweyLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
		setLayoutManager(layoutManager);
		setHasFixedSize(true);
		setHorizontalScrollBarEnabled(false);

		if (attrs != null) {
			TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.Dewey, 0, 0);

			try {
				cloakColor = typedArray.getColor(R.styleable.Dewey_cloakColor, cloakColor);
				minCloakPercentage = typedArray.getFloat(R.styleable.Dewey_minCloakPercentage, minCloakPercentage);
				stripHeight = typedArray.getDimensionPixelSize(R.styleable.Dewey_stripHeight, stripHeight);
				stripColor = typedArray.getColor(R.styleable.Dewey_stripColor, stripColor);
				animationDurationMs = typedArray.getInteger(R.styleable.Dewey_animationDurationMs, animationDurationMs);
			} finally {
				typedArray.recycle();
			}
		}

		setFocusedPosition(0, false);
	}

	public void setFocusedPosition ( int position, boolean animated ) {
		if (onFocusedPositionChangedListener != null) {
			onFocusedPositionChangedListener.onFocusedPositionChanged(focusedPosition, position);
		}

		boolean requestedPositionIsVisible = position >= getChildPosition(getChildAt(0)) && position <= getChildPosition(getChildAt(getChildCount() - 1));

		// @TODO, enhance this by attempting to center the focused position.
		if (requestedPositionIsVisible && animated) {
			deweyDecorator.startAnimation(focusedPosition, position);
		} else if (animated) {
			smoothScrollToPosition(position);
		} else {
			scrollToPosition(position);
		}

		focusedPosition = position;
	}

	@Override
	public void setAdapter ( Adapter adapter ) {
		super.setAdapter(adapter);

		if ( deweyDecorator != null ) {
			if (deweyDecorator.getOnItemTouchListener() != null) {
				removeOnItemTouchListener(deweyDecorator.getOnItemTouchListener());
			}

			removeItemDecoration(deweyDecorator);
		}

		deweyDecorator = new DeweyDecorator(this, adapter);
		addItemDecoration(deweyDecorator);
	}

	public int getFocusedPosition () {
		return focusedPosition;
	}

	public void setOnFocusedPositionChangedListener ( OnFocusedPositionChangedListener onFocusedPositionChangedListener ) {
		this.onFocusedPositionChangedListener = onFocusedPositionChangedListener;
	}

	public
	@ColorRes
	int getCloakColor () {
		return cloakColor;
	}

	public void setCloakColor ( @ColorRes int cloakColor ) {
		this.cloakColor = cloakColor;

		if (deweyDecorator != null) {
			deweyDecorator.setupCloak();
		}
	}

	public float getMinCloakPercentage () {
		return minCloakPercentage;
	}

	public void setMinCloakPercentage ( float minCloakPercentage ) {
		if (minCloakPercentage < 0f || minCloakPercentage > 1f) {
			throw new IllegalArgumentException("Cloak percentage must be between 0 and 1.");
		}

		this.minCloakPercentage = minCloakPercentage;
	}

	public int getStripHeight () {
		return stripHeight;
	}

	public void setStripHeight ( @DimenRes int stripHeight ) {
		this.stripHeight = stripHeight;
	}

	public int getStripColor () {
		return stripColor;
	}

	public void setStripColor ( @ColorRes int stripColor ) {
		this.stripColor = stripColor;

		if (deweyDecorator != null) {
			deweyDecorator.setupStrip();
		}
	}

	public int getAnimationDurationMs () {
		return animationDurationMs;
	}

	public void setAnimationDurationMs ( int animationDurationMs ) {
		this.animationDurationMs = animationDurationMs;
	}

	public Interpolator getStripAnimationInterpolator () {
		return stripAnimationInterpolator;
	}

	public void setStripAnimationInterpolator ( Interpolator stripAnimationInterpolator ) {
		this.stripAnimationInterpolator = stripAnimationInterpolator;
	}

	public Interpolator getCloakCurveInterpolator () {
		return cloakCurveInterpolator;
	}

	public void setCloakCurveInterpolator ( Interpolator cloakCurveInterpolator ) {
		this.cloakCurveInterpolator = cloakCurveInterpolator;
	}

	public static interface OnFocusedPositionChangedListener {
		void onFocusedPositionChanged ( int previousFocusedPosition, int newFocusedPosition );
	}
}
