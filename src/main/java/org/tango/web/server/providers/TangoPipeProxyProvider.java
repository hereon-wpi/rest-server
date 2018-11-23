package org.tango.web.server.providers;

import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.tango.rest.rc4.entities.Failures;
import org.tango.web.server.binding.RequiresTangoPipe;
import org.tango.web.server.proxy.*;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.Objects;

/**
 * @author ingvord
 * @since 11/19/18
 */
@Provider
@RequiresTangoPipe
@Priority(Priorities.USER + 300)
public class TangoPipeProxyProvider implements ContainerRequestFilter {
    @Override
    public void filter(ContainerRequestContext containerRequestContext) throws IOException {
        UriInfo uriInfo = containerRequestContext.getUriInfo();

        String name = uriInfo.getPathParameters().getFirst("pipe");
        if(Objects.isNull(name)) {
            containerRequestContext.abortWith(Response.status(Response.Status.BAD_REQUEST).entity(Failures.createInstance("attribute name is null")).build());
            throw new AssertionError();
        }

        TangoDatabaseProxy databaseProxy = ResteasyProviderFactory.getContextData(TangoDatabaseProxy.class);
        if(Objects.isNull(databaseProxy)) {
            containerRequestContext.abortWith(Response.status(Response.Status.BAD_REQUEST).entity(Failures.createInstance("deviceProxy is null")).build());
            throw new AssertionError();
        }

        TangoDeviceProxy deviceProxy = ResteasyProviderFactory.getContextData(TangoDeviceProxy.class);
        if(Objects.isNull(deviceProxy)) {
            containerRequestContext.abortWith(Response.status(Response.Status.BAD_REQUEST).entity(Failures.createInstance("deviceProxy is null")).build());
            throw new AssertionError();
        }

        TangoPipeProxy proxy = new TangoPipeProxyImpl(databaseProxy.getTangoHost(), deviceProxy.getName(), name, deviceProxy.getProxy().toDeviceProxy());
        ResteasyProviderFactory.pushContext(TangoPipeProxy.class, proxy);
    }
}
