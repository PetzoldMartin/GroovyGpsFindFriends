package de.fh.zwickau.scriptsprachen.findme.client.ui

import de.fh.zwickau.scriptsprachen.findme.client.friend.Friend;

class EFriendList extends ArrayList<Friend> {

    public EFriendList(List<Friend> list){
        this.addAll(list);

    }
    Friend getFriendByListId (int ViewGroupNr, int ViewNr){
        def f = null;
        this.each {
            if (it.getClass() == Friend.class) {
                if (((Friend) it).getViewGroupNr() == ViewGroupNr && ((Friend) it).getViewNr() == ViewNr) {
                    f = (Friend) it
                }
            }
        }
        return f;
    }

    Friend getFriendByEmail(String email){
        def f = null;
        this.each {
            if (it.getClass() == Friend.class) {
                if (((Friend) it).getEmail() == email) {
                    f = (Friend) it
                }
            }
        }
        return f
    }
}