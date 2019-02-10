package org.jboss.resteasy.core.providerFactory;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Consumer;

public class TCCopyOnWriteArraySets<T> extends AbstractTCFactory<CopyOnWriteArraySet<T>, TCCopyOnWriteArraySet<T>>
{
   public TCCopyOnWriteArraySets(int initialCapacity)
   {
      super(initialCapacity);
   }

   @Override
   protected TCCopyOnWriteArraySet<T> newO()
   {
      return new TCCopyOnWriteArraySet<>(this);
   }

   @Override
   protected CopyOnWriteArraySet<T> clone(CopyOnWriteArraySet<T> toBeCopied)
   {
      return new CopyOnWriteArraySet<>(toBeCopied);
   }
   
   public int size(TCCopyOnWriteArraySet<T> key)
   {
      return getData(key).size();
   }

   public boolean isEmpty(TCCopyOnWriteArraySet<T> key)
   {
      return getData(key).isEmpty();
   }

   public boolean contains(TCCopyOnWriteArraySet<T> key, Object o)
   {
      return getData(key).contains(o);
   }
   
   public Iterator<T> iterator(TCCopyOnWriteArraySet<T> key)
   {
      return new Iterator<T>() {
         private final Iterator<? extends T> i = getData(key).iterator();

         public boolean hasNext() {return i.hasNext();}
         public T next()          {return i.next();}
         public void remove() {
             throw new UnsupportedOperationException();
         }
         @Override
         public void forEachRemaining(Consumer<? super T> action) {
             i.forEachRemaining(action);
         }
     };
   }

   public Object[] toArray(TCCopyOnWriteArraySet<T> key)
   {
      return getData(key).toArray();
   }

   public <R> R[] toArray(TCCopyOnWriteArraySet<T> key, R[] a)
   {
      return getData(key).toArray(a);
   }

   public boolean add(TCCopyOnWriteArraySet<T> key, T e)
   {
      prepareForModification(key);
      return getData(key).add(e);
   }

   public boolean remove(TCCopyOnWriteArraySet<T> key, Object o)
   {
      prepareForModification(key);
      return getData(key).remove(o);
   }

   public boolean containsAll(TCCopyOnWriteArraySet<T> key, Collection<?> c)
   {
      return getData(key).containsAll(c);
   }

   public boolean addAll(TCCopyOnWriteArraySet<T> key, Collection<? extends T> c)
   {
      prepareForModification(key);
      return getData(key).addAll(c);
   }

   public boolean removeAll(TCCopyOnWriteArraySet<T> key, Collection<?> c)
   {
      prepareForModification(key);
      return getData(key).removeAll(c);
   }

   public void clear(TCCopyOnWriteArraySet<T> key)
   {
      prepareForModification(key);
      getData(key).clear();
   }

   
}
