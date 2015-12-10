package de.fh.zwickau.scriptsprachen.findme.client.util

import de.fh.zwickau.scriptsprachen.findme.client.location.Vector;

class Friend {

    def name
    def email
    def lastKnownIp
    Vector lastKnownLocation

    @Override
    public String toString() {
        return "name: " + name + ", email: " + email + ", last known IP: " + lastKnownIp + ", last known location: " + lastKnownLocation
    }

}