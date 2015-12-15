package de.fh.zwickau.scriptsprachen.findme.client.ui

import android.app.Activity
import android.app.ProgressDialog
import com.arasthel.swissknife.annotations.OnUIThread

class Progress {

    static ProgressDialog progDialog
    static boolean isDialogShownFlag = false

    @OnUIThread
    public static void showProgress(String title, Activity activity) {
        progDialog = ProgressDialog.show(activity, title, "Bitte warten")
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        isDialogShownFlag = true
    }

    @OnUIThread
    public static void dismissProgress() {
        progDialog.dismiss()
        progDialog = null
        isDialogShownFlag = false
    }

    public static boolean isDialogShown() {
        return isDialogShownFlag
    }

}