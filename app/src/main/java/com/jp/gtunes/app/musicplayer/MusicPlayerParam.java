package com.jp.gtunes.app.musicplayer;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.jp.gtunes.domain.GoogleFile;

import java.util.ArrayList;
import java.util.List;

public class MusicPlayerParam implements Parcelable {
    private List<GoogleFile> mFiles;

    public List<GoogleFile> getFiles() {
        return mFiles;
    }

    private int mSelectedFileIndex;

    public int getSelectedFileIndex() {
        return mSelectedFileIndex;
    }

    public MusicPlayerParam(List<GoogleFile> files, int selectedFileIndex) {
        mFiles = files;
        mSelectedFileIndex = selectedFileIndex;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Bundle bundle = new Bundle();

        bundle.putParcelableArrayList("files", (ArrayList<? extends Parcelable>) mFiles);
        bundle.putInt("selected_index", mSelectedFileIndex);

        dest.writeBundle(bundle);
    }

    public static final Parcelable.Creator<MusicPlayerParam> CREATOR = new Creator<MusicPlayerParam>() {

        @Override
        public MusicPlayerParam createFromParcel(Parcel source) {
            Bundle bundle = source.readBundle();

            List<GoogleFile> files = bundle.getParcelableArrayList("files");
            int selectedFileIndex = bundle.getInt("selected_index");

            return new MusicPlayerParam(files, selectedFileIndex);
        }

        @Override
        public MusicPlayerParam[] newArray(int size) {
            return new MusicPlayerParam[size];
        }
    };
}
