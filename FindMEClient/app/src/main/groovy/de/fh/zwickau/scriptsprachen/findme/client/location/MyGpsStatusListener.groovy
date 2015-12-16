package de.fh.zwickau.scriptsprachen.findme.client.location

import android.location.GpsStatus
import android.location.LocationListener
import android.location.LocationManager;

class MyGpsStatusListener implements GpsStatus.Listener {

    LocationManager locationManager
    LocationListener networkLocationListener

    public MyGpsStatusListener(LocationManager locationManager, LocationListener networkLocationListener) {
        this.locationManager = locationManager
        this.networkLocationListener = networkLocationListener
    }

    @Override
    void onGpsStatusChanged(int event) {
        if (event == GpsStatus.GPS_EVENT_FIRST_FIX) {
            println "Got first GPS location - removing network provider"
            // Remove Network Location Provider
            locationManager.removeUpdates(networkLocationListener)
            // Remove this listener
            locationManager.removeGpsStatusListener(this)
        }
    }

}