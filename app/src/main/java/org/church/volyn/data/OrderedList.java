package org.church.volyn.data;

import android.annotation.TargetApi;
import android.os.Build;

import java.util.TreeSet;

/**
 * Created by Admin on 11.02.2015.
 */
public class OrderedList<E> extends TreeSet {
    private E[] elements;
    public OrderedList() {
        super();
        elements = (E[])new Object[0];
    }

    @Override
    public boolean add(Object object) {
        convertToArray();
        return super.add(object);
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    @Override
    public Object pollFirst() {
        convertToArray();
        return super.pollFirst();
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    @Override
    public Object pollLast() {
        convertToArray();
        return super.pollLast();
    }

    public E get(int position){
        return elements[position];
    }

    private void convertToArray() {
        elements = (E[]) this.toArray();
    }


}
