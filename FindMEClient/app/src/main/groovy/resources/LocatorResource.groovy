package resources;

import com.example.aisma.findmeclient.ClientLocator
import com.example.aisma.findmeclient.MainActivity
import com.example.aisma.findmeclient.Vector

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/")
public class LocatorResource {

    ClientLocator locator;

    public LocatorResource() {
        this.locator = MainActivity.getLocator()
    }

    @GET
    @Path("/getLocation")
    @Produces("text/plain")
    public String getLocation() {
        return locator.getLocation().toString()
    }

}
