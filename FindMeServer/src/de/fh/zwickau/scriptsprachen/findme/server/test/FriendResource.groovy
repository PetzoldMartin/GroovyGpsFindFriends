package de.fh.zwickau.scriptsprachen.findme.server.test

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
        println "requestFriend called with parameters email = $email and name = $name"
    }

    @GET
    @Path("/accept")
    @Produces("text/plain")
    public String accept(@QueryParam('ownEmail') String  email, @QueryParam('ownName') String  name) {
        println "accept called with parameters email = $email and name = $name"
    }

    @GET
    @Path("/deny")
    @Produces("text/plain")
    public String deny(@QueryParam('ownEmail') String  email) {
        println "deny called with parameters email = $email"
    }

    @GET
    @Path("/remove")
    @Produces("text/plain")
    public String remove(@QueryParam('ownEmail') String  email) {
        println "remove called with parameters email = $email"
    }

}
