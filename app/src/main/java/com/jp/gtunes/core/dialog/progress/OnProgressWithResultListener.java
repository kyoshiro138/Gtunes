package com.jp.gtunes.core.dialog.progress;

public interface OnProgressWithResultListener<TResult> {
    TResult onProgressBackgroundWithResult(String tag);

    void onProgressFinished(String tag, TResult result);
}
