package com.example.demo.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 *
 * @author WongCU
 * @date 2018/5/18
 */
public class RedisSequence {

    private static final String PREFIX = "CM";

    private static final Long START_COUNT_VAL = 10000L;

    @Autowired
    StringRedisTemplate redisTemplate;

    private String formatDate(Long time){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        return sdf.format(new Date(time));
    }

    public String generateSequence(String prefix){
        RedisConnection connection = null;
        try {
            connection = redisTemplate.getConnectionFactory().getConnection();
            String dateFormat = formatDate(connection.time());
            String nowDateKey = prefix + dateFormat;
            RedisAtomicLong atomicLong = new RedisAtomicLong(nowDateKey,redisTemplate.getConnectionFactory());
            if(1 == atomicLong.get()){
                //todo 设置失效时间24小时
            }
            Long count = START_COUNT_VAL + atomicLong.get();
            return dateFormat + count;
        }catch (Exception e){
            e.printStackTrace();
            return UUID.randomUUID().toString();
        }finally {
            if(null != connection){
                connection.close();
            }
        }
    }
}
