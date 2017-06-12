package org.church.volyn.ui.media;


import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

import org.church.volyn.config.Settings;

public class MediaPlayerFragment extends YouTubePlayerSupportFragment {


    private String mCurrentVideoID;
    private YouTubePlayer activePlayer;
    private MediaPlayerFragment.Callbacks mCallbacks;
    private YouTubePlayer.OnFullscreenListener mOnFullscreenListener;

    public interface Callbacks {
        void onFullscreen(boolean isFullscreen);
    }
    public static MediaPlayerFragment newInstance(String mediaSource) {

        MediaPlayerFragment playerYouTubeFrag = new MediaPlayerFragment();

        Bundle bundle = new Bundle();
        bundle.putString("url", mediaSource);

        playerYouTubeFrag.setArguments(bundle);
//        playerYouTubeFrag.init();
        return playerYouTubeFrag;
    }

    public MediaPlayerFragment() {
    }


    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        init();
//        mCurrentVideoID = getArguments().getString("url");
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallbacks = (Callbacks) activity;
    }

    public void init() {

        initialize(Settings.YOUTUBE_KEY, new YouTubePlayer.OnInitializedListener() {

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult result) {
                if (result.isUserRecoverableError()) {
                    result.getErrorDialog(getActivity(), 1).show();
                } else {
                    Toast.makeText(getActivity(),
                            "YouTubePlayer.onInitializationFailure(): " + result.toString(),
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
                activePlayer = player;
                activePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
                activePlayer.setFullscreenControlFlags(0);
                activePlayer.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_CUSTOM_LAYOUT);
                activePlayer.setOnFullscreenListener(new YouTubePlayer.OnFullscreenListener() {
                    @Override
                    public void onFullscreen(boolean b) {
                        if (mCallbacks != null) {
                            mCallbacks.onFullscreen(b);
                        }
                    }
                });
                if ((!wasRestored) && (mCurrentVideoID != null)) {
//                    activePlayer.cueVideo("sgHr9HY0k3E");
                    activePlayer.loadVideo(mCurrentVideoID);
                }
            }


        });
    }


    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        init();
        return super.onCreateView(layoutInflater, viewGroup, bundle);
    }

    public void play(String mediaSource) {
        if (activePlayer != null)
            activePlayer.loadVideo(mediaSource);
    }

    public void pause() {
        if (activePlayer != null) {
            activePlayer.pause();
        }
    }
}
