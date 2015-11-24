package com.example.aisma.findmeclient

import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity;

class Vector{
    def x,y
    Vector(){

    }
}

class ClientLocator {
    LocationManager locationManagerI
    MyLocationListener mlocListenerI
    Context mContext;
    ClientLocator(Context mContext){
        this.mContext = mContext;
        locationManagerI = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE)
        mlocListenerI = new MyLocationListener()
        locationManagerI.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mlocListenerI)
    }

    Vector getLocation(){
       return new Vector(x:mlocListenerI.getLa(),y:mlocListenerI.getLo())
    }

    String toString() {
        "$mlocListenerI"
    }
}

class MyLocationListener implements LocationListener {
    def la, lo

    String toString() {
        "@($la, $lo)"
    }

    @Override
    public void onLocationChanged(Location loc) {
        la = loc.getLatitude()
        lo = loc.getLongitude()
        println(this.toString())
    }

    @Override
    public void onProviderDisabled(String provider) {
        println("Gps Disabled")
    }

    @Override
    public void onProviderEnabled(String provider) {
        println("Gps Enabled")
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}
}