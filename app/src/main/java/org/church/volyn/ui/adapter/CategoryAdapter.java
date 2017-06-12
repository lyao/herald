/*
* Copyright (C) 2014 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package org.church.volyn.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.church.volyn.App;
import org.church.volyn.R;
import org.church.volyn.data.database.DataManager;
import org.church.volyn.downloadHelper.NewsManager;
import org.church.volyn.downloadHelper.DiskCache;
import org.church.volyn.downloadHelper.ImageCache;
import org.church.volyn.entities.Category;


/**
 * Provide views to RecyclerView with data from mDataSet.
 */
public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    private static final String TAG = "CustomAdapter";
    private static Context mContext;
    private OnItemClickListener mOnItemClickListener;
    private List<Category> mDataSet;
    private HashMap<Long, Bitmap> mCategoryBitmapMap;
    private HashSet<Category> mDisplayedCategories;
    public interface OnItemClickListener {
        public void onItemClick(View v, int position, long[] categoriesIds);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public CategoryAdapter(List<Category> dataSet, Context context) {
        mContext = context;
        mDataSet = dataSet;
        mDisplayedCategories = new HashSet<Category>();
        for (Category cat: mDataSet) {
            mDisplayedCategories.add(cat);
        }
        initCategoryBitmapMap();
    }

    public void addCategoryToDataset(Category category) {
        if (category != null && !mDisplayedCategories.contains(category)) {
            mDataSet.add(category);
            mDisplayedCategories.add(category);
            notifyDataSetChanged();
        } else {
            notifyDataSetChanged();
        }
    }

    public void addBitmapByUrl(long catId, String url) {
        mCategoryBitmapMap.put(catId, DiskCache.getInstance(mContext).getBitmap(url));
        Bitmap b = DiskCache.getInstance(mContext).getBitmap(url);
        if (b == null){
            b = ImageCache.getInstance(App.getContext()).getFromMemCache(url);
        }

        notifyDataSetChanged();

    }
    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.category_item_row, viewGroup, false);
        return new CategoryViewHolder(v);
    }

    @Override
    public void onBindViewHolder(CategoryViewHolder viewHolder, final int position) {
        Category category = mDataSet.get(position);
        viewHolder.bind(category);
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private final TextView titleTextView;
        //private final TextView newsDateTextView;
        private final ImageView categoryImageView;
        private Category mCategory;

        public CategoryViewHolder(View v) {
            super(v);
            // Define click listener for the ViewHolder's View.
            v.setOnClickListener(this);
            titleTextView = (TextView)v.findViewById(R.id.category_title);
            categoryImageView = (ImageView) v.findViewById(R.id.category_image);
        }

        public void bind(Category category) {
            mCategory = category;
            titleTextView.setText(mCategory.getTitle());
            categoryImageView.setVisibility(View.VISIBLE);
            Bitmap bm = mCategoryBitmapMap.get(new Long(category.getId()));
            if (bm != null) categoryImageView.setImageBitmap(bm);
        }

        @Override
        public void onClick(View view) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(view, getPosition(), getCategoriesIds());
            }
        }
    }

    public void initCategoryBitmapMap() {
        HashMap<Long, String> hashMap = (HashMap<Long, String>) DataManager.getInstance().getCategoriesBitmapsFileNames();
        if (hashMap.size() == 0) hashMap = NewsManager.getInstance().getCategoriesBitmapFileNames();
        mCategoryBitmapMap = new HashMap<Long, Bitmap>();

        for(Map.Entry<Long, String> entry : hashMap.entrySet()) {
            Long key = entry.getKey();
            String value = entry.getValue();
            mCategoryBitmapMap.put(key, DiskCache.getInstance(mContext).getBitmap(value));
            // do what you have to do here
            // In your case, an other loop.
        }
    }

    private long [] getCategoriesIds() {
        long[] arr = new long[mDataSet.size()];
        for (int i = 0; i < mDataSet.size(); i++) {
            arr[i] = mDataSet.get(i).getId();
        }
        return  arr;
    }

}
