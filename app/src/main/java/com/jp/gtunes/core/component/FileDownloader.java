package com.jp.gtunes.core.component;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Build;

public class FileDownloader {
    private Context mContext;
    private String mFileUrl;
    private String mDownloadDirectoryPath;

    public FileDownloader(Context context, String fileUrl, String downloadDirectoryPath) {
        mContext = context;
        mFileUrl = fileUrl;
        mDownloadDirectoryPath = downloadDirectoryPath;
    }

    public void startDownload(String nameOfFile) {
        if (isDownloadManagerAvailable()) {
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(mFileUrl));

            // in order for this if to run, you must use the android 3.2 to compile your app
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            }
            request.setDestinationInExternalPublicDir(mDownloadDirectoryPath, nameOfFile);

            // get download service and enqueue file
            DownloadManager manager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
            manager.enqueue(request);
        }
    }

    private boolean isDownloadManagerAvailable() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
    }
}
