package org.jboss.resteasy.plugins.server.embedded;

import org.jboss.resteasy.spi.ResteasyDeployment;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface EmbeddedJaxrsServerFactory
{
   EmbeddedJaxrsServer newServer(ResteasyDeployment deployment, int port, SecurityDomain securityDomain, String rootResourcePath);
}
