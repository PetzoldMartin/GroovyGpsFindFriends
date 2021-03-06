package de.fh.zwickau.scriptsprachen.findme.client.util

import android.app.Activity
import android.content.Context
import de.fh.zwickau.scriptsprachen.findme.client.friend.Friend

class StorageManager {

    private static StorageManager instance

    private static final String LOGIN_DATA_FILENAME = "loginData"
    private static final String FRIEND_LIST_FILENAME = "friendsData"


    private StorageManager() {

    }

    public static synchronized StorageManager getInstance() {
        if (instance == null)
            instance = new StorageManager()
        return instance
    }

    public void storeFriends(def friends, Activity context) {
        FileOutputStream fos = context.openFileOutput(FRIEND_LIST_FILENAME, Context.MODE_PRIVATE)
        ObjectOutputStream os = new ObjectOutputStream(fos)
        os.writeObject(friends)
        os.close()
        fos.close()
    }

    public def loadFriends(Activity context) {
        try {
            FileInputStream fis = context.openFileInput(FRIEND_LIST_FILENAME)
            ObjectInputStream is = new ObjectInputStream(fis)
            def friends = is.readObject();
            is.close()
            fis.close()
            return Collections.synchronizedMap(friends)
        } catch (Exception e) {
            return Collections.synchronizedMap(new HashMap<String, Friend>())
        }
    }

    public void storeLoginData(String email, String name, Activity context) {
        FileOutputStream fos = context.openFileOutput(LOGIN_DATA_FILENAME, Context.MODE_PRIVATE)
        fos.write(new String(email + ":" + name).getBytes())
        fos.close()
    }


    public String getEmail(Activity context) {
        try {
            FileInputStream fis = context.openFileInput(LOGIN_DATA_FILENAME)
            List<String> lines = fis.readLines()
            String email = lines.size() > 0 ? lines.get(0).split(":")[0] : null
            fis.close()
            return email
        } catch (Exception ex) {
            return null
        }
    }

    public String getName(Activity context) {
        try {
            FileInputStream fis = context.openFileInput(LOGIN_DATA_FILENAME)
            List<String> lines = fis.readLines()
            String name = lines.size() > 0 ? lines.get(0).split(":")[1] : null
            fis.close()
            return name
        } catch (Exception ex) {
            return null
        }
    }

}