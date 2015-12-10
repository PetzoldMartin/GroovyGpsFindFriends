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
        return "50.00 70.00"
    }

}
