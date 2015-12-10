package de.fh.zwickau.scriptsprachen.findme.client.rest

import android.util.Log
import com.arasthel.swissknife.annotations.OnBackground
import com.sun.jersey.spi.container.servlet.ServletContainer
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.ServletContextHandler
import org.eclipse.jetty.servlet.ServletHolder

public class RESTServer {

    private final static String LOG_TAG = "Jetty"
    private Server webServer = null

    @OnBackground
    public void startServer() {
        java.net.InetSocketAddress addresse = new java.net.InetSocketAddress("localhost", 8080)
        System.setProperty("java.net.preferIPv4Stack", "true")
        System.setProperty("java.net.preferIPv6Addresses", "false")

        webServer = new Server(addresse)

        ServletHolder servletHolder = new ServletHolder(ServletContainer.class)

        servletHolder.setInitParameter("com.sun.jersey.config.property.resourceConfigClass", "com.sun.jersey.api.core.ClassNamesResourceConfig")
        servletHolder.setInitParameter("com.sun.jersey.config.property.classnames", "de.fh.zwickau.scriptsprachen.findme.client.rest.resources.HelloResource")

        ServletContextHandler servletContextHandler = new ServletContextHandler(webServer, "/", true, false)
        servletContextHandler.addServlet(servletHolder, "/hello/*")

        ServletHolder locatorHolder = new ServletHolder(ServletContainer.class)
        locatorHolder.setInitParameter("com.sun.jersey.config.property.resourceConfigClass", "com.sun.jersey.api.core.ClassNamesResourceConfig")
        locatorHolder.setInitParameter("com.sun.jersey.config.property.classnames", "de.fh.zwickau.scriptsprachen.findme.client.rest.resources.LocatorResource")
        servletContextHandler.addServlet(locatorHolder, "/locator/*")

        webServer.setHandler(servletContextHandler)

        try {
            webServer.start()
            Log.d(LOG_TAG, "Started Web server")
            webServer.join()
        }
        catch (Exception ex) {
            Log.d(LOG_TAG, "Unexpected exception while starting Web server: " + ex)
        }
    }

    @OnBackground
    public void stopServer() {
        try {
            webServer.stop()
            Log.d(LOG_TAG, "Stopped Web server")
        } catch (Exception ex) {
            Log.d(LOG_TAG, "Unexpected exception while stopping Web server: " + ex)
        }
    }

}