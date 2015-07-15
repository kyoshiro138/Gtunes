package com.jp.gtunes.core.dialog.progress;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SystemProgressDialog<TResult> extends ProgressDialog {
    private OnProgressWithResultListener<TResult> mResultListener;
    private OnProgressListener mListener;
    private String mTag;

    public SystemProgressDialog(Context context) {
        super(context);
        initDialog("ProgressDialog");
    }

    public SystemProgressDialog(Context context, String tag) {
        super(context);
        initDialog(tag);
    }

    public SystemProgressDialog(Context context, int theme, String tag) {
        super(context, theme);
        initDialog(tag);
    }

    protected void initDialog(String tag) {
        mTag = tag;

        setIndeterminate(true);
        setCancelable(false);
    }

    public void startProgress(OnProgressListener listener) {
        mListener = listener;

        if (!this.isShowing()) {
            show();
        }

        OnProgressTask task = new OnProgressTask(this);
        task.execute();
    }

    public void startProgress(OnProgressWithResultListener<TResult> listener) {
        mResultListener = listener;

        if (!this.isShowing()) {
            show();
        }

        OnProgressTask task = new OnProgressTask(this);
        task.execute();
    }

    private class OnProgressTask extends AsyncTask<Void, Void, TResult> {
        private ProgressDialog mDialog;
        private ScheduledExecutorService mExecutor;

        public OnProgressTask(ProgressDialog dialog) {
            mExecutor = Executors.newSingleThreadScheduledExecutor();
            mDialog = dialog;
        }

        @Override
        protected TResult doInBackground(Void... params) {
            if (mListener != null) {
                mListener.onProgressBackground(mTag);
                mListener = null;
            }

            if (mResultListener != null) {
                return mResultListener.onProgressBackgroundWithResult(mTag);
            }
            return null;
        }

        @Override
        protected void onPostExecute(final TResult result) {
            super.onPostExecute(result);
            if (mResultListener != null) {
                mResultListener.onProgressFinished(mTag, result);
                mResultListener = null;
            }

            Runnable dismissRunnable = new Runnable() {
                @Override
                public void run() {
                    if (mDialog != null && mDialog.isShowing()) {
                        mDialog.dismiss();
                    }
                }
            };

            mExecutor.schedule(dismissRunnable, 1, TimeUnit.SECONDS);
            mExecutor.shutdown();
        }
    }
}
