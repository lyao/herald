package org.church.volyn.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import java.util.ArrayList;

import org.church.volyn.Constants;
import org.church.volyn.R;
import org.church.volyn.data.database.DataManager;
import org.church.volyn.downloadHelper.NewsManager;
import org.church.volyn.entities.NewsItem;
import org.church.volyn.ui.fragment.NewsDetailFragment;
import org.church.volyn.ui.fragment.NewsRecyclerViewFragment;
import org.church.volyn.ui.media.MediaFragment;
import org.church.volyn.ui.media.MediaPlayerFragment;
import org.church.volyn.ui.view.VideoBoxView;

/**
 * Created by user on 31.01.2015.
 */
public class NewsPagerActivity extends BaseActivity implements NewsDetailFragment.Callbacks {
    private ViewPager mViewPager;
    private ArrayList<NewsItem> mNews;
    private int mCategoryId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // toolbar.setOnMenuItemClickListener(this);

        mViewPager = (ViewPager) findViewById(R.id.news_view_pager);
        Bundle extras = getIntent().getExtras();
        if (extras != null)
            mCategoryId = extras.getInt(Constants.CATEGORY_ID);
        getNews(mCategoryId);
        FragmentManager fm = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fm) {
            @Override
            public Fragment getItem(int position) {
                return NewsDetailFragment.newInstance(mNews.get(position).getNewsLink(), position);
            }

            @Override
            public int getCount() {
                return mNews.size();
            }
        });
        setCurrentItemPosition();

//        //MediaPlayerFragment player = (MediaPlayerFragment) fm.findFragmentById(R.id.video_fragment);
//        MediaPlayerFragment mediaPlayerFragment = (MediaPlayerFragment) getFragmentManager().findFragmentById(R.id.youtube_fragment);
//        int catId = 0;
//        if (mediaPlayerFragment == null) {
//            catId = 0;
//            extras = getIntent().getExtras();
//            if (extras != null) {
//                catId = extras.getInt(Constants.CATEGORY_ID);
//            }
//            mediaPlayerFragment = (MediaPlayerFragment) MediaPlayerFragment.newInstance();
//            fm.beginTransaction().add(R.id.video_close_button, mediaPlayerFragment).commit();
//        }
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_news_pager;
    }

    @Override
    protected void setActionBarBackButton(int drawable) {
        super.setActionBarBackButton(R.drawable.ic_back);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.menu_category_detail, menu);
//        return super.onCreateOptionsMenu(menu);
//    }

    //@Override
//    public boolean onMenuItemClick(MenuItem menuItem) {
//        switch (menuItem.getItemId()) {
//            case R.id.action_share1:
////                Toast.makeText(getApplicationContext(), "Share", Toast.LENGTH_SHORT).show();
////                ShareDialogFragment dialog = ShareDialogFragment.newInstance(mNews.get(mViewPager.getCurrentItem()).getTitle(), mNews.get(mViewPager.getCurrentItem()).getNewsLink(), mNews.get(mViewPager.getCurrentItem()).getImageUrl());
////                dialog.show(getSupportFragmentManager(), "");
//                Intent i = new Intent(this, ShareActivity.class);
//                i.putExtra(Constants.NEWS_TITLE, mNews.get(mViewPager.getCurrentItem()).getTitle());
//                i.putExtra(Constants.NEWS_URL, mNews.get(mViewPager.getCurrentItem()).getNewsLink());
//                i.putExtra(Constants.NEWS_IMAGE_URL, mNews.get(mViewPager.getCurrentItem()).getImageUrl());
//                startActivity(i);
//                return true;
//        }
//        return false;
//    }

    void getNews(int catId) {
        if (catId == 0)
            mNews = (ArrayList<NewsItem>) NewsManager.getInstance().getNewsItemsFromCache();
        else
            mNews = (ArrayList<NewsItem>) DataManager.getInstance().getNews(catId);
    }

    void setCurrentItemPosition() {
        Bundle b = getIntent().getExtras();
        if (b != null) {
            mViewPager.setCurrentItem(b.getInt(Constants.CURRENT_ITEM_POSITION));
        }

    }

    @Override
    public void onSetCategotyTitle() {
        setActionBarTitle(mNews.get(mViewPager.getCurrentItem()).getCategory().getTitle().toUpperCase());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }
}
