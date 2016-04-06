package com.tippingcanoe.dewey;

import android.content.Context;
import android.graphics.PointF;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

class DeweyLayoutManager extends LinearLayoutManager {
	int uniformCellWidth = 0;
	int forcedCellWidth = 0;
	int animationDuration = 0;

	public DeweyLayoutManager(Context context, int uniformCellWidth, int animationDuration) {
		super(context);
		this.uniformCellWidth = uniformCellWidth;
		this.animationDuration = animationDuration;
		updateUniformCellWidth();
	}

	public DeweyLayoutManager(Context context, int orientation, boolean reverseLayout, int uniformCellWidth, int animationDuration) {
		super(context, orientation, reverseLayout);
		this.uniformCellWidth = uniformCellWidth;
		this.animationDuration = animationDuration;
		updateUniformCellWidth();
	}

	public DeweyLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes, int uniformCellWidth, int animationDuration) {
		super(context, attrs, defStyleAttr, defStyleRes);
		this.uniformCellWidth = uniformCellWidth;
		this.animationDuration = animationDuration;
		updateUniformCellWidth();
	}

	@Override
	public void onMeasure ( RecyclerView.Recycler recycler, RecyclerView.State state, int widthSpec, int heightSpec ) {
		super.onMeasure(recycler, state, widthSpec, heightSpec);

		updateUniformCellWidth();
	}

	@Override
	public RecyclerView.LayoutParams generateDefaultLayoutParams() {
		return updateLayoutParamsForUniformWidthIfNeeded(super.generateDefaultLayoutParams());
	}

	@Override
	public RecyclerView.LayoutParams generateLayoutParams(ViewGroup.LayoutParams lp) {
		return updateLayoutParamsForUniformWidthIfNeeded(super.generateLayoutParams(lp));
	}


	@Override
	public RecyclerView.LayoutParams generateLayoutParams(Context c, AttributeSet attrs) {
		return updateLayoutParamsForUniformWidthIfNeeded(super.generateLayoutParams(c, attrs));
	}

	@Override
	public boolean checkLayoutParams(RecyclerView.LayoutParams lp) {
		return super.checkLayoutParams(lp) && (forcedCellWidth == 0 || lp.width == forcedCellWidth);
	}

	protected RecyclerView.LayoutParams updateLayoutParamsForUniformWidthIfNeeded ( RecyclerView.LayoutParams layoutParams ) {
		if ( forcedCellWidth != 0 ) {
			layoutParams.width = forcedCellWidth;
		}

		return layoutParams;
	}

	public boolean areCellsUniform() {
		return uniformCellWidth > 0;
	}

	public int getUniformCellWidth() {
		return uniformCellWidth;
	}

	protected void updateUniformCellWidth() {
		if ( areCellsUniform() ) {
			if ( (uniformCellWidth * getItemCount()) < getWidth() ) {
				forcedCellWidth = (int) ((float) getWidth() / (float) getItemCount());
			} else {
				forcedCellWidth = uniformCellWidth;
			}
		}
	}

	public void setUniformCellWidth(int uniformCellWidth) {
		this.uniformCellWidth = uniformCellWidth;
		updateUniformCellWidth();
	}

	public int getForcedCellWidth() {
		return forcedCellWidth;
	}

	@Override
	public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
		if ( areCellsUniform() ) {
			View firstVisibleChild = recyclerView.getChildAt(0);
			int distanceInPixels = 0;

			if ( firstVisibleChild != null ) {
				int currentPosition = recyclerView.getChildLayoutPosition(firstVisibleChild);
				distanceInPixels = Math.abs((currentPosition - position) * getUniformCellWidth());
				if (distanceInPixels == 0) {
					distanceInPixels = (int) Math.abs(ViewCompat.getY(firstVisibleChild));
				}
			}

			if ( distanceInPixels == 0 ) {
				super.smoothScrollToPosition(recyclerView, state, position);
			} else {
				DurationSmoothScroller smoothScroller = new DurationSmoothScroller(recyclerView.getContext(), distanceInPixels, animationDuration);
				smoothScroller.setTargetPosition(position);
				startSmoothScroll(smoothScroller);
			}
		} else {
			super.smoothScrollToPosition(recyclerView, state, position);
		}
	}

	class DurationSmoothScroller extends LinearSmoothScroller {
		private static final int TARGET_SEEK_SCROLL_DISTANCE_PX = 10000;
		private final float MILLISECONDS_PER_PX;
		private final float distanceInPixels;
		private final float duration;

		public DurationSmoothScroller(Context context, int distanceInPixels, int duration) {
			super(context);
			this.distanceInPixels = distanceInPixels;
			duration = 1000;
			MILLISECONDS_PER_PX = calculateSpeedPerPixel(context.getResources().getDisplayMetrics());
			this.duration = distanceInPixels < TARGET_SEEK_SCROLL_DISTANCE_PX ?
					(int) (Math.abs(distanceInPixels) * MILLISECONDS_PER_PX) : duration;
		}

		@Override
		public PointF computeScrollVectorForPosition(int targetPosition) {
			return DeweyLayoutManager.this.computeScrollVectorForPosition(targetPosition);
		}

		@Override
		protected int calculateTimeForScrolling(int dx) {
			float proportion = (float) dx / distanceInPixels;
			return (int) (duration * proportion);
		}
	}
}
