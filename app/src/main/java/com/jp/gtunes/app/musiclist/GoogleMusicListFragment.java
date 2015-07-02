package com.jp.gtunes.app.musiclist;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Environment;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.vending.billing.IInAppBillingService;
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
import com.jp.gtunes.utils.inappbilling.IabHelper;
import com.jp.gtunes.utils.inappbilling.IabResult;
import com.jp.gtunes.utils.inappbilling.Inventory;
import com.jp.gtunes.utils.inappbilling.Purchase;
import com.jp.gtunes.utils.inappbilling.SkuDetails;

public class GoogleMusicListFragment extends BaseFragment implements OnServiceResponseListener<FileResponseData>, AdapterView.OnItemClickListener, OnItemButtonClickListener, IabHelper.QueryInventoryFinishedListener, IabHelper.OnIabSetupFinishedListener, IabHelper.OnIabPurchaseFinishedListener {
    private ListView mFileList;
    private String mAccessToken;
    private IabHelper mHelper;
    private boolean mIsPurchasedEnabled = false;
    private boolean mIsAppPurchased = false;

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

        String url = "https://www.googleapis.com/drive/v2/files?q=mimeType='audio/mpeg'";
        GoogleServiceClient<FileResponseData> client = new GoogleServiceClient<>(getActivity(), "getFiles", url, FileResponseData.class, this);
        client.executeGet();

        mHelper = new IabHelper(getActivity(), "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqzkRRCESZqE6MQr3i9U9n9/N4lA72SrSL4dnGZHTO5GhN5XdTcc/s6BuyS4KqHDwRkZW5ShpIFphKdaD50sxmBRtFIE2QeVqWvr3Yg2Pyo4mVh3Wpqdd7pp2nImra/NBzs9mRf/Jj9MX/8oQZeV77cQtMdMAd/kNlYFstXxxsLbou/ko2Ij7zcaC1XjzTeCSLtWJx/5Y+OtEoC5ZELsGwS3Ln6xY3nnqkq63ffWPiwMDnZOFb0wPqdgJuUD7ZDjDa6v6zC6w0mOsf9xncZ8pjAV5mbIW1HuBWhMdun2nFCGDUKISOgtftGDkryfRkthyWXYoHY+67rAj0HMGekDp2wIDAQAB");
        mHelper.startSetup(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((GtunesFragmentActivity) getActivity()).updateScreenTitle("Google Music");
        ((GtunesFragmentActivity) getActivity()).setButtonBackEnabled(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mHelper != null) {
            mHelper.dispose();
            mHelper = null;
        }
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
        if (mIsAppPurchased) {
            MusicListAdapter adapter = (MusicListAdapter) mFileList.getAdapter();
            MusicPlayerParam param = new MusicPlayerParam(adapter.getItemList(), position);
            getNavigator().navigateTo(new MusicPlayerFragment(), param);
        } else {
            if(mIsPurchasedEnabled) {
                mHelper.launchPurchaseFlow(getActivity(), "test_purchase", 9999, this, "test");
            }
        }
    }

    @Override
    public void onButtonClick(View view) {
        if (mIsAppPurchased) {
            GoogleFile googleFile = (GoogleFile) view.getTag();

            String url = String.format("%s&access_token=%s", googleFile.getUrl(), mAccessToken);
            String dir = Environment.DIRECTORY_MUSIC;

            FileDownloader downloader = new FileDownloader(getActivity(), url, dir);
            downloader.startDownload(googleFile.getFileName());
        } else {
            if(mIsPurchasedEnabled) {
                mHelper.launchPurchaseFlow(getActivity(), "test_purchase", 9999, this, "test");
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onQueryInventoryFinished(IabResult result, Inventory inv) {
        if (result.isFailure()) {
            return;
        }
        SkuDetails skuDetails = inv.getSkuDetails("test_purchase");
        if (skuDetails != null) {
            mIsAppPurchased = true;
        }
    }

    @Override
    public void onIabSetupFinished(IabResult result) {
        if (result.isSuccess()) {
            mIsPurchasedEnabled = true;
            mHelper.queryInventoryAsync(this);
        }
    }

    @Override
    public void onIabPurchaseFinished(IabResult result, Purchase info) {
        if(result.isSuccess()) {
            mIsAppPurchased = true;
        }
    }
}
