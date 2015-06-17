package com.jp.gtunes.app.base;

import com.jp.gtunes.R;
import com.jp.gtunes.app.googleconnect.GoogleConnectFragment;
import com.jp.gtunes.core.activity.BaseFragmentActivity;
import com.jp.gtunes.core.fragment.BaseFragment;
import com.jp.gtunes.core.navigator.BaseNavigator;

public class GtunesFragmentActivity extends BaseFragmentActivity {
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
}
