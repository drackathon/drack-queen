package org.drack.hackathon.config;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import org.drack.hackathon.service.PetsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zalando.problem.ProblemModule;

import java.io.IOException;

import static com.google.common.base.Preconditions.checkNotNull;

public final class ConfigUtils {

    private ConfigUtils(){}

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigUtils.class);

    public static void configureJsonObjectMapper(final ObjectMapper mapper) {
        checkNotNull(mapper, "ObjectMapper must not be null");

        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
        mapper.registerModule(new Jdk8Module());
        mapper.registerModule(new ProblemModule());
        mapper.registerSubtypes(PetsService.NotSupportedException.class);

        mapper.addHandler(new DeserializationProblemHandler() {
            @Override
            public boolean handleUnknownProperty(final DeserializationContext ctxt,
                                                 final JsonParser jp,
                                                 final JsonDeserializer<?> deserializer,
                                                 final Object beanOrClass,
                                                 final String propertyName)
                    throws IOException {

                ctxt.getParser().skipChildren(); // avoid endless cycling

                LOGGER.warn("unknown property occurred in JSON representation: [beanOrClass={}, property={}]",
                             beanOrClass, propertyName);

                return true; // problem is considered as resolved
            }
        });
    }
}
