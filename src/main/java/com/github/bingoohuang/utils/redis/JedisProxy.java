package com.github.bingoohuang.utils.redis;

import com.github.bingoohuang.utils.proxy.Cglibs;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.n3r.diamond.client.Minerable;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Transaction;

public class JedisProxy {
    /**
     * 创建JedisCommands代理，每次操作，自动从资源池中获取连接，使用完，释放回资源池。
     *
     * @param jedisPool Jedis连接池配置
     * @return Jedis代理对象
     */
    public static Jedis createJedisProxy(JedisPool jedisPool) {
        //
        return (Jedis) Cglibs.proxy(Jedis.class, (o1, m1, args1, p1) -> {
            val pooled = jedisPool.getResource();
            val result = m1.invoke(pooled, args1);
            switch (m1.getName()) {
                case "multi":
                    return Cglibs.proxy(Transaction.class, (o2, m2, args2, p2) -> {
                        Object o = m2.invoke(result, args2);
                        if (m2.getName().equals("exec")) pooled.close();
                        return o;
                    });
                case "close":
                    jedisPool.destroy();
                    break;
                default:
                    pooled.close();
            }
            return result;

        });
    }


    /**
     * 根据diamond配置生成Jedis对象.
     *
     * @param redisConfig Jedis配置
     * @return Jedis代理对象
     */
    public static Jedis createJedisProxy(Minerable redisConfig) {
        val host = redisConfig.getString("redis.host", "127.0.0.1");
        val port = redisConfig.getInt("redis.port", 6379);
        val password = redisConfig.getString("redis.password");
        val maxTotal = redisConfig.getInt("redis.maxTotal", 10);
        val defaultDb = redisConfig.getInt("redis.database", 0);

        val poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(maxTotal);
        poolConfig.setMaxIdle(5);
        poolConfig.setMaxWaitMillis(1000 * 10);
        poolConfig.setTestOnBorrow(true);

        val jedisPool = new JedisPool(poolConfig, host, port, 2000, StringUtils.trimToNull(password), defaultDb);
        return JedisProxy.createJedisProxy(jedisPool);
    }

}
