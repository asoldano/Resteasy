package org.jboss.resteasy.core.providerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Abstract Tree Copies Factory
 * 
 * @author asoldano
 *
 * @param <D> Underlying Data (usually the collection that's memory expensive to copy)
 * @param <O> Tree Copies Object (basically the key for accessing the data as well as the object hiding the TC retrieval complexity)
 */
public abstract class AbstractTCFactory<D,O>
{
   private static class DataNode<D,O> {
      public D data;
      public O parent;
      public List<O> children;
      
      public DataNode(D data, O parent)
      {
         this.data = data;
         this.parent = parent;
      }
      
      public void addChild(O o) {
         if (children == null) {
            children = new ArrayList<>();
         }
         children.add(o);
      }
   }
   
   private final Map<O, DataNode<D,O>> dataStore;
   
   public AbstractTCFactory(int initialCapacity) {
      dataStore = new ConcurrentHashMap<>(initialCapacity);
   }
   
   /**
    * Creates a new instance of the TC object
    */
   protected abstract O newO();
   
   /**
    * Clones the underlying data
    * 
    * @param toBeCopied
    * @return
    */
   protected abstract D clone(D toBeCopied);
   
   /**
    * Creates the first TC Object by wrapping the given data
    * 
    * @param data
    * @return
    */
   public O wrapData(D data) {
      O s = newO();
      dataStore.put(s, new DataNode<>(data, null));
      return s;
   }
   
   /**
    * Creates a new TC Object by copying/inheriting from another TC Object
    * 
    * @param parent
    * @return
    */
   public O copyFrom(O parent) {
      O s = newO();
      dataStore.put(s, new DataNode<>(null, parent));
      dataStore.get(parent).addChild(s);
      return s;
   }
   
   protected D getData(O key) {
      DataNode<D,O> n = dataStore.get(key);
      if (n == null) {
         throw new IllegalArgumentException("Cannot find data for key " + key);
      }
      while (n.data == null) {
         n = dataStore.get(n.parent);
      }
      return n.data;
   }
   
   protected void prepareForModification(O o)
   {
      DataNode<D,O> dn = dataStore.get(o);
      if (dn.parent != null)
      {
         //clone data from parent
         DataNode<D,O> dnp = dataStore.get(dn.parent);
         dn.data = clone(dnp.data);
         //move children under parent
         if (dn.children != null) {
            for (O c : dn.children) {
               dataStore.get(c).parent = dn.parent;
               dnp.addChild(c);
            }
            dn.children.clear();
         }
         //unbind from parent
         dnp.children.remove(o);
         dn.parent = null;
      }
      else
      {
         //detach children
         if (dn.children != null) {
            for (O c : dn.children) {
               DataNode<D,O> dnc = dataStore.get(c);
               dnc.parent = null;
               dnc.data = clone(dn.data);
            }
            dn.children.clear();
         }
      }
   }
   
}
