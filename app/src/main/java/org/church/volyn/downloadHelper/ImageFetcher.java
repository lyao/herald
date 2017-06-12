package org.church.volyn.downloadHelper;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

import org.church.volyn.BuildConfig;

/**
 * Created by user on 23.03.2015.
 */
public class ImageFetcher {

    private static final String TAG = "ImageFetcher";
    private Context mContext;
    private ImageCache mImageCache;
    private Resources mResources;
    private Bitmap mLoadingBitmap;
    public ImageFetcher(Context context) {
        mContext = context;
        mResources = context.getResources();
        mImageCache = ImageCache.getInstance(mContext);
    }


    public static void cancelWork(ImageView imageView) {
        final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);
        if (bitmapWorkerTask != null) {
            bitmapWorkerTask.cancel(true);
            if (BuildConfig.DEBUG) {
                final Object bitmapData = bitmapWorkerTask.mData;
            }
        }
    }


    public void loadImage(Object data, ImageView imageView) {
        if (data == null) {
            return;
        }

        Bitmap value = null;

        if (mImageCache != null) {
            value = mImageCache.getFromMemCache(String.valueOf(data));
        }

//        if (value == null) {
//            value = mImageCache.getFromDiskCache(String.valueOf(data));
//        }
        if (value != null) {
            // Bitmap found in memory cache

//            final TransitionDrawable td =
//                    new TransitionDrawable(new Drawable[] {
//                            new ColorDrawable(Color.TRANSPARENT),
//                            new BitmapDrawable(mResources, value)
//                    });
//
//            imageView.setImageDrawable(td);
//            td.startTransition(1000);




       imageView.setImageBitmap(value);
        } else if (cancelPotentialWork(data, imageView)) {
            //BEGIN_INCLUDE(execute_background_task)
            final BitmapWorkerTask task = new BitmapWorkerTask(data, imageView);
            final AsyncDrawable asyncDrawable =
                    new AsyncDrawable(mResources, mLoadingBitmap, task);
            imageView.setImageDrawable(asyncDrawable);
            task.execute();

            // NOTE: This uses a custom version of AsyncTask that has been pulled from the
            // framework and slightly modified. Refer to the docs at the top of the class
            // for more info on what was changed.
            //END_INCLUDE(execute_background_task)
        }
    }


    public static boolean cancelPotentialWork(Object data, ImageView imageView) {
        //BEGIN_INCLUDE(cancel_potential_work)
        final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);
        if (bitmapWorkerTask != null) {
            final Object bitmapData = bitmapWorkerTask.mData;
            if (bitmapData == null || !bitmapData.equals(data)) {
                bitmapWorkerTask.cancel(true);
//                if (BuildConfig.DEBUG) {
//                    Log.d(TAG, "cancelPotentialWork - cancelled work for " + data);
//                }
            } else {
                // The same work is already in progress.
                return false;
            }
        }
        return true;
        //END_INCLUDE(cancel_potential_work)
    }

    private static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getBitmapWorkerTask();
            }
        }
        return null;
    }

    private class BitmapWorkerTask extends AsyncTask<Void, Void, Bitmap> {
        private Object mData;
        private final WeakReference<ImageView> imageViewReference;

        public BitmapWorkerTask(Object data, ImageView imageView) {
            mData = data;
            imageViewReference = new WeakReference<ImageView>(imageView);
        }

        /**
         * Background processing.
         */
        @Override
        protected Bitmap doInBackground(Void... params) {

            final String dataString = String.valueOf(mData);
            Bitmap bitmap = null;
//            BitmapDrawable drawable = null;

            // If the image cache is available and this task has not been cancelled by another
            // thread and the ImageView that was originally bound to this task is still bound back
            // to this task and our "exit early" flag is not set then try and fetch the bitmap from
            // the cache
            if (mImageCache != null && !isCancelled() && getAttachedImageView() != null) {
//                    && !mExitTasksEarly) {
//                bitmap = mImageCache.getBitmapFromDiskCache(dataString);
                bitmap = mImageCache.getFromDiskCache(dataString);
            }

                if (mImageCache != null) {
                    mImageCache.saveToMemCache(dataString, bitmap);
                }


            return bitmap;
            //END_INCLUDE(load_bitmap_in_background)
        }

        /**
         * Once the image is processed, associates it to the imageView
         */
        @Override
        protected void onPostExecute(Bitmap value) {
            //BEGIN_INCLUDE(complete_background_work)
            // if cancel was called on this task or the "exit early" flag is set then we're done
//            if (isCancelled() || mExitTasksEarly) {
            if (isCancelled()) {
                value = null;
            }

            final ImageView imageView = getAttachedImageView();
            if (value != null && imageView != null) {
//                if (BuildConfig.DEBUG) {
//                    Log.d(TAG, "onPostExecute - setting bitmap");
//                }
                setImageDrawable(imageView, value);
            }
            //END_INCLUDE(complete_background_work)
        }

        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        @Override
        protected void onCancelled(Bitmap value) {
            super.onCancelled(value);
//            synchronized (mPauseWorkLock) {
//                mPauseWorkLock.notifyAll();
//            }
        }

        /**
         * Returns the ImageView associated with this task as long as the ImageView's task still
         * points to this task as well. Returns null otherwise.
         */
        private ImageView getAttachedImageView() {
            final ImageView imageView = imageViewReference.get();
            final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

            if (this == bitmapWorkerTask) {
                return imageView;
            }

            return null;
        }
    }


    private static class AsyncDrawable extends BitmapDrawable {
        private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;

        public AsyncDrawable(Resources res, Bitmap bitmap, BitmapWorkerTask bitmapWorkerTask) {
            super(res, bitmap);
            bitmapWorkerTaskReference =
                    new WeakReference<BitmapWorkerTask>(bitmapWorkerTask);
        }

        public BitmapWorkerTask getBitmapWorkerTask() {
            return bitmapWorkerTaskReference.get();
        }
    }

    private void setImageDrawable(ImageView imageView, Bitmap bitmap) {
        imageView.setImageBitmap(bitmap);
    }



}
