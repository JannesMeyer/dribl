package de.hsbremen.android.dribl.fragments;

import de.hsbremen.android.dribl.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class StreamFragment extends Fragment {

	FragmentTabHost mTabHost;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		mTabHost = new FragmentTabHost(getActivity());
		mTabHost.setup(getActivity(), getChildFragmentManager(), R.id.content_frame);
		
		// add tabs
		mTabHost.addTab(mTabHost.newTabSpec("popular").setIndicator("Popular"), ApiTestFragment.class, null);
		mTabHost.addTab(mTabHost.newTabSpec("everyone").setIndicator("Everyone"), ContentFragment.class, null);
		mTabHost.addTab(mTabHost.newTabSpec("debuts").setIndicator("Debuts"), ContentFragment.class, null);
		
		return mTabHost;
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		mTabHost = null;
	}
	
}
