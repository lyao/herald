/*
 * Copyright (C) 2014 Antonio Leiva Gordillo.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.church.volyn.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import org.church.volyn.R;

public abstract class BaseActivity extends AppCompatActivity {

    protected Toolbar toolbar;
    private static final int DRAWABLE_BACK = R.drawable.ic_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResource());
        View v = (View) findViewById(R.id.toolbar_layout);
        toolbar = (Toolbar) v.findViewById(R.id.toolbar);

        if (toolbar != null) {
            toolbar.setNavigationIcon(DRAWABLE_BACK);
            setActionBarBackButton(DRAWABLE_BACK);
        }
    }

    protected abstract int getLayoutResource();

    protected Toolbar getToolbar(){
        return (Toolbar) findViewById(R.id.toolbar);
    };

    protected void setActionBarIcon(int iconRes) {
        toolbar.setNavigationIcon(iconRes);
    }

    protected void setActionBarTitle(int stringRes) {
        toolbar.setTitle(stringRes);
    }

    protected void setActionBarTitle(String title) {

        toolbar.setTitle(title);
    }

    protected void setActionBarBackButton(int drawable) {
        if (drawable == 0) return;
        toolbar.setNavigationIcon(drawable);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
