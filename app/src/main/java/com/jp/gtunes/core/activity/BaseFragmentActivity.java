package com.jp.gtunes.core.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.jp.gtunes.core.navigator.BaseNavigator;

public abstract class BaseFragmentActivity extends FragmentActivity {
    protected abstract int getActivityLayoutResource();
    protected abstract int getContentFragmentId();

    protected abstract BaseNavigator createNavigator();

    private BaseNavigator mNavigator;

    public BaseNavigator getNavigator() {
        return mNavigator;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getActivityLayoutResource());
        mNavigator = createNavigator();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Fragment fragment = getSupportFragmentManager().findFragmentById(getContentFragmentId());
        if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }
}
