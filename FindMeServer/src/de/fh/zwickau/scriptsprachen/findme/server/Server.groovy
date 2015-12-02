package de.fh.zwickau.scriptsprachen.findme.server

import org.glassfish.grizzly.http.server.HttpServer
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory
import org.glassfish.jersey.server.ResourceConfig

import de.fh.zwickau.scriptsprachen.findme.server.resource.*;
final ResourceConfig rc = new ResourceConfig();

rc.register(Auth.class)
rc.register(Mediator.class)
final HttpServer server =GrizzlyHttpServerFactory.createHttpServer("http://141.32.20.48:8080".toURI(), rc)

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