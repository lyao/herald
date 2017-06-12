package org.church.volyn.ui.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.AlertDialog;
import android.view.View;
import android.widget.ImageButton;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.ShareApi;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareButton;
import com.facebook.share.widget.ShareDialog;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.StatusesService;

import java.util.Arrays;

import org.church.volyn.Constants;
import org.church.volyn.R;

public class ShareActivity extends Activity implements View.OnClickListener {

    private String mNewsTitle = "";
    private String mNewsUrl = "";
    private String mNewsImageUrl = "";

    private ImageButton twitterShareButton;
    private TwitterLoginButton twitterLoginButton;
    private PendingAction pendingTwitterAction = PendingAction.NONE;

    private CallbackManager callbackManager;
    private ProfileTracker profileTracker;
    private ShareDialog shareDialog;
    private boolean canPresentShareDialog;
    private PendingAction pendingFacebookAction = PendingAction.NONE;

    private enum PendingAction {
        NONE,
        POST_STATUS_UPDATE
    }

    private static final String PERMISSION = "publish_actions";

    private FacebookCallback<Sharer.Result> shareCallback = new FacebookCallback<Sharer.Result>() {
        @Override
        public void onSuccess(Sharer.Result result) {
            showAlertDialog(null, "Facebook status was updated successfully");
        }

        @Override
        public void onCancel() {
            showAlertDialog(null, "Facebook status was updated successfully");
        }

        @Override
        public void onError(FacebookException e) {
            showAlertDialog(null, "Facebook status update failed");
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        mNewsTitle = intent.getStringExtra(Constants.NEWS_TITLE);
        mNewsUrl = intent.getStringExtra(Constants.NEWS_URL);
        mNewsImageUrl = intent.getStringExtra(Constants.NEWS_IMAGE_URL);

        setContentView(R.layout.activity_share);
        twitterShareButton = (ImageButton) findViewById(R.id.tweet_button);
        twitterShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performTwitterStatusUpdate(PendingAction.POST_STATUS_UPDATE);
            }
        });
        twitterLoginButton = new TwitterLoginButton(this);
        twitterLoginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                pendingTwitterAction = PendingAction.POST_STATUS_UPDATE;
                handleTwitterPendingAction();
            }

            @Override
            public void failure(TwitterException e) {
                pendingTwitterAction = PendingAction.NONE;
            }
        });


        ShareButton shareButton = (ShareButton) findViewById(R.id.fb_share_button);
        shareButton.setOnClickListener(this);

        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        handleFacebookPendingAction();
                    }

                    @Override
                    public void onCancel() {
                        if (pendingFacebookAction != PendingAction.NONE) {
                            pendingFacebookAction = PendingAction.NONE;
                        }
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        if (pendingFacebookAction != PendingAction.NONE
                                && exception instanceof FacebookAuthorizationException) {
                            pendingFacebookAction = PendingAction.NONE;
                        }
                    }
                });


        shareDialog = new ShareDialog(this);
        shareDialog.registerCallback(
                callbackManager,
                shareCallback);

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                handleFacebookPendingAction();
            }
        };

        canPresentShareDialog = ShareDialog.canShow(
                ShareLinkContent.class);
    }



    private void handleTwitterPendingAction() {
        PendingAction previouslyPendingAction = pendingTwitterAction;
        pendingTwitterAction = PendingAction.NONE;
        switch (previouslyPendingAction) {
            case NONE:
                break;
            case POST_STATUS_UPDATE:
                updateTwitterStatus();
                break;
        }
    }

    private void performTwitterStatusUpdate(PendingAction action) {

        TwitterSession session = Twitter.getSessionManager().getActiveSession();
        if (session != null) {

            TwitterAuthToken authToken = session.getAuthToken();

            if (authToken != null && !authToken.isExpired()) {
                pendingTwitterAction = action;
                handleTwitterPendingAction();

            } else {
                twitterLoginButton.performClick();
            }
        } else {
            twitterLoginButton.performClick();
        }
    }

    private void updateTwitterStatus() {
        TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient(Twitter.getSessionManager().getActiveSession());
        StatusesService statusesService = twitterApiClient.getStatusesService();
        StringBuilder sb = new StringBuilder(mNewsTitle);
        if (mNewsTitle.length() > 118)
            sb.delete(118, mNewsTitle.length()-1);
        sb.append(" ").append(mNewsUrl);

        statusesService.update(sb.toString(), null, null, null, null,
                null, null, null, new Callback<Tweet>() {
                    @Override
                    public void success(Result<Tweet> result) {
//                        Toast.makeText(getApplicationContext(), "Twitter status was updated successfully", Toast.LENGTH_SHORT).show();
                        showAlertDialog(null, "Twitter status was updated successfully");
                    }

                    public void failure(TwitterException exception) {
//                        Toast.makeText(getApplicationContext(), "Twitter status update failed", Toast.LENGTH_SHORT).show();
                        showAlertDialog(null, "Twitter status update failed");
                    }
                });

    }

    private void handleFacebookPendingAction() {
        PendingAction previouslyPendingAction = pendingFacebookAction;
        // These actions may re-set pendingAction if they are still pending, but we assume they
        // will succeed.
        pendingFacebookAction = PendingAction.NONE;
        switch (previouslyPendingAction) {
            case NONE:
                break;
            case POST_STATUS_UPDATE:
                postFacebookStatusUpdate();
                break;
        }
    }

    @Override
    public void onClick(View v) {
        onClickPostStatusUpdate();
    }
    private void onClickPostStatusUpdate() {
        performFacebookPublish(PendingAction.POST_STATUS_UPDATE, canPresentShareDialog);
    }

    private void performFacebookPublish(PendingAction action, boolean allowNoToken) {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken != null) {
            pendingFacebookAction = action;
            if (hasPublishPermission()) {
                // We can do the action right away.
                handleFacebookPendingAction();
                return;
            } else {
                // We need to get new permissions, then complete the action when we get called back.
                LoginManager.getInstance().logInWithPublishPermissions(
                        this,
                        Arrays.asList(PERMISSION));
                return;
            }
        }

        if (allowNoToken) {
            pendingFacebookAction = action;
            handleFacebookPendingAction();
        }
    }
    private void postFacebookStatusUpdate() {
        Profile profile = Profile.getCurrentProfile();
        ShareLinkContent linkContent = new ShareLinkContent.Builder()
                .setContentTitle(mNewsTitle)
                .setContentDescription(getResources().getString(R.string.app_name))
                .setContentUrl(Uri.parse(mNewsUrl))
                .setImageUrl(Uri.parse(mNewsImageUrl))
                .build();
        if (canPresentShareDialog) {
            shareDialog.show(linkContent);
        } else if (profile != null && hasPublishPermission()) {
            ShareApi.share(linkContent, shareCallback);
        } else {
            pendingFacebookAction = PendingAction.POST_STATUS_UPDATE;
        };
    }

    private boolean hasPublishPermission() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null && accessToken.getPermissions().contains(PERMISSION);
    }

    private void showAlertDialog(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        twitterLoginButton.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, requestCode, data);
    }
}
