/**
* Rebecca Sanders
* rsan397
* Comp Sci 230: Assignment 2
*/

import java.util.ArrayList;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Image;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import java.io.File;
import java.util.Scanner;
import java.io.IOException;
import java.nio.file.Paths;

import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.MutableComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.BorderLayout;

import javax.swing.SwingUtilities;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.BorderFactory;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import java.lang.Math;


public class FlowScreen extends JFrame implements ActionListener{
    private final JPanel radioButtonPanel;
    private final JPanel graphPanel;
    private final ButtonGroup radioButtons;
    private final JRadioButton radioButtonSource;
    private final JRadioButton radioButtonDestination;
    private ArrayList<String> sourceFiles;
    private ArrayList<String> destinationFiles;
    private ArrayList<String> fileArray;
    private JComboBox<String> filesComboBox;
    private ArrayList<ArrayList<String>> sourceXFiles;
    private ArrayList<ArrayList<String>> sourceYFiles;
    private DrawGraph basicGraph;
    private int xMax;
    private int yMax;
    private String ipAddress;
    private ArrayList<Double> yValues;

    /**
    * Constructs the JFrame and JPanels - RadioButtons and JPanel for graph
    */
    public FlowScreen(){
    	super("Flow volume viewer");
    	setLayout(new FlowLayout(FlowLayout.LEFT));
    	setSize(1000,500);
        GridBagConstraints d = new GridBagConstraints();
    	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // The following panel will hold the radio buttons,
        radioButtonPanel = new JPanel();
        radioButtonPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = GridBagConstraints.RELATIVE;
        c.anchor = GridBagConstraints.NORTHWEST;
        radioButtons = new ButtonGroup();
        radioButtonSource = new JRadioButton("Source hosts");
        radioButtonSource.setSelected(true);
        radioButtons.add(radioButtonSource);
        radioButtonSource.addActionListener(this);
        radioButtonPanel.add(radioButtonSource, c);
        
        radioButtonDestination = new JRadioButton("Destination hosts");
        radioButtons.add(radioButtonDestination);
        radioButtonDestination.addActionListener(this);
        radioButtonPanel.add(radioButtonDestination,c);
        
        d.gridx = 0;
        d.gridy = 0;
        d.anchor = GridBagConstraints.FIRST_LINE_START;

        add(radioButtonPanel);
        setVisible(true);

        xMax = 0;
        yMax = 0;
        ipAddress = null;

        setupArrayLists();
        setupMenu();
        setupComboBox();

        //graph jpanel:
        graphPanel = new JPanel();
        graphPanel.setSize(1000, 350);
        d.gridx = 0;
        d.gridy = 1;
        d.anchor = GridBagConstraints.LAST_LINE_START; //left aligned//
        basicGraph = new DrawGraph();
        graphPanel.add(basicGraph);
        add(graphPanel);
        setVisible(true);
        
    }

    /**
    * sets up all arraylists in one place
    */
    private void setupArrayLists(){
        sourceFiles = new ArrayList<String>();
        destinationFiles = new ArrayList<String>();
        fileArray = new ArrayList<String>();
        sourceXFiles = new ArrayList<ArrayList<String>>();
        sourceYFiles = new ArrayList<ArrayList<String>>();
        yValues = new ArrayList<Double>();
    }   

