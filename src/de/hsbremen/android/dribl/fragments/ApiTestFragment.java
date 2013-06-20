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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;
import de.hsbremen.android.dribl.DetailActivity;
import de.hsbremen.android.dribl.R;
import de.hsbremen.android.dribl.adapter.ImageListCursorAdapter;
import de.hsbremen.android.dribl.provider.DribbbleContract;




public class ApiTestFragment extends Fragment implements LoaderCallbacks<Cursor> {

	/**
	 * Tag for logging
	 */
	public static final String TAG = ApiTestFragment.class.getSimpleName();
	
	Activity mActivity;
	CursorAdapter mAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_apitest, container, false);
		
		mActivity = getActivity();
		
		GridView gridview = (GridView) view.findViewById(R.id.gridview);
		gridview.setEmptyView((TextView) view.findViewById(R.id.empty));
		
		// Set adapter and let the cursor be changed later
		mAdapter = new ImageListCursorAdapter(mActivity, null);
		gridview.setAdapter(mAdapter);
		
		// Init loader with id 1 and set this class as callback
		getLoaderManager().initLoader(1, null, this);
		
		// Click listener
		gridview.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            	Log.d(TAG, "item clicked: " + position);
            	Intent intent = new Intent(getActivity(), DetailActivity.class);
            	startActivity(intent);
//            	Builder builder = new AlertDialog.Builder(mActivity, AlertDialog.THEME_HOLO_LIGHT);
//        		builder.setTitle("Hallo Titel").setItems(new String[] { "Ja", "Nein", "Vielleicht" }, new OnClickListener() {
//        			@Override
//        			public void onClick(DialogInterface dialog, int which) {
//        				Log.d(TAG, "Something clicked " + which);
//        			}
//        		}).show();
            }
        } );
				
		return view;
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		if (id == 0) {
//			String searchString = null;
//
//			String[] projection = { UserDictionary.Words._ID, UserDictionary.Words.WORD, UserDictionary.Words.LOCALE };
//			String selection;
//			String[] selectionArgs = {""};
//			if (!TextUtils.isEmpty(searchString)) {
//				selection = UserDictionary.Words.WORD + " = ?";
//				selectionArgs[0] = searchString;
//			} else {
//				selection = null;
//				selectionArgs = null;
//			}
//			return new CursorLoader(mActivity, UserDictionary.Words.CONTENT_URI, projection, selection, selectionArgs, null);
		} else if (id == 1) {
			String[] projection = {  };
			return new CursorLoader(mActivity,
					DribbbleContract.Image.CONTENT_URI,
					projection,
					null,		// No selection clause
					null,		// No selection arguments
					null		// No default sort order
					);
		}
		
		// Invalid ID was passed
		return null;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		// Load finished, swap cursor
		mAdapter.swapCursor(cursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// Clear out the old cursor
		mAdapter.swapCursor(null);
	}

}
