package org.jboss.resteasy.client.jaxrs;

import java.net.URI;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.UriBuilder;

public interface ResteasyClient extends Client
{
   @Override
   public ResteasyWebTarget target(URI uri);
   
   @Override
   public ResteasyWebTarget target(String uri);
   
   @Override
   public ResteasyWebTarget target(UriBuilder uriBuilder);

   @Override
   public ResteasyWebTarget target(Link link);
   
   public ClientHttpEngine httpEngine();

   public ExecutorService asyncInvocationExecutor();

   public ScheduledExecutorService getScheduledExecutor();

   public void abortIfClosed();

   public boolean isClosed();

}
