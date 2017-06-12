/*
 * Copyright (C) 2013 The Android Open Source Project
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

package org.church.volyn.ui.slidingTabsView;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Locale;

import org.church.volyn.R;
import org.church.volyn.ui.activity.MainActivity;
import org.church.volyn.ui.media.MediaFragment;
import org.church.volyn.ui.fragment.CategoryRecyclerViewFragment;
import org.church.volyn.ui.fragment.NewsRecyclerViewFragment;

public class SlidingTabsBasicFragment extends Fragment {

    static final String LOG_TAG = "SlidingTabsBasicFragment";
    private SlidingTabLayout mSlidingTabLayout;
    private SamplePagerAdapter mPagerAdapter;
    private Toolbar toolbar;
    static final int TABS_QUANTITY = 3;

    private ViewPager mViewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        mPagerAdapter = new SamplePagerAdapter(getFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setOffscreenPageLimit(2);
        mSlidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setViewPager(mViewPager);
//        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
//        toolbar.setLogo(R.drawable.action_logo);
    }

    public String getChildFragmentTag(int position)  {
        return "android:switcher:" + mViewPager.getId() + ":" + position;
    }


    public class SamplePagerAdapter extends FragmentPagerAdapter {

        public SamplePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position){
                case 0:
                    return NewsRecyclerViewFragment.newInstance(0);
                case 1:
                    return CategoryRecyclerViewFragment.newInstance();
                case 2:
                    return MediaFragment.newsInsance();
                default:
                    return MainActivity.PlaceholderFragment.newInstance(position + 1);
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return TABS_QUANTITY;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.news).toUpperCase(l);
                case 1:
                    return getString(R.string.categories).toUpperCase(l);
                case 2:
                    return getString(R.string.media).toUpperCase(l);
                case 3:
                    return "Об'єкти";
                case 4:
                    return "Аудіо";
                case 5:
                    return "Інше";
//                    return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
        }
    }
}
