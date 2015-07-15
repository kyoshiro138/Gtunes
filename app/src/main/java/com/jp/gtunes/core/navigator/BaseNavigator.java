package com.jp.gtunes.core.navigator;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.jp.gtunes.core.fragment.BaseFragment;

public abstract class BaseNavigator {
    private int mContentFragmentId;
    private FragmentManager mFragmentManager;

    protected abstract BaseFragment getStartupFragment();

    public BaseNavigator(int contentFragmentId, FragmentManager fragmentManager) {
        mContentFragmentId = contentFragmentId;
        mFragmentManager = fragmentManager;

        initContentFragment();
    }

    private void initContentFragment() {
        BaseFragment fragment = getStartupFragment();
        if (fragment != null) {
            FragmentTransaction transaction = mFragmentManager.beginTransaction();
            transaction.setCustomAnimations(android.support.v7.appcompat.R.anim.abc_fade_out, android.support.v7.appcompat.R.anim.abc_fade_in);
            transaction.add(mContentFragmentId, fragment, fragment.getClass().getSimpleName());
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    private void addParam(BaseFragment fragment, Parcelable param) {
        if (param != null) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(fragment.getClass().getSimpleName(), param);

            fragment.setArguments(bundle);
        }
    }

    public void navigateTo(BaseFragment fragment, @Nullable Parcelable param) {
        addParam(fragment, param);

        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.setCustomAnimations(android.support.v7.appcompat.R.anim.abc_fade_in, android.support.v7.appcompat.R.anim.abc_fade_out);
        transaction.replace(mContentFragmentId, fragment, fragment.getClass().getSimpleName());
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void navigateToFirstLevelFragment(BaseFragment fragment, @Nullable Parcelable param) {
        addParam(fragment, param);

        mFragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.setCustomAnimations(android.support.v7.appcompat.R.anim.abc_fade_in, android.support.v7.appcompat.R.anim.abc_fade_out);
        transaction.replace(mContentFragmentId, fragment, fragment.getClass().getSimpleName());
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public final void navigateBack() {
        mFragmentManager.popBackStack();

        Fragment fragment = mFragmentManager.findFragmentById(mContentFragmentId);
        if (fragment != null && fragment instanceof Refreshable) {
            ((Refreshable) fragment).onBackRefresh();
        }
    }

    public final void navigateBack(int count) {
        for (int i = 0; i < count && mFragmentManager.getBackStackEntryCount() - i > 1; i++) {
            mFragmentManager.popBackStack();
        }

        Fragment fragment = mFragmentManager.findFragmentById(mContentFragmentId);
        if (fragment != null && fragment instanceof Refreshable) {
            ((Refreshable) fragment).onBackRefresh();
        }
    }

    public final void navigateBackToFirstLevelFragment() {
        mFragmentManager.popBackStackImmediate(1, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        Fragment fragment = mFragmentManager.findFragmentById(mContentFragmentId);
        if (fragment != null && fragment instanceof Refreshable) {
            ((Refreshable) fragment).onBackRefresh();
        }
    }
}
