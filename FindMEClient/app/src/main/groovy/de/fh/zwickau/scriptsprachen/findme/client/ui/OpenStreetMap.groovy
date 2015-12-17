package de.fh.zwickau.scriptsprachen.findme.client.ui

import android.content.Context
import android.support.v4.content.ContextCompat
import com.arasthel.swissknife.annotations.OnBackground
import com.arasthel.swissknife.annotations.OnUIThread
import de.fh.zwickau.scriptsprachen.findme.client.R
import de.fh.zwickau.scriptsprachen.findme.client.location.ClientLocator
import de.fh.zwickau.scriptsprachen.findme.client.friend.Friend
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
    def selfNode

    OpenStreetMap(Context mContext, MapView mMapView, ClientLocator ILocator) {
        this.mContext = mContext
        this.mMapView = mMapView
        this.ILocator = ILocator
        // Initial settings
        mMapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)
        mMapView.setBuiltInZoomControls(true)
        mMapController = (MapController) mMapView.getController()
        mMapController.setZoom(15)
        setOwnLocationToCenter()
    }

    @OnBackground
    public void setOwnLocationToCenter() {
        // Wait for listener to get data
        while (ILocator.getLocation().x == null) {
            sleep(1000)
        }
        removeMarker(selfNode)
        def gPt = new GeoPoint(ILocator.getLocation().x, ILocator.getLocation().y)
        selfNode = createSelf(gPt)
        mMapController.setCenter(gPt)

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

    public void createFriend(Friend friend) {
        if (friend.lastKnownLocation == null)
            return

        def friendNode = createNode(friend.lastKnownLocation.x, friend.lastKnownLocation.y)
        friendNode.setTitle(friend.name)
        friendNode.setSnippet(friend.email)
        friendNode.setSubDescription(friend.lastKnownIp)
        friendNodes[friend.email] = friendNode
    }

    public void removeAllMarkers() {
        for (Marker m : friendNodes.values()) {
            removeMarker(m)
        }
    }

    public void removeFriend(Friend friend) {
        def friendNode = friendNodes[friend.email]
        removeMarker(friendNode)
    }

    void removeMarker(Marker marker) {
        if (marker != null) {
            closeInfoWindow(marker)
            mMapView.getOverlays().remove(marker)
        }
    }

    @OnUIThread
    public closeInfoWindow(Marker marker) {
        marker.closeInfoWindow()
    }

    Marker createNode(double lat, double loc) {
        def geoPoint = new GeoPoint(lat, loc)
        createNode(geoPoint)
    }

    Marker createSelf(GeoPoint geoPoint) {
        createNode(geoPoint, true)
    }

}

