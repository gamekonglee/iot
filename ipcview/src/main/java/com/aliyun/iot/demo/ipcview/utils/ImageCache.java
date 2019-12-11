package com.aliyun.iot.demo.ipcview.utils;

import android.graphics.Bitmap;
import android.util.LruCache;

/**
 *
 *
 * ！！！注意！！！
 * 注意本示例代码主要用于演示部分视频业务接口以及对应的效果
 * 代码中涉及的交互，UI以及代码框架请自行设计，示例代码仅供参考，稳定性请客户自行保证。
 *
 * @author azad
 */
public class ImageCache {


    private LruCache<String, Bitmap> cache;

    private ImageCache() {
    }

    private static class ImageCacheHolder {
        private static final ImageCache IMAGE_CACHE = new ImageCache();
    }

    public static ImageCache getInstance() {
        return ImageCacheHolder.IMAGE_CACHE;
    }

    public void init() {
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory / 4;
        cache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }
        };
    }

    public Bitmap getImage(String url) {
        return cache.get(url);
    }

    public void addImgIntoCache(String url, Bitmap bitmap) {
        if (getImage(url) == null) {
            cache.put(url, bitmap);
        }
    }

}
