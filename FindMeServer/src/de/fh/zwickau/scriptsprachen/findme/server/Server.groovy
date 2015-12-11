package de.fh.zwickau.scriptsprachen.findme.server

import org.glassfish.grizzly.http.server.HttpServer
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory
import org.glassfish.jersey.server.ResourceConfig
import org.glassfish.grizzly.http.server.CLStaticHttpHandler

import de.fh.zwickau.scriptsprachen.findme.server.resources.*
import de.fh.zwickau.scriptsprachen.findme.server.test.LocatorResource

class Globals {
	public static final String SERVER_IP = "http://localhost:8080"
}

final ResourceConfig rc = new ResourceConfig();
rc.register(Auth.class)
rc.register(Mediator.class)
rc.register(Admin.class)
rc.register(LocatorResource.class)
rc.register(Admin2.class)
rc.register(RequestFilter.class)
final HttpServer server = GrizzlyHttpServerFactory.createHttpServer(Globals.SERVER_IP.toURI(), rc)
//server.getServerConfiguration().addHttpHandler(new CLStaticHttpHandler(...))

String email="testemail"
String name="testname"
Auth.eMailAddresses.add(email)
Auth.names.put(email, name)
Auth.isLoggedIn.put(email, true)
Mediator.ipMap.put(email, "10.0.2.2:8080")

email="testemail2"
name="testname2"
Auth.eMailAddresses.add(email)
Auth.names.put(email, name)
Auth.isLoggedIn.put(email, true)
Mediator.ipMap.put(email, "10.0.2.2:8080")

//email="onlinemail"
//name="online"
//Auth.eMailAddresses.add(email)
//Auth.names.put(email, name)
//Auth.isLoggedIn.put(email, true)