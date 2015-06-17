package com.jp.gtunes.core.service.client;

public interface OnServiceResponseListener<T> {
    void onResponseSuccess(String tag, T response);

    void onResponseFailed(String tag);

    void onParseError(String tag, String response);
}
