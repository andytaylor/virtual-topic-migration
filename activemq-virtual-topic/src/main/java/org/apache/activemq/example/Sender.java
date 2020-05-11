package org.apache.activemq.example;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTopic;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

public class Sender {
   public static void main(String[] args) throws Exception {
      String destination = arg(args, 0, "event");
      String host = arg(args, 1, "localhost");
      int port = Integer.parseInt(arg(args, 2, "61616"));
      String user = arg(args, 3, "admin");
      String password = arg(args, 4, "password");

      int messages = 1000000;
      int size = 256;

      String DATA = "abcdefghijklmnopqrstuvwxyz";
      String body = "";
      for( int i=0; i < size; i ++) {
         body += DATA.charAt(i%DATA.length());
      }

      ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("tcp://" + host + ":" + port);

      Connection connection = factory.createConnection(user, password);
      connection.start();
      Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
      Destination dest = new ActiveMQTopic(destination);
      MessageProducer producer = session.createProducer(dest);
      producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

      for( int i=1; i <= messages; i ++) {
         TextMessage msg = session.createTextMessage(body);
         msg.setIntProperty("id", i);
         producer.send(msg);
         Thread.sleep(10);
         if( (i % 1000) == 0) {
            System.out.println(String.format("Sent %d messages", i));
         }
      }

      producer.send(session.createTextMessage("SHUTDOWN"));
      connection.close();

   }

   private static String env(String key, String defaultValue) {
      String rc = System.getenv(key);
      if( rc== null )
         return defaultValue;
      return rc;
   }

   private static String arg(String []args, int index, String defaultValue) {
      if( index < args.length )
         return args[index];
      else
         return defaultValue;
   }

}
