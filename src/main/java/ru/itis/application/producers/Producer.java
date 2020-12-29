package ru.itis.application.producers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import ru.itis.application.CourseDto;
import ru.itis.application.models.Account;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

public class Producer {

    private static Scanner scanner = new Scanner(System.in);

    private final static String FIRST_EXCHANGE_NAME = "accounts_passes";
    private final static String SECOND_EXCHANGE_NAME = "account_confirming";
    private final static String THIRD_EXCHANGE_NAME = "courses";

    private final static String EXCHANGE_TYPE_FANOUT = "fanout";
    private final static String EXCHANGE_TYPE_DIRECT = "direct";
    private final static String EXCHANGE_TYPE_TOPIC = "topic";

    private final static String CONFIRM_ROUTING_KEY = "confirm_account";
    private final static String COURSE_ROUTING_KEY = "courses.complete";
    private final static String CERTIFICATE_ROUTING_KEY = "courses.certificate";

    private final static String CONFIRM_QUEUE = "confirm_queue";

    private final static ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) {
        run();
    }

    private static void run() {
        System.out.println();
        System.out.println("Выберите необходимое действие:");
        System.out.println("1 - Создать аккаунт и получить пропуск");
        System.out.println("2 - Подтвердить аккаунт");
        System.out.println("3 - Пройти курс");
        System.out.println("4 - Получить сертификат");
        System.out.println();
        System.out.println("0 - Выйти");
        String s = scanner.nextLine();
        switch (s) {
            case "1":
                createAccountAndGetPass();
                run();
                break;
            case "2":
                confirmAccount();
                run();
                break;
            case "3":
                completeCourse();
                run();
                break;
            case "4":
                getCertificate();
                run();
                break;
            case "0":
                break;
            default:
                System.out.println("Введено неверное значение, повторите попытку");
                run();
        }
    }

    private static void createAccountAndGetPass() {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");

        try {
            Connection connection = connectionFactory.newConnection();
            Channel channel = connection.createChannel();
            channel.exchangeDeclare(FIRST_EXCHANGE_NAME, EXCHANGE_TYPE_FANOUT);

            System.out.println("Введите ваше имя");
            String name = scanner.nextLine();
            System.out.println("Введите вашу фамилию");
            String surname = scanner.nextLine();
            System.out.println("Введите ваш email");
            String email = scanner.nextLine();

            Account account = Account.builder()
                    .name(name)
                    .surname(surname)
                    .email(email)
                    .build();

            byte[] bytes = objectMapper.writeValueAsBytes(account);
            channel.basicPublish(FIRST_EXCHANGE_NAME, "",null, bytes);

        } catch (IOException | TimeoutException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private static void confirmAccount() {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");

        try {
            Connection connection = connectionFactory.newConnection();
            Channel channel = connection.createChannel();
            channel.exchangeDeclare(SECOND_EXCHANGE_NAME, EXCHANGE_TYPE_DIRECT);
            channel.queueBind(CONFIRM_QUEUE, SECOND_EXCHANGE_NAME, CONFIRM_ROUTING_KEY);

            System.out.println("Введите ваш email");
            String email = scanner.nextLine();

            channel.basicPublish(SECOND_EXCHANGE_NAME, CONFIRM_ROUTING_KEY, null, email.getBytes());
        } catch (IOException | TimeoutException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private static void completeCourse() {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");

        try {
            Connection connection = connectionFactory.newConnection();
            Channel channel = connection.createChannel();
            channel.exchangeDeclare(THIRD_EXCHANGE_NAME, EXCHANGE_TYPE_TOPIC);

            System.out.println("Введите название курса");
            String name = scanner.nextLine();
            System.out.println("Введите ваш email");
            String email = scanner.nextLine();

            byte[] bytes = objectMapper.writeValueAsBytes(CourseDto.builder()
                    .courseName(name)
                    .userEmail(email)
                    .build());

            channel.basicPublish(THIRD_EXCHANGE_NAME, COURSE_ROUTING_KEY, null, bytes);
        } catch (IOException | TimeoutException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private static void getCertificate() {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");

        try {
            Connection connection = connectionFactory.newConnection();
            Channel channel = connection.createChannel();
            channel.exchangeDeclare(THIRD_EXCHANGE_NAME, EXCHANGE_TYPE_TOPIC);

            System.out.println("Введите название курса");
            String name = scanner.nextLine();
            System.out.println("Введите ваш email");
            String email = scanner.nextLine();

            byte[] bytes = objectMapper.writeValueAsBytes(CourseDto.builder()
                    .courseName(name)
                    .userEmail(email)
                    .build());

            channel.basicPublish(THIRD_EXCHANGE_NAME, CERTIFICATE_ROUTING_KEY, null, bytes);
        } catch (IOException | TimeoutException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
