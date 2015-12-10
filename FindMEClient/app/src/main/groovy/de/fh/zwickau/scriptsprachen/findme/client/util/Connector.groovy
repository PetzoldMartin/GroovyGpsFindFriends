package de.fh.zwickau.scriptsprachen.findme.client.util;

class Connector implements IConnector {

    private static Connector instance;

    private Connector() {

    }

    public static synchronized Connector getInstance() {
        if (instance == null)
            instance = new Connector()
        return instance
    }

    @Override
    List<Friend> getFriends() {
        return null
    }

}