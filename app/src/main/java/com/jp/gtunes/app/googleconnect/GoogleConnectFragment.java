package com.jp.gtunes.app.googleconnect;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.jp.gtunes.R;
import com.jp.gtunes.core.fragment.BaseFragment;
import com.jp.gtunes.core.googleservice.GoogleDriveApi;
import com.jp.gtunes.core.googleservice.OnGoogleAuthListener;

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
        Toast.makeText(getActivity(), token, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onAuthError(Exception e) {

    }
}
