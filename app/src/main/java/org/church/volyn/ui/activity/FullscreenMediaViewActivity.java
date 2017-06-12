
package org.church.volyn.ui.activity;

import android.content.pm.ActivityInfo;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

import org.church.volyn.R;
import org.church.volyn.config.Settings;
import org.church.volyn.data.database.DataManager;
import org.church.volyn.downloadHelper.DiskCache;
import org.church.volyn.entities.NewsItem;
import org.church.volyn.utils.ExtraParams;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenMediaViewActivity extends AppCompatActivity {

    private String mNewsGuid;
    private String mTitle;
    private String mYoutubeVideo;
    private ImageView mSinglePhotoImageView;
    private TextView mTitleView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_single_photo_layout);
        Bundle b = getIntent().getExtras();
        if (b != null) {
            mNewsGuid = b.getString(ExtraParams.NEWS_GUID);
            mTitle = b.getString(ExtraParams.NEWS_TITLE);
            mYoutubeVideo = b.getString(ExtraParams.YOUTUBE_VIDEO);
        }
        if (mYoutubeVideo != null && !mYoutubeVideo.isEmpty()) {
            initVideoView();
        } else {
            initPhotoView();
        }
    }

    private void initCommonView() {

        ((ImageView) findViewById(R.id.close_photo)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mTitleView = (TextView)findViewById(R.id.photo_title);
        mTitleView.setText(mTitle);
    }


    private void initPhotoView() {

        initCommonView();
        mSinglePhotoImageView = (ImageView) findViewById(R.id.gallery_single_photo_view);

        NewsItem newsItem = DataManager.getInstance().getNewsByGuid(mNewsGuid);
        String filePath = DiskCache.getFilePath(newsItem.getImageUrl());
        Glide.with(this).load(filePath)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(mSinglePhotoImageView);
    }

    private void initVideoView() {
        initCommonView();

        YouTubePlayerSupportFragment youTubePlayerFragment =
                (YouTubePlayerSupportFragment) getSupportFragmentManager().findFragmentById(R.id.youtube_fragment);
        youTubePlayerFragment.initialize(Settings.YOUTUBE_KEY, new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                if (!b) {
                    youTubePlayer.loadVideo(mYoutubeVideo);
                    youTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
                    youTubePlayer.setFullscreenControlFlags(0);
                    youTubePlayer.setOnFullscreenListener(new YouTubePlayer.OnFullscreenListener() {
                        @Override
                        public void onFullscreen(boolean isFullscreen) {
                            if (isFullscreen) {
                                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
                            } else {
                                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
                            }
                        }
                    });
                }
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

            }
        });
    }
}
