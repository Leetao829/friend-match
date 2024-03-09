package com.leetao.usercenter.service;

import com.leetao.usercenter.exception.BusinessException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@SpringBootTest
public class RedissonTest {
	@Test
	void test(){
		Lock lock = new ReentrantLock();

		lock.lock();
		try {

		}finally {
			lock.unlock();
		}
	}

}
