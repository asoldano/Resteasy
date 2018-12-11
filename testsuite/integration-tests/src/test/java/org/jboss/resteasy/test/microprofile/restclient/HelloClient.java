package org.jboss.resteasy.test.microprofile.restclient;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

@Path("/")
public interface HelloClient {

    @GET
    @Path("/hello")
    String hello();

    @GET
    @Path("/call")
    String call(@QueryParam("url") String url) throws Exception;
}