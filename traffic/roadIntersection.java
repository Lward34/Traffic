/*
*  File:roadIntersection.java
*  Author:Lilian Ward
*  Date: December 7, 2021
*  CMSC335
*  Purpose:Runnable Intersection class displays the traffic light colors
*
*/


import java.awt.Color;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.JLabel;

public class roadIntersection implements Runnable {

    //Array of colors to cycle through
    private final String[] COLORS = {"Green", "Yellow", "Red"};
    private int i = 0;
    private String currentLight = COLORS[i];
    private Thread thread;
    private String threadName;
    private JLabel tlc;
    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    public final AtomicBoolean suspended = new AtomicBoolean(false);

    public roadIntersection(String name, JLabel print) {
        this.threadName = name;
        this.tlc = print;
        System.out.println("Creating " + threadName);
    }

//Synchronized method for getting traffic light color
    public synchronized String getColor() {
        this.currentLight = COLORS[i];
        return this.currentLight;
    }

    public void start() {
        System.out.println("Starting " + getThreadName());
        if (getThread() == null) {
            setThread(new Thread(this, getThreadName()));
            getThread().start();
        }
    }

    public void stop() {
        getThread().interrupt();
        isRunning.set(false);
        System.out.println("Stopping " + getThreadName());
    }

    public void interrupt() {
        getThread().interrupt();//If light is sleeping, we can call interrupt to wake it when hitting "Pause" button
    }

    public void suspend() {
        suspended.set(true);
        System.out.println("Suspending " + getThreadName());
    }

    public synchronized void resume() {
        suspended.set(false);
        notify();
        System.out.println("Resuming " + getThreadName());
    }

    @Override
    public void run() {
        System.out.println("Running " + getThreadName());
        isRunning.set(true);
        while (isRunning.get()) {
            try {
                synchronized (this) {
                    while (suspended.get()) {
                        System.out.println(getThreadName() + " waiting");
                        wait();
                    }
                }
                switch (getColor()) {
                    case "Green":
                        tlc.setForeground(new Color(0, 204, 0));//Set font color to green
                        tlc.setText(getColor());
                        Thread.sleep(10000);//Stay green for 10 seconds
                        i++;
                        break;
                    case "Yellow":
                        tlc.setForeground(new Color(247, 226, 35)); //Font color yellow
                        tlc.setText(getColor());
                        Thread.sleep(5000);//Yellow for 5 seconds
                        i++;
                        break;
                    case "Red":
                        tlc.setForeground(new Color(204, 0, 0)); //Font color red
                        tlc.setText(getColor());

                        Thread.sleep(5000);//Red for 5 seconds
                        i = 0;//Set i back to 0
                        break;
                    default:
                        break;
                }
            } catch (InterruptedException ex) {//If thread gets interrupted, set suspended true
                System.out.println(getThreadName() + " Interrupted");
                suspended.set(true);
            }

        }
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

    /**
     * @return the threadName
     */
    public String getThreadName() {
        return threadName;
    }

    /**
     * @param threadName the threadName to set
     */
    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }
}
