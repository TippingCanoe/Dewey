package com.tippingcanoe.dewey;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
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

	int stripOffset = 0;
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
		boolean uniformCells = false;

		if (attrs != null) {
			TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.Dewey, 0, 0);

			try {
				cloakColor = typedArray.getColor(R.styleable.Dewey_cloakColor, cloakColor);
				minCloakPercentage = typedArray.getFloat(R.styleable.Dewey_minCloakPercentage, minCloakPercentage);
				stripOffset = typedArray.getDimensionPixelSize(R.styleable.Dewey_stripOffset, stripOffset);
				stripHeight = typedArray.getDimensionPixelSize(R.styleable.Dewey_stripHeight, stripHeight);
				stripColor = typedArray.getColor(R.styleable.Dewey_stripColor, stripColor);
				animationDurationMs = typedArray.getInteger(R.styleable.Dewey_animationDurationMs, animationDurationMs);
				uniformCells = typedArray.getBoolean(R.styleable.Dewey_uniformCells, uniformCells);
			} finally {
				typedArray.recycle();
			}
		}

		layoutManager = new DeweyLayoutManager(context, LinearLayoutManager.HORIZONTAL, false, uniformCells);
		setLayoutManager(layoutManager);
		setHasFixedSize(true);
		setHorizontalScrollBarEnabled(false);

		setFocusedPosition(0, false, true);
	}

	protected int adjustPositionToCenterIfNeeded(int inputPosition) {
		if ( inputPosition != focusedPosition && layoutManager != null && layoutManager.areCellsUniform() && layoutManager.getUniformCellWidth() != 0 ) {
			boolean scrollingRight = inputPosition > focusedPosition;
			float cellsInView = ((float) getMeasuredWidth() / (float) layoutManager.getUniformCellWidth());
			int cellsToCenter = (int) Math.floor((cellsInView / 2.0f));
			int offset = (scrollingRight ? 1 : -1) * cellsToCenter;
			int adjustedPosition = Math.min(inputPosition + offset, getAdapter().getItemCount() - 1);

			if ( adjustedPosition >= 0 && inputPosition >= offset ) {
				return adjustedPosition;
			}
		}

		return inputPosition;
	}

	public void setFocusedPosition ( int position, boolean animated ) {
		setFocusedPosition(position, animated, false);
	}

	public void setFocusedPosition ( int position, boolean animated, boolean silently ) {
		if (!silently && onFocusedPositionChangedListener != null) {
			onFocusedPositionChangedListener.onFocusedPositionChanged(focusedPosition, position);
		}

		int adjustedScrollTarget = adjustPositionToCenterIfNeeded(position);

		if (requestedPositionIsVisible(position) && requestedPositionIsVisible(adjustedScrollTarget) && animated) {
			deweyDecorator.startAnimation(focusedPosition, position);
		} else if (animated) {
			smoothScrollToPosition(adjustedScrollTarget);
		} else {
			scrollToPosition(adjustedScrollTarget);
		}

		focusedPosition = position;
	}

	public boolean requestedPositionIsVisible ( int position ) {
		return position >= getChildAdapterPosition(getChildAt(0)) && position <= getChildAdapterPosition(getChildAt(getChildCount() - 1));
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
	@ColorInt
	int getCloakColor () {
		return cloakColor;
	}

	public void setCloakColor ( @ColorRes int cloakColor ) {
		this.cloakColor = getResources().getColor(cloakColor);

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
		this.stripHeight = getResources().getDimensionPixelSize(stripHeight);
	}

	public @ColorInt int getStripColor () {
		return stripColor;
	}

	public void setStripColor ( @ColorRes int stripColor ) {
		this.stripColor = getResources().getColor(stripColor);

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

	public boolean areCellsUniform() {
		return ((DeweyLayoutManager) getLayoutManager()).areCellsUniform();
	}

	public void setUniformCells(boolean uniformCells) {
		((DeweyLayoutManager) getLayoutManager()).setUniformCells(uniformCells);
	}

	public int getStripOffset() {
		return stripOffset;
	}

	public void setStripOffset(@DimenRes int stripOffset) {
		this.stripOffset = getResources().getDimensionPixelSize(stripOffset);
	}

	public static interface OnFocusedPositionChangedListener {
		void onFocusedPositionChanged ( int previousFocusedPosition, int newFocusedPosition );
	}

	public void remeasureItemWidth() {
		((DeweyLayoutManager) getLayoutManager()).updateForcedCellWidth(this);
		forceLayout();
		requestLayout();
	}
}
