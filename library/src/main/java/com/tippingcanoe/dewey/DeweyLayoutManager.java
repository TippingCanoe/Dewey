package com.tippingcanoe.dewey;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

class DeweyLayoutManager extends LinearLayoutManager {
	boolean uniformCells = false;
	int uniformCellWidth = 0;
	/**
	 * Creates a vertical LinearLayoutManager
	 *
	 * @param context Current context, will be used to access resources.
	 */
	public DeweyLayoutManager(Context context, boolean uniformCells) {
		super(context);
		this.uniformCells = uniformCells;
	}

	/**
	 * @param context       Current context, will be used to access resources.
	 * @param orientation   Layout orientation. Should be {@link #HORIZONTAL} or {@link
	 *                      #VERTICAL}.
	 * @param reverseLayout When set to true, layouts from end to start.
	 */
	public DeweyLayoutManager(Context context, int orientation, boolean reverseLayout, boolean uniformCells) {
		super(context, orientation, reverseLayout);
		this.uniformCells = uniformCells;
	}

	/**
	 * Constructor used when layout manager is set in XML by RecyclerView attribute
	 * "layoutManager". Defaults to vertical orientation.
	 *
	 * @param context
	 * @param attrs
	 * @param defStyleAttr
	 * @param defStyleRes
	 * @attr ref android.support.v7.recyclerview.R.styleable#RecyclerView_android_orientation
	 * @attr ref android.support.v7.recyclerview.R.styleable#RecyclerView_reverseLayout
	 * @attr ref android.support.v7.recyclerview.R.styleable#RecyclerView_stackFromEnd
	 */
	public DeweyLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes, boolean uniformCells) {
		super(context, attrs, defStyleAttr, defStyleRes);
		this.uniformCells = uniformCells;
	}

	/**
	 * Measure the attached RecyclerView. Implementations must call {@link #setMeasuredDimension(int, int)} before
	 * returning.
	 *
	 * <p>The default implementation will handle EXACTLY measurements and respect the minimum width and height
	 * properties of the host RecyclerView if measured as UNSPECIFIED. AT_MOST measurements will be treated as EXACTLY
	 * and the RecyclerView will consume all available space.</p>
	 *
	 * @param recycler   Recycler
	 * @param state      Transient state of RecyclerView
	 * @param widthSpec  Width {@link android.view.View.MeasureSpec}
	 * @param heightSpec Height {@link android.view.View.MeasureSpec}
	 */
	@Override
	public void onMeasure ( RecyclerView.Recycler recycler, RecyclerView.State state, int widthSpec, int heightSpec ) {
		if ( View.MeasureSpec.getMode(heightSpec) != View.MeasureSpec.EXACTLY ) {
			int height = measureFirstChildHeightAndObtainWidth(recycler);
			heightSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);
		}

		super.onMeasure(recycler, state, widthSpec, heightSpec);
	}

	protected int measureFirstChildHeightAndObtainWidth(RecyclerView.Recycler recycler) {
		View view = getItemCount() == 0 ? null : recycler.getViewForPosition(0);

		if (view != null) {
			RecyclerView.LayoutParams p = (RecyclerView.LayoutParams) view.getLayoutParams();

			if ( uniformCells ) {
				if (p.width != RecyclerView.LayoutParams.MATCH_PARENT && p.width != RecyclerView.LayoutParams.WRAP_CONTENT) {
					uniformCellWidth = p.width;
				}
			}

			int childHeightSpec = ViewGroup.getChildMeasureSpec(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), getPaddingTop() + getPaddingBottom(), p.height);
			view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), childHeightSpec);

			int height = view.getMeasuredHeight() + p.bottomMargin + p.topMargin;

			detachView(view);

			return height;
		}

		return 0;
	}

	/**
	 * {@inheritDoc}
	 */
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

	protected RecyclerView.LayoutParams updateLayoutParamsForUniformWidthIfNeeded ( RecyclerView.LayoutParams layoutParams ) {
		if ( uniformCells && uniformCellWidth != 0 && (uniformCellWidth * getItemCount()) < getWidth() ) {
			layoutParams.width = (int) ((float) getWidth() / (float) getItemCount());
		}

		return layoutParams;
	}

	public boolean areCellsUniform() {
		return uniformCells;
	}

	public void setUniformCells(boolean uniformCells) {
		this.uniformCells = uniformCells;
	}
}
