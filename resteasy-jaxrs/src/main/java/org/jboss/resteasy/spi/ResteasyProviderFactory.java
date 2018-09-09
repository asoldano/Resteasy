package org.jboss.resteasy.spi;

import org.jboss.resteasy.resteasy_jaxrs.i18n.LogMessages;
import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;
import org.jboss.resteasy.spi.metadata.ResourceBuilder;
import org.jboss.resteasy.spi.metadata.ResourceClassProcessor;
import org.jboss.resteasy.spi.util.ThreadLocalStack;
import org.jboss.resteasy.spi.util.Types;

import javax.annotation.Priority;
import javax.ws.rs.ConstrainedTo;
import javax.ws.rs.Consumes;
import javax.ws.rs.Priorities;
import javax.ws.rs.Produces;
import javax.ws.rs.RuntimeType;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.client.RxInvoker;
import javax.ws.rs.client.RxInvokerProvider;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Configurable;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Variant;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;
import javax.ws.rs.ext.Providers;
import javax.ws.rs.ext.ReaderInterceptor;
import javax.ws.rs.ext.RuntimeDelegate;
import javax.ws.rs.ext.RuntimeDelegate.HeaderDelegate;
import javax.ws.rs.ext.WriterInterceptor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public abstract class ResteasyProviderFactory extends RuntimeDelegate implements Providers, HeaderValueProcessor, Configurable<ResteasyProviderFactory>, Configuration
{
   static final ThreadLocalStack<Map<Class<?>, Object>> contextualData = new ThreadLocalStack<Map<Class<?>, Object>>();
   private static final int maxForwards = 20;
   private static volatile ResteasyProviderFactory instance;
   private static boolean registerBuiltinByDefault = true;

   public abstract Set<DynamicFeature> getServerDynamicFeatures();

   public abstract Set<DynamicFeature> getClientDynamicFeatures();

   public abstract Map<Class<?>, AsyncResponseProvider> getAsyncResponseProviders();

   public abstract Map<Class<?>, AsyncClientResponseProvider> getAsyncClientResponseProviders();
   
   public abstract Map<Class<?>, AsyncStreamProvider> getAsyncStreamProviders();

   public abstract Map<Type, ContextInjector> getContextInjectors();

   public abstract Map<Type, ContextInjector> getAsyncContextInjectors();

   public abstract Set<Class<?>> getProviderClasses();

   public abstract Set<Object> getProviderInstances();

   public static <T> void pushContext(Class<T> type, T data)
   {
      getContextDataMap().put(type, data);
   }

   public static void pushContextDataMap(Map<Class<?>, Object> map)
   {
      contextualData.push(map);
   }

   public static Map<Class<?>, Object> getContextDataMap()
   {
      return getContextDataMap(true);
   }

   public static <T> T getContextData(Class<T> type)
   {
      return (T) getContextDataMap().get(type);
   }

   public abstract boolean hasAsyncContextData(Type genericType);
   
   public abstract <T> T getContextData(Class<T> rawType, Type genericType, Annotation[] annotations, boolean unwrapAsync);

   public static <T> T popContextData(Class<T> type)
   {
      return (T) getContextDataMap().remove(type);
   }

   public static void clearContextData()
   {
      contextualData.clear();
   }

   private static Map<Class<?>, Object> getContextDataMap(boolean create)
   {
      Map<Class<?>, Object> map = contextualData.get();
      if (map == null)
      {
         contextualData.setLast(map = new HashMap<Class<?>, Object>());
      }
      return map;
   }

   public static Map<Class<?>, Object> addContextDataLevel()
   {
      if (getContextDataLevelCount() == maxForwards)
      {
         throw new BadRequestException(Messages.MESSAGES.excededMaximumForwards(getContextData(UriInfo.class).getPath()));
      }
      Map<Class<?>, Object> map = new HashMap<Class<?>, Object>();
      contextualData.push(map);
      return map;
   }

   public static int getContextDataLevelCount()
   {
      return contextualData.size();
   }

   public static void removeContextDataLevel()
   {
      contextualData.pop();
   }

   /**
    * Will not initialize singleton if not set.
    *
    * @return provider factory singleton
    */
   public static ResteasyProviderFactory peekInstance()
   {
      return instance;
   }

   public synchronized static void clearInstanceIfEqual(ResteasyProviderFactory factory)
   {
      if (instance == factory)
      {
         instance = null;
         RuntimeDelegate.setInstance(null);
      }
   }

   public synchronized static void setInstance(ResteasyProviderFactory factory)
   {
      synchronized (RD_LOCK)
      {
         instance = factory;
      }
      RuntimeDelegate.setInstance(factory);
   }

   final static Object RD_LOCK = new Object();

   /**
    * Initializes ResteasyProviderFactory singleton if not set.
    *
    * @return singleton provider factory
    */
   public static ResteasyProviderFactory getInstance()
   {
      ResteasyProviderFactory result = instance;
      if (result == null)
      { // First check (no locking)
         synchronized (RD_LOCK)
         {
            result = instance;
            if (result == null)
            { // Second check (with locking)
               RuntimeDelegate runtimeDelegate = RuntimeDelegate.getInstance();
               if (runtimeDelegate instanceof ResteasyProviderFactory)
               {
                  instance = result = (ResteasyProviderFactory) runtimeDelegate;
               }
               else
               {
                  instance = result = newInstance(); //TODO use reflection directly, to avoid circular dependency
               }
               if (registerBuiltinByDefault) instance.registerBuiltin();
            }
         }
      }
      return instance;
   }

   public static ResteasyProviderFactory newInstance()
   {
      //TODO implement this differently: call getInstance(), retrieve the class, classloader, constructor from it, store locally in singletons, use those starting from now.
      try
      {
         return (ResteasyProviderFactory)Thread.currentThread().getContextClassLoader().loadClass("org.jboss.resteasy.core.ResteasyProviderFactoryImpl").newInstance();
      }
      catch (InstantiationException | IllegalAccessException | ClassNotFoundException e)
      {
         throw new RuntimeException(e);
      }
   }

   public static void setRegisterBuiltinByDefault(boolean registerBuiltinByDefault)
   {
      ResteasyProviderFactory.registerBuiltinByDefault = registerBuiltinByDefault;
   }
   
   protected abstract void registerBuiltin();

   public abstract boolean isRegisterBuiltins();

   public abstract void setRegisterBuiltins(boolean registerBuiltins);

   public abstract InjectorFactory getInjectorFactory();

   public abstract void setInjectorFactory(InjectorFactory injectorFactory);

   public abstract boolean isBuiltinsRegistered();

   public abstract void setBuiltinsRegistered(boolean builtinsRegistered);

   public abstract void addHeaderDelegate(Class clazz, HeaderDelegate header);

   @Deprecated
   public abstract <T> MessageBodyReader<T> getServerMessageBodyReader(Class<T> type, Type genericType, Annotation[] annotations, MediaType mediaType);

//   public <T> MessageBodyReader<T> getServerMessageBodyReader(Class<T> type, Type genericType, Annotation[] annotations, MediaType mediaType, RESTEasyTracingLogger tracingLogger);

   public abstract <T> MessageBodyReader<T> getClientMessageBodyReader(Class<T> type, Type genericType, Annotation[] annotations, MediaType mediaType);

//   @Deprecated
//   private <T> MessageBodyReader<T> resolveMessageBodyReader(Class<T> type, Type genericType, Annotation[] annotations, MediaType mediaType, MediaTypeMap<SortedKey<MessageBodyReader>> availableReaders);

   public abstract List<ContextResolver> getContextResolvers(final Class<?> clazz, MediaType type);

   public abstract ParamConverter getParamConverter(Class clazz, Type genericType, Annotation[] annotations);

   public abstract <T> StringParameterUnmarshaller<T> createStringParameterUnmarshaller(Class<T> clazz);

   public abstract void registerProvider(Class provider);

   /**
    * Convert an object to a string.  First try StringConverter then, object.ToString()
    *
    * @param object object
    * @param clazz class
    * @param genericType generic type
    * @param annotations array of annotation
    * @return string representation
    */
   public abstract String toString(Object object, Class clazz, Type genericType, Annotation[] annotations);

   /**
    * Checks to see if RuntimeDelegate is a ResteasyProviderFactory
    * If it is, then use that, otherwise use this.
    *
    * @param aClass class of the header
    * @return header delegate
    */
   public abstract HeaderDelegate getHeaderDelegate(Class<?> aClass);

   /**
    * Register a @Provider class.  Can be a MessageBodyReader/Writer or ExceptionMapper.
    *
    * @param provider provider class
    * @param isBuiltin built-in
    */
   public abstract void registerProvider(Class provider, boolean isBuiltin);

   public abstract void registerProvider(Class provider, Integer priorityOverride, boolean isBuiltin, Map<Class<?>, Integer> contracts);

   /**
    * Register a @Provider object.  Can be a MessageBodyReader/Writer or ExceptionMapper.
    *
    * @param provider provider instance
    */
   public abstract void registerProviderInstance(Object provider);

   public abstract void registerProviderInstance(Object provider, Map<Class<?>, Integer> contracts, Integer priorityOverride, boolean builtIn);

   public abstract <T> AsyncResponseProvider<T> getAsyncResponseProvider(Class<T> type);
   
   public abstract <T> AsyncClientResponseProvider<T> getAsyncClientResponseProvider(Class<T> type);

   public abstract <T> AsyncStreamProvider<T> getAsyncStreamProvider(Class<T> type);

   public abstract MediaType getConcreteMediaTypeFromMessageBodyWriters(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType);

   public abstract Map<MessageBodyWriter<?>, Class<?>> getPossibleMessageBodyWritersMap(Class type, Type genericType, Annotation[] annotations, MediaType accept);

   // use the tracingLogger enabled version please
   @Deprecated
   public abstract <T> MessageBodyWriter<T> getServerMessageBodyWriter(Class<T> type, Type genericType, Annotation[] annotations, MediaType mediaType);


//   public <T> MessageBodyWriter<T> getServerMessageBodyWriter(Class<T> type, Type genericType, Annotation[] annotations, MediaType mediaType, RESTEasyTracingLogger tracingLogger);

   public abstract <T> MessageBodyWriter<T> getClientMessageBodyWriter(Class<T> type, Type genericType, Annotation[] annotations, MediaType mediaType);

//   @Deprecated
//   private <T> MessageBodyWriter<T> resolveMessageBodyWriter(Class<T> type, Type genericType, Annotation[] annotations, MediaType mediaType, MediaTypeMap<SortedKey<MessageBodyWriter>> availableWriters);

   /**
    * Create an instance of a class using provider allocation rules of the specification as well as the InjectorFactory
    * only does constructor injection.
    *
    * @param clazz class 
    * @param <T> type
    * @return provider instance of type T
    */
   public abstract <T> T createProviderInstance(Class<? extends T> clazz);

   /**
    * Property and constructor injection using the InjectorFactory.
    *
    * @param clazz class
    * @param <T> type
    * @return instance of type T
    */
   public abstract <T> T injectedInstance(Class<? extends T> clazz);

   /**
    * Property and constructor injection using the InjectorFactory.
    *
    * @param clazz class
    * @param request http request
    * @param response http response
    * @param <T> type
    * @return instance of type T
    */
   public abstract <T> T injectedInstance(Class<? extends T> clazz, HttpRequest request, HttpResponse response);

   public abstract void injectProperties(Object obj);
   
   public abstract void injectProperties(Object obj, HttpRequest request, HttpResponse response);

   // Configurable
   public abstract Map<String, Object> getMutableProperties();

   public abstract ResteasyProviderFactory setProperties(Map<String, ?> properties);

   public abstract Collection<Feature> getEnabledFeatures();

   public abstract <I extends RxInvoker> RxInvokerProvider<I> getRxInvokerProvider(Class<I> clazz);
   
   public abstract RxInvokerProvider<?> getRxInvokerProviderFromReactiveClass(Class<?> clazz);

   public abstract boolean isReactive(Class<?> clazz);

   public abstract ResourceBuilder getResourceBuilder();

}
