package CACHED_THREADPOOL;

import java.util.concurrent.Executors;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import java.io.IOException;
import static java.lang.System.exit;

import java.util.concurrent.ExecutorService;

import java.util.concurrent.TimeoutException;

public class Main {

    static boolean prepLanding = false;
    static boolean isLanding = false;
    static boolean landed = false;

    public static void main(String[] args) throws IOException, TimeoutException {
        ScheduledExecutorService ex = Executors.newScheduledThreadPool(1);
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

        //CACHED THREAD
        ExecutorService cacEx = Executors.newCachedThreadPool();
        //SENSOR | PRODUCER
        cacEx.execute(sensor);
        cacEx.execute(sensor1);
        cacEx.execute(sensor2);
        cacEx.execute(sensor3);
        cacEx.execute(sensor4);

        //ACTUATOR / CONSUMER
        cacEx.execute(wing);
        cacEx.execute(tail);
        cacEx.execute(oxy);
        cacEx.execute(engine);
        cacEx.execute(gear);

        //FLIGHT CONTROL LOGIC | MIDDLEMAN
        cacEx.execute(() -> fc.Logic_Altitude());
        cacEx.execute(() -> fc.Logic_Weather());
        cacEx.execute(() -> fc.Logic_Cabin());
        cacEx.execute(() -> fc.Logic_Speed());
        cacEx.execute(() -> fc.Logic_Landing());
        ex.scheduleAtFixedRate(()
                -> {
            fc.Landing_Sign();
            if (Main.landed == true) {

                ex.shutdown();
                cacEx.shutdown();

                System.out.println("LANDED SUCCESFULLY :) ");
                Statistics.report();
                exit(0);
            }
        }, 60000, 1, TimeUnit.MILLISECONDS);

    }
}
