package org.jboss.resteasy.junit;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({ FIELD })
@Retention(RUNTIME)
public @interface EmbeddedServer {

   int port() default 8081;
   String rootResourcePath() default "";
   String deployment() default "";

}
