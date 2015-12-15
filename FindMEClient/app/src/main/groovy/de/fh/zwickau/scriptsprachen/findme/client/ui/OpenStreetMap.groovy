package de.fh.zwickau.scriptsprachen.findme.client.ui

import android.content.Context
import android.os.Looper
import android.support.v4.content.ContextCompat
import android.widget.Toast
import com.arasthel.swissknife.annotations.OnBackground
import com.arasthel.swissknife.annotations.OnUIThread
import de.fh.zwickau.scriptsprachen.findme.client.R
import de.fh.zwickau.scriptsprachen.findme.client.location.ClientLocator
import de.fh.zwickau.scriptsprachen.findme.client.util.Friend
import org.osmdroid.bonuspack.overlays.InfoWindow
import org.osmdroid.bonuspack.overlays.Marker
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapController
import org.osmdroid.views.MapView

class OpenStreetMap {

    def mContext
    def mMapView
    def mMapController
    def ILocator
    HashMap<String, Marker> friendNodes = [:]

    OpenStreetMap(Context mContext, MapView mMapView, ClientLocator ILocator) {
        this.mContext = mContext
        this.mMapView = mMapView
        this.ILocator = ILocator
        initialize()
    }

    public void initialize(){
        // Initial settings
        mMapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)
        mMapView.setBuiltInZoomControls(true)
        mMapController = (MapController) mMapView.getController()
        mMapController.setZoom(13)
        def gPt = new GeoPoint(0, 0)
        mMapController.setCenter(gPt)

        setOwnLocationToCenter()
    }

    @OnBackground
    void setOwnLocationToCenter() {
        // Wait for listener to get data
        while (ILocator.getLocation().x == null) {
            sleep(1000)
        }
        def gPt = new GeoPoint(ILocator.getLocation().x, ILocator.getLocation().y)
        mMapController.setCenter(gPt)
        createSelf(gPt)

        refreshMap()
    }

    @OnUIThread
    void refreshMap() {
        mMapView.invalidate()
    }

    Marker createNode(GeoPoint geoPoint, boolean self = false) {
        Marker nodeMarker = new Marker(mMapView)
        nodeMarker.setPosition(geoPoint)
        def nodeIcon = ContextCompat.getDrawable(mContext, R.drawable.marker_cluster)
        if (!self)
            nodeMarker.setIcon(nodeIcon)
        mMapView.getOverlays().add(nodeMarker)
        nodeMarker
    }

    public void createFriend(Friend friend){
        if (friend.lastKnownLocation == null)
            return

        def friendNode = createNode(friend.lastKnownLocation.x,friend.lastKnownLocation.y)
        friendNode.setTitle(friend.name)
        friendNode.setSnippet(friend.email)
        friendNode.setSubDescription(friend.lastKnownIp)
        friendNodes[friend.email] = friendNode
    }

    public void removeAllMarkers() {
        for (Marker m : friendNodes.values()) {
            closeInfoWindow(m)
            mMapView.getOverlays().remove(m)
        }
    }

    public void removeFriend(Friend friend) {
        def friendNode = friendNodes[friend.email]
        if (friendNode != null){
            closeInfoWindow(friendNode)
            mMapView.getOverlays().remove(friendNode)
        }
    }

    @OnUIThread
    public closeInfoWindow(Marker marker){
        marker.closeInfoWindow()
    }

    Marker createNode(double lat, double loc) {
        def geoPoint = new GeoPoint(lat, loc)
        createNode(geoPoint)
    }

    void createSelf(GeoPoint geoPoint) {
        createNode(geoPoint, true)
    }

}

