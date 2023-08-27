package TOPIC_EXCHANGE;

import java.util.Random;
import com.rabbitmq.client.*;
import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Act_Engines implements Runnable {

    Random rand = new Random();

    String spExchange1 = "spFlightToAct";
    String spExchange2 = "spActToSensor";
    private final String key11 = "speedCon";
    private final String key12 = "new_speed";

    ConnectionFactory cf = new ConnectionFactory();
    private static Connection cn;
    private static Channel ch;

    public Act_Engines() {
        try {
            cn = cf.newConnection();
            ch = cn.createChannel();
        } catch (IOException | TimeoutException ex) {
            Logger.getLogger(Act_Engines.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        retrieveMsg();
    }

    public void retrieveMsg() {
        try {
            //Connection cn = cf.newConnection();
            String qName = ch.queueDeclare().getQueue();
            ch.exchangeDeclare(spExchange1, "direct");
            ch.queueBind(qName, spExchange1, key11);

            try {
                ch.basicConsume(qName, true, (x, msg) -> {
                    String command = new String(msg.getBody(), "UTF-8");
                    Statistics.reportA3.add((double) System.currentTimeMillis());      //SEND TO STATISTICS REPORT
                    String add;
                    if (command.equals("Increase")) {
                        int gen = rand.nextInt(64) + 64;
                        add = Integer.toString(gen);
                        System.out.println("ENGINES:\tCommand Received " + command + " -> Speed-Up the Plane by Increasing the Engine Power... " + add);
                    } else if (command.equals("Decrease")) {
                        int gen = rand.nextInt(64) + 64;
                        gen = gen * -1;
                        add = Integer.toString(gen);
                        System.out.println("ENGINES:\tCommand Received " + command + " -> Slow-Down the Plane by Decreasing the Engine Power... " + add);

                    } else {
                        add = Integer.toString(0);
                        System.out.println("ENGINES:\tCommand Received " + command + " -> Speed Stable & Fine.... Hence, " + add + " Changes");
                    }

                    sendMsg(spExchange2, key12, add);
                }, x -> {
                });
            } catch (IOException ex) {
                Logger.getLogger(Act_Engines.class.getName()).log(Level.SEVERE, null, ex);
            }

        } catch (IOException ex) {
            Logger.getLogger(Act_Engines.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void sendMsg(String exchange, String key, String msg) {
        try {
            ch.exchangeDeclare(exchange, "direct");
            ch.basicPublish(exchange, key, false, null, msg.getBytes());
            System.out.println("ENGINES:\tSENT ADDITIONAL SPEED TO THE SENSOR -> " + msg);
        } catch (IOException ex) {
            Logger.getLogger(Act_Engines.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
