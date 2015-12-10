package de.fh.zwickau.scriptsprachen.findme.client.location

import android.location.Location
import android.location.LocationListener
import android.os.Bundle

class MyLocationListener implements LocationListener {

    def la, lo

    String toString() {
        "@($la, $lo)"
    }

    @Override
    public void onLocationChanged(Location loc) {
        la = loc.getLatitude()
        lo = loc.getLongitude()
    }

    @Override
    public void onProviderDisabled(String provider) {
        println("GPS Disabled")
    }

    @Override
    public void onProviderEnabled(String provider) {
        println("GPS Enabled")
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

}