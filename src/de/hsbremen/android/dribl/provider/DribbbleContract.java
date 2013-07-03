package de.hsbremen.android.dribl.provider;

import android.net.Uri;
import android.provider.BaseColumns;

public final class DribbbleContract {

    public static final String AUTHORITY = "de.hsbremen.android.dribl.provider";
    public static final Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);
	
    /**
     * Constants for the image list
     */
	public final static class Image {
		// This class cannot be instantiated
		private Image() {}
		
		public static final Uri STREAM_POPULAR_URI = Uri.withAppendedPath(AUTHORITY_URI, "popular");
		public static final Uri STREAM_EVERYONE_URI = Uri.withAppendedPath(AUTHORITY_URI, "everyone");
		public static final Uri STREAM_DEBUTS_URI = Uri.withAppendedPath(AUTHORITY_URI, "debuts");
		public static final Uri SEARCH_URI = Uri.withAppendedPath(AUTHORITY_URI, "search");
		public static final Uri COLLECTION_URI = Uri.withAppendedPath(AUTHORITY_URI, "collection");
		
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/image";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/image";
		
		public static final String _ID = BaseColumns._ID;
		public static final String URL = "url";
		public static final String IMAGE_URL = "image_url";
		public static final String TITLE = "title";
		public static final String AUTHOR = "author";
		public static final String LIKES_COUNT = "likes_count";
		public static final String REBOUNDS_COUNT = "rebounds_count";
		public static final String COMMENTS_COUNT = "comments_count";
	}
	
}
