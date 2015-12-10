package de.fh.zwickau.scriptsprachen.findme.client.util

import android.app.Activity
import android.util.Log
import de.fh.zwickau.scriptsprachen.findme.client.rest.RESTRequests
import de.fh.zwickau.scriptsprachen.findme.client.location.Vector

class Connector implements IConnector {

    private static Connector instance;

    def friends = [:]
    def restRequest = new RESTRequests()
    def activity = null
    def restRequestDone = false
    def restRequestFailed = false
    def currentTargetEmail

    private Connector(Activity activity) {
        this.activity = activity
    }

    public static synchronized Connector getInstance(Activity activity) {
        if (instance == null)
            instance = new Connector(activity)
        return instance
    }

    @Override
    List<Friend> getFriends(boolean update) {
        if (update) {
            def email = StorageManager.getInstance().getLoginData(activity)
            restRequest.getAllUsers(email, this)
            while (!restRequestDone) {
                if (restRequestFailed) {
                    Log.d("getFriends", "Failed to get list of E-Mail addresses")
                    restRequestFailed = false
                    break
                }
            }
            restRequestDone = false
            for (Friend f : friends.values()) {
                currentTargetEmail = f.email
                restRequest.getIpForEmail(email, currentTargetEmail, this)
                while (!restRequestDone) {
                    if (restRequestFailed) {
                        Log.d("getFriends", "Failed to get IP for " + currentTargetEmail)
                        restRequestFailed = false
                        continue
                    }
                }
                restRequestDone = false
            }
            for (Friend f : friends.values()) {
                currentTargetEmail = f.email
                restRequest.getLocation(f.lastKnownIp, this)
                while (!restRequestDone) {
                    if (restRequestFailed) {
                        Log.d("getFriends", "Failed to get location for " + currentTargetEmail)
                        restRequestFailed = false
                        continue
                    }
                }
                restRequestDone = false
            }
        }
        println friends.values()
        return friends.values().asList()
    }

    def restRequestDone(String response) {
        if (response.startsWith("[")) {
            // getOnlineUsers request
            response = response.replace("[", "").replace("]", "")
            String[] emailAddresses = response.split(",")
            for (String email : emailAddresses) {
                email = email.trim()
                Friend f = friends[email]
                if (f == null)
                    f = new Friend()
                f.email = email
                f.name = "test"
                friends.put(email, f)
            }
        }
        else if (response.contains(":")) {
            // getIP request
            Friend f = friends[currentTargetEmail]
            f.lastKnownIp = response
        }
        else {
            // getLocation request
            Friend f = friends[currentTargetEmail]
            f.lastKnownLocation = Vector.fromString(response)
        }
        restRequestDone = true
    }

    def restRequestFailed(String errorMessage) {
        // TODO
        restRequestFailed = true
    }

}