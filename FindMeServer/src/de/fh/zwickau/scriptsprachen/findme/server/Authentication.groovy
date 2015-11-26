package de.fh.zwickau.scriptsprachen.findme.server

import javax.ws.rs.*
import javax.ws.rs.core.*

import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory
import org.glassfish.jersey.server.ResourceConfig

GrizzlyHttpServerFactory.createHttpServer("http://localhost:8080".toURI(), new ResourceConfig(Auth.class))

@Path('/')
class Auth {
	
	static ArrayList<String> usernames = new ArrayList<>()
	static HashMap<Integer, Boolean> isLoggedIn = new HashMap<>()
	
	@GET
	@Path('/register')
	def get(@QueryParam('username') String username, @QueryParam('secret') String secret) {
		if (!"geheim".equals(secret))
			return "INVALID SECRET"
		else {
			if (usernames.contains(username))
				return "Username already taken"
			else {
				usernames.add(username)
				isLoggedIn.put(usernames.size, false)
				return usernames.size
			}
		}
	}
	
	@GET
	@Path('/login')
	def login(@QueryParam('id') int id) {
		if (id < 0 || id >= usernames.size)
			return "Invalid ID"
		else {
			isLoggedIn.put(id, true)
			return "Login successful"
		}
	}
	
	@GET
	@Path('/logout')
	def logout(@QueryParam('id') int id) {
		if (id < 0 || id >= usernames.size)
			return "Invalid ID"
		else {
			isLoggedIn.put(id, false)
			return "Logout successful"
		}
	}

}
