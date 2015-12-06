package de.fh.zwickau.scriptsprachen.findme.server

import java.util.HashSet;

import javax.ws.rs.*
import javax.ws.rs.core.*


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
			//println Auth.isLoggedIn.findAll{key, value -> value==true}.keySet().findAll{it!=email}.toString()
			return  Auth.isLoggedIn.findAll{key, value -> value==true}.keySet().findAll{it!=email}.toString()
		} else {
			return "Cannot Respose you are not logged in"
		}
	}

	static def checkIP(org.glassfish.grizzly.http.server.Request req, String email) {
		if (email!=null) {
			String remoteAddr = req.getRemoteAddr();
				int remotePort = req.getRemotePort();
			ipMap[email]=remoteAddr + ":" + remotePort
		} else {
			println "No email id used for $req.getRemoteAddr()"
		}
	}
}
