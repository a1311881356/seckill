package pers.lbw.seckill;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import pers.lbw.seckill.redis.RedisService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SeckillApplicationTests {
	
	@Autowired
	RedisService rs;

	@Test
	public void contextLoads() {
		rs.get(null, null, null);
	}

}

