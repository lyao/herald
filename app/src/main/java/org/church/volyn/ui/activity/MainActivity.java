package org.church.volyn.ui.activity;

import android.app.AlarmManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.church.volyn.R;
import org.church.volyn.data.database.DataManager;
import org.church.volyn.downloadHelper.DownloadIntentService;
import org.church.volyn.downloadHelper.NewsManager;
import org.church.volyn.entities.NewsItem;
import org.church.volyn.parser.XmlPullNewsParser;
import org.church.volyn.ui.media.MediaFragment;
import org.church.volyn.ui.fragment.CategoryRecyclerViewFragment;
import org.church.volyn.media.MediaDownloaderIntentService;
import org.church.volyn.ui.media.MediaPlayerFragment;
import org.church.volyn.ui.fragment.NewsRecyclerViewFragment;
import org.church.volyn.ui.slidingTabsView.SlidingTabsBasicFragment;
import org.church.volyn.utils.NetworkUtils;


public class MainActivity extends BaseActivity implements NewsManager.NewsManagerStateNotifier, CategoryRecyclerViewFragment.Callbacks,
        MediaFragment.MediaListeners, MediaPlayerFragment.Callbacks {

    private static final int ANIMATION_DURATION_MILLIS = 300;
    NewsRecyclerViewFragment mNewsRecyclerViewFragment;
    private static final int PERIOD_OF_NEWS_UPDATE = 60 * 60 * 1000; // 1 HOUR
    AlarmManager am;
    AlarmReceiver alarmReceiver;
    View videoBox;
    MediaPlayerFragment mediaPlayerFragment;
    LinearLayout mainLayout;
    ImageView videoCloseButton;
    TextView videoTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if ((savedInstanceState == null) && (NetworkUtils.hasConnection(this)))
            startDownloadService();
        FragmentManager fm = getSupportFragmentManager();
        SlidingTabsBasicFragment mainFragment = (SlidingTabsBasicFragment) fm.findFragmentById(R.id.fragment_container);
        if (mainFragment == null) {
            mainFragment = new SlidingTabsBasicFragment();
            fm.beginTransaction().add(R.id.fragment_container, mainFragment).commit();
        }
//        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("news-download-state"));
        NewsManager.getInstance().setOnAddNewsItemListener(this);
        videoBox = (ViewGroup) findViewById(R.id.video_box);
        videoBox.setVisibility(View.INVISIBLE);
        mainLayout = (LinearLayout) findViewById(R.id.main_layout);
        videoCloseButton = (ImageView) findViewById(R.id.video_close_button);
        videoCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayerFragment.pause();
                ViewPropertyAnimator animator = videoBox.animate().translationYBy(-videoBox.getHeight()).setDuration(ANIMATION_DURATION_MILLIS);
                mainLayout.animate().translationYBy(-videoBox.getHeight()).setDuration(ANIMATION_DURATION_MILLIS);
            }
        });

        videoTitle = (TextView) findViewById(R.id.media_title);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_main;
    }

//    @Override
//    protected void setActionBarTitle(int stringRes) {
//        super.setActionBarTitle(R.string.title_activity_category_detail);
//    }

    @Override
    protected void setActionBarIcon(int iconRes) {
        super.setActionBarIcon(R.drawable.action_logo);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mNewsRecyclerViewFragment != null) {
//            ArrayList<NewsItem> ni = (ArrayList) NewsManager.getInstance().getNewsFromCache();
//            mNewsRecyclerViewFragment.updateRecyclerView(NewsManager.getInstance().getNewsItemsFromCache());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //     NewsManager.setOnAddNewsItemListener(null);
    }

    @Override
    protected void onStart() {
        super.onStart();
//        Intent intent = new Intent(this, DownloadService.class);
//        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
//        unbindService(mConnection);
    }

    private void startDownloadService() {
        if (!XmlPullNewsParser.isWorking) {
            Intent i = new Intent(MainActivity.this, DownloadIntentService.class);
            startService(i);
        }
        Intent i1 = new Intent(MainActivity.this, MediaDownloaderIntentService.class);
        startService(i1);
    }

