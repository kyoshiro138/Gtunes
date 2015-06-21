package com.jp.gtunes.app.base;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.jp.gtunes.R;
import com.jp.gtunes.app.googleconnect.GoogleConnectFragment;
import com.jp.gtunes.core.activity.BaseFragmentActivity;
import com.jp.gtunes.core.fragment.BaseFragment;
import com.jp.gtunes.core.navigator.BaseNavigator;

public class GtunesFragmentActivity extends BaseFragmentActivity implements View.OnClickListener {
    private TextView mTextScreenTitle;
    private ImageButton mBtnBack;

    @Override
    protected int getActivityLayoutResource() {
        return R.layout.activity_main;
    }

    @Override
    protected int getContentFragmentId() {
        return R.id.layout_content;
    }

    @Override
    protected BaseNavigator createNavigator() {
        return new GtunesNavigator(getContentFragmentId(), getSupportFragmentManager());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTextScreenTitle = (TextView) findViewById(R.id.toolbar_screen_title);
        mBtnBack = (ImageButton) findViewById(R.id.toolbar_back_button);

        mBtnBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.toolbar_back_button) {
            getNavigator().navigateBack();
        }
    }

    public void updateScreenTitle(String title) {
        mTextScreenTitle.setText(title);
    }

    public void setButtonBackEnabled(boolean enabled) {
        if (enabled) {
            mBtnBack.setVisibility(View.VISIBLE);
        } else {
            mBtnBack.setVisibility(View.GONE);
        }
    }
}
