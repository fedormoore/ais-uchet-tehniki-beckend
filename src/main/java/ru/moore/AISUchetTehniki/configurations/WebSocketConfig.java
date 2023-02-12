package ru.moore.AISUchetTehniki.configurations;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    //    настраиваем брокер сообщений, который будет использоваться
    //    для направления сообщений от одного клиента к другому.
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        //    говорим, что  сообщения, чей адрес начинается с  “/topic“,
        //    должны быть направлены в брокер сообщений. Брокер перенаправляет
        //    сообщения всем клиентам, подписанным на тему.
        config.enableSimpleBroker("/topic");

        //    говорим, что сообщения, чей адрес (куда отправлены) начинается с  “/app“,
        //    должны быть направлены в методы, занимающиеся обработкой сообщений.
        //    методы, аннотированные @MessageMapping.
        config.setApplicationDestinationPrefixes("/app");
    }

    //    регистрируем конечную точку, которую клиенты будут использовать,
    //    чтобы подключиться к нашему Websocket-серверу.
    //    SockJS – для браузеров, которые не поддерживают Websocket.
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry
                .addEndpoint("/websocket")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

}
