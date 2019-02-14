package pers.lbw.seckill.rabbitmq;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.annotation.Resources;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.HeadersExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class MQConfig {
	
	public static final String QUEUE_NAME="queue";
	public static final String TOPIC_QUEUE1="topic.queue1";
	public static final String TOPIC_QUEUE2="topic.queue2";
	public static final String HEADERS_QUEUE="headersQueue";
	public static final String TOPIC_EXCHANGE="topicExchange";
	public static final String FANOUT_EXCHANGE="fanoutExchange";
	public static final String HEADERS_EXCHANGE="headersExchange";
	public static final String ROUTING_KEY1="topic.key1";
	public static final String ROUTING_KEY2="topic.#";//#代表一个或者多个字符
	
	public static final String SECKILL_QUEUE="seckill.queue";
	
	//1.Direct Exchange模式(直连交换机模式)
	@Bean
	public Queue queue() {
		return new Queue(QUEUE_NAME,true);//第一个参数是队列的名称，第二个是配置是否持久化
	}
	
	//2.Topic Exchange(主题交换机模式)
	@Bean
	public Queue topicQueue1() {
		return new Queue(TOPIC_QUEUE1,true);//第一个参数是队列的名称，第二个是配置是否持久化
	}
	@Bean
	public Queue topicQueue2() {
		return new Queue(TOPIC_QUEUE2,true);//第一个参数是队列的名称，第二个是配置是否持久化
	}
	@Bean
	public TopicExchange topicExchange() {
		return new TopicExchange(TOPIC_EXCHANGE);
	}
	@Bean
	public Binding topicBinding1(@Qualifier("topicQueue1")Queue topicQueue1,@Qualifier("topicExchange")TopicExchange topicExchange) {
		System.err.println("topicQueue1相等吗："+(topicQueue1()==topicQueue1));
		System.err.println("topicExchange相等吗："+(topicExchange()==topicExchange));
		return BindingBuilder.bind(topicQueue1).to(topicExchange).with(ROUTING_KEY1);
	}
	@Bean
	public Binding topicBinding2() {
		return BindingBuilder.bind(topicQueue2()).to(topicExchange()).with(ROUTING_KEY2);
	}
	
	//3.Fanout Exchange（广播交换机模式）
	@Bean
	public FanoutExchange fanoutExchange() {
		return new FanoutExchange(FANOUT_EXCHANGE);
	}
	@Bean
	public Binding fanoutBinding1() {
		return BindingBuilder.bind(topicQueue1()).to(fanoutExchange());
	}
	@Bean
	public Binding fanoutBinding2() {
		return BindingBuilder.bind(topicQueue2()).to(fanoutExchange());
	}
	
	//Headers Exchange
	@Bean
	public HeadersExchange headersExchange() {
		return new HeadersExchange(HEADERS_EXCHANGE);
	}
	@Bean
	public Queue headersQueue() {
		return new Queue(HEADERS_QUEUE,true);//第一个参数是队列的名称，第二个是配置是否持久化
	}
	@Bean
	public Binding headersBinding() {
		Map<String,Object> map=new HashMap<>();
		map.put("header1", "value1");
		map.put("header2", "value2");
		map.put("header3", "value3");
		return BindingBuilder.bind(headersQueue()).to(headersExchange()).whereAll(map).match();
	}
	
	@Bean
	public Queue seckillQueue() {
		return new Queue(SECKILL_QUEUE,true);//第一个参数是队列的名称，第二个是配置是否持久化
	}
}
