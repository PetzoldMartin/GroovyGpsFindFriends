package com.example.aisma.findmeclient;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle
import android.widget.Toast
import com.arasthel.swissknife.SwissKnife
import com.arasthel.swissknife.annotations.OnClick;

import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import android.app.Activity;
import android.os.Bundle
import org.osmdroid.views.overlay.ItemizedIconOverlay
import org.osmdroid.views.overlay.OverlayItem;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

public class MainActivity extends AppCompatActivity {
    def ILocator
    def mMapView;
    def mMapController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // This must be called for injection of views and callbacks to take place
        SwissKnife.inject(this);
        // This must be called for saved state restoring
        SwissKnife.restoreState(this, savedInstanceState);
        // This mus be called for automatic parsing of intent extras
        SwissKnife.loadExtras(this)

        mMapView = (MapView) findViewById(R.id.mapview);
        mMapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);
        mMapView.setBuiltInZoomControls(true);
        mMapController = (MapController) mMapView.getController();
        mMapController.setZoom(13);
        def gPt = new GeoPoint(0,0);
        mMapController.setCenter(gPt);

        ILocator=new ClientLocator(this);

    }

    @OnClick(R.id.test)
    public void onClick() {
        Toast.makeText(this, ILocator.toString(), Toast.LENGTH_SHORT).show()
            if (ILocator.getLocation().x == null)
            return;
        def gPt = new GeoPoint(ILocator.getLocation().x ,ILocator.getLocation().y);
        mMapController.setCenter(gPt);
        // Create an ArrayList with overlays to display objects on map
        def overlayItemArray = new ArrayList<OverlayItem>();

        // Create som init objects
        OverlayItem centerMe = new OverlayItem("Ich bin Hier", "Zweite Zeile",
                new GeoPoint(ILocator.getLocation().x,ILocator.getLocation().y));

        // Add the init objects to the ArrayList overlayItemArray
        overlayItemArray.add(centerMe);

        // Add the Array to the IconOverlay
        ItemizedIconOverlay<OverlayItem> itemizedIconOverlay = new ItemizedIconOverlay<OverlayItem>(this, overlayItemArray, null);

        // Add the overlay to the MapView
        mMapView.getOverlays().add(itemizedIconOverlay);

    }

    @OnClick(R.id.jetty)
    public void startJetty() {
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        Server jettyServer = new Server(8080);
        jettyServer.setHandler(context);
        ServletHolder jerseyServlet = context.addServlet(org.glassfish.jersey.servlet.ServletContainer.class, "/*");
        jerseyServlet.setInitOrder(0);
        jerseyServlet.setInitParameter("jersey.config.server.provider.classnames", Rest.class.getCanonicalName());
        jettyServer.start();
        jettyServer.join();
    }

    @Path('/')
    private class Rest {

        @GET
        @Path('/test/get')
        def get() {
            return "Works"
        }

    }

}

