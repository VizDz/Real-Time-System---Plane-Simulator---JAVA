//package com.mycompany.rts_plane_simulator;
//package com.mycompany.rts_plane_simulator;
package com.mycompany.rts_plane_simulator;

import com.rabbitmq.client.*;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Act_LandingGear implements Runnable {

    private final String landExchange2 = "landing";
    private final String key15 = "alt_land_1";
    private final String landExchange3 = "landing1";
    private final String key16 = "speed_land_1";

    ConnectionFactory cf = new ConnectionFactory();
    private static Connection cn;
    private static Channel ch;
    private static double speed;
    private static double alt;

    public Act_LandingGear() {
        try {
            cn = cf.newConnection();
            ch = cn.createChannel();
        } catch (IOException | TimeoutException ex) {
            Logger.getLogger(Act_LandingGear.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        retrieveMsg();

    }

    public void landing() {
        System.out.println("LANDING SYSTEM:\tLANDING GEAR OUT.....");
        double x = alt / 40;
        double y = speed / 50;

        while (speed > 0 || alt > 0) {
            System.out.println("LANDING SYSTEM:\tCURRENT SPEED -> " + String.format("%.1f", speed) + " ||| CURRENT ALTITUDE -> " + String.format("%.1f", alt));
            if (alt <= 0 && speed >= 0) {
                speed = speed - y;
            }

            if (alt >= 0 && speed <= 0) {
                alt = alt - x;
            }

            if (alt > 0 && speed > 0) {
                alt = alt - x;
                speed = speed - y;
            }

            if (speed <= 0) {
                speed = 0;
            }
            if (alt <= 0) {
                alt = 0;
            }

            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                Logger.getLogger(Act_LandingGear.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        Main.landed = true;
    }

    public void retrieveMsg() {
        try {
            ch.exchangeDeclare(landExchange2, "direct");
            String qName = ch.queueDeclare().getQueue();
            ch.queueBind(qName, landExchange2, key15);

            try {

                ch.basicConsume(qName, true, (x, msg) -> {
                    String temp = new String(msg.getBody(), "UTF-8");
                    alt = Integer.parseInt(temp);
                    System.out.println("\t\tLANDING SYSTEM:\tTAKING CURRENT ALTITUDE OF THE PLANE -> " + temp);

                }, x -> {
                });
            } catch (IOException ex) {
                Logger.getLogger(Act_LandingGear.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (IOException ex) {
            Logger.getLogger(Act_LandingGear.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            //Channel ch1 = cn.createChannel();
            ch.exchangeDeclare(landExchange3, "direct");
            String qName1 = ch.queueDeclare().getQueue();
            ch.queueBind(qName1, landExchange3, key16);

            try {
                ch.basicConsume(qName1, true, (x, msg) -> {
                    String temp = new String(msg.getBody(), "UTF-8");
                    speed = Integer.parseInt(temp);
                    System.out.println("\t\tLANDING SYSTEM:\tTAKING CURRENT SPEED OF THE PLANE -> " + temp);

                    if (alt != 0 && speed != 0) {
                        System.out.println("\t\tLANDING SYSTEM:\tONLY ACC ALTITUDE & SPEED ||| PREPARING FOR LANDING....");
                        Main.isLanding = true;
                        landing();
                    }
//                    try {
//                        ch1.close();
//                    } catch (IOException | TimeoutException ex) {
//                        Logger.getLogger(Flight_Control.class.getName()).log(Level.SEVERE, null, ex);
//                    }
                }, x -> {
                });

            } catch (IOException ex) {
                Logger.getLogger(Act_LandingGear.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (IOException ex) {
            Logger.getLogger(Act_LandingGear.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
