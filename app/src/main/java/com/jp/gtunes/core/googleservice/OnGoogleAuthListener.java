package com.jp.gtunes.core.googleservice;

public interface OnGoogleAuthListener {
    void onAuthSuccess(String token);
    void onAuthError(Exception e);
}
