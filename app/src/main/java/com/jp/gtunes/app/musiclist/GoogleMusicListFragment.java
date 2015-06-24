package com.jp.gtunes.app.musiclist;

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
import com.jp.gtunes.core.fragment.BaseFragment;
import com.jp.gtunes.core.service.client.OnServiceResponseListener;
import com.jp.gtunes.domain.GoogleFile;
import com.jp.gtunes.service.client.GoogleServiceClient;
import com.jp.gtunes.service.response.data.FileResponseData;
import com.jp.gtunes.utils.PreferenceUtils;

public class GoogleMusicListFragment extends BaseFragment implements OnServiceResponseListener<FileResponseData>, AdapterView.OnItemClickListener, OnItemButtonClickListener {
    private ListView mFileList;
    private String mAccessToken;

    @Override
    protected int getFragmentLayoutResource() {
        return R.layout.fragment_google_music_list;
    }

    @Override
    protected void bindView(View rootView) {
        mFileList = (ListView) rootView.findViewById(R.id.list_google_files);

        mFileList.setOnItemClickListener(this);

        // TODO: add billing service
        // http://developer.android.com/google/play/billing/billing_integrate.html
    }

    @Override
    protected void loadData() {
        mAccessToken = (String) PreferenceUtils.getValue(getActivity(), "access_token", "", PreferenceUtils.PREFERENCE_TYPE_STRING);

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
        MusicListAdapter adapter = new MusicListAdapter(getActivity(), response.getItems());
        adapter.setOnItemButtonClickListener(this);
        mFileList.setAdapter(adapter);
    }

    @Override
    public void onResponseFailed(String tag) {
    }

    @Override
    public void onParseError(String tag, String response) {
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        MusicListAdapter adapter = (MusicListAdapter) mFileList.getAdapter();
        MusicPlayerParam param = new MusicPlayerParam(adapter.getItemList(), position);
        getNavigator().navigateTo(new MusicPlayerFragment(), param);
    }

    @Override
    public void onButtonClick(View view) {
        //TODO: download file
        GoogleFile googleFile = (GoogleFile) view.getTag();

        String url = String.format("%s&access_token=%s", googleFile.getUrl(), mAccessToken);
        String dir = Environment.DIRECTORY_DOWNLOADS;

        FileDownloader downloader = new FileDownloader(getActivity(), url, dir);
        downloader.startDownload("music.mp3");
    }
}
