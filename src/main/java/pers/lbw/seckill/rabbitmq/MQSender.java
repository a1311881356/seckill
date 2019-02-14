package pers.lbw.seckill.rabbitmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pers.lbw.seckill.redis.RedisService;

@Service
public class MQSender {
	
	@Autowired
	AmqpTemplate amqpTemplate;
	
	private static Logger log=LoggerFactory.getLogger(MQConfig.class);
	
	public void send(Object message) {
		String msg=RedisService.parseBeanToString(message);
		amqpTemplate.convertAndSend(MQConfig.QUEUE_NAME,msg);
		log.info("发送的消息："+msg);
	}
	
	public void sendTopic(Object message) {
		String msg=RedisService.parseBeanToString(message);
		amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE,MQConfig.ROUTING_KEY1,msg+"1");
		amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE,MQConfig.ROUTING_KEY2,msg+"2");
		log.info("发送topic消息："+msg);
	}
	
	public void sendFanout(Object message) {
		String msg=RedisService.parseBeanToString(message);
		amqpTemplate.convertAndSend(MQConfig.FANOUT_EXCHANGE,null,msg);//无需填写routingKey，因为是广播发送
		log.info("发送fanout消息："+msg);
	}
	
	public void sendHeaders(Object message) {
		String msg=RedisService.parseBeanToString(message);
		MessageProperties map=new MessageProperties();
		map.setHeader("header1", "value1");
		map.setHeader("header2", "value2");
		map.setHeader("header3", "value3");
		Message obj=new Message(msg.getBytes(), map);//这里的Message类包括消息的内容和headers的内容,构造方法中第一个是消息的内容，第二个是headers的内容
		amqpTemplate.convertAndSend(MQConfig.HEADERS_EXCHANGE,null,obj);//无需填写routingKey,因为采取判断headers中的内容来匹配
		log.info("发送headers消息："+msg);
	}

	public void sendSeckillMsg(SeckillMessage sm) {
		String sm_str = RedisService.parseBeanToString(sm);
		amqpTemplate.convertAndSend(MQConfig.SECKILL_QUEUE, sm_str);
	}
	
}
