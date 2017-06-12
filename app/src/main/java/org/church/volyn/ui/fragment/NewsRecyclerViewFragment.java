package org.church.volyn.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import org.church.volyn.Constants;
import org.church.volyn.R;
import org.church.volyn.data.database.DataManager;
import org.church.volyn.downloadHelper.ImageManager;
import org.church.volyn.downloadHelper.NewsManager;
import org.church.volyn.entities.NewsItem;
import org.church.volyn.entities.NewsItemGuidComparator;
import org.church.volyn.ui.activity.NewsPagerActivity;
import org.church.volyn.ui.adapter.NewsAdapter;
import org.church.volyn.utils.IntentWrapper;
import org.church.volyn.utils.NetworkUtils;


public class NewsRecyclerViewFragment extends Fragment implements NewsAdapter.OnItemClickListener {

    public static final int ITEMS_ON_SCREEN = 100;
    protected RecyclerView mRecyclerView;
    ArrayList<NewsItem> mNews;
    HashSet<String> mDisplayedNews;
    NewsAdapter mNewsAdapter;
    int mCategoryId;
    SwipeRefreshLayout mRefresh;
    ProgressBar mProgressBar;
    public NewsRecyclerViewFragment() {
    }

    public static NewsRecyclerViewFragment newInstance(int categoryId) {
        Bundle args = new Bundle();
        args.putInt(Constants.CATEGORY_ID, categoryId);
        NewsRecyclerViewFragment fragment = new NewsRecyclerViewFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCategoryId = getArguments().getInt(Constants.CATEGORY_ID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recycler_view, container, false);
        //mNews = new ArrayList<NewsItem>();
        mNews = (ArrayList<NewsItem>) DataManager.getInstance().getNews(mCategoryId);
        if (mNews.size() != 0) NewsManager.getInstance().onDatasetIsNotEmpty();
        mDisplayedNews = new HashSet<>();
        mNewsAdapter = new NewsAdapter(mNews, getActivity());

        for (NewsItem news: mNews) {
            mDisplayedNews.add(news.getNewsLink());
        }

        mNewsAdapter.setOnItemClickListener(this);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        //((SimpleItemAnimator) mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        mRecyclerView.setAdapter(mNewsAdapter);
        ImageManager.setNewsAdapter(mNewsAdapter);
      //  if (!XmlPullNewsParser.isWorking)
          //  refreshNewsList();
        mRefresh = (SwipeRefreshLayout) rootView.findViewById(R.id.refresh_content);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.loading_spinner);
        if (mNews.size() == 0) mProgressBar.setVisibility(View.VISIBLE);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRefresh.setProgressViewOffset(true, -20, 150);
        mRefresh.setDistanceToTriggerSync(200);
        mRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if (NetworkUtils.hasConnection(getActivity())) {
                    IntentWrapper.startDownloadService(getActivity());
                    mRefresh.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mRefresh.setRefreshing(false);
                        }
                    }, 3000);
                } else {
                    mRefresh.setRefreshing(false);
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle(getString(R.string.warning));
                    builder.setMessage(getString(R.string.no_internet_connection));
                    builder.setPositiveButton("OK", null);
                    builder.show();
                }
            }
        });
        if (mCategoryId != 0) mRefresh.setEnabled(false);

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void refreshNewsList() {
        mNews.clear();
        mDisplayedNews.clear();
        ArrayList<NewsItem> news = (ArrayList<NewsItem>) DataManager.getInstance().getNews(mCategoryId);
        for (NewsItem ni : news) {
            updateRecyclerView(ni);
        }
    }

    public void updateRecyclerView(NewsItem newsItem) {
        if ((mCategoryId > 0) && (mCategoryId != newsItem.getCategory().getId()))
            return;
        int position = addNewsItem(newsItem);
        if (position > -1) {
            mNewsAdapter.notifyItemInserted(position);
        }
        if (position > -1 && mProgressBar.getVisibility() == View.VISIBLE) mProgressBar.setVisibility(View.GONE);
    }

    private int addNewsItem(NewsItem newsItem) {
        int position = -1;
        if (mDisplayedNews.contains(newsItem.getNewsLink())) {
            return position;
        }
//        if (mNews.size() >= ITEMS_ON_SCREEN) {
//            mNews.remove(mNews.size() - 1);
//        }
        mNews.add(newsItem);
        Collections.sort(mNews, new NewsItemGuidComparator());
        mDisplayedNews.add(newsItem.getNewsLink());
        position = mNews.indexOf(newsItem);
        return position;
    }

    @Override
    public void onItemClick(View v, int position) {
        //IntentWrapper.openGalleryActivity(getActivity(), mNews.get(position).getGuid());
        Intent i = new Intent(getActivity(), NewsPagerActivity.class);
        i.putExtra(Constants.NEWS_URL, mNews.get(position).getNewsLink());
        i.putExtra(Constants.CURRENT_ITEM_POSITION, position);
        i.putExtra(Constants.CATEGORY_ID, mCategoryId);
        startActivity(i);
    }
}
