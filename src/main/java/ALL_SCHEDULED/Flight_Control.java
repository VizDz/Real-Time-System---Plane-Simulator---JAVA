package ALL_SCHEDULED;

import com.rabbitmq.client.*;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Flight_Control {

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

    public void Logic_Altitude() {
        try {
            //Connection cn1 = cf.newConnection();
            Channel ch1 = cn.createChannel();
            ch1.exchangeDeclare(altExchange, "direct");
            String qName = ch1.queueDeclare().getQueue();
            ch1.queueBind(qName, altExchange, key1);
            try {
                ch1.basicConsume(qName, true, (x, msg) -> {
                    String temp = new String(msg.getBody(), "UTF-8");
                    Statistics.reportF1.add((double) System.currentTimeMillis());      //SEND TO STATISTICS REPORT
                    String command = switch (temp) {
                        case "High" ->
                            "Down";
                        case "Low" ->
                            "Up";
                        default ->
                            "Nothing";
                    };
                    System.out.println("FLIGHT CONTROL: RECEIVED ALTITUDE STATUS - " + temp + "||| SENT COMMAND TO WING FLAGS -> " + command);

                    //SEND MESSAGE TO WINGS
                    sendMsg(altExchange1, key2, command);

                    try {

                        ch1.close();

                    } catch (IOException | TimeoutException ex) {
                        Logger.getLogger(Flight_Control.class.getName()).log(Level.SEVERE, null, ex);
                    }
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
            //Connection cn2 = cf.newConnection();
            Channel ch2 = cn.createChannel();
            ch2.exchangeDeclare(weatExchange, "direct");
            String qName = ch2.queueDeclare().getQueue();
            ch2.queueBind(qName, weatExchange, key4); //queue, the exchange, the key
            try {
                ch2.basicConsume(qName, true, (x, msg) -> {
                    String weather = new String(msg.getBody(), "UTF-8");
                    Statistics.reportF4.add((double) System.currentTimeMillis());      //SEND TO STATISTICS REPORT
                    if (weather.equals("Good")) {
                        System.out.println("FLIGHT CONTROL:\tRECEIVED WEATHER CONDITION -> " + weather + " ||| SENT COMMAND TO TAIL FLAGS -> FOLLOW THE NORMAL LINE");
                    } else {
                        System.out.println("FLIGHT CONTROL:\tRECEIVED WEATHER CONDITION -> " + weather + " ||| SEND COMMAND TO TAIL FLAGS -> CHANGE TO THE SAFER LINE");
                    }

                    //SEND MESSAGE TO TAIL
                    sendMsg(weatExchange1, key5, weather);

                    try {
                        ch2.close();
                    } catch (IOException | TimeoutException ex) {
                        Logger.getLogger(Flight_Control.class.getName()).log(Level.SEVERE, null, ex);
                    }
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
            Channel ch3 = cn.createChannel();
            ch3.exchangeDeclare(cabExchange, "direct");
            String qName = ch3.queueDeclare().getQueue();
            ch3.queueBind(qName, cabExchange, key7); //queue, the exchange, the key
            try {
                ch3.basicConsume(qName, true, (x, msg) -> {
                    String temp = new String(msg.getBody(), "UTF-8");
                    Statistics.reportF2.add((double) System.currentTimeMillis());      //SEND TO STATISTICS REPORT
                    int pre = Integer.parseInt(temp);
                    String condition;
                    if (pre <= 40) {
                        condition = "Low";
                    } else {
                        condition = "Fine";
                    }
                    System.out.println("FLIGHT CONTROL:\tCURRENT CABIN PRESSURE STATUS -> " + condition + " | " + pre + "% => FORWARD THE STATUS TO OXYGEN SYSTEM");

                    sendMsg(cabExchange1, key8, temp);

                    try {
                        ch3.close();
                    } catch (IOException | TimeoutException ex) {
                        Logger.getLogger(Flight_Control.class.getName()).log(Level.SEVERE, null, ex);
                    }
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
            //Connection cn = cf.newConnection();
            Channel ch4 = cn.createChannel();
            ch4.exchangeDeclare(spExchange, "direct");
            String qName = ch4.queueDeclare().getQueue();
            ch4.queueBind(qName, spExchange, key10);
            try {
                ch4.basicConsume(qName, true, (x, msg) -> {
                    String temp = new String(msg.getBody(), "UTF-8");
                    String command;
                    Statistics.reportF3.add((double) System.currentTimeMillis());      //SEND TO STATISTICS REPORT
                    if (temp.equals("Fast")) {
                        command = "Decrease";
                    } else if (temp.equals("Slow")) {
                        command = "Increase";
                    } else {
                        command = "Nothing";
                    }
                    System.out.println("FLIGHT CONTROL: RECEIVED SPEED STATUS - " + temp + " ||| SENT COMMAND TO ENGINES -> " + command);

                    sendMsg(spExchange1, key11, command);

                    try {
                        ch4.close();

                    } catch (IOException | TimeoutException ex) {
                        Logger.getLogger(Flight_Control.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }, x -> {
                });
            } catch (IOException ex) {
                Logger.getLogger(Flight_Control.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (IOException ex) {
            Logger.getLogger(Flight_Control.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void sendMsg(String channel, String key, String msg) {
        try {
            ch = cn.createChannel();
            ch.exchangeDeclare(channel, "direct");
            ch.basicPublish(channel, key, false, null, msg.getBytes());

        } catch (IOException ex) {
            Logger.getLogger(Flight_Control.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void Logic_Landing() {
        System.out.println("FLIGHT CONTROL : Landing in progress....");
        Main.prepLanding = true;
        //System.out.println("[FLIGHT CONTROL : PREPARATION FOR LANDING -> " + Main.prepLanding);

    }
}
