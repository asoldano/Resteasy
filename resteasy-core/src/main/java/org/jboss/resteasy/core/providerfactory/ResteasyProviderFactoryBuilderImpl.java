package org.jboss.resteasy.core.providerfactory;

import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.ResteasyProviderFactoryBuilder;

public class ResteasyProviderFactoryBuilderImpl implements ResteasyProviderFactoryBuilder
{

   private volatile Strategy strategy = null;

   @Override
   public ResteasyProviderFactory newInstance(boolean registerBuiltin)
   {
      ResteasyProviderFactoryImpl rpf = new ResteasyProviderFactoryImpl();
      if (registerBuiltin) {
         rpf.registerBuiltin();
      }
      return rpf;
   }

   @Override
   public Strategy getStrategy()
   {
      // TODO use MP Config
      return strategy != null ? strategy : Strategy.TCCL_STRATEGY;
   }

   @Override
   public void setStrategy(Strategy strategy)
   {
      this.strategy = strategy;
   }

}
