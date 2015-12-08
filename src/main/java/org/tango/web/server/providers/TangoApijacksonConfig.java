package org.tango.web.server.providers;

import fr.esrf.Tango.AttrDataFormat;
import fr.esrf.Tango.AttrWriteType;
import fr.esrf.Tango.DevFailed;
import fr.esrf.Tango.DispLevel;
import fr.esrf.TangoApi.PipeBlob;
import fr.esrf.TangoApi.PipeDataElement;
import fr.esrf.TangoDs.TangoConst;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.Version;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.module.SimpleModule;
import org.codehaus.jackson.type.JavaType;
import org.jacorb.notification.util.WeakHashSet;
import org.tango.client.ez.util.TangoUtils;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 07.12.2015
 */
@Provider
@Produces(MediaType.APPLICATION_JSON)
public class TangoApiJacksonConfig implements ContextResolver<ObjectMapper>
{
    private ObjectMapper objectMapper;


    public TangoApiJacksonConfig() throws Exception
    {
        objectMapper = new ObjectMapper();
        // Set human readable date format
        SimpleModule tangoModule = new SimpleModule("MyModule", new Version(1, 9, 12, null));
        tangoModule.addSerializer(new AttrWriteTypeSerializer(AttrWriteType.class));
        tangoModule.addSerializer(new AttrDataFormatSerializer(AttrDataFormat.class));
        tangoModule.addSerializer(new DispLevelSerializer(DispLevel.class));
        tangoModule.addSerializer(new PipeBlobSerializer(PipeBlob.class));
        objectMapper.registerModule(tangoModule);
    }


    public ObjectMapper getContext(Class<?> objectType)
    {
        return objectMapper;
    }

    public static class AttrWriteTypeSerializer extends org.codehaus.jackson.map.ser.std.SerializerBase<AttrWriteType> {
        public AttrWriteTypeSerializer(Class<AttrWriteType> t) {
            super(t);
        }

        public AttrWriteTypeSerializer(JavaType type) {
            super(type);
        }

        public AttrWriteTypeSerializer(Class<?> t, boolean dummy) {
            super(t, dummy);
        }

        @Override
        public void serialize(AttrWriteType value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
            jgen.writeString(value.toString());
        }
    }

    public static class AttrDataFormatSerializer extends org.codehaus.jackson.map.ser.std.SerializerBase<AttrDataFormat> {
        public AttrDataFormatSerializer(Class<AttrDataFormat> t) {
            super(t);
        }

        public AttrDataFormatSerializer(JavaType type) {
            super(type);
        }

        public AttrDataFormatSerializer(Class<?> t, boolean dummy) {
            super(t, dummy);
        }

        @Override
        public void serialize(AttrDataFormat value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
            jgen.writeString(value.toString());
        }
    }


    public static class DispLevelSerializer extends org.codehaus.jackson.map.ser.std.SerializerBase<DispLevel> {
        public DispLevelSerializer(Class<DispLevel> t) {
            super(t);
        }

        public DispLevelSerializer(JavaType type) {
            super(type);
        }

        public DispLevelSerializer(Class<?> t, boolean dummy) {
            super(t, dummy);
        }

        @Override
        public void serialize(DispLevel value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
            jgen.writeString(value.toString());
        }
    }

    public static class PipeBlobSerializer extends org.codehaus.jackson.map.ser.std.SerializerBase<PipeBlob> {
        public PipeBlobSerializer(Class<PipeBlob> t) {
            super(t);
        }

        public PipeBlobSerializer(JavaType type) {
            super(type);
        }

        public PipeBlobSerializer(Class<?> t, boolean dummy) {
            super(t, dummy);
        }

        @Override
        public void serialize(PipeBlob value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
            try {
                jgen.writeStartArray();
                for(PipeDataElement element : value){
                    jgen.writeStartObject();
                    jgen.writeObjectField("name", element.getName());
                    switch (element.getType()){
                        case TangoConst.Tango_DEV_PIPE_BLOB:
                            jgen.writeObjectField("value", element.extractPipeBlob());
                            break;
                        case TangoConst.Tango_DEV_BOOLEAN:
                            jgen.writeObjectField("value", element.extractBooleanArray());
                            break;
                        case TangoConst.Tango_DEV_SHORT:
                            jgen.writeObjectField("value", element.extractShortArray());
                            break;
                        case TangoConst.Tango_DEV_LONG:
                            jgen.writeObjectField("value", element.extractLongArray());
                            break;
                        case TangoConst.Tango_DEV_LONG64:
                            jgen.writeObjectField("value", element.extractLong64Array());
                            break;
                        case TangoConst.Tango_DEV_FLOAT:
                            jgen.writeObjectField("value", element.extractFloatArray());
                            break;
                        case TangoConst.Tango_DEV_DOUBLE:
                            jgen.writeObjectField("value", element.extractDoubleArray());
                            break;
                        case TangoConst.Tango_DEV_STRING:
                            jgen.writeObjectField("value", element.extractStringArray());
                            break;
                        case TangoConst.Tango_DEV_USHORT:
                            jgen.writeObjectField("value", element.extractUShortArray());
                            break;
                        case TangoConst.Tango_DEV_ULONG:
                            jgen.writeObjectField("value", element.extractULongArray());
                            break;
                        case TangoConst.Tango_DEV_ULONG64:
                            jgen.writeObjectField("value", element.extractULong64Array());
                            break;
                        case TangoConst.Tango_DEV_STATE:
                            jgen.writeObjectField("value", element.extractDevStateArray());
                            break;
                        //TODO ?!
                    }
                    jgen.writeEndObject();
                }
                jgen.writeEndArray();
            } catch (DevFailed devFailed) {
                throw new IOException(TangoUtils.convertDevFailedToException(devFailed));
            }
        }
    }
}
