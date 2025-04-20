package ru.yandex.practicum.intershop.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.*;
import ru.yandex.practicum.intershop.model.Item;

import java.time.Duration;
import java.util.List;

@Configuration
@EnableCaching
public class CacheConfiguration {

    public static final String REDIS_ITEM_CACHE = "item";
    public static final String REDIS_ITEM_IMAGE_CACHE = "item_image";
    public static final String REDIS_ITEM_SEARCH_CACHE = "item_search";
    public static final String REDIS_ITEM_SEARCH_COUNT_CACHE = "item_search_count";

    @Bean
    public RedisCacheManagerBuilderCustomizer cacheCustomizer() {
        return builder -> builder.cacheDefaults(
                RedisCacheConfiguration.defaultCacheConfig()
                        .entryTtl(Duration.ofMinutes(1))
                        .disableCachingNullValues()
                        .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                        .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(RedisSerializer.json())))

                .withCacheConfiguration(REDIS_ITEM_IMAGE_CACHE,
                RedisCacheConfiguration.defaultCacheConfig()
                        .entryTtl(Duration.ofMinutes(1))
                        .disableCachingNullValues()
                        .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                        .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(RedisSerializer.byteArray())));
    }

    @Bean
    public ReactiveRedisTemplate<String, Object> jsonSerializerRedisTemplate(ReactiveRedisConnectionFactory factory) {
        RedisSerializer<String> keySerializer = new StringRedisSerializer();
        Jackson2JsonRedisSerializer<Object> valueSerializer =
                new Jackson2JsonRedisSerializer<>(Object.class);

        RedisSerializationContext<String, Object> context = RedisSerializationContext
                .<String, Object>newSerializationContext(keySerializer)
                .value(valueSerializer)
                .build();

        return new ReactiveRedisTemplate<>(factory, context);
    }

    @Bean
    public ReactiveRedisTemplate<String, List<Item>> itemListRedisTemplate (ReactiveRedisConnectionFactory factory) {

        ObjectMapper mapper = new ObjectMapper();
        mapper.activateDefaultTyping(mapper.getPolymorphicTypeValidator(), ObjectMapper.DefaultTyping.NON_FINAL);

        Jackson2JsonRedisSerializer<List<Item>> valueSerializer =
                new Jackson2JsonRedisSerializer<>(mapper, (Class<List<Item>>)(Class<?>)List.class); // Немного хак

        RedisSerializer<String> keySerializer = new StringRedisSerializer();

        RedisSerializationContext<String, List<Item>> context = RedisSerializationContext
                .<String, List<Item>>newSerializationContext(keySerializer)
                .value(valueSerializer)
                .build();

        return new ReactiveRedisTemplate<>(factory, context);
    }


}
