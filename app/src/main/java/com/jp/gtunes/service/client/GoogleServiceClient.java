package com.jp.gtunes.service.client;

import android.content.Context;

import com.android.volley.Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jp.gtunes.core.service.client.BaseRestClient;
import com.jp.gtunes.core.service.client.OnServiceResponseListener;
import com.jp.gtunes.core.service.request.BaseRequest;
import com.jp.gtunes.core.service.response.BaseJsonResponse;
import com.jp.gtunes.service.request.GtunesRequest;
import com.jp.gtunes.service.response.GtunesJsonResponse;
import com.jp.gtunes.utils.PreferenceUtils;

import java.io.IOException;

public class GoogleServiceClient<TResponse> extends BaseRestClient<TResponse> {
    public GoogleServiceClient(Context context, String tag, String url, Class<TResponse> responseType, OnServiceResponseListener<TResponse> listener) {
        super(context, tag, url, responseType, listener);
    }

    @Override
    protected BaseJsonResponse<TResponse> createResponse(String responseString, Class<TResponse> responseType) throws IOException {
        return new GtunesJsonResponse<>(responseString, responseType);
    }

    @Override
    protected BaseRequest createRequest(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        String accessToken = (String) PreferenceUtils.getValue(mContext, "access_token", "", PreferenceUtils.PREFERENCE_TYPE_STRING);
        return new GtunesRequest(method, url, accessToken, this, this);
    }
}
