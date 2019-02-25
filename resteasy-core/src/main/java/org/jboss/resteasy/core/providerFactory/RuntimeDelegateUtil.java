package org.jboss.resteasy.core.providerFactory;

import java.net.URI;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.Variant;
import javax.ws.rs.ext.RuntimeDelegate.HeaderDelegate;

import org.jboss.resteasy.plugins.delegates.CacheControlDelegate;
import org.jboss.resteasy.plugins.delegates.CookieHeaderDelegate;
import org.jboss.resteasy.plugins.delegates.DateDelegate;
import org.jboss.resteasy.plugins.delegates.EntityTagDelegate;
import org.jboss.resteasy.plugins.delegates.LinkDelegate;
import org.jboss.resteasy.plugins.delegates.LinkHeaderDelegate;
import org.jboss.resteasy.plugins.delegates.LocaleDelegate;
import org.jboss.resteasy.plugins.delegates.MediaTypeHeaderDelegate;
import org.jboss.resteasy.plugins.delegates.NewCookieHeaderDelegate;
import org.jboss.resteasy.plugins.delegates.UriHeaderDelegate;
import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;
import org.jboss.resteasy.specimpl.LinkBuilderImpl;
import org.jboss.resteasy.specimpl.ResponseBuilderImpl;
import org.jboss.resteasy.specimpl.ResteasyUriBuilderImpl;
import org.jboss.resteasy.specimpl.VariantListBuilderImpl;
import org.jboss.resteasy.spi.LinkHeader;

@SuppressWarnings({"rawtypes", "unchecked"})
public class RuntimeDelegateUtil
{
   private Map<Class<?>, HeaderDelegate> headerDelegates;

   public RuntimeDelegateUtil()
   {
   }

   protected void initialize(ResteasyProviderFactoryImpl parent)
   {
      headerDelegates = parent == null ? new ConcurrentHashMap<>() : new ConcurrentHashMap<>(parent.getHeaderDelegates());
      addHeaderDelegateIfAbsent(MediaType.class, MediaTypeHeaderDelegate.INSTANCE, parent);
      addHeaderDelegateIfAbsent(NewCookie.class, NewCookieHeaderDelegate.INSTANCE, parent);
      addHeaderDelegateIfAbsent(Cookie.class, CookieHeaderDelegate.INSTANCE, parent);
      addHeaderDelegateIfAbsent(URI.class, UriHeaderDelegate.INSTANCE, parent);
      addHeaderDelegateIfAbsent(EntityTag.class, EntityTagDelegate.INSTANCE, parent);
      addHeaderDelegateIfAbsent(CacheControl.class, CacheControlDelegate.INSTANCE, parent);
      addHeaderDelegateIfAbsent(Locale.class, LocaleDelegate.INSTANCE, parent);
      addHeaderDelegateIfAbsent(LinkHeader.class, LinkHeaderDelegate.INSTANCE, parent);
      addHeaderDelegateIfAbsent(javax.ws.rs.core.Link.class, LinkDelegate.INSTANCE, parent);
      addHeaderDelegateIfAbsent(Date.class, DateDelegate.INSTANCE, parent);
   }

   protected UriBuilder createUriBuilder()
   {
      return new ResteasyUriBuilderImpl();
   }

   protected Response.ResponseBuilder createResponseBuilder()
   {
      return new ResponseBuilderImpl();
   }

   protected Variant.VariantListBuilder createVariantListBuilder()
   {
      return new VariantListBuilderImpl();
   }

   protected Link.Builder createLinkBuilder()
   {
      return new LinkBuilderImpl();
   }

   protected <T> HeaderDelegate<T> createHeaderDelegate(Class<T> tClass, ResteasyProviderFactoryImpl parent)
   {
      if (tClass == null)
         throw new IllegalArgumentException(Messages.MESSAGES.tClassParameterNull());
      if (headerDelegates == null && parent != null)
         return parent.createHeaderDelegate(tClass);

      Class<?> clazz = tClass;
      while (clazz != null)
      {
         HeaderDelegate<T> delegate = headerDelegates.get(clazz);
         if (delegate != null)
         {
            return delegate;
         }
         delegate = createHeaderDelegateFromInterfaces(clazz.getInterfaces());
         if (delegate != null)
         {
            return delegate;
         }
         clazz = clazz.getSuperclass();
      }

      return createHeaderDelegateFromInterfaces(tClass.getInterfaces());
   }

   private <T> HeaderDelegate<T> createHeaderDelegateFromInterfaces(Class<?>[] interfaces)
   {
      HeaderDelegate<T> delegate = null;
      for (int i = 0; i < interfaces.length; i++)
      {
         delegate = headerDelegates.get(interfaces[i]);
         if (delegate != null)
         {
            return delegate;
         }
         delegate = createHeaderDelegateFromInterfaces(interfaces[i].getInterfaces());
         if (delegate != null)
         {
            return delegate;
         }
      }
      return null;
   }

   protected Map<Class<?>, HeaderDelegate> getHeaderDelegates(ResteasyProviderFactoryImpl parent)
   {
      if (headerDelegates == null && parent != null)
         return parent.getHeaderDelegates();
      return headerDelegates;
   }

   private void addHeaderDelegateIfAbsent(Class clazz, HeaderDelegate header, ResteasyProviderFactoryImpl parent)
   {
      if (headerDelegates == null || !headerDelegates.containsKey(clazz))
      {
         addHeaderDelegate(clazz, header, parent);
      }
   }

   protected void addHeaderDelegate(Class clazz, HeaderDelegate header, ResteasyProviderFactoryImpl parent)
   {
      if (headerDelegates == null)
      {
         headerDelegates = new ConcurrentHashMap<Class<?>, HeaderDelegate>();
         headerDelegates.putAll(parent.getHeaderDelegates());
      }
      headerDelegates.put(clazz, header);
   }
}
