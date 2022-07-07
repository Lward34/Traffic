/*
*  File:realTime.java
*  Author:Lilian Ward
*  Date: December 7, 2021
*  CMSC335
*  Purpose:Runnable Time class to get current time 
*  and constantly update current time in GUI.
*  
*/


import java.text.SimpleDateFormat;
import java.util.Date;

public class realTime implements Runnable {

    private boolean isRunning;
    private String wdTime = "hh:mm:ss a"; 
    private SimpleDateFormat dateTime = new SimpleDateFormat(wdTime);
    private Date actualDate;
 

    public realTime() {
        this.actualDate = new Date(System.currentTimeMillis());
        this.isRunning = Thread.currentThread().isAlive();
    }

    public String getTime() {
        actualDate = new Date(System.currentTimeMillis());
        return dateTime.format(actualDate);
    }

    @Override
    public void run() {
        while (isRunning) {  //While running, constantly update current time
            trafficPoint.Timer.setText(getTime());
        }
    }
}
