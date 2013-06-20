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
		
		public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, "images"); 
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/image";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/image";
		
		public static final String _ID = BaseColumns._ID;
		public static final String IMAGE_URL = "image_url";	
	}
	
}
