package com.jp.gtunes.app.googleconnect;

import android.content.Intent;
import android.view.View;
import android.widget.Button;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.jp.gtunes.R;
import com.jp.gtunes.app.base.GtunesFragmentActivity;
import com.jp.gtunes.app.musiclist.GoogleMusicListFragment;
import com.jp.gtunes.core.dialog.progress.SystemProgressDialog;
import com.jp.gtunes.core.fragment.BaseFragment;
import com.jp.gtunes.core.googleservice.GoogleDriveApi;
import com.jp.gtunes.core.googleservice.OnGoogleAuthListener;
import com.jp.gtunes.core.navigator.Refreshable;
import com.jp.gtunes.utils.PreferenceUtils;

public class GoogleConnectFragment extends BaseFragment
        implements View.OnClickListener, OnGoogleAuthListener, Refreshable, Response.Listener<String> {
    private GoogleDriveApi mDriveApi;
    private SystemProgressDialog mDialog;
    private Button BtnConnect, BtnContinue;
    private String mToken;
    private boolean mIsConnected;

    @Override
    protected int getFragmentLayoutResource() {
        return R.layout.fragment_google_connect;
    }

    @Override
    protected void bindView(View rootView) {
        BtnConnect = (Button) rootView.findViewById(R.id.btn_google_connect);
        BtnContinue = (Button) rootView.findViewById(R.id.btn_continue);

        BtnConnect.setOnClickListener(this);
        BtnContinue.setOnClickListener(this);
    }

    @Override
    protected void loadData() {
        mDriveApi = new GoogleDriveApi(getActivity());
        mToken = (String) PreferenceUtils.getValue(getActivity(), "access_token", "", PreferenceUtils.PREFERENCE_TYPE_STRING);
        mIsConnected = mToken != null && !mToken.equals("");
//        PreferenceUtils.saveValue(getActivity(), "APP_PURCHASED", true, PreferenceUtils.PREFERENCE_TYPE_BOOLEAN);

        mDialog = new SystemProgressDialog(getActivity());
        mDialog.setMessage("Connecting...!");

        if (mIsConnected) {
            BtnConnect.setText(getActivity().getText(R.string.button_google_unlink));
            BtnContinue.setVisibility(View.VISIBLE);
        } else {
            BtnConnect.setText(getActivity().getText(R.string.button_google_connect));
            BtnContinue.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onBackRefresh() {
        loadData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_google_connect:
                if (mIsConnected) {
                    UnlinkGoogleDrive();
                } else {
                    ConnectGoogleDrive();
                }
                break;
            case R.id.btn_continue:
                getNavigator().navigateTo(new GoogleMusicListFragment(), null);
                break;
            default:
                break;
        }
    }

    private void UnlinkGoogleDrive() {
//        SystemProgressDialog dialog = new SystemProgressDialog(getActivity());
//        dialog.setMessage("Disconnecting...!");
//        dialog.startProgress(new OnProgressListener() {
//            @Override
//            public void onProgressBackground(String tag) {
//                mDriveApi.unlinkGoogleDrive(mToken);
//            }
//        });

//        String url = String.format("https://accounts.google.com/o/oauth2/revoke?token=%s", mToken);
//        StringRequest request = new StringRequest(StringRequest.Method.GET, url, this, null);
//        RequestQueue queue = Volley.newRequestQueue(getActivity());
//        queue.add(request);
//        queue.start();

        PreferenceUtils.saveValue(getActivity(), "access_token", "", PreferenceUtils.PREFERENCE_TYPE_STRING);
        BtnConnect.setText(getActivity().getText(R.string.button_google_connect));
        BtnContinue.setVisibility(View.INVISIBLE);
        mIsConnected = false;

    }

    private void ConnectGoogleDrive() {
        if (mDialog != null && !mDialog.isShowing()) {
            mDialog.show();

            if (mDriveApi != null) {
                String[] scopes = new String[]{
                        GoogleDriveApi.SCOPE_DRIVE,
                        GoogleDriveApi.SCOPE_DRIVE_READONLY,
                        GoogleDriveApi.SCOPE_DRIVE_APPDATA,
                        GoogleDriveApi.SCOPE_DRIVE_FILE,
                        GoogleDriveApi.SCOPE_DRIVE_APPS_READONLY,
                        GoogleDriveApi.SCOPE_DRIVE_METADATA,
                        GoogleDriveApi.SCOPE_DRIVE_METADATA_READONLY
                };
                mDriveApi.authenticate(scopes, this);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mDriveApi.handleActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((GtunesFragmentActivity) getActivity()).updateScreenTitle("");
        ((GtunesFragmentActivity) getActivity()).setButtonBackEnabled(false);
    }

    @Override
    public void onAuthSuccess(String token) {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }

        PreferenceUtils.saveValue(getActivity(), "access_token", token, PreferenceUtils.PREFERENCE_TYPE_STRING);
        getNavigator().navigateTo(new GoogleMusicListFragment(), null);
    }

    @Override
    public void onAuthError(Exception e) {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }

    @Override
    public void onResponse(String response) {
        PreferenceUtils.saveValue(getActivity(), "access_token", "", PreferenceUtils.PREFERENCE_TYPE_STRING);
        BtnConnect.setText(getActivity().getText(R.string.button_google_connect));
        BtnContinue.setVisibility(View.INVISIBLE);
        mIsConnected = false;
    }
}
