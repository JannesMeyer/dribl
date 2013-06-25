package de.hsbremen.android.dribl.fragments;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
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
import de.hsbremen.android.dribl.provider.DribbbleContract;

public class StreamFragment extends Fragment implements LoaderCallbacks<Cursor>, OnItemClickListener {
	
	Activity mActivity;
	CursorAdapter mAdapter;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mActivity = activity;
		
		// Let this fragment take over the ActionBar title
		// whenever we're attaching to an Activity 
		String title = getArguments().getString("title");
		activity.getActionBar().setTitle(title);
	}
	
	@Override
	public void onDetach() {
		super.onDetach();
		mActivity = null;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Retain this fragment between orientation changes
		setRetainInstance(true);
		
		// Get the fragment's initialization arguments
		Bundle args = getArguments();
		
		// Create cursor adapter (without a cursor yet)
		// Attention! This activity might not exist for the whole lifetime of the adapter,
		// so you better don't save a reference to it in the adapter
		mAdapter = new ImageListCursorAdapter(mActivity, null);
		
		// Init loader and set 'this' as LoaderCallback
		getLoaderManager().initLoader(0, args, this);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		if (id == 0) {
			// Create a loader that loads a stream of images
			final String selection = "list = ?";
			final String[] selectionArgs = { args.getString("list") };
			return new CursorLoader(mActivity, DribbbleContract.Image.CONTENT_URI, null, selection, selectionArgs, null);
		} else {
			throw new IllegalArgumentException("Invalid loader ID");
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
		Intent intent = new Intent(mActivity, DetailActivity.class);
    	startActivity(intent);
	}
	
}
