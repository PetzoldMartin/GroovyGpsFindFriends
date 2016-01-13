package de.fh.zwickau.scriptsprachen.findme.client.rest.resources

import de.fh.zwickau.scriptsprachen.findme.client.friend.Friend
import de.fh.zwickau.scriptsprachen.findme.client.friend.FriendState
import de.fh.zwickau.scriptsprachen.findme.client.util.Core

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.QueryParam

@Path("/")
public class LocatorResource {

    @GET
    @Path("/getLocation")
    @Produces("text/plain")
    public String getLocation(@QueryParam('ownEmail') String email) {
        Friend f = Core.getConnector().getFriends(false).find { email.equals(it.email) }
        if (f != null && (f.state == FriendState.FRIEND || f.state == FriendState.ACCEPTED))
            return Core.getLocator().getLocation().toString()
        else
            return "Not a friend"
    }

}
