package de.fh.zwickau.scriptsprachen.findme.client.rest

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.util.Log
import com.arasthel.swissknife.annotations.OnBackground
import com.sun.jersey.spi.container.servlet.ServletContainer
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.ServletContextHandler
import org.eclipse.jetty.servlet.ServletHolder

public class RESTServer extends BroadcastReceiver {

    private final static String LOG_TAG = "Jetty"

    private final static int port = 8080
    private final static String localhost = "localhost"
    private Server webServer = null
    private static Activity context

    def static getServerAdress() {
        try {
            ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)
            NetworkInfo netInfo = conMan.getActiveNetworkInfo()
            String ipString = localhost;
            if (netInfo != null && netInfo.getType() == ConnectivityManager.TYPE_WIFI) { //WIFI
                Log.i(LOG_TAG, "Wifi connected");
                WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
                int ip = wifiInfo.getIpAddress(); //int
                ipString = String.format(
                        "%d.%d.%d.%d",
                        (ip & 0xff),
                        (ip >> 8 & 0xff),
                        (ip >> 16 & 0xff),
                        (ip >> 24 & 0xff))

            } else if (netInfo != null && netInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                Log.i(LOG_TAG, "Mobile connected");
                for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                    NetworkInterface intf = (NetworkInterface) en.nextElement();
                    for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                        InetAddress inetAddress = enumIpAddr.nextElement();
                        if (!inetAddress.isLoopbackAddress()) {
                            ipString = inetAddress.getHostAddress() //string
                        }
                    }
                }
            }
            Log.i(LOG_TAG, "Network Jetty IP: " + ipString)
            return new java.net.InetSocketAddress(ipString, port)

        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage())
        }
        Log.i(LOG_TAG, "cant get network ip --> fallback bind Jetty to localhost")
        return new java.net.InetSocketAddress(localhost, port) // fallback return
    }

    @OnBackground
    public void startServer(Activity activity) {
        this.context = activity
        java.net.InetSocketAddress addresse = getServerAdress()

        System.setProperty("java.net.preferIPv4Stack", "true")
        System.setProperty("java.net.preferIPv6Addresses", "false")

        Log.i(LOG_TAG + " IP config", "Jetty IP set to: " + addresse.toString())
        if (webServer == null) {
            webServer = new Server(addresse)
        }

        ServletContextHandler servletContextHandler = new ServletContextHandler(webServer, "/", true, false)

        ServletHolder locatorHolder = new ServletHolder(ServletContainer.class)
        locatorHolder.setInitParameter("com.sun.jersey.config.property.resourceConfigClass", "com.sun.jersey.api.core.ClassNamesResourceConfig")
        locatorHolder.setInitParameter("com.sun.jersey.config.property.classnames", "de.fh.zwickau.scriptsprachen.findme.client.rest.resources.LocatorResource")
        servletContextHandler.addServlet(locatorHolder, "/locator/*")

        ServletHolder friendHolder = new ServletHolder(ServletContainer.class)
        friendHolder.setInitParameter("com.sun.jersey.config.property.resourceConfigClass", "com.sun.jersey.api.core.ClassNamesResourceConfig")
        friendHolder.setInitParameter("com.sun.jersey.config.property.classnames", "de.fh.zwickau.scriptsprachen.findme.client.rest.resources.FriendResource")
        servletContextHandler.addServlet(friendHolder, "/friend/*")

        webServer.setHandler(servletContextHandler)

        try {
            webServer.start()
            Log.i(LOG_TAG, "Started Web server")
            webServer.join()
        }
        catch (Exception ex) {
            Log.i(LOG_TAG, "Unexpected exception while starting Web server: " + ex)
        }
    }

    @OnBackground
    public void stopServer() {
        try {
            Log.i(LOG_TAG, "Stopped Web server")
            if (webServer != null) {
                webServer.stop()
                webServer = null
            }
        } catch (Exception ex) {
            Log.i(LOG_TAG, "Unexpected exception while stopping Web server: " + ex)
        }
    }

    @Override
    void onReceive(Context context, Intent intent) {
        Log.i(LOG_TAG, "Connectivity changed")
        //TODO addconnector remove connector
        // stopServer()
        //TODO check intent connectivity changed
        // startServer(this.context)
    }
}
