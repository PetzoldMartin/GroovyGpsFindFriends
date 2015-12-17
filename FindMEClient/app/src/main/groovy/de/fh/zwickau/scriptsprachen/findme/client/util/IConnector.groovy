package de.fh.zwickau.scriptsprachen.findme.client.util

import de.fh.zwickau.scriptsprachen.findme.client.friend.Friend;

interface IConnector {

    public List<Friend> getFriends(boolean update)
    public void updateFriend(Friend friend)
    public void removeFriend(Friend friend)

}