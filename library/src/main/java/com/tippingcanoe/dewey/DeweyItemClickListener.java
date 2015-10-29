package com.tippingcanoe.dewey;

import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

class DeweyItemClickListener implements RecyclerView.OnItemTouchListener {
	Dewey dewey;
	DeweyDecorator deweyDecorator;
	GestureDetector gestureDetector;

	public DeweyItemClickListener ( Dewey dewey, DeweyDecorator deweyDecorator ) {
		this.dewey = dewey;
		this.deweyDecorator = deweyDecorator;
		this.gestureDetector = new GestureDetector(dewey.getContext(), new SingleTapDetector(dewey, deweyDecorator));
	}

	@Override
	public boolean onInterceptTouchEvent ( RecyclerView rv, MotionEvent e ) {
		return gestureDetector.onTouchEvent(e);
	}

	@Override
	public void onTouchEvent ( RecyclerView rv, MotionEvent e ) {

	}

	@Override
	public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

	}

	protected class SingleTapDetector extends GestureDetector.SimpleOnGestureListener {
		Dewey dewey;
		DeweyDecorator deweyDecorator;

		public SingleTapDetector ( Dewey dewey, DeweyDecorator deweyDecorator ) {
			this.dewey = dewey;
			this.deweyDecorator = deweyDecorator;
		}

		@Override
		public boolean onSingleTapUp ( MotionEvent e ) {
			return findPositionUnder(e.getX()) != null;
		}

		@Override
		public boolean onSingleTapConfirmed ( MotionEvent e ) {
			Integer tappedPosition = findPositionUnder(e.getX());

			if (tappedPosition != null) {
				dewey.setFocusedPosition(tappedPosition, true);
			}

			return tappedPosition != null;
		}

		protected Integer findPositionUnder ( float x ) {
			if (deweyDecorator.getHeaderView() != null) {
				if (x < deweyDecorator.getHeaderView().getMeasuredWidth()) {
					return 0;
				}
			}

			if (deweyDecorator.getFooterView() != null) {
				if (x > (dewey.getMeasuredWidth() - deweyDecorator.getFooterView().getMeasuredWidth()) && x < dewey.getMeasuredWidth()) {
					return deweyDecorator.getFooterPos();
				}
			}

			for (int i = 0; i < dewey.getChildCount(); i++) {
				View child = dewey.getChildAt(i);
				if (x >= child.getLeft() && x <= child.getRight()) {
					return dewey.getChildPosition(child);
				}
			}

			return null;
		}
	}
}
