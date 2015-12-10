package de.fh.zwickau.scriptsprachen.findme.client.rest.resources

import de.fh.zwickau.scriptsprachen.findme.client.util.Core;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/")
public class LocatorResource {

    @GET
    @Path("/getLocation")
    @Produces("text/plain")
    public String getLocation() {
        return Core.getLocator().getLocation().toString()
    }

}
