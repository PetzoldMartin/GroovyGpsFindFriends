package de.fh.zwickau.scriptsprachen.findme.server.resources

import javax.ws.rs.*
import javax.ws.rs.core.*

import org.glassfish.grizzly.http.server.Session

import de.fh.zwickau.scriptsprachen.findme.server.Globals;
import de.fh.zwickau.scriptsprachen.findme.server.HTML;

import org.glassfish.grizzly.http.server.Request
import org.glassfish.grizzly.http.server.Response

@Path('/admin')
class Admin implements HTML {
		
	private static final String ADMIN_USERNAME = "admin"
	private static final String ADMIN_PASSWORD = "admin"
	private static final String IS_LOGGED_IN = "isLoggedIn"
	
	@GET
	@Path('/login')
	@Produces("text/html")
	def login(@Context Request req, @QueryParam('error') String error) {
		StringBuilder b = new StringBuilder()
		b.append(HTML_LOGIN_BEGIN)
		if (error != null && !error.equals("")) {
			String errorMessage = "";
			if (error.equals("notLoggedIn"))
				errorMessage = "Login erforderlich"
			else if (error.equals("invalid"))
				errorMessage = "Ungueltige Zugangsdaten"
			b.append("<h4>" + errorMessage + "</h4>")
		}
		b.append(HTML_LOGIN_END)
		return b.toString()
	}
	
	@POST
	@Path('/performLogin')
	@Produces("text/html")
	def performLogin(@Context Request req, @FormParam('username') String username, @FormParam('password') String password) {
		Response res = req.getResponse()
		if (!ADMIN_USERNAME.equals(username) || !ADMIN_PASSWORD.equals(password)) {
			res.sendRedirect(Globals.SERVER_IP + "/admin/login?error=invalid")
		}
		else {
			Session session = req.getSession(true)
			Object isLoggedIn = session.getAttribute(IS_LOGGED_IN)
			if (isLoggedIn == null) {
				isLoggedIn = new Boolean(true)
				session.setAttribute(IS_LOGGED_IN, isLoggedIn)
			}
			res.sendRedirect(Globals.SERVER_IP + "/admin/showUsers")
		}
	}
		
	@GET
	@Path('/showUsers')
	@Produces("text/html")
	def showUsers(@Context Request req) {
		// Check session
		Session session = req.getSession(true)
		Object isLoggedIn = session.getAttribute(IS_LOGGED_IN)
		if (isLoggedIn == null || isLoggedIn == false) {
			Response res = req.getResponse()
			res.sendRedirect(Globals.SERVER_IP + "/admin/login?error=notLoggedIn")
			return
		}
		
		StringBuilder b = new StringBuilder()
		b.append(HTML_LIST_BEGIN)
		for (String email : Auth.eMailAddresses) {
			b.append("<tr>")
			b.append("<td>" + email + "</td>")
			b.append("<td>" + (Auth.isLoggedIn.get(email) == true ? "Online" : "Offline") + "</td>")
			b.append("</tr>")
		}
		b.append(HTML_LIST_END)
		return b.toString()
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
		return cssBuilder.toString()
	}
	
	@GET
	@Path('/logout')
	@Produces("text/html")
	def logout(@Context Request req) {
		Response res = req.getResponse()
		Session session = req.getSession(true)
		Object isLoggedIn = session.getAttribute(IS_LOGGED_IN)
		isLoggedIn = new Boolean(false)
		session.setAttribute(IS_LOGGED_IN, isLoggedIn)
		res.sendRedirect(Globals.SERVER_IP + "/admin/login")
	}

}
