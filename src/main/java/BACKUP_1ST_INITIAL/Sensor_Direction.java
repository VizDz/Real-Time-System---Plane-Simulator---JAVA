package BACKUP_1ST_INITIAL;
import java.util.Random;
import com.rabbitmq.client.*;
import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Sensor_Direction implements Runnable {

    private final Random rand = new Random();

    ConnectionFactory cf = new ConnectionFactory();
    String dirExchange = "dirFlightToAct";
    private final String key6 = "dir";
    private static String dir = "Same";

    private static Connection cn;
    private static Channel ch;

    public Sensor_Direction() {
        try {
            cn = cf.newConnection();
            ch = cn.createChannel();
        } catch (IOException | TimeoutException ex) {
            Logger.getLogger(Sensor_Direction.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        getMsg(dirExchange, key6);
    }

    public void getMsg(String exchange, String key) {
        try {
            String qName = ch.queueDeclare().getQueue();
            ch.exchangeDeclare(exchange, "direct");
            ch.queueBind(qName, exchange, key);
            try {
                ch.basicConsume(qName, false, (x, msg) -> {
                    if (msg != null) {
                        String CurrentDir = new String(msg.getBody(), "UTF-8");
                        int way = rand.nextInt(100);
                        if (CurrentDir.equals("Bad")) {
                            if (way <= 25) {
                                dir = "North";
                            } else if (way <= 50) {
                                dir = "South";

                            } else if (way <= 75) {
                                dir = "East";

                            } else {
                                dir = "West";
                            }
                        } else {
                            dir = "Same";
                        }
                    } else {
                        dir = "Same";
                    }
                    System.out.println("DIR SENSOR:\tCURRENT DIRECTION FROM ACTUAL PATH LINE -> " + dir);
                }, x -> {
                });
            } catch (IOException ex) {
                Logger.getLogger(Sensor_Direction.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (IOException ex) {
            Logger.getLogger(Sensor_Direction.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
