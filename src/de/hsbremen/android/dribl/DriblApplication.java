package de.hsbremen.android.dribl;

import java.io.File;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.webimageloader.ImageLoader;
import com.webimageloader.ImageLoader.Logger;
import com.webimageloader.loader.MemoryCache;
import com.webimageloader.util.IOUtil;

/**
 * Class that creates a global ImageLoader
 * Set this as your {@code android:application} in {@code AndroidManifest.xml}.
 *
 */
public class DriblApplication extends Application {

	private static final String TAG = "Dribl";
	
    private static final int MEMORY_CACHE_DIVIDER = 8;
    private static final int DISK_CACHE_SIZE = 10 * 1024 * 1024;

    private ImageLoader mImageLoader;

    @Override
    public void onCreate() {
        super.onCreate();
        
        // Get memory class of this device, exceeding this amount will throw an
        // OutOfMemory exception.
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        int memClass = am.getMemoryClass();

        // Use only a part of the available memory for caching
        final int memoryCacheSize = 1024 * 1024 * memClass / MEMORY_CACHE_DIVIDER;

        File cacheDir = IOUtil.getDiskCacheDir(this, "images");
        
        mImageLoader = new ImageLoader.Builder(this)
                .enableDiskCache(cacheDir, DISK_CACHE_SIZE)
                .enableMemoryCache(memoryCacheSize).build();
    }
    
    public ImageLoader getImageLoader() {
    	return mImageLoader;
    }
    
    @Override
    public void onTerminate() {
        super.onTerminate();
        mImageLoader.destroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();

        MemoryCache memoryCache = mImageLoader.getMemoryCache();
        if (memoryCache != null) {
            if (Logger.DEBUG) Log.d(TAG, "onLowMemory() called, eviciting all bitmaps");
            memoryCache.evictAll();
        }
    }

    @Override
    @TargetApi(14)
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);

        MemoryCache memoryCache = mImageLoader.getMemoryCache();
        if (memoryCache == null) {
            return;
        }

        if (level >= TRIM_MEMORY_MODERATE) {
            // Nearing middle of list of cached background apps
            // Evict our entire bitmap cache
            if (Logger.DEBUG) Log.d(TAG, "onTrimMemory(), level>=TRIM_MEMORY_MODERATE called, eviciting all bitmaps");
            memoryCache.evictAll();
        } else if (level >= TRIM_MEMORY_BACKGROUND) {
            // Entering list of cached background apps
            // Evict oldest half of our bitmap cache
            if (Logger.DEBUG) Log.d(TAG, "onTrimMemory(), level>=TRIM_MEMORY_BACKGROUND called, evicing half of all bitmaps");
            memoryCache.trimToSize(memoryCache.size() / 2);
        }
    }
    
}