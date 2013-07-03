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

	private String mActionBarTitle;
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
        mActionBarTitle = null;
        
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
        
        
        if (savedInstanceState == null) {
        	// Set inital fragment
            selectItem(0);
            mDrawerList.setItemChecked(0, true);
        } else {
        	// Restore state
        	setActionBarTitle(savedInstanceState.getString("title_override", null));
        }
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
    	outState.putString("title_override", mActionBarTitle);
    }
    
    private void setActionBarTitle(String title) {
    	mActionBarTitle = title;
    	if (title == null) {
    		// Set default ActionBar title
    		getActionBar().setTitle(R.string.app_name);
    	} else {
    		getActionBar().setTitle(title);
    	}
    }
    
    private void setActionBarTitle(int titleResourceId) {
    	setActionBarTitle(getResources().getString(titleResourceId));
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        
        // SearchView
        mSearchItem = menu.findItem(R.id.menu_search);
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
			setActionBarTitle("Search: " + query);
			
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
    	
    	// Reset the ActionBar title
    	setActionBarTitle(null);
    	
    	// Create the new fragment that should be opened
    	Fragment newFragment;
    	if (position == 0) {
    		// Streams
    		newFragment = new StreamPagerFragment();
    	} else if(position == 1) {
    		// My Collection
    		setActionBarTitle(R.string.action_mycollection);
    		newFragment = StreamFragment.newInstance(DribbbleContract.Image.COLLECTION_URI);
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
