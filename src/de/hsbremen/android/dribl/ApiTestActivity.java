package de.hsbremen.android.dribl;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ListActivity;
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
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import de.hsbremen.android.dribl.provider.DribbbleContract;


public class ApiTestActivity extends ListActivity implements LoaderCallbacks<Cursor> {

	/**
	 * Tag for logging
	 */
	public static final String TAG = ApiTestActivity.class.getSimpleName();
	
	CursorAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_api_test);
		
//		getListView().set
		
//		mAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, null, new String[] { UserDictionary.Words.WORD }, new int[] { android.R.id.text1 }, 0);
		mAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, null, new String[] { DribbbleContract.Pictures.IMAGE_URL }, new int[] { android.R.id.text1 }, 0);
		setListAdapter(mAdapter);
		getLoaderManager().initLoader(1, null, this);
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT);
		builder.setTitle("Hallo Titel").setItems(new String[] { "Ja", "Nein", "Vielleicht" }, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Log.d(TAG, "Something clicked " + which);
			}
		}).show();
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		if (id == 0) {
			String searchString = null;

			String[] projection = { UserDictionary.Words._ID, UserDictionary.Words.WORD, UserDictionary.Words.LOCALE };
			String selection;
			String[] selectionArgs = {""};
			if (!TextUtils.isEmpty(searchString)) {
				selection = UserDictionary.Words.WORD + " = ?";
				selectionArgs[0] = searchString;
			} else {
				selection = null;
				selectionArgs = null;
			}
			
			return new CursorLoader(this, UserDictionary.Words.CONTENT_URI, projection, selection, selectionArgs, null);
		} else if (id == 1) {
			String[] projection = {  };
			return new CursorLoader(this, DribbbleContract.Pictures.CONTENT_URI, projection, null, null, null);
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
