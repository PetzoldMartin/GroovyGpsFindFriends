package de.fh.zwickau.scriptsprachen.findme.server.resources

import java.util.HashSet;

import javax.ws.rs.*
import javax.ws.rs.core.*

import de.fh.zwickau.scriptsprachen.findme.server.resources.Auth

@Path('/medi')
class Mediator {

	static HashMap<String,String> ipMap = [:]

	@GET
	@Path('/getIP')
	def getIP(@Context org.glassfish.grizzly.http.server.Request req,@QueryParam('targetEmail') String targetEmail, @QueryParam('email') String email) {
		checkIP(req, email)
		if(Auth.isLoggedIn.get(targetEmail)) {
			return ipMap.get(targetEmail).toString()
		} else {
			return targetEmail + " is not online"
		}
	}

	@GET
	@Path('/getOnlineUsers')
	def getOnlineUsers(@Context org.glassfish.grizzly.http.server.Request req,@QueryParam('email') String email) {
		checkIP(req, email)
		if(Auth.isLoggedIn.get(email)) {
			Set<String> emails = Auth.isLoggedIn.findAll{key, value -> value == true}.keySet().findAll{it != email}
			StringBuilder b = new StringBuilder("[")
			emails.each {b.append(Auth.names[it] + ":" + it + ",")}
			b.replace(b.length() - 1, b.length(), "]")
			return b.toString()
		} else {
			return "Cannot Respose you are not logged in"
		}
	}

	static def checkIP(org.glassfish.grizzly.http.server.Request req, String email) {
		if (email!=null) {
			String remoteAddr = req.getRemoteAddr();
			int remotePort = req.getRemotePort();
			ipMap[email] = remoteAddr + ":" + remotePort
		} else {
			println "No email id used for " + req.getRemoteAddr()
		}
	}
	
}
