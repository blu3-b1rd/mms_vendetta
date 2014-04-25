package com.mms.view;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ScrollView;

import com.mms.vendetta.R;

public class TrackableScrollView extends ScrollView {
	
	public interface OnTrackedViewVisibilityFactorChangeListener {
		public void onTrackedViewVisibilityFactorChanged(float factor);
	}
	
	private View baseView;
	private OnTrackedViewVisibilityFactorChangeListener listener;

	public TrackableScrollView(Context context) {
		this(context, null);
	}
	
	public TrackableScrollView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public TrackableScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		this.baseView = this.getChildAt(0)
				.findViewById(R.id.img_band);
	}
	
	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		
		Rect bounds = new Rect();
		this.baseView.getDrawingRect(bounds);
		int delta = bounds.bottom - this.getScrollY();
		
		if(delta >= bounds.bottom){
			this.updateFactor(1.0f);
		} else if(delta <= 0){
			this.updateFactor(0.0f);
		} else {
			float factor = (delta * 1.0f) / bounds.bottom;
			this.updateFactor(factor);
		}
		
	}
	
	private void updateFactor(float newFactor){
		if(this.listener != null){
			this.listener.onTrackedViewVisibilityFactorChanged(
					1.0f - newFactor);
		}
	}
	
	public void setOnTrackedViewVisibilityFactorChangeListener(
			OnTrackedViewVisibilityFactorChangeListener listener){
		this.listener = listener;
	}

}
