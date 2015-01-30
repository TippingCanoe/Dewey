package com.tippingcanoe.dewey;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
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
	float minCloakPercentage = 50f;
	int cloakColor = Color.argb((int) (255f / 2f), 255, 255, 255);

	int stripHeight = 10;
	int stripColor = Color.RED;
	Paint stripPaint;

	DeweyItemClickListener deweyItemClickListener;

	public DeweyDecorator ( Dewey dewey, RecyclerView.Adapter adapter ) {
		this.dewey = dewey;
		this.adapter = adapter;

		setup();
	}

	public DeweyItemClickListener getOnItemTouchListener () {
		return deweyItemClickListener;
	}

	protected void setup() {
		cloakPaint = new Paint();
		cloakPaint.setColor(cloakColor);

		stripPaint = new Paint();
		stripPaint.setColor(stripColor);

		setupAdapterObserver();
		updateLayout();

		deweyItemClickListener = new DeweyItemClickListener(dewey, this);
		dewey.addOnItemTouchListener(deweyItemClickListener);
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
	 * will be drawn before the item views are drawn, and will thus appear underneath the views.
	 *
	 * @param c      Canvas to draw into
	 * @param parent RecyclerView this ItemDecoration is drawing into
	 * @param state  The current state of RecyclerView
	 */
	@Override
	public void onDraw ( Canvas c, RecyclerView parent, RecyclerView.State state ) {
		if (headerView != null) {
			View firstView = parent.getChildAt(0);
			if (parent.getChildPosition(firstView) == 0) {
				firstView.setVisibility(View.INVISIBLE);
			} else {
				firstView.setVisibility(View.VISIBLE);
			}
		}

		if (footerView != null) {
			View lastView = parent.getChildAt(parent.getChildCount() - 1);
			if (parent.getChildPosition(lastView) == footerPos) {
				lastView.setVisibility(View.INVISIBLE);
			} else {
				lastView.setVisibility(View.VISIBLE);
			}
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

		int dX = 0;
		int dY = 0;

		int firstVisiblePos = parent.getChildPosition(parent.getChildAt(0));
		int lastVisiblePos = parent.getChildPosition(parent.getChildAt(parent.getChildCount() - 1));

		// Draw sticky header.
		if (headerView != null) {
			float headerPercentageHidden = getPercentageOfViewHidden(parent.getChildAt(0), parent);

			if (firstVisiblePos == 0) {
				cloakPaint.setAlpha(mixAlpha(Color.alpha(cloakColor), Math.max(minCloakPercentage, headerPercentageHidden)));
			} else {
				cloakPaint.setAlpha(Color.alpha(cloakColor));
			}

			c.drawRect(0, 0, headerView.getMeasuredWidth(), headerView.getMeasuredHeight(), cloakPaint);

			headerView.draw(c);
		}

		// Draw sticky footer.
		if ( footerView != null ) {
			float footerPercentageHidden = getPercentageOfViewHidden(parent.getChildAt(parent.getChildCount() - 1), parent);

			if (lastVisiblePos == footerPos) {
				cloakPaint.setAlpha(mixAlpha(Color.alpha(cloakColor), Math.max(minCloakPercentage, footerPercentageHidden)));
			} else {
				cloakPaint.setAlpha(Color.alpha(cloakColor));
			}

			dX = (-1 * dX) + parent.getMeasuredWidth() - footerView.getMeasuredWidth();
			c.translate(dX, dY);

			c.drawRect(0, 0, footerView.getMeasuredWidth(), footerView.getMeasuredHeight(), cloakPaint);
			footerView.draw(c);
		}

		// Draw indicator strip.
		if ((dewey.getFocusedPosition() == 0 && headerView != null) || (dewey.getFocusedPosition() == footerPos && footerView != null) || (dewey.getFocusedPosition() >= firstVisiblePos && dewey.getFocusedPosition() <= lastVisiblePos)) {
			View currentFocusedChild;

			if (dewey.getFocusedPosition() == 0) {
				currentFocusedChild = headerView;
				dX = (-1 * dX);
			} else if (dewey.getFocusedPosition() == footerPos) {
				currentFocusedChild = footerView;
				dX = (-1 * dX) + (parent.getMeasuredWidth() - footerView.getMeasuredWidth());
			} else {
				currentFocusedChild = parent.getChildAt(dewey.getFocusedPosition() - firstVisiblePos);
				dX = (-1 * dX) + currentFocusedChild.getLeft();
			}

			dY = (-1 * dY) + parent.getMeasuredHeight() - stripHeight;
			c.translate(dX, dY);

			c.drawRect(0, 0, currentFocusedChild.getMeasuredWidth(), stripHeight, stripPaint);
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

	public View getHeaderView () {
		return headerView;
	}

	public View getFooterView () {
		return footerView;
	}

	public int getFooterPos () {
		return footerPos;
	}
}
