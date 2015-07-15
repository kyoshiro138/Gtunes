package com.jp.gtunes.core.googleservice;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.GooglePlayServicesUtil;

import java.io.IOException;

public class GoogleDriveApi {
    private static final int REQUEST_CODE_PICK_ACCOUNT = 1001;
    private static final int REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR = 1002;

    public static final String SCOPE_DRIVE = "https://www.googleapis.com/auth/drive";
    public static final String SCOPE_DRIVE_READONLY = "https://www.googleapis.com/auth/drive.readonly";
    public static final String SCOPE_DRIVE_FILE = "https://www.googleapis.com/auth/drive.file";
    public static final String SCOPE_DRIVE_METADATA = "https://www.googleapis.com/auth/drive.metadata";
    public static final String SCOPE_DRIVE_METADATA_READONLY = "https://www.googleapis.com/auth/drive.metadata.readonly";
    public static final String SCOPE_DRIVE_APPDATA = "https://www.googleapis.com/auth/drive.appdata";
    public static final String SCOPE_DRIVE_APPS_READONLY = "https://www.googleapis.com/auth/drive.apps.readonly";

    private FragmentActivity mActivity;
    private OnGoogleAuthListener mListener;

    private String mScope = "";

    public GoogleDriveApi(FragmentActivity activity) {
        mActivity = activity;
    }

    public void authenticate(String[] scopes, OnGoogleAuthListener listener) {
        mListener = listener;

        if (scopes != null && scopes.length > 0) {
            mScope = "oauth2:";
            for (String scope : scopes) {
                mScope += scope + " ";
            }
            showAccountPickerDialog();
        }
    }

    public void unlinkGoogleDrive(String token) {
        try {
            GoogleAuthUtil.clearToken(mActivity, token);
        } catch (GoogleAuthException | IOException e) {
            e.printStackTrace();
        }
    }

    private void showAccountPickerDialog() {
        String[] accountTypes = new String[]{"com.google"};
        Intent intent = AccountPicker.newChooseAccountIntent(null, null,
                accountTypes, false, null, null, null, null);
        mActivity.startActivityForResult(intent, REQUEST_CODE_PICK_ACCOUNT);
    }


    public void handleActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PICK_ACCOUNT) {
            if (resultCode == Activity.RESULT_OK) {
                String email = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                if (email != null) {
                    GetAccessToken task = new GetAccessToken(mActivity, email, mScope);
                    task.execute();
                }
            }
        }
    }

    private void handleException(final Exception e) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (e instanceof GooglePlayServicesAvailabilityException) {
                    int statusCode = ((GooglePlayServicesAvailabilityException) e).getConnectionStatusCode();
                    Dialog dialog = GooglePlayServicesUtil.getErrorDialog(statusCode, mActivity, REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR);
                    dialog.show();
                } else if (e instanceof UserRecoverableAuthException) {
                    Intent intent = ((UserRecoverableAuthException) e).getIntent();
                    mActivity.startActivityForResult(intent, REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR);
                }

                if (mListener != null) {
                    mListener.onAuthError(e);
                }
            }
        });
    }

    private class GetAccessToken extends AsyncTask<Void, Void, String> {
        Activity mActivity;
        String mScope;
        String mEmail;

        public GetAccessToken(Activity activity, String name, String scope) {
            this.mActivity = activity;
            this.mScope = scope;
            this.mEmail = name;
        }

        protected String fetchToken() throws IOException {
            try {
                return GoogleAuthUtil.getToken(mActivity, mEmail, mScope);
            } catch (GoogleAuthException fatalException) {
                fatalException.printStackTrace();
                handleException(fatalException);
            }
            return null;
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                String token = fetchToken();
                if (token != null) {
                    Log.d("GOOGLE DRIVE TOKEN", token);
                    return token;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "";
        }

        @Override
        protected void onPostExecute(String token) {
            if (!token.equals("") && mListener != null) {
                mListener.onAuthSuccess(token);
            }
        }
    }
}
