package org.church.volyn.data;

import android.annotation.TargetApi;
import android.os.Build;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.church.volyn.entities.NewsItem;
import org.church.volyn.entities.NewsItemGuidComparator;

/**
 * Created by Admin on 11.02.2015.
 */
public class NewsCache extends LinkedList {
//    private NewsItem[] elements;
    private HashMap<String, NewsItem> hashMap;
    private NewsItemGuidComparator guidComparator;
    public NewsCache() {
        super();
//        elements = (NewsItem[])new Object[0];
        hashMap = new HashMap<String, NewsItem>();
        guidComparator = new NewsItemGuidComparator();
    }

    @Override
    public boolean add(Object object) {
        if (!hashMap.containsKey(((NewsItem) object).getNewsLink())) {
            super.add(object);
            addNews((NewsItem) object);
        }
        return true;
    }

    @Override
    public boolean addAll(Collection collection) {
        ArrayList<NewsItem> list = (ArrayList) collection;
        for ( NewsItem newsItem : list ){
            this.add(newsItem);
//            add(newsItem);
        }
        return true;
    }

    private void sortByDate() {
        Collections.sort(this, guidComparator);
    }

    private void addNews(NewsItem newsItem) {
        hashMap.put(newsItem.getNewsLink(), newsItem);
        sortByDate();
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    @Override
    public Object pollFirst() {
        sortByDate();
        return super.pollFirst();
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    @Override
    public Object pollLast() {
        sortByDate();
        return super.pollLast();
    }

    public NewsItem get(String key) {
        return hashMap.get(key);
    }

    public List<NewsItem> getNews() {
        return new ArrayList<NewsItem>(this) ;
    }
}
