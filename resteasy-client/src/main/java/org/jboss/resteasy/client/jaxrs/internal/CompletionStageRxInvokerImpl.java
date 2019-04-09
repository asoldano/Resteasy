package org.jboss.resteasy.client.jaxrs.internal;

import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutorService;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.client.CompletionStageRxInvoker;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.SyncInvoker;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

/**
 *
 * @author <a href="mailto:ron.sigal@jboss.com">Ron Sigal</a>
 * @author <a href="mailto:alessio.soldano@jboss.com">Alessio Soldano</a>
 * <p>
 * Date March 9, 2016
 */
public class CompletionStageRxInvokerImpl implements CompletionStageRxInvoker
{
   private final ClientInvocationBuilder builder;

   private final ExecutorService executor;

   public CompletionStageRxInvokerImpl(final SyncInvoker builder)
   {
      this(builder, null);
   }

   public CompletionStageRxInvokerImpl(final SyncInvoker builder, final ExecutorService executor)
   {
      this.builder = (ClientInvocationBuilder)builder;
      this.executor = executor;
   }

   private ClientInvocation createClientInvocation(String method, Entity<?> entity)
   {
      ClientInvocation invocation = builder.createClientInvocation(builder.invocation);
      invocation.setMethod(method);
      invocation.setEntity(entity);
      return invocation;
   }

   @Override
   public CompletionStage<Response> get()
   {
      return createClientInvocation(HttpMethod.GET, null).submit();
   }

   @Override
   public <T> CompletionStage<T> get(Class<T> responseType)
   {
      return createClientInvocation(HttpMethod.GET, null).submit(responseType);
   }

   @Override
   public <T> CompletionStage<T> get(GenericType<T> responseType)
   {
      return createClientInvocation(HttpMethod.GET, null).submit(responseType);
   }

   @Override
   public CompletionStage<Response> put(Entity<?> entity)
   {
      return createClientInvocation(HttpMethod.PUT, entity).submit();
   }

   @Override
   public <T> CompletionStage<T> put(Entity<?> entity, Class<T> clazz)
   {
      return createClientInvocation(HttpMethod.PUT, entity).submit(clazz);
   }

   @Override
   public <T> CompletionStage<T> put(Entity<?> entity, GenericType<T> type)
   {
      return createClientInvocation(HttpMethod.PUT, entity).submit(type);
   }

   @Override
   public CompletionStage<Response> post(Entity<?> entity)
   {
      return createClientInvocation(HttpMethod.POST, entity).submit();
   }

   @Override
   public <T> CompletionStage<T> post(Entity<?> entity, Class<T> clazz)
   {
      return createClientInvocation(HttpMethod.POST, entity).submit(clazz);
   }

   @Override
   public <T> CompletionStage<T> post(Entity<?> entity, GenericType<T> type)
   {
      return createClientInvocation(HttpMethod.POST, entity).submit(type);
   }

   @Override
   public CompletionStage<Response> delete()
   {
      return createClientInvocation(HttpMethod.DELETE, null).submit();
   }

   @Override
   public <T> CompletionStage<T> delete(Class<T> responseType)
   {
      return createClientInvocation(HttpMethod.DELETE, null).submit(responseType);
   }

   @Override
   public <T> CompletionStage<T> delete(GenericType<T> responseType)
   {
      return createClientInvocation(HttpMethod.DELETE, null).submit(responseType);
   }

   @Override
   public CompletionStage<Response> head()
   {
      return createClientInvocation(HttpMethod.HEAD, null).submit();
   }

   @Override
   public CompletionStage<Response> options()
   {
      return createClientInvocation(HttpMethod.OPTIONS, null).submit();
   }

   @Override
   public <T> CompletionStage<T> options(Class<T> responseType)
   {
      return createClientInvocation(HttpMethod.OPTIONS, null).submit(responseType);
   }

   @Override
   public <T> CompletionStage<T> options(GenericType<T> responseType)
   {
      return createClientInvocation(HttpMethod.OPTIONS, null).submit(responseType);
   }

   @Override
   public CompletionStage<Response> trace()
   {
      return createClientInvocation("TRACE", null).submit();
   }

   @Override
   public <T> CompletionStage<T> trace(Class<T> responseType)
   {
      return createClientInvocation("TRACE", null).submit(responseType);
   }

   @Override
   public <T> CompletionStage<T> trace(GenericType<T> responseType)
   {
      return createClientInvocation("TRACE", null).submit(responseType);
   }

   @Override
   public CompletionStage<Response> method(String name)
   {
      return createClientInvocation(name, null).submit();
   }

   @Override
   public <T> CompletionStage<T> method(String name, Class<T> responseType)
   {
      return createClientInvocation(name, null).submit(responseType);
   }

   @Override
   public <T> CompletionStage<T> method(String name, GenericType<T> responseType)
   {
      return createClientInvocation(name, null).submit(responseType);
   }

   @Override
   public CompletionStage<Response> method(String name, Entity<?> entity)
   {
      return createClientInvocation(name, entity).submit();
   }

   @Override
   public <T> CompletionStage<T> method(String name, Entity<?> entity, Class<T> responseType)
   {
      return createClientInvocation(name, entity).submit(responseType);
   }

   @Override
   public <T> CompletionStage<T> method(String name, Entity<?> entity, GenericType<T> responseType)
   {
      return createClientInvocation(name, entity).submit(responseType);
   }

   public ExecutorService getExecutor()
   {
      return executor;
   }

   public CompletionStage<Response> patch(Entity<?> entity)
   {
      return createClientInvocation(HttpMethod.PATCH, entity).submit();
   }

   public <T> CompletionStage<T> patch(Entity<?> entity, Class<T> responseType)
   {
      return createClientInvocation(HttpMethod.PATCH, entity).submit(responseType);
   }

   public <T> CompletionStage<T> patch(Entity<?> entity, GenericType<T> responseType)
   {
      return createClientInvocation(HttpMethod.PATCH, entity).submit(responseType);
   }

}
