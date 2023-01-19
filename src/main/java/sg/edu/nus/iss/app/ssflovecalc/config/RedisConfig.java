package sg.edu.nus.iss.app.ssflovecalc.config;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

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

    // define the return redis template bean as single Object throughout the runtime.
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
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        
        // enable redis to store java object on the value column
        // converts object into byte array for transfer & storage and back
        // ClassLoader avoids class loading issues (not included by default)
        RedisSerializer<Object> objSerializer = new JdkSerializationRedisSerializer(getClass().getClassLoader());
        // SET VALUE TYPES
        redisTemplate.setValueSerializer(objSerializer);
        redisTemplate.setHashValueSerializer(objSerializer);

        return redisTemplate;
    }
}