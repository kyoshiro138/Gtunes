package com.jp.gtunes.core.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Fragment fragment = getSupportFragmentManager().findFragmentById(getDrawerContentContainerLayoutId());
        if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
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

    public final void navigateToFragment(BaseFragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(android.support.v7.appcompat.R.anim.abc_fade_in, android.support.v7.appcompat.R.anim.abc_fade_out);
        transaction.replace(getDrawerContentContainerLayoutId(), fragment, fragment.getClass().getSimpleName());
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public final void navigateToFirstLevelFragment(BaseFragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(android.support.v7.appcompat.R.anim.abc_fade_in, android.support.v7.appcompat.R.anim.abc_fade_out);
        transaction.replace(getDrawerContentContainerLayoutId(), fragment, fragment.getClass().getSimpleName());
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public final void navigateBack() {
        getSupportFragmentManager().popBackStack();
    }

    public final void navigateBack(int count) {
        for (int i = 0; i < count && getSupportFragmentManager().getBackStackEntryCount() - i > 1; i++) {
            getSupportFragmentManager().popBackStack();
        }
    }

    public final void navigateBackToFirstLevelFragment() {
        getSupportFragmentManager().popBackStackImmediate(1, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }
}
