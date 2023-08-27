package FC_LOGIC_MULTITHREAD;

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
        ExecutorService actEx = Executors.newFixedThreadPool(16);
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

        //INITIAL SYSTEM !!!!!!!!!!!!!! 1ST CHOICE
        //SENSOR | PRODUCER
        actEx.execute(sensor);
        actEx.execute(sensor1);
        actEx.execute(sensor2);
        actEx.execute(sensor3);
        actEx.execute(sensor4);

        //FLIGHT CONTROL LOGIC | MIDDLEMAN
        actEx.execute(() -> fc.Logic_Altitude());
        actEx.execute(() -> fc.Logic_Weather());
        actEx.execute(() -> fc.Logic_Cabin());
        actEx.execute(() -> fc.Logic_Speed());
        actEx.execute(() -> fc.Logic_Landing());

        //ACTUATOR / CONSUMER
        actEx.execute(wing);
        actEx.execute(tail);
        actEx.execute(oxy);
        actEx.execute(engine);
        actEx.execute(gear);
        ex.scheduleAtFixedRate(()
                -> {
            fc.Landing_Sign();
            if (Main.landed == true) {

                ex.shutdown();
                actEx.shutdown();

                System.out.println("LANDED SUCCESFULLY :) ");
                Statistics.report();
                exit(0);
            }
        }, 60000, 1, TimeUnit.MILLISECONDS);
    }
}
