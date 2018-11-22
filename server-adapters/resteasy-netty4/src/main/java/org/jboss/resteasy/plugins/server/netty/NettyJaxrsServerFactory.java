package org.jboss.resteasy.plugins.server.netty;

import org.jboss.resteasy.plugins.server.embedded.EmbeddedJaxrsServer;
import org.jboss.resteasy.plugins.server.embedded.EmbeddedJaxrsServerFactory;
import org.jboss.resteasy.plugins.server.embedded.SecurityDomain;
import org.jboss.resteasy.spi.ResteasyDeployment;

public class NettyJaxrsServerFactory implements EmbeddedJaxrsServerFactory
{
   public EmbeddedJaxrsServer newServer(ResteasyDeployment deployment, int port, SecurityDomain securityDomain, String rootResourcePath)
   {
      NettyJaxrsServer netty = new NettyJaxrsServer();
      netty.setDeployment(deployment);
      netty.setPort(port);
      netty.setRootResourcePath(rootResourcePath);
      netty.setSecurityDomain(securityDomain);
      return netty;
   }
}
