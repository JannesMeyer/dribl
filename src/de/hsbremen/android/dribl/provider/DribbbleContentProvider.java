package de.hsbremen.android.dribl.provider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.MatrixCursor.RowBuilder;
import android.net.Uri;
import android.support.v4.util.LruCache;
import android.util.Log;

public class DribbbleContentProvider extends ContentProvider {

	private static final String TAG = DribbbleContentProvider.class.getSimpleName();
	
	private LruCache<String, String> responseCache;
//	private static final UriMatcher sUriMatcher;
	
	
	@Override
	public boolean onCreate() {
//		sUriMatcher.addURI("com.example.app.provider", "table3", 1);
		Log.d("Dribl", "New ContentProvider instance");

	    // Store a maximum of 10 responses in the cache. A typical response is about 20KB in size.
	    final int cacheSize = 10;
	    responseCache = new LruCache<String, String>(cacheSize);
	    
		return true;
	}
	
	/**
	 * This method accesses the response cache in a thread-safe way
	 *  
	 * @param key
	 * @param response
	 */
	public synchronized void addResponseToMemoryCache(String key, String response) {
		if (responseCache.get(key) == null) {
            responseCache.put(key, response);
        }
	}
	
	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		
		MatrixCursor c = new MatrixCursor(new String[] {
			DribbbleContract.Image._ID,
			DribbbleContract.Image.IMAGE_URL,
			DribbbleContract.Image.TITLE
		});
		
		String url = "http://api.dribbble.com/shots/popular";

		String response = responseCache.get(url);
		// If the cache failed we need to actually download it from the network
		if (response == null) {
			response = loadStringFromUrl(url);
			addResponseToMemoryCache(url, response);
		}
		
		
		// Parse JSON
		try {
			JSONObject object = new JSONObject(response);
			JSONArray shots = object.getJSONArray("shots");
			for (int i = 0; i < shots.length(); ++i) {
				JSONObject shot = shots.getJSONObject(i);
				// Add row to the matrix cursor
				RowBuilder row = c.newRow();

				// Add the id
				row.add(shot.getLong("id"));

				// Always load the non-retina version (400x300)
				if (shot.has("image_400_url")) {
					row.add(shot.getString("image_400_url"));
				} else {
					row.add(shot.getString("image_url"));
				}
				
				// Add the title
				row.add(shot.getString("title"));
			}
		} catch (JSONException e) {
			Log.e(TAG, e.getMessage());
		}
		
		// Return the MatrixCursor
		return c;
	}

	private static String loadStringFromUrl(String url) {
		// Load JSON data (using Apache's http client library)
		StringBuilder builder = new StringBuilder();
		HttpClient client = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(url);
		try {
			HttpResponse response = client.execute(httpGet);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			if (statusCode == 200) {
				HttpEntity entity = response.getEntity();
				InputStream content = entity.getContent();
				BufferedReader reader = new BufferedReader(new InputStreamReader(content));
				String line;
				while ((line = reader.readLine()) != null) {
					builder.append(line);
				}
			} else {
				Log.e(TAG, "Failed to download file");
			}
		} catch (ClientProtocolException e) {
			Log.e(TAG, e.getMessage());
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
		}
		return builder.toString();
	}
	
	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		throw new UnsupportedOperationException();
	}
	
}
