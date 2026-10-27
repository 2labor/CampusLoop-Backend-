package com.example.project.campusloop.config

import com.example.project.campusloop.infra.websocket.RedisWebSocketReceiver
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.listener.PatternTopic
import org.springframework.data.redis.listener.RedisMessageListenerContainer
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer
import java.util.concurrent.Executors

@Configuration
class RedisConfig {

    @Bean
    fun redisSerializer(): RedisSerializer<String> {
        return StringRedisSerializer()
    }


    @Bean
    fun redisTemplate(
        connectionFactory: RedisConnectionFactory,
        redisSerializer: RedisSerializer<String>,
    ): RedisTemplate<String, Any> {
        val template = RedisTemplate<String, Any>()
        template.connectionFactory = connectionFactory
        template.keySerializer = StringRedisSerializer()

        val serializer = GenericJackson2JsonRedisSerializer()
        template.valueSerializer = serializer
        template.hashValueSerializer = serializer
        template.hashKeySerializer = serializer

        return template
    }

    @Bean
    fun broadcastListenerAdapter(receiver: RedisWebSocketReceiver): MessageListenerAdapter {
        val adapter = MessageListenerAdapter(receiver, "handleBroadcast")
        adapter.setSerializer(GenericJackson2JsonRedisSerializer())

        return adapter
    }

    @Bean
    fun userMessageListenerAdapter(receiver: RedisWebSocketReceiver): MessageListenerAdapter {
        val adapter = MessageListenerAdapter(receiver, "handleUserMessage")
        adapter.setSerializer(GenericJackson2JsonRedisSerializer())

        return adapter
    }

    @Bean
    fun redisMessageListenerContainer(
        connectionFactory: RedisConnectionFactory,
        broadcastListenerAdapter: MessageListenerAdapter,
        userMessageListenerAdapter: MessageListenerAdapter
    ): RedisMessageListenerContainer {
        val container = RedisMessageListenerContainer()
        container.setConnectionFactory(connectionFactory)

        container.addMessageListener(broadcastListenerAdapter, PatternTopic("ws:broadcast:*"))
        container.addMessageListener(userMessageListenerAdapter, PatternTopic("ws:user:*"))

        container.setTaskExecutor(Executors.newFixedThreadPool(4))
        return container
    }
}