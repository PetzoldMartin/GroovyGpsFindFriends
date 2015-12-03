package com.example.aisma.findmeclient

import android.util.Log
import com.sun.jersey.spi.container.servlet.ServletContainer
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.ServletContextHandler
import org.eclipse.jetty.servlet.ServletHolder;

public class RESTServer {

    private final static String LOG_TAG = "Jetty"

    public void startServer() {
        java.net.InetSocketAddress addresse = new java.net.InetSocketAddress("localhost", 8088)
        System.setProperty("java.net.preferIPv4Stack", "true")
        System.setProperty("java.net.preferIPv6Addresses", "false")


        Server webServer = new Server(addresse)

        ServletHolder servletHolder = new ServletHolder(ServletContainer.class)

        servletHolder.setInitParameter("com.sun.jersey.config.property.resourceConfigClass", "com.sun.jersey.api.core.ClassNamesResourceConfig")
        servletHolder.setInitParameter("com.sun.jersey.config.property.classnames", "resources.Rest")

        ServletContextHandler servletContextHandler = new ServletContextHandler(webServer, "/", true, false)
        servletContextHandler.addServlet(servletHolder, "/hello")

        webServer.setHandler(servletContextHandler)


        try {
            webServer.start()
            Log.d(LOG_TAG, "started Web server")

        }
        catch (Exception e) {
            Log.d(LOG_TAG, "unexpected exception starting Web server: " + e)
        }
    }

}
