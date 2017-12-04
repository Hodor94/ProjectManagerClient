package com.grum.raphael.projectmanagerclient.com.grum.raphael.projectmanagerclient.fragments;

import android.content.Context;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.grum.raphael.projectmanagerclient.R;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by Raphael on 01.12.2017.
 */

public class ChatListArrayAdapter extends BaseAdapter {

    private ArrayList<ConnectChatWithIdentifier> data;

    public ChatListArrayAdapter(ArrayList<ConnectChatWithIdentifier> data) {
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public ConnectChatWithIdentifier getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View result;
        if (convertView == null) {
            result = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_list_item,
                    parent, false);
        } else {
            result = convertView;
        }
        ConnectChatWithIdentifier item = getItem(position);

        ((TextView) result.findViewById(R.id.chat_name)).setText(item.getName());
        ((TextView) result.findViewById(R.id.chat_position)).setText("" + item.getPositionInList());

        if (position % 2 == 0) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                Drawable drawable = parent.getContext()
                        .getDrawable(R.drawable.border_text_view_colored);
                ((TextView) result.findViewById(R.id.chat_name)).setBackground(drawable);
            }
        } else {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                Drawable drawable = parent.getContext()
                        .getDrawable(R.drawable.border_text_view);
                ((TextView) result.findViewById(R.id.chat_name)).setBackground(drawable);
            }
        }

        return result;
    }
}
