package com.example.project.campusloop.config

import com.example.project.campusloop.infra.websocket.RedisWebSocketReceiver
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
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

    private fun jsonRedisSerializer(): GenericJackson2JsonRedisSerializer {
        val objectMapper = ObjectMapper()
            .registerKotlinModule()
            .registerModule(JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.EVERYTHING,
                JsonTypeInfo.As.PROPERTY
            )

        return GenericJackson2JsonRedisSerializer(objectMapper)
    }

    @Bean
    fun redisTemplate(
        connectionFactory: RedisConnectionFactory,
        redisSerializer: RedisSerializer<String>,
    ): RedisTemplate<String, Any> {
        val template = RedisTemplate<String, Any>()
        template.connectionFactory = connectionFactory
        template.keySerializer = StringRedisSerializer()

        val serializer = jsonRedisSerializer()
        template.valueSerializer = serializer
        template.hashValueSerializer = serializer
        template.hashKeySerializer = serializer

        return template
    }

    @Bean
    fun broadcastListenerAdapter(receiver: RedisWebSocketReceiver): MessageListenerAdapter {
        val adapter = MessageListenerAdapter(receiver, "handleBroadcast")
        adapter.setSerializer(jsonRedisSerializer())
        return adapter
    }

    @Bean
    fun userMessageListenerAdapter(receiver: RedisWebSocketReceiver): MessageListenerAdapter {
        val adapter = MessageListenerAdapter(receiver, "handleUserMessage")
        adapter.setSerializer(jsonRedisSerializer())
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