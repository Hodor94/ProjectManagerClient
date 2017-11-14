package com.grum.raphael.projectmanagerclient.tasks;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.grum.raphael.projectmanagerclient.MainActivity;
import com.grum.raphael.projectmanagerclient.R;


/**
 * Created by Raphael on 13.11.2017.
 */


public class CheckInternet {

    public static boolean isNetworkAvailable(Context mContext) {
        boolean result = false;
        ConnectivityManager cm = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            result = true;
        }
        return result;
    }

    public static AlertDialog internetNotAvailable(final Activity activity) {
        AlertDialog alertDialog = new AlertDialog.Builder(activity)
                .setTitle(R.string.error)
                .setMessage("Sie sind nicht mit dem Internet verbunden!\n" +
                        "Bitte verbinden Sie sich, um die Funktionen zu nutzen!")
                .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(activity, MainActivity.class);
                        activity.startActivity(intent);
                    }
                }).create();
        return alertDialog;
    }
}
