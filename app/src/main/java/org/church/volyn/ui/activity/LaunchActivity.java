package org.church.volyn.ui.activity;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;

import org.church.volyn.R;
import org.church.volyn.config.Settings;
import org.church.volyn.utils.IntentWrapper;

public class LaunchActivity extends FragmentActivity {

    private static final String TAG = LaunchActivity.class.getSimpleName();

    private ItemHolder mItemHolder;
    private int stopPosition;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (!IntentWrapper.IS_TABLET) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            if (!Settings.ENABLE_ROTATION_FOR_TABLET) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_launch);
        mItemHolder = new ItemHolder();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                launchNextActivity();
            }
        }, 1000);
        //launchNextActivity();
        }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void launchNextActivity() {
        if (true) {
            IntentWrapper.openMainActivity(this);
        } else {
//            IntentWrapper.openWelcomeActivity(this);
        }
        finish();
    }

    private class ItemHolder {


        public ItemHolder() {
//            videoView = (VideoView) findViewById(R.id.video_view);
        }
    }
}
