package com.tippingcanoe.dewey;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

public class DeweyDecorator extends RecyclerView.ItemDecoration {
	Dewey dewey;
	RecyclerView.Adapter adapter;

	RecyclerView.ViewHolder headerViewHolder;
	RecyclerView.ViewHolder footerViewHolder;

	View headerView;
	View footerView;

	int headerViewTypeId;
	int footerViewTypeId;

	int footerPos;

	Paint cloakPaint;
	int cloakColor = Color.argb((int)(255f/2f), 0, 0, 0);

	public DeweyDecorator ( Dewey dewey, RecyclerView.Adapter adapter ) {
		this.dewey = dewey;
		this.adapter = adapter;

		setup();
	}

	protected void setup() {
		cloakPaint = new Paint();
		cloakPaint.setColor(cloakColor);

		setupAdapterObserver();
		updateLayout();
	}

	protected void setupAdapterObserver () {
		adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
			@Override
			public void onChanged () {
				super.onChanged();
				updateLayout();
			}

			@Override
			public void onItemRangeChanged ( int positionStart, int itemCount ) {
				super.onItemRangeChanged(positionStart, itemCount);
				updateLayout();
			}

			@Override
			public void onItemRangeInserted ( int positionStart, int itemCount ) {
				super.onItemRangeInserted(positionStart, itemCount);
				updateLayout();
			}

			@Override
			public void onItemRangeRemoved ( int positionStart, int itemCount ) {
				super.onItemRangeRemoved(positionStart, itemCount);
				updateLayout();
			}

			@Override
			public void onItemRangeMoved ( int fromPosition, int toPosition, int itemCount ) {
				super.onItemRangeMoved(fromPosition, toPosition, itemCount);
				updateLayout();
			}
		});
	}

	@SuppressWarnings("unchecked")
	protected void updateLayout () {
		if ( adapter.getItemCount() > 3 ) {
			int newHeaderViewTypeId = adapter.getItemViewType(0);
			if ( headerView == null || headerViewTypeId != newHeaderViewTypeId ) {
				headerViewTypeId = newHeaderViewTypeId;

				RecyclerView.ViewHolder recycledViewHolder = dewey.getRecycledViewPool().getRecycledView(headerViewTypeId);
				if (recycledViewHolder != null) {
					headerViewHolder = recycledViewHolder;
				} else {
					headerViewHolder = adapter.createViewHolder(dewey, headerViewTypeId);
				}

				adapter.onBindViewHolder(headerViewHolder, 0);
				headerView = headerViewHolder.itemView;

				int widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
				int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);

				headerView.measure(widthSpec, heightSpec);
				headerView.layout(0, 0, headerView.getMeasuredWidth(), headerView.getMeasuredHeight());
			}

			footerPos = adapter.getItemCount() - 1;

			int newFooterViewTypeId = adapter.getItemViewType(footerPos);
			if ( footerView == null || footerViewTypeId != newFooterViewTypeId ) {
				footerViewTypeId = newFooterViewTypeId;

				RecyclerView.ViewHolder recycledViewHolder = dewey.getRecycledViewPool().getRecycledView(footerViewTypeId);
				if (recycledViewHolder != null) {
					footerViewHolder = recycledViewHolder;
				} else {
					footerViewHolder = adapter.createViewHolder(dewey, footerViewTypeId);
				}

				adapter.onBindViewHolder(footerViewHolder, footerPos);
				footerView = footerViewHolder.itemView;

				int widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
				int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);

				footerView.measure(widthSpec, heightSpec);
				footerView.layout(0, 0, footerView.getMeasuredWidth(), footerView.getMeasuredHeight());
			}
		} else {
			headerView = null;
			footerView = null;

			dewey.getRecycledViewPool().putRecycledView(headerViewHolder);
			dewey.getRecycledViewPool().putRecycledView(footerViewHolder);

			headerViewHolder = null;
			footerViewHolder = null;

			headerViewTypeId = -1;
			footerViewTypeId = -1;

			footerPos = -1;
		}
	}

	/**
	 * Draw any appropriate decorations into the Canvas supplied to the RecyclerView. Any content drawn by this method
	 * will be drawn after the item views are drawn and will thus appear over the views.
	 *
	 * @param c      Canvas to draw into
	 * @param parent RecyclerView this ItemDecoration is drawing into
	 * @param state  The current state of RecyclerView.
	 */
	@Override
	public void onDrawOver ( Canvas c, RecyclerView parent, RecyclerView.State state ) {
		c.save();

		if (headerView != null) {
			if ( parent.getChildPosition(parent.getChildAt(0)) == 0 ) {
				cloakPaint.setAlpha(mixAlpha(Color.alpha(cloakColor), getPercentageOfViewHidden(parent.getChildAt(0), parent)));
			} else {
				cloakPaint.setAlpha(Color.alpha(cloakColor));
			}

			c.drawRect(0, 0, headerView.getMeasuredWidth(), headerView.getMeasuredHeight(), cloakPaint);
			headerView.draw(c);
		}

		if ( footerView != null ) {
			c.translate(parent.getMeasuredWidth() - footerView.getMeasuredWidth(), 0);

			if ( parent.getChildPosition(parent.getChildAt(parent.getChildCount() - 1)) == footerPos ) {
				cloakPaint.setAlpha(mixAlpha(Color.alpha(cloakColor), getPercentageOfViewHidden(parent.getChildAt(parent.getChildCount() - 1), parent)));
			} else {
				cloakPaint.setAlpha(Color.alpha(cloakColor));
			}

			c.drawRect(0, 0, footerView.getMeasuredWidth(), footerView.getMeasuredHeight(), cloakPaint);
			footerView.draw(c);
		}

		c.restore();
	}

	public float getPercentageOfViewHidden ( View view, RecyclerView parent ) {
		float fraction = 0f;

		if ( view.getLeft() < 0 ) {
			fraction = ((float) Math.abs(view.getLeft())) / ((float) view.getMeasuredWidth());
		} else if ( view.getRight() > parent.getMeasuredWidth() ) {
			fraction = ((float) (Math.abs(view.getRight() - parent.getMeasuredWidth()))) / ((float) view.getMeasuredWidth());
		}

		return fraction * 100f;
	}

	public int mixAlpha ( int staticAlpha, float percentage ) {
		return (int) (((float) staticAlpha) * (percentage / 100f));
	}
}
