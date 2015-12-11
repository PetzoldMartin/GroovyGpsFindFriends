package de.fh.zwickau.scriptsprachen.findme.server.test

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces

@Path("/locator")
public class LocatorResource {

    @GET
    @Path("/getLocation")
    @Produces("text/plain")
    public String getLocation() {
		def rand = new Random()
		def x_rand = rand.nextInt(99)
		def y_rand = rand.nextInt(99)
        return "50.$x_rand 70.$y_rand"
    }

}
