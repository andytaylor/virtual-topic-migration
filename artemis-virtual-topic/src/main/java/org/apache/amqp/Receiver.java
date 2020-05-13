package org.apache.amqp;

import org.apache.qpid.jms.JmsConnectionFactory;
import org.apache.qpid.jms.JmsQueue;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;

public class Receiver {
   public static void main(String []args) throws JMSException {


      String destination = arg(args, 0, "event");
      String host = arg(args, 1, "localhost");
      int port = Integer.parseInt(arg(args, 2, "61616"));
      String user = arg(args, 3, "admin");
      String password = arg(args, 4, "password");

      JmsConnectionFactory factory = new JmsConnectionFactory("amqp://" + host + ":" + port);

      Connection connection = factory.createConnection(user, password);
      connection.start();
      Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
      Destination dest = new JmsQueue(destination);

      MessageConsumer consumer = session.createConsumer(dest);
      long start = System.currentTimeMillis();
      long count = 1;
      System.out.println("Waiting for messages...");
      while(true) {
         Message msg = consumer.receive();
         if( msg instanceof TextMessage) {
            String body = ((TextMessage) msg).getText();
            if( "SHUTDOWN".equals(body)) {
               long diff = System.currentTimeMillis() - start;
               System.out.println(String.format("Received %d in %.2f seconds", count, (1.0*diff/1000.0)));
               break;
            } else {
               count = msg.getIntProperty("id");

               if( count == 0 ) {
                  start = System.currentTimeMillis();
               }
               if( count % 1000 == 0 ) {
                  System.out.println(String.format("Received %d messages.", count));
               }
               count ++;
            }

         } else {
            System.out.println("Unexpected message type: "+msg.getClass());
         }
      }
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
