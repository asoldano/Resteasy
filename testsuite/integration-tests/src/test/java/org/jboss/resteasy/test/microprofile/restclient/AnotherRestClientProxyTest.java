package org.jboss.resteasy.test.microprofile.restclient;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.net.URL;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import javax.ws.rs.client.ClientBuilder;

import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.microprofile.MicroprofileClientBuilderResolver;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.reactivex.Single;

@RunWith(Arquillian.class)
@RunAsClient
public class AnotherRestClientProxyTest
{

   @ArquillianResource
   URL url;

   @Deployment
   public static Archive<?> deploy()
   {
      WebArchive war = TestUtil.prepareArchive(AnotherRestClientProxyTest.class.getSimpleName());
      war.addClass(AnotherRestClientProxyTest.class);
      war.addPackage(Resource.class.getPackage());
      war.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
      war.addClass(PortProviderUtil.class);
      war.addAsManifestResource(new StringAsset("Dependencies: org.eclipse.microprofile.restclient,org.jboss.resteasy.resteasy-rxjava2 services\n"), "MANIFEST.MF");
      return TestUtil.finishContainerPrepare(war, null);
   }

   private String generateURL(String path)
   {
      return PortProviderUtil.generateURL(path, AnotherRestClientProxyTest.class.getSimpleName());
   }

   @Test
   public void testNormal() throws Exception
   {
      ResteasyClient client = (ResteasyClient) ClientBuilder.newBuilder().build();
      RxInterface proxy = client.target(generateURL("")).proxy(RxInterface.class);
      CompletionStage<String> cs = proxy.normalCs();
      System.out.println(cs.toCompletableFuture().get());
      Single<String> single = proxy.normalSingle();
      single.subscribe((String s) -> {
         System.out.println(s);
      });
      client.close();
   }

   @Test
   public void testMP() throws Exception
   {
      RxInterface mpRestClient = MicroprofileClientBuilderResolver.instance()
            .newBuilder()
            .baseUrl(new URL(generateURL("")))
            .build(RxInterface.class);
      String s = mpRestClient.normalString();
      System.out.println(s);
   }

   @Test
   public void testMPRX() throws Exception
   {
      RxInterface mpRestClient = MicroprofileClientBuilderResolver.instance().newBuilder()
            .baseUrl(new URL(generateURL(""))).build(RxInterface.class);
      CompletionStage<String> cs = mpRestClient.normalCs();
      System.out.println(cs.toCompletableFuture().get());
      Single<String> single = mpRestClient.normalSingle();
      single.subscribe((String s) -> {
         System.out.println(s);
      });
   }

}