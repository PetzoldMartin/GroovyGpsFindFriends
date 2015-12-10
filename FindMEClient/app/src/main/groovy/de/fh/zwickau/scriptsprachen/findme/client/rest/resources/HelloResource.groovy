package de.fh.zwickau.scriptsprachen.findme.client.rest.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces

@Path("/")
public class HelloResource {

    @GET
    @Path("/test")
    @Produces("text/plain")
    public String get() {
        return "Works";
    }

}
