package ru.itis.application.consumers;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import ru.itis.application.services.AccountService;
import ru.itis.application.services.impl.AccountServiceImpl;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.TimeoutException;

public class ConfirmingConsumer {

    private final static String CONFIRM_QUEUE = "confirm_queue";
    private static AccountService accountService = new AccountServiceImpl();

    public static void main(String[] args) {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");

        try {
            Connection connection = connectionFactory.newConnection();
            Channel channel = connection.createChannel();
            channel.basicQos(3);
            channel.basicConsume(CONFIRM_QUEUE, false, (consumerTag, message) -> {
                String email = new String(message.getBody());
                System.out.println("Start confirm account " + LocalDateTime.now().toString());
                try {
                    System.out.println("Account confirming done " + LocalDateTime.now().toString());
                    accountService.confirmAccount(email);
                    channel.basicAck(message.getEnvelope().getDeliveryTag(), false);
                } catch (IOException e) {
                    System.err.println("FAILED");
                    channel.basicReject(message.getEnvelope().getDeliveryTag(), false);
                }

            }, consumerTag -> {});
        } catch (IOException | TimeoutException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
