package de.hsbremen.android.dribl.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Fragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.UserDictionary;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import de.hsbremen.android.dribl.R;
import de.hsbremen.android.dribl.provider.DribbbleContract;


public class ApiTestFragment extends Fragment implements LoaderCallbacks<Cursor> {

	/**
	 * Tag for logging
	 */
	public static final String TAG = ApiTestFragment.class.getSimpleName();
	
	Activity mActivity;
	SimpleCursorAdapter mAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_apitest, container, false);
		
		mActivity = getActivity();
		
//		ListView listview = (ListView) view.findViewById(R.id.list);
		GridView listview = (GridView) view.findViewById(R.id.gridview);
		listview.setEmptyView((TextView) view.findViewById(R.id.empty));
		
		mAdapter = new SimpleCursorAdapter(mActivity, android.R.layout.simple_list_item_1, null, new String[] { DribbbleContract.Pictures.IMAGE_URL }, new int[] { android.R.id.text1 }, 0);
		listview.setAdapter(mAdapter);
		listview.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            	Builder builder = new AlertDialog.Builder(mActivity, AlertDialog.THEME_HOLO_LIGHT);
        		builder.setTitle("Hallo Titel").setItems(new String[] { "Ja", "Nein", "Vielleicht" }, new OnClickListener() {
        			@Override
        			public void onClick(DialogInterface dialog, int which) {
        				Log.d(TAG, "Something clicked " + which);
        			}
        		}).show();
            }
        } );
		getLoaderManager().initLoader(1, null, this);		
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
//			
//			return new CursorLoader(mActivity, UserDictionary.Words.CONTENT_URI, projection, selection, selectionArgs, null);
		} else if (id == 1) {
			String[] projection = {  };
			return new CursorLoader(mActivity, DribbbleContract.Pictures.CONTENT_URI, projection, null, null, null);
		}
		
		return null;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		// Load finished, swap cursor
		mAdapter.swapCursor(cursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// Invalidated data
		mAdapter.swapCursor(null);
	}

}
