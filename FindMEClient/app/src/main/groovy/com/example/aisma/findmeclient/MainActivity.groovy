package com.example.aisma.findmeclient

import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.arasthel.swissknife.SwissKnife
import com.arasthel.swissknife.annotations.OnClick
import org.osmdroid.views.MapView
import android.os.Bundle

public class MainActivity extends AppCompatActivity {
    def static ILocator
    def openStreetMap;
    RESTRequests restRequests;
    RESTServer restServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sidebar_container);
        this.setLayoutInlay(R.layout.activity_main);
        // This must be called for injection of views and callbacks to take place
        SwissKnife.inject(this);
        // This must be called for saved state restoring
        SwissKnife.restoreState(this, savedInstanceState);
        // This mus be called for automatic parsing of intent extras
        SwissKnife.loadExtras(this)

        ILocator = new ClientLocator(this);
        openStreetMap = new OpenStreetMap(this, (MapView) findViewById(R.id.mapview), ILocator)
        restRequests = new RESTRequests()
        restServer = new RESTServer()

        getSupportActionBar().show();
    }

    @OnClick(R.id.jetty)
    public void startJetty() {
        restServer.startServer(ILocator)
    }

    @OnClick(R.id.restClient)
    public void restResponse() {
        //restRequests.testRestRequest()
        //restRequests.getAllUsers("testemail")
        //restRequests.register("myemail", "Tobias")
        //restRequests.login("myemail")
        //restRequests.getIpForEmail("testemail", "myemail")
        //restRequests.logout("myemail")
        restRequests.getLocation("localhost:8080")
    }

    public static ClientLocator getLocator() {
        return this.ILocator
    }

    private void setLayoutInlay(id){
        LayoutInflater inflater = getLayoutInflater();
        View v = inflater.inflate(id, null);
        ((FrameLayout)findViewById(R.id.content_frame)).addView(v);
    }
}
