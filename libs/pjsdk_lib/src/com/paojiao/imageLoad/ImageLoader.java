package com.paojiao.imageLoad;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.webkit.URLUtil;
import android.widget.ImageView;

public class ImageLoader {
    private FileCache fileCache;
    MemoryCache memoryCache = new MemoryCache();
    private Map<ImageView, String> imageViews = Collections
            .synchronizedMap(new WeakHashMap<ImageView, String>());
    ExecutorService executorService;
    Handler handler = new Handler();// handler to display images in UI thread

    private Context mContext;
    private int defautImage;
    private int imageShowWidth;
    private int imageShowHeight;

    private static ImageLoader imageLoader;

    public static ImageLoader getInstance(Context context) {
        if (imageLoader == null) {
            imageLoader = new ImageLoader(context);
        }
        return imageLoader;
    }

    private ImageLoader(Context context) {
        mContext = context;
        fileCache = new FileCache(context);
        executorService = Executors.newFixedThreadPool(5);
    }

    /**
     * 配置图片宽度
     * 
     * @param defautImage
     *            默认图片
     * @param screenWidthFraction
     *            屏幕宽度比例 screenWidth/screenWidthFraction=imageShowWidth 正方形图
     */
    public void configureImageLoader(int defautImage, int screenWidthFraction) {
        this.defautImage = defautImage;
        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        int imageWidth = metrics.widthPixels / screenWidthFraction;
        imageShowWidth = imageWidth;
        imageShowHeight = imageWidth;
    }

    /**
     * 
     * @param defautImage
     *            默认图片
     * @param imageWidth
     *            图片显示宽度
     * @param imageHeight
     *            图片显示高度
     */
    public void configureImageLoader(int defautImage, int imageWidth, int imageHeight) {
        this.defautImage = defautImage;
        imageShowWidth = imageWidth;
        imageShowHeight = imageHeight;
    }

    /**
     * 
     * @param path
     *            图片的路径
     * @param imageView
     * @param defautImage
     *            默认显示图片
     * @param imageWidth
     *            图片显示宽度
     * @param imageHeight
     *            图片显示高度
     */
    public void displayImage(String path, ImageView imageView, int defautImage, int imageWidth,
            int imageHeight) {
        this.defautImage = defautImage;
        imageShowWidth = imageWidth;
        imageShowHeight = imageHeight;
        displayImage(path, imageView);
    }

    /**
     * 此方法请确认先调用configureImageLoader
     * 
     * @param path
     * @param imageView
     */
    public void displayImage(String path, ImageView imageView) {

        imageViews.put(imageView, path);
        Bitmap bitmap = memoryCache.get(path);
        if (bitmap != null)
            imageView.setImageBitmap(bitmap);
        else {
            queuePhoto(path, imageView);
            imageView.setImageResource(defautImage);
        }
    }

    public void displayLocImage(String path, ImageView imageView) {

        imageViews.put(imageView, path);
        Bitmap bitmap = memoryCache.get(path);
        if (bitmap != null)
            imageView.setImageBitmap(bitmap);
        else {
            bitmap = getBitmap(path);
            imageView.setImageBitmap(bitmap);
        }
    }

    private void queuePhoto(String path, ImageView imageView) {
        PhotoToLoad p = new PhotoToLoad(path, imageView);
        executorService.submit(new PhotosLoader(p));
    }

    // Task for the queue
    private class PhotoToLoad {
        public String path;
        public ImageView imageView;

        public PhotoToLoad(String u, ImageView i) {
            path = u;
            imageView = i;
        }
    }

    class PhotosLoader implements Runnable {
        PhotoToLoad photoToLoad;

        PhotosLoader(PhotoToLoad photoToLoad) {
            this.photoToLoad = photoToLoad;
        }

        @Override
        public void run() {
            try {
                if (imageViewReused(photoToLoad))
                    return;
                Bitmap bmp = getBitmap(photoToLoad.path);
                memoryCache.put(photoToLoad.path, bmp);
                if (imageViewReused(photoToLoad))
                    return;
                BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad);
                handler.post(bd);
            } catch (Throwable th) {
                th.printStackTrace();
            }
        }
    }

    boolean imageViewReused(PhotoToLoad photoToLoad) {
        String tag = imageViews.get(photoToLoad.imageView);
        if (tag == null || !tag.equals(photoToLoad.path))
            return true;
        return false;
    }

    public Bitmap getBitmap(String path) {
        File f;
        if (URLUtil.isHttpUrl(path)) {
            f = fileCache.getFile(path);
            // from SD cache
            Bitmap b = BitmapUtil.decodeFile(f, imageShowWidth, imageShowHeight);
            if (b != null) {
                return b;
            }
            else {
                // from web
                return loadNetImage(path, f);
            }

        }
        else {
            f = new File(path);
            // from SD cache
            Bitmap b = BitmapUtil.decodeFile(f, imageShowWidth, imageShowHeight);
            if (b != null) {
                return b;
            }

        }
        return null;
    }

    private Bitmap loadNetImage(String path, File f) {
        try {
            Bitmap bitmap = null;
            URL imageUrl = new URL(path);
            HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            conn.setInstanceFollowRedirects(true);
            InputStream is = conn.getInputStream();
            OutputStream os = new FileOutputStream(f);
            Utils.CopyStream(is, os);
            os.close();
            conn.disconnect();
            bitmap = BitmapUtil.decodeFile(f, imageShowWidth, imageShowHeight);
            return bitmap;
        } catch (Throwable ex) {
            ex.printStackTrace();
            if (ex instanceof OutOfMemoryError)
                memoryCache.clear();
            return null;
        }
    }

    // Used to display bitmap in the UI thread
    class BitmapDisplayer implements Runnable {
        Bitmap bitmap;
        PhotoToLoad photoToLoad;

        public BitmapDisplayer(Bitmap b, PhotoToLoad p) {
            bitmap = b;
            photoToLoad = p;
        }

        public void run() {
            if (imageViewReused(photoToLoad))
                return;
            if (bitmap != null)
                photoToLoad.imageView.setImageBitmap(bitmap);
            else
                photoToLoad.imageView.setImageResource(defautImage);
        }
    }
    
    

}
