package com.jp.gtunes.app.base;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.jp.gtunes.R;
import com.jp.gtunes.core.activity.BaseFragmentActivity;
import com.jp.gtunes.core.navigator.BaseNavigator;
import com.jp.gtunes.utils.PreferenceUtils;
import com.jp.gtunes.utils.inappbilling.IabHelper;
import com.jp.gtunes.utils.inappbilling.IabResult;
import com.jp.gtunes.utils.inappbilling.Inventory;
import com.jp.gtunes.utils.inappbilling.SkuDetails;

public class GtunesFragmentActivity extends BaseFragmentActivity implements View.OnClickListener, IabHelper.OnIabSetupFinishedListener, IabHelper.QueryInventoryFinishedListener {
    public static final String APP_PURCHASE_ITEM_ID = "test_purchase";

    private TextView mTextScreenTitle;
    private ImageButton mBtnBack;
    private IabHelper mHelper;

    @Override
    protected int getActivityLayoutResource() {
        return R.layout.activity_main;
    }

    @Override
    protected int getContentFragmentId() {
        return R.id.layout_content;
    }

    @Override
    protected BaseNavigator createNavigator() {
        return new GtunesNavigator(getContentFragmentId(), getSupportFragmentManager());
    }

    public IabHelper getIabHelper() {
        return mHelper;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTextScreenTitle = (TextView) findViewById(R.id.toolbar_screen_title);
        mBtnBack = (ImageButton) findViewById(R.id.toolbar_back_button);

        mBtnBack.setOnClickListener(this);

        mHelper = new IabHelper(this, "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqzkRRCESZqE6MQr3i9U9n9/N4lA72SrSL4dnGZHTO5GhN5XdTcc/s6BuyS4KqHDwRkZW5ShpIFphKdaD50sxmBRtFIE2QeVqWvr3Yg2Pyo4mVh3Wpqdd7pp2nImra/NBzs9mRf/Jj9MX/8oQZeV77cQtMdMAd/kNlYFstXxxsLbou/ko2Ij7zcaC1XjzTeCSLtWJx/5Y+OtEoC5ZELsGwS3Ln6xY3nnqkq63ffWPiwMDnZOFb0wPqdgJuUD7ZDjDa6v6zC6w0mOsf9xncZ8pjAV5mbIW1HuBWhMdun2nFCGDUKISOgtftGDkryfRkthyWXYoHY+67rAj0HMGekDp2wIDAQAB");
        mHelper.startSetup(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
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
    public void onClick(View v) {
        if (v.getId() == R.id.toolbar_back_button) {
            getNavigator().navigateBack();
        }
    }

    public void updateScreenTitle(String title) {
        mTextScreenTitle.setText(title);
    }

    public void setButtonBackEnabled(boolean enabled) {
        if (enabled) {
            mBtnBack.setVisibility(View.VISIBLE);
        } else {
            mBtnBack.setVisibility(View.GONE);
        }
    }

    @Override
    public void onIabSetupFinished(IabResult result) {
        if (result.isSuccess()) {
            mHelper.queryInventoryAsync(this);
        }
    }

    @Override
    public void onQueryInventoryFinished(IabResult result, Inventory inv) {
        if (result.isFailure()) {
            return;
        }
        SkuDetails skuDetails = inv.getSkuDetails(APP_PURCHASE_ITEM_ID);
        if (skuDetails != null) {
            PreferenceUtils.saveValue(this, "APP_PURCHASED", true, PreferenceUtils.PREFERENCE_TYPE_BOOLEAN);
        }
    }
}
