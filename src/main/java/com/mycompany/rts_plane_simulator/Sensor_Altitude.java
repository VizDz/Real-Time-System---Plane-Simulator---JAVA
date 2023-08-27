package com.mycompany.rts_plane_simulator;

import java.util.Random;
import com.rabbitmq.client.*;
import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Sensor_Altitude implements Runnable {

    private final Random rand = new Random();
    private final int max = 11000;
    private final int min = 10000;

    String altExchange = "altSensorToFlight";
    String altExchange2 = "altActToSensor";
    private final String key1 = "altitude";
    private final String key3 = "add_alt";

    private final String landExchange = "landing";
    private final String key13 = "alt_land";

    ConnectionFactory cf = new ConnectionFactory();
    String condition;
    private static Connection cn;
    private static Channel ch;

    public class CurrentAlt {

        private static int current;

        // Getter for currentAlt
        public static int getCurrent() {
            return current;
        }

        // Setter for currentAlt
        public static void setCurrent(int current_1) {
            current = current_1;
        }
    }

    public Sensor_Altitude() {
        try {
            cn = cf.newConnection();
            ch = cn.createChannel();
        } catch (IOException | TimeoutException ex) {
            Logger.getLogger(Sensor_Altitude.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(1000);
                if (Main.prepLanding == true && Main.isLanding == false) {
                    String a1 = Integer.toString(CurrentAlt.getCurrent());
                    sendMsg(landExchange, key13, a1);
                } else if (Main.prepLanding == false) {
                    String item = genAlt();
                    
                    sendMsg(altExchange, key1, item);
                    getMsg(altExchange2, key3);
                }
                
            } catch (InterruptedException ex) {
                Logger.getLogger(Act_LandingGear.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

    }

    public String genAlt() {
        int currentAlt = CurrentAlt.getCurrent();
        int change = rand.nextInt(500);
        if (currentAlt == 0) {
            currentAlt = (max + min) / 2;
        }
        if (rand.nextBoolean()) {
            change *= -1;
        }

        int newAlt = currentAlt + change;
        if (newAlt >= max) {
            condition = "High";
        } else if (newAlt <= min) {
            condition = "Low";
        } else {
            condition = "Good";
        }

        CurrentAlt.setCurrent(newAlt);

        System.out.println("ALT SENSOR:\tCURRENT ALTITUDE = " + currentAlt + " ||| CHANGE = " + change + " ||| LATEST ALTITUDE ->  " + condition + " | " + newAlt);
        Statistics.reportS1.add((double) System.currentTimeMillis());      //SEND TO STATISTICS REPORT
        return condition;
    }

    public void sendMsg(String exchange, String key, String msg) {
        try {
            ch.exchangeDeclare(exchange, "direct");
            ch.basicPublish(exchange, key, null, msg.getBytes());
            System.out.println("ALT SENSOR:\tLATEST ALTITUDE STATUS HAS BEEN SENT -> " + msg);
        } catch (IOException ex) {
            Logger.getLogger(Sensor_Altitude.class.getName()).log(Level.SEVERE, null, ex);
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
                    //System.out.println("RECEIVED MEW  -> " + temp);

                    //LOGIC TO SET CURRENT VALUE
                    int add = Integer.parseInt(temp); // Parse the altitude string and assign it to currentAlt
                    int cur = CurrentAlt.getCurrent();
                    CurrentAlt.setCurrent(cur + add);
                    try {
                        ch1.close();
                    } catch (IOException | TimeoutException ex) {
                        Logger.getLogger(Sensor_Altitude.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }, x -> {
                });
            } catch (IOException ex) {
                Logger.getLogger(Sensor_Altitude.class.getName()).log(Level.SEVERE, null, ex);
            }

        } catch (IOException ex) {
            Logger.getLogger(Sensor_Altitude.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
