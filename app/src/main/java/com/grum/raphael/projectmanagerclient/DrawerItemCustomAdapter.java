package com.grum.raphael.projectmanagerclient;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by grumraph on 14.08.17.
 */

public class DrawerItemCustomAdapter extends ArrayAdapter<DataModel> {

    private Context context;
    private int layoutResourceId;
    private DataModel[] data = null;

    public DrawerItemCustomAdapter(Context context, int layoutResourceId, DataModel[] data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        View inflater
                = ((Activity) context)
                .getLayoutInflater()
                .inflate(layoutResourceId, parent, false);

        ImageView imageViewIcon = (ImageView) listItem.findViewById(R.id.imageViewIcon);
        TextView textViewName = (TextView) listItem.findViewById(R.id.textViewName);

        DataModel folder = data[position];

        imageViewIcon.setImageResource(folder.getIcon());
        textViewName.setText(folder.getName());

        return listItem;
    }
}
