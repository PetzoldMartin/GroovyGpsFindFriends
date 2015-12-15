package de.fh.zwickau.scriptsprachen.findme.client.location

import android.content.Context
import android.location.LocationManager

class ClientLocator {

    LocationManager locationManagerI
    MyLocationListener mlocListenerI
    Context mContext;

    ClientLocator(Context mContext){
        this.mContext = mContext;
        locationManagerI = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE)
        mlocListenerI = new MyLocationListener()
        locationManagerI.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mlocListenerI)
        if (locationManagerI.getAllProviders().contains(LocationManager.NETWORK_PROVIDER)) // required since bluestacks does not have this provider
            locationManagerI.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mlocListenerI)
    }

    Vector getLocation(){
       return new Vector(x:mlocListenerI.getLa(), y:mlocListenerI.getLo())
    }

}

