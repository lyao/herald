package org.church.volyn.ui.fragment;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.HashSet;
import java.util.List;

import org.church.volyn.R;
import org.church.volyn.data.database.DataManager;
import org.church.volyn.downloadHelper.ImageManager;
import org.church.volyn.downloadHelper.NewsManager;
import org.church.volyn.entities.Category;
import org.church.volyn.ui.adapter.CategoryAdapter;


public class CategoryRecyclerViewFragment extends Fragment implements CategoryAdapter.OnItemClickListener,
        NewsManager.NewsManagerCategoriesNotifier, ImageManager.Notifier {

    public static final int ITEMS_ON_SCREEN = 100;
    protected RecyclerView mRecyclerView;
    List<Category> mCategories;
    CategoryAdapter mCategoryAdapter;
    Callbacks mCallbacks;
    private HashSet<Category> mDisplayedCategories;
    SwipeRefreshLayout mRefresh;
    ProgressBar mProgressBar;


    public CategoryRecyclerViewFragment() {
    }

    public static CategoryRecyclerViewFragment newInstance() {
        CategoryRecyclerViewFragment fragment = new CategoryRecyclerViewFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NewsManager.getInstance().setOnAddCategoryListener(this);
        mDisplayedCategories = new HashSet<Category>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recycler_view, container, false);
       // mCategories = new ArrayList<Category>();
        initCategories();
        mCategoryAdapter = new CategoryAdapter(mCategories, getActivity());
        mCategoryAdapter.setOnItemClickListener(this);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mCategoryAdapter);
        mRefresh = (SwipeRefreshLayout)rootView.findViewById(R.id.refresh_content);
        mRefresh.setEnabled(false);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.loading_spinner);
        if (mCategories.size() == 0) mProgressBar.setVisibility(View.VISIBLE);
        return rootView;
    }

    private void initCategories() {
        mCategories = DataManager.getInstance().getNonEmptyCategories();
        if (mCategories.size() == 0) {
            NewsManager.getInstance().setNeedUseCategoriesCache(true);
            ImageManager.setNotifier(this);
        } else {
            NewsManager.getInstance().setNeedUseCategoriesCache(false);
            ImageManager.setNotifier(null);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallbacks = (Callbacks)activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onItemClick(View v, int position, long[] categoriesIds) {
        mCallbacks.onCategoryClicked(position, categoriesIds);
    }

    @Override
    public void onCategoriesAdded(Category category) {

        mCategoryAdapter.addCategoryToDataset(category);
        if (mProgressBar.getVisibility() == View.VISIBLE) mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onBitmapLinkAdded(Long catId, String url) {

        ImageManager.getInstance().registerCategoryObserver(catId, url);
    }

    @Override
    public void onImageDownloaded(Long id, String url) {
        mCategoryAdapter.addBitmapByUrl(id, url);
    }

    public interface Callbacks {
        public void onCategoryClicked(int position, long[] categoriesIds);
    }

}
