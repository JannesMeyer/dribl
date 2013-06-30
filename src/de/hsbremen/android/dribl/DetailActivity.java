package de.hsbremen.android.dribl;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.webimageloader.ImageLoader;
import com.webimageloader.ext.ImageHelper;

import de.hsbremen.android.dribl.provider.DribbbleContract;

public class DetailActivity extends Activity {

	public static final String EXTRA_BASE_URI = "content_uri";
	public static final String EXTRA_ID = "id";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);
		
		ImageLoader imageLoader = ((DriblApplication) getApplicationContext()).getImageLoader();
		ImageView imageView = (ImageView) findViewById(R.id.image);
		TextView titleText = (TextView) findViewById(R.id.title);
		TextView authorText = (TextView) findViewById(R.id.author);
		
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		Intent in = getIntent();
		Uri baseUri = in.getParcelableExtra(EXTRA_BASE_URI);
		long id = in.getLongExtra(EXTRA_ID, -1);
		if (baseUri == null || id == -1) {
			throw new IllegalArgumentException("Missing arguments");
		}
		
		// Get a cursor that's position is set to the current image
		Uri contentUri = ContentUris.withAppendedId(baseUri, id);
		Cursor cursor = getContentResolver().query(contentUri, null, null, null, null);
		if (cursor == null) {
			// Fail gracefully
			return;
		}
		
		final String imageUrl = cursor.getString(cursor.getColumnIndex(DribbbleContract.Image.IMAGE_URL));
		final String title = cursor.getString(cursor.getColumnIndex(DribbbleContract.Image.TITLE));
		final String author = cursor.getString(cursor.getColumnIndex(DribbbleContract.Image.AUTHOR));
		
		// Load image
		new ImageHelper(this, imageLoader)
			.setLoadingResource(R.drawable.placeholder)
			.load(imageView, imageUrl);
		
		// Set title
		getActionBar().setTitle(title);
		titleText.setText(title);
		
		// Set author
		authorText.setText(author);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			// This call is only needed for compatibility with old API levels
			case android.R.id.home:
				NavUtils.navigateUpFromSameTask(this);
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
