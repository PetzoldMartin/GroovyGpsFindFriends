package resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/")
public class Rest {

    @GET
    @Produces("text/plain")
    public String get() {
        return "Works";
    }

}
