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
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import org.church.volyn.R;
import org.church.volyn.downloadHelper.ImageFetcher;
import org.church.volyn.ui.view.NewsImageView;
import org.church.volyn.entities.NewsItem;
import org.church.volyn.utils.DateUtils;


/**
 * Provide views to RecyclerView with data from mDataSet.
 */
public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {
    private static final String TAG = "CustomAdapter";
    private static Context mContext;
    private OnItemClickListener mOnItemClickListener;
    private ArrayList<NewsItem> mDataSet;
    private ImageFetcher mImageFetcher;

    public interface OnItemClickListener {
        public void onItemClick(View v, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }
    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used by RecyclerView.
     */
    public NewsAdapter(ArrayList<NewsItem> dataSet, Context context) {
        mContext = context;
        mDataSet = dataSet;
        mImageFetcher = new ImageFetcher(context);
    }


    // Create new views (invoked by the layout manager)
    @Override
    public NewsViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.news_item_row, viewGroup, false);
        return new NewsViewHolder(v);
    }



    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(NewsViewHolder viewHolder, final int position) {
        NewsItem newsItem = mDataSet.get(position);
        viewHolder.bindNews(newsItem);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    public class NewsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private final TextView newsTitleTextView;
        private final TextView newsDateTextView;
        private final NewsImageView newsImageView;
        private String mImageLink;
        private NewsItem mNewsItem;

        public NewsViewHolder(View v) {
            super(v);
            // Define click listener for the ViewHolder's View.
            v.setOnClickListener(this);
//            Typeface type = Typeface.createFromAsset(mContext.getAssets(),"fonts/proximanova_small_bold.otf");
            newsTitleTextView = (TextView)v.findViewById(R.id.news_title);
//            newsTitleTextView.setTypeface(type);
//            newsTitleTextView.setTypeface(null, Typeface.BOLD);
            newsDateTextView = (TextView)v.findViewById(R.id.news_date);
//            Typeface type1 = Typeface.createFromAsset(mContext.getAssets(),"fonts/proximanova_thin.otf");
//            newsDateTextView.setTypeface(type1);
            newsImageView = (NewsImageView) v.findViewById(R.id.news_image);
        }

        public void bindNews(NewsItem newsItem) {
            mNewsItem = newsItem;
            String dateString = DateUtils.getDate(mNewsItem.getPubDate(), "dd.MM.yyyy");
            newsTitleTextView.setText(mNewsItem.getTitle());
//            newsDateTextView.setText(dateString);
            newsDateTextView.setText(mNewsItem.getCategory().getTitle());
            newsImageView.setNewsItem(mNewsItem);
            newsImageView.setVisibility(View.VISIBLE);
            if (newsItem.getImageUrl() != null) {
                mImageFetcher.loadImage(newsItem.getImageUrl(), newsImageView);
            }
            else {
                newsImageView.setImageBitmap(null);
            }
        }

        @Override
        public void onClick(View view) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(view, getPosition());
            }
        }
    }
}
