package org.drack.hackathon.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
class CommonConfig {
    @Autowired
    public void configureJsonObjectMapper(final ObjectMapper mapper) {
        ConfigUtils.configureJsonObjectMapper(mapper);
    }
}