    /**
    * sets up the menubar and the actionlistener events for when a menu item is selected
    * calls the ProcessFile class to open and sort through the file
    * gets array lists of ip addresses from ProcessFile which are later added to JComboBox
    */
    private void setupMenu() {
    	JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic('F');
		menuBar.add(fileMenu);
		JMenuItem fileMenuOpen = new JMenuItem("Open trace file");
		fileMenuOpen.addActionListener(
				new ActionListener()
				{
					public void actionPerformed(ActionEvent e) {
    					JFileChooser fileChooser = new JFileChooser(".");
    					int retval = fileChooser.showOpenDialog(FlowScreen.this);
    					if (retval == JFileChooser.APPROVE_OPTION) {
    						File f = fileChooser.getSelectedFile();
                            //process file class, return arraylists
                            ProcessFile pf = new ProcessFile(f);
                            sourceFiles = pf.getSource(); //gets values for dropdown
                            destinationFiles = pf.getDestination(); //gets values for dropdown
                            sourceXFiles = pf.getSourceValues(); //get values for graph
                            sourceYFiles = pf.getDestinationValues();
    					}
					}
				}
        );
		fileMenu.add(fileMenuOpen);
		JMenuItem fileMenuQuit = new JMenuItem("Quit");
		fileMenu.add(fileMenuQuit);
		fileMenuQuit.addActionListener(
				new ActionListener() 
				{
					public void actionPerformed(ActionEvent e) {
						System.exit(0);
					}
				}
				);
	}

