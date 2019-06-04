package org.jboss.resteasy.plugins.providers.jackson;

import java.lang.annotation.Annotation;

/**
 * This is derived from com.fasterxml.jackson.jaxrs.cfg.AnnotationBundleKey
 */
final class AnnotationBundleKey
{
   private static final Annotation[] NO_ANNOTATIONS = new Annotation[0];

   private final Annotation[] _annotations;

   /**
    * We also seem to need the type as part of the key (as per [Issue#11]);
    * hopefully that and annotations are enough (if not, may need to reconsider
    * the way caching is done, and possibly only cache derivation of annotations,
    * not mapper or reader/writer).
    */
   private final Class<?> _type;

   private final int _hashCode;

   /*
   /**********************************************************
   /* Construction
   /**********************************************************
    */

   AnnotationBundleKey(final Annotation[] annotations, final Class<?> type)
   {
      _type = type;
      // getting hash of name is faster than Class.hashCode() just because latter uses system identity hash:
      final int typeHash = type.getName().hashCode();
      if (annotations == null || annotations.length == 0)
      {
         _annotations = NO_ANNOTATIONS;
         _hashCode = typeHash;
      }
      else
      {
         _annotations = annotations;
         _hashCode = calcHash(annotations) ^ typeHash;
      }
   }

   private static int calcHash(Annotation[] annotations)
   {
      /* hmmh. Can't just base on Annotation type; chances are that Annotation
       * instances use identity hash, which has to do.
       */
      final int len = annotations.length;
      int hash = len;
      for (int i = 0; i < len; ++i)
      {
         hash = (hash * 31) + annotations[i].hashCode();
      }
      return hash;
   }

   /*
   /**********************************************************
   /* Overridden methods
   /**********************************************************
    */

   @Override
   public int hashCode()
   {
      return _hashCode;
   }

   @Override
   public String toString()
   {
      return "[Annotations: " + _annotations.length + ", type: " + _type.getName() + ", hash 0x"
            + Integer.toHexString(_hashCode) + "]";
   }

   @Override
   public boolean equals(Object o)
   {
      if (o == this)
         return true;
      if (o == null)
         return false;
      if (o.getClass() != getClass())
         return false;
      AnnotationBundleKey other = (AnnotationBundleKey) o;
      if ((other._hashCode != _hashCode) || (other._type != _type))
      {
         return false;
      }
      return _equals(other._annotations);
   }

   private boolean _equals(Annotation[] otherAnn)
   {
      final int len = _annotations.length;
      if (otherAnn.length != len)
      {
         return false;
      }

      // 05-May-2019, tatu: If we wanted to true equality of contents we should
      //   do order-insensitive check; however, our use case is not unifying all
      //   possible permutations but rather trying to ensure that caching of same
      //   method signature is likely to match. So false negatives are acceptable
      //   over having to do order-insensitive comparison.

      switch (len)
      {
         default :
            for (int i = 0; i < len; ++i)
            {
               if (!_annotations[i].equals(otherAnn[i]))
               {
                  return false;
               }
            }
            return true;

         case 3 :
            if (!_annotations[2].equals(otherAnn[2]))
            {
               return false;
            }
            // fall through
         case 2 :
            if (!_annotations[1].equals(otherAnn[1]))
            {
               return false;
            }
            // fall through
         case 1 :
            if (!_annotations[0].equals(otherAnn[0]))
            {
               return false;
            }
            // fall through
         case 0 :
      }
      return true;
   }
}
