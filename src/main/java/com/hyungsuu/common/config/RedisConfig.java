package com.hyungsuu.common.config;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;

import com.hyungsuu.apigate.samaple.web.UserController;

import io.lettuce.core.api.StatefulConnection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
@Slf4j
@Configuration
@PropertySource({ "classpath:/application.properties", "classpath:/ecid/egovProps/globals.properties" })
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;
   
 //   @Value("${redis.password}")
//    private String password;
//    @Value("${redis.clientName}")
//    private String clientName;
    
    @Value("${spring.data.redis.lettuce.pool.max-active}")
    private int maxTotal;
    @Value("${spring.data.redis.lettuce.pool.max-idle}")
    private int maxIdle;
    @Value("${spring.data.redis.lettuce.pool.min-idle}")
    private int minIdle;
    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        log.info("host: {}", host);
        log.info("port: {}", port);

        // Redis Config
        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration(host, port);
//        redisConfig.setPassword(password);

        // Client Config
        // Connection Pool을 사용하지 않을 경우 이 설정 사용
//        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
//                .clientName(clientName)
//                .build();
        // Connection Pool을 사용할 경우 이 설정 사용
        GenericObjectPoolConfig<StatefulConnection<?, ?>> genericObjectPoolConfig = new GenericObjectPoolConfig<StatefulConnection<?, ?>>();
        genericObjectPoolConfig.setMaxTotal(10000);
        genericObjectPoolConfig.setMaxIdle(10000);
        genericObjectPoolConfig.setMinIdle(10);
		
        LettucePoolingClientConfiguration clientConfig = LettucePoolingClientConfiguration.builder()
//                .clientName(clientName)
                .poolConfig(genericObjectPoolConfig)
                .build();

        LettuceConnectionFactory connectionFactory = new LettuceConnectionFactory(redisConfig, clientConfig);
        connectionFactory.setShareNativeConnection(true);
        return connectionFactory;
    }
//    @Bean
//    public RedisConnectionFactory redisConnectionFactory() {
//        return new LettuceConnectionFactory(host, port);
//    }

    //RedisTemplate 사용을 위한 추가
    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer()); // Optional: for hash keys
        redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());        
        // Use GenericJackson2JsonRedisSerializer for values and hash values
 //       GenericJackson2JsonRedisSerializer jsonRedisSerializer = new GenericJackson2JsonRedisSerializer();
 //       redisTemplate.setValueSerializer(jsonRedisSerializer);
  //      redisTemplate.setHashValueSerializer(jsonRedisSerializer);
        return redisTemplate;
    }
}