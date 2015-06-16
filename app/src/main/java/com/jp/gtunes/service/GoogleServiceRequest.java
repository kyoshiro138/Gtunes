package com.jp.gtunes.service;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.jp.gtunes.core.service.request.BaseRequest;

import java.util.HashMap;
import java.util.Map;

public class GoogleServiceRequest extends BaseRequest {
    private String mAccessToken;

    public GoogleServiceRequest(int method, String url, String accessToken, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
        mAccessToken = accessToken;
    }

    public GoogleServiceRequest(String url, String accessToken, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(url, listener, errorListener);
        mAccessToken = accessToken;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        HashMap<String, String> params = new HashMap<>();
        params.put("Authorization", "Bearer " + mAccessToken);

        return params;
    }
}
