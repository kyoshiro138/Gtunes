package com.jp.gtunes.core.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

public abstract class BaseListAdapter<TData> extends ArrayAdapter<TData> {
    protected Context mContext;
    protected List<TData> mItemList;
    protected OnItemButtonClickListener mButtonClickListener;

    public List<TData> getItemList() {
        return mItemList;
    }

    public BaseListAdapter(Context context, List<TData> itemList) {
        super(context, -1, itemList);
        mContext = context;
        mItemList = itemList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        Object viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(getItemLayoutResource(), parent, false);

            viewHolder = bindViewHolder(view);

            view.setTag(viewHolder);
            if(view instanceof ViewGroup) {
                ((ViewGroup)view).setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
            }
        } else {
            view = convertView;

            viewHolder = view.getTag();
        }

        loadData(viewHolder, mItemList.get(position));

        return view;
    }

    public void setOnItemButtonClickListener(OnItemButtonClickListener listener) {
        mButtonClickListener = listener;
    }

    protected abstract int getItemLayoutResource();

    protected abstract Object bindViewHolder(View view);

    protected abstract void loadData(Object viewHolder, TData data);
}
