package com.grum.raphael.projectmanagerclient.com.grum.raphael.projectmanagerclient.fragments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.sql.Array;
import java.util.ArrayList;
import java.util.HashMap;
import com.grum.raphael.projectmanagerclient.R;


/**
 * Created by Raphael on 16.11.2017.
 */

public class HashMapAdapterForList extends BaseAdapter {

    private final ArrayList data;

    public HashMapAdapterForList(HashMap<String, String> map) {
        data = new ArrayList();
        data.addAll(map.entrySet());
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public HashMap.Entry<String, String> getItem(int position) {
        return (HashMap.Entry) data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View result;

        if (convertView == null) {
            result = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.hashmap_adapter_item, parent, false);
        } else {
            result = convertView;
        }
        HashMap.Entry<String, String> item = getItem(position);

        ((TextView) result.findViewById(R.id.answer_key)).setText(item.getKey());
        ((TextView) result.findViewById(R.id.answer_value)).setText(item.getValue());

        return result;
    }
}
