package org.jboss.resteasy.spi;

import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Application;

/**
 * This class is used to configure and initialize the core components of RESTEasy.
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface ResteasyDeployment
{
   public void start();

   public void merge(ResteasyDeployment other);

   public static Application createApplication(String applicationClass, Dispatcher dispatcher, ResteasyProviderFactory providerFactory)
   {
      Class<?> clazz = null;
      try
      {
         clazz = Thread.currentThread().getContextClassLoader().loadClass(applicationClass);
      }
      catch (ClassNotFoundException e)
      {
         throw new RuntimeException(e);
      }

      Application app = (Application)providerFactory.createProviderInstance(clazz);
      dispatcher.getDefaultContextObjects().put(Application.class, app);
      ResteasyProviderFactory.pushContext(Application.class, app);
      PropertyInjector propertyInjector = providerFactory.getInjectorFactory().createPropertyInjector(clazz, providerFactory);
      propertyInjector.inject(app, false);
      return app;
   }

   public void registration();

   public void stop();

   public boolean isUseContainerFormParams();

   public void setUseContainerFormParams(boolean useContainerFormParams);

   public List<String> getJndiComponentResources();

   public void setJndiComponentResources(List<String> jndiComponentResources);

   public String getApplicationClass();

   public void setApplicationClass(String applicationClass);

   public String getInjectorFactoryClass();

   public void setInjectorFactoryClass(String injectorFactoryClass);

   public boolean isDeploymentSensitiveFactoryEnabled();

   public void setDeploymentSensitiveFactoryEnabled(boolean deploymentSensitiveFactoryEnabled);

   public boolean isAsyncJobServiceEnabled();

   public void setAsyncJobServiceEnabled(boolean asyncJobServiceEnabled);

   public int getAsyncJobServiceMaxJobResults();

   public void setAsyncJobServiceMaxJobResults(int asyncJobServiceMaxJobResults);

   public long getAsyncJobServiceMaxWait();

   public void setAsyncJobServiceMaxWait(long asyncJobServiceMaxWait);

   public int getAsyncJobServiceThreadPoolSize();

   public void setAsyncJobServiceThreadPoolSize(int asyncJobServiceThreadPoolSize);

   public String getAsyncJobServiceBasePath();

   public void setAsyncJobServiceBasePath(String asyncJobServiceBasePath);

   public Application getApplication();

   public void setApplication(Application application);

   public boolean isRegisterBuiltin();

   public void setRegisterBuiltin(boolean registerBuiltin);

   public List<String> getProviderClasses();

   public void setProviderClasses(List<String> providerClasses);

   public List<Object> getProviders();

   public void setProviders(List<Object> providers);

   public List<Class> getActualProviderClasses();

   public void setActualProviderClasses(List<Class> actualProviderClasses);

   public List<Class> getActualResourceClasses();

   public void setActualResourceClasses(List<Class> actualResourceClasses);

   public boolean isSecurityEnabled();

   public void setSecurityEnabled(boolean securityEnabled);

   public List<String> getJndiResources();

   public void setJndiResources(List<String> jndiResources);

   public List<String> getResourceClasses();

   public void setResourceClasses(List<String> resourceClasses);

   public Map<String, String> getMediaTypeMappings();

   public void setMediaTypeMappings(Map<String, String> mediaTypeMappings);

   public List<Object> getResources();

   public void setResources(List<Object> resources);

   public Map<String, String> getLanguageExtensions();

   public void setLanguageExtensions(Map<String, String> languageExtensions);

   public Registry getRegistry();

   public void setRegistry(Registry registry);

   public Dispatcher getDispatcher();

   public void setDispatcher(Dispatcher dispatcher);

   public ResteasyProviderFactory getProviderFactory();

   public void setProviderFactory(ResteasyProviderFactory providerFactory);

   public void setMediaTypeParamMapping(String paramMapping);

   public List<ResourceFactory> getResourceFactories();

   public void setResourceFactories(List<ResourceFactory> resourceFactories);

   public List<String> getUnwrappedExceptions();

   public void setUnwrappedExceptions(List<String> unwrappedExceptions);

   public Map<String, String> getConstructedDefaultContextObjects();

   public void setConstructedDefaultContextObjects(Map<String, String> constructedDefaultContextObjects);

   public Map<Class, Object> getDefaultContextObjects();

   public void setDefaultContextObjects(Map<Class, Object> defaultContextObjects);

   public List<String> getScannedResourceClasses();

   public void setScannedResourceClasses(List<String> scannedResourceClasses);

   public List<String> getScannedProviderClasses();

   public void setScannedProviderClasses(List<String> scannedProviderClasses);

   public List<String> getScannedJndiComponentResources();

   public void setScannedJndiComponentResources(List<String> scannedJndiComponentResources);

   public boolean isWiderRequestMatching();

   public void setWiderRequestMatching(boolean widerRequestMatching);

   public boolean isAddCharset();

   public void setAddCharset(boolean addCharset);

   public InjectorFactory getInjectorFactory();

   public void setInjectorFactory(InjectorFactory injectorFactory);
}
