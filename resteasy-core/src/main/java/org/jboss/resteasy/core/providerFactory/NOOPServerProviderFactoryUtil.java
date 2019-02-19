package org.jboss.resteasy.core.providerFactory;

import java.util.Map;

import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.ReaderInterceptor;
import javax.ws.rs.ext.WriterInterceptor;

import org.jboss.resteasy.core.MediaTypeMap;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.interception.JaxrsInterceptorRegistry;

/**
 * A ServerProviderFactoryUtil that does nothing, useful to save memory when creating a ResteasyProviderFactory for client side only
 */
public final class NOOPServerProviderFactoryUtil extends ServerProviderFactoryUtil
{
   public static final NOOPServerProviderFactoryUtil INSTANCE = new NOOPServerProviderFactoryUtil(null);

   private NOOPServerProviderFactoryUtil(ResteasyProviderFactoryImpl rpf)
   {
      super(rpf);
   }

   @Override
   protected void initializeRegistriesAndFilters(ResteasyProviderFactoryImpl parent)
   {
      //NOOP
   }
   
   protected JaxrsInterceptorRegistry<ReaderInterceptor> getServerReaderInterceptorRegistry(ResteasyProviderFactory parent)
   {
      throw new UnsupportedOperationException();
   }

   protected JaxrsInterceptorRegistry<WriterInterceptor> getServerWriterInterceptorRegistry(ResteasyProviderFactory parent)
   {
      throw new UnsupportedOperationException();
   }

   protected JaxrsInterceptorRegistry<ContainerRequestFilter> getContainerRequestFilterRegistry(ResteasyProviderFactory parent)
   {
      throw new UnsupportedOperationException();
   }

   protected JaxrsInterceptorRegistry<ContainerResponseFilter> getContainerResponseFilterRegistry(ResteasyProviderFactory parent)
   {
      throw new UnsupportedOperationException();
   }
   
   protected void processProviderContracts(Class provider, Integer priorityOverride, boolean isBuiltin,
         Map<Class<?>, Integer> contracts, Map<Class<?>, Integer> newContracts, ResteasyProviderFactoryImpl parent)
   {
      //NOOP
   }
   
   protected void processProviderInstanceContracts(Object provider, Map<Class<?>, Integer> contracts,
         Integer priorityOverride, boolean builtIn, Map<Class<?>, Integer> newContracts, ResteasyProviderFactoryImpl parent)
   {
      //NOOP
   }
   
   protected MediaTypeMap<SortedKey<MessageBodyReader>> getServerMessageBodyReaders(ResteasyProviderFactoryImpl parent)
   {
      throw new UnsupportedOperationException();
   }

   protected MediaTypeMap<SortedKey<MessageBodyWriter>> getServerMessageBodyWriters(ResteasyProviderFactoryImpl parent)
   {
      throw new UnsupportedOperationException();
   }

   protected void addMessageBodyReader(MessageBodyReader provider, Class<?> providerClass, int priority,
         boolean isBuiltin, ResteasyProviderFactoryImpl parent)
   {
      //NOOP
   }

   protected void addMessageBodyWriter(MessageBodyWriter provider, Class<?> providerClass, int priority,
         boolean isBuiltin, ResteasyProviderFactoryImpl parent)
   {
      //NOOP
   }
}
