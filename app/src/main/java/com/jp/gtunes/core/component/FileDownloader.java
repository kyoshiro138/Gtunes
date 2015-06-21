package com.jp.gtunes.core.component;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;

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
        if(isDownloadManagerAvailabled()) {
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(mFileUrl));
//            request.setDescription("Some descrition");
//            request.setTitle("Some title");
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

    private boolean isDownloadManagerAvailabled() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
    }
}
