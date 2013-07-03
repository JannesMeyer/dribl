package de.hsbremen.android.dribl.provider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Hashtable;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

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
	
	private static final int SEARCH = 3;
	private static final int SEARCH_ID = 4;
	
	private static final int COLLECTION = 5;
	private static final int COLLECTION_ID = 6;
	
	private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	
	static {
        sURIMatcher.addURI(DribbbleContract.AUTHORITY, "search", SEARCH);
        sURIMatcher.addURI(DribbbleContract.AUTHORITY, "search/#", SEARCH_ID);
        sURIMatcher.addURI(DribbbleContract.AUTHORITY, "collection", COLLECTION);
        sURIMatcher.addURI(DribbbleContract.AUTHORITY, "collection/#", COLLECTION_ID);
		sURIMatcher.addURI(DribbbleContract.AUTHORITY, "*", STREAM);
        sURIMatcher.addURI(DribbbleContract.AUTHORITY, "*/#", STREAM_ID);
	}
	
	// We need a Hashtable instead of a HashMap to be thread-safe
	private Map<String, Cursor> responseCache = new Hashtable<String, Cursor>();
	
	// We need a database for the collection
	private CollectionDBHelper mCollectionDBHelper;
	
	@Override
	public boolean onCreate() {
		Log.d("Dribl", "New DribbbleContentProvider instance");
		
		mCollectionDBHelper = new CollectionDBHelper(getContext().getApplicationContext());
		
		return true;
	}
	
	/**
	 * Returns the MIME-Type that a query with this URI would return.
	 * @return MIME-Type
	 */
	@Override
	public String getType(Uri uri) {
		switch (sURIMatcher.match(uri)) {
		case COLLECTION:
		case SEARCH:
		case STREAM:
			return DribbbleContract.Image.CONTENT_TYPE;

		case COLLECTION_ID:
		case SEARCH_ID:
		case STREAM_ID:
			return DribbbleContract.Image.CONTENT_ITEM_TYPE;

		default:
			// Unrecognized URI
			throw new IllegalArgumentException("Unrecognized URI");
		}
	}

	/**
	 * Loads data from the Dribbble API.
	 * 
	 * @param uri that points to the content
	 * @param projection is unsupported
	 * @param selection is unsupported
	 * @param selectionArgs is unsupported
	 * @param sortOrder is unsupported 
	 * @return Cursor that points to a list of Dribbble images
	 */
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		switch (sURIMatcher.match(uri)) {
		case COLLECTION:
		{
			// Get all items in the collection
			Cursor cursor = mCollectionDBHelper.getCollection();
			cursor.setNotificationUri(getContext().getContentResolver(), uri);
			// The key for the cache needs to be the correct path segment of the URI
			String key = uri.getPathSegments().get(0);
			addResponseToMemoryCache(key, cursor);
			return cursor;
		}
		case SEARCH:
		{
			if (selection == null || !selection.equals("q = ?") || selectionArgs == null || selectionArgs.length < 1) {
				throw new IllegalArgumentException("Selection is missing");
			}
			// Re-do a search request everytime
			Cursor cursor = loadSearchAsMatrixCursor(selectionArgs[0]);
			// The key for the cache needs to be the correct path segment of the URI
			String key = uri.getPathSegments().get(0);
			addResponseToMemoryCache(key, cursor);
			return cursor;
		}
		case STREAM:
		{
			Cursor cursor;
			String tableName = uri.getPathSegments().get(0);
		
			// Ask the cache for this table
			cursor = responseCache.get(tableName);
			// Cache miss
			if (cursor == null) {
				cursor = loadStreamAsMatrixCursor("http://api.dribbble.com/shots/" + tableName);
				addResponseToMemoryCache(tableName, cursor);
			}
			
			return cursor;
		}
		case COLLECTION_ID:
			// This is a special case for testing existence.
			// FOR NORMAL DATA RETRIEVAL FALL THROUGH TO THE NEXT CASE
			if (projection != null && projection.length > 0 && projection[0].equals("1")) {
				long id = Long.parseLong(uri.getLastPathSegment());
				// Checks whether this id is contained in the collection database
				// If it is, a Cursor with length 1 is returned, otherwise 0
				return mCollectionDBHelper.exists(id);
			}
		case SEARCH_ID:
		case STREAM_ID:
		{
			Log.d("Dribl", "Data request for: " + uri.toString());
			
			long id = Long.parseLong(uri.getLastPathSegment());
			String listName = uri.getPathSegments().get(0);
			Cursor cursor = responseCache.get(listName);
			
			// Cache miss
			if (cursor == null) {
				cursor = loadStreamAsMatrixCursor("http://api.dribbble.com/shots/" + listName);
				addResponseToMemoryCache(listName, cursor);
			}
			
			// Read through the cursor from the beginning
			cursor.moveToPosition(-1);
			while (cursor.moveToNext()) {
				long currentId = cursor.getLong(cursor.getColumnIndex(DribbbleContract.Image._ID));
				if (id == currentId) {
					// Return the cursor with its position set to the requested element
					return cursor;
				}
			}
			return null;
		}
		default:
			// Unrecognized URI
			throw new IllegalArgumentException("Unrecognized URI");
		}
	}
	
	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		switch (sURIMatcher.match(uri)) {
		case COLLECTION_ID:
			long resultId = mCollectionDBHelper.addToCollection(values);
			
			if (resultId == -1) {
				// Failure
				return null;
			} else {
				// Success
				return uri;				
			}
		default:
			throw new UnsupportedOperationException();		
		}
	}
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		switch (sURIMatcher.match(uri)) {
		case COLLECTION_ID:
			// Get the id that's specified in the URI
			long id = Long.parseLong(uri.getLastPathSegment());
			
			// Do the deletion and return the number of rows affected
			int rowsAffected = mCollectionDBHelper.removeFromCollection(id);
			if (rowsAffected > 0) {
				getContext().getContentResolver().notifyChange(uri, null, false);
			}
			return rowsAffected;
			
		default:
			throw new UnsupportedOperationException();		
		}
	}
	
	/**
	 * This method accesses the response cache in a thread-safe way
	 *  
	 * @param key
	 * @param response
	 */
	private void addResponseToMemoryCache(String key, Cursor response) {
		synchronized(responseCache) {
			if (responseCache.get(key) == null) {
	            responseCache.put(key, response);
	        }			
		}
	}

	/**
	 * Downloads data from the Dribbble api and parses the JSON response into a MatrixCursor of shots.
	 * An example of the response structure can be found here:
	 * http://api.dribbble.com/shots/popular
	 *
	 * projection, selection and sortOrder are unsupported
	 * 
	 * @param url
	 * @return MatrixCursor with data on success, or null on failure
	 */
	private static MatrixCursor loadStreamAsMatrixCursor(String url) {
		String response = loadStringFromUrl(url);
		if (response == null) {
			return null;
		}
		
		// Setup the MatrixCursor
		MatrixCursor out = new MatrixCursor(new String[] {
			DribbbleContract.Image._ID,
			DribbbleContract.Image.URL,
			DribbbleContract.Image.TITLE,
			DribbbleContract.Image.LIKES_COUNT,
			DribbbleContract.Image.COMMENTS_COUNT,
			DribbbleContract.Image.REBOUNDS_COUNT,
			DribbbleContract.Image.AUTHOR,
			DribbbleContract.Image.IMAGE_URL
		});
		
		// Parse the JSON response
		try {
			JSONObject object = new JSONObject(response);
			JSONArray shots = object.getJSONArray("shots");
			for (int i = 0, len = shots.length(); i < len; ++i) {
				// Get nodes
				JSONObject shot = shots.getJSONObject(i);
				JSONObject player = shot.getJSONObject("player");
				
				// Add a new row to the MatrixCursor
				RowBuilder row = out.newRow();
				
				// ID, URL, Title, likes, comments, rebounds, author
				row.add(shot.getLong("id"));
				row.add(shot.getString("url"));
				row.add(shot.getString("title"));
				row.add(shot.getInt("likes_count"));
				row.add(shot.getInt("comments_count"));
				row.add(shot.getInt("rebounds_count"));
				row.add(player.getString("name"));
				
				// Always load the non-retina version (400x300)
				row.add(shot.getString(shot.has("image_400_url") ? "image_400_url" : "image_url"));
			}
			
			return out;
		} catch (JSONException e) {
			// The JSON response unexpected, return nothing
			Log.e("Dribl", e.getClass().getSimpleName() + ": " + e.getMessage());
			return null;
		}
	}
	
	/**
	 * This method is responsible for returning data when the user does a search.
	 * The method is implemented by parsing the HTML response from the dribbble.com
	 * website when doing a search there, because the JSON API does not provide
	 * search functionality
	 * 
	 * @return
	 */
	private static MatrixCursor loadSearchAsMatrixCursor(String query) {
		// Setup the MatrixCursor
		// We can ignore memory leak warnings because we are
		// catching all of our own exceptions and returning
		// the empty MatrixCursor there
		@SuppressWarnings("resource")
		MatrixCursor out = new MatrixCursor(new String[] {
			DribbbleContract.Image._ID,
			DribbbleContract.Image.URL,
			DribbbleContract.Image.TITLE,
			DribbbleContract.Image.LIKES_COUNT,
			DribbbleContract.Image.COMMENTS_COUNT,
			DribbbleContract.Image.REBOUNDS_COUNT,
			DribbbleContract.Image.AUTHOR,
			DribbbleContract.Image.IMAGE_URL
		});
		
		try {
			String searchUrl = "http://dribbble.com/search?utf8=%E2%9C%93&q=" + URLEncoder.encode(query, "UTF-8");
			Log.d("Dribl", "Starting request");
			String response = loadStringFromUrl(searchUrl);
			Log.d("Dribl", "Starting to parse");
			Document doc = Jsoup.parse(response, searchUrl);
			
			// Prepare regular expressions
			Pattern idPattern = Pattern.compile("/shots/(\\d+)-");
			
			// Process each image
			for (Element shot : doc.select(".dribbble")) {
				Log.d("Dribl", "Processing a shot");
				
				// Find all necessary elements in the HTML response
				Element linkTag = shot.select(".dribbble-link").first();
				if (linkTag == null) {
					throw new IOException("No link found");
				}
				String imageUrl;
				Element imgTag = linkTag.select("img").first();
				Element imgTag2 = linkTag.child(0).child(1);
				if (imgTag == null) {
					throw new IOException("No img tag found");
				}
				if (imgTag2 != null) {
					// Found hi-res image
					imageUrl = imgTag2.absUrl("data-src");
				} else {
					// No hi-res image found
					imageUrl = imgTag.absUrl("src");
				}
				Element authorLink = shot.nextElementSibling().select(".url").first();
				if (authorLink == null) {
					throw new IOException("No author found");
				}
				// Find id
				Matcher m = idPattern.matcher(linkTag.attr("href"));
				if (!m.find()) {
					throw new IOException("No ID found");
				}
				long id = Long.parseLong(m.group(1));
				
				// Add a new row to the MatrixCursor. Now it gets serious.
				RowBuilder row = out.newRow();
				
				// ID, URL, Title
				row.add(id);
				row.add(linkTag.absUrl("href"));
				row.add(imgTag.attr("alt"));
				
				row.add(0); // likes_count
				row.add(0); // comments_count
				row.add(0); // rebounds_count
				
				// Author
				row.add(authorLink.text());
				
				// Image URL
				row.add(imageUrl);
			}

			return out;
		} catch (UnsupportedEncodingException e) {
			out.close();
			throw new RuntimeException("This device doesn't support UTF-8. How curious.");
		} catch (IOException e) {
			// Error while parsing the response. Try to fail gracefully.
			Log.e("Dribl", "Parser error: " + e.getMessage());
			return out;
		} catch (NullPointerException e) {
			// Error while parsing the response. Try to fail gracefully.
			Log.e("Dribl", e.getClass().getSimpleName() + ": " + e.getMessage());
			return out;
		}
	}
	
	/**
	 * Downloads a URL as a String
	 * 
	 * @param url
	 * @return
	 */
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
				Log.e("Dribl", "Failed to download the data");
				return null;
			}
		} catch (ClientProtocolException e) {
			Log.e("Dribl", e.getClass().getSimpleName() + ": " + e.getMessage());
			return null;
		} catch (IOException e) {
			Log.e("Dribl", e.getClass().getSimpleName() + ": " + e.getMessage());
			return null;
		}
		
		return builder.toString();
	}
	
}
