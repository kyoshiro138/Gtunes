package com.jp.gtunes.core.fragment;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;

public abstract class BaseParamFragment<E extends Parcelable> extends BaseFragment {
    protected static final String KEY_PARAM = "KEY_PARAM";

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null) {
            E param = bundle.getParcelable(getClass().getSimpleName());

            loadParam(param);
        }

        loadData();
    }

    protected abstract void loadParam(E param);
}