package org.stathissideris.ascii2image.core;
import javax.swing.*;

public class DitaaGUI {

    //TODO needs member variables for holding filepaths I think?




    public void openGUI() {
        JFrame frame = new JFrame("My First GUI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300,300);
        JButton button1 = new JButton("Press");
        frame.getContentPane().add(button1);
        frame.setVisible(true);
    }



    //TODO needs events for file selection, hitting 'save'

    //TODO anything this wants to use from CommandLineConverter probably needs factoring out into own class??

}
