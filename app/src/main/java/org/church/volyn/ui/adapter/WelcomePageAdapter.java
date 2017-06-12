package org.church.volyn.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.MailTo;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import org.church.volyn.R;
public class WelcomePageAdapter extends PagerAdapter {


    private static final int ITEMS_COUNT = 5;
    private static final int POS_WELCOME = 0;
    private static final int POS_NEWS = 1;
    private static final int POS_CATEGORIES = 2;
    private static final int POS_MEDIAS = 3;
    private static final int POS_GALLERY = 4;

    private LayoutInflater mLayoutInflater;
    private Context mContext;

    public WelcomePageAdapter(Context context) {
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = context;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public int getCount() {
        return ITEMS_COUNT;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = null;
        if (position == POS_WELCOME) {
            itemView = mLayoutInflater.inflate(R.layout.view_welcome_welcome, container, false);
        } else if (position == POS_NEWS) {
            itemView = mLayoutInflater.inflate(R.layout.view_welcome_news, container, false);
        } else if (position == POS_CATEGORIES) {
//            itemView = mLayoutInflater.inflate(IS_TABLET ? R.layout.view_welcome_create : R.layout.view_welcome_create_phone,
//                    container, false);
        } else if (position == POS_MEDIAS) {
//            itemView = mLayoutInflater.inflate(IS_TABLET ? R.layout.view_welcome_library : R.layout.view_welcome_library_phone,
//                    container, false);
        }
        container.addView(itemView);
        return itemView;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout) object);
    }
}
