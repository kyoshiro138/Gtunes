package com.jp.gtunes.app.musiclist;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.jp.gtunes.R;
import com.jp.gtunes.app.musicplayer.MusicPlayerFragment;
import com.jp.gtunes.app.musicplayer.MusicPlayerParam;
import com.jp.gtunes.core.fragment.BaseFragment;
import com.jp.gtunes.core.service.client.OnServiceResponseListener;
import com.jp.gtunes.service.client.GoogleServiceClient;
import com.jp.gtunes.service.response.data.FileResponseData;

public class GoogleMusicListFragment extends BaseFragment implements OnServiceResponseListener<FileResponseData>, AdapterView.OnItemClickListener {
    private ListView mFileList;

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
        String url = "https://www.googleapis.com/drive/v2/files?fields=etag,items(createdDate,downloadUrl,webContentLink,fileSize,id,mimeType,title),kind,nextLink,nextPageToken,selfLink&q=mimeType='audio/mpeg'";
        GoogleServiceClient<FileResponseData> client = new GoogleServiceClient<>(getActivity(), "getFiles", url, FileResponseData.class, this);
        client.executeGet();
    }

    @Override
    public void onResponseSuccess(String tag, FileResponseData response) {
        MusicListAdapter adapter = new MusicListAdapter(getActivity(), response.getItems());
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
}
