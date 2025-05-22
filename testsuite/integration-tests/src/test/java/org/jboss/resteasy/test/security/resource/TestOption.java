package org.jboss.resteasy.test.security.resource;

import java.util.function.Supplier;

import org.jboss.resteasy.spi.config.Options;
import org.jboss.resteasy.spi.util.Functions;

/**
 * A custom option class for testing security manager interactions.
 */
public class TestOption {
    // Create a subclass of Options to access the protected constructor
    public static class TestOptionImpl<T> extends Options<T> {
        public TestOptionImpl(final String key, final Class<T> type, final Supplier<T> defaultValueSupplier) {
            super(key, type, defaultValueSupplier);
        }
    }

    private static final Supplier<String> DEFAULT_VALUE_SUPPLIER = Functions.singleton(() -> "default-value");

    // Use our subclass instead of Options directly
    public static final TestOptionImpl<String> TEST_OPTION = new TestOptionImpl<>(
            "org.jboss.resteasy.test.security.option",
            String.class,
            DEFAULT_VALUE_SUPPLIER);
}
