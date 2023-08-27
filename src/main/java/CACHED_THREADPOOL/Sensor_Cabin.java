package CACHED_THREADPOOL;

import java.util.Random;
import com.rabbitmq.client.*;
import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Sensor_Cabin implements Runnable {

    private final Random rand = new Random();

    private final int min = 40;
    private final int max = 90;

    private final String cabExchange = "cabSensorToFlight";
    private final String cabExchange2 = "cabActToSensor";
    ConnectionFactory cf = new ConnectionFactory();
    private final String key7 = "pressure";
    private final String key9 = "newPressure";

    private static Connection cn;
    private static Channel ch;

    public Sensor_Cabin() {
        try {
            cn = cf.newConnection();
            ch = cn.createChannel();
        } catch (IOException | TimeoutException ex) {
            Logger.getLogger(Sensor_Cabin.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public class CurrentPres {

        private static int pres;

        // Getter for currentAlt
        public static int getPres() {
            return pres;
        }

        // Setter for currentAlt
        public static void setPres(int current_3) {
            pres = current_3;
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(1000);
                if (Main.prepLanding == false) {

                    String itemKey = cabPressure();
                    sendMsg(cabExchange, key7, itemKey);
                    getMsg(cabExchange2, key9);
                }
                
            } catch (InterruptedException ex) {
                Logger.getLogger(Act_LandingGear.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    public String cabPressure() {
       int currentP = CurrentPres.getPres();
        int change = rand.nextInt(20);
        int a = currentP;
        if (currentP == 0) {
            currentP = max - 10;
        }
        if (rand.nextBoolean()) {
            change *= -1;
        }
        if (currentP > max) {
            change = currentP - max;
            currentP = max;
        } else {
            currentP += change;
        }

        CurrentPres.setPres(currentP);

        System.out.println("CABIN SENSOR:\tCURRENT PRESSURE = " + a + "% ||| CHANGE = " + change + "& ||| LATEST PRESSURE ->  " + currentP + "%");
        Statistics.reportS2.add((double) System.currentTimeMillis());      //SEND TO STATISTICS REPORT
        return Integer.toString(currentP);

    }

    public void sendMsg(String exchange, String key, String msg) {
        try {
            ch.exchangeDeclare(exchange, "direct"); //name, type
            ch.basicPublish(exchange, key, false, null, msg.getBytes());
            System.out.println("CABIN SENSOR:\tSENT CURRENT PRESSURE STATUS TO FLIGHT CONTROL SYSTEM -> " + msg);
        } catch (IOException ex) {
            Logger.getLogger(Sensor_Cabin.class.getName()).log(Level.SEVERE, null, ex);
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
                    CurrentPres.setPres(add);
                    try {
                        ch1.close();
                    } catch (IOException | TimeoutException ex) {
                        Logger.getLogger(Sensor_Cabin.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }, x -> {
                });
            } catch (IOException ex) {
                Logger.getLogger(Sensor_Cabin.class.getName()).log(Level.SEVERE, null, ex);
            }

        } catch (IOException ex) {
            Logger.getLogger(Sensor_Cabin.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
