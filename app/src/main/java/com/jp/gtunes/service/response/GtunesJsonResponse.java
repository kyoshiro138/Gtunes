package com.jp.gtunes.service.response;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jp.gtunes.core.service.response.BaseJsonResponse;

import java.io.IOException;

public class GtunesJsonResponse<T> extends BaseJsonResponse<T> {
    public GtunesJsonResponse(String response, Class<T> responseType) throws IOException {
        super(response, responseType);
    }

    @Override
    protected T parseJson(String responseString, Class<T> responseType) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(responseString, responseType);
    }
}