//    private ServiceConnection mConnection = new ServiceConnection() {
//
//        @Override
//        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
//            not_used_DownloadService.LocalBinder binder = (not_used_DownloadService.LocalBinder) iBinder;
//            mService = binder.getService();
////            mBound = true;
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName componentName) {
////            mBound = false;
//        }
//    };

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
//            String url = intent.getStringExtra("message");
//            NewsRecyclerViewFragment newsFragment = (NewsRecyclerViewFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
//            newsFragment.updateRecyclerView(NewsManager.getInstance().getNewsItemFromCache(url));
//            mNews.add(NewsManager.getNewsItemFromCache(url));
//            mNewsAdapter.notifyDataSetChanged();
//            mNewsAdapter.notifyDataSetChanged();
        }
    };

    @Override
    public void onItemAdded(NewsItem newsItem) {
        SlidingTabsBasicFragment ff = (SlidingTabsBasicFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        NewsRecyclerViewFragment f = (NewsRecyclerViewFragment) getSupportFragmentManager().findFragmentByTag(ff.getChildFragmentTag(0));
        f.updateRecyclerView(newsItem);
    }

    @Override
    public void onDatasetIsNotEmpty() {

    }

//    @Override
//    public void onCategoryClicked(int categoryId) {
//        Intent i = new Intent(this, CategoryDetailActivity.class);
//        Bundle bundle = new Bundle();
//        bundle.putInt(Constants.CATEGORY_ID, categoryId);
//        i.putExtras(bundle);
//        startActivity(i);
//    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.menu_main, menu);
//        return super.onCreateOptionsMenu(menu);
//    }

    private void initPeriodicallyDownloadServiceRunning() {
        alarmReceiver = new AlarmReceiver();
        registerReceiver(alarmReceiver, new IntentFilter("ua.church.volyn"));
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0,
                new Intent("ua.church.volyn"),
                PendingIntent.FLAG_UPDATE_CURRENT);
        am = (AlarmManager) (this.getSystemService(Context.ALARM_SERVICE));
        am.setRepeating(AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + PERIOD_OF_NEWS_UPDATE, PERIOD_OF_NEWS_UPDATE, pendingIntent);
    }

//    @Override
//    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
//        if (!b)
//            youTubePlayer.cueVideo("sgHr9HY0k3E");
////                    activePlayer.cueVideo(mCurrentVideoID);
//    }
//
//    @Override
//    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
//
//    }

    @Override
    public void onMediaClick(String mediaSource, String mediaTitle) {

        mediaPlayerFragment = (MediaPlayerFragment) getSupportFragmentManager().findFragmentById(R.id.youtube_fragment);
        mediaPlayerFragment.play(mediaSource);
        videoTitle.setText(mediaTitle);
//            videoBox.setTranslationY(-videoBox.getHeight());
//            videoBox.setVisibility(View.VISIBLE);
//            videoBox.animate().translationY(0).setDuration(1000);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        // The videoBox is INVISIBLE if no video was previously selected, so we need to show it now.
        if (videoBox.getVisibility() != View.VISIBLE) {
            videoBox.setTranslationY(-videoBox.getHeight());
            videoBox.setVisibility(View.VISIBLE);
        }

        if (videoBox.getTranslationY() < 0) {
            videoBox.animate().translationY(0).setDuration(ANIMATION_DURATION_MILLIS);
            mainLayout.animate().translationY(videoBox.getHeight()).setDuration(ANIMATION_DURATION_MILLIS);
        }

    }

    @Override
    public void onFullscreen(boolean isFullscreen) {
        if (isFullscreen) {
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            videoTitle.setVisibility(View.GONE);
            videoCloseButton.setVisibility(View.GONE);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            videoTitle.setVisibility(View.VISIBLE);
            videoCloseButton.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onCategoryClicked(int categoryId, long[] categoriesIds) {

    }

    public class AlarmReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            context.startService(new Intent(context,
                    DownloadIntentService.class));
        }
    }


    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_placeholder, container, false);
            TextView tv1 = (TextView) rootView.findViewById(R.id.section_label);
            tv1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DataManager.getInstance().prepareNewsForCleaning();
                }
            });

            TextView tv2 = (TextView) rootView.findViewById(R.id.section_label2);
            tv2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DataManager.getInstance().cleanNews();

                }
            });

            return rootView;
        }
    }

}
