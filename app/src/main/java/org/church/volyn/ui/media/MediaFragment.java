package org.church.volyn.ui.media;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import org.apache.commons.net.ftp.FTPClient;

import java.util.ArrayList;
import java.util.List;

import org.church.volyn.R;
import org.church.volyn.media.MediaContainer;
import org.church.volyn.media.MediaDownloaderIntentService;
import org.church.volyn.media.MediaManager;
import org.church.volyn.utils.NetworkUtils;

/**
 * Created by user on 10.09.2015.
 */
public class MediaFragment extends Fragment implements MediaContainerAdapter.OnMediaItemClickListener {
    private FTPClient mFtpClient;
    private List<MediaContainer> mMediaContainerList;
    private MediaContainerAdapter mMediaAdapter;
    private MediaListeners mCallbacks;
    private ScrollView mMediaScrollView;
    private LinearLayout mMediaContainer;
    private SwipeRefreshLayout mRefresh;
    private ProgressBar mProgress;
    private TextView mInternetMessage;

    @Override
    public void onMediaItemClick(String mediaSource, String mediaTitle) {
        if (mCallbacks != null) {
            mCallbacks.onMediaClick(mediaSource, mediaTitle);
        }
    }

    public interface MediaListeners {
        void onMediaClick(String mediaSource, String mediaTitle);
    }

    public MediaFragment() {

    }

    public static MediaFragment newsInsance() {
        return new MediaFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMediaContainerList = new ArrayList<>();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_media, container, false);
        mMediaContainer = (LinearLayout) v.findViewById(R.id.media_container);
        mMediaScrollView = (ScrollView) v.findViewById(R.id.media_scrollview);
        mProgress = (ProgressBar) v.findViewById(R.id.loading_spinner);
        mInternetMessage = (TextView) v.findViewById(R.id.no_internet_message);
        mRefresh = (SwipeRefreshLayout) v.findViewById(R.id.refresh_media_content);
        mRefresh.setProgressViewOffset(true, -20, 250);
        mRefresh.setDistanceToTriggerSync(200);
        mRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (NetworkUtils.hasConnection(getActivity())) {
                    MediaAsyncTask at = new MediaAsyncTask();
                    at.execute();
//                    IntentWrapper.startDownloadService(getActivity());
                    mRefresh.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mRefresh.setRefreshing(false);
                        }
                    }, 10000);
                } else {
                    mRefresh.setRefreshing(false);
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle(getString(R.string.warning));
                    builder.setMessage(getString(R.string.no_internet_connection_check_and_apdate));
                    builder.setPositiveButton("OK", null);
                    builder.show();
                }
            }
        });

        buildMediaView();

        return v;
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if (menuVisible && mMediaContainer.getChildCount() == 0) buildMediaView();
    }

    private class MediaAsyncTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            MediaDownloaderIntentService.getMedias();
            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (mRefresh.isRefreshing())  mRefresh.setRefreshing(false);
            buildMediaView();
            mMediaScrollView.setVisibility(View.VISIBLE);
        }
    }

    public void build() {
        buildMediaView();
    }
    private void buildMediaView() {
        if (mMediaContainerList.size() == 0) {
            mMediaContainerList = MediaManager.getInstance().getMediaContainers();
        }
        if (mMediaContainerList.size() == 0) {
            mMediaContainerList = MediaManager.getInstance().getMediaContainersFromFile();
        }

        if ((mMediaContainerList.size() == 0) &&(!NetworkUtils.hasConnection(getActivity()))) {
            mMediaScrollView.setVisibility(View.GONE);
            mProgress.setVisibility(View.GONE);
            mInternetMessage.setVisibility(View.VISIBLE);
            return;
        } else if (mMediaContainerList.size() == 0 &&NetworkUtils.hasConnection(getActivity())) {
            mMediaScrollView.setVisibility(View.GONE);
            mProgress.setVisibility(View.VISIBLE);
            mInternetMessage.setVisibility(View.GONE);
            return;
        }

        mMediaScrollView.setVisibility(View.VISIBLE);
        mProgress.setVisibility(View.GONE);
        mInternetMessage.setVisibility(View.GONE);

        mMediaContainer.removeAllViews();

        for (int i = 0; i < mMediaContainerList.size(); i++) {

            if (mMediaContainerList.get(i).isEmpty()) continue;
            MediaContainerView mcv = new MediaContainerView(getActivity());
            MediaContainerAdapter mca = new MediaContainerAdapter(getActivity(), mMediaContainerList.get(i).getMediaElements());

            mca.setOnMediaItemClickListener(this);
            mca.setDrawableId(mMediaContainerList.get(i).getDrawableId());
            mca.setTitle(mMediaContainerList.get(i).getTitle());
            mca.setContainerType(mMediaContainerList.get(i).getType());
            mcv.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
            mcv.setRefreshMedia(mRefresh);
            mcv.setAdapter(mca);
            mMediaContainer.addView(mcv);
        }

        mProgress.setVisibility(View.GONE);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallbacks = (MediaListeners) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }
}
