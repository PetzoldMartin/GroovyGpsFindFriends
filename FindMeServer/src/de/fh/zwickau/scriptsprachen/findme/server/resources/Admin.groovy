package de.fh.zwickau.scriptsprachen.findme.server.resources

import javax.ws.rs.*
import javax.ws.rs.core.*

import groovy.xml.MarkupBuilder

import org.glassfish.grizzly.http.server.Session

import org.glassfish.grizzly.http.server.Request
import org.glassfish.grizzly.http.server.Response

@Path('/admin')
class Admin {

	private static final String ADMIN_USERNAME = "admin"
	private static final String ADMIN_PASSWORD = "admin"
	private static final String IS_LOGGED_IN = "isLoggedIn"

	@Context Request req

	def getServerIP() {
		"http://${req.getServerName()}:${req.getServerPort()}"
	}


	@GET
	@Path('/login')
	@Produces("text/html")
	def login(@QueryParam('error') String error) {
		def writer = new StringWriter()
		def builder = new groovy.xml.MarkupBuilder(writer)
		builder.html () {
			head {
				title "Login"
				link( rel:"stylesheet",	type:"text/css", href:"${getServerIP()}/admin/style.css")
			}
			body {
				h1 "Login"
				if (error != null && !error.equals("")) {
					String errorMessage = "";
					if (error.equals("notLoggedIn"))
						errorMessage = "Login erforderlich"
					else if (error.equals("invalid"))
						errorMessage = "Ungueltige Zugangsdaten"
					h4 (errorMessage)
				}

				form ('action': "${getServerIP()}/admin/performLogin", 'method':"post") {
					table {
						tr {
							td {
								label ('for':"username", "Nutzername")
							}
							td {
								input ('type':"text", 'name':"username" ,'maxlength': 30)
							}
						}
						tr {
							td {
								label ('for':"password", "Passwort")
							}
							td {
								input ('type':"password", 'name':"password" ,'maxlength': 40)
							}
						}
					}
					br {
						button ('type': "submit", "Login")
					}
				}
			}
		}
		return writer.toString()
	}

	@POST
	@Path('/performLogin')
	@Produces("text/html")
	def performLogin(@FormParam('username') String username, @FormParam('password') String password) {
		Response res = req.getResponse()
		if (!ADMIN_USERNAME.equals(username) || !ADMIN_PASSWORD.equals(password)) {
			res.sendRedirect("${getServerIP()}/admin/login?error=invalid")
		}
		else {
			Session session = req.getSession(true)
			Object isLoggedIn = session.getAttribute(IS_LOGGED_IN)
			isLoggedIn = new Boolean(true)
			session.setAttribute(IS_LOGGED_IN, isLoggedIn)
			res.sendRedirect("${getServerIP()}/admin/showUsers")
		}
	}

	@GET
	@Path('/showUsers')
	@Produces("text/html")
	def showUsers() {
		// Check session
		Session session = req.getSession(true)
		Object isLoggedIn = session.getAttribute(IS_LOGGED_IN)
		if (isLoggedIn == null || isLoggedIn == false) {
			Response res = req.getResponse()
			res.sendRedirect("${getServerIP()}/admin/login?error=notLoggedIn")
		}

		def writer = new StringWriter()
		def builder = new groovy.xml.MarkupBuilder(writer)
		builder.html () {
			head {
				title "User list"
				link( rel:"stylesheet",	type:"text/css", href:"${getServerIP()}/admin/style.css")
			}
			body {
				h1 "Registrierte Nutzer"
				table (border:'1') {
					tr {
						th "E-Mail Adresse"
						th "Name"
						th "IP-Adresse"
						th "Online-Status"
					}
					for (user in Auth.eMailAddresses.sort()) {
						tr {
							td user
							td Auth.names[user]
							td Mediator.ipMap[user]
							td {
								Auth.isLoggedIn[user] ? (font (color: "green", "Online")) : (font (color: "red", "Offline" ))
							}
						}
					}
				}
				br {
					form ('action': "${getServerIP()}/admin/logout", 'method':"get") {
						button ('type': "submit", "Logout")
					}
				}
			}
		}
		return writer.toString()
	}

	@GET
	@Path('/style.css')
	@Produces("text/css")
	def getCss() {
		File f = new File("resources/style.css")
		FileInputStream fis = new FileInputStream(f)
		List<String> lines = fis.readLines()
		StringBuilder cssBuilder = new StringBuilder()
		lines.each {s -> cssBuilder.append(s)}
		cssBuilder.toString()
	}

	@GET
	@Path('/logout')
	@Produces("text/html")
	def logout() {
		Response res = req.getResponse()
		Session session = req.getSession(true)
		Object isLoggedIn = session.getAttribute(IS_LOGGED_IN)
		isLoggedIn = new Boolean(false)
		session.setAttribute(IS_LOGGED_IN, isLoggedIn)
		res.sendRedirect("${getServerIP()}/admin/login")
	}
}
