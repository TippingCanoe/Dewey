package com.tippingcanoe.dewey;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

class DeweyDecorator extends RecyclerView.ItemDecoration {
	final static int NO_POSITION = -1;
	final static int NO_VIEW_TYPE = -1;

	Dewey dewey;
	RecyclerView.Adapter adapter;

	RecyclerView.ViewHolder headerViewHolder;
	RecyclerView.ViewHolder footerViewHolder;

	View headerView;
	View footerView;

	int headerViewTypeId;
	int footerViewTypeId;

	int footerPos;

	int framesPerMs = 90 * 1000;
	int framesRemaining = 0;
	int totalFrames = 0;

	Paint cloakPaint;
	Paint stripPaint;

	int animatingFromPosition = NO_POSITION;

	float animatingStripXOffset = 0f;
	float animatingStripXGoal = 0f;
	float animatingStripXOffsetPerFrame = 0f;

	float animatingStripWidthOffset = 0f;
	float animatingStripWidthGoal = 0f;
	float animatingStripWidthOffsetPerFrame = 0f;

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
		setupCloak();
		setupStrip();

		setupAdapterObserver();
		updateLayout();

		deweyItemClickListener = new DeweyItemClickListener(dewey, this);
		dewey.addOnItemTouchListener(deweyItemClickListener);

