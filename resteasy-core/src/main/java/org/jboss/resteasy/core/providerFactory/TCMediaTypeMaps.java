package org.jboss.resteasy.core.providerFactory;

import java.util.List;

import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.core.MediaTypeMap;

public class TCMediaTypeMaps<T> extends AbstractTCFactory<MediaTypeMap<T>, TCMediaTypeMap<T>>
{
   public TCMediaTypeMaps(int initialCapacity)
   {
      super(initialCapacity);
   }

   @Override
   protected TCMediaTypeMap<T> newO()
   {
      return new TCMediaTypeMap<>(this);
   }

   @Override
   protected MediaTypeMap<T> clone(MediaTypeMap<T> toBeCopied)
   {
      return toBeCopied.clone();
   }
   
   public void add(TCMediaTypeMap<T> key, MediaType type, T obj) {
      prepareForModification(key);
      getData(key).add(type, obj);
   }
   
   public List<T> getPossible(TCMediaTypeMap<T> key, MediaType accept) {
      return getData(key).getPossible(accept);
   }
   
   public List<T> getPossible(TCMediaTypeMap<T> key, MediaType accept, Class<?> type) {
      return getData(key).getPossible(accept, type);
   }

}
