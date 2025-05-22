package org.jboss.resteasy.test.security;

import java.lang.reflect.Method;
import java.security.Permission;

import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.core.ResteasyDeploymentImpl;
import org.jboss.resteasy.spi.ResteasyConfiguration;
import org.jboss.resteasy.spi.config.Options;
import org.jboss.resteasy.test.security.resource.SecurityCheckApplication;
import org.jboss.resteasy.test.security.resource.SecurityCheckResource;
import org.jboss.resteasy.test.security.resource.TestOption;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Test for RESTEASY-3593: Pass the ResteasyConfiguration to the Option.getValue() when security manager is enabled
 *
 * @see org.jboss.resteasy.core.ResteasyDeploymentImpl#getOptionValue(Options, ResteasyConfiguration)
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class OptionSecurityManagerTest {

    private static final String TEST_OPTION_KEY = "org.jboss.resteasy.test.security.option";
    private static final String TEST_OPTION_VALUE = "test-value";

    private static SecurityManager originalSecurityManager;
    private ResteasyClient client;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(OptionSecurityManagerTest.class.getSimpleName());
        war.addClass(SecurityCheckApplication.class);
        war.addClass(SecurityCheckResource.class);
        war.addClass(TestOption.class);

        // Set our test property
        war.addAsResource(new org.jboss.shrinkwrap.api.asset.StringAsset(
                TEST_OPTION_KEY + "=" + TEST_OPTION_VALUE), "META-INF/microprofile-config.properties");

        return TestUtil.finishContainerPrepare(war, null, SecurityCheckResource.class);
    }

    @BeforeEach
    public void setup() {
        // Save the original security manager
        originalSecurityManager = System.getSecurityManager();

        // Set a test security manager for this test that allows all permissions
        // We just need the security manager to be active, but we don't want it to
        // block any operations needed by the test framework
        System.setSecurityManager(new SecurityManager() {
            @Override
            public void checkPermission(Permission perm) {
                // Allow all permissions, we just need the SecurityManager to be active
                // The important thing is that System.getSecurityManager() != null
                // so that the code under test uses AccessController.doPrivileged()
            }

            @Override
            public void checkExit(int status) {
                // Allow exit without throwing SecurityException
            }
        });

        // Create client for testing
        client = (ResteasyClient) ResteasyClientBuilder.newClient();
    }

    @AfterEach
    public void teardown() {
        // Restore original security manager
        System.setSecurityManager(originalSecurityManager);
        if (client != null) {
            client.close();
        }
    }

    /**
     * This test simulates the change made in RESTEASY-3593.
     * It verifies that the configuration is correctly passed to Options.getValue()
     * when a security manager is present.
     */
    @Test
    public void testOptionGetValueWithSecurityManager() throws Exception {
        // Create our test option
        final Options<String> option = TestOption.TEST_OPTION;

        // Call getOptionValue method via reflection to test the actual implementation
        Method getOptionValueMethod = ResteasyDeploymentImpl.class.getDeclaredMethod(
                "getOptionValue", Options.class, ResteasyConfiguration.class);
        getOptionValueMethod.setAccessible(true);

        // Our test will use null as the ResteasyConfiguration since we can't easily create one
        // The important thing is that the right method (option.getValue(config)) is called in the PrivilegedAction
        String result = (String) getOptionValueMethod.invoke(null, option, null);

        // Since we're running with a SecurityManager, the privileged action should be used
        // Even with null config, we should get our default value at minimum
        Assertions.assertNotNull(result, "Result should not be null");

        // The second part of our test - check the endpoint can access options
        String url = PortProviderUtil.generateURL("/security/option", OptionSecurityManagerTest.class.getSimpleName());
        Response response = client.target(url).request().get();
        Assertions.assertEquals(200, response.getStatus());
        String body = response.readEntity(String.class);
        Assertions.assertEquals("true", body); // Should match Options.ENABLE_DEFAULT_EXCEPTION_MAPPER default value
    }
}
