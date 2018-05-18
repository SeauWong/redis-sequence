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

	private static final ExecutorService executor = Executors.newCachedThreadPool();

	@Test
	public void testRedisSequence() throws InterruptedException {
		CyclicBarrier cyclicBarrier = new CyclicBarrier(10000);
		CountDownLatch countDownLatch = new CountDownLatch(10000);
		for(int i = 0 ; i < 10000; i++){
			executor.execute(() -> {
				try {
					cyclicBarrier.await();
				} catch (Exception e) {
					e.printStackTrace();
				}
				System.out.println(redisSequence.generateSequence("CM"));
				countDownLatch.countDown();
			});
		}
		countDownLatch.await();
	}

}
