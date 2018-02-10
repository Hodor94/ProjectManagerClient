package com.grum.raphael.projectmanagerclient;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

/**
 * This class is used to create an alert error due to the user token expired.
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

    /**
     * @return Returns the token expired error alert.
     */
    public AlertDialog getAlertDialog() {
        return alertDialog;
    }
}
