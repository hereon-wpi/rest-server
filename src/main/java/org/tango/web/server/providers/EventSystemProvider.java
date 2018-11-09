package org.tango.web.server.providers;

import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.tango.web.server.binding.EventSystem;
import org.tango.web.server.event.EventsManager;
import org.tango.web.server.event.SubscriptionsContext;
import org.tango.web.server.event.TangoSseBroadcasterFactory;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.sse.Sse;
import java.io.IOException;

/**
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 11/8/18
 */
@Provider
@PreMatching
@EventSystem
public class EventSystemProvider implements ContainerRequestFilter {
    private EventsManager context;
    private SubscriptionsContext subscriptions;

    @Context
    public void setSse(Sse sse) {
        this.context = new EventsManager(new TangoSseBroadcasterFactory(sse));
        this.subscriptions = new SubscriptionsContext();
    }



    @Override
    public void filter(ContainerRequestContext requestContext) {
        ResteasyProviderFactory.pushContext(EventsManager.class,
                context);
        ResteasyProviderFactory.pushContext(SubscriptionsContext.class,
                subscriptions);
    }
}