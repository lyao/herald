package org.church.volyn.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import org.church.volyn.Constants;
import org.church.volyn.R;
import org.church.volyn.data.database.DataManager;
import org.church.volyn.downloadHelper.NewsManager;
import org.church.volyn.entities.NewsItem;
import org.church.volyn.ui.fragment.NewsRecyclerViewFragment;

import java.util.ArrayList;

/**
 * Created by user on 31.01.2015.
 */
public class NewsByCategoryPagerActivity extends BaseActivity  {
    private ViewPager mViewPager;
    private ArrayList<NewsItem> mNews;
    private int mCategoryPosition = 0;
    long[] mCategories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCategories = new long[]{};

        mViewPager = (ViewPager) findViewById(R.id.news_view_pager);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mCategoryPosition = extras.getInt(Constants.CATEGORY_POSITION);
            mCategories = extras.getLongArray(Constants.CATEGORIES);
        }

        getNews(new Long(mCategories[mCategoryPosition]).intValue());
        FragmentManager fm = getSupportFragmentManager();

        mViewPager.setAdapter(new FragmentStatePagerAdapter(fm) {
            @Override
            public Fragment getItem(int position) {

                return NewsRecyclerViewFragment.newInstance(new Long(mCategories[position]).intValue());
                //return NewsDetailFragment.newInstance(mNews.get(position).getNewsLink(), position);
            }

            @Override
            public int getCount() {
                return mCategories.length;
            }
        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                toolbar.setTitle(DataManager.getInstance().getCategoryById(new Long(mCategories[position]).intValue()).getTitle().toUpperCase());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        setCurrentItemPosition();

    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_news_pager;
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.menu_category_detail, menu);
//        return super.onCreateOptionsMenu(menu);
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
            mViewPager.setCurrentItem(mCategoryPosition);
            toolbar.setTitle(DataManager.getInstance().getCategoryById(new Long(mCategories[mCategoryPosition]).intValue()).getTitle().toUpperCase());
        }

    }

    @Override
    protected void setActionBarBackButton(int drawable) {
        super.setActionBarBackButton(R.drawable.ic_back);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

 }
