package org.jboss.resteasy.test.microprofile.restclient;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/")
public class Resource
{
   @GET
   @Path("normal")
   public String normal()
   {
      return "resteasy";
   }
}
