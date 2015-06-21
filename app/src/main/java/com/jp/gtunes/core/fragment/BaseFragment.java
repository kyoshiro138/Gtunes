package com.jp.gtunes.core.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.jp.gtunes.core.activity.BaseFragmentActivity;
import com.jp.gtunes.core.navigator.BaseNavigator;

public abstract class BaseFragment extends Fragment {
    protected static final String KEY_PARAM = "KEY_PARAM";

    private View mRootView;
    private BaseFragmentActivity mActivity;

    public View getRootView() {
        return mRootView;
    }

    public BaseNavigator getNavigator() {
        return mActivity.getNavigator();
    }

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

    protected void showToast(CharSequence text) {
        Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
    }

    protected abstract int getFragmentLayoutResource();

    protected abstract void bindView(View rootView);

    protected abstract void loadData();
}
