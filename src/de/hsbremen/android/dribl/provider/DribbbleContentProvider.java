package de.hsbremen.android.dribl.provider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Hashtable;

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
import android.util.Log;

public class DribbbleContentProvider extends ContentProvider {

	private enum RequestType {
		PopularList,
		EveryoneList,
		DebutsList,
		SearchResultList
	};
	
	// We need a Hashtable to be thread-safe
	private Hashtable<RequestType, Cursor> responseCache = new Hashtable<RequestType, Cursor>();
	
	@Override
	public boolean onCreate() {
		Log.d("Dribl", "New DribbbleContentProvider instance");
//		sUriMatcher.addURI("com.example.app.provider", "table3", 1);

	    // Store a maximum of 10 responses in the cache. A typical response is about 20KB in size.
//	    final int cacheSize = 10;
//	    responseCache = new LruCache<String, String>(cacheSize);
	    
		return true;
	}
	
	/**
	 * This method accesses the response cache in a thread-safe way
	 *  
	 * @param key
	 * @param response
	 */
	public synchronized void addResponseToMemoryCache(RequestType key, Cursor response) {
		if (responseCache.get(key) == null) {
            responseCache.put(key, response);
        }
	}
	
	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * The sortOrder parameter is unsupported because the Dribbble API already sorts the data
	 * by the desired criteria
	 */
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		// TODO: Add support for projection, selection
		
		// Determine the type of this request (we are caching each type separately)
		RequestType requestType;
		if (selection.equals("list = ?")) {
			if (selectionArgs[0].equals("popular")) {
				requestType = RequestType.PopularList;
			} else if (selectionArgs[0].equals("everyone")) {
				requestType = RequestType.EveryoneList;
			} else if (selectionArgs[0].equals("debuts")) {
				requestType = RequestType.DebutsList;
			} else {
				throw new IllegalArgumentException("Illegal selection argument");
			}
		} else if (selection.equals("q = ?")) {
			// Not supported yet
			requestType = RequestType.SearchResultList;
		} else {
			throw new IllegalArgumentException("Illegal selection");
		}

		// Query the cache
		Cursor cursor = responseCache.get(requestType);
		if (cursor == null) {
			// Cache miss, do a download
			String url = getApiUrlForRequestType(requestType, null);
			cursor = loadStreamAsMatrixCursor(url);
			// Save the response to the cache
			addResponseToMemoryCache(requestType, cursor);
		}
		
		return cursor;
	}
	
	private static String getApiUrlForRequestType(RequestType type, String args) {
		// Find the appropriate API endpoint for this request
		switch (type) {
		case PopularList:
			return "http://api.dribbble.com/shots/popular";
		case EveryoneList:
			return "http://api.dribbble.com/shots/everyone";
		case DebutsList:
			return "http://api.dribbble.com/shots/debuts";
		case SearchResultList:
			// not implemented
		}
		return null;
	}
	
	/**
	 * Downloads data from the Dribbble api and parses the JSON response into a MatrixCursor of shots
	 * @param url
	 * @return MatrixCursor with data on success, or null on failure
	 */
	private static MatrixCursor loadStreamAsMatrixCursor(String url) {
		// If the synchronous download fails the String will be empty
		String response = loadStringFromUrl(url);
		if (response.isEmpty()) {
			return null;
		}
		
		// Parse the JSON response
		try {
			MatrixCursor out = new MatrixCursor(new String[] {
					DribbbleContract.Image._ID,
					DribbbleContract.Image.IMAGE_URL,
					DribbbleContract.Image.TITLE
				});
			JSONObject object = new JSONObject(response);
			JSONArray shots = object.getJSONArray("shots");
			for (int i = 0, len = shots.length(); i < len; ++i) {
				JSONObject shot = shots.getJSONObject(i);
				// Add row to the MatrixCursor
				RowBuilder row = out.newRow();
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
