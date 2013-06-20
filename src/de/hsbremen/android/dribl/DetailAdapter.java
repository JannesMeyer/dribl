//package de.hsbremen.android.dribl;
//
//import java.util.ArrayList;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//public class DetailAdapter extends BaseAdapter {
//
//	private ArrayList<DetailHelper> details;
//	Context context;
//	
//	public DetailAdapter(ArrayList<DetailHelper> _detail, Context _context) {
//		details = _detail;
//		context = _context;
//	}
//	
//	@Override
//	public int getCount() {
//		return details.size();
//	}
//
//	@Override
//	public Object getItem(int position) {
//		return details.get(position);
//	}
//
//	@Override
//	public long getItemId(int position) {
//		return position;
//	}
//
//	@Override
//	public View getView(int position, View convertView, ViewGroup parent) {
//		View view = convertView;
//		if (view == null) {
//			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//			view = inflater.inflate(R.layout.details_list_item, null);
//		}
//		
//		ImageView image = (ImageView) view.findViewById(R.id.detail_icon);
//		TextView amount = (TextView) view.findViewById(R.id.details_amount);
//		
//		DetailHelper detail = details.get(position);
//		
//		return view;
//	}
//
//}
