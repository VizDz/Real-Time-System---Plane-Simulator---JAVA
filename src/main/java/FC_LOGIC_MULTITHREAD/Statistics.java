/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package FC_LOGIC_MULTITHREAD;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;

public class Statistics {

    public static ArrayList<Double> reportS1 = new ArrayList<Double>();     //ALTITUDE
    public static ArrayList<Double> reportS2 = new ArrayList<Double>();     //CABIN PRESSURE
    public static ArrayList<Double> reportS3 = new ArrayList<Double>();     //SPEED
    public static ArrayList<Double> reportS4 = new ArrayList<Double>();     //WEATHER

    public static ArrayList<Double> reportF1 = new ArrayList<Double>();     //FC ALTITUDE
    public static ArrayList<Double> reportF2 = new ArrayList<Double>();      //FC CABIN PRESSURE
    public static ArrayList<Double> reportF3 = new ArrayList<Double>();     //FC SPEED
    public static ArrayList<Double> reportF4 = new ArrayList<Double>();     //FC WEATHER

    public static ArrayList<Double> reportA1 = new ArrayList<Double>();     //ACT WING
    public static ArrayList<Double> reportA2 = new ArrayList<Double>();      //ACT OXY
    public static ArrayList<Double> reportA3 = new ArrayList<Double>();     //ACT ENGINE
    public static ArrayList<Double> reportA4 = new ArrayList<Double>();     //ACT TAIL

    private static ArrayList<Double> reportSF1 = new ArrayList<Double>();     //SENSOR ALT TO FC
    private static ArrayList<Double> reportSF2 = new ArrayList<Double>();     //SENSOR PRES TO FC
    private static ArrayList<Double> reportSF3 = new ArrayList<Double>();     //SENSOR SPEED TO FC
    private static ArrayList<Double> reportSF4 = new ArrayList<Double>();     //SENSOR WEAT TO FC 

    private static ArrayList<Double> reportFA1 = new ArrayList<Double>();     //FC TO WING
    private static ArrayList<Double> reportFA2 = new ArrayList<Double>();     //FC TO OXY
    private static ArrayList<Double> reportFA3 = new ArrayList<Double>();     //FC TO ENG
    private static ArrayList<Double> reportFA4 = new ArrayList<Double>();     //FC TO TAIL

    private static ArrayList<Double> reportSA1 = new ArrayList<Double>();     //SENSPR TO WING
    private static ArrayList<Double> reportSA2 = new ArrayList<Double>();     //SENSOR TO OXY
    private static ArrayList<Double> reportSA3 = new ArrayList<Double>();     //SENSOR TO ENG
    private static ArrayList<Double> reportSA4 = new ArrayList<Double>();     //SENSOR TO TAIL

    private static final DecimalFormat df = new DecimalFormat("0.00");

    private static int cnt1 = 0;
    private static int cnt2 = 0;
    private static int cnt3 = 0;
    private static int cnt4 = 0;
    private static int cnt5 = 0;
    private static int cnt6 = 0;
    private static int cnt7 = 0;
    private static int cnt8 = 0;

    private static double tot1 = 0;
    private static double tot2 = 0;
    private static double tot3 = 0;
    private static double tot4 = 0;
    private static double tot5 = 0;
    private static double tot6 = 0;
    private static double tot7 = 0;
    private static double tot8 = 0;
    private static double tot9 = 0;
    private static double tot10 = 0;
    private static double tot11 = 0;
    private static double tot12 = 0;

