package CACHED_THREADPOOL;

import java.util.Random;
import com.rabbitmq.client.*;
import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Sensor_Speed implements Runnable {

    private final Random rand = new Random();
    private final int max = 1024;
    private final int min = 512;

    private final String spExchange = "spSensorToFlight";
    private final String spExchange2 = "spActToSensor";
    private final String key10 = "speed";
    private final String key12 = "new_speed";

    private final String landExchange1 = "landing1";
    private final String key14 = "speed_land";

    ConnectionFactory cf = new ConnectionFactory();
    String condition;
    private static Connection cn;
    private static Channel ch;

    public class Speed {

        private static int current;

        // Getter for currentAlt
        public static int getSp() {
            return current;
        }

        // Setter for currentAlt
        public static void setSp(int current_2) {
            current = current_2;
        }
    }

    public Sensor_Speed() {
        try {
            cn = cf.newConnection();
            ch = cn.createChannel();
        } catch (IOException | TimeoutException ex) {
            Logger.getLogger(Sensor_Speed.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {

        while (true) {
            try {
                Thread.sleep(1000);
                if (Main.prepLanding == true && Main.isLanding == false) {
                    String a2 = Integer.toString(Speed.getSp());
                    sendMsg(landExchange1, key14, a2);
                }else if(Main.prepLanding == false) {
                    String itemKey = genSp();
                    sendMsg(spExchange, key10, itemKey);
                    getMsg(spExchange2, key12);
                }
                
            } catch (InterruptedException ex) {
                Logger.getLogger(Act_LandingGear.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    public String genSp() {
        int currentSp = Speed.getSp();
        if (currentSp == 0) {
            currentSp = (max + min) / 2;
        }
        int a = currentSp;
        int change = rand.nextInt(128);
        if (rand.nextBoolean()) {
            change *= -1;
        }
        currentSp += change;
        if (currentSp >= max) {
            condition = "Fast";

        } else if (currentSp <= min) {
            condition = "Slow";

        } else {
            condition = "Normal";
        }
        Speed.setSp(currentSp);

        System.out.println("SPEED SENSOR:\tCURRENT SPEED = " + a + " ||| CHANGE = " + change + " ||| LATEST SPEED ->  " + condition + " | " + currentSp);
        Statistics.reportS3.add((double) System.currentTimeMillis());      //SEND TO STATISTICS REPORT
        return condition;
    }

    public void sendMsg(String exchange, String key, String msg) {
        try {
            ch.exchangeDeclare(exchange, "direct");
            ch.basicPublish(exchange, key, false, null, msg.getBytes());
            System.out.println("SPEED SENSOR:\tLATEST SPEED STATUS HAS BEEN SENT -> " + msg);
        } catch (IOException ex) {
            Logger.getLogger(Sensor_Speed.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void getMsg(String exchange, String key) {
        try {
            Channel ch1 = cn.createChannel();
            String qName = ch1.queueDeclare().getQueue();
            ch1.exchangeDeclare(exchange, "direct");
            ch1.queueBind(qName, exchange, key);

            try {
                ch1.basicConsume(qName, true, (x, msg) -> {
                    String temp = new String(msg.getBody(), "UTF-8");

                    //LOGIC TO SET CURRENT VALUE
                    int add = Integer.parseInt(temp); // Parse the altitude string and assign it to currentAlt
                    int cur = Speed.getSp();
                    Speed.setSp(cur + add);

                    try {
                        ch1.close();
                    } catch (IOException | TimeoutException ex) {
                        Logger.getLogger(Sensor_Speed.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }, x -> {
                });
            } catch (IOException ex) {
                Logger.getLogger(Sensor_Speed.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (IOException ex) {
            Logger.getLogger(Sensor_Speed.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
