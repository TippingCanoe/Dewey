package com.tippingcanoe.dewey;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

class DeweyLayoutManager extends LinearLayoutManager {
	/**
	 * Creates a vertical LinearLayoutManager
	 *
	 * @param context Current context, will be used to access resources.
	 */
	public DeweyLayoutManager ( Context context ) {
		super(context);
	}

	/**
	 * @param context       Current context, will be used to access resources.
	 * @param orientation   Layout orientation. Should be {@link #HORIZONTAL} or {@link #VERTICAL}.
	 * @param reverseLayout When set to true, layouts from end to start.
	 */
	public DeweyLayoutManager ( Context context, int orientation, boolean reverseLayout ) {
		super(context, orientation, reverseLayout);
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
			int height = measureFirstChildHeight(recycler);

			if (height != 0) {
				heightSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);
			}
		}

		super.onMeasure(recycler, state, widthSpec, heightSpec);
	}

	protected int measureFirstChildHeight ( RecyclerView.Recycler recycler ) {
		View view = recycler.getViewForPosition(0);

		if (view != null) {
			RecyclerView.LayoutParams p = (RecyclerView.LayoutParams) view.getLayoutParams();
			int childHeightSpec = ViewGroup.getChildMeasureSpec(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), getPaddingTop() + getPaddingBottom(), p.height);
			view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), childHeightSpec);

			recycler.recycleView(view);

			return view.getMeasuredHeight() + p.bottomMargin + p.topMargin;
		}

		return 0;
	}
}
