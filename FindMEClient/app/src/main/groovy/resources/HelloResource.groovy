package resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/")
public class HelloResource {

    @GET
    @Path("/test")
    @Produces("text/plain")
    public String get() {
        return "Works";
    }

}