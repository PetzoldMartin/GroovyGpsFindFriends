package de.fh.zwickau.scriptsprachen.findme.client.util

import android.app.Activity
import de.fh.zwickau.scriptsprachen.findme.client.location.ClientLocator
import de.fh.zwickau.scriptsprachen.findme.client.rest.RESTServer

final class Core {

    public static final String SERVER_IP = "http://10.0.2.2:8080"

    private static IConnector connector = null
    private static ClientLocator locator = null
    private static final RESTServer restServer = new RESTServer()

    private Core() {

    }

    public static void init(Activity activity) {
        locator = new ClientLocator(activity.getApplicationContext())
        connector = Connector.getInstance(activity)
        restServer.startServer()
    }

    public static synchronized IConnector getConnector() {
        return connector
    }

    public static synchronized ClientLocator getLocator() {
        return locator
    }

    public static synchronized void stopServer() {
        restServer.stopServer()
    }

}