package com.example.aisma.findmeclient

import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity;

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

