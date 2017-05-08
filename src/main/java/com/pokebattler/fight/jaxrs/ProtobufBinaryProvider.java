package com.pokebattler.fight.jaxrs;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import javax.ws.rs.Consumes;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;

@Provider
@Produces(ProtobufBinaryProvider.TYPE_APPLICATION_X_PROTOBUF)
public class ProtobufBinaryProvider implements MessageBodyWriter<Message> {
	public static final String TYPE_APPLICATION_X_PROTOBUF = "application/x-protobuf";
    JsonFormat.Printer printer = JsonFormat.printer().omittingInsignificantWhitespace();
    JsonFormat.Parser parser = JsonFormat.parser();
    Logger log = LoggerFactory.getLogger(getClass());

    public ProtobufBinaryProvider() {
        super();
        log.info("Registered");
    }

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return (Message.class.isAssignableFrom(type) || Message.Builder.class.isAssignableFrom(type));
    }

    @Override
    public long getSize(Message myBean, Class<?> type, Type genericType, Annotation[] annotations,
            MediaType mediaType) {
        return 0;
    }

    @Override
    public void writeTo(Message myBean, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
            throws IOException, WebApplicationException {
        try {
        	myBean.writeTo(entityStream);
        } catch (final Exception e) {
            log.error("Could not write?", e);
            throw new ProcessingException("Error serializing proto");
        }
    }
    //Note to support readFrom, you need to know the class of the proto, probably from a header

}