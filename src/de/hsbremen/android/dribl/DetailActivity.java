package de.hsbremen.android.dribl;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.webimageloader.ImageLoader;
import com.webimageloader.ext.ImageHelper;

import de.hsbremen.android.dribl.adapter.IconTextArrayAdapter;
import de.hsbremen.android.dribl.provider.DribbbleContract;

public class DetailActivity extends Activity {

	public static final String EXTRA_BASE_URI = "content_uri";
	public static final String EXTRA_ID = "id";

	private ListAdapter mListAdapter;
	private ActionBar mActionBar;
	private ImageLoader mImageLoader;
	private ImageView mImageView;
	private TextView mTitleText;
	private TextView mAuthorText;	
	
	private String likesCount;
	private String reboundCount;
	private String commentCount;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);
		
		mActionBar = getActionBar();
		mImageLoader = ((DriblApplication) getApplicationContext()).getImageLoader();
		mImageView = (ImageView) findViewById(R.id.image);
		mTitleText = (TextView) findViewById(R.id.title);
		mAuthorText = (TextView) findViewById(R.id.author);
		
		// Setup action bar
		mActionBar.setDisplayHomeAsUpEnabled(true);
		
		// Get intent data
		Intent intent = getIntent();
		Uri baseUri = intent.getParcelableExtra(EXTRA_BASE_URI);
		long id = intent.getLongExtra(EXTRA_ID, -1);
		if (baseUri == null || id == -1) {
			throw new IllegalArgumentException("Missing arguments");
		}
		
		// Get a cursor that's position is set to the current image
		Cursor cursor = getContentResolver().query(ContentUris.withAppendedId(baseUri, id),
				null,  // No projection
				null,  // No selection
				null,  // No selectionArgs
				null   // No sortOrder
			);
		if (cursor != null) {
			// Get the data from the cursor
			final String imageUrl = cursor.getString(cursor.getColumnIndex(DribbbleContract.Image.IMAGE_URL));
			final String title = cursor.getString(cursor.getColumnIndex(DribbbleContract.Image.TITLE));
			final String author = cursor.getString(cursor.getColumnIndex(DribbbleContract.Image.AUTHOR));
			
			likesCount = cursor.getString(cursor.getColumnIndex(DribbbleContract.Image.LIKES_COUNT));
			reboundCount = cursor.getString(cursor.getColumnIndex(DribbbleContract.Image.REBOUNDS_COUNT));
			commentCount = cursor.getString(cursor.getColumnIndex(DribbbleContract.Image.COMMENTS_COUNT));
			
			
			// Load image
			new ImageHelper(this, mImageLoader)
				.setLoadingResource(R.drawable.placeholder)
				.load(mImageView, imageUrl);
			// Set text
			mActionBar.setTitle(title);
			mTitleText.setText(title);
			mAuthorText.setText(author);
			
			Log.d("***API INFO***", "comment count: " + commentCount);
			Log.d("***API INFO***", "likes count: " + likesCount);
		}
		
		// Prepare info content
		String[] texts = getResources().getStringArray(R.array.detail_list);
		String[] counts = {
				likesCount, 
				reboundCount, 
				commentCount
		};
		
		for (int i = 0; i < texts.length; ++i) {
			texts[i] = counts[i] + " " + texts[i]; 
		}
		int[] icons = {
				R.drawable.icon_likes,
				R.drawable.icon_buckets,
				R.drawable.icon_views
		};
		
		// Create the listadapter
		mListAdapter = new IconTextArrayAdapter(this, icons, texts, R.layout.row_icontext) {
			@Override
			public boolean isEnabled(int position) {
				// Make all items in this list non-clickable
				return false;
			}
		};
		
		ListView detailList = (ListView) findViewById(R.id.detailList);
		detailList.setAdapter(mListAdapter);
		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			// This call is only needed for compatibility with older API levels
			case android.R.id.home:
				NavUtils.navigateUpFromSameTask(this);
				return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.detail, menu);
		
		return true;
	}

}
