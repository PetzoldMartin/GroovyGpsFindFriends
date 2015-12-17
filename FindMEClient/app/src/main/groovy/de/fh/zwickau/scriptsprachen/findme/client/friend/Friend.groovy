package de.fh.zwickau.scriptsprachen.findme.client.friend

import de.fh.zwickau.scriptsprachen.findme.client.location.Vector;

class Friend {

    def name
    def email
    def lastKnownIp
    Vector lastKnownLocation
    FriendState state

    @Override
    public String toString() {
        return "name: " + name + ", email: " + email + ", last known IP: " + lastKnownIp + ", last known location: " + lastKnownLocation + ", state: " + state
    }

}