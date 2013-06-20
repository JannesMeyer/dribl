package de.hsbremen.android.dribl.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import de.hsbremen.android.dribl.R;

public class InfoFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_info, container, false);

		ListView detailList = (ListView) view.findViewById(R.id.detailList);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.detail_list)) {
			
			// disable user-actions on list
			@Override
			public boolean isEnabled(int position) {
				return false;
			}
		};
				
		detailList.setAdapter(adapter);
		
		return view;
	}		
	
}
