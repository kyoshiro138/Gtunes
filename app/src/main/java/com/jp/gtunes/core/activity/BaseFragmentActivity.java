package com.jp.gtunes.core.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import com.jp.gtunes.core.fragment.BaseFragment;

public abstract class BaseFragmentActivity extends FragmentActivity {

    protected abstract int getActivityLayoutResource();

    protected abstract int getDrawerContentContainerLayoutId();

    protected abstract BaseFragment getStartupFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getActivityLayoutResource());
        initContentFragment();
    }

    private void initContentFragment() {
        BaseFragment contentFragment = getStartupFragment();

        if (contentFragment != null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(android.support.v7.appcompat.R.anim.abc_fade_out, android.support.v7.appcompat.R.anim.abc_fade_in);
            transaction.add(getDrawerContentContainerLayoutId(), contentFragment, contentFragment.getClass().getSimpleName());
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }
}
