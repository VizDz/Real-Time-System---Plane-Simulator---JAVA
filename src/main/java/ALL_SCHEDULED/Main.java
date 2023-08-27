package ALL_SCHEDULED;

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
      
        ExecutorService actEx = Executors.newFixedThreadPool(1);

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

       
        //MODIFIED SENSOR SENSOR AND CONSUMER | PRODUCER TO SCHEDULED THREAD....
        ScheduledExecutorService ex = Executors.newScheduledThreadPool(14);
        //SENSOR | PRODUCER
        ex.scheduleAtFixedRate(sensor, 0, 1, TimeUnit.SECONDS);
        ex.scheduleAtFixedRate(sensor1, 0, 1, TimeUnit.SECONDS);
        actEx.execute(sensor2);
        ex.scheduleAtFixedRate(sensor3, 0, 1, TimeUnit.SECONDS);
        ex.scheduleAtFixedRate(sensor4, 0, 1, TimeUnit.SECONDS);

        //ACTUATOR / CONSUMER
        ex.scheduleAtFixedRate(wing, 0, 1, TimeUnit.SECONDS);
        ex.scheduleAtFixedRate(tail, 0, 1, TimeUnit.SECONDS);
        ex.scheduleAtFixedRate(oxy, 0, 1, TimeUnit.SECONDS);
        ex.scheduleAtFixedRate(engine, 0, 1, TimeUnit.SECONDS);
         ex.scheduleAtFixedRate(gear, 0, 1, TimeUnit.SECONDS);

        
          
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
                
                System.out.println("LANDED SUCCESFULLY :) ");
                Statistics.report();
                exit(0);
            }
        }, 10, 1, TimeUnit.SECONDS);

    }
}
