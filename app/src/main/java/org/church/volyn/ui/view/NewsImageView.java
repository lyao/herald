package org.church.volyn.ui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import org.church.volyn.entities.NewsItem;

/**
 * Created by Admin on 24.02.2015.
 */
public class NewsImageView extends ImageView {

    NewsItem mNewsItem;
    static final String TAG = "CustomAdapter";
    Context mContext;
    public NewsImageView(Context context) {
        super(context);
        mContext = context;
    }

    public NewsImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        if (null != mNewsItem.getImageUrl()) {
//            ImageManager.getInstance().fetchBitmap(this);
////            setImageBitmap(ImageManager.getInstance().setBitmap(mImageUrl));
//        } else {
//            setImageBitmap(null);
//        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    public NewsItem getNewsItem() {
        return mNewsItem;
    }

    public void setNewsItem(NewsItem newsItem) {
        this.mNewsItem = newsItem;
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        final TransitionDrawable td =
                new TransitionDrawable(new Drawable[] {
                        new ColorDrawable(Color.TRANSPARENT),
                        new BitmapDrawable(mContext.getResources(), bm)
                });

        setImageDrawable(td);
        td.startTransition(1000);
    }

//    public void setImageBitmapFadeIn(Bitmap bm) {
//        super.setImageBitmap(bm);
//        final TransitionDrawable td =
//                new TransitionDrawable(new Drawable[] {
//                        new ColorDrawable(Color.TRANSPARENT),
//                        new BitmapDrawable(mContext.getResources(), bm)
//                });
//
//        setImageDrawable(td);
//        td.startTransition(1000);
//    }

}

