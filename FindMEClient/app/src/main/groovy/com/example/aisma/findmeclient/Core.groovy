package com.example.aisma.findmeclient

final class Core {

    public static final String SERVER_IP = "http://10.0.2.2:8080"

    private static IConnector connector = null

    private Core() {

    }

    public static synchronized IConnector getConnector() {
        if (connector == null)
            connector = Connector.getInstance()
        return connector
    }

}