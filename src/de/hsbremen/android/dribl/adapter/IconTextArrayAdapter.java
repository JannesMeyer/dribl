package de.hsbremen.android.dribl.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import de.hsbremen.android.dribl.R;

/**
 * This is a generic ListAdapter that displays icons alongside with text labels
 * 
 * @author jannes
 */
public class IconTextArrayAdapter extends BaseAdapter {

	private final LayoutInflater mInflater;
	private final int mRowResourceId;
	private final String[] mLabels;
	private final int[] mImages;

	private class ViewHolder {
		TextView textView;
	}

	public IconTextArrayAdapter(Context context, int[] images, String[] labels, int rowResourceId) {
		mInflater = LayoutInflater.from(context);
		if (images.length != labels.length) {
			throw new IllegalArgumentException("The images and the labels have to be of the same length");
		}
		mRowResourceId = rowResourceId;
		mImages = images;
		mLabels = labels;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			// Inflate a row, but don't attach it to the parent
			convertView = mInflater.inflate(mRowResourceId, parent, false);
			// Store its childview references in a ViewHolder
			holder = new ViewHolder();
			holder.textView = (TextView) convertView.findViewById(R.id.label);
			convertView.setTag(holder);
		} else {
			// Retrieve a stored ViewHolder because this thing got recycled
			holder = (ViewHolder) convertView.getTag();
		}

		// Update the contents
		holder.textView.setCompoundDrawablesWithIntrinsicBounds(mImages[position], 0, 0, 0);
		holder.textView.setText(mLabels[position]);

		return convertView;
	}

	@Override
	public int getCount() {
		return mLabels.length;
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