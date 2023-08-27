package TOPIC_EXCHANGE;

import com.rabbitmq.client.*;
import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Act_TailFlags implements Runnable {

    ConnectionFactory cf = new ConnectionFactory();
    private final String weatExchange1 = "weatFlightToAct";
    private final String dirExchange = "dirFlightToAct";
    private final String key5 = "tail";
    private final String key6 = "dir";

    private static Connection cn;
    private static Channel ch;

    public Act_TailFlags() {
        try {
            cn = cf.newConnection();
            ch = cn.createChannel();
        } catch (IOException | TimeoutException ex) {
            Logger.getLogger(Act_TailFlags.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        retrieveMsg();
    }

    public void retrieveMsg() {
        try {
            String qName = ch.queueDeclare().getQueue();
            ch.exchangeDeclare(weatExchange1, "direct");
            ch.queueBind(qName, weatExchange1, key5);

            try {
                ch.basicConsume(qName, true, (x, msg) -> {
                    String weather = new String(msg.getBody(), "UTF-8");
                    Statistics.reportA4.add((double) System.currentTimeMillis());      //SEND TO STATISTICS REPORT

                    if (weather.equals("Bad")) {
                        System.out.println("TAIL FLAGS:\tChanging Plane Directions to AVOID BAD WEATHER");

                    } else {
                        System.out.println("TAIL FLAGS:\tNo Changes in Directions.... Nice Weather :) ");
                    }

                    //SEND MESSAGE TO DIRECTION SENSOR
                    sendMsg(dirExchange, key6, weather);
                }, x -> {
                });

            } catch (IOException ex) {
                Logger.getLogger(Act_TailFlags.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (IOException ex) {
            Logger.getLogger(Act_TailFlags.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void sendMsg(String exchange, String key, String msg) {
        try {
            ch.exchangeDeclare(exchange, "direct");
            ch.basicPublish(exchange, key, false, null, msg.getBytes());
            System.out.println("TAIL FLAGS:\tSENT SIGNAL TO SENSE DIRECTION TO THE DIRECTION SENSOR");
        } catch (IOException ex) {
            Logger.getLogger(Act_TailFlags.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
