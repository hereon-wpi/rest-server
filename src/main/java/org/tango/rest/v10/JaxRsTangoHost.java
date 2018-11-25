package org.tango.rest.v10;

import com.google.common.collect.Lists;
import fr.esrf.Tango.DevFailed;
import org.tango.rest.v10.entities.NamedEntity;
import org.tango.rest.v10.entities.TangoHost;
import org.tango.web.server.binding.Partitionable;
import org.tango.web.server.binding.StaticValue;
import org.tango.web.server.proxy.TangoDatabaseProxy;
import org.tango.web.server.tree.DeviceFilters;
import org.tango.web.server.tree.DevicesTreeContext;

import javax.ws.rs.*;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.stream.Collectors;

import static org.tango.web.server.providers.DevicesTreeContextProvider.WILDCARD;

/**
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 11/19/18
 */
@Produces(MediaType.APPLICATION_JSON)
@Path("/hosts/{var:.+}")
public class JaxRsTangoHost {
    @Context TangoDatabaseProxy database;

    @GET
    public TangoHost get(@Context UriInfo uriInfo) throws DevFailed {
        return new TangoHost(database.getHost(), database.getPort(), database.getName(), database.getInfo(), uriInfo.getAbsolutePath());
    }

    @GET
    @StaticValue
    @Partitionable
    @Path("/devices")
    public List<NamedEntity> getDevices(@DefaultValue("*/*/*") @QueryParam("wildcard") String wildcard,
                                        @Context UriInfo uriInfo) {
        return database.getDeviceNames(wildcard).stream()
                .map(s -> new NamedEntity(s, database.getDeviceAlias(s), uriInfo.getAbsolutePathBuilder().path(s).build()))
                .collect(Collectors.toList());
    }

    @Path("/devices/tree")
    @StaticValue
    @Partitionable
    public DevicesTree getDevicesTree(@Context final UriInfo uriInfo) {
        List<String> filters = uriInfo.getQueryParameters(true).get(WILDCARD);

        DeviceFilters df = new DeviceFilters(filters);

        DevicesTreeContext context = new DevicesTreeContext(Lists.newArrayList(database), df);
        return new DevicesTree(context);
    }

    @GET
    @Path("/devices/{domain}")
    @StaticValue
    @Partitionable
    public Object getFamilies(@PathParam("domain") String domain,
                              @DefaultValue("*") @QueryParam("wildcard") String wildcard,
                              @Context final UriInfo uriInfo) throws DevFailed {
        return database.asEsrfDatabase().get_device_family(domain + "/" + wildcard);
    }

    @GET
    @Path("/devices/{domain}/{family}")
    @StaticValue
    @Partitionable
    public Object getMembers(@PathParam("domain") String domain,
                             @PathParam("family") String family,
                             @DefaultValue("*") @QueryParam("wildcard") String wildcard,
                             @Context final UriInfo uriInfo) throws DevFailed {
        return database.asEsrfDatabase().get_device_member(domain + "/" + family + "/" + wildcard);
    }

    @Path("/devices/{domain}/{family}/{member}")
    public JaxRsDevice getDevice(@Context ResourceContext rc) {
        return rc.getResource(JaxRsDevice.class);
    }



    @Path("/devices/tree")
    public DevicesTree getDevicesTreeForHost() {
        return new DevicesTree();
    }
}
