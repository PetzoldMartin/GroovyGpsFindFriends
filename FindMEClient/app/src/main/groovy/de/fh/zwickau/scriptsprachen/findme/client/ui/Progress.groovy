package de.fh.zwickau.scriptsprachen.findme.client.ui

import android.app.Activity
import android.app.ProgressDialog

class Progress {

    static ProgressDialog progDialog

    public static void showProgress(String title, Activity activity) {
        progDialog = ProgressDialog.show(activity, title, "Bitte warten")
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
    }

    public static void dismissProgress() {
        progDialog.dismiss()
    }

}