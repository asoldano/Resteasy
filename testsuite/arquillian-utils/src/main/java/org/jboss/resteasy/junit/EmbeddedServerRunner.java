package org.jboss.resteasy.junit;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

import org.jboss.resteasy.core.ResteasyDeploymentImpl;
import org.jboss.resteasy.plugins.server.embedded.EmbeddedJaxrsServer;
import org.jboss.resteasy.plugins.server.embedded.EmbeddedJaxrsServerFactory;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkField;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.junit.runners.model.TestClass;

public class EmbeddedServerRunner extends BlockJUnit4ClassRunner
{
   private Map<String, ResteasyDeployment> deployments;

   public EmbeddedServerRunner(final Class<?> klass) throws InitializationError
   {
      super(klass);
   }

   @Override
   protected Statement withBeforeClasses(final Statement originalStatement)
   {
      processDeployments();
      injectServer();
      return super.withBeforeClasses(originalStatement);
   }

   @Override
   protected Statement withAfterClasses(final Statement originalStatement)
   {
      Statement stmt = super.withAfterClasses(originalStatement);
      return new Statement()
      {
         @Override
         public void evaluate() throws Throwable
         {
            stmt.evaluate();
            cleanupInjections();
         }
      };
   }

   private void processDeployments()
   {
      TestClass clazz = getTestClass();
      for (FrameworkMethod m : clazz.getAnnotatedMethods(Deployment.class))
      {
         if (m.isStatic() && ResteasyDeployment.class.isAssignableFrom(m.getReturnType()))
         {
            Deployment a = m.getAnnotation(Deployment.class);
            if (deployments == null)
            {
               deployments = new HashMap<>();
            }
            try
            {
               deployments.put(a.name(), (ResteasyDeployment) m.getMethod().invoke(null));
            }
            catch (Exception e)
            {
               throw new RuntimeException(e);
            }
         }
      }
   }

   private void injectServer()
   {
      TestClass clazz = getTestClass();
      EmbeddedJaxrsServerFactory factory = ServiceLoader.load(EmbeddedJaxrsServerFactory.class).iterator().next();
      for (FrameworkField f : clazz.getAnnotatedFields(EmbeddedServer.class))
      {
         if (f.isStatic() && EmbeddedJaxrsServer.class.isAssignableFrom(f.getType()))
         {
            EmbeddedServer a = f.getAnnotation(EmbeddedServer.class);
            String rootResourcePath = a.rootResourcePath();
            if (rootResourcePath == null)
            {
               rootResourcePath = "/";
            }
            else if (!rootResourcePath.startsWith("/"))
            {
               rootResourcePath = "/" + rootResourcePath;
            }
            ResteasyDeployment dep = deployments.get(a.deployment());
            if (dep == null)
            {
               dep = new ResteasyDeploymentImpl();
            }
            EmbeddedJaxrsServer v = factory.newServer(dep, a.port(), null, rootResourcePath);
            try
            {
               f.getField().set(null, v);
            }
            catch (Exception e)
            {
               throw new RuntimeException(e);
            }
         }
      }
   }

   private void cleanupInjections()
   {
      TestClass clazz = getTestClass();
      for (FrameworkField f : clazz.getAnnotatedFields(EmbeddedServer.class))
      {
         if (f.isStatic() && EmbeddedJaxrsServer.class.isAssignableFrom(f.getType()))
         {
            try
            {
               f.getField().set(null, null);
            }
            catch (Exception e)
            {
               throw new RuntimeException(e);
            }
         }
      }
   }

}
