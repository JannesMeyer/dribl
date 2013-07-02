package de.hsbremen.android.dribl.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.viewpager.extensions.PagerSlidingTabStrip;

import de.hsbremen.android.dribl.R;
import de.hsbremen.android.dribl.provider.DribbbleContract;

public class StreamPagerFragment extends Fragment {

    /**
     * The content uris to show
     */
	private static final Uri[] sContentUris = {
		DribbbleContract.Image.STREAM_POPULAR_URI,
		DribbbleContract.Image.STREAM_EVERYONE_URI,
		DribbbleContract.Image.STREAM_DEBUTS_URI
	};
	private static final String[] sTitles = {
		"Popular",
		"Everyone",
		"Debuts"
	};
    
    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager mPager;
    
    private PagerSlidingTabStrip mTabs;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private PagerAdapter mPagerAdapter;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_stream_viewpager, container, false);
        
        mPagerAdapter = new ScreenSlidePagerAdapter(getFragmentManager());
        
        mPager = (ViewPager) view.findViewById(R.id.pager);
        mPager.setAdapter(mPagerAdapter);
        
        mTabs = (PagerSlidingTabStrip) view.findViewById(R.id.tabs);
        mTabs.setViewPager(mPager);
        
        return view;
	}
	
    /**
     * A simple pager adapter that represents 3 objects, in sequence.
     */
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
    	
    	/**
    	 * Construct a ScreenSlidePagerAdapter
    	 * @param fm
    	 */
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
        	// Arguments
        	Bundle args = new Bundle();
        	args.putString("title", "Test");
    		args.putParcelable(StreamFragment.ARGUMENT_CONTENT_URI, sContentUris[position]);
    		
        	// New fragment with arguments
        	Fragment fragment = new StreamFragment();
    		fragment.setArguments(args);
    		
    		return fragment;
        }

        @Override
        public CharSequence getPageTitle(int position) {
        	return sTitles[position];
        }
        
        @Override
        public int getCount() {
            return sContentUris.length;
        }
    }
	
}