		dewey.addOnScrollListener(new RecyclerView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
				if (newState == RecyclerView.SCROLL_STATE_DRAGGING || newState == RecyclerView.SCROLL_STATE_SETTLING) {
					resetAnimationProperties();
				}

				super.onScrollStateChanged(recyclerView, newState);
			}
		});
	}

	protected void setupAdapterObserver () {
		adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
			@Override
			public void onChanged() {
				super.onChanged();
				updateLayout();
			}

			@Override
			public void onItemRangeChanged(int positionStart, int itemCount) {
				super.onItemRangeChanged(positionStart, itemCount);
				updateLayout();
			}

			@Override
			public void onItemRangeInserted(int positionStart, int itemCount) {
				super.onItemRangeInserted(positionStart, itemCount);
				updateLayout();
			}

			@Override
			public void onItemRangeRemoved(int positionStart, int itemCount) {
				super.onItemRangeRemoved(positionStart, itemCount);
				updateLayout();
			}

			@Override
			public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
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

				measureView(headerView);

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

				measureView(footerView);

				footerView.layout(0, 0, footerView.getMeasuredWidth(), footerView.getMeasuredHeight());
			}
		} else {
			headerView = null;
			footerView = null;

			if ( dewey != null && dewey.getRecycledViewPool() != null ) {
				if (headerViewHolder != null) {
					dewey.getRecycledViewPool().putRecycledView(headerViewHolder);
				}

				if (footerViewHolder != null) {
					dewey.getRecycledViewPool().putRecycledView(footerViewHolder);
				}
			}

			headerViewHolder = null;
			footerViewHolder = null;

			headerViewTypeId = NO_VIEW_TYPE;
			footerViewTypeId = NO_VIEW_TYPE;

			footerPos = NO_POSITION;
		}
	}

	protected void measureView ( View view ) {
		int width = getWidthForView(view);

		int widthSpec;
		if ( width != 0 ) {
			widthSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
		} else {
			widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		}

		int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);

		view.measure(widthSpec, heightSpec);
	}

	protected int getWidthForView ( View view ) {
		if ( view.getLayoutParams().width != ViewGroup.LayoutParams.MATCH_PARENT && view.getLayoutParams().width != ViewGroup.LayoutParams.WRAP_CONTENT ) {
			return view.getLayoutParams().width;
		}

		return 0;
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

		int dX;
		int dY = 0;

		int firstVisiblePos = parent.getChildLayoutPosition(parent.getChildAt(0));
		int lastVisiblePos = parent.getChildLayoutPosition(parent.getChildAt(parent.getChildCount() - 1));

		boolean drawStripUnderHeaderAndFooter = dewey.getFocusedPosition() != 0 && dewey.getFocusedPosition() != footerPos;

		if (drawStripUnderHeaderAndFooter) {
			drawStrip(parent, c, firstVisiblePos, lastVisiblePos);
		}

		// Draw sticky header.
		if (headerView != null) {
			float headerPercentageHidden = getPercentageOfViewHidden(parent.getChildAt(0), parent);

			if (firstVisiblePos == 0) {
				cloakPaint.setAlpha(getMixedCloakAlpha(headerPercentageHidden));
			} else {
				cloakPaint.setAlpha(Color.alpha(dewey.getCloakColor()));
			}

			c.drawRect(0, 0, headerView.getMeasuredWidth(), headerView.getMeasuredHeight(), cloakPaint);
			headerView.draw(c);
		}

		// Draw sticky footer.
		if ( footerView != null ) {
			float footerPercentageHidden = getPercentageOfViewHidden(parent.getChildAt(parent.getChildCount() - 1), parent);

			if (lastVisiblePos == footerPos) {
				cloakPaint.setAlpha(getMixedCloakAlpha(footerPercentageHidden));
			} else {
				cloakPaint.setAlpha(Color.alpha(dewey.getCloakColor()));
			}

			dX = parent.getMeasuredWidth() - footerView.getMeasuredWidth();
			c.translate(dX, dY);

			c.drawRect(0, 0, footerView.getMeasuredWidth(), footerView.getMeasuredHeight(), cloakPaint);
			footerView.draw(c);

			c.translate(-1 * dX, -1 * dY);
		}

		if (!drawStripUnderHeaderAndFooter) {
			drawStrip(parent, c, firstVisiblePos, lastVisiblePos);
		}

		c.restore();

		if (framesRemaining > 0) {
			framesRemaining--;

			animatingStripXOffset = animatingStripXOffset + animatingStripXOffsetPerFrame;
			animatingStripWidthOffset = animatingStripWidthOffset + animatingStripWidthOffsetPerFrame;

			dewey.postInvalidateDelayed((long) (1000f / ((float) framesPerMs / 1000f)));
		} else {
			resetAnimationProperties();
		}
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

	public void setupCloak () {
		cloakPaint = new Paint();
		cloakPaint.setColor(dewey.getCloakColor());
	}

	public void setupStrip () {
		stripPaint = new Paint();
		stripPaint.setColor(dewey.getStripColor());
	}

	public void startAnimation ( int previouslyFocusedPosition, int newFocusedPosition ) {
		resetAnimationProperties();

		if (previouslyFocusedPosition != newFocusedPosition) {
			View previouslyFocusedChild = null;
			View newFocusedChild = null;

			int minVisiblePos = adapter.getItemCount() - 1;
			int maxVisiblePos = 0;

			for (int i = 0; i < dewey.getChildCount(); i++) {
				int childPosition = dewey.getChildLayoutPosition(dewey.getChildAt(i));

				if (childPosition != RecyclerView.NO_POSITION) {
					if (childPosition == previouslyFocusedPosition) {
						previouslyFocusedChild = dewey.getChildAt(i);
					} else if (childPosition == newFocusedPosition) {
						newFocusedChild = dewey.getChildAt(i);
					}

					if (childPosition < minVisiblePos) {
						minVisiblePos = childPosition;
					}

					if (childPosition > maxVisiblePos) {
						maxVisiblePos = childPosition;
					}
				}
			}

			if (newFocusedChild != null) {
				int stripXOrigin;
				int stripWidthOrigin;

				if (previouslyFocusedChild == null) {
					// Previously focused child is off-screen, find the appropriate screen edge and animate from it.
					if (previouslyFocusedPosition < newFocusedPosition) {
						stripXOrigin = 0;
						previouslyFocusedPosition = minVisiblePos;
					} else {
						stripXOrigin = dewey.getMeasuredWidth();
						previouslyFocusedPosition = maxVisiblePos;
					}

					stripWidthOrigin = newFocusedChild.getMeasuredWidth();
				} else {
					// Previously focused child is on-screen, use its values.
					stripXOrigin = previouslyFocusedChild.getLeft();
					stripWidthOrigin = previouslyFocusedChild.getMeasuredWidth();
				}

				int frameCount = (int) ((framesPerMs / 1000f) * (dewey.getAnimationDurationMs() / 1000f));

				animatingStripXGoal = newFocusedChild.getLeft();
				animatingStripWidthGoal = newFocusedChild.getMeasuredWidth();

				int dX = (int) (animatingStripXGoal - stripXOrigin);
				int dWidth = (int) (animatingStripWidthGoal - stripWidthOrigin);

				animatingStripXOffsetPerFrame = (float) dX / (float) frameCount;
				animatingStripWidthOffsetPerFrame = (float) dWidth / (float) frameCount;

				framesRemaining = totalFrames = frameCount;
				animatingFromPosition = previouslyFocusedPosition;

				// Start the animation.
				dewey.invalidate();
			}
		}
	}

	protected void drawStrip ( RecyclerView parent, Canvas c, int firstVisiblePos, int lastVisiblePos ) {
		int dX = 0;
		int dY;
		int dWidth;

		boolean isAnimating = animatingFromPosition != NO_POSITION;

		int stripPosition = isAnimating ? animatingFromPosition : dewey.getFocusedPosition();

		if ((stripPosition == 0 && headerView != null) || (stripPosition == footerPos && footerView != null) || (stripPosition >= firstVisiblePos && stripPosition <= lastVisiblePos)) {
			View currentFocusedChild;

			if (stripPosition == 0) {
				currentFocusedChild = headerView;
			} else if (stripPosition == footerPos && footerView != null) {
				currentFocusedChild = footerView;
				dX = parent.getMeasuredWidth() - footerView.getMeasuredWidth();
			} else {
				currentFocusedChild = parent.getChildAt(stripPosition - firstVisiblePos);
				dX = currentFocusedChild.getLeft();
			}

			if (currentFocusedChild != null) {
				dY = parent.getMeasuredHeight() - dewey.getStripHeight();
				dWidth = currentFocusedChild.getMeasuredWidth();

				if (isAnimating) {
					float percentageComplete = 1f - ((float) framesRemaining) / ((float) totalFrames);

					dX += animatingStripXOffset;
					// Apply interpolation.
					dX = (int) (dX + ((animatingStripXGoal - dX) * dewey.getStripAnimationInterpolator().getInterpolation(percentageComplete)));

					dWidth += animatingStripWidthOffset;
					// Apply interpolation.
					dWidth = (int) (dWidth + ((animatingStripWidthGoal - dWidth) * dewey.getStripAnimationInterpolator().getInterpolation(percentageComplete)));
				}

				c.translate(dX, dY);

				c.drawRect(0, 0, dWidth, dewey.getStripHeight(), stripPaint);

				c.translate(-1 * dX, -1 * dY);
			}
		}
	}

	protected int getMixedCloakAlpha ( float percentageHidden ) {
		int originalAlpha = Color.alpha(dewey.getCloakColor());
		float appliedPercentageHidden = Math.max(dewey.getMinCloakPercentage(), percentageHidden) / 100f;
		int dAlpha = mixAlpha(Color.alpha(originalAlpha), appliedPercentageHidden);

		// Apply interpolation.
		return (int) (dAlpha + ((originalAlpha - dAlpha) * dewey.getCloakCurveInterpolator().getInterpolation(appliedPercentageHidden)));
	}

	protected void resetAnimationProperties () {
		framesRemaining = 0;
		totalFrames = 0;

		animatingFromPosition = NO_POSITION;

		animatingStripXOffset = 0f;
		animatingStripXGoal = 0f;
		animatingStripXOffsetPerFrame = 0f;

		animatingStripWidthOffset = 0f;
		animatingStripWidthGoal = 0f;
		animatingStripWidthOffsetPerFrame = 0f;
	}
}
