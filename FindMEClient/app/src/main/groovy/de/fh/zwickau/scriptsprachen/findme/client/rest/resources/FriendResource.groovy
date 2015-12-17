package de.fh.zwickau.scriptsprachen.findme.client.rest.resources

import de.fh.zwickau.scriptsprachen.findme.client.friend.Friend
import de.fh.zwickau.scriptsprachen.findme.client.friend.FriendState
import de.fh.zwickau.scriptsprachen.findme.client.util.Core

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.QueryParam

@Path("/")
public class FriendResource {

    @GET
    @Path("/requestFriend")
    @Produces("text/plain")
    public String requestFriend(@QueryParam('ownEmail') String email, @QueryParam('ownName') String  name) {
        List<Friend> friends = Core.getConnector().getFriends(false)
        Friend f = friends.find{email.equals(it.email)}
        if (f != null)
            // Friend already known (if the request was denied or something, we will send the request again on refresh, not here)
            return "Already requested"
        else {
            f = new Friend()
            f.name = name
            f.email = email
            f.state = FriendState.REQUESTED
            Core.getConnector().updateFriend(f)
            return "Okay"
        }
    }

    @GET
    @Path("/accept")
    @Produces("text/plain")
    public String accept(@QueryParam('ownEmail') String  email, @QueryParam('ownName') String  name) {
        List<Friend> friends = Core.getConnector().getFriends(false)
        Friend f = friends.find{email.equals(it.email)}
        if (f == null)
            return "No request was sent"
        else {
            if (f.state == FriendState.REQUESTED) {
                // E-Mail is already set at this point
                f.name = name
                f.state = FriendState.FRIEND
                Core.getConnector().updateFriend(f)
                return "Okay"
            }
            else
                return "Already accepted"
        }
    }

    @GET
    @Path("/deny")
    @Produces("text/plain")
    public String deny(@QueryParam('ownEmail') String  email) {
        List<Friend> friends = Core.getConnector().getFriends(false)
        Friend f = friends.find{email.equals(it.email)}
        if (f == null)
            return "No request was sent"
        else {
            if (f.state == FriendState.REQUESTED) {
                Core.getConnector().removeFriend(f)
                return "Okay"
            }
            else
                return "Already accepted" // Remove is the method to call instead
        }
    }

    @GET
    @Path("/remove")
    @Produces("text/plain")
    public String remove(@QueryParam('ownEmail') String  email) {
        List<Friend> friends = Core.getConnector().getFriends(false)
        Friend f = friends.find{email.equals(it.email)}
        if (f == null)
            return "Friend not known"
        else {
            Core.getConnector().removeFriend(f)
            return "Okay"
        }
    }

}
