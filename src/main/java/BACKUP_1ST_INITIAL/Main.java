package BACKUP_1ST_INITIAL;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TransferQueue;
import com.rabbitmq.client.*;
import java.io.IOException;
import static java.lang.System.exit;
import static java.time.Clock.system;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    static boolean prepLanding = false;
    static boolean isLanding = false;
    static boolean landed = false;

    public static void main(String[] args) throws IOException, TimeoutException {
        ScheduledExecutorService ex = Executors.newScheduledThreadPool(5);
        ExecutorService actEx = Executors.newFixedThreadPool(10);
        Sensor_Altitude sensor = new Sensor_Altitude();
        Sensor_Weather sensor1 = new Sensor_Weather();
        Sensor_Direction sensor2 = new Sensor_Direction();
        Sensor_Cabin sensor3 = new Sensor_Cabin();
        Sensor_Speed sensor4 = new Sensor_Speed();
        Flight_Control fc = new Flight_Control();
        Act_WingFlags wing = new Act_WingFlags();
        Act_TailFlags tail = new Act_TailFlags();
        Act_OxygenMask oxy = new Act_OxygenMask();
        Act_Engines engine = new Act_Engines();
        Act_LandingGear gear = new Act_LandingGear();
        
        
        //INITIAL SYSTEM WITH MIXED FIXED & SCHEDULED.. 2nd OPTION
        //SENSOR | PRODUCER
        actEx.execute(sensor);
        actEx.execute(sensor1);
        actEx.execute(sensor2);
        actEx.execute(sensor3);
        actEx.execute(sensor4);

        //ACTUATOR / CONSUMER
        actEx.execute(wing);
        actEx.execute(tail);
        actEx.execute(oxy);
        actEx.execute(engine);
        actEx.execute(gear);

        //FLIGHT CONTROL LOGIC | MIDDLEMAN
        ex.scheduleAtFixedRate(() -> fc.Logic_Altitude(), 0, 1, TimeUnit.SECONDS);
        ex.scheduleAtFixedRate(() -> fc.Logic_Weather(), 0, 1, TimeUnit.SECONDS);
        ex.scheduleAtFixedRate(() -> fc.Logic_Cabin(), 0, 1, TimeUnit.SECONDS);
        ex.scheduleAtFixedRate(() -> fc.Logic_Speed(), 0, 1, TimeUnit.SECONDS);
        ex.scheduleAtFixedRate(()
                -> {
            fc.Logic_Landing();
            if (Main.landed == true) {

                ex.shutdown();
                actEx.shutdown();

                System.out.println("LANDED SUCCESSFULLY :) ");
                Statistics.report();
                exit(0);
            }
        }, 10, 1, TimeUnit.SECONDS);

    }
}
