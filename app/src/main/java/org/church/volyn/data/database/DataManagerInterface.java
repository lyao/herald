package org.church.volyn.data.database;

import java.util.List;

import org.church.volyn.entities.NewsItem;

/**
 * Created by user on 23.01.2015.
 */
public interface DataManagerInterface {

    public NewsItem getNews(String url);
    public List<NewsItem> getNews(int categoryId);
    public long saveNews(NewsItem newsItem);
}
