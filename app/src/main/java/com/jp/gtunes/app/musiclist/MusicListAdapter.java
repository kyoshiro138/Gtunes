package com.jp.gtunes.app.musiclist;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.jp.gtunes.R;
import com.jp.gtunes.core.adapter.BaseListAdapter;
import com.jp.gtunes.domain.GoogleFile;

import java.util.List;

public class MusicListAdapter extends BaseListAdapter<GoogleFile> {
    public MusicListAdapter(Context context, List<GoogleFile> itemList) {
        super(context, itemList);
    }

    @Override
    protected int getItemLayoutResource() {
        return R.layout.list_music_item;
    }

    @Override
    protected Object bindViewHolder(View view) {
        MusicItemViewHolder viewHolder = new MusicItemViewHolder();

        viewHolder.mText = (TextView) view.findViewById(R.id.list_item_music_title);

        return viewHolder;
    }

    @Override
    protected void loadData(Object viewHolder, GoogleFile googleFile) {
        MusicItemViewHolder rowViewHolder = (MusicItemViewHolder) viewHolder;
        rowViewHolder.mText.setText(googleFile.getTitle());
    }

    private static class MusicItemViewHolder {
        public TextView mText;
    }
}
