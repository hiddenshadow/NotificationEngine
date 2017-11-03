package services;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import play.Logger;
import play.Play;

public class QueuingServiceImpl implements QueuingService {

    private static Channel mailChannel;

    public static void initRMQConnection() throws Exception {
        Logger.debug("Initializing RMQConnection ");
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(Play.application().configuration().getString("host"));
        Connection connection = factory.newConnection();
        mailChannel = connection.createChannel();
        mailChannel.exchangeDeclare(
                Play.application().configuration().getString("exchangeName"),
                Play.application().configuration().getString("exchangeType"),
                Play.application().configuration().getBoolean("isDurableExchange"));
    }

    public static void publishMessage(String message) throws Exception {
        if(mailChannel == null){
            initRMQConnection();
        }
        mailChannel.basicPublish(
                Play.application().configuration().getString("exchangeName"),
                Play.application().configuration().getString("routingKey"), null, message.getBytes());
        Logger.debug("Sent '"+ "':'" + message + "'");
    }
}

