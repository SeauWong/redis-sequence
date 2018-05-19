package com.example.demo.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author WongCU
 * @date 2018/5/18
 */
@Component
public class RedisSequence {

    private static final Long COUNT_RADIX = 100000L;

    private static final StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();

    private static final Semaphore semaphore = new Semaphore(200,true);

    @Autowired
    StringRedisTemplate redisTemplate;

    private String formatDate(Long time){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        return sdf.format(new Date(time));
    }

    public String generateSequence(String prefix){
        RedisConnection connection = null;
        try {
            semaphore.acquire();
            connection = redisTemplate.getConnectionFactory().getConnection();
            String dateFormat = formatDate(connection.time());
            String nowDateKey = prefix + dateFormat;
            Long count = connection.incr(stringRedisSerializer.serialize(nowDateKey));
            if(1 == count){
                //设置失效时间24小时
                redisTemplate.expire(nowDateKey,1, TimeUnit.DAYS);
            }
            count += COUNT_RADIX;
            return dateFormat + count;
        }catch (Exception e){
            e.printStackTrace();
            return UUID.randomUUID().toString();
        }finally {
            if(null != connection){
                connection.close();
            }
            semaphore.release(1);
        }
    }
}
