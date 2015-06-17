package com.jp.gtunes.service.response.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.jp.gtunes.domain.GoogleFile;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FileResponseData {
    @JsonProperty("kind")
    private String mKind;

    public String getKind() {
        return mKind;
    }

    @JsonProperty("etag")
    private String mTag;

    public String getTag() {
        return mTag;
    }

    @JsonProperty("items")
    private List<GoogleFile> mItems;

    public List<GoogleFile> getItems() {
        return mItems;
    }

}
