package com.jp.gtunes.core.service.client;

import android.content.Context;
import android.util.Log;

import com.android.volley.VolleyError;
import com.jp.gtunes.core.service.response.BaseJsonResponse;

import java.io.IOException;

public abstract class BaseRestClient<TResponse> extends BaseServiceClient {
    private OnServiceResponseListener<TResponse> mListener;
    private Class<TResponse> mType;

    public BaseRestClient(Context context, String tag, String url, Class<TResponse> responseType, OnServiceResponseListener<TResponse> listener) {
        super(context, tag, url, null);
        mListener = listener;
        mType = responseType;
    }

    @Override
    public void onResponse(String responseString) {
        try {
            Log.d("SERVICE RESPONSE", responseString);

            TResponse responseData = createResponse(responseString, mType).getResponseData();
            if (mListener != null) {
                mListener.onResponseSuccess(mTag, responseData);
            }
        } catch (Exception e) {
            if (mListener != null) {
                mListener.onParseError(mTag, responseString);
            }
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        if (mListener != null) {
            mListener.onResponseFailed(mTag);
        }
    }

    protected abstract BaseJsonResponse<TResponse> createResponse(String responseString, Class<TResponse> responseType) throws IOException;
}
