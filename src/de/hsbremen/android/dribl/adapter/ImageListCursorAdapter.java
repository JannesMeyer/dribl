package de.hsbremen.android.dribl.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.webimageloader.ImageLoader;
import com.webimageloader.ext.ImageHelper;

import de.hsbremen.android.dribl.DriblApplication;
import de.hsbremen.android.dribl.R;
import de.hsbremen.android.dribl.provider.DribbbleContract;

public class ImageListCursorAdapter extends CursorAdapter {
	
	private LayoutInflater mInflater;
	private ImageLoader mImageLoader;
	
	class ViewHolder {
	    ImageView image;
	    TextView title;
	}
	
	public ImageListCursorAdapter(Context context, Cursor cursor) {
		super(context, cursor, 0);
		mInflater = LayoutInflater.from(context);
		mImageLoader = ((DriblApplication) context.getApplicationContext()).getImageLoader();
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		// Use a viewholder so we don't have to do findViewById lookups everytime a view gets recycled
		ViewHolder listItem = (ViewHolder) view.getTag();
		
		// Get data
		String imageUrl = cursor.getString(cursor.getColumnIndex(DribbbleContract.Image.IMAGE_URL));
		String title = cursor.getString(cursor.getColumnIndex(DribbbleContract.Image.TITLE));
		
		// This will load the image asynchronously and fade it in nicely
		new ImageHelper(context, mImageLoader)
			.setLoadingResource(R.drawable.placeholder)
			.setFadeIn(true)
			.load(listItem.image, imageUrl);

		// Show the image's title
		listItem.title.setText(title);
	}
 
	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		// Inflate the view, but don't attach it to the parent yet
		View view = mInflater.inflate(R.layout.row_driblimage, parent, false);
		
		// Make a ViewHolder
		ViewHolder holder = new ViewHolder();
		holder.image = (ImageView) view.findViewById(R.id.image);
        holder.title = (TextView) view.findViewById(R.id.title);
        // Store the ViewHolder as a tag
        view.setTag(holder);
		
		return view;
	}
	
	@Override
	public boolean hasStableIds() {
		return true;
	}
}
