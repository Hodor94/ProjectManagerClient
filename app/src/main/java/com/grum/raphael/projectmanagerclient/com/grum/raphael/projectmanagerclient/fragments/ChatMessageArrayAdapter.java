package com.grum.raphael.projectmanagerclient.com.grum.raphael.projectmanagerclient.fragments;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.grum.raphael.projectmanagerclient.MainActivity;
import com.grum.raphael.projectmanagerclient.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Raphael on 02.12.2017.
 */

public class ChatMessageArrayAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<JSONObject> messages;

    public ChatMessageArrayAdapter(ArrayList<JSONObject> messages, Context context) {
        this.messages = messages;
        this.context = context;
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public JSONObject getItem(int position) {
        return messages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View result;
        if (convertView == null) {
            result = LayoutInflater.from(parent.getContext()).inflate(R.layout.message, parent,
                    false);
        } else {
            result = convertView;
        }
        JSONObject message = getItem(position);
        String author;
        String timestamp;
        String messageText;
        try {
            author = message.getString("author");
            timestamp = message.getString("date");
            messageText = message.getString("message");
        } catch (JSONException e) {
            // Set values of data to null
            author = null;
            timestamp = null;
            messageText = null;
        }
        if (author != null && timestamp != null && messageText != null) {
            ((TextView) result.findViewById(R.id.message_user)).setText(author);
            ((TextView) result.findViewById(R.id.message_time)).setText(timestamp);
            ((TextView) result.findViewById(R.id.message_text)).setText(messageText);
            if (author.equals(MainActivity.userData.getUsername())) {
                ((TextView) result.findViewById(R.id.message_user))
                        .setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
            }
        }
        return result;
    }
}
