package org.church.volyn.ui.activity;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.AlarmManager;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
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
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewStub;
import android.widget.FrameLayout;

import com.crashlytics.android.Crashlytics;

import org.church.volyn.Constants;
import org.church.volyn.R;
import org.church.volyn.config.Settings;
import org.church.volyn.data.database.DataManager;
import org.church.volyn.downloadHelper.DownloadIntentService;
import org.church.volyn.downloadHelper.NewsManager;
import org.church.volyn.ui.media.MediaFragment;
import org.church.volyn.entities.NewsItem;
import org.church.volyn.ui.media.MediaPlayerFragment;
import org.church.volyn.ui.fragment.CategoryRecyclerViewFragment;
import org.church.volyn.ui.fragment.NewsRecyclerViewFragment;
import org.church.volyn.ui.slidingTabsView.SlidingTabsBasicFragment;
import org.church.volyn.ui.view.VideoBoxView;
import org.church.volyn.utils.IntentWrapper;
import org.church.volyn.utils.NetworkUtils;

import io.fabric.sdk.android.Fabric;


public class MainPhoneActivity extends AppCompatActivity implements NewsManager.NewsManagerStateNotifier, CategoryRecyclerViewFragment.Callbacks,
        MediaFragment.MediaListeners, MediaPlayerFragment.Callbacks, Animator.AnimatorListener, VideoBoxView.onCloseButtonListener {

    private static final int ANIMATION_DURATION_MILLIS = 300;
    NewsRecyclerViewFragment mNewsRecyclerViewFragment;
    private static final int PERIOD_OF_NEWS_UPDATE = 60 * 60 * 1000; // 1 HOUR
    AlarmManager am;
    AlarmReceiver alarmReceiver;
    VideoBoxView videoBox;
    //LinearLayout mainLayout;
    ViewStub splashViewStub;
    View splashView;
    Handler mHandler;
    FrameLayout mFragmentContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        enableCrashlitics();
        splashViewStub = (ViewStub) findViewById(R.id.splash_screen_viewstub);
        splashViewStub.setLayoutResource(R.layout.fragment_launch);
        splashView = splashViewStub.inflate();

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Bundle reply = msg.getData();
                SlidingTabsBasicFragment ff = (SlidingTabsBasicFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                if (ff != null) {
                    MediaFragment f = (MediaFragment) getSupportFragmentManager().findFragmentByTag(ff.getChildFragmentTag(2));
                    f.build();

                }
            }
        };

        if ((savedInstanceState == null) && (NetworkUtils.hasConnection(this))) {
            IntentWrapper.startDownloadService(this);
//            IntentWrapper.startMediaDownloadService(this);
            IntentWrapper.startMediaDownloadService(this, mHandler);
        }

        FragmentManager fm = getSupportFragmentManager();
        SlidingTabsBasicFragment mainFragment = (SlidingTabsBasicFragment) fm.findFragmentById(R.id.fragment_container);
        if (mainFragment == null) {
            mainFragment = new SlidingTabsBasicFragment();
            fm.beginTransaction().add(R.id.fragment_container, mainFragment).commit();
        }

        NewsManager.getInstance().setOnAddNewsItemListener(this);

        mHandler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                hideSplashScreen();
            }
        };
        mHandler.postDelayed(runnable, 10000);
        mFragmentContainer = (FrameLayout) findViewById(R.id.fragment_container);

        videoBox = (VideoBoxView) findViewById(R.id.video_box);
        videoBox.setVisibility(View.INVISIBLE);
//        mainLayout = (LinearLayout) findViewById(R.id.main_layout);
        videoBox.setOnCloseButtonListener(this);
        videoBox.setAnchor(mFragmentContainer);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //     NewsManager.setOnAddNewsItemListener(null);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        DataManager.getInstance().cleanNews();
    }

    private void enableCrashlitics() {
        if (!Settings.DEBUG) Fabric.with(this, new Crashlytics());
    }

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
        if (ff != null) {
            NewsRecyclerViewFragment f = (NewsRecyclerViewFragment) getSupportFragmentManager().findFragmentByTag(ff.getChildFragmentTag(0));
            f.updateRecyclerView(newsItem);
            hideSplashScreen();
        }
    }

    @Override
    public void onDatasetIsNotEmpty() {
        hideSplashScreen();
    }

    @Override
    public void onCategoryClicked(int position, long[] categoriesIds) {
//        Intent i = new Intent(this, CategoryDetailActivity.class);
        Intent i = new Intent(this, NewsByCategoryPagerActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.CATEGORY_POSITION, position);
        bundle.putLongArray(Constants.CATEGORIES, categoriesIds);
        i.putExtras(bundle);
        startActivity(i);
    }

    private void hideSplashScreen() {
        if (splashView.getVisibility() == View.VISIBLE) {
            ValueAnimator valueAnimator = ObjectAnimator.ofFloat(splashView, "alpha", 1f, 0f);
            valueAnimator.setStartDelay(1000);
            valueAnimator.addListener(this);
            valueAnimator.setDuration(splashView.getContext().getResources().getInteger(android.R.integer.config_mediumAnimTime)).start();
        }

    }
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

    @Override
    public void onMediaClick(String mediaSource, String mediaTitle) {

        videoBox.play(mediaSource);
        videoBox.setMediaTitle(mediaTitle);
        videoBox.show();

    }

    @Override
    public void onCloseButtonClick() {
        videoBox.hide();
    }

    @Override
    public void onFullscreen(boolean isFullscreen) {

        if (isFullscreen) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        }

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            videoBox.setMediaTitleVisibility(View.GONE);
            videoBox.setCloseButtonVisibility(View.GONE);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            videoBox.setMediaTitleVisibility(View.VISIBLE);
            videoBox.setCloseButtonVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onAnimationStart(Animator animator) {

    }

    @Override
    public void onAnimationEnd(Animator animator) {
        splashView.setVisibility(View.GONE);
    }

    @Override
    public void onAnimationCancel(Animator animator) {

    }

    @Override
    public void onAnimationRepeat(Animator animator) {

    }

    public class AlarmReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            context.startService(new Intent(context,
                    DownloadIntentService.class));
        }
    }

}
