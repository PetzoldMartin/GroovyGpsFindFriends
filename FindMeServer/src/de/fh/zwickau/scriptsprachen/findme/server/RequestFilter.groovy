package de.fh.zwickau.scriptsprachen.findme.server

import java.io.IOException;
import java.util.logging.*
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.*
import javax.ws.rs.ext.Provider;

@Provider
@PreMatching

//http://www.developerscrappad.com/1814/java/java-ee/rest-jax-rs/java-ee-7-jax-rs-2-0-simple-rest-api-authentication-authorization-with-custom-http-header/
public class RequestFilter implements ContainerRequestFilter {

    private final static Logger log = Logger.getLogger( RequestFilter.class.getName() );

    @Override
    public void filter( ContainerRequestContext requestCtx ) throws IOException {

        String path = requestCtx.getUriInfo().getPath();
        if (!path.contains("admin/"))
		log.info( "Filtering request path: " + path );
		
		def authHash = requestCtx.getHeaderString(HttpHeaders.AUTHORIZATION)
		//log.info("Hash: " + authHash)
        // IMPORTANT!!! First, Acknowledge any pre-flight test from browsers for this case before validating the headers (CORS stuff)
        if ( requestCtx.getRequest().getMethod().equals( "OPTIONS" ) ) {
            requestCtx.abortWith( Response.status( Response.Status.OK ).build() );
            return;
        }
//
//        // For any pther methods besides login, the authToken must be verified
//        if ( !path.startsWith( "/demo-business-resource/login/" ) ) {
//            String authToken = requestCtx.getHeaderString( DemoHTTPHeaderNames.AUTH_TOKEN );
//
//            // if it isn't valid, just kick them out.
//            if ( !demoAuthenticator.isAuthTokenValid( serviceKey, authToken ) ) {
//                requestCtx.abortWith( Response.status( Response.Status.UNAUTHORIZED ).build() );
//            }
//        }
		//requestCtx.abortWith( Response.status( Response.Status.UNAUTHORIZED ).build() );
    }
}