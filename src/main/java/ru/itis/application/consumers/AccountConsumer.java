package ru.itis.application.consumers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import ru.itis.application.models.Account;
import ru.itis.application.services.AccountService;
import ru.itis.application.services.impl.AccountServiceImpl;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.TimeoutException;

public class AccountConsumer {

    private final static String EXCHANGE_NAME = "accounts_passes";
    private final static String EXCHANGE_TYPE = "fanout";

    private static AccountService accountService = new AccountServiceImpl();

    public static void main(String[] args) {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");

        try {
            Connection connection = connectionFactory.newConnection();
            Channel channel = connection.createChannel();
            channel.basicQos(3);

            channel.exchangeDeclare(EXCHANGE_NAME, EXCHANGE_TYPE);
            String queue = channel.queueDeclare().getQueue();

            channel.queueBind(queue, EXCHANGE_NAME, "");
            ObjectMapper objectMapper = new ObjectMapper();
            DeliverCallback deliverCallback = (consumerTag, message) -> {
                Account account = objectMapper.treeToValue(objectMapper.readTree(message.getBody()), Account.class);
                System.out.println("Start creating account " + LocalDateTime.now().toString());
                try {
                    accountService.newAccount(account);
                    System.out.println("Finish creating account " + LocalDateTime.now().toString());
                    channel.basicAck(message.getEnvelope().getDeliveryTag(), false);
                } catch (IOException e) {
                    System.err.println("FAILED");
                    channel.basicReject(message.getEnvelope().getDeliveryTag(), false);
                }
            };

            channel.basicConsume(queue, false, deliverCallback, consumerTag -> {
            });
        } catch (IOException | TimeoutException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
