package de.hsbremen.android.dribl.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import de.hsbremen.android.dribl.DetailActivity;
import de.hsbremen.android.dribl.R;
import de.hsbremen.android.dribl.adapter.ImageListCursorAdapter;

public class StreamFragment extends Fragment implements LoaderCallbacks<Cursor>, OnItemClickListener {
	
	public static final String ARGUMENT_CONTENT_URI = "content_uri";
	public static final String ARGUMENT_QUERY = "query";
	
	CursorAdapter mAdapter;
	Uri mContentUri;
	String mSearchQuery;

	public static Fragment newInstance(Uri contentUri) {
		return newInstance(contentUri, null);
	}
	
	public static Fragment newInstance(Uri contentUri, String query) {
		// Create arguments
		Bundle args = new Bundle();
	    args.putParcelable(ARGUMENT_CONTENT_URI, contentUri);
	    args.putString(ARGUMENT_QUERY, query);
		
	    // Create fragment
		Fragment fragment = new StreamFragment();
	    fragment.setArguments(args);
	    
	    return fragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Retain this fragment between orientation changes
		setRetainInstance(true);
		
		// Create cursor adapter (without a cursor yet)
		// Attention! This activity might not exist for the whole lifetime of the adapter,
		// so you better don't save a reference to it in the adapter
		mAdapter = new ImageListCursorAdapter(getActivity(), null);
		
		// Get the fragment's initialization arguments
		Bundle args = getArguments();
		mContentUri = args.getParcelable(ARGUMENT_CONTENT_URI);
		mSearchQuery = args.getString(ARGUMENT_QUERY);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		// Init loader and set 'this' as LoaderCallback
		getLoaderManager().initLoader(0, null, this);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		// These IDs should be global to the application
		if (id == 0) {
			// Create a loader for the requested image list
			return new CursorLoader(getActivity(), mContentUri, null, null, null, null);
		} else {
			throw new IllegalArgumentException("Unknown loader ID");
		}
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		// Cursor creation finished
		mAdapter.swapCursor(cursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// Clear the old cursor if the data gets invalidated
		mAdapter.swapCursor(null);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_stream, container, false);
		
		// Setup the gridview
		GridView gridview = (GridView) view.findViewById(R.id.gridview);
		gridview.setOnItemClickListener(this);
		gridview.setEmptyView(view.findViewById(R.id.empty));
		gridview.setAdapter(mAdapter);

		return view;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// Open the detail view for this item
		Intent intent = new Intent(getActivity(), DetailActivity.class);
		intent.putExtra(DetailActivity.EXTRA_BASE_URI, mContentUri);
		intent.putExtra(DetailActivity.EXTRA_ID, id);
    	startActivity(intent);
	}
	
}
