package de.fh.zwickau.scriptsprachen.findme.server

interface HTML {
	
	public static final String HTML_LOGIN_BEGIN =
		"<html>" +
		"<head>" +
		"<link rel=\"stylesheet\" href=\"" + (Globals.SERVER_IP + "/admin/style.css") + "\">" +
		"</head>" +
		"<body>" +
		"<h1>Login</h1>";
	public static final String HTML_LOGIN_END =
		"<form action=\"" + (Globals.SERVER_IP + "/admin/performLogin") + "\" method=\"post\">" +
		"<table>" +
		"<tr>" +
		"<td><label for=\"username\">Nutzername</label></td>" +
		"<td><input type=\"text\" name=\"username\" maxlength=\"30\"></td>" +
		"</tr>" +
		"<tr>" +
		"<td><label for=\"password\">Passwort</label></td>" +
		"<td><input type=\"password\" name=\"password\" maxlength=\"40\"></td>" +
		"</tr>" +
		"</table>" +
		"<br/>" +
		"<button type=\"submit\">Login</button>" +
		"</form>" +
		"</body>" +
		"</html>";
	
	public static final String HTML_LIST_BEGIN =
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
	public static final String HTML_LIST_END =
		"</table>" +
		"<br/>" +
		"<form action=\"" + (Globals.SERVER_IP + "/admin/logout") + "\" method=\"get\">" +
		"<button type=\"submit\">Logout</button>" +
		"</form>" +
		"</body>" +
		"</html>";

}
