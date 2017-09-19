package com.grum.raphael.projectmanagerclient;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

/**
 * Created by Raphael on 31.08.2017.
 */

public class ErrorAlertExpiredRights {

    private AlertDialog alertDialog;

    public ErrorAlertExpiredRights(Context context) {
        alertDialog = generateAlertDialog(context);
    }

    private AlertDialog generateAlertDialog(final Context context) {
       AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle(R.string.error_internal)
                .setMessage("Die Berechtigung für die gewählte Aktion fehlt oder ist " +
                        "abgelaufen!")
                .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent
                                = new Intent(context, MainActivity.class);
                        context.startActivity(intent);
                    }
                })
                .create();
        return alertDialog;
    }

    public AlertDialog getAlertDialog() {
        return alertDialog;
    }
}