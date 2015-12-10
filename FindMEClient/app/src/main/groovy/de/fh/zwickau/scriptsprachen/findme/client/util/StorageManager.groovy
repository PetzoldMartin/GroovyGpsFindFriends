package de.fh.zwickau.scriptsprachen.findme.client.util

import android.app.Activity
import android.content.Context

class StorageManager {

    private static StorageManager instance

    private static final String LOGIN_DATA_FILENAME = "loginData"

    private StorageManager() {

    }

    public static StorageManager getInstance() {
        if (instance == null)
            instance = new StorageManager()
        return instance
    }

    public void storeLoginData(String email, Activity context) {
        FileOutputStream fos = context.openFileOutput(LOGIN_DATA_FILENAME, Context.MODE_PRIVATE)
        fos.write(email.getBytes())
        fos.close()
    }

    public String getLoginData(Activity context) {
        try {
            FileInputStream fis = context.openFileInput(LOGIN_DATA_FILENAME)
            List<String> lines = fis.readLines()
            String email = lines.size() > 0 ? lines.get(0) : null
            fis.close()
            return email
        } catch (Exception ex) {
            return null
        }
    }

}