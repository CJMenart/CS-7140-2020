package org.stathissideris.ascii2image.core;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.imageio.ImageIO;
import javax.swing.*;

public class DitaaGUI{
    // the main window
    private JFrame frame;
    // the location of the currently loaded source file
    private String sourceFilePath;
    // a boolean indicating true when there is a source file and it is readable
    private boolean sourceFileValid = false;
    // the location in the temp dir where ditaa has generated the image (once a valid source is loaded)
    private String tempOutputLoc;
    // keep a reference to the output image label and save buttons at the class level so that they can be accessed
    // by both sub-panels
    private JLabel outputImagePanel;
    private JButton saveFileButton;

    // the options to pass to DITAA
    ConversionOptions options = new ConversionOptions();

    /**
     * Creates a new default DITAA GUI
     *
     */ 
    public DitaaGUI(){
        this.frame = new JFrame("DITAA GUI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.frame.getContentPane().setLayout(new BoxLayout(this.frame.getContentPane(), BoxLayout.LINE_AXIS));

        JSplitPane mainSplit = new JSplitPane();
        mainSplit.setLeftComponent(this.createSourceContentPane());
        mainSplit.setRightComponent(this.createOutputContentPane());

        this.frame.getContentPane().add(mainSplit);
        this.frame.pack();
    }

    /**
     * Creates a new DITAA GUI which will use the provided conversion options for all invocations of DITAA
     */ 
    public DitaaGUI(ConversionOptions opts){
        this();

        this.options = opts;
    }

    /**
     * Opens the DITAA GUI
     */
    public void openGUI() {
        this.frame.setVisible(true);
    }

    /**
     * Creates the Source panel which provides the functions of choosing/loading a file and displaying its contents.
     *
     * @return the JPanel representing the source panel
     */
    private JPanel createSourceContentPane(){
        JPanel sourcePanel = new JPanel();
        sourcePanel.setLayout(new BoxLayout(sourcePanel, BoxLayout.PAGE_AXIS));

        // Add the text area to display the file contents
        final JTextArea sourceTextArea = new JTextArea();
        sourceTextArea.setRows(30);
        sourceTextArea.setFont(new Font("monospaced", Font.PLAIN, 12));
        sourceTextArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(sourceTextArea);
        sourcePanel.add(scrollPane);

        // Add the button to load a file and label to show the selected file name
        JPanel filePanel = new JPanel();
        filePanel.setLayout(new BoxLayout(filePanel, BoxLayout.LINE_AXIS));

        // Add a place to show the selected file name
        JLabel sourceFileLabel = new JLabel();
        sourceFileLabel.setText("Selected File: ");
        filePanel.add(sourceFileLabel);

        final JTextField filenameField = new JTextField(20);
        filenameField.setEditable(false);
        filePanel.add(filenameField);

        /*
         * A button to load the file - most of the logic is driven by the following action which is performed after
         * the file is selected and loaded:
         * 1. file loaded
         * 2. file read
         * if file is readable:
         * 3. put contents into text area
         * 4. run ditaa
         * 5. load ditaa output into the results panel
         */
        final JButton loadFileButton = new JButton("Load file...");
        loadFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if(actionEvent.getSource() == loadFileButton){

                    // Open a file chooser
                    JFileChooser fileChooser = new JFileChooser();
                    int chooserReturn = fileChooser.showOpenDialog(DitaaGUI.this.frame);
                    if(chooserReturn != JFileChooser.APPROVE_OPTION){
                        // if "cancel" or the 'x' was used to close the dialog, go no further
                        return;
                    }
                    File selectedFile = fileChooser.getSelectedFile();

                    // set the name of the file
                    DitaaGUI.this.sourceFilePath = selectedFile.getAbsolutePath();
                    filenameField.setText(DitaaGUI.this.sourceFilePath);

                    //open the file and set it's contents in the text area
                    String fileContents;
                    try {
                        fileContents = new String(Files.readAllBytes(Paths.get(DitaaGUI.this.sourceFilePath)));
                        DitaaGUI.this.sourceFileValid = true;
                    }
                    catch(IOException | InvalidPathException e){
                        fileContents = "<< Unable to read selected file :\n" +
                                e.getMessage() +
                                "\n>>";
                        DitaaGUI.this.sourceFileValid = false;
                    }
                    sourceTextArea.setText(fileContents);

                    // Run DITAA
                    if(DitaaGUI.this.sourceFileValid) {
                        DitaaGUI.this.tempOutputLoc = DitaaGUI.this.runDitaa();

                        // load the output image into the output panel
                        DitaaGUI.this.updateOutputImage();
                        DitaaGUI.this.saveFileButton.setEnabled(true);
                    }
                    else{
                        // set back to false in case it was enabled previously
                        DitaaGUI.this.clearOutputImage();
                        DitaaGUI.this.saveFileButton.setEnabled(false);
                    }
                }
            }
        });
        filePanel.add(loadFileButton);

        sourcePanel.add(filePanel);

        return sourcePanel;
    }


    /**
     * Creates the output content panel which provides the functions of previewing DITAA output and saving
     *
     * @return the JPanel representing the content panel
     */
    private JPanel createOutputContentPane(){
        JPanel sourcePanel = new JPanel();
        sourcePanel.setLayout(new BoxLayout(sourcePanel, BoxLayout.PAGE_AXIS));

        // Add the text area to display the file contents
        this.outputImagePanel = new JLabel();
        JScrollPane scrollPane = new JScrollPane(outputImagePanel);
        sourcePanel.add(scrollPane);

        this.saveFileButton = new JButton("Save As...");
        saveFileButton.setEnabled(false); //(disable until a file is loaded)
        saveFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if(actionEvent.getSource() == saveFileButton){
                    JFileChooser fileChooser = new JFileChooser();
                    int chooserReturn = fileChooser.showSaveDialog(DitaaGUI.this.frame);

                    if(chooserReturn != JFileChooser.APPROVE_OPTION){
                        // if "cancel" or the 'x' was used to close the dialog, go no further
                        return;
                    }

                    // Get the location and save a copy
                    File selectedFile = fileChooser.getSelectedFile();
                    try {
                        Files.copy(Paths.get(DitaaGUI.this.tempOutputLoc), selectedFile.toPath());
                    }
                    catch(IOException e){
                        System.err.println("Unable to save to location " + selectedFile + ":");
                        System.err.println(e.getMessage());
                        System.err.println(e.getStackTrace());
                    }
                }
            }
        });

        sourcePanel.add(saveFileButton);
        return sourcePanel;
    }

    /**
     * Runs DITAA on the currently selected input file.
     *
     * @return String representing the path to the output diagram.
     */
    private String runDitaa(){
        // get a temporary file path
        Path outFilePath = null;
        try {
            outFilePath = Files.createTempFile("ditaa", ".png");
        } catch (IOException e) {
            System.err.println("Failed to create temp output file:");
            System.err.println(e.getMessage());
            System.err.println(e.getStackTrace());
        }

        CommandLineConverter.runSimpleMode(this.options, this.sourceFilePath, outFilePath.toString());
        return outFilePath.toString();
    }

    /**
     * Updates the image preview to show the most recently computed DITAA diagram.
     */
    private void updateOutputImage(){
        if(this.outputImagePanel != null && this.sourceFileValid && this.tempOutputLoc != null && this.tempOutputLoc.length() > 0){
            try{
                Image preview = ImageIO.read(new File(this.tempOutputLoc));
                this.outputImagePanel.setIcon(new ImageIcon(preview));
            }
            catch(IOException e){
                System.err.println("Unable to update image preview: " +
                        e.getMessage() +
                        e.getStackTrace());

                this.outputImagePanel.setText("Error loading preview.");
            }
        }
    }

    /**
     * Clears the currently displayed DITAA diagram.
     */
    private void clearOutputImage(){
        if(this.outputImagePanel != null){
            this.outputImagePanel.setIcon(null);
        }
    }
}
