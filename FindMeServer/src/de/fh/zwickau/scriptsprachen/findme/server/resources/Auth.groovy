package de.fh.zwickau.scriptsprachen.findme.server.resources

import javax.jws.Oneway
import javax.servlet.ServletException;
import javax.ws.rs.*

import javax.ws.rs.core.*
import groovy.json.*

@Path('/auth')
class Auth {

	private static final String SECRET = "geheim"

	private static final String DATA = "resources/UserData.json"

	static HashSet<String> eMailAddresses = []
	static HashMap<String, Boolean> isLoggedIn = [:]
	static HashMap<String, String> names = load()



	static def load() {
		new File(DATA).withInputStream  { is ->
			names = new JsonSlurper().parse(is)
			eMailAddresses= names.keySet()
			eMailAddresses.each {it -> isLoggedIn[it]=false}
		}
		return names
	}
	static void save () {
		new File(DATA).withOutputStream { out -> out << new JsonBuilder(names).toString()}
		JsonBuilder builder = new JsonBuilder(names)
	}

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
				save()
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
