package ru.itis.application.consumers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import ru.itis.application.CourseDto;
import ru.itis.application.services.CourseService;
import ru.itis.application.services.impl.CourseServiceImpl;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.TimeoutException;

public class CourseCompletingConsumer {

    private final static String EXCHANGE_NAME = "courses";
    private final static String COURSE_ROUTING_KEY = "courses.complete";

    private static CourseService courseService = new CourseServiceImpl();

    public static void main(String[] args) {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");

        try {
            Connection connection = connectionFactory.newConnection();
            Channel channel = connection.createChannel();
            channel.basicQos(3);
            String queueName = channel.queueDeclare().getQueue();
            channel.queueBind(queueName, EXCHANGE_NAME, COURSE_ROUTING_KEY);

            ObjectMapper objectMapper = new ObjectMapper();

            channel.basicConsume(queueName, false, (consumerTag, message) -> {
                CourseDto courseDto = objectMapper.treeToValue(objectMapper.readTree(message.getBody()), CourseDto.class);
                System.out.println("Start completing course " + LocalDateTime.now().toString());
                try {
                    courseService.completeCourse(courseDto);
                    System.out.println("Finish completing course " + LocalDateTime.now().toString());
                    channel.basicAck(message.getEnvelope().getDeliveryTag(), false);
                } catch (IOException e) {
                    System.err.println("FAILED");
                    channel.basicReject(message.getEnvelope().getDeliveryTag(), false);
                }

            }, consumerTag -> {
            });
        } catch (IOException | TimeoutException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
