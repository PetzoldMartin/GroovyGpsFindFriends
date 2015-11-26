package de.fh.zwickau.scriptsprachen.findme.server

import javax.ws.rs.*
import javax.ws.rs.core.*

import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory
import org.glassfish.jersey.server.ResourceConfig

GrizzlyHttpServerFactory.createHttpServer("http://localhost:8080".toURI(), new ResourceConfig(Auth.class))

@Path('/')
class Auth {
	
	private static final String SECRET = "geheim"
	
	static HashSet<String> eMailAddresses = new HashSet<>()
	static HashMap<String, String> names = new HashMap<>()
	static HashMap<String, Boolean> isLoggedIn = new HashMap<>()
	
	@GET
	@Path('/auth/register')
	def register(@QueryParam('email') String email, @QueryParam('name') String name, @QueryParam('secret') String secret) {
		if (!SECRET.equals(secret))
			return "Invalid secret"
		else {
			if (eMailAddresses.contains(email))
				return "E-Mail already taken"
			else {
				eMailAddresses.add(email)
				names.put(email, name)
				isLoggedIn.put(email, false)
				return "Register successful"
			}
		}
	}
	
	@GET
	@Path('/auth/login')
	def login(@QueryParam('email') String email) {
		if (!eMailAddresses.contains(email))
			return "E-Mail is unknown"
		else {
			isLoggedIn.put(email, true)
			return "Login successful"
		}
	}
	
	@GET
	@Path('/auth/logout')
	def logout(@QueryParam('email') String email) {
		if (!eMailAddresses.contains(email))
			return "E-Mail is unknown"
		else {
			isLoggedIn.put(email, false)
			return "Logout successful"
		}
	}

}
