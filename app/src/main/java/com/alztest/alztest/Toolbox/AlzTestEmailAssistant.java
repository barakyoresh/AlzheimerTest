/*
 * Copyright (c) 2015. Barak Yoresh. all rights reserved.
 */

package com.alztest.alztest.Toolbox;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

/**
 * Created by Barak Yoresh on 15/07/2015.
 */
public class AlzTestEmailAssistant {

    public static final String[] recipients = new String[] {"dailygreg@yahoo.com"};
    public static final String EMAIL_INTENT_TYPE = "message/rfc822";

    public static void sendNewEmail(String subject, String body, Uri attachmentLocation, Activity activity) {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType(EMAIL_INTENT_TYPE);
        i.putExtra(Intent.EXTRA_EMAIL  , recipients);
        i.putExtra(Intent.EXTRA_SUBJECT, subject);
        i.putExtra(Intent.EXTRA_TEXT   , body);
        i.putExtra(Intent.EXTRA_STREAM, attachmentLocation);
        i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            activity.startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(activity, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }
}
