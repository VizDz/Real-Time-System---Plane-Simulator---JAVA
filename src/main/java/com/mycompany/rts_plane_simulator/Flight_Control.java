package com.mycompany.rts_plane_simulator;

import com.rabbitmq.client.*;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Flight_Control implements Runnable {

    private final String altExchange = "altSensorToFlight";
    private final String altExchange1 = "altFlightToAct";
    private final String key1 = "altitude";
    private final String key2 = "wing";

    private final String weatExchange = "weatSensorToFlight";
    private final String weatExchange1 = "weatFlightToAct";
    private final String key4 = "weather";
    private final String key5 = "tail";

    private final String cabExchange = "cabSensorToFlight";
    private final String cabExchange1 = "cabFlightToAct";
    private final String key7 = "pressure";
    private final String key8 = "cabinCon";

    private final String spExchange = "spSensorToFlight";
    private final String spExchange1 = "spFlightToAct";
    private final String key10 = "speed";
    private final String key11 = "speedCon";

    private final String landExchange = "landing";
    private final String key13 = "alt_land";
    private final String landExchange1 = "landing1";
    private final String key14 = "speed_land";

    private final String landExchange2 = "landing";
    private final String key15 = "alt_land_1";
    private final String landExchange3 = "landing1";
    private final String key16 = "speed_land_1";

    //String landExchange = "landing";
    //String landExchange1 = "landing1";
    ConnectionFactory cf = new ConnectionFactory();
    private static Connection cn;
    private static Channel ch;

    public Flight_Control() {
        try {
            cn = cf.newConnection();
            ch = cn.createChannel();
        } catch (IOException | TimeoutException ex) {
            Logger.getLogger(Sensor_Altitude.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        Logic_Altitude();
        Logic_Cabin();
        Logic_Weather();
        Logic_Speed();
        Logic_Landing();
    }

    public void Logic_Altitude() {
        try {
            ch.exchangeDeclare(altExchange, "direct");
            String qName = ch.queueDeclare().getQueue();
            ch.queueBind(qName, altExchange, key1);
            try {
                ch.basicConsume(qName, true, (x, msg) -> {
                    String temp = new String(msg.getBody(), "UTF-8");

                    String command = switch (temp) {
                        case "High" ->
                            "Down";
                        case "Low" ->
                            "Up";
                        default ->
                            "Nothing";
                    };
                    Statistics.reportF1.add((double) System.currentTimeMillis());      //SEND TO STATISTICS REPORT
                    System.out.println("FLIGHT CONTROL: RECEIVED ALTITUDE STATUS - " + temp + "||| SENT COMMAND TO WING FLAGS -> " + command);

                    //SEND MESSAGE TO WINGS
                    sendMsg(altExchange1, key2, command);

                }, x -> {
                });
            } catch (IOException ex) {
                Logger.getLogger(Flight_Control.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (IOException ex) {
            Logger.getLogger(Flight_Control.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void Logic_Weather() {
        try {

            ch.exchangeDeclare(weatExchange, "direct");
            String qName = ch.queueDeclare().getQueue();
            ch.queueBind(qName, weatExchange, key4); //queue, the exchange, the key
            try {
                ch.basicConsume(qName, true, (x, msg) -> {
                    String weather = new String(msg.getBody(), "UTF-8");

                    if (weather.equals("Good")) {
                        System.out.println("FLIGHT CONTROL:\tRECEIVED WEATHER CONDITION -> " + weather + " ||| SENT COMMAND TO TAIL FLAGS -> FOLLOW THE NORMAL LINE");
                    } else {
                        System.out.println("FLIGHT CONTROL:\tRECEIVED WEATHER CONDITION -> " + weather + " ||| SEND COMMAND TO TAIL FLAGS -> CHANGE TO THE SAFER LINE");
                    }
                    Statistics.reportF4.add((double) System.currentTimeMillis());      //SEND TO STATISTICS REPORT
                    //SEND MESSAGE TO TAIL
                    sendMsg(weatExchange1, key5, weather);

                }, x -> {
                });
            } catch (IOException ex) {
                Logger.getLogger(Flight_Control.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (IOException ex) {
            Logger.getLogger(Flight_Control.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void Logic_Cabin() {
        try {

            ch.exchangeDeclare(cabExchange, "direct");
            String qName = ch.queueDeclare().getQueue();
            ch.queueBind(qName, cabExchange, key7); //queue, the exchange, the key
            try {
                ch.basicConsume(qName, true, (x, msg) -> {
                    String temp = new String(msg.getBody(), "UTF-8");

                    int pre = Integer.parseInt(temp);
                    String condition;
                    if (pre <= 40) {
                        condition = "Low";
                    } else {
                        condition = "Fine";
                    }
                    Statistics.reportF2.add((double) System.currentTimeMillis());      //SEND TO STATISTICS REPORT
                    System.out.println("FLIGHT CONTROL:\tCURRENT CABIN PRESSURE STATUS -> " + condition + " | " + pre + "% => FORWARD THE STATUS TO OXYGEN SYSTEM");

                    sendMsg(cabExchange1, key8, temp);

                }, x -> {
                });
            } catch (IOException ex) {
                Logger.getLogger(Flight_Control.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (IOException ex) {
            Logger.getLogger(Flight_Control.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void Logic_Speed() {
        try {

            ch.exchangeDeclare(spExchange, "direct");
            String qName = ch.queueDeclare().getQueue();
            ch.queueBind(qName, spExchange, key10);
            try {
                ch.basicConsume(qName, true, (x, msg) -> {
                    String temp = new String(msg.getBody(), "UTF-8");

                    String command;

                    if (temp.equals("Fast")) {
                        command = "Decrease";
                    } else if (temp.equals("Slow")) {
                        command = "Increase";
                    } else {
                        command = "Nothing";
                    }
                    Statistics.reportF3.add((double) System.currentTimeMillis());      //SEND TO STATISTICS REPORT
                    System.out.println("FLIGHT CONTROL: RECEIVED SPEED STATUS - " + temp + " ||| SENT COMMAND TO ENGINES -> " + command);

                    sendMsg(spExchange1, key11, command);

                }, x -> {
                });
            } catch (IOException ex) {
                Logger.getLogger(Flight_Control.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (IOException ex) {
            Logger.getLogger(Flight_Control.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void Logic_Landing() {
        try {
            ch.exchangeDeclare(landExchange, "direct");
            String qName = ch.queueDeclare().getQueue();
            ch.queueBind(qName, landExchange, key13);
            try {
                ch.basicConsume(qName, true, (x, msg) -> {
                    String temp = new String(msg.getBody(), "UTF-8");
                    System.out.println("FLIGHT CONTROL: RECEIVED CURRENT ALTITUDE TO PREPARE FOR LANDING -> " + temp);
                    //SEND MESSAGE TO LANDING GEAR
                    sendMsg(landExchange2, key15, temp);
                }, x -> {
                });
            } catch (IOException ex) {
                Logger.getLogger(Flight_Control.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (IOException ex) {
            Logger.getLogger(Flight_Control.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            ch.exchangeDeclare(landExchange1, "direct");
            String qName = ch.queueDeclare().getQueue();
            ch.queueBind(qName, landExchange1, key14);
            try {
                ch.basicConsume(qName, true, (x, msg) -> {
                    String temp = new String(msg.getBody(), "UTF-8");
                    System.out.println("FLIGHT CONTROL: RECEIVED CURRENT SPEED TO PREPARE FOR LANDING -> " + temp);
                    //SEND MESSAGE TO LANDING GEAR
                    sendMsg(landExchange3, key16, temp);

                }, x -> {
                });
            } catch (IOException ex) {
                Logger.getLogger(Flight_Control.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (IOException ex) {
            Logger.getLogger(Flight_Control.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void sendMsg(String exchange, String key, String msg) {
        try {
            ch.exchangeDeclare(exchange, "direct");
            ch.basicPublish(exchange, key, false, null, msg.getBytes());

        } catch (IOException ex) {
            Logger.getLogger(Flight_Control.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void Landing_Sign() {

        //System.out.println("FLIGHT CONTROL : Landing in progress....");
        Main.prepLanding = true;
//        try {
//            Thread.sleep(schedule);
//            System.out.println("FLIGHT CONTROL : Landing in progress....");
//            Main.prepLanding = true;
//        } catch (InterruptedException ex) {
//            Logger.getLogger(Act_LandingGear.class.getName()).log(Level.SEVERE, null, ex);
//        }
        //System.out.println("[FLIGHT CONTROL : PREPARATION FOR LANDING -> " + Main.prepLanding);
    }
}
