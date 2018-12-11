package org.jboss.resteasy.test.microprofile.restclient;

import java.net.URL;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.eclipse.microprofile.rest.client.RestClientBuilder;

@Path("/")
public class HelloResource {

    @GET
    @Produces("text/plain")
    @Path("/hello")
    public String hello() {
       return "Hello";
    }

    @GET
    @Produces("text/plain")
    @Path("/call")
    public String call(@QueryParam("url") String url) throws Exception {
       RestClientBuilder builder = RestClientBuilder.newBuilder();
       HelloClient restClient = builder.baseUrl(new URL(url)).build(HelloClient.class);
       String response = restClient.hello();
       return response +  "2";
   }

}