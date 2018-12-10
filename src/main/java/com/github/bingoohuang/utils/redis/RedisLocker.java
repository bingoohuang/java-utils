package com.github.bingoohuang.utils.redis;

import com.github.bingoohuang.utils.time.DateTimes;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.joda.time.DateTime;
import redis.clients.jedis.Jedis;

import java.io.Closeable;

@RequiredArgsConstructor
public class RedisLocker implements Closeable {
    private final Jedis jedis;
    private final String key;

    @Override public void close() {
        jedis.del(key);
    }

    public boolean tryLock() {
        val ret = jedis.set(key, DateTimes.formatDateTime(DateTime.now()), "nx", "ex", 10);
        return "OK".equals(ret);
    }
}
