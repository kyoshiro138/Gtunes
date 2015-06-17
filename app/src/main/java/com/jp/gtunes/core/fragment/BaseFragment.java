package com.jp.gtunes.core.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jp.gtunes.core.activity.BaseFragmentActivity;

public abstract class BaseFragment extends Fragment {
    protected static final String KEY_PARAM = "KEY_PARAM";

    protected View mRootView;
    private BaseFragmentActivity mActivity;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mActivity = (BaseFragmentActivity) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(getFragmentLayoutResource(), container, false);

        bindView(mRootView);

        return mRootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        loadData();
    }

    protected abstract int getFragmentLayoutResource();

    protected abstract void bindView(View rootView);

    protected abstract void loadData();

    protected final void navigateTo(BaseFragment fragment, @Nullable Parcelable param) {
        addParam(fragment, param);

        mActivity.navigateToFragment(fragment);
    }

    private void addParam(BaseFragment fragment, Parcelable param) {
        if (param != null) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(KEY_PARAM, param);

            fragment.setArguments(bundle);
        }
    }
}
