package de.hsbremen.android.dribl.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import de.hsbremen.android.dribl.R;

/**
 * This is a ListAdapter that displays icons alongside with strings
 * 
 * @author jannes
 */
public class IconTextArrayAdapter extends BaseAdapter {

	private final LayoutInflater mInflater;
	private final String[] mTexts;
	private final int[] mImages;

	private class ViewHolder {
		ImageView imageView;
		TextView textView;
	}

	public IconTextArrayAdapter(Context context, int[] images, String[] texts) {
		mInflater = LayoutInflater.from(context);
		if (images.length != texts.length) {
			throw new IllegalArgumentException("The images and the texts have to be of the same length");
		}
		mImages = images;
		mTexts = texts;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			// Inflate a row, but don't attach it to the parent
			convertView = mInflater.inflate(R.layout.row_icontext, parent, false);
			// Store its childview references in a ViewHolder
			holder = new ViewHolder();
			holder.textView = (TextView) convertView.findViewById(R.id.label);
			holder.imageView = (ImageView) convertView.findViewById(R.id.icon);
			convertView.setTag(holder);
		} else {
			// Retrieve a stored ViewHolder because this thing got recycled
			holder = (ViewHolder) convertView.getTag();
		}

		// Update the contents
		holder.imageView.setImageResource(mImages[position]);
		holder.textView.setText(mTexts[position]);

		return convertView;
	}

	@Override
	public int getCount() {
		return mTexts.length;
	}

	@Override
	public String getItem(int position) {
		throw new UnsupportedOperationException("getItem is not supported yet");
//		return mStrings[position];
	}

	@Override
	public long getItemId(int position) {
		// Let's use the position as an item's ID
		return position;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

}