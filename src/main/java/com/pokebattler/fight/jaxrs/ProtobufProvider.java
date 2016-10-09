package com.pokebattler.fight.jaxrs;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

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
@Produces("application/json")
public class ProtobufProvider implements MessageBodyReader<Message>, MessageBodyWriter<Message> {
    JsonFormat.Printer printer = JsonFormat.printer().omittingInsignificantWhitespace();
    JsonFormat.Parser parser = JsonFormat.parser();
    Logger log = LoggerFactory.getLogger(getClass());

    public ProtobufProvider() {
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
        try (OutputStreamWriter writer = new OutputStreamWriter(entityStream)) {
            printer.appendTo(myBean, writer);
        } catch (final Exception e) {
            log.error("Could not write?", e);
            throw new ProcessingException("Erorr serializing proto");
        }
    }

    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return (Message.class.isAssignableFrom(type) || Message.Builder.class.isAssignableFrom(type));
    }

    @Override
    public Message readFrom(Class<Message> type, Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
            throws IOException, WebApplicationException {
        try (InputStreamReader reader = new InputStreamReader(entityStream)){
            final Method m = type.getMethod("newBuilder");
            final Message.Builder builder = (Message.Builder) m.invoke(null);
            parser.merge(reader, builder);

            return builder.build();
        } catch (final Exception e) {
            log.error("Could not read?", e);
            // TODO Auto-generated catch block
            throw new ProcessingException("Error deserializing proto");
        }

    }
}