package de.fh.zwickau.scriptsprachen.findme.client.location

import android.content.Context
import android.location.LocationManager

class ClientLocator {

    LocationManager locationManagerI
    MyLocationListener mGpslocListenerI
    MyLocationListener mNetworklocListenerI
    Context mContext;

    ClientLocator(Context mContext){
        this.mContext = mContext;
        locationManagerI = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE)
        mGpslocListenerI = new MyLocationListener()
        locationManagerI.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mGpslocListenerI)
        if (locationManagerI.getAllProviders().contains(LocationManager.NETWORK_PROVIDER)) { // required since bluestacks does not have this provider
            mNetworklocListenerI = new MyLocationListener()
            locationManagerI.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mNetworklocListenerI)
            def gpsStatusListener = new MyGpsStatusListener(locationManagerI, mNetworklocListenerI)
            locationManagerI.addGpsStatusListener(gpsStatusListener)
        }
    }

    Vector getLocation(){
        if (mGpslocListenerI.getLa() == null && locationManagerI.getAllProviders().contains(LocationManager.NETWORK_PROVIDER))
            return new Vector(x:mNetworklocListenerI.getLa(), y:mNetworklocListenerI.getLo())
        else
            return new Vector(x:mGpslocListenerI.getLa(), y:mGpslocListenerI.getLo())
    }

}

