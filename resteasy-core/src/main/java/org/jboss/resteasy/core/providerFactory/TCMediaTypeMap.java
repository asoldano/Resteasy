package org.jboss.resteasy.core.providerFactory;

import java.util.List;

import javax.ws.rs.core.MediaType;

public class TCMediaTypeMap<T>
{
   private final TCMediaTypeMaps<T> factory;
   
   protected TCMediaTypeMap(TCMediaTypeMaps<T> factory)
   {
      this.factory = factory;
   }
   
   public void add(MediaType type, T obj) {
      factory.add(this, type, obj);
   }
   
   public List<T> getPossible(MediaType accept) {
      return factory.getPossible(this, accept);
   }
   
   public List<T> getPossible(MediaType accept, Class<?> type) {
      return factory.getPossible(this, accept, type);
   }
   
   //TODO useCache
}
