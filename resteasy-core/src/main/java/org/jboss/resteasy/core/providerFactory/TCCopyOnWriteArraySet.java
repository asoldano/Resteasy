package org.jboss.resteasy.core.providerFactory;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public class TCCopyOnWriteArraySet<T> implements Set<T>
{
   private final TCCopyOnWriteArraySets<T> factory;
   
   protected TCCopyOnWriteArraySet(TCCopyOnWriteArraySets<T> factory)
   {
      this.factory = factory;
   }

   @Override
   public int size()
   {
      return factory.size(this);
   }

   @Override
   public boolean isEmpty()
   {
      return factory.isEmpty(this);
   }

   @Override
   public boolean contains(Object o)
   {
      return factory.contains(this, o);
   }

   @Override
   public Iterator<T> iterator()
   {
      return factory.iterator(this);
   }

   @Override
   public Object[] toArray()
   {
      return factory.toArray(this);
   }

   @Override
   public <R> R[] toArray(R[] a)
   {
      return factory.toArray(this, a);
   }

   @Override
   public boolean add(T e)
   {
      return factory.add(this, e);
   }

   @Override
   public boolean remove(Object o)
   {
      return factory.remove(this, o);
   }

   @Override
   public boolean containsAll(Collection<?> c)
   {
      return factory.containsAll(this, c);
   }

   @Override
   public boolean addAll(Collection<? extends T> c)
   {
      return factory.addAll(this, c);
   }

   @Override
   public boolean retainAll(Collection<?> c)
   {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean removeAll(Collection<?> c)
   {
      return factory.removeAll(this, c);
   }

   @Override
   public void clear()
   {
      factory.clear(this);
   }
}
