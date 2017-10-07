package com.grum.raphael.projectmanagerclient.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.text.format.DateFormat;


/**
 * Created by Raphael on 01.10.2017.
 */
// TODO
public class InvitationService extends IntentService {
    public static final String PARAM_IN_MSG = "imsg";
    public static final String PARMA_OUT_MSG = "omsg";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public InvitationService() {
        super("InvitationsService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String msg = intent.getStringExtra(PARAM_IN_MSG);
        SystemClock.sleep(3000); // 30 seconds
        String resultTxt = msg + " " + DateFormat.format("MM/dd/yy h:mmaa", System.currentTimeMillis());
    }
}
