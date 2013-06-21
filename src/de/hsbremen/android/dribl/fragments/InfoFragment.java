package de.hsbremen.android.dribl.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import de.hsbremen.android.dribl.R;
import de.hsbremen.android.dribl.adapter.InfoListAdapter;

public class InfoFragment extends Fragment {
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_info, container, false);

		
		ListView detailList = (ListView) view.findViewById(R.id.detailList);
		String [] detail_items = getResources().getStringArray(R.array.detail_list);
//		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.details_list_item, detail_items);

//		ListView detailList = (ListView) view.findViewById(R.id.detailList);
//		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.details_list_item, detail_items) {
//			// disable user-actions on list
//			@Override
//			public boolean isEnabled(int position) {
//				return false;
//			}
//		};
//		detailList.setAdapter(adapter);
		
		InfoListAdapter infoList = new InfoListAdapter(getActivity(), detail_items) {
			public boolean isEnabled(int position) {
				return false;
			};
		};
		detailList.setAdapter(infoList);
		
		return view;
	}		
	
}
