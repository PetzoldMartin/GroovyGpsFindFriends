package resources

import com.example.aisma.findmeclient.Core;

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
