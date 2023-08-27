package com.mycompany.rts_plane_simulator;

import java.util.Random;
import com.rabbitmq.client.*;
import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Sensor_Weather implements Runnable {

    private final Random rand = new Random();
    private final String weatExchange = "weatSensorToFlight";
    private final String key4 = "weather";

    ConnectionFactory cf = new ConnectionFactory();
    private static Channel ch;
    private static Connection cn;
    private static String weather;

    public Sensor_Weather() {

        try {
            cn = cf.newConnection();
            ch = cn.createChannel();
        } catch (IOException | TimeoutException ex) {
            Logger.getLogger(Sensor_Weather.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {

        while (true) {

            try {
                Thread.sleep(1000);
                if (Main.prepLanding == false) {
                    String item = genWeather();
                    sendMsg(weatExchange, key4, item);
                }

            } catch (InterruptedException ex) {
                Logger.getLogger(Act_LandingGear.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    public String genWeather() {
        int chance = rand.nextInt(100);
        if (chance <= 75) {
            weather = "Good";
        } else {
            weather = "Bad";
        }

        System.out.println("WEATHER SENSOR:\tCURRENT WEATHER -> " + weather);
        Statistics.reportS4.add((double) System.currentTimeMillis());      //SEND TO STATISTICS REPORT
        return weather;
    }

    public void sendMsg(String exchange, String key, String msg) {
        try {
            ch.exchangeDeclare(exchange, "direct");
            ch.basicPublish(exchange, key, false, null, msg.getBytes());
            System.out.println("WEATHER SENSOR:\tCURRENT WEATHER HAS BEEN SENT -> " + msg);
        } catch (IOException ex) {
            Logger.getLogger(Sensor_Weather.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
