package de.fh.zwickau.scriptsprachen.findme.client.rest.resources

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.QueryParam

@Path("/")
public class FriendResource {

    @GET
    @Path("/requestFriend")
    @Produces("text/plain")
    public String requestFriend(@QueryParam('ownEmail') email, @QueryParam('ownName') name) {

    }

    @GET
    @Path("/accept")
    @Produces("text/plain")
    public String accept(@QueryParam('ownEmail') email, @QueryParam('ownName') name) {

    }

    @GET
    @Path("/deny")
    @Produces("text/plain")
    public String deny(@QueryParam('ownEmail') email) {

    }

    @GET
    @Path("/remove")
    @Produces("text/plain")
    public String remove(@QueryParam('ownEmail') email) {

    }

}
