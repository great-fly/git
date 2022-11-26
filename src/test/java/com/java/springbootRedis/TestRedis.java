package com.java.springbootRedis;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import redis.clients.jedis.Jedis;

@SpringBootTest //动态获取spring容器中的数据
public class TestRedis {

    @Autowired
    public Jedis jedis;

    @Test
    public void testSpringRedis() {
        jedis.set("jedis","spring测试");
        System.out.println(jedis.get("jedis"));
    }
}