    /**
    * sets up the JCmoboBox that appears when a radiobutton is selected
    * triggers an actionevent when combobox item is selected
    * actionevent get values associated with the ip address from calling the ProcessGraph Class
    * then calls repaint() to redraw graph
    */
    private void setupComboBox() {
        filesComboBox = new JComboBox<String>();
        filesComboBox.setModel((MutableComboBoxModel<String>) filesComboBox.getModel());
        filesComboBox.setMaximumRowCount(4);
        filesComboBox.setMinimumSize(new Dimension(300,25));
        filesComboBox.removeAllItems();
        filesComboBox.setVisible(false);
        filesComboBox.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    JComboBox filesComboBox = (JComboBox) e.getSource();
                    int index = filesComboBox.getSelectedIndex();
                    if (index >= 0 && sourceXFiles.size() >0 && sourceYFiles.size() >0){
                        ipAddress = sourceFiles.get(index);
                        ProcessGraph pg = new ProcessGraph(sourceXFiles, ipAddress);
                        yValues = pg.getYValues();
                        xMax = pg.getXMax();
                        yMax = pg.getYMax();

                        basicGraph.repaint();
                    }
                    return;
                    }
                }
            );
        add(filesComboBox, BorderLayout.NORTH);
        
    }
    /**
    * updates the combobox values when a new file is opened and added in the jmenu
    */
    private void updateComboBox(){
        for (String line : fileArray) {
            filesComboBox.addItem(line);
        }
        filesComboBox.setVisible(true);

    }

    /**
    * triggered action event when a radiobutton is selected
    * updates the JComboBox values to store IP Addresses
    * @param ActionEvent event
    */
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == radioButtonSource) {
            filesComboBox.removeAllItems();
            fileArray = sourceFiles;
            if (fileArray.size() > 0) {
                updateComboBox();
                return;
            }
            else {
                filesComboBox.setVisible(false);
            }
        }
        if (event.getSource() == radioButtonDestination) {
            filesComboBox.removeAllItems();
            fileArray = destinationFiles;
            if (fileArray.size() > 0) {
                updateComboBox();
                return;
            }
            else {
                filesComboBox.setVisible(false);
            }
        }
        return;
    }

    /**
    * Inner class which draws the graphs in the JPanel
    */
    class DrawGraph extends JPanel{
        private int xAxis = 950;
        private int yAxis = 280;

        public DrawGraph(){
            setVisible(true);
        }

        /**
        * sets size of Jpanel
        *@return a new dimension
        */
        public Dimension getPreferredSize() {
            return new Dimension(1000,350);
        }

        /**
        * draws the default graph, and when triggered by the repaint() method, updates graph to show values
        * associated with the ip addresses
        *@param Graphics g
        */
        public void paintComponent(Graphics g) {
            super.paintComponent(g);       
            // draw white background
            g.setColor(Color.WHITE);
            g.drawRect(0,0, 1000, 350) ;
            g.fillRect(0,0, 1000, 350) ;

            //x and y axis
            g.setColor(Color.BLACK);
            g.drawString("Volume [bytes]",15,10);
            g.drawString("Time [s]", 500, 340);
            g.drawLine(50,20,50,300); //y
            g.drawLine(50,300,950,300); //x
            

            if (yValues.size() <=0){
                g.drawString("0", 30, 300);
                //drawticks
                g.drawLine(45, 300, 50, 300);
                g.drawLine(50, 300, 50, 305);

                int xIncrements = xAxis/13;
                int xLabel = 0;
                for (int p = 50; p<xAxis; p+=xIncrements){
                    g.drawLine((p), 300, p, 305);
                    String label = Integer.toString(xLabel);
                    g.drawString(label, p, 325);
                    xLabel = xLabel + 50;
                }
            }

            else if (yValues.size() >0){
                plotPoints(g);
            }
            
        }

        /**
        * draws the new x axis and y axis based on values associated with ip addresses
        * plots all data points on graph
        *@param Graphics g
        */
        public void plotPoints(Graphics g){ 

            //yAxis Labels
            int yLabelPoint = 0;
            double yAx = (double) yAxis;
            double yIncrement = yAx/(yMax/1000.0);
            int counter = 0;
            int yLabel = 0;

            if (yMax<500000){
                double d;
                for (d=300; d>20; d-=yIncrement){
                    if (counter == yLabel){
                        int d2 = (int) d;
                        g.setColor(Color.BLACK);
                        g.drawLine(45,d2, 50, d2);
                        String label = Integer.toString(yLabel);
                        g.drawString(label + "K", 5, d2);
                        yLabel +=50; 
                    }
                    counter=counter +1;
                }
            }

            else if (yMax<1000000){
                double d;
                for (d=300; d>20; d-=yIncrement){
                    if (counter == yLabel){
                        int d2 = (int) d;
                        g.setColor(Color.BLACK);
                        g.drawLine(45,d2, 50, d2);
                        String label = Integer.toString(yLabel);
                        g.drawString(label + "K", 5, d2);
                        yLabel +=100; 
                    }
                    counter=counter +1;
                }
            }

            else if (yMax<2000000){
                double d;
                for (d=300; d>20; d-=yIncrement){
                    if (counter == yLabel){
                        int d2 = (int) d;
                        g.setColor(Color.BLACK);
                        g.drawLine(45,d2, 50, d2);
                        String label = Integer.toString(yLabel);
                        g.drawString(label + "K", 5, d2);
                        yLabel +=200; 
                    }
                    counter=counter +1;
                }
            }

            else{
                double d;
                for (d=300; d>20; d-=yIncrement){
                    if (counter == yLabel){
                        int d2 = (int) d;
                        g.setColor(Color.BLACK);
                        g.drawLine(45,d2, 50, d2);
                        String label = Integer.toString(yLabel);
                        g.drawString(label + "K", 5, d2);
                        yLabel +=500; 
                    }
                    counter=counter +1;
                }

            }

            //xAxis
            int xLabel = 0;
            int xLabelPoint = 0;
            int xIncrement = xAxis/(xMax/2);
            int xPoint = 50;

            for (int index = 0; index<yValues.size(); index++){
                Double doubley = yValues.get(index);
                int y = doubley.intValue();
                double yPlot = (y/1000)*yIncrement;
                int y2 = (int) yPlot;
                int y3 = 297-y2;
                 //yvalue
                g.setColor(Color.BLACK);
                g.drawOval(xPoint, y3,4,4);
                g.setColor(Color.RED);
                g.fillOval(xPoint,y3,4,4);
                //xAxis labels
                if(index == xLabelPoint){
                    g.setColor(Color.BLACK);
                    g.drawLine(xPoint, 300, xPoint, 305);
                    String label = Integer.toString(xLabel);
                    g.drawString(label, xPoint, 325);
                    xLabel +=50;
                    xLabelPoint+=25;

                }
                xPoint +=xIncrement;
            } 

        }

    }

}