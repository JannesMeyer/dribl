package de.hsbremen.android.dribl.views;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class StretchableImageView extends ImageView {

    public StretchableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override 
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    	// Default aspect ratio
    	float aspectRatio = 4.0f / 3.0f;

    	Drawable drawable = getDrawable();
		if (drawable != null) {
			// Calculate the aspect ratio if the drawable has an intrinsic size
			int intrinsicWidth = drawable.getIntrinsicWidth();
			int intrinsicHeight = drawable.getIntrinsicHeight();
			if (intrinsicWidth != -1 && intrinsicHeight != -1) {
				// Ceil not round (to avoid thin borders along the edges)
				aspectRatio = (float)intrinsicWidth / (float)intrinsicHeight;
			}
		}
		
		int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = (int) Math.ceil(width / aspectRatio);
        setMeasuredDimension(width, height);
    }

}