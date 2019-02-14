package pers.lbw.seckill.rabbitmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pers.lbw.seckill.domain.OrderInfo;
import pers.lbw.seckill.domain.SeckillOrder;
import pers.lbw.seckill.domain.SeckillUser;
import pers.lbw.seckill.redis.RedisService;
import pers.lbw.seckill.service.GoodsService;
import pers.lbw.seckill.service.OrderService;
import pers.lbw.seckill.service.SeckillService;
import pers.lbw.seckill.vo.GoodsVo;

@Service
public class MQReceiver {

	private static Logger log = LoggerFactory.getLogger(MQConfig.class);

	@Autowired
	GoodsService gs;

	@Autowired
	OrderService os;

	@Autowired
	SeckillService ss;
	
	@Autowired
	RedisService rs;

	@RabbitListener(queues = MQConfig.QUEUE_NAME)
	public void receive(String message) {
		log.info("收到的消息：" + message);
	}

	@RabbitListener(queues = MQConfig.TOPIC_QUEUE1)
	public void receiveTopicQueue1(String message) {
		log.info("收到TOPIC_QUEUE1的消息：" + message);
	}

	@RabbitListener(queues = MQConfig.TOPIC_QUEUE2)
	public void receiveTopicQueue2(String message) {
		log.info("收到TOPIC_QUEUE2的消息：" + message);
	}

	// headers交换机模式接收
	@RabbitListener(queues = MQConfig.HEADERS_QUEUE)
	public void receiveHeadersQueue(byte[] message) {
		log.info("收到HEADERS_QUEUE的消息：" + new String(message));
	}

	@RabbitListener(queues = MQConfig.SECKILL_QUEUE)
	public void receiveSeckillMsg(String sm_str) {
		SeckillMessage sm = RedisService.parseStringToBean(sm_str, SeckillMessage.class);
		log.info("收到的消息：" + sm);
		SeckillUser user = sm.getUser();
		long goodsId = sm.getGoodsId();

		// 判断库存
		GoodsVo goodsVo = gs.getGoodsVoByGoodsId(goodsId);
		Integer count = goodsVo.getStockCount();
		if (count <= 0) {
			//-1表示处理失败
			os.setSeckillResult(sm.getUser().getId(),sm.getGoodsId(),-1);
			return;
		}
		
		//防止重复秒杀
		SeckillOrder order=os.getSeckillOrderByUserIdAndGoodsId(user.getId(),goodsId);
		if(order!=null) {
			os.setSeckillResult(sm.getUser().getId(),sm.getGoodsId(),-1);
			return;
		}

		//下订单、减少库存
		OrderInfo orderInfo=ss.seckill(user,goodsVo);
		
		//设置当次秒杀的结果,非-1表示成功，非-1其实就是订单号
		os.setSeckillResult(sm.getUser().getId(),sm.getGoodsId(),orderInfo.getId());
	}
	
	
}
