package org.church.volyn.ui.fragment;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import org.church.volyn.R;


/**
 * Created by yura on 26.01.16.
 */
public abstract class BaseToolbarFragment extends Fragment {

    private static final String TAG = BaseToolbarFragment.class.getSimpleName();
    private static final int DRAWABLE_BACK = R.drawable.ic_back;

    private Toolbar mToolbar;

    @Override
    public void onActivityCreated(Bundle b) {
        super.onActivityCreated(b);
        mToolbar = (Toolbar) getView().findViewById(R.id.toolbar);
        if (mToolbar != null) {
           // mToolbar.setTitleTextColor(0x000000);
            mToolbar.setNavigationIcon(DRAWABLE_BACK);
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().finish();
                }
            });
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
        if (mToolbar != null) {
            // mToolbar.setTitleTextColor(0x000000);
            mToolbar.setNavigationIcon(DRAWABLE_BACK);
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().finish();
                }
            });
        }
    }

    protected void hideNavigationButton() {
        if (mToolbar != null) {
            mToolbar.setNavigationIcon(null);
        }
    }

    protected void showNavigationButton() {
        if (mToolbar != null) {
            mToolbar.setNavigationIcon(DRAWABLE_BACK);
        }
    }

    protected void setTitle(String title) {
        if (mToolbar != null) {
            mToolbar.setTitle(title);
        }
    }

}
