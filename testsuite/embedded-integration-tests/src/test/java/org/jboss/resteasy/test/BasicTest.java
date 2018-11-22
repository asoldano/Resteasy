/*
 * JBoss, Home of Professional Open Source
 * Copyright 2016, Red Hat Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.resteasy.test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.core.ResteasyDeploymentImpl;
import org.jboss.resteasy.junit.Deployment;
import org.jboss.resteasy.junit.EmbeddedServer;
import org.jboss.resteasy.junit.EmbeddedServerRunner;
import org.jboss.resteasy.plugins.server.embedded.EmbeddedJaxrsServer;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.test.resource.SimpleResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EmbeddedServerRunner.class)
public class BasicTest {

   @EmbeddedServer(port = 8080, rootResourcePath = "BasicTest")
   public static EmbeddedJaxrsServer server;

   @EmbeddedServer(port = 8081, rootResourcePath = "BasicTest2", deployment = "dep2")
   public static EmbeddedJaxrsServer server2;

   static Client client;

   @Deployment
   public static ResteasyDeployment deploy() {
      ResteasyDeployment dep = new ResteasyDeploymentImpl();
      dep.getResources().add(new SimpleResource());
      return dep;
   }

   @Deployment(name = "dep2")
   public static ResteasyDeployment deploy2() {
      ResteasyDeployment dep = new ResteasyDeploymentImpl();
      dep.getResources().add(new SimpleResource());
      return dep;
   }

   @BeforeClass
   public static void init() {
      server.start();
      client = ClientBuilder.newClient();
   }

   @AfterClass
   public static void after() throws Exception {
      client.close();
      server.stop();
   }

   private String generateURL(String path) {
      return PortProviderUtil.generateURL(path, BasicTest.class.getSimpleName());
   }

   @Test
   public void testServer() {
      Builder builder = client.target(generateURL("/simpleresource")).request();
      builder.accept(MediaType.TEXT_PLAIN_TYPE);

      Response getResponse = builder.get();
      String responseBody = getResponse.readEntity(String.class);
      Assert.assertEquals("The response body doesn't match the expected", "hello", responseBody);
   }

   @Test
   public void testServer2() {
      server2.start();
      try {
         Builder builder = client.target(PortProviderUtil.generateURL("/simpleresource", "BasicTest2", PortProviderUtil.getHost(), 8081)).request();
         builder.accept(MediaType.TEXT_PLAIN_TYPE);

         Response getResponse = builder.get();
         String responseBody = getResponse.readEntity(String.class);
         Assert.assertEquals("The response body doesn't match the expected", "hello", responseBody);
      } finally {
         server2.stop();
      }
   }

}
