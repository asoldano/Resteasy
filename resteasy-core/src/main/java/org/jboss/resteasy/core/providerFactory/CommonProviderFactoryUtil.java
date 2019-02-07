package org.jboss.resteasy.core.providerFactory;

import java.lang.reflect.Constructor;
import java.util.Map;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;

import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;
import org.jboss.resteasy.spi.ConstructorInjector;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.util.PickConstructor;

/**
 * 
 */
public final class CommonProviderFactoryUtil
{
   static boolean isA(Class target, Class type, Map<Class<?>, Integer> contracts)
   {
      if (!type.isAssignableFrom(target))
         return false;
      if (contracts == null || contracts.size() == 0)
         return true;
      for (Class<?> contract : contracts.keySet())
      {
         if (contract.equals(type))
            return true;
      }
      return false;
   }

   static boolean isA(Object target, Class type, Map<Class<?>, Integer> contracts)
   {
      return isA(target.getClass(), type, contracts);
   }
   
   static int getPriority(Integer override, Map<Class<?>, Integer> contracts, Class type, Class<?> component)
   {
      if (override != null)
         return override;
      if (contracts != null)
      {
         Integer p = contracts.get(type);
         if (p != null)
            return p;
      }
      // Check for weld proxy.
      component = component.isSynthetic() ? component.getSuperclass() : component;
      Priority priority = component.getAnnotation(Priority.class);
      if (priority == null)
         return Priorities.USER;
      return priority.value();
   }
   
   static void injectProperties(ResteasyProviderFactory rpf, Class declaring, Object obj)
   {
      rpf.getInjectorFactory().createPropertyInjector(declaring, rpf).inject(obj, false).toCompletableFuture()
            .getNow(null);
   }

   static void injectProperties(ResteasyProviderFactory rpf, Object obj)
   {
      rpf.getInjectorFactory().createPropertyInjector(obj.getClass(), rpf).inject(obj, false).toCompletableFuture()
            .getNow(null);
   }

   static void injectProperties(ResteasyProviderFactory rpf, Object obj, HttpRequest request, HttpResponse response)
   {
      rpf.getInjectorFactory().createPropertyInjector(obj.getClass(), rpf).inject(request, response, obj, false)
            .toCompletableFuture().getNow(null);
   }
   
   static <T> T createProviderInstance(ResteasyProviderFactory rpf, Class<? extends T> clazz)
   {
      ConstructorInjector constructorInjector = createConstructorInjector(rpf, clazz);

      T provider = (T) constructorInjector.construct(false).toCompletableFuture().getNow(null);
      return provider;
   }

   private static <T> ConstructorInjector createConstructorInjector(ResteasyProviderFactory rpf, Class<? extends T> clazz)
   {
      Constructor<?> constructor = PickConstructor.pickSingletonConstructor(clazz);
      if (constructor == null)
      {
         throw new IllegalArgumentException(
               Messages.MESSAGES.unableToFindPublicConstructorForProvider(clazz.getName()));
      }
      return rpf.getInjectorFactory().createConstructor(constructor, rpf);
   }
}