    public static void report() {

        for (Double value : reportF1) {
            Double x = (reportF1.get(cnt1) - reportS1.get(cnt1));
            reportSF1.add(x);
            tot1 = tot1 + x;
            cnt1 = cnt1 + 1;
        }
        for (Double value : reportF2) {
            Double x = (reportF2.get(cnt2) - reportS2.get(cnt2));
            reportSF2.add(x);
            tot2 = tot2 + x;
            cnt2 = cnt2 + 1;
        }
        for (Double value : reportF3) {
            Double x = (reportF3.get(cnt3) - reportS3.get(cnt3));
            reportSF3.add(x);
            tot3 = tot3 + x;
            cnt3 = cnt3 + 1;
        }
        for (Double value : reportF4) {
            Double x = (reportF4.get(cnt4) - reportS4.get(cnt4));
            reportSF4.add(x);
            tot4 = tot4 + x;
            cnt4 = cnt4 + 1;
        }
        for (Double value : reportA1) {
            Double x = (reportA1.get(cnt5) - reportF1.get(cnt5));
            Double y = (reportA1.get(cnt5) - reportS1.get(cnt5));
            reportSA1.add(y);
            tot9 = tot9 + y;
            reportFA1.add(x);
            tot5 = tot5 + x;
            cnt5 = cnt5 + 1;
        }
        for (Double value : reportA2) {
            Double x = (reportA2.get(cnt6) - reportF2.get(cnt6));
            Double y = (reportA2.get(cnt6) - reportS2.get(cnt6));
            reportSA2.add(y);
            tot10 = tot10 + y;
            reportFA2.add(x);
            tot6 = tot6 + x;
            cnt6 = cnt6 + 1;

        }
        for (Double value : reportA3) {
            double x = (reportA3.get(cnt7) - reportF3.get(cnt7));
            Double y = (reportA3.get(cnt7) - reportS3.get(cnt7));
            reportSA3.add(y);
            tot11 = tot11 + y;
            reportFA3.add(x);
            tot7 = tot7 + x;
            cnt7 = cnt7 + 1;
        }
        for (Double value : reportA4) {
            double x = (reportA4.get(cnt8) - reportF4.get(cnt8));
            Double y = (reportA4.get(cnt8) - reportS4.get(cnt8));
            reportSA4.add(y);
            tot12 = tot12 + y;
            reportFA4.add(x);

            tot8 = tot8 + x;
            cnt8 = cnt8 + 1;
        }
        System.out.println("\n==================\n    STATISTICS\n==================\n");
        System.out.println("======== ALTITUDE REPORT ========");
        System.out.println("Max Time (Sensor -> Flight):\t" + Collections.max(reportSF1) + " miliseconds");
        System.out.println("Min Time (Sensor -> Flight):\t" + Collections.min(reportSF1) + " miliseconds");
        System.out.println("Total Time (Sensor -> Flight):\t" + tot1 + " miliseconds");
        System.out.println("Average Time (Sensor -> Flight):" + df.format(tot1 / reportSF1.size()) + " miliseconds");

        System.out.println("\nMax Time (Flight -> Wing):\t" + Collections.max(reportFA1) + " miliseconds");
        System.out.println("Min Time (Flight -> Wing):\t" + Collections.min(reportFA1) + " miliseconds");
        System.out.println("Total Time (Flight -> Wing):\t" + tot5 + " miliseconds");
        System.out.println("Average Time (Flight -> Wing):\t" + df.format(tot5 / reportFA1.size()) + " miliseconds");

        System.out.println("\nMax Time (Sensor -> Wing):\t" + Collections.max(reportSA1) + " miliseconds");
        System.out.println("Min Time (Sensor -> Wing):\t" + Collections.min(reportSA1) + " miliseconds");
        System.out.println("Total Time (Sensor -> Wing):\t" + tot9 + " miliseconds");
        System.out.println("Average Time (Sensor -> Wing):\t" + df.format(tot9 / reportSA1.size()) + " miliseconds");

        System.out.println("\n======== CABIN PRESSURE REPORT ========");
        System.out.println("Max Time (Sensor -> Flight):\t\t" + Collections.max(reportSF2) + " miliseconds");
        System.out.println("Min Time (Sensor -> Flight):\t\t" + Collections.min(reportSF2) + " miliseconds");
        System.out.println("Total Time (Sensor -> Flight):\t\t" + tot2 + " miliseconds");
        System.out.println("Average Time (Sensor -> Flight):\t" + df.format(tot2 / reportSF2.size()) + " miliseconds");

        System.out.println("\nMax Time (Flight -> Oxygen System):\t\t" + Collections.max(reportFA2) + " miliseconds");
        System.out.println("Min Time (Flight -> Oxygen System):\t\t" + Collections.min(reportFA2) + " miliseconds");
        System.out.println("Total Time (Flight -> Oxygen System):\t\t" + tot6 + " miliseconds");
        System.out.println("Average Time (Flight -> Oxygen System):\t\t" + df.format(tot6 / reportFA2.size()) + " miliseconds");

        System.out.println("\nMax Time (Sensor -> Oxygen System):\t\t" + Collections.max(reportSA2) + " miliseconds");
        System.out.println("Min Time (Sensor -> Oxygen System):\t\t" + Collections.min(reportSA2) + " miliseconds");
        System.out.println("Total Time (Sensor -> Oxygen System):\t\t" + tot10 + " miliseconds");
        System.out.println("Average Time (Sensor -> Oxygen System):\t\t" + df.format(tot10 / reportSA2.size()) + " miliseconds");

        System.out.println("\n======== SPEED REPORT ========");
        System.out.println("Max Time (Sensor -> Flight):\t\t" + Collections.max(reportSF3) + " miliseconds");
        System.out.println("Min Time (Sensor -> Flight):\t\t" + Collections.min(reportSF3) + " miliseconds");
        System.out.println("Total Time (Sensor -> Flight):\t\t" + tot3 + " miliseconds");
        System.out.println("Average Time (Sensor -> Flight):\t" + df.format(tot3 / reportSF3.size()) + " miliseconds");

        System.out.println("\nMax Time (Flight -> Engine):\t\t" + Collections.max(reportFA3) + " miliseconds");
        System.out.println("Min Time (Flight -> Engine):\t\t" + Collections.min(reportFA3) + " miliseconds");
        System.out.println("Total Time (Flight -> Engine):\t\t" + tot7 + " miliseconds");
        System.out.println("Average Time (Flight -> Engine):\t" + df.format(tot7 / reportFA3.size()) + " miliseconds");

        System.out.println("\nMax Time (Sensor -> Engine):\t\t" + Collections.max(reportSA3) + " miliseconds");
        System.out.println("Min Time (Sensor -> Engine):\t\t" + Collections.min(reportSA3) + " miliseconds");
        System.out.println("Total Time (Sensor -> Engine):\t\t" + tot11 + " miliseconds");
        System.out.println("Average Time (Sensor -> Engine):\t" + df.format(tot11 / reportSA3.size()) + " miliseconds");

        System.out.println("\n======== WEATHER REPORT ========");
        System.out.println("Max Time (Sensor -> Flight):\t\t" + Collections.max(reportSF4) + " miliseconds");
        System.out.println("Min Time (Sensor -> Flight):\t\t" + Collections.min(reportSF4) + " miliseconds");
        System.out.println("Total Time (Sensor -> Flight):\t\t" + tot4 + " miliseconds");
        System.out.println("Average Time (Sensor -> Flight):\t" + df.format(tot4 / reportSF4.size()) + " miliseconds");

        System.out.println("\nMax Time (Flight -> Tail):\t\t" + Collections.max(reportFA4) + " miliseconds");
        System.out.println("Min Time (Flight -> Tail):\t\t" + Collections.min(reportFA4) + " miliseconds");
        System.out.println("Total Time (Flight -> Tail):\t\t" + tot8 + " miliseconds");
        System.out.println("Average Time (Flight -> Tail):\t\t" + df.format(tot8 / reportFA4.size()) + " miliseconds");

        System.out.println("\nMax Time (Sensor -> Tail):\t\t" + Collections.max(reportSA4) + " miliseconds");
        System.out.println("Min Time (Sensor -> Tail):\t\t" + Collections.min(reportSA4) + " miliseconds");
        System.out.println("Total Time (Sensor -> Tail):\t\t" + tot12 + " miliseconds");
        System.out.println("Average Time (Sensor -> Tail):\t\t" + df.format(tot12 / reportSA4.size()) + " miliseconds");

        System.out.println("\n\n=================================\nF0R BENCHMARKING SENSOR TO FLIGHT\n==================================================");
        System.out.println("\t\tALTITUDE SENSOR -----> FLIGHT CONTROL = ");
        for (Double value : reportSF1) {
            System.out.println(value);
        }
        System.out.println("\t\tCABIN PRESSURE -----> FLIGHT CONTROL = ");
        for (Double value : reportSF2) {
            System.out.println(value);
        }
        System.out.println("\t\tSPEED SENSOR -----> FLIGHT CONTROL = ");
        for (Double value : reportSF3) {
            System.out.println(value);
        }
        System.out.println("\t\tWEATHER SENSOR -----> FLIGHT CONTROL = ");
        for (Double value : reportSF4) {
            System.out.println(value);

        }

        System.out.println("\n\n=================================\nF0R BENCHMARKING FLIGHT TO ACT\n==================================================");

        System.out.println("\t\tFLIGHT CONTROL  ----> WING ");
        for (Double value : reportFA1) {
            System.out.println(value);

        }
        System.out.println("\t\tFLIGHT CONTROL  ----> OXYGEN ");
        for (Double value : reportFA2) {
            System.out.println(value);

        }
        System.out.println("\t\tFLIGHT CONTROL  ----> ENGINE ");
        for (Double value : reportFA3) {
            System.out.println(value);

        }
        System.out.println("\t\tFLIGHT CONTROL  ----> TAIL ");
        for (Double value : reportFA4) {
            System.out.println(value);

        }

        System.out.println("\n\n=================================\nF0R BENCHMARKING SENSOR TO ACT\n==================================================");
        System.out.println("\t\tALTITUDE SENSOR -----> WING = ");
        for (Double value : reportSA1) {
            System.out.println(value);
        }
        System.out.println("\t\tCABIN PRESSURE -----> OXYGEN = ");
        for (Double value : reportSA2) {
            System.out.println(value);
        }
        System.out.println("\t\tSPEED SENSOR ----->ENGINE = ");
        for (Double value : reportSA3) {
            System.out.println(value);
        }
        System.out.println("\t\tWEATHER SENSOR -----> TAIL = ");
        for (Double value : reportSA4) {
            System.out.println(value);

        }

    }

}
