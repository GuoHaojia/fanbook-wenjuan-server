package com.tduck.cloud.api.config;

import com.alibaba.fastjson.serializer.ToStringSerializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Cool
 * @version 1.0
 * @email 861917988@qq.com
 * @data 2022/10/29 22:49
 */
@Configuration
public class JacksonConfig {
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        return jacksonObjectMapperBuilder -> jacksonObjectMapperBuilder
                .serializerByType(Long.class, com.fasterxml.jackson.databind.ser.std.ToStringSerializer.instance)
                .serializerByType(Long.TYPE, com.fasterxml.jackson.databind.ser.std.ToStringSerializer.instance);
    }
}
