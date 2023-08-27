package com.mycompany.rts_plane_simulator;

import java.util.Random;
import com.rabbitmq.client.*;
import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Act_OxygenMask implements Runnable {

    private final Random rand = new Random();

    String cabExchange1 = "cabFlightToAct";
    String cabExchange2 = "cabActToSensor";
    private final String key8 = "cabinCon";
    private final String key9 = "newPressure";

    ConnectionFactory cf = new ConnectionFactory();
    private static Connection cn;
    private static Channel ch;

    public Act_OxygenMask() {
        try {
            cn = cf.newConnection();
            ch = cn.createChannel();
        } catch (IOException | TimeoutException ex) {
            Logger.getLogger(Act_OxygenMask.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        retrieveMsg();
    }

    public void retrieveMsg() {
        try {
            String qName = ch.queueDeclare().getQueue();
            ch.exchangeDeclare(cabExchange1, "direct");
            ch.queueBind(qName, cabExchange1, key8);
            try {
                ch.basicConsume(qName, true, (x, msg) -> {

                    String temp = new String(msg.getBody(), "UTF-8");
                    Statistics.reportA2.add((double) System.currentTimeMillis());      //SEND TO STATISTICS REPORT
                    int pre = Integer.parseInt(temp);
                    if (pre <= 40) {
                        System.out.println("OXYGEN SYSTEM:\tCABIN PRESSURE STATUS -> LOW || OXYGEN MASK OUT || PASSENGERS MUST WEAR THE MASK ");
                        while (pre <= 85) {
                            int change = rand.nextInt(15);
                            pre = pre + change;
                            temp = Integer.toString(pre);
                            System.out.println("OXYGEN SYSTEM:\tRESTORING CABIN PRESSURE.... [" + pre + "%]");
                        }
                        System.out.println("OXYGEN SYSTEM:\tCABIN PRESSSURE RESTORED :)");
                    } else {
                        System.out.println("OXYGEN SYSTEM:\tCABIN PRESSURE STATUS -> FINE || OXYGEN MASK ON HELD... ");

                    }

                    sendMsg(cabExchange2, key9, temp);
                }, x -> {
                });

            } catch (IOException ex) {
                Logger.getLogger(Act_OxygenMask.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (IOException ex) {
            Logger.getLogger(Act_OxygenMask.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void sendMsg(String exchange, String key, String msg) {
        try {
            ch.exchangeDeclare(exchange, "direct");
            ch.basicPublish(exchange, key, false, null, msg.getBytes());
            System.out.println("OXYGEN SYSTEM:\tSENT CURRENT CABIN PRESSURE -> " + msg);
        } catch (IOException ex) {
            Logger.getLogger(Act_OxygenMask.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
