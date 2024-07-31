package com.nhnacademy.bookstoreinjun.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.test.util.ReflectionTestUtils;

class RabbitConfigTest {

    private RabbitConfig rabbitConfig;

    @BeforeEach
    void setUp() {
        rabbitConfig = new RabbitConfig("localhost", 5672, "guest", "guest");

        ReflectionTestUtils.setField(rabbitConfig, "increaseInventoryExchangeName", "increaseInventoryExchange");
        ReflectionTestUtils.setField(rabbitConfig, "increaseInventoryQueueName", "increaseInventoryQueue");
        ReflectionTestUtils.setField(rabbitConfig, "increaseInventoryRoutingKey", "increaseInventoryRoutingKey");
        ReflectionTestUtils.setField(rabbitConfig, "increaseInventoryDlqQueueName", "increaseInventoryDlqQueue");
        ReflectionTestUtils.setField(rabbitConfig, "increaseInventoryDlqRoutingKey", "increaseInventoryDlqRoutingKey");

        ReflectionTestUtils.setField(rabbitConfig, "decreaseInventoryExchangeName", "decreaseInventoryExchange");
        ReflectionTestUtils.setField(rabbitConfig, "decreaseInventoryQueueName", "decreaseInventoryQueue");
        ReflectionTestUtils.setField(rabbitConfig, "decreaseInventoryRoutingKey", "decreaseInventoryRoutingKey");
        ReflectionTestUtils.setField(rabbitConfig, "decreaseInventoryDlqQueueName", "decreaseInventoryDlqQueue");
        ReflectionTestUtils.setField(rabbitConfig, "decreaseInventoryDlqRoutingKey", "decreaseInventoryDlqRoutingKey");

        ReflectionTestUtils.setField(rabbitConfig, "cartCheckoutExchangeName", "cartCheckoutExchange");
        ReflectionTestUtils.setField(rabbitConfig, "cartCheckoutQueueName", "cartCheckoutQueue");
        ReflectionTestUtils.setField(rabbitConfig, "cartCheckoutRoutingKey", "cartCheckoutRoutingKey");
        ReflectionTestUtils.setField(rabbitConfig, "cartCheckoutDlqQueueName", "cartCheckoutDlqQueue");
        ReflectionTestUtils.setField(rabbitConfig, "cartCheckoutDlqRoutingKey", "cartCheckoutDlqRoutingKey");
    }

    @Test
    void testConnectionFactory() {
        ConnectionFactory connectionFactory = rabbitConfig.connectionFactory();
        assertNotNull(connectionFactory);
    }

    @Test
    void testRabbitTemplate() {
        ConnectionFactory connectionFactory = rabbitConfig.connectionFactory();
        RabbitTemplate rabbitTemplate = rabbitConfig.rabbitTemplate(connectionFactory);
        assertNotNull(rabbitTemplate);
        assertInstanceOf(Jackson2JsonMessageConverter.class, rabbitTemplate.getMessageConverter());
    }

    @Test
    void testIncreaseInventoryExchange() {
        DirectExchange exchange = rabbitConfig.increaseInventoryExchange();
        assertNotNull(exchange);
        assertEquals("increaseInventoryExchange", exchange.getName());
    }


    @Test
    void testIncreaseInventoryQueue() {
        Queue queue = rabbitConfig.increaseInventoryQueue();
        assertNotNull(queue);
        assertEquals("increaseInventoryQueue", queue.getName());
        assertEquals("increaseInventoryExchange", queue.getArguments().get("x-dead-letter-exchange"));
        assertEquals("increaseInventoryDlqRoutingKey", queue.getArguments().get("x-dead-letter-routing-key"));
    }

    @Test
    void testIncreaseInventoryDlqQueue() {
        Queue queue = rabbitConfig.increaseInventoryDlqQueue();
        assertNotNull(queue);
        assertEquals("increaseInventoryDlqQueue", queue.getName());
    }

    @Test
    void testIncreaseInventoryBinding(){
        Binding binding = rabbitConfig.increaseInventoryBinding();
        assertNotNull(binding);
    }

    @Test
    void testDecreaseInventoryExchange() {
        DirectExchange exchange = rabbitConfig.decreaseInventoryExchange();
        assertNotNull(exchange);
        assertEquals("decreaseInventoryExchange", exchange.getName());
    }

    @Test
    void testDecreaseInventoryQueue() {
        Queue queue = rabbitConfig.decreaseInventoryQueue();
        assertNotNull(queue);
        assertEquals("decreaseInventoryQueue", queue.getName());
        assertEquals("decreaseInventoryExchange", queue.getArguments().get("x-dead-letter-exchange"));
        assertEquals("decreaseInventoryDlqRoutingKey", queue.getArguments().get("x-dead-letter-routing-key"));
    }

    @Test
    void testDecreaseInventoryDlqQueue() {
        Queue queue = rabbitConfig.decreaseInventoryDlqQueue();
        assertNotNull(queue);
        assertEquals("decreaseInventoryDlqQueue", queue.getName());
    }

    @Test
    void testDecreaseInventoryBinding(){
        Binding binding = rabbitConfig.decreaseInventoryBinding();
        assertNotNull(binding);
    }

    @Test
    void testCartCheckoutExchange() {
        DirectExchange exchange = rabbitConfig.cartCheckoutExchange();
        assertNotNull(exchange);
        assertEquals("cartCheckoutExchange", exchange.getName());
    }

    @Test
    void testCartCheckoutQueue() {
        Queue queue = rabbitConfig.cartCheckoutQueue();
        assertNotNull(queue);
        assertEquals("cartCheckoutQueue", queue.getName());
        assertEquals("cartCheckoutExchange", queue.getArguments().get("x-dead-letter-exchange"));
        assertEquals("cartCheckoutDlqRoutingKey", queue.getArguments().get("x-dead-letter-routing-key"));
    }

    @Test
    void testCartCheckoutDlqQueue() {
        Queue queue = rabbitConfig.cartCheckoutDlqQueue();
        assertNotNull(queue);
        assertEquals("cartCheckoutDlqQueue", queue.getName());
    }

    @Test
    void testCartCheckoutBinding(){
        Binding binding = rabbitConfig.cartCheckoutBinding();
        assertNotNull(binding);
    }
}