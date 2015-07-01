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
import com.jp.gtunes.utils.inappbilling.SkuDetails;

public class GoogleMusicListFragment extends BaseFragment implements OnServiceResponseListener<FileResponseData>, AdapterView.OnItemClickListener, OnItemButtonClickListener, IabHelper.QueryInventoryFinishedListener, IabHelper.OnIabSetupFinishedListener {
    private ListView mFileList;
    private String mAccessToken;
    private IabHelper mHelper;

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

//        Intent serviceIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
//        serviceIntent.setPackage("com.android.vending");
//        getActivity().bindService(serviceIntent, mBillingServiceConnection, Context.BIND_AUTO_CREATE);

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
    public void onStop() {
        super.onStop();
        if (mBillingService != null) {
            getActivity().unbindService(mBillingServiceConnection);
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
        MusicListAdapter adapter = (MusicListAdapter) mFileList.getAdapter();
        MusicPlayerParam param = new MusicPlayerParam(adapter.getItemList(), position);
        getNavigator().navigateTo(new MusicPlayerFragment(), param);
    }

    @Override
    public void onButtonClick(View view) {
        boolean isEnabled = false;
        if (isEnabled) {
            GoogleFile googleFile = (GoogleFile) view.getTag();

            String url = String.format("%s&access_token=%s", googleFile.getUrl(), mAccessToken);
            String dir = Environment.DIRECTORY_MUSIC;

            FileDownloader downloader = new FileDownloader(getActivity(), url, dir);
            downloader.startDownload(googleFile.getFileName());
        } else {
//            try {
//                Bundle bundle = mBillingService.getPurchases(3, getActivity().getPackageName(), "inapp", null);
//
//                if (bundle.getInt("RESPONSE_CODE") == 0) {
//                    ArrayList<String> purchaseDataList = bundle.getStringArrayList("INAPP_PURCHASE_DATA_LIST");
//                    showToast(String.format("%d item", purchaseDataList.size()));
//                }

//                String developerPayload = "test";
//                Bundle bundle = mBillingService.getBuyIntent(3, getActivity().getPackageName(), "test_purchase", "inapp", developerPayload);
//                PendingIntent pendingIntent = bundle.getParcelable("BUY_INTENT");
//
//                if (bundle.getInt("RESPONSE_CODE") == 0 /*BILLING_RESPONSE_RESULT_OK*/) {
//                    getActivity().startIntentSenderForResult(pendingIntent.getIntentSender(),
//                            1001, new Intent(), 0, 0, 0);
//                }


//            List<String> additionalSkuList = new ArrayList<>();
//            additionalSkuList.add("test_purchase");
//            mHelper.queryInventoryAsync(true, additionalSkuList, this);

//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            showToast("IAB is unhandled");
            super.onActivityResult(requestCode, resultCode, data);
        } else {
            showToast("IAB is handled");
        }
//
//        if (requestCode == 1001) {
//            int responseCode = data.getIntExtra("RESPONSE_CODE", 0);
//            String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
//            String dataSignature = data.getStringExtra("INAPP_DATA_SIGNATURE");
//
//            if (resultCode == Activity.RESULT_OK) {
//                try {
//                    JSONObject jo = new JSONObject(purchaseData);
//                    String sku = jo.getString("productId");
//                    showToast("purchase ok " + sku);
//                } catch (JSONException e) {
//                    showToast("purchase failed");
//                    e.printStackTrace();
//                }
//            }
//        }
    }

    private IInAppBillingService mBillingService;
    private ServiceConnection mBillingServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBillingService = IInAppBillingService.Stub.asInterface(service);
            try {
                int response = mBillingService.isBillingSupported(3, getActivity().getPackageName(), "inapp");
                Log.d("BILLING SERVICE", "response " + response);
            } catch (RemoteException e) {
                e.printStackTrace();
                Log.d("BILLING SERVICE", "response failed");
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    public void onQueryInventoryFinished(IabResult result, Inventory inv) {
        if (result.isFailure()) {
            return;
        }
        SkuDetails skuDetails = inv.getSkuDetails("test_purchase");
        if (skuDetails != null) {
            showToast("OK");
        } else {
            showToast("Failed");
        }
    }

    @Override
    public void onIabSetupFinished(IabResult result) {
        if (result.isSuccess()) {
            mHelper.queryInventoryAsync(this);
        } else {
            showToast("Setup failed");
        }
    }
}
