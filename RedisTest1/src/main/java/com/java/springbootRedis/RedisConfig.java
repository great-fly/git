package com.java.springbootRedis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import redis.clients.jedis.Jedis;

@Configuration//表明一个配置类 一般与@Bean注解连用
@PropertySource("classpath:/i18n/redis.properties")//导入配置文件
public class RedisConfig {

    @Value("${redis.host}")
    private String host;
    @Value("${redis.port}")
    private  Integer port;

    @Bean   //将方法的返回值结果，交给Spring容器进行管理
    public Jedis jedis() {
        return new Jedis(host,port);
    }

}
