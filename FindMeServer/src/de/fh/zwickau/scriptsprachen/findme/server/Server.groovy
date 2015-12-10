package de.fh.zwickau.scriptsprachen.findme.server

import org.glassfish.grizzly.http.server.HttpServer
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory
import org.glassfish.jersey.server.ResourceConfig

import de.fh.zwickau.scriptsprachen.findme.server.resource.*

class Globals {
	public static final String SERVER_IP = "http://localhost:8080"
}

final ResourceConfig rc = new ResourceConfig();
rc.register(Auth.class)
rc.register(Mediator.class)
rc.register(Admin.class)
rc.register(RequestFilter.class)

final HttpServer server =GrizzlyHttpServerFactory.createHttpServer(Globals.SERVER_IP.toURI(), rc)

String email="testemail"
String name="testname"
Auth.eMailAddresses.add(email)
Auth.names.put(email, name)
Auth.isLoggedIn.put(email, true)

email="onlinemail"
name="online"
Auth.eMailAddresses.add(email)
Auth.names.put(email, name)
Auth.isLoggedIn.put(email, true)