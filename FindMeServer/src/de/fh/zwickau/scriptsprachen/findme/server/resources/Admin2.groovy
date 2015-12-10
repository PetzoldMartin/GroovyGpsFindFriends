package de.fh.zwickau.scriptsprachen.findme.server.resources


import groovy.xml.MarkupBuilder

import java.util.HashMap;
import java.util.HashSet

import javax.ws.rs.*
import javax.ws.rs.core.*
import javax.ws.rs.core.Response.ResponseBuilder;

import org.glassfish.grizzly.http.HttpResponsePacket;


@Path('/admin2')
class Admin2 {


	def username="admin"
	def password="pw"
	def baseHash = "$username:$password".bytes.encodeBase64().toString()

	@Context HttpHeaders httpHeaders

	@GET
	@Path('/logout')
	@Produces("text/html")
	def logout() {
		def writer = new StringWriter()
		def builder = new groovy.xml.MarkupBuilder(writer)
		builder.html () {
			head { title "LogoutScreen" }
			body {
				h1 "LogoutScreen"
				
				a('href': 'getlist') { button("Adminlist") }
			}
		}
		return writer.toString()
	}

	@GET
	@Path('/getlist')
	@Produces("text/html")
	def getList(){
		if (httpHeaders.getHeaderString(HttpHeaders.AUTHORIZATION)!=null) {
			if (httpHeaders.getHeaderString(HttpHeaders.AUTHORIZATION).split(" ")[1].equalsIgnoreCase("$baseHash")){

				def writer = new StringWriter()
				def builder = new groovy.xml.MarkupBuilder(writer)
				builder.html () {
					head { title "User list" }
					body {
						h1 "Registrierte User"
						table (border:'1') {
							tr {
								th "user email"
								th "name"
								th "Ip"
								th "Online"
							}
							for (user in Auth.eMailAddresses.sort()) {
								tr {
									td user
									td Auth.names[user]
									td Mediator.ipMap[user]
									td Auth.isLoggedIn[user]
								}
							}
						}
						a('href': 'logout') { button("logout") }
					}
				}
				return writer.toString()
			}
		}
		Response.status( Response.Status.UNAUTHORIZED ).header(HttpHeaders.WWW_AUTHENTICATE, "Basic realm=\"Private\"").build()
	}
}
