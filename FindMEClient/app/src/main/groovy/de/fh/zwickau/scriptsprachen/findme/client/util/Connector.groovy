package de.fh.zwickau.scriptsprachen.findme.client.util

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.arasthel.swissknife.annotations.OnBackground
import com.arasthel.swissknife.annotations.OnUIThread
import de.fh.zwickau.scriptsprachen.findme.client.friend.Friend
import de.fh.zwickau.scriptsprachen.findme.client.friend.FriendState
import de.fh.zwickau.scriptsprachen.findme.client.location.Vector
import de.fh.zwickau.scriptsprachen.findme.client.rest.RESTRequests

class Connector implements IConnector {

    private static Connector instance;

    def friends = Collections.synchronizedMap(new HashMap<String, Friend>())
    def restRequest = new RESTRequests()
    def activity = null
    def restRequestDone = false
    def restRequestFailed = false
    def currentTargetEmail

    private Connector(Activity activity) {
        this.activity = activity
        friends = StorageManager.getInstance().loadFriends(activity)
        //testing data for serialization
//        friends.put(getTestFriend().email,getTestFriend())
//        StorageManager.getInstance().storeFriends(friends,activity)
//        friends=[:]
    }

    public static synchronized Connector getInstance(Activity activity) {
        if (instance == null)
            instance = new Connector(activity)
        return instance
    }

    Friend getTestFriend() {
        Friend test = new Friend()
        test.email = "testemail"
        test.name = "testname"
        test.state = FriendState.FRIEND
        test.lastKnownIp = "192.168.0.5"
        return test
    }

    @Override
    List<Friend> getFriends(boolean update) {
        def email = StorageManager.getInstance().getEmail(activity)
        def name = StorageManager.getInstance().getName(activity)

        List<Friend> requestList = friends.values().asList().findAll {
            ((Friend) it).state != FriendState.FRIEND && ((Friend) it).state != FriendState.REQUESTED && ((Friend) it).state != FriendState.REQUESTSENT
        }
        retryRequests(email, name, requestList)
        if (update) {
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
        StorageManager.getInstance().storeFriends(friends, activity)
        println friends.values()
        return friends.values().asList()
    }

    @OnBackground
    public void updateFriend(Friend friend, FriendState newState) {
        def email = StorageManager.getInstance().getEmail(activity)

        friend.state = newState
        friends.put(friend.email, friend)
        StorageManager.getInstance().storeFriends(friends, activity)

        if (friend.lastKnownIp == null && friend.state != FriendState.REQUESTED)
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
        } else {
            // We received the remove request or the REST request was successful
            friends.remove(friend.email)
            StorageManager.getInstance().storeFriends(friends, activity)
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
        } else if (response.contains(":")) {
            // getIP request
            Friend f = friends[currentTargetEmail]
            f.lastKnownIp = response.split(":")[0]
        } else {
            // getLocation request
            if (!"Not a friend".equals(response)) {
                Friend f = friends[currentTargetEmail]
                f.lastKnownLocation = Vector.fromString(response)
            } else
                showErrorToast(currentTargetEmail + " ist kein Freund")
        }
        restRequestDone = true
        StorageManager.getInstance().storeFriends(friends, activity)

    }

    def restRequestFailed(String errorMessage) {
        restRequestFailed = true
    }

    @OnUIThread
    def showErrorToast(String message) {
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
    }

    @OnBackground
    def retryRequests(String ownEmail, String ownName, List<Friend> list) {
        for (Friend f : friends.values()) {
            if (f.state == FriendState.REQUESTED || f.state == FriendState.FRIEND ||
                    f.state == FriendState.REQUESTSENT || !tryGetIp(f, ownEmail))
                continue;

            if (f.state == FriendState.REMOVED)
                restRequest.remove(f, ownEmail, this)
            if (f.state == FriendState.ACCEPTED)
                restRequest.accept(f, ownEmail, ownName, this)
            if (f.state == FriendState.DENIED)
                restRequest.deny(f, ownEmail, this)
        }

    }

    @OnBackground
    public void requestFriend(String targetEmail) {
        Friend f = friends.get(targetEmail)
        if (f == null || f.state == FriendState.REQUESTSENT) {
            f = new Friend()
            f.setEmail(targetEmail)
            f.setState(FriendState.REQUESTSENT)
            friends.put(targetEmail, f)
            def ownEmail = StorageManager.getInstance().getEmail(activity)
            def ownName = StorageManager.getInstance().getName(activity)
            if (tryGetIp(f, ownEmail))
                restRequest.requestFriend(f, ownEmail, ownName)
        }
    }
}