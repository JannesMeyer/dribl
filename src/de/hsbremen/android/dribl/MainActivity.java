package de.hsbremen.android.dribl;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import de.hsbremen.android.dribl.fragments.StreamFragment;
import de.hsbremen.android.dribl.fragments.HelloWorldFragment;
import de.hsbremen.android.dribl.provider.DribbbleContract;



public class MainActivity extends FragmentActivity {

	private String[] mListItems;
	private ListView mDrawerList;
	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_main);
        // Enable home button
        getActionBar().setDisplayHomeAsUpEnabled(true);
        
        // Get views
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        
        // NavigationDrawer
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer, R.string.open_drawer, R.string.close_drawer);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        
        // Get all menu items
        mListItems = getResources().getStringArray(R.array.menu_items);
        
        // Set list contents
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.row_navigationdrawer, mListItems);
        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        
        // Set inital fragment
        if (savedInstanceState == null) {
            selectItem(0);
            mDrawerList.setItemChecked(0, true);
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        
        // searchview
//        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        // search dribble 
        // ...
        return super.onCreateOptionsMenu(menu);
    }
    
    
    /**
     * Switches the contents of the FrameLayout
     * 
     * @param position
     */
    private void selectItem(int position) {
    	Fragment newFragment;
    	Bundle args = new Bundle();
    	args.putString("title", mListItems[position]);
    	switch (position) {
	    	case 0:
	    		newFragment = new StreamFragment();
	    		args.putParcelable(StreamFragment.ARGUMENT_CONTENT_URI, DribbbleContract.Image.STREAM_POPULAR_URI);
	    		break;
	    	case 1:
	    		newFragment = new StreamFragment();
	    		args.putParcelable(StreamFragment.ARGUMENT_CONTENT_URI, DribbbleContract.Image.STREAM_EVERYONE_URI);
	    		break;
	    	case 2:
	    		newFragment = new StreamFragment();
	    		args.putParcelable(StreamFragment.ARGUMENT_CONTENT_URI, DribbbleContract.Image.STREAM_POPULAR_URI);
	    		break;
	    	default:
	    		newFragment = new HelloWorldFragment();
    	}
    	
    	// Set arguments
    	newFragment.setArguments(args);
    	
    	// Replace the fragment in a FragmentTransaction    	
    	FragmentManager fm = getSupportFragmentManager();
    	if (fm != null) {
    		fm.beginTransaction().replace(R.id.content_frame, newFragment).commit();
    	} else {
    		Log.d("Dribl", "FragmentManager is null");
    	}
    	
    	// Close drawer on click
    	mDrawerLayout.closeDrawer(mDrawerList);
    }

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}


	/**
	 * Clicked on an item
	 *
	 */
    private class DrawerItemClickListener implements OnItemClickListener {
    	@Override
    	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    		// Ignore this event if the same element is already selected
//    		Log.d("Dribl", "" + mDrawerList.getCheckedItemPosition() + " " + position);
//    		if (mDrawerList.getCheckedItemPosition() == position) {
//    			return;
//    		}
    		// Swap fragment
    		selectItem(position);
    	}
    }
        
}
