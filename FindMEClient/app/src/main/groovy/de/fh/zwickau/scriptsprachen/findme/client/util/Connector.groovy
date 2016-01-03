package de.fh.zwickau.scriptsprachen.findme.client.util

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.arasthel.swissknife.annotations.OnBackground
import com.arasthel.swissknife.annotations.OnUIThread
import de.fh.zwickau.scriptsprachen.findme.client.friend.Friend
import de.fh.zwickau.scriptsprachen.findme.client.friend.FriendState
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
        def email = StorageManager.getInstance().getEmail(activity)
        def name = StorageManager.getInstance().getName(activity)
        retryRequests(email, name)
        if (update) {
            // TODO: Remove the following code once the Friend functionality is fully implemented
            restRequest.getAllUsers(email, this)
            while (!restRequestDone) {
                if (restRequestFailed) {
                    Log.d("getFriends", "Failed to get list of E-Mail addresses")
                    showErrorToast("Fehler: E-Mail-Adressen konnten nicht empfangen werden")
                    restRequestFailed = false
                    break
                }
            }
            restRequestDone = false

            for (Friend f : friends.values()) {
                if (f.state != FriendState.FRIEND)
                    continue
                currentTargetEmail = f.email
                restRequest.getIpForEmail(email, currentTargetEmail, this)
                while (!restRequestDone) {
                    if (restRequestFailed) {
                        Log.d("getFriends", "Failed to get IP for " + currentTargetEmail)
                        showErrorToast("Fehler: IP-Adresse für " + currentTargetEmail + " konnte nicht empfangen werden")
                        restRequestFailed = false
                        restRequestDone = true
                    }
                }
                restRequestDone = false
            }

            for (Friend f : friends.values()) {
                if (f.state != FriendState.FRIEND)
                    continue
                currentTargetEmail = f.email
                restRequest.getLocation(f.lastKnownIp, email, this)
                while (!restRequestDone) {
                    if (restRequestFailed) {
                        Log.d("getFriends", "Failed to get location for " + currentTargetEmail)
                        showErrorToast("Fehler: Koordinaten für " + currentTargetEmail + " konnte nicht empfangen werden")
                        restRequestFailed = false
                        restRequestDone = true
                    }
                }
                restRequestDone = false
            }
        }
        println friends.values()
        return friends.values().asList()
    }

    @OnBackground
    public void updateFriend(Friend friend, FriendState newState) {
        def email = StorageManager.getInstance().getEmail(activity)

        friend.state = newState
        friends.put(friend.email, friend)

        if (friend.lastKnownIp == null)
            if (!tryGetIp(friend, email))
                return

		if (newState == FriendState.ACCEPTED) {
            def name = StorageManager.getInstance().getName(activity)
            restRequest.accept(friend, email, name, this)
        }
		if (newState == FriendState.DENIED)
			restRequest.deny(friend, email, this)
    }

    private boolean tryGetIp(Friend f, String ownEmail) {
        currentTargetEmail = f.email
        restRequest.getIpForEmail(ownEmail, currentTargetEmail, this)
        while (!restRequestDone) {
            if (restRequestFailed) {
                Log.d("getFriends", "Failed to get IP for " + currentTargetEmail)
                showErrorToast("Fehler: IP-Adresse für " + currentTargetEmail + " konnte nicht empfangen werden")
                restRequestFailed = false
                return false
            }
        }
        restRequestDone = false
        return true
    }

    public void removeFriend(Friend friend, boolean withRestRequest) {
        if (withRestRequest == true) {
            // We are the initiator of the remove functionality
            Friend f = friends.get(friend.email)
            f.state = FriendState.REMOVED
            def email = StorageManager.getInstance().getEmail(activity)
            restRequest.remove(f, email, this)
        }
        else {
            // We received the remove request or the REST request was successful
            friends.remove(friend.email)
        }
    }

    def restRequestDone(String response) {
        if (response.startsWith("[")) {
            // getOnlineUsers request
            response = response.replace("[", "").replace("]", "")
            String[] emailAddresses = response.split(",")
            for (String nameAndEmail : emailAddresses) {
                nameAndEmail = nameAndEmail.trim()
                String[] parts = nameAndEmail.split(":")
                Friend f = friends[parts[1]]
                if (f == null)
                    f = new Friend()
                f.email = parts[1]
                f.name = parts[0]
                f.state = FriendState.FRIEND
                friends.put(f.email, f)
            }
        }
        else if (response.contains(":")) {
            // getIP request
            Friend f = friends[currentTargetEmail]
            f.lastKnownIp = response.split(":")[0]
        }
        else {
            // getLocation request
            if (!"Not a friend".equals(response)) {
                Friend f = friends[currentTargetEmail]
                f.lastKnownLocation = Vector.fromString(response)
            }
            else
                showErrorToast(currentTargetEmail + " ist kein Freund")
        }
        restRequestDone = true
    }

    def restRequestFailed(String errorMessage) {
        restRequestFailed = true
    }

    @OnUIThread
    def showErrorToast(String message) {
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
    }

    @OnBackground
    def retryRequests(String ownEmail, String ownName) {
        for (Friend f : friends.values()) {
            if (!tryGetIp(f, ownEmail))
                continue;

            if (f.state == FriendState.REMOVED) 
                restRequest.remove(f, ownEmail, this)
            if (f.state == FriendState.ACCEPTED)
                restRequest.accept(f, ownEmail, ownName,  this)
            if (f.state == FriendState.DENIED)
                restRequest.deny(f, ownEmail, this)
        }

    }

}