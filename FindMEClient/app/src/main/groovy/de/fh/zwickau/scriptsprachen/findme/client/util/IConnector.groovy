package de.fh.zwickau.scriptsprachen.findme.client.util

import de.fh.zwickau.scriptsprachen.findme.client.friend.Friend
import de.fh.zwickau.scriptsprachen.findme.client.friend.FriendState;

interface IConnector {

    public List<Friend> getFriends(boolean update)
    public void updateFriend(Friend friend, FriendState newState)
    public void removeFriend(Friend friend, boolean withRestRequest)
    public void requestFriend(String targetEmail)


}