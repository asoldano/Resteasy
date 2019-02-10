package org.jboss.resteasy.core.providerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class TCConcurrentHashMaps<K, V> extends AbstractTCFactory<ConcurrentHashMap<K, V>, TCConcurrentHashMap<K, V>>
{
   public TCConcurrentHashMaps(int initialCapacity)
   {
      super(initialCapacity);
   }

   @Override
   protected TCConcurrentHashMap<K, V> newO()
   {
      return new TCConcurrentHashMap<>(this);
   }

   @Override
   protected ConcurrentHashMap<K, V> clone(ConcurrentHashMap<K, V> toBeCopied)
   {
      return new ConcurrentHashMap<>(toBeCopied);
   }
   
   public int size(TCConcurrentHashMap<K, V> key)
   {
      return getData(key).size();
   }
   
   public boolean isEmpty(TCConcurrentHashMap<K, V> key)
   {
      return getData(key).isEmpty();
   }
   
   public boolean containsKey(TCConcurrentHashMap<K, V> key, Object k)
   {
      return getData(key).containsKey(k);
   }
   
   public boolean containsValue(TCConcurrentHashMap<K, V> key, Object value)
   {
      return getData(key).containsValue(value);
   }
   
   public V get(TCConcurrentHashMap<K, V> key, Object k)
   {
      return getData(key).get(k);
   }
   
   public V put(TCConcurrentHashMap<K, V> key, K k, V value)
   {
      prepareForModification(key);
      return getData(key).put(k, value);
   }
   
   public V remove(TCConcurrentHashMap<K, V> key, Object k)
   {
      prepareForModification(key);
      return getData(key).remove(k);
   }
   
   public void putAll(TCConcurrentHashMap<K, V> key, Map<? extends K, ? extends V> m)
   {
      prepareForModification(key);
      getData(key).putAll(m);
   }

   public void clear(TCConcurrentHashMap<K, V> key)
   {
      prepareForModification(key);
      getData(key).clear();
   }
   
   public Set<K> keySet(TCConcurrentHashMap<K, V> key)
   {
      //TODO: offer an option for getting a modifiable collection that either triggers cloning upfront or when really needed
      return Collections.unmodifiableSet(getData(key).keySet());
   }
   
   public Collection<V> values(TCConcurrentHashMap<K, V> key)
   {
      //TODO: offer an option for getting a modifiable collection that either triggers cloning upfront or when really needed
      return Collections.unmodifiableCollection(getData(key).values());
   }
   
   public Set<java.util.Map.Entry<K, V>> entrySet(TCConcurrentHashMap<K, V> key)
   {
      //TODO: offer an option for getting a modifiable collection that either triggers cloning upfront or when really needed
      return Collections.unmodifiableSet(getData(key).entrySet());
   }
   
   public V putIfAbsent(TCConcurrentHashMap<K, V> key, K k, V value)
   {
      prepareForModification(key);
      return getData(key).putIfAbsent(k, value);
   }
   
   public boolean remove(TCConcurrentHashMap<K, V> key, Object k, Object value)
   {
      prepareForModification(key);
      return getData(key).remove(k, value);
   }
   
   public boolean replace(TCConcurrentHashMap<K, V> key, K k, V oldValue, V newValue)
   {
      prepareForModification(key);
      return getData(key).replace(k, oldValue, newValue);
   }
   
   public V replace(TCConcurrentHashMap<K, V> key, K k, V value)
   {
      prepareForModification(key);
      return getData(key).replace(k, value);
   }

}
