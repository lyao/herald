package org.church.volyn.entities;

import java.util.Comparator;

/**
 * Created by Admin on 12.02.2015.
 */
public class NewsItemGuidComparator implements Comparator<NewsItem> {

    @Override
    public int compare(NewsItem newsItem, NewsItem newsItem2) {
        return newsItem2.getGuid().compareTo(newsItem.getGuid());
    }
}
