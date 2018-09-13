package org.jboss.resteasy.test.jsapi;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import javax.ws.rs.client.ClientBuilder;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import org.junit.runner.RunWith;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

/**
 * @tpSubChapter Jsapi
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class JSAPIGetBasicJsapiHandlingScriptTest {

    static ResteasyClient client;
    protected static final Logger logger = LogManager.getLogger(JSAPIGetBasicJsapiHandlingScriptTest.class.getName());

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(JSAPIGetBasicJsapiHandlingScriptTest.class.getSimpleName());
        war.addAsWebInfResource(JSAPIGetBasicJsapiHandlingScriptTest.class.getPackage(), "web.xml", "web.xml");
        return TestUtil.finishContainerPrepare(war, null, (Class<?>[]) null);
    }

    @Before
    public void init() {
        client = (ResteasyClient)ClientBuilder.newClient();
    }

    @After
    public void after() throws Exception {
        client.close();
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, JSAPIGetBasicJsapiHandlingScriptTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails The deployed application has configured JSApi servlet and the test gets the header of JSAPI script
     * for handling request to REST resources with javascript.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGetJsapiHeaderScript() throws Exception {
        WebTarget target = client.target(generateURL("/rest-js"));
        Response response = target.request().get();
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        String responseString = response.readEntity(String.class);
        logger.info(responseString);
        Assert.assertTrue("Basic javascript generated by jsapi doesn't contain expected code", responseString.contains("REST.Request = function (){"));
    }
}
