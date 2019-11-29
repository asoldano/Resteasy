package org.jboss.resteasy.spi;

public interface ResteasyProviderFactoryBuilder
{
   enum Strategy {
      THREAD_STRATEGY, TCCL_STRATEGY
   };

   ResteasyProviderFactory newInstance(boolean registerBuiltin);

   Strategy getStrategy();

   void setStrategy(Strategy strategy);
}
