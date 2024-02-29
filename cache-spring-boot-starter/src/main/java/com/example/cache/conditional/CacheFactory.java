package com.example.cache.conditional;

import com.example.aspect.CacheAspect;
import com.example.cache.Cache;
import com.example.cache.impl.LFUCache;
import com.example.cache.impl.LRUCache;
import com.example.util.Constants;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class CacheFactory {

    @Value("${cache.type:LFU}")
    private String typeCache;

    @Value("${cache.size:5}")
    private Integer sizeCache;

    @Bean
    @Conditional(CacheConditional.class)
    public CacheAspect cacheAspect() {
        Cache cache = null;
        if (typeCache.equals(Constants.CACHE_TYPE_LRU)) {
            cache = new LRUCache();
            cache.setSizeCache(sizeCache);
        }
        if (typeCache.equals(Constants.CACHE_TYPE_LFU)) {
            cache = new LFUCache();
            cache.setSizeCache(sizeCache);
        }
        return new CacheAspect(cache, objectMapper());
    }

    @Bean
    @Profile("test")
    public ObjectMapper objectMapper(){
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        objectMapper.findAndRegisterModules();
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        return objectMapper;
    }

}
