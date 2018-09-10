package org.jboss.resteasy.spi.interception;

import java.lang.reflect.AccessibleObject;
import java.util.Comparator;
import java.util.List;

import org.jboss.resteasy.spi.ResteasyProviderFactory;

public interface JaxrsInterceptorRegistry<T>
{
   public static class Match
   {
      public Match(Object interceptor, int order)
      {
         this.interceptor = interceptor;
         this.order = order;
      }

      final public Object interceptor;

      final public int order;
   }

   public interface InterceptorFactory
   {
      Match preMatch();

      Match postMatch(@SuppressWarnings("rawtypes") Class declaring, AccessibleObject target);
   }

   public JaxrsInterceptorRegistry<T> clone(ResteasyProviderFactory factory);

   public Class<T> getIntf();

   public static class AscendingPrecedenceComparator implements Comparator<Match>
   {
      public int compare(Match match, Match match2)
      {
         if (match.order < match2.order)
         {
            return -1;
         }
         if (match.order == match2.order)
         {
            return 0;
         }
         return 1;
      }
   }

   public static class DescendingPrecedenceComparator implements Comparator<Match>
   {
      public int compare(Match match, Match match2)
      {
         if (match2.order < match.order)
         {
            return -1;
         }
         if (match2.order == match.order)
         {
            return 0;
         }
         return 1;
      }
   }

   public List<JaxrsInterceptorRegistryListener> getListeners();

   public T[] preMatch();

   public T[] postMatch(@SuppressWarnings("rawtypes") Class declaring, AccessibleObject target);

   public void register(InterceptorFactory factory);

   public void registerClass(Class<? extends T> declaring);

   public void registerClass(Class<? extends T> declaring, int priority);

   public void registerSingleton(T interceptor);

   public void registerSingleton(T interceptor, int priority);
}
