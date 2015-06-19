package com.jp.gtunes.app.googleconnect;

import android.content.Intent;
import android.view.View;

import com.jp.gtunes.R;
import com.jp.gtunes.app.musiclist.GoogleMusicListFragment;
import com.jp.gtunes.core.fragment.BaseFragment;
import com.jp.gtunes.core.googleservice.GoogleDriveApi;
import com.jp.gtunes.core.googleservice.OnGoogleAuthListener;
import com.jp.gtunes.utils.PreferenceUtils;

public class GoogleConnectFragment extends BaseFragment implements View.OnClickListener, OnGoogleAuthListener {
    private GoogleDriveApi driveApi;

    @Override
    protected int getFragmentLayoutResource() {
        return R.layout.fragment_google_connect;
    }

    @Override
    protected void bindView(View rootView) {
        rootView.findViewById(R.id.btn_google_connect).setOnClickListener(this);
    }

    @Override
    protected void loadData() {
        driveApi = new GoogleDriveApi(getActivity());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_google_connect:
                if (driveApi != null) {
                    String[] scopes = new String[]{
                            GoogleDriveApi.SCOPE_DRIVE,
                            GoogleDriveApi.SCOPE_DRIVE_READONLY,
                            GoogleDriveApi.SCOPE_DRIVE_APPDATA,
                            GoogleDriveApi.SCOPE_DRIVE_FILE,
                            GoogleDriveApi.SCOPE_DRIVE_APPS_READONLY,
                            GoogleDriveApi.SCOPE_DRIVE_METADATA,
                            GoogleDriveApi.SCOPE_DRIVE_METADATA_READONLY
                    };
                    driveApi.authenticate(scopes, this);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        driveApi.handleActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onAuthSuccess(String token) {
        token = "ya29.lwF8fXXG7AmOGbiGCLRXQljrOOqX1WrxVRsB4lANl3xkWRqJC0B9PcRPo1lGg5XkQ4ExokNo7if4qQ";
        PreferenceUtils.saveValue(getActivity(), "access_token", token, PreferenceUtils.PREFERENCE_TYPE_STRING);
        getNavigator().navigateTo(new GoogleMusicListFragment(), null);
    }

    @Override
    public void onAuthError(Exception e) {

    }
}
