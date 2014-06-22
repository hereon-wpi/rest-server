package org.tango.web.server;

import fr.esrf.Tango.DevVarLongStringArray;
import fr.esrf.TangoApi.DeviceInfo;
import hzg.wpn.tango.client.proxy.TangoProxies;
import hzg.wpn.tango.client.proxy.TangoProxy;
import hzg.wpn.tango.client.proxy.TangoProxyException;

import javax.annotation.concurrent.ThreadSafe;
import java.util.Arrays;
import java.util.Collection;

/**
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 23.05.14
 */
@ThreadSafe
public class DatabaseDs {
    public static final String DEFAULT_ID = "sys/database/2";
    public static final String TANGO_DB = "tango.db";

    private final String tangoHost;
    private final TangoProxy proxy;

    public DatabaseDs(String tangoHost) throws TangoProxyException {
        this(tangoHost, DEFAULT_ID);
    }

    public DatabaseDs(String tangoHost, String devname) throws TangoProxyException {
        this.tangoHost = tangoHost;
        this.proxy = TangoProxies.newDeviceProxyWrapper("tango://" + tangoHost + "/" + devname);
    }

    public DeviceInfo getDeviceInfo(String devname) throws TangoProxyException {
        DevVarLongStringArray info = proxy.executeCommand("DbGetDeviceInfo", devname);
        DeviceInfo deviceInfo = new DeviceInfo(info);
        return deviceInfo;
    }

    public String getDeviceAddress(String devname) throws TangoProxyException {
        DeviceInfo info = getDeviceInfo(devname);
        return "tango://" + tangoHost + "/" + info.name;
    }

    public Collection<String> getDeviceList() throws TangoProxyException {
        String[] result = proxy.executeCommand("DbGetDeviceWideList", "*");
        return Arrays.asList(result);
    }
}