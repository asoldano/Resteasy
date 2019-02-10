package org.jboss.resteasy.core.providerFactory;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

public class TCConcurrentHashMap<K, V> implements ConcurrentMap<K, V>
{
   private final TCConcurrentHashMaps<K, V> factory;
   
   protected TCConcurrentHashMap(TCConcurrentHashMaps<K, V> factory)
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
   public boolean containsKey(Object key)
   {
      return factory.containsKey(this, key);
   }

   @Override
   public boolean containsValue(Object value)
   {
      return factory.containsValue(this, value);
   }

   @Override
   public V get(Object key)
   {
      return factory.get(this, key);
   }

   @Override
   public V put(K key, V value)
   {
      return factory.put(this, key, value);
   }

   @Override
   public V remove(Object key)
   {
      return factory.remove(this, key);
   }

   @Override
   public void putAll(Map<? extends K, ? extends V> m)
   {
      factory.putAll(this, m);
   }

   @Override
   public void clear()
   {
      factory.clear(this);
   }

   @Override
   public Set<K> keySet()
   {
      return factory.keySet(this);
   }

   @Override
   public Collection<V> values()
   {
      return factory.values(this);
   }

   @Override
   public Set<java.util.Map.Entry<K, V>> entrySet()
   {
      return factory.entrySet(this);
   }

   @Override
   public V putIfAbsent(K key, V value)
   {
      return factory.putIfAbsent(this, key, value);
   }

   @Override
   public boolean remove(Object key, Object value)
   {
      return factory.remove(this, key, value);
   }

   @Override
   public boolean replace(K key, V oldValue, V newValue)
   {
      return factory.replace(this, key, oldValue, newValue);
   }

   @Override
   public V replace(K key, V value)
   {
      return factory.replace(this, key, value);
   }
   
}
