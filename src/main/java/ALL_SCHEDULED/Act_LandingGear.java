//package com.mycompany.rts_plane_simulator;
//package com.mycompany.rts_plane_simulator;
package ALL_SCHEDULED;

import com.rabbitmq.client.*;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Act_LandingGear implements Runnable {

    private final String landExchange = "landing";
    private final String landExchange1 = "landing1";
    private final String key13 = "alt_land";
    private final String key14 = "speed_land";

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
        if (Main.prepLanding == true && Main.isLanding == false) {
            retrieveMsg();
        }

    }

    public void landing() {
        System.out.println("LANDING SYSTEM: LANDING GEAR OUT");
        Main.isLanding = true;

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
                Thread.sleep(50);
            } catch (InterruptedException ex) {
                Logger.getLogger(Act_LandingGear.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        Main.landed = true;

    }

    public void retrieveMsg() {
        try {
            Channel ch5 = cn.createChannel();
            ch5.exchangeDeclare(landExchange, "direct");
            String qName = ch5.queueDeclare().getQueue();
            ch5.queueBind(qName, landExchange, key13);

            try {

                ch5.basicConsume(qName, true, (x, msg) -> {
                    String temp = new String(msg.getBody(), "UTF-8");
                    alt = Integer.parseInt(temp);
                    System.out.println("\t\tLANDING SYSTEM:\tTAKING CURRENT ALTITUDE OF THE PLANE -> " + temp);

                    try {
                        ch5.close();
                    } catch (IOException | TimeoutException ex) {
                        Logger.getLogger(Flight_Control.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }, x -> {
                });
            } catch (IOException ex) {
                Logger.getLogger(Act_LandingGear.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (IOException ex) {
            Logger.getLogger(Act_LandingGear.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {

            Channel ch6 = cn.createChannel();
            ch6.exchangeDeclare(landExchange1, "direct");
            String qName1 = ch6.queueDeclare().getQueue();
            ch6.queueBind(qName1, landExchange1, key14);

            try {
                ch6.basicConsume(qName1, true, (x, msg) -> {
                    String temp = new String(msg.getBody(), "UTF-8");
                    speed = Integer.parseInt(temp);
                    System.out.println("\t\tLANDING SYSTEM:\tTAKING CURRENT SPEED OF THE PLANE -> " + temp);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Act_LandingGear.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    landing();

                    try {
                        ch6.close();
                    } catch (IOException | TimeoutException ex) {
                        Logger.getLogger(Flight_Control.class.getName()).log(Level.SEVERE, null, ex);
                    }
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
