package de.fh.zwickau.scriptsprachen.findme.client.friend

import de.fh.zwickau.scriptsprachen.findme.client.location.Vector
import de.fh.zwickau.scriptsprachen.findme.client.util.Core;

class Friend implements Serializable {

    def name
    def email
    def lastKnownIp
    Vector lastKnownLocation
    FriendState state
    boolean visibility = false
    def ViewGroupNr, ViewNr

    @Override
    public String toString() {
        return "name: " + name + ", email: " + email + ", last known IP: " + lastKnownIp + ", last known location: " + lastKnownLocation + ", state: " + state + ", visibility: " + visibility
    }

}

