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

    OpenStreetMap(Context mContext, MapView mMapView, ClientLocator ILocator) {
        this.mContext = mContext
        this.mMapView = mMapView
        this.ILocator = ILocator
        initialize()
    }

    public void initialize(){
        //initial settings
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
        // wait for listener to get data
        while (ILocator.getLocation().x == null) {
            sleep(1000)
        }
        def gPt = new GeoPoint(ILocator.getLocation().x, ILocator.getLocation().y)
        mMapController.setCenter(gPt)
        createSelf(gPt)

        refreshMap()

        // prepare for Toast in non UI thread
        Looper.prepare()
        Toast.makeText(mContext, "Ausgangspunkt " + ILocator.toString() + " gesetzt!", Toast.LENGTH_SHORT).show()
    }

    /**
     * Do this everytime on the UI thread because views can only get handled there
     */
    @OnUIThread
    void refreshMap() {
        mMapView.invalidate()
    }

    /**
     * create Node on Map
     */
    Marker createNode(GeoPoint geoPoint, boolean self = false) {
        Marker nodeMarker = new Marker(mMapView)
        nodeMarker.setPosition(geoPoint)
        def nodeIcon = ContextCompat.getDrawable(mContext, R.drawable.marker_cluster)
        if (!self)
            nodeMarker.setIcon(nodeIcon)
        mMapView.getOverlays().add(nodeMarker)
        nodeMarker
    }

    @OnBackground
    public void createFriend(Friend friend){
        if (friend.lastKnownLocation == null)
            return

        def friendNode = createNode(friend.lastKnownLocation.x,friend.lastKnownLocation.y)
        friendNode.setTitle(friend.name)
        friendNode.setSnippet(friend.email)
        friendNode.setSubDescription(friend.lastKnownIp)
    }

    /**
     * @Overload
     */
    Marker createNode(double lat, double loc) {
        def geoPoint = new GeoPoint(lat, loc)
        createNode(geoPoint)
    }

    void createSelf(GeoPoint geoPoint) {
        createNode(geoPoint, true)
    }
}

