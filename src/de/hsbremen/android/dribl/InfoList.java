package de.hsbremen.android.dribl;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class InfoList extends ArrayAdapter<String> {
	
	private final Context context;
	private final String[] values;

	public InfoList(Context context, String[] values) {
		super(context, R.layout.details_list_item, values);
		this.context = context;
		this.values = values;
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.details_list_item, parent, false);
		TextView textView = (TextView) rowView.findViewById(R.id.detail_label);
		ImageView imageView = (ImageView) rowView.findViewById(R.id.detail_icon);
		textView.setText(values[position]);
		String iconString = "R.drawable.icon_" + values[position].toString();
		Uri icon = Uri.parse(iconString);
		imageView.setImageURI(icon);
		
		
		return rowView;
	}

}
