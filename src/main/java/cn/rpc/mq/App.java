package cn.rpc.mq;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import com.fasterxml.jackson.databind.ObjectMapper;

@ComponentScan
public class App {
	public static void main(String[] args) throws Exception {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(App.class);
		RabbitTemplate rabbit = context.getBean(RabbitTemplate.class);
		//单位：milliseconds
		rabbit.setReplyTimeout(10000);
		
		String phone = "15210101010";
		String content = "周年庆大促，五折优惠";
		Map<String, Object> body = new HashMap<>();
		body.put("phone", phone);
		body.put("content", content);
		
		MessageProperties messageProperties = new MessageProperties();
		messageProperties.setContentType("json");
		Message message = new Message(new ObjectMapper().writeValueAsBytes(body), messageProperties);
		
		Message reply = rabbit.sendAndReceive("", "sms", message, new CorrelationData(UUID.randomUUID().toString()));
		System.out.println(reply.getBody().length);
		System.out.println(new String(reply.getBody()));
		System.out.println(reply.getMessageProperties());
		
		TimeUnit.SECONDS.sleep(10);
		context.close();
	}
}
