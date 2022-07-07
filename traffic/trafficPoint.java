/*
*  File: trafficPoint.java
*  Author:Lilian Ward
*  Date: December 7, 2021
*  CMSC335
*  Purpose:trafficPoint class creates the GUI that displays the current time, 
*  traffic signals, and other information for traffic analysts. 
*  Including viewing ports/panels showing all the simulation components 
*  required to accomplish this project.
*
*/



import javax.swing.JOptionPane;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class trafficPoint extends JFrame implements Runnable, ChangeListener {

    //variables declaration
    private int numberOfCars;
    private static boolean isRunning;
    private static final AtomicBoolean simIsRunning = new AtomicBoolean(false);
    
    //Ceate label for the timer
    static JLabel Timer = new JLabel();
    private static final long serialVersionUID = 1L;

    //JButtons start, pause, and stop traffic lights
    private JButton start = new JButton("Start");
    private JButton pause = new JButton("Pause");
    private JButton stop = new JButton("Stop");

    //Create label Traffic 1,2,3
    static JLabel Traffic1 = new JLabel();
    static JLabel Traffic2 = new JLabel();
    static JLabel Traffic3 = new JLabel();
    
    //JSliders for showing car progress
    static JSlider slider1 = new JSlider(0, 3000);
    static JSlider slider2 = new JSlider(0, 3000);
    static JSlider slider3 = new JSlider(0, 3000);

    //Create 3 runnable intersection objects, each on their own thread
    roadIntersection A = new roadIntersection("Thread: intA", Traffic1);
    roadIntersection B = new roadIntersection("Thread: intB", Traffic2);
    roadIntersection C = new roadIntersection("Thread: intC", Traffic3);

    //Creates up to three runnable Car objects with a thread for each one
    Car car1 = new Car("Thread:Car1", 300, 0);
    Car car2 = new Car("Thread:Car2", 1000, 0);
    Car car3 = new Car("Thread:Car3", 2000, 1000);

    //Array of cars to loop through later
    Car[] carArray = {car1, car2, car3};

    // creates three intersections threads
    roadIntersection[] intersectionArray = {A, B, C};
    static Thread gui;

    //get rowData position
    Object[][] trafficData = {
        {"Car1", car1.getPosition(), 0, 0},
        {"Car2", car2.getPosition(), 0, 0},
        {"Car3", car3.getPosition(), 0, 0}
    };

//Table for displaying data
    String[] listNames = {"Car", "X-Pos", "Y-Pos", "Speed km/h"};//ColumnNames
    JTable dataTable = new JTable(trafficData, listNames);// rowsData and ColumnNames

//-- end class variable declarations    
 public static void main(String[] args) {
        trafficPoint test = new trafficPoint();
        test.display();
        gui = new Thread(test);
        Thread time = new Thread(new realTime());
        time.start();

    }   
          
    //constructor 
    public trafficPoint() {
        super("Traffic Tracker");
        isRunning = Thread.currentThread().isAlive();
        GuiMain();
        setButtons();
        
    }
    
    // creates frame
    private void display() {
        setSize(600, 400);
        setVisible(true);
        setResizable(false);
        setLocationRelativeTo(null);//Centers the frame on the screen
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//Sets the window to be closeable

    }

    //--------------------------------------Creates the GUI--------------------------------------
    private void GuiMain() {

        // Set Labels
        JLabel title = new JLabel("Welcome to UMGC Traffic Simulator Program!");
        JLabel title2 = new JLabel("Click the 'Start' to begin the Simulation");
        JLabel title3= new JLabel ("Note:Sliders represent the cars.Not the intersections");
        JLabel time = new JLabel("Current time: ");

        //traffic lights
        JLabel trafficLightControlA = new JLabel("Intersection A: ");
        JLabel trafficLightControlB = new JLabel("Intersection B: ");
        JLabel trafficLightControlC = new JLabel("Intersection C: ");

        //Add changeListeners to car sliders
        slider1.addChangeListener(this);
        slider2.addChangeListener(this);
        slider3.addChangeListener(this);

        slider1.setValue(car1.getPosition());
        slider2.setValue(car2.getPosition());
        slider3.setValue(car3.getPosition());

        slider1.setMajorTickSpacing(1000);
        slider1.setPaintTicks(true);

        slider2.setMajorTickSpacing(1000);
        slider2.setPaintTicks(true);

        // creates the ViewPort, set panel
        dataTable.setPreferredScrollableViewportSize(new Dimension(400, 70));
        dataTable.setFillsViewportHeight(true);
        JPanel dataPanel = new JPanel();

        //-------Creates the scroll pane and add the table to it
        JScrollPane scrollPane = new JScrollPane(dataTable);
        dataPanel.add(scrollPane);

        //-------------- dialog JOptionPane message to add up to three cars------------ 
        do {
            String sInput = JOptionPane.showInputDialog("How many cars (1-3)?");    // let the user enter input in the textfield
            if (sInput == null) {    // if the user press the "cancel" button
                System.exit(0);  // system closes it
            } else if (!sInput.equals("")) { // if the input is empty and the user presses the "Ok" button
                try {
                    numberOfCars = Integer.parseInt(sInput); //will keep repeatedly asking until the user enters the number of cars
                } catch (Exception ex) { //Or catches exception for invalid input by
                    System.out.println("We had an exception: " + ex);  // prints exception at output
                    JOptionPane.showMessageDialog(null, "Invalid Input"); // Displays a modal message dialog for "invalid input" and let the user try again.

                }

            }
        } while (numberOfCars < 1 || numberOfCars > 3); //Car numbers cannot be less than 1 or more than 3

        //--------------------------------------Creates the GUI Layout-------------------------------------
        //The following GUI layout code was generated by the NetBeans designer JFrame Form
        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);

        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        layout.setHorizontalGroup(layout.createSequentialGroup()
                .addContainerGap(30, 30) //Container gap on left side
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(title)
                        .addComponent(title2)
                        .addComponent(title3)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                        //---time
                                        .addComponent(time)
                                        .addComponent(Timer)))
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                                .addGroup(layout.createSequentialGroup()
                                        //---start,pause,stop
                                        .addComponent(start)
                                        .addComponent(pause)
                                        .addComponent(stop)))
                        //---slider 1,2,3
                        .addGroup(layout.createSequentialGroup()// creates 3 sliders per thread.car
                                .addComponent(slider1)
                                .addComponent(slider2)
                                .addComponent(slider3))
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                .addGroup(layout.createSequentialGroup()
                                        //---traffic control 1,2,3
                                        .addComponent(trafficLightControlA)
                                        .addComponent(Traffic1)
                                        .addContainerGap(20, 20)
                                        .addComponent(trafficLightControlB)
                                        .addComponent(Traffic2)
                                        .addContainerGap(20, 20)
                                        .addComponent(trafficLightControlC)
                                        .addComponent(Traffic3))
                                .addComponent(dataPanel)))
                .addContainerGap(30, 30) //Container gap on right side
        );
        layout.setVerticalGroup(layout.createSequentialGroup()
                .addGroup(layout.createSequentialGroup()
                        .addComponent(title)
                        .addComponent(title2)
                        .addComponent(title3))
                .addGap(20, 20, 20)
                //---time
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(time)
                        .addComponent(Timer))
                //---set start,pause,stop buttons
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(start)
                        .addComponent(pause)
                        .addComponent(stop))
                //---set sliders
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(slider1)
                        .addComponent(slider2)
                        .addComponent(slider3))
                //---set traffic
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(trafficLightControlA)
                        .addComponent(Traffic1)
                        .addComponent(trafficLightControlB)
                        .addComponent(Traffic2)
                        .addComponent(trafficLightControlC)
                        .addComponent(Traffic3))
                .addComponent(dataPanel)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addGap(20, 20, 20))
                .addGap(20, 20, 20)
        );
        pack();
    }// end of autogenerated code;
    //following code was manually developed.

    //------------setButtons functionality---------------
    private void setButtons() {
        //for initial operation: disable the 'pause' and 'stop' buttons, enable 'start'
        start.setEnabled(true);
        pause.setEnabled(false);
        stop.setEnabled(false);

        //-------------------Start car and intersection threads with start button-------------------
        // Handle action events.
        start.addActionListener((ActionEvent e) -> {
            if (!simIsRunning.get()) {
                System.out.println(Thread.currentThread().getName() + " calling start");
                // Start components
                //intersections A,B,C
                A.start();
                B.start();
                C.start();
                switch (numberOfCars) {//add controls for numberOfCars to allow add up to three cars
                    case 1:
                        car1.start();
                        gui.start();
                        break;
                    case 2:
                        car1.start();
                        car2.start();
                        gui.start();
                        break;
                    case 3:
                        car1.start();
                        car2.start();
                        car3.start();
                        gui.start();
                        break;
                }
            }

            //Set simIsRunning 
            simIsRunning.set(true);//Set simIsRunning to true
            //now enable pause and stop; disable start
            start.setEnabled(false);
            pause.setEnabled(true);
            stop.setEnabled(true);

        });

        //-------------------set Listeners for "pause" and "continue"-------------------
        // Handle action events.
        pause.addActionListener((ActionEvent e) -> {
            if (simIsRunning.get()) {
                for (int i = 0; i < numberOfCars; i++) { // updates the number of cars when pause/continue the threads
                    carArray[i].suspend();
                    System.out.println(Thread.currentThread().getName() + " calling suspend");
                }

                for (roadIntersection i : intersectionArray) { //Call interrupt for sleeping intersection threads
                    i.interrupt();
                    i.suspend();
                }
                pause.setText("Continue");
                simIsRunning.set(false);
                //disable stop - make them resume first
                stop.setEnabled(false);
            } else {
                for (int i = 0; i < numberOfCars; i++) {// updates the numberOfCars when suspended/resume
                    if (carArray[i].suspended.get()) {
                        carArray[i].resume();
                        System.out.println(Thread.currentThread().getName() + " calling resume");
                    }
                }

                for (roadIntersection i : intersectionArray) {
                    i.resume();
                }
                pause.setText("Pause");
                simIsRunning.set(true);
                //allow them to stop again now
                stop.setEnabled(true);
            }
        });

        //--------set Listener for "stop"-----
       // Handle action events.
        stop.addActionListener((ActionEvent e) -> {
            if (simIsRunning.get()) {
                System.out.println(Thread.currentThread().getName() + " calling stop");
                for (int index = 0; index < numberOfCars; index++) { //for loop to avoid nulls in the array
                    carArray[index].stop();
                }
                for (roadIntersection i : intersectionArray) {
                    i.stop();
                   
                }
                stop.setText("End");//Sets the window to be closeable);
                simIsRunning.set(false);
                //disable stop and pause; 
                stop.setEnabled(false);
                pause.setEnabled(false);
                
            }
        });
    }//end setButtons

    //-----update data in table
    // Handle change events
    @Override
    public void stateChanged(ChangeEvent e) {
        //When car sliders change, update data in table
        trafficData[0][1] = slider1.getValue();
        trafficData[1][1] = slider2.getValue();
        trafficData[2][1] = slider3.getValue();

        //Update speed
        trafficData[0][3] = car1.getSpeed() + " km/h";
        trafficData[1][3] = car2.getSpeed() + " km/h";
        trafficData[2][3] = car3.getSpeed() + " km/h";
        
        //Update table
        dataTable.repaint();

    }//end stateChange
      
    //-------------------creates the intersection interaction simulation-------------------
    private void getInteraction() {
        if (simIsRunning.get()) {
            switch (A.getColor()) {
                case "Red": //Get colors for intersections, if Red check xPosition
                    for (Car i : carArray) {
                        //If car xPosition is within 500 meters and light is red, set suspend to true for car to wait
                        if (i.getPosition() > 500 && i.getPosition() < 1000) {
                            i.trafficLight.set(true);
                        }
                    }
                    break;
                case "Green":
                    for (Car i : carArray) {
                        if (i.trafficLight.get()) {
                            i.resume();
                        }
                    }
                    break;
            }
            switch (B.getColor()) {
                case "Red":
                    for (Car i : carArray) {
                        //If car xPosition is within 500 meters and light is red, set suspend to true for car to wait
                        if (i.getPosition() > 1500 && i.getPosition() < 2000) {
                            i.trafficLight.set(true);
                        }
                    }
                    break;
                case "Green":
                    for (Car i : carArray) {
                        if (i.trafficLight.get()) {
                            i.resume();
                        }
                    }
                    break;
            }
            switch (C.getColor()) {
                case "Red":
                    for (Car i : carArray) {
                        //If car xPosition is within 500 meters and light is red, set suspend to true for car to wait
                        if (i.getPosition() > 2500 && i.getPosition() < 3000) {
                            i.trafficLight.set(true);
                        }
                    }
                    break;
                case "Green":
                    for (Car i : carArray) {
                        if (i.trafficLight.get()) {
                            i.resume();
                        }
                    }
                    break;
            }
        }
    }//end getInteraction

    @Override
    public void run() {
        while (isRunning) {
            //While running, if simulation is running, set car sliders to car xPosition and get data
            if (simIsRunning.get()) {
                slider1.setValue(car1.getPosition());
                slider2.setValue(car2.getPosition());
                slider3.setValue(car3.getPosition());
                getInteraction();
            }
        }
    }//end run         
}//end class

