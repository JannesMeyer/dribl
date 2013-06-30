package de.hsbremen.android.dribl.provider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.Map;

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
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.MatrixCursor.RowBuilder;
import android.net.Uri;
import android.util.Log;

public class DribbbleContentProvider extends ContentProvider {

	private static final int STREAM = 1;
	private static final int STREAM_ID = 2;
	
	private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	
	// We need a Hashtable instead of a HashMap to be thread-safe
	private Map<String, Cursor> responseCache = new Hashtable<String, Cursor>();
	
	static {
		sURIMatcher.addURI(DribbbleContract.AUTHORITY, "*", STREAM);
        sURIMatcher.addURI(DribbbleContract.AUTHORITY, "*/#", STREAM_ID);
	}
	
	@Override
	public boolean onCreate() {
		Log.d("Dribl", "New DribbbleContentProvider instance");
		return true;
	}
	
	/**
	 * This method accesses the response cache in a thread-safe way
	 *  
	 * @param key
	 * @param response
	 */
	public synchronized void addResponseToMemoryCache(String key, Cursor response) {
		if (responseCache.get(key) == null) {
            responseCache.put(key, response);
        }
	}
	
	@Override
	public String getType(Uri uri) {
		switch (sURIMatcher.match(uri)) {
		case STREAM:
			return DribbbleContract.Image.CONTENT_TYPE;
		case STREAM_ID:
			return DribbbleContract.Image.CONTENT_ITEM_TYPE;
		default:
			// Error
			throw new IllegalArgumentException("Unrecognized URI");
		}
	}

	/**
	 * The sortOrder parameter is unsupported because the Dribbble API already sorts the data
	 * by the desired criteria
	 */
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		String listName;
		Cursor cursor;
		
		switch (sURIMatcher.match(uri)) {
		case STREAM:
			listName = uri.getPathSegments().get(0); 

			cursor = responseCache.get(listName);
			// Cache miss
			if (cursor == null) {
				cursor = loadStreamAsMatrixCursor("http://api.dribbble.com/shots/" + listName);
				addResponseToMemoryCache(listName, cursor);
			}
			
			return cursor;
			
		case STREAM_ID:
			listName = uri.getPathSegments().get(0);
			cursor = responseCache.get(listName);
			// Cache miss
			if (cursor == null) {
				cursor = loadStreamAsMatrixCursor("http://api.dribbble.com/shots/" + listName);
				addResponseToMemoryCache(listName, cursor);
			}

			long id = Long.parseLong(uri.getLastPathSegment());
			
			// Read through the cursor from the beginning
			cursor.moveToPosition(-1);
			while (cursor.moveToNext()) {
				long currentId = cursor.getLong(cursor.getColumnIndex(DribbbleContract.Image._ID));
				if (id == currentId) {
					break;
				}
			}

			return cursor;

		default:
			// Error
			throw new IllegalArgumentException("Unrecognized URI");
		}
	}
	
	/**
	 * Downloads data from the Dribbble api and parses the JSON response into a MatrixCursor of shots
	 * @param url
	 * @return MatrixCursor with data on success, or null on failure
	 */
	private static MatrixCursor loadStreamAsMatrixCursor(String url) {
		// TODO: Add support for projection, selection

		// If the synchronous download fails the String will be empty
		String response = loadStringFromUrl(url);
		if (response.isEmpty()) {
			return null;
		}
		
		// Parse the JSON response
		try {
			MatrixCursor out = new MatrixCursor(new String[] {
					DribbbleContract.Image._ID,
					DribbbleContract.Image.URL,
					DribbbleContract.Image.IMAGE_URL,
					DribbbleContract.Image.TITLE,
					DribbbleContract.Image.AUTHOR
				});
			JSONObject object = new JSONObject(response);
			JSONArray shots = object.getJSONArray("shots");
			for (int i = 0, len = shots.length(); i < len; ++i) {
				JSONObject shot = shots.getJSONObject(i);
				// Add row to the MatrixCursor
				RowBuilder row = out.newRow();
				// Add the id
				row.add(shot.getLong("id"));
				
				// Add the url
				row.add(shot.getString("url"));
				
				// Always load the non-retina version (400x300)
				if (shot.has("image_400_url")) {
					row.add(shot.getString("image_400_url"));
				} else {
					row.add(shot.getString("image_url"));
				}	
				// Add the title
				row.add(shot.getString("title"));
				
				// Add the author
				JSONObject player = shot.getJSONObject("player");
				row.add(player.getString("name"));
			}
			// Return the MatrixCursor
			return out;
		} catch (JSONException e) {
			// Log the error and return null
			Log.e("Dribl", e.getClass().getSimpleName() + ": " + e.getMessage());
			return null;
		}
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
				Log.e("Dribl", "Failed to download file");
			}
		} catch (ClientProtocolException e) {
			Log.e("Dribl", e.getMessage());
		} catch (IOException e) {
			Log.e("Dribl", e.getMessage());
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
