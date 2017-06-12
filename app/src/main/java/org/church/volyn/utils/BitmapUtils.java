package org.church.volyn.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Build;

import java.io.ByteArrayInputStream;

/**
 * Created by Admin on 16.03.2015.
 */
public class BitmapUtils {

    private BitmapUtils() {
    }

    ;

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static int getBitmapSize(Bitmap bitmap) {
//        Bitmap bitmap = value.getBitmap();

        // From KitKat onward use getAllocationByteCount() as allocated bytes can potentially be
        // larger than bitmap byte count.
        if (BitmapUtils.hasKitKat()) {
            return bitmap.getAllocationByteCount();
        }

        if (BitmapUtils.hasHoneycombMR1()) {
            return bitmap.getByteCount();
        }

        // Pre HC-MR1
        return bitmap.getRowBytes() * bitmap.getHeight();
    }

    public static boolean hasFroyo() {
        // Can use static final constants like FROYO, declared in later versions
        // of the OS since they are inlined at compile time. This is guaranteed behavior.
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
    }

    public static boolean hasGingerbread() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
    }

    public static boolean hasHoneycomb() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }

    public static boolean hasHoneycombMR1() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1;
    }

    public static boolean hasJellyBean() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }

    public static boolean hasKitKat() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    public static Bitmap decodeBitmap(byte[] imageBytes) {
        Bitmap returnedBitmap = null;
        Bitmap bm = BitmapFactory.decodeStream(new ByteArrayInputStream(imageBytes));
        try {
            BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
            int targetWidth = 150;
            int targetHeight = 150;
            bitmapOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length, bitmapOptions);
            int hScale = bitmapOptions.outHeight / targetHeight;
            int wScale = bitmapOptions.outWidth / targetWidth;
            int sampleSize = Math.max(hScale, wScale);
            if (sampleSize > 1) {
                bitmapOptions.inSampleSize = sampleSize;
            }

            bitmapOptions.inJustDecodeBounds = false;
            returnedBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length, bitmapOptions);
            ;
        } finally {
        }

        return returnedBitmap;
    }

    public static Bitmap scaleBitmap(Bitmap bitmap) {
        float width = bitmap.getWidth();
        float height = bitmap.getHeight();
        if (width > height) {
            return Bitmap.createScaledBitmap(bitmap, (int) (width / height * 150), 150, false);
        } else {
            return Bitmap.createScaledBitmap(bitmap, 150, (int) (height / width * 150), false);
        }
    }

    public static Drawable getDrawableByName(Context context, String name) {
        Resources resources = context.getResources();
        final int resourceId = resources.getIdentifier(name, "drawable",
                context.getPackageName());
        return resources.getDrawable(resourceId);

    }
}
