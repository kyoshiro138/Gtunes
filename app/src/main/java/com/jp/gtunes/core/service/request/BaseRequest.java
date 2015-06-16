package com.jp.gtunes.core.service.request;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.Map;

public abstract class BaseRequest extends StringRequest {
    private IRequestParam mParam;

    public BaseRequest(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
    }

    public BaseRequest(String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(url, listener, errorListener);
    }

    public void addParam(IRequestParam param) {
        mParam = param;
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        if (mParam != null) {
            return mParam.getRequestParam();
        }
        return super.getParams();
    }
}
