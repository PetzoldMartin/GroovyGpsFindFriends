package com.example.aisma.findmeclient

import android.app.Activity

final class Core {

    public static final String SERVER_IP = "http://10.0.2.2:8080"

    private static IConnector connector = null
    private static ClientLocator locator = null
    private static Activity activity = null
    private static final RESTServer restServer = new RESTServer()

    private Core() {

    }

    public static void init(Activity activity) {
        this.activity = activity
        locator = new ClientLocator(activity.getApplicationContext())
        restServer.startServer(locator)
    }

    public static synchronized IConnector getConnector() {
        if (connector == null)
            connector = Connector.getInstance()
        return connector
    }

    public static synchronized ClientLocator getLocator() {
        return locator
    }

    public static synchronized void stopServer() {
        // TODO
    }

}