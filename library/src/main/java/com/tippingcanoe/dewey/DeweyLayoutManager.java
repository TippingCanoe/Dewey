package com.tippingcanoe.dewey;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

class DeweyLayoutManager extends LinearLayoutManager {
	int uniformCellWidth = 0;
	int forcedCellWidth = 0;

	public DeweyLayoutManager(Context context, int uniformCellWidth) {
		super(context);
		this.uniformCellWidth = uniformCellWidth;
		updateUniformCellWidth();
	}

	public DeweyLayoutManager(Context context, int orientation, boolean reverseLayout, int uniformCellWidth) {
		super(context, orientation, reverseLayout);
		this.uniformCellWidth = uniformCellWidth;
		updateUniformCellWidth();
	}

	public DeweyLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes, int uniformCellWidth) {
		super(context, attrs, defStyleAttr, defStyleRes);
		this.uniformCellWidth = uniformCellWidth;
		updateUniformCellWidth();
	}

	@Override
	public void onMeasure ( RecyclerView.Recycler recycler, RecyclerView.State state, int widthSpec, int heightSpec ) {
		if ( View.MeasureSpec.getMode(heightSpec) != View.MeasureSpec.EXACTLY ) {
			int height = measureFirstChildHeight(recycler);
			heightSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);
		}

		super.onMeasure(recycler, state, widthSpec, heightSpec);

		updateUniformCellWidth();
	}

	protected int measureFirstChildHeight(RecyclerView.Recycler recycler) {
		View view = getItemCount() == 0 ? null : recycler.getViewForPosition(0);

		if (view != null) {
			RecyclerView.LayoutParams p = (RecyclerView.LayoutParams) view.getLayoutParams();

			int childHeightSpec = ViewGroup.getChildMeasureSpec(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), getPaddingTop() + getPaddingBottom(), p.height);
			view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), childHeightSpec);

			int height = view.getMeasuredHeight() + p.bottomMargin + p.topMargin;

			detachView(view);

			return height;
		}

		return 0;
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
		if ( areCellsUniform() && (uniformCellWidth * getItemCount()) < getWidth() ) {
			forcedCellWidth = (int) ((float) getWidth() / (float) getItemCount());
		}
	}

	public void setUniformCellWidth(int uniformCellWidth) {
		this.uniformCellWidth = uniformCellWidth;
		updateUniformCellWidth();
	}

	public int getForcedCellWidth() {
		return forcedCellWidth;
	}
}
