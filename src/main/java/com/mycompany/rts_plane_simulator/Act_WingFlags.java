package com.mycompany.rts_plane_simulator;

import com.rabbitmq.client.*;
import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Random;

public class Act_WingFlags implements Runnable {

    Random rand = new Random();

    private final String altExchange1 = "altFlightToAct";
    private final String altExchange2 = "altActToSensor";
    private final String key2 = "wing";
    private final String key3 = "add_alt";

    ConnectionFactory cf = new ConnectionFactory();
    private static Connection cn;
    private static Channel ch;

    public Act_WingFlags() {
        try {
            cn = cf.newConnection();
            ch = cn.createChannel();
        } catch (IOException | TimeoutException ex) {
            Logger.getLogger(Act_WingFlags.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        retrieveMsg();
    }

    public void retrieveMsg() {
        try {

            String qName = ch.queueDeclare().getQueue();
            ch.exchangeDeclare(altExchange1, "direct");
            ch.queueBind(qName, altExchange1, key2);
            try {
                ch.basicConsume(qName, true, (x, msg) -> {
                    String command = new String(msg.getBody(), "UTF-8");
                    Statistics.reportA1.add((double) System.currentTimeMillis());      //SEND TO STATISTICS REPORT
                    String add;
                    if (command.equals("Up")) {
                        int gen = rand.nextInt(300) + 200;
                        add = Integer.toString(gen);
                        System.out.println("WING FLAGS:\tCommand Received " + command + " -> Increase the Angle of Attack of the Wings to Increase Lift of the Plane by " + add);
                    } else if (command.equals("Down")) {
                        int gen = rand.nextInt(300) + 200;
                        gen = gen * -1;
                        add = Integer.toString(gen);
                        System.out.println("WING FLAGS:\tCommand Received " + command + " -> Reduce the Angle of Attack of the Wings to Reduce Lift of the Plane by " + add);

                    } else {
                        add = Integer.toString(0);
                        System.out.println("WING FLAGS:\tCommand Received " + command + " -> Altitude Stable.... Hence, " + add + " Changes");
                    }

                    sendMsg(altExchange2, key3, add);
                    //                    try {
//                        ch1.close();
//                    } catch (IOException | TimeoutException ex) {
//                        Logger.getLogger(Sensor_Altitude.class.getName()).log(Level.SEVERE, null, ex);
//                    }

                }, x -> {
                });
            } catch (IOException ex) {
                Logger.getLogger(Act_WingFlags.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (IOException ex) {
            Logger.getLogger(Act_WingFlags.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void sendMsg(String exchange, String key, String msg) {
        try {
            ch.exchangeDeclare(exchange, "direct");
            ch.basicPublish(exchange, key,false, null, msg.getBytes());
            System.out.println("WING FLAGS:\tSENT ADDITIONAL ATTIUDE TO THE SENSOR -> " + msg);
        } catch (IOException ex) {
            Logger.getLogger(Act_WingFlags.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
