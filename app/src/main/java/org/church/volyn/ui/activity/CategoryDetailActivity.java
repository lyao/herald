package org.church.volyn.ui.activity;

import android.support.v4.app.FragmentManager;
import android.os.Bundle;

import org.church.volyn.Constants;
import org.church.volyn.R;
import org.church.volyn.data.database.DataManager;
import org.church.volyn.ui.fragment.NewsRecyclerViewFragment;

public class CategoryDetailActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentManager fm = getSupportFragmentManager();
        NewsRecyclerViewFragment mainFragment = (NewsRecyclerViewFragment) fm.findFragmentById(R.id.fragment_container);
        int catId = 0;
        if (mainFragment == null) {
            catId = 0;
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                catId = extras.getInt(Constants.CATEGORY_ID);
            }
            mainFragment = NewsRecyclerViewFragment.newInstance(catId);
            fm.beginTransaction().add(R.id.fragment_container, mainFragment).commit();
        }
        toolbar.setTitle(DataManager.getInstance().getCategoryById(catId).getTitle());
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.base_layout;
    }

    @Override
    protected void setActionBarBackButton(int drawable) {
        super.setActionBarBackButton(R.drawable.ic_back);
    }

    //    @Override
//    protected Toolbar getToolbar() {
//      //  return null;
//    }
}
