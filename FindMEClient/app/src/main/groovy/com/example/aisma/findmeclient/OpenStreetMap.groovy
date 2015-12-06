package com.example.aisma.findmeclient

import android.content.Context
import android.support.v4.content.ContextCompat
import android.widget.Toast
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

        mMapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)
        mMapView.setBuiltInZoomControls(true)
        mMapController = (MapController) mMapView.getController()
        mMapController.setZoom(13)
        def gPt = new GeoPoint(0, 0)
        mMapController.setCenter(gPt)
    }

    void test(){
        Toast.makeText(mContext, ILocator.toString(), Toast.LENGTH_SHORT).show()
        if (ILocator.getLocation().x == null)
            return
        def gPt = new GeoPoint(ILocator.getLocation().x, ILocator.getLocation().y)
        mMapController.setCenter(gPt)

        Marker nodeMarker = new Marker(mMapView)
        nodeMarker.setPosition(new GeoPoint(ILocator.getLocation().x, ILocator.getLocation().y))
        def nodeIcon = ContextCompat.getDrawable(mContext, R.drawable.marker_cluster)
        nodeMarker.setIcon(nodeIcon)
        nodeMarker.setTitle("Max Mueller")
        nodeMarker.setSnippet("Snippettext")
        nodeMarker.setSubDescription("SubDescription Text")
        mMapView.getOverlays().add(nodeMarker)
        mMapView.invalidate()
    }
}

