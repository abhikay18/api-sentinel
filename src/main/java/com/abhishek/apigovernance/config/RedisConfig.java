package com.abhishek.apigovernance.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.data.redis.serializer.GenericToStringSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Integer> redisTemplate(
            RedisConnectionFactory connectionFactory) {

        RedisTemplate<String, Integer> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Key as String
        template.setKeySerializer(new StringRedisSerializer());

        // Value as Integer (so DECR works)
        template.setValueSerializer(new GenericToStringSerializer<>(Integer.class));

        template.afterPropertiesSet();
        return template;
    }
}
