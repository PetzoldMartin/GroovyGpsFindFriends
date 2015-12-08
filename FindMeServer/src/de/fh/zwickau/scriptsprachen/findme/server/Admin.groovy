package de.fh.zwickau.scriptsprachen.findme.server

import javax.ws.rs.*
import javax.ws.rs.core.*

import javax.servlet.http.*

@Path('/admin')
class Admin {
	
	private static final String HTML_LIST_BEGIN =
		"<html>" +
		"<head>" +
		"<link rel=\"stylesheet\" href=\"" + (Globals.SERVER_IP + "/admin/style.css") + "\">" +
		"</head>" +
		"<body>" +
		"<h1>FindMe-Nutzerliste</h1>" +
		"<table>" +
		"<tr>" +
		"<th>E-Mail-Adresse</th>" +
		"<th>Status</th>" +
		"</tr>";
	private static final String HTML_LIST_END =
		"</table>" +
		"</body>" +
		"</html>";
	
	@GET
	@Path('/login')
	@Produces("text/plain")
	def login(@Context HttpServletRequest req) {
		HttpSession session = req.getSession(true)
		Object isLoggedIn = session.getAttribute("isLoggedIn")
		if (isLoggedIn == null) {
			isLoggedIn = new Boolean(true)
			session.setAttribute("isLoggedIn", isLoggedIn)
		}
		return "Test"
	}
		
	@GET
	@Path('/showUsers')
	@Produces("text/html")
	def showUsers() {
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

}
