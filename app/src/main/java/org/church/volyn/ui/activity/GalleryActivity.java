package org.church.volyn.ui.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.church.volyn.data.database.DataManager;
import org.church.volyn.downloadHelper.DiskCache;
import org.church.volyn.entities.NewsItem;
import org.church.volyn.ui.fragment.SlideshowDialogFragment;
import org.church.volyn.utils.DateUtils;
import org.church.volyn.utils.ExtraParams;

import java.util.ArrayList;

import org.church.volyn.R;
import org.church.volyn.ui.gallery.GalleryAdapter;
import org.church.volyn.ui.gallery.Image;

public class GalleryActivity extends BaseActivity {

    public static final int GALLERY_SINGLE_PHOTO = 0;
    public static final int GALLERY_MANY_PHOTOS = 1;

    private ArrayList<Image> images;
    private ProgressDialog pDialog;
    private GalleryAdapter mAdapter;
    private RecyclerView recyclerView;
    private String mNewsGuid;
    private TextView mTextView;
    private String mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle b = getIntent().getExtras();
        if (b != null) {
            mNewsGuid = b.getString(ExtraParams.NEWS_GUID);
            mTitle = b.getString(ExtraParams.NEWS_TITLE);
        }

        pDialog = new ProgressDialog(this);
        mTextView = (TextView) findViewById(R.id.gallery_title);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        mTextView.setText(mTitle);
        toolbar.setTitle(getResources().getString(R.string.gallery_title).toUpperCase());

        pDialog = new ProgressDialog(this);
        images = new ArrayList<>();

        mAdapter = new GalleryAdapter(getApplicationContext(), images);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        recyclerView.addOnItemTouchListener(new GalleryAdapter.RecyclerTouchListener(getApplicationContext(), recyclerView, new GalleryAdapter.ClickListener() {
            @Override
            public void onClick(View view, int position) {

                openSlideFragment(position);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

       fetchImages();
    }

    @Override
    protected int getLayoutResource() {
            return R.layout.activity_gallery;
    }

    private void openSlideFragment(int position) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(ExtraParams.IMAGES, images);
        bundle.putInt(ExtraParams.POSITION, position);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        SlideshowDialogFragment newFragment = SlideshowDialogFragment.newInstance();
        newFragment.setArguments(bundle);
        newFragment.show(ft, "slideshow");
    }

    private void fetchImages() {

       // pDialog.setMessage("Downloading json...");
        //pDialog.show();
        ArrayList<String> list = (ArrayList<String>) DataManager.getInstance().getGallery(mNewsGuid);
        NewsItem newsItem = DataManager.getInstance().getNewsByGuid(mNewsGuid);
        images.clear();
        String timestamp = DateUtils.getDate(newsItem.getPubDate(), "dd.MM.yyyy");
        // String title = newsItem.getTitle();
        for (int i = 0; i < list.size(); i++) {

            Image image = new Image();
            image.setName(mTitle);

            String url = list.get(i);
            image.setSmall(url);
            image.setMedium(url);
            image.setLarge(url);
            image.setTimestamp(timestamp);

            images.add(image);
        }

      //  pDialog.hide();
        mAdapter.notifyDataSetChanged();


    }
}