package com.jp.gtunes.app.base;

import android.support.v4.app.FragmentManager;

import com.jp.gtunes.app.googleconnect.GoogleConnectFragment;
import com.jp.gtunes.core.fragment.BaseFragment;
import com.jp.gtunes.core.navigator.BaseNavigator;

public class GtunesNavigator extends BaseNavigator {
    public GtunesNavigator(int contentFragmentId, FragmentManager fragmentManager) {
        super(contentFragmentId, fragmentManager);
    }

    @Override
    protected BaseFragment getStartupFragment() {
        return new GoogleConnectFragment();
    }
}
