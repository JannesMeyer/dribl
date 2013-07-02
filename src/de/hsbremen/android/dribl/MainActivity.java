package de.hsbremen.android.dribl;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import de.hsbremen.android.dribl.fragments.HelloWorldFragment;
import de.hsbremen.android.dribl.fragments.StreamFragment;
import de.hsbremen.android.dribl.fragments.StreamPagerFragment;
import de.hsbremen.android.dribl.provider.DribbbleContract;



public class MainActivity extends FragmentActivity {

	private String mTitleOverride;
	private String[] mListItems;
	private ListView mDrawerList;
	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;
	private MenuItem mSearchItem;
	private SearchView mSearchView;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_main);
        // Enable home button
        getActionBar().setDisplayHomeAsUpEnabled(true);
        
        // Read app title
        mTitleOverride = null;
        
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
    protected void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
    	outState.putString("title_override", mTitleOverride);
    }
    
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
    	super.onRestoreInstanceState(savedInstanceState);
    	mTitleOverride = savedInstanceState.getString("title_override", null);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        
        // SearchView
        mSearchItem = (MenuItem) menu.findItem(R.id.menu_search);
        mSearchView = (SearchView) mSearchItem.getActionView();
        
        // Set hint
        mSearchView.setQueryHint(mSearchItem.getTitle());
        // Setup listener
        mSearchView.setOnQueryTextListener(new SearchListener());
        
        
        return super.onCreateOptionsMenu(menu);
    }
    
    private class SearchListener implements OnQueryTextListener {
    	
		@Override
		public boolean onQueryTextSubmit(String query) {
			// Set the action bar's title
			getActionBar().setTitle("Search: " + query);
			
			// Deselect navigation drawer
	        int selectedItem = mDrawerList.getCheckedItemPosition();
	        if (selectedItem != AdapterView.INVALID_POSITION) {
	        	mDrawerList.setItemChecked(selectedItem, false);        	
	        }
			
			// Hide search bar
			mSearchItem.collapseActionView();
			
	    	// Create the new fragment that should be opened
	    	Fragment newFragment = StreamFragment.newInstance(DribbbleContract.Image.SEARCH_URI, query);
	    	
	    	// Replace the fragment
	    	FragmentManager fm = getSupportFragmentManager();
	    	fm.beginTransaction()
	    	  .replace(R.id.content_frame, newFragment)
	    	  .commit();
			
			// Usually the SearchView would start its associated intent but since
			// this SearchView doesn't actually have its own layout we have to handle it in code
			return true;
		}
		
		@Override
		public boolean onQueryTextChange(String newText) {
			return false;
		}
		
	}
    
    /**
     * Handle the search key on some Android devices
     */
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
    	if (keyCode == KeyEvent.KEYCODE_SEARCH) {
    		mSearchItem.expandActionView();
    		return true;
    	} else {
    		return super.onKeyUp(keyCode, event);
    	}
    }
    
    /**
     * Switches the contents of the FrameLayout
     * 
     * @param position
     */
    private void selectItem(int position) {
    	// Close the drawer
    	mDrawerLayout.closeDrawer(mDrawerList);
    	
    	// Create the new fragment that should be opened
    	Fragment newFragment;
    	if (position == 0) {
    		// Stream
    		newFragment = new StreamPagerFragment();
    	} else if (position == 3) {
    		// Search
    		newFragment = StreamFragment.newInstance(DribbbleContract.Image.SEARCH_URI, "Test");
    	} else {
    		// All else
    		newFragment = new HelloWorldFragment();
    	}
    	
    	// Use the new fragment    	
    	FragmentManager fm = getSupportFragmentManager();
    	fm.beginTransaction()
    	  .replace(R.id.content_frame, newFragment)
    	  .commit();
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
    		// Swap fragment
    		selectItem(position);
    	}
    }
        
}
