package com.jp.gtunes.core.service.response;

import java.io.IOException;

public abstract class BaseJsonResponse<TData> extends BaseResponse {
    private TData mResponseData;

    public TData getResponseData() {
        return mResponseData;
    }

    public BaseJsonResponse(String response, Class<TData> responseType) throws IOException {
        super(response);
        mResponseData = parseJson(response, responseType);
    }

    protected abstract TData parseJson(String responseString, Class<TData> responseType) throws IOException;
}
