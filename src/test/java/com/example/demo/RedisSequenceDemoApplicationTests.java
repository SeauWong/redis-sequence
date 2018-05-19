package com.example.demo;

import com.example.demo.redis.RedisSequence;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisSequenceDemoApplicationTests {

	@Autowired
	RedisSequence redisSequence;

	@Test
	public void testRedisSequence() throws InterruptedException {
		CyclicBarrier cyclicBarrier = new CyclicBarrier(20000);
		CountDownLatch countDownLatch = new CountDownLatch(20000);
		for(int i = 0 ; i < 20000; i++){
			new Thread(() -> {
				try {
					cyclicBarrier.await();
				} catch (Exception e) {
					e.printStackTrace();
				}
				System.out.println(redisSequence.generateSequence("CM"));
				countDownLatch.countDown();
			}).start();
		}
		countDownLatch.await();
	}

}
