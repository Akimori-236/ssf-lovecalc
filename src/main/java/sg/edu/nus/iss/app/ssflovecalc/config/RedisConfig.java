package sg.edu.nus.iss.app.ssflovecalc.config;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;

@Configuration
public class RedisConfig {

    // gets host from application properties
    @Value("${spring.redis.host}")
    private String redisHost;

    // gets port number from application properties
    @Value("${spring.redis.port}")
    private Optional<Integer> redisPort;

    // empty string for localhost use
    @Value("${spring.redis.username}")
    private String redisUsername;

    // empty string for localhost use
    @Value("${spring.redis.password}")
    private String redisPassword;

    // define the return redis template bean as single Object throughout the
    // runtime.
    // Return the RedisTemplate
    @Bean
    @Scope("singleton")
    public RedisTemplate<String, Object> redisTemplate() {
        final RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();

        // SET THE REDIS CONFIGS
        config.setHostName(redisHost);
        config.setPort(redisPort.get());
        System.out.println(redisUsername);
        System.out.println(redisPassword);
        if (!redisUsername.isEmpty() && !redisPassword.isEmpty()) {
            config.setUsername(redisUsername);
            config.setPassword(redisPassword);
        }
        config.setDatabase(0); // hard coded database

        // BUILD JEDIS CONNNECTION W REDIS CONFIGS
        final JedisClientConfiguration jedisClient = JedisClientConfiguration
                .builder()
                .build();
        final JedisConnectionFactory jedisFac = new JedisConnectionFactory(config, jedisClient);
        jedisFac.afterPropertiesSet(); // set properties

        // THE THING THAT INTERACTS W REDIS STORE
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        // associate with the redis connection
        redisTemplate.setConnectionFactory(jedisFac);

        // SET KEY TYPES
        redisTemplate.setKeySerializer(new StringRedisSerializer()); // this is by default
        // set the map key/value serialization type to String
        redisTemplate.setHashKeySerializer(redisTemplate.getKeySerializer());

        // enable redis to store java object on the value column
        // converts object into byte array for transfer & storage and back
        // ClassLoader avoids class loading issues (not included by default)
        // RedisSerializer<Object> objSerializer = new
        // JdkSerializationRedisSerializer(getClass().getClassLoader());
        // SET VALUE TYPES
        redisTemplate.setValueSerializer(new JsonRedisSerializer());
        redisTemplate.setHashValueSerializer(new JsonRedisSerializer());

        return redisTemplate;
    }

    static class JsonRedisSerializer implements RedisSerializer<Object> {

        private final ObjectMapper om;

        public JsonRedisSerializer() {
            this.om = new ObjectMapper().activateDefaultTyping(LaissezFaireSubTypeValidator.instance,
                    ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
        }

        @Override
        public byte[] serialize(Object t) throws SerializationException {
            try {
                return om.writeValueAsBytes(t);
            } catch (JsonProcessingException e) {
                throw new SerializationException(e.getMessage(), e);
            }
        }

        @Override
        public Object deserialize(byte[] bytes) throws SerializationException {

            if (bytes == null) {
                return null;
            }
            try {
                return om.readValue(bytes, Object.class);
            } catch (Exception e) {
                throw new SerializationException(e.getMessage(), e);
            }
        }
    }
}