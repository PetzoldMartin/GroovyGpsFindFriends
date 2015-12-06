package de.fh.zwickau.scriptsprachen.findme.server

import javax.ws.rs.*
import javax.ws.rs.core.*

@Path('/auth')
class Auth {
	
	private static final String SECRET = "geheim"
	
	static HashSet<String> eMailAddresses = []
	static HashMap<String, String> names = [:]
	static HashMap<String, Boolean> isLoggedIn = [:]
	
	@GET
	@Path('/register')
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
	@Path('/login')
	def login(@Context org.glassfish.grizzly.http.server.Request req, @QueryParam('email') String email) {
		Mediator.checkIP(req, email)
		if (!eMailAddresses.contains(email))
			return "E-Mail is unknown"
		else {
			isLoggedIn.put(email, true)
			return "Login successful"
		}
	}
	
	@GET
	@Path('/logout')
	def logout(@QueryParam('email') String email) {
		if (!eMailAddresses.contains(email))
			return "E-Mail is unknown"
		else {
			isLoggedIn.put(email, false)
			return "Logout successful"
		}
	}

}
