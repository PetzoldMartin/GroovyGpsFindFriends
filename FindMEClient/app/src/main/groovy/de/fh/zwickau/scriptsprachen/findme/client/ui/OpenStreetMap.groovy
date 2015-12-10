package de.fh.zwickau.scriptsprachen.findme.client.ui

import android.content.Context
import android.os.Looper
import android.support.v4.content.ContextCompat
import android.widget.Toast
import com.arasthel.swissknife.annotations.OnBackground
import com.arasthel.swissknife.annotations.OnUIThread
import de.fh.zwickau.scriptsprachen.findme.client.R
import de.fh.zwickau.scriptsprachen.findme.client.location.ClientLocator
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

    def centerPoint, zoomLevel

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
        //TODO: Autozoom so i can see all friends at start
        // create test friend
        createNode(ILocator.getLocation().x + 0.1, ILocator.getLocation().y)

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
     * Testfriend
     * TODO: add dynamic data for title, snippet and subdescription
     */
    void createNode(GeoPoint geoPoint, boolean self = false) {
        Marker nodeMarker = new Marker(mMapView)
        nodeMarker.setPosition(geoPoint)
        def nodeIcon = ContextCompat.getDrawable(mContext, R.drawable.marker_cluster)
        if (!self)
            nodeMarker.setIcon(nodeIcon)
        nodeMarker.setTitle("Max Mueller")
        nodeMarker.setSnippet("Snippettext")
        nodeMarker.setSubDescription("SubDescription Text")
        mMapView.getOverlays().add(nodeMarker)
    }

    /**
     * @Overload
     */
    void createNode(double lat, double loc) {
        def geoPoint = new GeoPoint(lat, loc)
        createNode(geoPoint)
    }

    void createSelf(GeoPoint geoPoint) {
        createNode(geoPoint, true)
    }
}

