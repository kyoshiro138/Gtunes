package com.jp.gtunes.app.musiclist;

import android.content.Intent;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.jp.gtunes.R;
import com.jp.gtunes.app.base.GtunesFragmentActivity;
import com.jp.gtunes.app.musicplayer.MusicPlayerFragment;
import com.jp.gtunes.app.musicplayer.MusicPlayerParam;
import com.jp.gtunes.core.adapter.OnItemButtonClickListener;
import com.jp.gtunes.core.component.FileDownloader;
import com.jp.gtunes.core.dialog.progress.SystemProgressDialog;
import com.jp.gtunes.core.fragment.BaseFragment;
import com.jp.gtunes.core.service.client.OnServiceResponseListener;
import com.jp.gtunes.domain.GoogleFile;
import com.jp.gtunes.service.client.GoogleServiceClient;
import com.jp.gtunes.service.response.data.FileResponseData;
import com.jp.gtunes.utils.PreferenceUtils;
import com.jp.gtunes.utils.inappbilling.IabHelper;
import com.jp.gtunes.utils.inappbilling.IabResult;
import com.jp.gtunes.utils.inappbilling.Purchase;

import java.util.ArrayList;
import java.util.List;

public class GoogleMusicListFragment extends BaseFragment
        implements OnServiceResponseListener<FileResponseData>, AdapterView.OnItemClickListener, OnItemButtonClickListener, IabHelper.OnIabPurchaseFinishedListener {

    private static final int SONG_LIMIT_FOR_UNPURCHASED = 5;

    private ListView mFileList;
    private String mAccessToken;
    private IabHelper mHelper;
    private boolean mIsAppPurchased = false;
    private SystemProgressDialog mDialog;

    @Override
    protected int getFragmentLayoutResource() {
        return R.layout.fragment_google_music_list;
    }

    @Override
    protected void bindView(View rootView) {
        mFileList = (ListView) rootView.findViewById(R.id.list_google_files);

        mFileList.setOnItemClickListener(this);
    }

    @Override
    protected void loadData() {
        mAccessToken = (String) PreferenceUtils.getValue(getActivity(), "access_token", "", PreferenceUtils.PREFERENCE_TYPE_STRING);

        mHelper = ((GtunesFragmentActivity) getActivity()).getIabHelper();
        mIsAppPurchased = (boolean) PreferenceUtils.getValue(getActivity(), "APP_PURCHASED", false, PreferenceUtils.PREFERENCE_TYPE_BOOLEAN);

        mDialog = new SystemProgressDialog(getActivity());
        mDialog.setMessage("Loading drive...!");
        mDialog.show();

        String url = "https://www.googleapis.com/drive/v2/files?q=mimeType='audio/mpeg'";
        GoogleServiceClient<FileResponseData> client = new GoogleServiceClient<>(getActivity(), "getFiles", url, FileResponseData.class, this);
        client.executeGet();
    }

    @Override
    public void onResume() {
        super.onResume();
        ((GtunesFragmentActivity) getActivity()).updateScreenTitle("Google Music");
        ((GtunesFragmentActivity) getActivity()).setButtonBackEnabled(true);
    }

    @Override
    public void onResponseSuccess(String tag, FileResponseData response) {
        MusicListAdapter adapter = new MusicListAdapter(getActivity(), response.getItems(), mIsAppPurchased);
        adapter.setOnItemButtonClickListener(this);
        mFileList.setAdapter(adapter);

        if(mDialog!=null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }

    @Override
    public void onResponseFailed(String tag) {
        if(mDialog!=null && mDialog.isShowing()) {
            mDialog.dismiss();
        }

        showToast("Unable to get media file from drive. Please go back and try again.");
    }

    @Override
    public void onParseError(String tag, String response) {
        if(mDialog!=null && mDialog.isShowing()) {
            mDialog.dismiss();
        }

        showToast("Unable to get media file from drive. Please go back and try again.");
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (!mIsAppPurchased && position >= SONG_LIMIT_FOR_UNPURCHASED) {
            mHelper.launchPurchaseFlow(getActivity(), GtunesFragmentActivity.APP_PURCHASE_ITEM_ID, 9999, this, "test");
        } else {
            MusicListAdapter adapter = (MusicListAdapter) mFileList.getAdapter();
            List<GoogleFile> fileList = new ArrayList<>();
            if (!mIsAppPurchased) {
                for (int i = 0; i < adapter.getCount(); i++) {
                    if (i >= SONG_LIMIT_FOR_UNPURCHASED)
                        break;

                    fileList.add(adapter.getItem(i));
                }
            } else {
                fileList.addAll(adapter.getItemList());
            }

            MusicPlayerParam param = new MusicPlayerParam(fileList, position);
            getNavigator().navigateTo(new MusicPlayerFragment(), param);
        }
    }

    @Override
    public void onButtonClick(View view) {
        GoogleFile googleFile = (GoogleFile) view.getTag();

        String url = String.format("%s&access_token=%s", googleFile.getUrl(), mAccessToken);
        String dir = Environment.DIRECTORY_MUSIC;

        FileDownloader downloader = new FileDownloader(getActivity(), url, dir);
        downloader.startDownload(googleFile.getFileName());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onIabPurchaseFinished(IabResult result, Purchase info) {
        if (result.isSuccess()) {
            PreferenceUtils.saveValue(getActivity(), "APP_PURCHASED", true, PreferenceUtils.PREFERENCE_TYPE_BOOLEAN);
        }
    }
}
