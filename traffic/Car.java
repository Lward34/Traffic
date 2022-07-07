/*
*  File:Car.java
*  Author:Lilian Ward
*  Date: December 7, 2021
*  CMSC335
*  Purpose:Runnable Car class to increment xPosition and display speed.
*
*/


import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;

 /*
 * Code created based on zipfile examples 
 * provided at CMSC335
 */

public class Car implements Runnable {

    //variables
    private int xPos;  // the x position
    private final int yPos;    //the y position
    private String threadName = "";
    private Thread thread;
    private int speed = 0;
    private final AtomicBoolean isRunning = new AtomicBoolean(false); 
    public final AtomicBoolean trafficLight = new AtomicBoolean(false);//Boolean for trafficLight
    public final AtomicBoolean suspended = new AtomicBoolean(false);//Boolean for "suspend"

    //Constructor 
    public Car(String name, int max, int min) {
        this.yPos = 0;
        this.threadName = name;
        this.xPos = ThreadLocalRandom.current().nextInt(min, max);
    }

    public synchronized int getPosition() {
        return xPos;
    }

    /*
    * Incrementing 5 meters every 1/10th of a second.
    * Which thats 50 meters per second, 3000 meters per minute
    * then 3 km per minute * 60 for 180 kph (distance=Speed*time)
    */
    public int getSpeed() {
        if (isRunning.get()) {
            if (trafficLight.get()) {
                speed = 0;
            } else {
                speed = 3 * 60;
            }
        } else {
            speed = 0;
        }
        return speed;
    }

    /**
     * @return the thread
     */
    public Thread getThread() {
        return thread;
    }

    /**
     * @param thread the thread to set
     */
    public void setThread(Thread thread) {
        this.thread = thread;
    }

    // Creates and starting the threads 
    public void start() {
        System.out.println("Creating " + threadName);
        System.out.println("Starting " + threadName);
        if (getThread() == null) {
            setThread(new Thread(this, threadName));
            getThread().start();
        }
    }

    public void stop() {
        thread.interrupt(); //use "interrupt" to avoid unsafe deprecated "stop"
        isRunning.set(false);
        System.out.println("Stopping " + threadName);
        try {
            for (int n = 3; n > 0; n--) {//count 5-1 to stop thread.car
                System.out.println(n);
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) { // catch exception for interrupted
            System.out.println("Main thread interrupted");
        }
     
    }

    public void suspend() {
        suspended.set(true);
        System.out.println("Suspending " + threadName);
      
    }

    public synchronized void resume() { //If car is suspended, set suspended to false and notify
        if (suspended.get() || trafficLight.get()) {
            suspended.set(false);
            trafficLight.set(false);
            notify();
            System.out.println("Resuming " + threadName);
        }
    }

    @Override
    public void run() {
        System.out.println("Running " + threadName);
        isRunning.set(true);
        while (isRunning.get()) {
            try {
                while (xPos < 3000) {
                    synchronized (this) {
                        while (suspended.get() || trafficLight.get()) { // Get to suspended "pause" the threads until this is released it.
                            System.out.println(threadName + " waiting");
                            wait();// waits for condition to occur
                        }
                    }
                    if (isRunning.get()) { //Check if still running
                        Thread.sleep(100);
                        xPos += 5;
                    }
                }
                xPos = 0;
            } catch (InterruptedException ex) {               
                return;
            }

        }
    }// end run
}//end class
