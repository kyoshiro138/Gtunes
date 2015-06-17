package com.jp.gtunes.core.service.client;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.jp.gtunes.core.service.request.BaseRequest;
import com.jp.gtunes.core.service.request.IRequestParam;

public abstract class BaseServiceClient implements Response.ErrorListener, Response.Listener<String> {
    protected Context mContext;
    protected RequestQueue mRequestQueue;
    protected String mUrl;
    protected String mTag;
    private OnServiceResponseListener<String> mListener;

    public BaseServiceClient(Context context, String tag, String url, OnServiceResponseListener<String> listener) {
        mContext = context;
        mRequestQueue = Volley.newRequestQueue(context);
        mTag = tag;
        mUrl = url;
        mListener = listener;
    }

    public void executeGet() {
        BaseRequest request = createRequest(Request.Method.GET, mUrl, this, this);
        request.setTag(mTag);

        Log.d("SERVICE REQUEST", String.format("URL: %s", mUrl));

        mRequestQueue.add(request);
        mRequestQueue.start();
    }

    public void executePost(IRequestParam param) {
        BaseRequest request = createRequest(Request.Method.POST, mUrl, this, this);
        request.setTag(mTag);

        if (param != null) {
            request.addParam(param);
            Log.d("SERVICE REQUEST", String.format("URL: %s PARAM: %s", mUrl, param.getRequestParam().toString()));
        }

        mRequestQueue.add(request);
        mRequestQueue.start();
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        if (mListener != null) {
            mListener.onResponseFailed(mTag);
        }
    }

    @Override
    public void onResponse(String response) {
        Log.d("SERVICE RESPONSE", response);
        if (mListener != null) {
            mListener.onResponseSuccess(mTag, response);
        }
    }

    protected abstract BaseRequest createRequest(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener);
}
