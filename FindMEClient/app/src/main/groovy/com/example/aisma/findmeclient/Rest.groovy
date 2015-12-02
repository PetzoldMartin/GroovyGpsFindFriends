package com.example.aisma.findmeclient

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Path("/test")
public class Rest {

    @GET
    @Path("/works")
    @Produces(MediaType.TEXT_PLAIN)
    public String get() {
        return "Works";
    }

}
