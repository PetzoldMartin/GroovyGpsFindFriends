package de.fh.zwickau.scriptsprachen.findme.server

import org.glassfish.grizzly.http.server.HttpServer
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory
import org.glassfish.jersey.server.ResourceConfig
import org.glassfish.grizzly.http.server.CLStaticHttpHandler

import de.fh.zwickau.scriptsprachen.findme.server.resources.*
import de.fh.zwickau.scriptsprachen.findme.server.test.LocatorResource
import org.glassfish.grizzly.http.server.NetworkListener

final ResourceConfig rc = new ResourceConfig();
rc.register(Auth.class)
rc.register(Mediator.class)
rc.register(Admin.class)
rc.register(LocatorResource.class)
rc.register(RequestFilter.class)

HttpServer server = null

// server binding
Enumeration e = NetworkInterface.getNetworkInterfaces();
while(e.hasMoreElements()) {
	NetworkInterface n = (NetworkInterface) e.nextElement();
	Enumeration ee = n.getInetAddresses();
	while (ee.hasMoreElements()) {
		InetAddress i = (InetAddress) ee.nextElement();
		
		if(!(i.getHostAddress() =~ /:/ || i.getHostAddress().startsWith("0."))  ) { 
			if (server == null)
				server = GrizzlyHttpServerFactory.createHttpServer(("http://" + i.getHostAddress() + ":" + 8080).toURI(), rc)
			else {
				final NetworkListener listener = new NetworkListener("grizzly", i.getHostAddress(), 8080)
				server.addListener(listener)
			}
		}
	}
}

//final HttpServer server = GrizzlyHttpServerFactory.createHttpServer(Globals.SERVER_IP.toURI(), rc)
//server.getServerConfiguration().addHttpHandler(new CLStaticHttpHandler(...))

// testdata
String email="testemail"
String name="testname"
Auth.eMailAddresses.add(email)
Auth.names.put(email, name)
Auth.isLoggedIn.put(email, true)
Mediator.ipMap.put(email, "10.0.2.2:8080")
//Mediator.ipMap.put(email, "141.32.21.150:8080")

email="testemail2"
name="testname2"
Auth.eMailAddresses.add(email)
Auth.names.put(email, name)
Auth.isLoggedIn.put(email, true)
Mediator.ipMap.put(email, "10.0.2.2:8080")
//Mediator.ipMap.put(email, "141.32.21.150:8080")