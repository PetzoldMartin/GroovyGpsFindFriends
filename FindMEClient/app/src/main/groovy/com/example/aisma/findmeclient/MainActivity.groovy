package com.example.aisma.findmeclient

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.arasthel.swissknife.SwissKnife
import com.arasthel.swissknife.annotations.OnBackground
import com.arasthel.swissknife.annotations.OnClick
import org.glassfish.jersey.server.ServerProperties
import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView
import android.os.Bundle
import org.osmdroid.views.overlay.ItemizedIconOverlay
import org.osmdroid.views.overlay.OverlayItem;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder

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
        def gPt = new GeoPoint(0, 0);
        mMapController.setCenter(gPt);

        ILocator = new ClientLocator(this);

    }

    @OnClick(R.id.test)
    public void onClick() {
        Toast.makeText(this, ILocator.toString(), Toast.LENGTH_SHORT).show()
        if (ILocator.getLocation().x == null)
            return;
        def gPt = new GeoPoint(ILocator.getLocation().x, ILocator.getLocation().y);
        mMapController.setCenter(gPt);

        def Marker = new Marker(mMapView)

        // Create an ArrayList with overlays to display objects on map
        def overlayItemArray = new ArrayList<OverlayItem>();

        // Create som init objects
        OverlayItem centerMe = new OverlayItem("Ich bin Hier", "Zweite Zeile",
                new GeoPoint(ILocator.getLocation().x, ILocator.getLocation().y));

        // Add the init objects to the ArrayList overlayItemArray
        overlayItemArray.add(centerMe);

        // Add the Array to the IconOverlay
        ItemizedIconOverlay<OverlayItem> itemizedIconOverlay = new ItemizedIconOverlay<OverlayItem>(this, overlayItemArray, null);

        // Add the overlay to the MapView
        mMapView.getOverlays().add(itemizedIconOverlay);

    }

    @OnClick(R.id.jetty)
    public void startJetty() {
//        ResourceConfig resourceConfig = new ResourceConfig();
//        resourceConfig.packages(Rest.class.getPackage().getName());
//        ServletContainer servletContainer = new ServletContainer(resourceConfig);
//        ServletHolder sh = new ServletHolder(servletContainer);
//        Server server = new Server(8080);
//        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
//        context.setContextPath("/");
//        context.addServlet(sh, "/*");
//        server.setHandler(context);
//        server.start();
//        server.join();

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        Server jettyServer = new Server(8080);
        jettyServer.setHandler(context);
        ServletHolder jerseyServlet = context.addServlet(org.glassfish.jersey.servlet.ServletContainer.class, "/*");
        jerseyServlet.setInitOrder(1);
        jerseyServlet.setInitParameter(ServerProperties.PROVIDER_PACKAGES, "resources");
        jettyServer.start();
        jettyServer.join();
    }

    @OnClick(R.id.restClient)
    public void restResponse() {
        restRequest()
    }
    @OnBackground
    public void restRequest() {
                final String url = "http://rest-service.guides.spring.io/greeting";
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                String response = restTemplate.getForEntity(url, Object).getClass();
                response.
        }


}