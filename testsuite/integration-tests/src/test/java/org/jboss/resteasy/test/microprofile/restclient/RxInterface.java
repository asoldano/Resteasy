package org.jboss.resteasy.test.microprofile.restclient;

import java.util.concurrent.CompletionStage;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import io.reactivex.Single;

@Path("/")
public interface RxInterface
{
   @GET
   @Path("normal")
   Single<String> normalSingle();

   @GET
   @Path("normal")
   String normalString();

   @GET
   @Path("normal")
   CompletionStage<String> normalCs();
}
