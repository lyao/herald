package org.church.volyn.ui.fragment;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeThumbnailView;

import org.church.volyn.App;
import org.church.volyn.Constants;
import org.church.volyn.R;
import org.church.volyn.data.database.DataManager;
import org.church.volyn.downloadHelper.ImageManager;
import org.church.volyn.downloadHelper.DiskCache;
import org.church.volyn.downloadHelper.ImageCache;
import org.church.volyn.downloadHelper.NewsManager;
import org.church.volyn.entities.NewsItem;
import org.church.volyn.utils.IntentWrapper;
import org.church.volyn.utils.NetworkUtils;

public class NewsDetailFragment extends Fragment {
//    private static final String NEWS_ITEM_URL = "news_item_url";

    private NewsItem mNewsItem;
    private int mNewsPosition;
    private WebView mWebView;
    private Callbacks mCallbacks;
    private long imageFilesize = 0;
    private Toolbar toolbar;
    private TextView mNewsTitle;
    private ImageView mNewsImage;
    private ImageView mIconGallery;
    private ImageView mIconVideo;
    private ImageView mYoutubePlay;
    private ViewStub mVideoThumbStub;
    private YouTubeThumbnailView mVideoThumb;

    public interface Callbacks {
        public void onSetCategotyTitle();
    }

    public static NewsDetailFragment newInstance(String newsItemUrl, int newsPosition) {

        Bundle args = new Bundle();
        args.putString(Constants.NEWS_URL, newsItemUrl);
        args.putInt(Constants.CURRENT_ITEM_POSITION, newsPosition);
        NewsDetailFragment fragment = new NewsDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public NewsDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String newsItemUrl = getArguments().getString(Constants.NEWS_URL);
            mNewsItem = NewsManager.getInstance().getNewsItemFromCache(newsItemUrl);
            if ((mNewsItem != null) && (mNewsItem.getImageUrl() != null)) {
                imageFilesize = ImageCache.getInstance(App.getContext()).sizeOf(mNewsItem.getImageUrl());
                if ((imageFilesize == 0 ) && (NetworkUtils.hasConnection(getActivity()))) {
                    ImageManager.startDownload(mNewsItem);
                }
            }
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_news_detail, container, false);
        mNewsTitle = (TextView) v.findViewById(R.id.news_title);
        mNewsImage = (ImageView) v.findViewById(R.id.news_front_image);
        mIconGallery = (ImageView) v.findViewById(R.id.icon_gallery);
        mIconVideo = (ImageView) v.findViewById(R.id.icon_video_play);
        mYoutubePlay = (ImageView) v.findViewById(R.id.youtube_play);
//        mVideoThumbStub = (ViewStub) v.findViewById(R.id.video_thumbnail_stub);
        mWebView = (WebView) v.findViewById(R.id.webView);

        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
//                if (mNewsItem.getImageUrl() != null) {
//                    long size = ImageCache.getInstance(App.getContext()).sizeOf(mNewsItem.getImageUrl());
//                    if (imageFilesize != size || imageFilesize == 0) {
////                        mWebView.loadUrl("javascript:(function() { " + "document.getElementsByTagName('img')[0].style.display = 'none'; " + "})()");
//                        mWebView.loadUrl("javascript:(function() { " + "document.getElementsByClassName('front_image_container')[0].style.display = 'none'; " + "})()");
//                        mWebView.clearCache(true);
//                    }
//                }
            }

        });

        mWebView.loadDataWithBaseURL("file://" + getActivity().getCacheDir().toString() + "/", mNewsItem.getNewsContent(), "text/html", "UTF-8", null);

        mNewsTitle.setText(mNewsItem.getTitle());

        showHideFrontImage();
        showHideGalleryIcon();
        showHideVideoIcon();
       // showVideoThumb();
        showVideoPlayButton();

        mYoutubePlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentWrapper.openFullScreenYoutubeActivity(getActivity(), mNewsItem);
            }
        });
        mNewsImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isGallery()) {
                    IntentWrapper.openGalleryActivity(getActivity(), mNewsItem);
                } else {
                    IntentWrapper.openFullScreenPhotoActivity(getActivity(), mNewsItem);
                }
            }
        });

        mIconVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentWrapper.openFullScreenYoutubeActivity(getActivity(), mNewsItem);
            }
        });

        if (mCallbacks != null) {
            mCallbacks.onSetCategotyTitle();
        }
        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (Callbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    boolean isGallery() {
        if (DataManager.getInstance().getGallery(mNewsItem.getGuid()).size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    boolean hasVideo() {
        if (mNewsItem.getVideoUrl() != null && !mNewsItem.getVideoUrl().isEmpty() ) {
            return true;
        } else {
            return false;
        }
    }

    void showHideFrontImage() {
        long size = ImageCache.getInstance(App.getContext()).sizeOf(mNewsItem.getImageUrl());
        if (size == 0){
            mNewsImage.setVisibility(View.GONE);
        } else {
            mNewsImage.setVisibility(View.VISIBLE);
            Bitmap bm = BitmapFactory.decodeFile(DiskCache.getFilePath(mNewsItem.getImageUrl()));
            mNewsImage.setImageBitmap(bm);
        }
    }

    void showHideGalleryIcon() {
        if (isGallery()) {
            mIconGallery.setVisibility(View.VISIBLE);
        } else {
            mIconGallery.setVisibility(View.GONE);
        }
    }

    void showHideVideoIcon() {
        if (hasVideo()) {
            mIconVideo.setVisibility(View.VISIBLE);
        } else {
            mIconVideo.setVisibility(View.GONE);
        }
    }

    void showVideoPlayButton() {
        long size = ImageCache.getInstance(App.getContext()).sizeOf(mNewsItem.getImageUrl());
        if (!hasVideo() || size > 0) return;
        mYoutubePlay.setVisibility(View.VISIBLE);
        mIconVideo.setVisibility(View.GONE);
    }
//    void showVideoThumb() {
//        long size = ImageCache.getInstance(App.getContext()).sizeOf(mNewsItem.getImageUrl());
//        if (!hasVideo() || size > 0) return;
//        mVideoThumb = (YouTubeThumbnailView) mVideoThumbStub.inflate();
//        mVideoThumb.initialize(Settings.YOUTUBE_DEV_KEY, new YouTubeThumbnailView.OnInitializedListener() {
//            @Override
//            public void onInitializationSuccess(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader youTubeThumbnailLoader) {
//                youTubeThumbnailLoader.setVideo(mNewsItem.getVideoUrl());
//                youTubeThumbnailLoader.setOnThumbnailLoadedListener(new YouTubeThumbnailLoader.OnThumbnailLoadedListener() {
//                    @Override
//                    public void onThumbnailLoaded(YouTubeThumbnailView youTubeThumbnailView, String s) {
//
//                    }
//
//                    @Override
//                    public void onThumbnailError(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader.ErrorReason errorReason) {
//
//                    }
//                });
//            }
//
//            @Override
//            public void onInitializationFailure(YouTubeThumbnailView youTubeThumbnailView, YouTubeInitializationResult youTubeInitializationResult) {
//
//            }
//        });
//    }
}
