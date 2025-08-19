package site.wetsion.framework.dundunjob.store.redis;

import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.redisson.spring.starter.RedissonAutoConfigurationCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "job.store", name = "impl", havingValue = "redis")
public class RedissonConfigCustomizer implements RedissonAutoConfigurationCustomizer {
    @Override
    public void customize(Config config) {
        config.setCodec(new JsonJacksonCodec());
    }
}
