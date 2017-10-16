/* Rev 0.2
 * 
 * This is a legacy example, and some of the methods used below is only best used when using a Shimmer2r device e.g. writeenabled sensors.
 * The recommended way of enabling sensors is via the bluetooth manager configureShimmer method. Note that the configureShimmer method does NOT work with 2R devices 
 * 
 * 
 * Copyright (c) 2014, Shimmer Research, Ltd.
 * All rights reserved
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:

 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above
 *       copyright notice, this list of conditions and the following
 *       disclaimer in the documentation and/or other materials provided
 *       with the distribution.
 *     * Neither the name of Shimmer Research, Ltd. nor the names of its
 *       contributors may be used to endorse or promote products derived
 *       from this software without specific prior written permission.

 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * @author Cathy Swanton , Jong Chern Lim, Ruaidhri Molloy
 * @date   May, 2014
 *
 * Purpose of this example is to demonstrate some of the functionality of LogandStream firmware and not a fully completed application
 */

package com.shimmerresearch.legacyexamples;

import info.monitorenter.gui.chart.Chart2D;
import info.monitorenter.gui.chart.IAxis;
import info.monitorenter.gui.chart.IRangePolicy;
import info.monitorenter.gui.chart.ITrace2D;
import info.monitorenter.gui.chart.rangepolicies.RangePolicyFixedViewport;
import info.monitorenter.gui.chart.traces.Trace2DLtd;
import info.monitorenter.util.Range;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.google.common.collect.Multimap;
import com.shimmerresearch.biophysicalprocessing.ECGtoHRAdaptive;
import com.shimmerresearch.biophysicalprocessing.ECGtoHRAlgorithm;
import com.shimmerresearch.biophysicalprocessing.PPGtoHRAlgorithm;
import com.shimmerresearch.bluetooth.ShimmerBluetooth;
import com.shimmerresearch.bluetooth.ShimmerBluetooth.BT_STATE;
import com.shimmerresearch.driver.BasicProcessWithCallBack;
import com.shimmerresearch.driver.CallbackObject;
import com.shimmerresearch.driver.Configuration;
import com.shimmerresearch.driver.FormatCluster;
import com.shimmerresearch.driver.ObjectCluster;
import com.shimmerresearch.driver.ShimmerMsg;
import com.shimmerresearch.driver.Configuration.Shimmer3;
import com.shimmerresearch.driverUtilities.ChannelDetails.CHANNEL_TYPE;
import com.shimmerresearch.driverUtilities.ShimmerVerDetails.HW_ID;
import com.shimmerresearch.exceptions.ShimmerException;
import com.shimmerresearch.exgConfig.ExGConfigOptionDetails.EXG_CHIP_INDEX;
import com.shimmerresearch.pcDriver.ShimmerPC;
import com.shimmerresearch.sensors.SensorGSR;
import com.shimmerresearch.sensors.bmpX80.SensorBMP180;
import com.shimmerresearch.sensors.lsm303.SensorLSM303DLHC;
import com.shimmerresearch.sensors.mpu9x50.SensorMPU9X50;
import com.shimmerresearch.tools.LoggingPC;
import com.shimmerresearch.algorithms.*;

import javax.swing.SpinnerNumberModel;

public class ShimmerCapture extends BasicProcessWithCallBack{
	
	public static final int SHIMMER_1=0;
	public static final int SHIMMER_2=1;
	public static final int SHIMMER_2R=2;
	public static final int SHIMMER_3=3;
	public static final int SHIMMER_SR30=4;
	protected int mShimmerVersion;
	private int downSample=0;
	private JFrame frame;
	private JFrame configFrame;
	private JFrame exgFrame;
	ShimmerPC mShimmer = new ShimmerPC("ShimmerDevice", true);
	
	
	private JButton btnStartStreaming;
	private JButton btnStopStreaming;
	private JButton btnDisconnect;
	private JButton btnConnect;
	private JButton btnOK;
	private JButton btnCancel;
	private JButton btnToggleLed;
	
	private JTextField textFieldComPort;
	private JTextField textFieldState;
	private JTextField textFieldMessage;
	
	private JLabel lblShimmerState;
	private JLabel lblSampFreq;
	private JLabel lblEXG;
	private JLabel lblAccelRange;
	private JLabel lblGyroRange;
	private JLabel lblMagRange;
	private JLabel lblGSRRange;
	private JLabel lblPressRange;
	private JLabel lblSensors;
	private JLabel lblSelectComPort;
	private JLabel lblSignals;
	private JLabel lblNumberOfBeats;
	
	private JComboBox<String> comboBoxSamplingRate;
	private JComboBox<String> comboBoxAccelRange;
	private JComboBox<String> comboBoxGyroRange;
	private JComboBox<String> comboBoxMagRange;
	private JComboBox<String> comboBoxGsrRange;
	private JComboBox<String> comboBoxPressureResolution;
	
	private JCheckBox chckbx5VReg;
	private JCheckBox chckbxVoltageMon;
	private JCheckBox chckbx3DOrientation;
	private JCheckBox chckbxOnTheFlyGyroCal;
	private JCheckBox chckbxLowPowerMag;
	private JCheckBox chckbxLowPowerAcc;
	private JCheckBox chckbxLowPowerGyro;
	private JCheckBox chckbxInternalExpPower;
	private JCheckBox chckbxEnablePPGtoHR;
	private JCheckBox chckbxEnableECGtoHR;
	private JCheckBox[] listOfSensorsShimmer3;
	private JCheckBox[] listOfSensorsShimmer2;
	private JCheckBox[] listOfSignals;
	private JCheckBox[] calibratedSignals;
	private JCheckBox chckbxHeartRate;
	
	private JSpinner spinnerNumberOfBeatsToAve;
	
	private Chart2D chart;
	private IAxis<?> yAxis;
	
	private JMenuBar menuBar;
	private JMenu menuFile;
	private JMenu menuTools;
	private JMenuItem menuItemQuit;
	private JMenuItem menuItemConfigure;
	private JMenuItem menuItemExgSettings;
	private JCheckBox menuItemSaveToCsv;
	
	private float   mSpeed = 1.0f;
	private float   mLastX;
	private String samplingFreqS3[] = {"1Hz","10.2Hz","51.2Hz", "102.4Hz", "204.8Hz", "250.1Hz", "512Hz", "1024Hz"};
	private String samplingFreqS2[] = {"0Hz","10.2Hz","51.2Hz", "102.4Hz", "128Hz", "170.6Hz", "204.8Hz", "256Hz", "512Hz", "1024Hz"};
	private Color traceColours[] = {Color.RED, Color.BLUE, Color.GREEN, Color.BLACK, Color.YELLOW, Color.CYAN, Color.GRAY, Color.MAGENTA, Color.ORANGE, Color.LIGHT_GRAY, Color.PINK, Color.DARK_GRAY};
	private ITrace2D[] traces = new ITrace2D[12];
	private ITrace2D traceHR;		
	
	private String[] enabledSensorSignals;
	private String listofCompatibleSensorsShimmer3[];
	private String listofCompatibleSensorsShimmer2[];
    private boolean firstConfiguration=true;
	private Boolean loggingData=false;
	private Boolean calibrated[] = {false, false, false, false, false, false, false, false, false, false, false, false};
    private int mReturnEnabledSensors = 0;
	private int numberOfSignals;
	private int indexAccel;
	private int indexGyro;
	private int indexMag;
	private int indexGSR;
	private int indexPressureRes;
	private int maxTraces= traceColours.length;
    private int calibratedCount;
    private int maxDataPoint=-10000;
    private int minDataPoint=10000;
    private int returnVal;
    private double samplingRate;
	private Color backgroundColor;
    static LoggingPC log;
    private String fileName = "ShimmerConnect.csv";
    
    //EXG Frame
    private JLabel lblChip1;
    private JLabel lblChip2;
    private JLabel lblExgGainChip1Channel1;
    private JLabel lblExgGainChip1Channel2;
    private JLabel lblExgGainChip2Channel1;
    private JLabel lblExgGainChip2Channel2;
    private JCheckBox chckbxHPF0_05;
    private JCheckBox chckbxHPF0_5;
    private JCheckBox chckbxHPF5;
    private JCheckBox chckbxBSF_50;
    private JCheckBox chckbxBSF_60;
    private JCheckBox chckbxEcgConfig;
    private JCheckBox chckbxEmgConfig;
    private JCheckBox chckbxTestSignal;
    private JComboBox<Integer> comboBoxGainChip1Channel1;
    private JComboBox<Integer> comboBoxGainChip1Channel2;
    private JComboBox<Integer> comboBoxGainChip2Channel1;
    private JComboBox<Integer> comboBoxGainChip2Channel2;
    private JTextField[] textFieldChip1 = new JTextField[10];
    private JTextField[] textFieldChip2 = new JTextField[10];
    private JButton btnExgOk;
    private JButton btnExgCancel;
    
    private double cornerFrequencyHPF=-1;
    private double cornerFrequencyBSF1=-1;
    private double cornerFrequencyBSF2=-1;
    private boolean firstExgConfiguration=true;
    private boolean highPassFilterEnabled=false;
    private boolean bandStopFilterEnabled=false;

	private int[] exgConfigurationChip1 = new int[10];
	private int[] exgConfigurationChip2 = new int[10];
	double exg1Ch1Data=0, exg1Ch2Data=0, exg2Ch1Data=0, exg2Ch2Data=0;
	double[] exg1Data24bit = new double[4];
	double[] exg2Data24bit = new double[4];
	double[] exg1Data16bit = new double[4];
	double[] exg2Data16bit = new double[4];
	
	private PPGtoHRAlgorithm heartRateCalculation;
	//private ECGtoHRAlgorithm heartRateCalculationECG;
	private ECGtoHRAdaptive heartRateCalculationECG;
	private boolean calculateHeartRate = false;
	private int INVALID_RESULT=-1;
	
	//Logging - file already exists
	private JFrame fileExistsWindow = new JFrame();
	private JLabel lblFileExists;
	private JButton btnOverwriteFile;
	private JButton btnCancelWriteFile;

	Filter hpfexg1ch1;
	Filter hpfexg1ch2;
	Filter hpfexg2ch1;
	Filter hpfexg2ch2;

	Filter bsfexg1ch1;
	Filter bsfexg1ch2;
	Filter bsfexg2ch1;
	Filter bsfexg2ch2;
	Filter lpf;
	Filter hpf;
	Filter lpfECG;
	Filter hpfECG;
	
	private JButton btnReadStatus;
	private JButton btnReadDirectory;
	private JLabel lblnoOfBeats;
	private JSpinner spinnerNumberOfBeatsToAveECG;
	
	
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					
					ShimmerCapture window = new ShimmerCapture();
					window.frame.setVisible(true);
					/*SerialPort serialPort = new SerialPort("COM155");
					try {
			            System.out.println("Port opened: " + serialPort.openPort());
			            System.out.println("Params setted: " + serialPort.setParams(115200, 8, 1, 0));
			            serialPort.writeByte((byte)0x2E);
			            byte [] response = serialPort.readBytes(8);
			            System.out.print(Arrays.toString(response));
			            System.out.println("Port closed: " + serialPort.closePort());
			        }
			        catch (SerialPortException ex){
			            System.out.println(ex);
			        }
					 */


					//shimmer.stopStreaming();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public ShimmerCapture() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		setWaitForData(mShimmer);
		frame = new JFrame("Shimmer Capture");
		frame.setBounds(100, 100, 720, 592);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		backgroundColor = frame.getBackground();
		
		configFrame = new JFrame("Configure");
		configFrame.setBounds(150, 150, 434, 772);
		configFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		configFrame.getContentPane().setLayout(null);
		configFrame.setVisible(false);
		
		exgFrame = new JFrame("EXG");
		exgFrame.setBounds(150, 150, 400, 540);
		exgFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		exgFrame.getContentPane().setLayout(null);
		exgFrame.setVisible(false);

		btnConnect = new JButton("Connect");
		btnConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("Connect");
				mShimmer.connect(textFieldComPort.getText(),"");
				
			}
		});
		btnConnect.setBounds(140, 45, 98, 25);
		frame.getContentPane().add(btnConnect);
		
		btnDisconnect = new JButton("Disconnect");
		btnDisconnect.setEnabled(false);
		btnDisconnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					mShimmer.disconnect();
				} catch (ShimmerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//mShimmer = new ShimmerPC("ShimmerDevice", true);
			}
		});
		btnDisconnect.setBounds(252, 45, 98, 25);
		frame.getContentPane().add(btnDisconnect);

		btnStartStreaming = new JButton("Stream");
		btnStartStreaming.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//Create HPFs for EXG	
				try {
				double [] cutoff = {5.0};
				lpf = new Filter(Filter.LOW_PASS,mShimmer.getSamplingRateShimmer(), cutoff);
				cutoff[0] = 0.5;
				hpf = new Filter(Filter.HIGH_PASS,mShimmer.getSamplingRateShimmer(), cutoff);
				
				cutoff[0] = 51.2;
				lpfECG = new Filter(Filter.LOW_PASS,mShimmer.getSamplingRateShimmer(), cutoff);
				cutoff[0] = 0.5;
				hpfECG = new Filter(Filter.HIGH_PASS,mShimmer.getSamplingRateShimmer(), cutoff);
				
				
				cutoff[0] = cornerFrequencyHPF;
				hpfexg1ch1 = new Filter(Filter.HIGH_PASS,mShimmer.getSamplingRateShimmer(), cutoff);
				hpfexg1ch2 = new Filter(Filter.HIGH_PASS,mShimmer.getSamplingRateShimmer(), cutoff);
				hpfexg2ch1 = new Filter(Filter.HIGH_PASS,mShimmer.getSamplingRateShimmer(), cutoff);
				hpfexg2ch2 = new Filter(Filter.HIGH_PASS,mShimmer.getSamplingRateShimmer(), cutoff);
				//Create BSF for EXG
				cutoff = new double[2];
				cutoff[0]= cornerFrequencyBSF1;
				cutoff[1]= cornerFrequencyBSF2;
				bsfexg1ch1 = new Filter(Filter.BAND_STOP,mShimmer.getSamplingRateShimmer(), cutoff);
				bsfexg1ch2 = new Filter(Filter.BAND_STOP,mShimmer.getSamplingRateShimmer(), cutoff);
				bsfexg2ch1 = new Filter(Filter.BAND_STOP,mShimmer.getSamplingRateShimmer(), cutoff);
				bsfexg2ch2 = new Filter(Filter.BAND_STOP,mShimmer.getSamplingRateShimmer(), cutoff);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				if ((Integer)spinnerNumberOfBeatsToAve.getValue() <= 0){
					spinnerNumberOfBeatsToAve.setValue(1);
				}
				heartRateCalculation = new PPGtoHRAlgorithm(mShimmer.getSamplingRateShimmer(), (Integer)spinnerNumberOfBeatsToAve.getValue(),10); //10 second training period
				heartRateCalculationECG = new ECGtoHRAdaptive(mShimmer.getSamplingRateShimmer());
				
				mShimmer.startStreaming();
			}
		});
		btnStartStreaming.setBounds(10, 95, 98, 25);
		frame.getContentPane().add(btnStartStreaming);
		btnStartStreaming.setEnabled(false);
		
		
		btnStopStreaming = new JButton("Stop");
		btnStopStreaming.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mShimmer.stopStreaming();
			}
		});
		btnStopStreaming.setBounds(252, 95, 98, 25);
		frame.getContentPane().add(btnStopStreaming);
		btnStopStreaming.setEnabled(false);
		
		textFieldComPort = new JTextField();
		textFieldComPort.setBounds(10, 46, 116, 24);
		frame.getContentPane().add(textFieldComPort);
		textFieldComPort.setColumns(10);

		textFieldState = new JTextField();
		textFieldState.setBounds(450, 46, 150, 24);
		frame.getContentPane().add(textFieldState);
		textFieldState.setColumns(10);
		textFieldState.setText("Shimmer Disconnected");
		
		textFieldMessage = new JTextField();
		textFieldMessage.setBounds(0, 535, 704, 20);
		frame.getContentPane().add(textFieldMessage);
		textFieldMessage.setColumns(10);
		textFieldMessage.setBackground(backgroundColor);
		
		lblSelectComPort = new JLabel("Select COM Port");
		lblSelectComPort.setBounds(10, 32, 116, 14);
		frame.getContentPane().add(lblSelectComPort);
		
		lblShimmerState = new JLabel("Shimmer State");
		lblShimmerState.setBounds(450, 32, 98, 14);
		frame.getContentPane().add(lblShimmerState);
		
		lblSignals = new JLabel("Signals to View");
		lblSignals.setBounds(10, 200, 100, 14);
		frame.getContentPane().add(lblSignals);
		lblSignals.setVisible(false);
		
		chart = new Chart2D();
		chart.setBounds(270, 180, 400, 300);
		frame.getContentPane().add(chart);
		chart.setVisible(false);
		chart.setBackground(Color.WHITE);
		IAxis<?> xAxis = chart.getAxisX();
		xAxis.setVisible(false);
		yAxis = chart.getAxisY();
		yAxis.setAxisTitle(new IAxis.AxisTitle(""));

		menuBar = new JMenuBar();
		menuBar.setBounds(0, 0, 703, 25);
		frame.getContentPane().add(menuBar);
		
		menuFile = new JMenu("File	");
		menuBar.add(menuFile);
		menuItemQuit = new JMenuItem("Quit");
		menuFile.add(menuItemQuit);
		menuItemQuit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					mShimmer.disconnect();
				} catch (ShimmerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.exit(0); 
			}
		});
		
		menuTools = new JMenu("Tools");
		menuBar.add(menuTools);
		menuItemConfigure = new JMenuItem("Configure Shimmer");
		menuItemConfigure.setEnabled(false);
		menuTools.add(menuItemConfigure);
		menuItemConfigure.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				configuration();
			}
		});
		
		menuItemExgSettings = new JMenuItem("EXG Settings");
		menuItemExgSettings.setEnabled(false);
		menuTools.add(menuItemExgSettings);
		menuItemExgSettings.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				exgFrame.setVisible(true);
				exgConfiguration();
			}
		});

		menuItemSaveToCsv = new JCheckBox("Save to CSV");
		menuTools.add(menuItemSaveToCsv);
		
		JButton btnStreamandlog = new JButton("Stream and Log");
		btnStreamandlog.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				mShimmer.startDataLogAndStreaming();
			}
		});
		btnStreamandlog.setEnabled(true);
		btnStreamandlog.setBounds(118, 96, 120, 25);
		frame.getContentPane().add(btnStreamandlog);
		
		btnReadStatus = new JButton("Read Status");
		btnReadStatus.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				mShimmer.readStatusLogAndStream();
			}
		});
		btnReadStatus.setEnabled(true);
		btnReadStatus.setBounds(360, 96, 120, 25);
		frame.getContentPane().add(btnReadStatus);
		
		btnReadDirectory = new JButton("Read Directory");
		btnReadDirectory.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				mShimmer.readDirectoryName();
			}
		});
		btnReadDirectory.setEnabled(true);
		btnReadDirectory.setBounds(492, 96, 120, 25);
		frame.getContentPane().add(btnReadDirectory);
		menuItemSaveToCsv.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(menuItemSaveToCsv.isSelected()) {
					openFile();
				} else {
					loggingData=false;
					log.closeFile();
					textFieldMessage.setText("");
				}
			}
		});

		listofCompatibleSensorsShimmer3 = Configuration.Shimmer3.ListofCompatibleSensors;
		int newCount = 0;
		listOfSensorsShimmer3 = new JCheckBox[listofCompatibleSensorsShimmer3.length]; 
		for (int count=0; count<listofCompatibleSensorsShimmer3.length; count++) {
			listOfSensorsShimmer3[count] = new JCheckBox(listofCompatibleSensorsShimmer3[count], false);	
			//positioning
			if (count+1>(listofCompatibleSensorsShimmer3.length)/2) {
				listOfSensorsShimmer3[count].setBounds(200, 30+(25*newCount), 150, 20);
				newCount++;
			} else {	
				listOfSensorsShimmer3[count].setBounds(10, 30+(25*count), 150, 20);
			}
		}

		listofCompatibleSensorsShimmer2 = Configuration.Shimmer2.ListofCompatibleSensors;
		int newCount2 = 0;
		listOfSensorsShimmer2 = new JCheckBox[listofCompatibleSensorsShimmer2.length]; 
		for (int count=0; count<listofCompatibleSensorsShimmer2.length; count++) {
			listOfSensorsShimmer2[count] = new JCheckBox(listofCompatibleSensorsShimmer2[count], false);			
			if (count+1>(listofCompatibleSensorsShimmer2.length)/2) {
				listOfSensorsShimmer2[count].setBounds(200, 30+(25*newCount2), 150, 20);
				newCount2++;
			} else {	
				listOfSensorsShimmer2[count].setBounds(10, 30+(25*count), 150, 20);
			}
		}	
		
		listOfSignals = new JCheckBox[maxTraces]; 
		for (int count=0; count<maxTraces; count++) {
			listOfSignals[count] = new JCheckBox("", false);
			frame.getContentPane().add(listOfSignals[count]);
		}
		
		calibratedSignals = new JCheckBox[maxTraces]; 
		for (int count=0; count<maxTraces; count++) {
			calibratedSignals[count] = new JCheckBox("", false);
			frame.getContentPane().add(calibratedSignals[count]);
		}
		
		chckbxHeartRate = new JCheckBox("Heart Rate");
		chckbxHeartRate.setVisible(false);
		
		//Config Frame
		btnOK = new JButton("OK");
		btnOK.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				configurationDone();
			}
		});
		btnOK.setBounds(200, 668, 216, 25);
		configFrame.getContentPane().add(btnOK);
		
		btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				configFrame.setVisible(false);
			}
		});
		btnCancel.setBounds(10, 668, 110, 25);
		configFrame.getContentPane().add(btnCancel);
		
		btnToggleLed = new JButton("Toggle LED");
		btnToggleLed.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0){
				mShimmer.toggleLed();
			}
		});
		btnToggleLed.setBounds(10, 628, 110, 23);
		configFrame.getContentPane().add(btnToggleLed);
		
		comboBoxSamplingRate = new JComboBox<String>();
		comboBoxSamplingRate.setBounds(10, 305, 100, 20);
		configFrame.getContentPane().add(comboBoxSamplingRate);
		
		comboBoxAccelRange = new JComboBox<String>();
		comboBoxAccelRange.setBounds(10, 350, 100, 20);
		configFrame.getContentPane().add(comboBoxAccelRange);
		
		comboBoxGyroRange = new JComboBox<String>();
		comboBoxGyroRange.setBounds(10, 395, 100, 20);
		configFrame.getContentPane().add(comboBoxGyroRange);
		
		comboBoxMagRange = new JComboBox<String>();
		comboBoxMagRange.setBounds(200, 305, 100, 20);
		configFrame.getContentPane().add(comboBoxMagRange);
		
		comboBoxGsrRange = new JComboBox<String>();
		comboBoxGsrRange.setBounds(200, 350, 100, 20);
		configFrame.getContentPane().add(comboBoxGsrRange);
		
		comboBoxPressureResolution = new JComboBox<String>();
		comboBoxPressureResolution.setBounds(200, 395, 100, 20);
		configFrame.getContentPane().add(comboBoxPressureResolution);
		
		lblSampFreq = new JLabel("Sampling Frequency");
		lblSampFreq.setBounds(10, 290, 130, 14);
		configFrame.getContentPane().add(lblSampFreq);
		
		lblAccelRange = new JLabel("Accel Range");
		lblAccelRange.setBounds(10, 335, 130, 14);
		configFrame.getContentPane().add(lblAccelRange);
		
		lblGyroRange = new JLabel("Gyro Range");
		lblGyroRange.setBounds(10, 380, 130, 14);
		configFrame.getContentPane().add(lblGyroRange);
		
		lblMagRange = new JLabel("Mag Range");
		lblMagRange.setBounds(200, 290, 130, 14);
		configFrame.getContentPane().add(lblMagRange);
		
		lblGSRRange = new JLabel("GSR Range");
		lblGSRRange.setBounds(200, 335, 130, 14);
		configFrame.getContentPane().add(lblGSRRange);
		
		lblPressRange = new JLabel("Pressure Resolution");
		lblPressRange.setBounds(200, 380, 130, 14);
		configFrame.getContentPane().add(lblPressRange);
		
		lblSensors = new JLabel("Sensors to Sample");
		lblSensors.setBounds(10, 10, 116, 14);
		configFrame.getContentPane().add(lblSensors);
		
		chckbxVoltageMon = new JCheckBox("Enable Voltage Monitoring");
		chckbxVoltageMon.setBounds(10, 424, 230, 23);
		configFrame.getContentPane().add(chckbxVoltageMon);
		chckbxVoltageMon.setEnabled(false);
		
		chckbx5VReg = new JCheckBox("Enable 5V Regulator");
		chckbx5VReg.setBounds(10, 444, 230, 23);
		configFrame.getContentPane().add(chckbx5VReg);
		
		chckbx3DOrientation = new JCheckBox("Enable 3D Orientation");
		chckbx3DOrientation.setBounds(10, 464, 230, 23);
		configFrame.getContentPane().add(chckbx3DOrientation);
		
		chckbxOnTheFlyGyroCal = new JCheckBox("Enable Gyro On-the-Fly Calibration");
		chckbxOnTheFlyGyroCal.setBounds(10, 484, 230, 23);
		configFrame.getContentPane().add(chckbxOnTheFlyGyroCal);
		
		chckbxLowPowerMag = new JCheckBox("Enable Low Power Magnetometer");
		chckbxLowPowerMag.setBounds(10, 504, 230, 23);
		configFrame.getContentPane().add(chckbxLowPowerMag);
		chckbxLowPowerMag.setEnabled(false);
		
		chckbxLowPowerAcc = new JCheckBox("Enable Low Power Acceleration");
		chckbxLowPowerAcc.setBounds(10, 524, 230, 23);
		configFrame.getContentPane().add(chckbxLowPowerAcc);
		
		chckbxLowPowerGyro = new JCheckBox("Enable Low Power Gyroscope");
		chckbxLowPowerGyro.setBounds(10, 544, 230, 23);
		configFrame.getContentPane().add(chckbxLowPowerGyro);
		
		chckbxInternalExpPower = new JCheckBox("Enable Internal Exp Power");
		chckbxInternalExpPower.setBounds(10, 564, 230, 23);
		configFrame.getContentPane().add(chckbxInternalExpPower);
		
		chckbxEnablePPGtoHR = new JCheckBox("Enable PPG-HR");
		chckbxEnablePPGtoHR.setBounds(10, 584, 180, 23);
		configFrame.getContentPane().add(chckbxEnablePPGtoHR);
		chckbxEnablePPGtoHR.setEnabled(false);
		
		spinnerNumberOfBeatsToAve = new JSpinner();
		spinnerNumberOfBeatsToAve.setBounds(200, 628, 106, 24);
		configFrame.getContentPane().add(spinnerNumberOfBeatsToAve);
		spinnerNumberOfBeatsToAve.setValue(5);
		spinnerNumberOfBeatsToAve.setEnabled(false);
		
		lblNumberOfBeats = new JLabel("<html>No. Of Beats To<br/>Average (PPG-HR)</html>");
		lblNumberOfBeats.setBounds(200, 593, 106, 30);
		configFrame.getContentPane().add(lblNumberOfBeats);
		
		lblnoOfBeats = new JLabel("<html>No. Of Beats To<br/>Average (ECG-HR)</html>");
		lblnoOfBeats.setBounds(310, 592, 106, 30);
		configFrame.getContentPane().add(lblnoOfBeats);
		
		spinnerNumberOfBeatsToAveECG = new JSpinner();
		spinnerNumberOfBeatsToAveECG.setModel(new SpinnerNumberModel(new Integer(1), null, null, new Integer(1)));
		spinnerNumberOfBeatsToAveECG.setEnabled(false);
		spinnerNumberOfBeatsToAveECG.setBounds(310, 627, 106, 24);
		configFrame.getContentPane().add(spinnerNumberOfBeatsToAveECG);
		
		chckbxEnableECGtoHR = new JCheckBox("Enable ECG-HR");
		chckbxEnableECGtoHR.setEnabled(false);
		chckbxEnableECGtoHR.setBounds(10, 604, 180, 23);
		configFrame.getContentPane().add(chckbxEnableECGtoHR);
		
		//EXG Frame
		JPanel filteringPane = new JPanel();
		filteringPane.setLayout(null);
		filteringPane.setBounds(10, 10, 360, 115);
		exgFrame.getContentPane().add(filteringPane);

		chckbxHPF0_05 = new JCheckBox("Enable HPF 0.05Hz");
		chckbxHPF0_05.setBounds(10, 25, 140, 23);
		filteringPane.add(chckbxHPF0_05);
		
		chckbxHPF0_5 = new JCheckBox("Enable HPF 0.5Hz");
		chckbxHPF0_5.setBounds(10, 55, 140, 23);
		filteringPane.add(chckbxHPF0_5);
		
		chckbxHPF5 = new JCheckBox("Enable HPF 5Hz");
		chckbxHPF5.setBounds(10, 85, 140, 23);
		filteringPane.add(chckbxHPF5);
		
		chckbxBSF_50 = new JCheckBox("Enable BSF 49-51Hz");
		chckbxBSF_50.setBounds(180, 25, 140, 23);
		filteringPane.add(chckbxBSF_50);
		
		chckbxBSF_60 = new JCheckBox("Enable BSF 59-61Hz");
		chckbxBSF_60.setBounds(180, 55, 140, 23);
		filteringPane.add(chckbxBSF_60);
		
		JPanel settingsPane = new JPanel();
		settingsPane.setLayout(null);
		settingsPane.setBounds(10, 140, 360, 300);
		exgFrame.getContentPane().add(settingsPane);
		
		chckbxEcgConfig = new JCheckBox("Default ECG Config");
		chckbxEcgConfig.setBounds(10, 25, 150, 23);
		settingsPane.add(chckbxEcgConfig);
		
		chckbxEmgConfig = new JCheckBox("Default EMG Config");
		chckbxEmgConfig.setBounds(10, 55, 150, 23);
		settingsPane.add(chckbxEmgConfig);
		
		chckbxTestSignal = new JCheckBox("Test Signal");
		chckbxTestSignal.setBounds(10, 85, 150, 23);
		settingsPane.add(chckbxTestSignal);

		lblExgGainChip1Channel1 = new JLabel("EXG Gain Chip 1 Channel 1:");
		lblExgGainChip1Channel1.setBounds(10, 130, 153, 14);
		settingsPane.add(lblExgGainChip1Channel1);
		
		lblExgGainChip1Channel2 = new JLabel("EXG Gain Chip 1 Channel 2:");
		lblExgGainChip1Channel2.setBounds(180, 130, 153, 14);
		settingsPane.add(lblExgGainChip1Channel2);
		
		lblExgGainChip2Channel1 = new JLabel("EXG Gain Chip 2 Channel 1:");
		lblExgGainChip2Channel1.setBounds(10, 175, 153, 14);
		settingsPane.add(lblExgGainChip2Channel1);
		
		lblExgGainChip2Channel2 = new JLabel("EXG Gain Chip 2 Channel 2:");
		lblExgGainChip2Channel2.setBounds(180, 175, 153, 14);
		settingsPane.add(lblExgGainChip2Channel2);
		
		comboBoxGainChip1Channel1 = new JComboBox<Integer>();
		comboBoxGainChip1Channel1.setBounds(10, 145, 100, 20);
		settingsPane.add(comboBoxGainChip1Channel1);
		
		comboBoxGainChip1Channel2 = new JComboBox<Integer>();
		comboBoxGainChip1Channel2.setBounds(180, 145, 100, 20);
		settingsPane.add(comboBoxGainChip1Channel2);
		
		comboBoxGainChip2Channel1 = new JComboBox<Integer>();
		comboBoxGainChip2Channel1.setBounds(10, 190, 100, 20);
		settingsPane.add(comboBoxGainChip2Channel1);
		
		comboBoxGainChip2Channel2 = new JComboBox<Integer>();
		comboBoxGainChip2Channel2.setBounds(180, 190, 100, 20);
		settingsPane.add(comboBoxGainChip2Channel2);

		lblChip1 = new JLabel("Chip 1:");
		lblChip1.setBounds(10, 240, 45, 14);
		settingsPane.add(lblChip1);
		
		lblChip2 = new JLabel("Chip 2:");
		lblChip2.setBounds(10, 270, 45, 14);
		settingsPane.add(lblChip2);
		
		for (int count=0; count<10; count++) {
			textFieldChip1[count] = new JTextField();
			textFieldChip1[count].setBounds(60+((count)*28), 240, 25, 20);
			settingsPane.add(textFieldChip1[count]);
			textFieldChip1[count].setColumns(10);
		}
		for (int count=0; count<10; count++) {
			textFieldChip2[count] = new JTextField();
			textFieldChip2[count].setBounds(60+((count)*28), 270, 25, 20);
			settingsPane.add(textFieldChip2[count]);
			textFieldChip2[count].setColumns(10);
		}
		
		btnExgOk = new JButton("OK");
		btnExgOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				exgConfigurationDone();
			}
		});
		btnExgOk.setBounds(190, 455, 110, 25);
		exgFrame.getContentPane().add(btnExgOk);
		
		btnExgCancel = new JButton("Cancel");
		btnExgCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				exgFrame.setVisible(false);
			}
		});
		btnExgCancel.setBounds(20, 455, 110, 25);
		exgFrame.getContentPane().add(btnExgCancel);
		
		Color gray = new Color(214, 214, 214);
		Border border = BorderFactory.createLineBorder(gray);
		filteringPane.setBorder(BorderFactory.createTitledBorder(border, "Filtering Options *", TitledBorder.LEFT, TitledBorder.TOP));
		Border settingsBorder = BorderFactory.createLineBorder(gray);
		settingsPane.setBorder(BorderFactory.createTitledBorder(settingsBorder, "EXG Settings", TitledBorder.LEFT, TitledBorder.TOP));

		
		//Logging - file exists
		fileExistsWindow.setBounds (300, 300, 320, 150);
		fileExistsWindow.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		fileExistsWindow.getContentPane().setLayout(null);
		fileExistsWindow.setVisible(false);
		
		lblFileExists = new JLabel("<html>File already exists. <br/>Do you want to overwrite this file?</html>");
		lblFileExists.setBounds(10, 10, 250, 40);
		fileExistsWindow.getContentPane().add(lblFileExists);
		
		btnOverwriteFile = new JButton("Overwrite");
		btnOverwriteFile.setBounds(160, 70, 130, 25);
		fileExistsWindow.getContentPane().add(btnOverwriteFile);
		
		btnCancelWriteFile = new JButton("Choose New File");
		btnCancelWriteFile.setBounds(10, 70, 130, 25);
		fileExistsWindow.getContentPane().add(btnCancelWriteFile);
		
		
		
//		//Heart Rate panel
//		chartPPG = new Chart2D();
//		chartPPG.setBounds(10, 100, 650, 220);
//		paneHeartRate.add(chartPPG);
//		IAxis<?> xAxisPPG = chartPPG.getAxisX();
////		xAxisPPG.setVisible(false);
//		IAxis<?> yAxisPPG = chartPPG.getAxisY();
//		yAxisPPG.setAxisTitle(new IAxis.AxisTitle("Heart Rate (BPM)"));
//		chartPPG.setBackground(Color.WHITE);
//		tracePPG = new Trace2DLtd(500); 
//		tracePPG.setColor(Color.RED);
//		tracePPG.setName("PPG");
//		
//		chartHR = new Chart2D();
//		chartHR.setBounds(10, 340, 650, 220);
//		paneHeartRate.add(chartHR);
//		IAxis<?> xAxisHR = chartHR.getAxisX();
//		xAxisHR.setVisible(false);
//		yAxisHR = chartHR.getAxisY();
//		yAxisHR.setAxisTitle(new IAxis.AxisTitle("Heart Rate (BPM)"));
//		chartHR.setBackground(Color.WHITE);
//		traceHR = new Trace2DLtd(500); 
//		traceHR.setColor(Color.RED);
//		traceHR.setName("Heart Rate");
//		
//		lblPpgSignal = new JLabel("PPG Signal");
//		lblPpgSignal.setBounds(10, 85, 100, 14);
//		paneHeartRate.add(lblPpgSignal);
//		
//		lblHeartRate = new JLabel("Heart Rate");
//		lblHeartRate.setBounds(10, 325, 100, 14);
//		paneHeartRate.add(lblHeartRate);
//		
//		lblHeartRateOutput = new JLabel("Heart Rate");
//		lblHeartRateOutput.setBounds(670, 340, 100, 14);
//		paneHeartRate.add(lblHeartRateOutput);
//		
//		lblPpgChannelName = new JLabel("PPG Channel Name");
//		lblPpgChannelName.setBounds(50, 20, 150, 14);
//		paneHeartRate.add(lblPpgChannelName);
//		
//		lblNumberOfBeats = new JLabel("Number Of Beats To Average");
//		lblNumberOfBeats.setBounds(300, 20, 200, 14);
//		paneHeartRate.add(lblNumberOfBeats);
//		
//		comboBoxPpgChannel = new JComboBox<String>();
//		comboBoxPpgChannel.setBounds(50, 35, 150, 24);
//		paneHeartRate.add(comboBoxPpgChannel);
//		
//		spinnerNumberOfBeatsToAve = new JSpinner();
//		spinnerNumberOfBeatsToAve.setBounds(300, 35, 40, 24);
//		paneHeartRate.add(spinnerNumberOfBeatsToAve);
//		spinnerNumberOfBeatsToAve.setValue(5);
//		
//		textFieldHeartRate = new JTextField("");
//		textFieldHeartRate.setBounds(670, 355, 80, 24);
//		paneHeartRate.add(textFieldHeartRate);
//		textFieldHeartRate.setColumns(10);
	}
	
	private void openFile() {
		JFileChooser fr = new JFileChooser();
		fr.setAcceptAllFileFilterUsed(false);
		FileNameExtensionFilter filter = new FileNameExtensionFilter("csv files (*.csv)", "csv");
		fr.setFileFilter(filter);
		returnVal = fr.showOpenDialog(frame);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fr.getSelectedFile();
            fileName = file.toString();	
            log = new LoggingPC(fileName, ",");
            if (log.mFileExists) {
            	fileExistsWindow.setVisible(true);
            	
        		btnOverwriteFile.addActionListener(new ActionListener() {
        			public void actionPerformed(ActionEvent arg0) {
        				fileExistsWindow.setVisible(false);
        				loggingData=true;
        			}
        		});
        		btnCancelWriteFile.addActionListener(new ActionListener() {
        			public void actionPerformed(ActionEvent arg0) {
        				fileExistsWindow.setVisible(false);
        				loggingData=false;
        				openFile();
        			}
        		});
            } else {
            	loggingData=true;
				textFieldMessage.setText("Saving data to " + fileName);
            }
		} else {
			menuItemSaveToCsv.setSelected(false);
		}
	}
	
	private void connected() {
		mShimmer.readConfigBytes();
		mShimmerVersion = mShimmer.getShimmerVersion();
		if (firstConfiguration) {
			if (mShimmerVersion==SHIMMER_SR30 || mShimmerVersion==SHIMMER_3) {
				for (int count=0; count<listofCompatibleSensorsShimmer3.length; count++) {
					configFrame.getContentPane().add(listOfSensorsShimmer3[count]);
				}
				for (int i=0; i<samplingFreqS3.length; i++) {
					comboBoxSamplingRate.addItem(samplingFreqS3[i]);
				}
				for (int j=0; j<SensorLSM303DLHC.ListofLSM303AccelRange.length; j++) {
					comboBoxAccelRange.addItem(SensorLSM303DLHC.ListofLSM303AccelRange[j]);
				}
				for (int k=0; k<SensorMPU9X50.ListofGyroRange.length; k++) {
					System.out.print("SetComboBox");
					comboBoxGyroRange.addItem(SensorMPU9X50.ListofGyroRange[k]);
				}
				for (int l=0; l<SensorLSM303DLHC.ListofLSM303DLHCMagRange.length; l++) {
					comboBoxMagRange.addItem(SensorLSM303DLHC.ListofLSM303DLHCMagRange[l]);
				}
				for (int m=0; m<SensorGSR.ListofGSRRangeResistance.length; m++) {
					comboBoxGsrRange.addItem(SensorGSR.ListofGSRRangeResistance[m]);
				}
				for (int n=0; n<SensorBMP180.ListofPressureResolution.length; n++) {
					comboBoxPressureResolution.addItem(SensorBMP180.ListofPressureResolution[n]);
				}
				chckbxVoltageMon.setVisible(false);
			} else {
				for (int count=0; count<listofCompatibleSensorsShimmer2.length; count++) {
					configFrame.getContentPane().add(listOfSensorsShimmer2[count]);
				}
				for (int i=0; i<samplingFreqS2.length; i++) {
					comboBoxSamplingRate.addItem(samplingFreqS2[i]);
				}
				for (int j=0; j<Configuration.Shimmer2.ListofAccelRange.length; j++) {
					comboBoxAccelRange.addItem(Configuration.Shimmer2.ListofAccelRange[j]);
				}
				for (int l=0; l<Configuration.Shimmer2.ListofMagRange.length; l++) {
					comboBoxMagRange.addItem(Configuration.Shimmer2.ListofMagRange[l]);
				}
				for (int m=0; m<Configuration.Shimmer2.ListofGSRRange.length; m++) {
					comboBoxGsrRange.addItem(Configuration.Shimmer2.ListofGSRRange[m]);
				}
			}			

			comboBoxAccelRange.setEnabled(false);
			comboBoxGsrRange.setEnabled(false);
			comboBoxGyroRange.setEnabled(false);
			comboBoxMagRange.setEnabled(false);
			comboBoxPressureResolution.setEnabled(false);
			
			firstConfiguration=false;
		}

		enabledSensorSignals = mShimmer.getListofEnabledChannelSignals();
		numberOfSignals = enabledSensorSignals.length;
		if (numberOfSignals>maxTraces){
			numberOfSignals=maxTraces;
		}
		
		for (int count=0; count<numberOfSignals; count++) {
			traces[count] = new Trace2DLtd(500); 
			traces[count].setColor(traceColours[count]);
			traces[count].setName(enabledSensorSignals[count]);
			chart.addTrace(traces[count]);
		}
		traceHR = new Trace2DLtd(500);
		Color hrColour = new Color(85, 30, 230);
		traceHR.setColor(hrColour);
		traceHR.setName("Heart Rate");
		if (calculateHeartRate){
			//chart.addTrace(traceHR);
		}
		
		menuItemConfigure.setEnabled(true);
		lblSignals.setVisible(true);
			
		samplingRate = mShimmer.getSamplingRateShimmer();
		DecimalFormat df = new DecimalFormat("#.#");
		String rate = df.format(samplingRate);
		String SamplingRate = rate + "Hz";
		comboBoxSamplingRate.setSelectedItem(SamplingRate);
		if (mShimmer.getHardwareVersion()==HW_ID.SHIMMER_2R){
			if (mShimmer.getAccelRange()==3){
				comboBoxAccelRange.setSelectedIndex(1);
			} else {
				comboBoxAccelRange.setSelectedIndex(mShimmer.getAccelRange());
			}
		} else{
			comboBoxAccelRange.setSelectedIndex(mShimmer.getAccelRange());
		}
		
		
		comboBoxGsrRange.setSelectedIndex(mShimmer.getGSRRange());			
		if (mShimmerVersion==SHIMMER_SR30 || mShimmerVersion==SHIMMER_3) {
			System.out.print("Gyro Range  "+  comboBoxGyroRange.getItemCount()  + " "+ mShimmer.getGyroRange() + "\n");
			
			comboBoxMagRange.setSelectedIndex(mShimmer.getMagRange()-1);
			comboBoxGyroRange.setSelectedIndex(mShimmer.getGyroRange());
			comboBoxPressureResolution.setSelectedIndex(mShimmer.getPressureResolution());
			menuItemExgSettings.setEnabled(true);
		} else if(mShimmerVersion==HW_ID.SHIMMER_2R){
			comboBoxMagRange.setSelectedIndex(mShimmer.getMagRange());
		}
		chart.setVisible(true);	
		
	}
	
	/**
	 * Setting up configuration panel.
	 */
	private void configuration() {
		
		configFrame.setVisible(true);
		List<String> enabledSensors = mShimmer.getListofEnabledSensors();
		
		//Incase Cancel was clicked last time
		if (mShimmerVersion==SHIMMER_SR30 || mShimmerVersion==SHIMMER_3){
			for (int i=0; i<enabledSensors.size(); i++) {
				for (int j=0; j<listofCompatibleSensorsShimmer3.length; j++) {
					if (listOfSensorsShimmer3[j].isSelected() && (listofCompatibleSensorsShimmer3[j]!=enabledSensors.get(i))){
						listOfSensorsShimmer3[j].setSelected(false);
					}
					if(listofCompatibleSensorsShimmer3[j]==enabledSensors.get(i)){
						listOfSensorsShimmer3[j].setSelected(true);
					}
				}
			}
		} else {
			for (int i=0; i<enabledSensors.size(); i++) {
				for (int j=0; j<listofCompatibleSensorsShimmer2.length; j++) {
					if (listOfSensorsShimmer2[j].isSelected() && (listofCompatibleSensorsShimmer2[j]!=enabledSensors.get(i))){
						listOfSensorsShimmer2[j].setSelected(false);
					}
					if(listofCompatibleSensorsShimmer2[j]==enabledSensors.get(i)){
						listOfSensorsShimmer2[j].setSelected(true);
					}
				}
			}
		}
		
		for (int i=0; i<enabledSensors.size(); i++){
			String enabledSensor = enabledSensors.get(i);
			if (mShimmerVersion==SHIMMER_SR30 || mShimmerVersion==SHIMMER_3){
				for (int j=0; j<listOfSensorsShimmer3.length; j++){
					if (enabledSensor == listOfSensorsShimmer3[j].getText()) {
						listOfSensorsShimmer3[j].setSelected(true);
					}
				}
				if (listOfSensorsShimmer3[1].isSelected()) {
					comboBoxAccelRange.setEnabled(true);
				} else {
					comboBoxAccelRange.setEnabled(false);
				}
				if (listOfSensorsShimmer3[2].isSelected()) {
					comboBoxGyroRange.setEnabled(true);
				} else {
					comboBoxGyroRange.setEnabled(false);
				}
				if (listOfSensorsShimmer3[3].isSelected()) {
					comboBoxMagRange.setEnabled(true);
				} else {
					comboBoxMagRange.setEnabled(false);
				}
				if (listOfSensorsShimmer3[12].isSelected()) {
					comboBoxPressureResolution.setEnabled(true);
				} else {
					comboBoxPressureResolution.setEnabled(false);
				}
				if (listOfSensorsShimmer3[13].isSelected()) {
					comboBoxGsrRange.setEnabled(true);
					listOfSensorsShimmer3[10].setText("Internal ADC A13");
				//	listOfSensorsShimmer3[10].setText("Internal ADC A13/PPG");
				} else {
					comboBoxGsrRange.setEnabled(false);
					listOfSensorsShimmer3[10].setText("Internal ADC A13");
				}
			} else if (mShimmerVersion==SHIMMER_2 || mShimmerVersion==SHIMMER_2R) {
				for (int j=0; j<listOfSensorsShimmer2.length; j++){
					if (enabledSensor == listOfSensorsShimmer2[j].getText()) {
						listOfSensorsShimmer2[j].setSelected(true);
					}
				}
				if (listOfSensorsShimmer2[0].isSelected()) {
					comboBoxAccelRange.setEnabled(true);
				} else {
					comboBoxAccelRange.setEnabled(false);
				}
				
				if (listOfSensorsShimmer2[2].isSelected()) {
					comboBoxMagRange.setEnabled(true);
				} else {
					comboBoxMagRange.setEnabled(false);
				}
				
				if (listOfSensorsShimmer2[6].isSelected()) {
					comboBoxGsrRange.setEnabled(true);
				} else {
					comboBoxGsrRange.setEnabled(false);
				}
			}
		}
		
		if (mShimmer.get5VReg() == 1){
			chckbx5VReg.setSelected(true);
		} else {
			chckbx5VReg.setSelected(false);
		}
		if (mShimmer.is3DOrientatioEnabled()){
			chckbx3DOrientation.setSelected(true);
		} else {
			chckbx3DOrientation.setSelected(false);
		}
		if (mShimmer.isGyroOnTheFlyCalEnabled()){
			chckbxOnTheFlyGyroCal.setSelected(true);
		} else {
			chckbxOnTheFlyGyroCal.setSelected(false);
		}
		if (mShimmer.isLowPowerAccelEnabled()){
			chckbxLowPowerAcc.setSelected(true);
		} else {
			chckbxLowPowerAcc.setSelected(false);
		}
		if (mShimmer.isLowPowerGyroEnabled()){
			chckbxLowPowerGyro.setSelected(true);
		} else {
			chckbxLowPowerGyro.setSelected(false);
		}
		if (mShimmer.isLowPowerMagEnabled()){
			chckbxLowPowerMag.setSelected(true);
		} else {
			chckbxLowPowerMag.setSelected(false);
		}
		if (mShimmer.getInternalExpPower()==1){
			chckbxInternalExpPower.setSelected(true);
		} else {
			chckbxInternalExpPower.setSelected(false);
		}
		
		if (mShimmerVersion==SHIMMER_SR30 || mShimmerVersion==SHIMMER_3) {
			chckbx5VReg.setEnabled(false);
			chckbxLowPowerAcc.setEnabled(true);
			chckbxLowPowerGyro.setEnabled(true);
			chckbxInternalExpPower.setEnabled(true);
			
			if (listOfSensorsShimmer3[10].isSelected() && listOfSensorsShimmer3[13].isSelected() && chckbxInternalExpPower.isSelected()){
				chckbxEnablePPGtoHR.setEnabled(true);
			} else {
				chckbxEnablePPGtoHR.setEnabled(false);
				chckbxEnablePPGtoHR.setSelected(false);
				spinnerNumberOfBeatsToAve.setEnabled(false);
			}
			
			if (((listOfSensorsShimmer3[14].isSelected() && listOfSensorsShimmer3[15].isSelected()) 
					|| (listOfSensorsShimmer3[16].isSelected() && listOfSensorsShimmer3[17].isSelected())) 
					&& (mShimmer.isEXGUsingDefaultECGConfiguration())){
				chckbxEnableECGtoHR.setEnabled(true);
			} else {
				chckbxEnableECGtoHR.setEnabled(false);
				chckbxEnableECGtoHR.setSelected(false);
				spinnerNumberOfBeatsToAveECG.setEnabled(false);
			}
			
			if (chckbxEnableECGtoHR.isSelected()){
				spinnerNumberOfBeatsToAveECG.setEnabled(true);
			} else {
				spinnerNumberOfBeatsToAveECG.setEnabled(false);
			}
			
			if (chckbxEnablePPGtoHR.isSelected()){
				spinnerNumberOfBeatsToAve.setEnabled(true);
			} else {
				spinnerNumberOfBeatsToAve.setEnabled(false);
			}
			
			listOfSensorsShimmer3[1].addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent arg0) {
					if (listOfSensorsShimmer3[1].isSelected()) {
						comboBoxAccelRange.setEnabled(true);
					} else {
						comboBoxAccelRange.setEnabled(false);
					}
				}
			});
			listOfSensorsShimmer3[2].addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent arg0) {
					if (listOfSensorsShimmer3[2].isSelected()) {
						comboBoxGyroRange.setEnabled(true);
					} else {
						comboBoxGyroRange.setEnabled(false);
					}
				}
			});
			listOfSensorsShimmer3[3].addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent arg0) {
					if (listOfSensorsShimmer3[3].isSelected()) {
						chckbxLowPowerMag.setEnabled(true);
						comboBoxMagRange.setEnabled(true);
					} else {
						chckbxLowPowerMag.setEnabled(false);
						comboBoxMagRange.setEnabled(false);
					}
				}
			});
			listOfSensorsShimmer3[10].addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent arg0) {
					if (listOfSensorsShimmer3[10].isSelected()) {
						if (listOfSensorsShimmer3[13].isSelected() && chckbxInternalExpPower.isSelected())
						{
							chckbxEnablePPGtoHR.setEnabled(true);
						}
					} else {
						chckbxEnablePPGtoHR.setEnabled(false);
						chckbxEnablePPGtoHR.setSelected(false);
						spinnerNumberOfBeatsToAve.setEnabled(false);
					}
				}
			});
			listOfSensorsShimmer3[12].addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent arg0) {
					if (listOfSensorsShimmer3[12].isSelected()) {
						comboBoxPressureResolution.setEnabled(true);
					} else {
						comboBoxPressureResolution.setEnabled(false);
					}
				}
			});
			listOfSensorsShimmer3[13].addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent arg0) {
					if (listOfSensorsShimmer3[13].isSelected()) {
						comboBoxGsrRange.setEnabled(true);
						listOfSensorsShimmer3[10].setText("Internal ADC A13");
				//		listOfSensorsShimmer3[10].setText("Internal ADC A13/PPG");
						if (listOfSensorsShimmer3[10].isSelected() && chckbxInternalExpPower.isSelected())
						{
							chckbxEnablePPGtoHR.setEnabled(true);
						}
					} else {
						comboBoxGsrRange.setEnabled(false);
						listOfSensorsShimmer3[10].setText("Internal ADC A13");
						chckbxEnablePPGtoHR.setEnabled(false);
						chckbxEnablePPGtoHR.setSelected(false);
						spinnerNumberOfBeatsToAve.setEnabled(false);
					}
				}
			});
			listOfSensorsShimmer3[14].addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent arg0) {
					if (listOfSensorsShimmer3[14].isSelected()) {
						listOfSensorsShimmer3[16].setSelected(false);
					} 
					if (listOfSensorsShimmer3[14].isSelected() && listOfSensorsShimmer3[15].isSelected() && mShimmer.isEXGUsingDefaultECGConfiguration())
					{
						chckbxEnableECGtoHR.setEnabled(true);
					}
				}
			});
			listOfSensorsShimmer3[15].addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent arg0) {
					if (listOfSensorsShimmer3[15].isSelected()) {
						listOfSensorsShimmer3[17].setSelected(false);
					}
					if (listOfSensorsShimmer3[14].isSelected() && listOfSensorsShimmer3[15].isSelected() && mShimmer.isEXGUsingDefaultECGConfiguration())
					{
						chckbxEnableECGtoHR.setEnabled(true);
					}
				}
			});
			listOfSensorsShimmer3[16].addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent arg0) {
					if (listOfSensorsShimmer3[16].isSelected()) {
						listOfSensorsShimmer3[14].setSelected(false);
					} 
					if (listOfSensorsShimmer3[16].isSelected() && listOfSensorsShimmer3[17].isSelected() && mShimmer.isEXGUsingDefaultECGConfiguration())
					{
						chckbxEnableECGtoHR.setEnabled(true);
					}
				}
			});
			listOfSensorsShimmer3[17].addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent arg0) {
					if (listOfSensorsShimmer3[17].isSelected()) {
						listOfSensorsShimmer3[15].setSelected(false);
					} 
					if (listOfSensorsShimmer3[16].isSelected() && listOfSensorsShimmer3[17].isSelected() && mShimmer.isEXGUsingDefaultECGConfiguration())
					{
						chckbxEnableECGtoHR.setEnabled(true);
					}
				}
			});
			chckbxInternalExpPower.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent arg0) {
					if (chckbxInternalExpPower.isSelected()) {
						if (listOfSensorsShimmer3[10].isSelected() && listOfSensorsShimmer3[13].isSelected())
						{
							chckbxEnablePPGtoHR.setEnabled(true);
						}
					} else {
						chckbxEnablePPGtoHR.setEnabled(false);
						chckbxEnablePPGtoHR.setSelected(false);
						spinnerNumberOfBeatsToAve.setEnabled(false);
					}
				}
			});
			chckbxEnablePPGtoHR.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent arg0) {
					if (chckbxEnablePPGtoHR.isSelected()){
						if (!spinnerNumberOfBeatsToAve.isEnabled()){
							spinnerNumberOfBeatsToAve.setEnabled(true);
							JOptionPane.showMessageDialog(null,"Users should note that a Low Pass Filter (5Hz Cutoff) and a High Pass Filter (0.5Hz Cutoff) is used when calculating Heart Rate value","PPG To Heart Rate",JOptionPane.WARNING_MESSAGE);
						}
					} else {
						spinnerNumberOfBeatsToAve.setEnabled(false);
					}
				}
			});
			
			chckbxEnableECGtoHR.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent arg0) {
					if (chckbxEnableECGtoHR.isSelected()){
						if (!spinnerNumberOfBeatsToAveECG.isEnabled()){
							spinnerNumberOfBeatsToAveECG.setEnabled(true);
							JOptionPane.showMessageDialog(null,"Users are recomended to use High Pass Filter (0.5Hz Cutoff) and appropriate band stop filter. Filters can be selected in ExG Settings page. LA_RA used for calculation.","ECG To Heart Rate",JOptionPane.WARNING_MESSAGE);
						}
					} else {
						spinnerNumberOfBeatsToAveECG.setEnabled(false);
					}
				}
			});
			
		} else { //Shimmer 2
			chckbx5VReg.setEnabled(true);
			chckbxVoltageMon.setEnabled(true);
			chckbxLowPowerAcc.setEnabled(false);
			chckbxLowPowerGyro.setEnabled(false);
			chckbxInternalExpPower.setEnabled(false);
			
			listOfSensorsShimmer2[0].addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent arg0) {
					if (listOfSensorsShimmer2[0].isSelected()) {
						comboBoxAccelRange.setEnabled(true);
					} else {
						comboBoxAccelRange.setEnabled(false);
					}
				}
			});
			listOfSensorsShimmer2[2].addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent arg0) {
					if (listOfSensorsShimmer2[2].isSelected()) {
						comboBoxMagRange.setEnabled(true);
						chckbxLowPowerMag.setEnabled(true);
					} else {
						comboBoxMagRange.setEnabled(false);
						chckbxLowPowerMag.setEnabled(false);
					}
					
				}
			});
			listOfSensorsShimmer2[6].addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent arg0) {
					if (listOfSensorsShimmer2[6].isSelected()) {
						comboBoxGsrRange.setEnabled(true);
					} else {
						comboBoxGsrRange.setEnabled(false);
					}
					
				}
			});
			listOfSensorsShimmer2[3].addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent arg0) {
					listOfSensorsShimmer2[3].setSelected(true);
					if (listOfSensorsShimmer2[3].isSelected()) {
						listOfSensorsShimmer2[7].setSelected(false);
					}
				}
			});
			listOfSensorsShimmer2[7].addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent arg0) {
					listOfSensorsShimmer2[7].setSelected(true);
					if (listOfSensorsShimmer2[7].isSelected()) {
						listOfSensorsShimmer2[3].setSelected(false);
					}
				}
			});
		}
	}
	
	/**
	 * Applying configuration changes
	 */
	private void configurationDone() {
		String samplingRate = (String)comboBoxSamplingRate.getSelectedItem();
		samplingRate = samplingRate.substring(0, samplingRate.length()-2);
		double SamplingRate = Double.parseDouble(samplingRate);
		mShimmer.writeShimmerAndSensorsSamplingRate(SamplingRate);

		if (chckbx3DOrientation.isSelected()) {
			mShimmer.enable3DOrientation(true);
		} else {
			mShimmer.enable3DOrientation(false);
		}
		
		double threshold = 1.2; //Default value.
		int bufferSize = 100;
		if (chckbxOnTheFlyGyroCal.isSelected()) {
			mShimmer.enableOnTheFlyGyroCal(true, bufferSize, threshold);
		} else {
			mShimmer.enableOnTheFlyGyroCal(false, bufferSize, threshold);
		}
		
		if (chckbxLowPowerMag.isSelected()) {
			mShimmer.enableLowPowerMag(true);
		} else {
			mShimmer.enableLowPowerMag(false);
		}
		
		indexAccel = comboBoxAccelRange.getSelectedIndex();
		indexGyro = comboBoxGyroRange.getSelectedIndex();
		indexMag = comboBoxMagRange.getSelectedIndex();
		indexGSR = comboBoxGsrRange.getSelectedIndex();
		indexPressureRes = comboBoxPressureResolution.getSelectedIndex();
		if (mShimmerVersion==SHIMMER_SR30 || mShimmerVersion==SHIMMER_3) {
			mShimmer.writeAccelRange(indexAccel);
			mShimmer.writeGyroRange(indexGyro);
			mShimmer.writeMagRange(indexMag+1);
			mShimmer.writeGSRRange(indexGSR);
			mShimmer.writePressureResolution(indexPressureRes);
			if (chckbxLowPowerAcc.isSelected()) {
				mShimmer.enableLowPowerAccel(true);
			} else {
				mShimmer.enableLowPowerAccel(false);
			}
			if (chckbxLowPowerGyro.isSelected()) {
				mShimmer.enableLowPowerGyro(true);
			} else {
				mShimmer.enableLowPowerGyro(false);
			}
			if (chckbxInternalExpPower.isSelected()) {
				mShimmer.writeInternalExpPower(1);
			} else {
				mShimmer.writeInternalExpPower(0);
			}
			if (chckbxEnablePPGtoHR.isSelected()) {
				calculateHeartRate = true;
			} else if (chckbxEnableECGtoHR.isSelected()) {
				calculateHeartRate = true;
			} else {
				calculateHeartRate = false;
			}
		} else { //Shimmer 2
			if (chckbx5VReg.isSelected()) {
				mShimmer.writeFiveVoltReg(1);
			} else {
				mShimmer.writeFiveVoltReg(0);
			}
			
			if(indexAccel==1){
				mShimmer.writeAccelRange(3);
			} else {
				mShimmer.writeAccelRange(indexAccel);
			}
			mShimmer.writeMagRange(indexMag);
		}
		configFrame.setVisible(false);
		enableSensors();
	}
	
	/**
	 * Enabling selected sensors
	 */
	private void enableSensors() {
		chart.removeAllTraces();
		for (int count=0; count<maxTraces; count++) {
	    	traces[count] = new Trace2DLtd(300); 
	    	traces[count].setColor(traceColours[count]);
	    	chart.addTrace(traces[count]);
	    	traces[count].setVisible(false);
	    }
		if (mShimmerVersion==SHIMMER_SR30 || mShimmerVersion==SHIMMER_3) {
			if (listOfSensorsShimmer3[0].isSelected()) {
        		mReturnEnabledSensors=mReturnEnabledSensors | ShimmerPC.SENSOR_ACCEL;
	       	}
	       	if (listOfSensorsShimmer3[1].isSelected()) {
        		mReturnEnabledSensors=mReturnEnabledSensors | ShimmerPC.SENSOR_DACCEL;
	        }
	       	if (listOfSensorsShimmer3[2].isSelected()) {
	       		mReturnEnabledSensors=mReturnEnabledSensors | ShimmerPC.SENSOR_GYRO;
	       	}
	       	if (listOfSensorsShimmer3[3].isSelected()) {
	       		mReturnEnabledSensors=mReturnEnabledSensors | ShimmerPC.SENSOR_MAG;
	       	}
	       	if (listOfSensorsShimmer3[4].isSelected()) {
	       		mReturnEnabledSensors=mReturnEnabledSensors | ShimmerPC.SENSOR_BATT;
        	}
	        if (listOfSensorsShimmer3[5].isSelected()) {
	       		mReturnEnabledSensors=mReturnEnabledSensors | ShimmerPC.SENSOR_EXT_ADC_A7;
	       	}
	       	if (listOfSensorsShimmer3[6].isSelected()) {
	       		mReturnEnabledSensors=mReturnEnabledSensors | ShimmerPC.SENSOR_EXT_ADC_A6;
	       	}
	       	if (listOfSensorsShimmer3[7].isSelected()) {
	       		mReturnEnabledSensors=mReturnEnabledSensors | ShimmerPC.SENSOR_EXT_ADC_A15;
	       	}
        	if (listOfSensorsShimmer3[8].isSelected()) {
	        	mReturnEnabledSensors=mReturnEnabledSensors | ShimmerPC.SENSOR_INT_ADC_A1;
	        }
	       	if (listOfSensorsShimmer3[9].isSelected()) {
	       		mReturnEnabledSensors=mReturnEnabledSensors | ShimmerPC.SENSOR_INT_ADC_A12;
	       	}
	       	if (listOfSensorsShimmer3[10].isSelected()) {
	       		mReturnEnabledSensors=mReturnEnabledSensors | ShimmerPC.SENSOR_INT_ADC_A13;
	       	}
	       	if (listOfSensorsShimmer3[11].isSelected()) {
	       		mReturnEnabledSensors=mReturnEnabledSensors | ShimmerPC.SENSOR_INT_ADC_A14;
        	}
	        if (listOfSensorsShimmer3[12].isSelected()) {
	       		mReturnEnabledSensors=mReturnEnabledSensors | ShimmerPC.SENSOR_BMPX80;
	       	}
	       	if (listOfSensorsShimmer3[13].isSelected()){
	       		mReturnEnabledSensors=mReturnEnabledSensors | ShimmerPC.SENSOR_GSR;
	       	}
	       	if (listOfSensorsShimmer3[14].isSelected()) {
	       		mReturnEnabledSensors=mReturnEnabledSensors | ShimmerPC.SENSOR_EXG1_24BIT;
	       	} 
	       	if (listOfSensorsShimmer3[15].isSelected()) {
	       		mReturnEnabledSensors=mReturnEnabledSensors | ShimmerPC.SENSOR_EXG2_24BIT;
	       	} 
	       	if (listOfSensorsShimmer3[16].isSelected()) {
	       		mReturnEnabledSensors=mReturnEnabledSensors | ShimmerPC.SENSOR_EXG1_16BIT;
	       	}
	       	if (listOfSensorsShimmer3[17].isSelected()) {
	       		mReturnEnabledSensors=mReturnEnabledSensors | ShimmerPC.SENSOR_EXG2_16BIT;
	       	}
	       	if (listOfSensorsShimmer3[18].isSelected()) {
	       		mReturnEnabledSensors=mReturnEnabledSensors | ShimmerPC.SENSOR_BRIDGE_AMP;
	       	}
		} else { //Shimmer 2
	        if (listOfSensorsShimmer2[0].isSelected()) {
	       		mReturnEnabledSensors=mReturnEnabledSensors | ShimmerPC.SENSOR_ACCEL;
	       	}
	       	if (listOfSensorsShimmer2[1].isSelected()) {
	       		mReturnEnabledSensors=mReturnEnabledSensors | ShimmerPC.SENSOR_GYRO;
	       	}
	       	if (listOfSensorsShimmer2[2].isSelected()) {
	       		mReturnEnabledSensors=mReturnEnabledSensors | ShimmerPC.SENSOR_MAG;
	       	}
        	if (listOfSensorsShimmer2[3].isSelected()) {
	        	mReturnEnabledSensors=mReturnEnabledSensors | ShimmerPC.SENSOR_BATT;
	        }
	       	if (listOfSensorsShimmer2[4].isSelected()) {
	       		mReturnEnabledSensors=mReturnEnabledSensors | ShimmerPC.SENSOR_ECG;
	       	}
	       	if (listOfSensorsShimmer2[5].isSelected()) {
	       		mReturnEnabledSensors=mReturnEnabledSensors | ShimmerPC.SENSOR_EMG;
	       	}
	       	if (listOfSensorsShimmer2[6].isSelected()) {
	       		mReturnEnabledSensors=mReturnEnabledSensors | ShimmerPC.SENSOR_GSR;
        	}
	        if (listOfSensorsShimmer2[7].isSelected()) {
	       		mReturnEnabledSensors=mReturnEnabledSensors | ShimmerPC.SENSOR_EXP_BOARD_A0;
	       		mReturnEnabledSensors=mReturnEnabledSensors | ShimmerPC.SENSOR_EXP_BOARD_A7;
	       	}
	       	if (listOfSensorsShimmer2[8].isSelected()) {
	       		mReturnEnabledSensors=mReturnEnabledSensors | ShimmerPC.SENSOR_BRIDGE_AMP;
	       	}
	       	if (listOfSensorsShimmer2[9].isSelected()){
	       		mReturnEnabledSensors=mReturnEnabledSensors | ShimmerPC.SENSOR_HEART;
	       	}
		}
		mShimmer.writeEnabledSensors(mReturnEnabledSensors);
		mReturnEnabledSensors=0;
	}
	
	/**
	 * Setting up check boxes of enabled sensor signals on main panel
	 */
	public void setupListOfEnabledSensors() {
		enabledSensorSignals = mShimmer.getListofEnabledChannelSignals();
		numberOfSignals = enabledSensorSignals.length;
		for (int count=0; count<maxTraces; count++) {
			listOfSignals[count].setVisible(false);
			frame.getContentPane().remove(listOfSignals[count]);
			calibratedSignals[count].setVisible(false);
			frame.getContentPane().remove(calibratedSignals[count]);
			calibrated[count]=false;
		}
		chckbxHeartRate.setVisible(false);
		frame.getContentPane().remove(chckbxHeartRate);
		if(numberOfSignals>maxTraces) {	//12 is max number of traces
			numberOfSignals=maxTraces;
		}
		int yLocationForHeartRate=0;
		for (int count=0; count<numberOfSignals; count++) {
			listOfSignals[count] = new JCheckBox(enabledSensorSignals[count], false);
			frame.getContentPane().add(listOfSignals[count]);	
			listOfSignals[count].setBounds(10,220+(20*count),200,15);
			
			calibratedSignals[count] = new JCheckBox("CAL", false);
			frame.getContentPane().add(calibratedSignals[count]);
			calibratedSignals[count].setBounds(207, 220+(20*count),50,15);
			if (listOfSignals[count].getText() == "EXG1 STATUS" || listOfSignals[count].getText() == "EXG2 STATUS") {
				calibratedSignals[count].setEnabled(false);
			} 
			
			yLocationForHeartRate = 220+(20*(count+1)); 
		}
		if (calculateHeartRate)
		{
			chckbxHeartRate.setVisible(true);
			chckbxHeartRate.setBounds(10, yLocationForHeartRate, 200, 15);
			frame.getContentPane().add(chckbxHeartRate);
			SwingUtilities.updateComponentTreeUI(frame);
		}
	}
	
	/**
	 * Detecting which signals are chosen to show on the graph
	 * @return a list of chosen sensor signals
	 */
	private String[] selectSignalsToView() {
		List<String> listofSignals = new ArrayList<String>();
		String[] selectedSignals;
		for(int count=0; count<numberOfSignals; count++) {
			if (listOfSignals[count].isSelected()) {
				listofSignals.add(enabledSensorSignals[count]);
				calibratedCount=listofSignals.size();
				if(calibratedSignals[count].isSelected()) {
					calibrated[calibratedCount-1]=true;
				} else if (!calibratedSignals[count].isSelected()){
					calibrated[calibratedCount-1]=false;
				}
			}	
			listOfSignals[count].addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					if (!(listOfSignals[0].isSelected())) {
						maxDataPoint = -10000;
						minDataPoint = 10000;
					}
				}
			});
			calibratedSignals[count].addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					if (calibratedSignals[0].isSelected()) {
						maxDataPoint = -10000;
						minDataPoint = 10000;
					} else if (!(calibratedSignals[0].isSelected())) {
						maxDataPoint = -10000;
						minDataPoint = 10000;
					}
				}
			});
		}
		selectedSignals = listofSignals.toArray(new String[listofSignals.size()]);
		return selectedSignals;
	}
	
	private void exgConfiguration() {
		byte[] exg1RegisterContents = mShimmer.getEXG1RegisterArray();
		byte[] exg2RegisterContents = mShimmer.getEXG2RegisterArray();
		if (firstExgConfiguration) {
			int[] gain = {1, 2, 3, 4, 6, 8, 12};
			for (int i=0; i<gain.length; i++) {
				comboBoxGainChip1Channel1.addItem(gain[i]);
				comboBoxGainChip1Channel2.addItem(gain[i]);
				comboBoxGainChip2Channel1.addItem(gain[i]);
				comboBoxGainChip2Channel2.addItem(gain[i]);
			}
			
			firstExgConfiguration=false;
		}
		
		int exgGain = (exg1RegisterContents[3] >> 4) & 7;
        //CATHY: cmdEXGGain1.SelectedIndex = ??
        if (exgGain == 0)
        {
        	comboBoxGainChip1Channel1.setSelectedIndex(4);
        }
        else if (exgGain == 1)
        {
        	comboBoxGainChip1Channel1.setSelectedIndex(0);
        }
        else if (exgGain == 2)
        {
        	comboBoxGainChip1Channel1.setSelectedIndex(1);
        }
        else if (exgGain == 3)
        {
        	comboBoxGainChip1Channel1.setSelectedIndex(2);
        }
        else if (exgGain == 4)
        {
        	comboBoxGainChip1Channel1.setSelectedIndex(3);
        }
        else if (exgGain == 5)
        {
        	comboBoxGainChip1Channel1.setSelectedIndex(5);
        }
        else if (exgGain == 6)
        {
        	comboBoxGainChip1Channel1.setSelectedIndex(6);
        }
		
        exgGain = (exg1RegisterContents[4] >> 4) & 7;
        //CATHY: cmdEXGGain1.SelectedIndex = ??
        if (exgGain == 0)
        {
        	comboBoxGainChip1Channel2.setSelectedIndex(4);
        }
        else if (exgGain == 1)
        {
        	comboBoxGainChip1Channel2.setSelectedIndex(0);
        }
        else if (exgGain == 2)
        {
        	comboBoxGainChip1Channel2.setSelectedIndex(1);
        }
        else if (exgGain == 3)
        {
        	comboBoxGainChip1Channel2.setSelectedIndex(2);
        }
        else if (exgGain == 4)
        {
        	comboBoxGainChip1Channel2.setSelectedIndex(3);
        }
        else if (exgGain == 5)
        {
        	comboBoxGainChip1Channel2.setSelectedIndex(5);
        }
        else if (exgGain == 6)
        {
        	comboBoxGainChip1Channel2.setSelectedIndex(6);
        }
		
        exgGain = (exg2RegisterContents[3] >> 4) & 7;
        //CATHY: cmdEXGGain1.SelectedIndex = ??
        if (exgGain == 0)
        {
        	comboBoxGainChip2Channel1.setSelectedIndex(4);
        }
        else if (exgGain == 1)
        {
        	comboBoxGainChip2Channel1.setSelectedIndex(0);
        }
        else if (exgGain == 2)
        {
        	comboBoxGainChip2Channel1.setSelectedIndex(1);
        }
        else if (exgGain == 3)
        {
        	comboBoxGainChip2Channel1.setSelectedIndex(2);
        }
        else if (exgGain == 4)
        {
        	comboBoxGainChip2Channel1.setSelectedIndex(3);
        }
        else if (exgGain == 5)
        {
        	comboBoxGainChip2Channel1.setSelectedIndex(5);
        }
        else if (exgGain == 6)
        {
        	comboBoxGainChip2Channel1.setSelectedIndex(6);
        }
		
        exgGain = (exg2RegisterContents[4] >> 4) & 7;
        //CATHY: cmdEXGGain1.SelectedIndex = ??
        if (exgGain == 0)
        {
        	comboBoxGainChip2Channel2.setSelectedIndex(4);
        }
        else if (exgGain == 1)
        {
        	comboBoxGainChip2Channel2.setSelectedIndex(0);
        }
        else if (exgGain == 2)
        {
        	comboBoxGainChip2Channel2.setSelectedIndex(1);
        }
        else if (exgGain == 3)
        {
        	comboBoxGainChip2Channel2.setSelectedIndex(2);
        }
        else if (exgGain == 4)
        {
        	comboBoxGainChip2Channel2.setSelectedIndex(3);
        }
        else if (exgGain == 5)
        {
        	comboBoxGainChip2Channel2.setSelectedIndex(5);
        }
        else if (exgGain == 6)
        {
        	comboBoxGainChip2Channel2.setSelectedIndex(6);
        }
        
		chckbxHPF0_05.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				if (chckbxHPF0_05.isSelected()) {
					chckbxHPF0_5.setSelected(false);
					chckbxHPF5.setSelected(false);
				}
			}
		});
		chckbxHPF0_5.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				if (chckbxHPF0_5.isSelected()) {
					chckbxHPF0_05.setSelected(false);
					chckbxHPF5.setSelected(false);
				}
			}
		});
		chckbxHPF5.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				if (chckbxHPF5.isSelected()) {
					chckbxHPF0_05.setSelected(false);
					chckbxHPF0_5.setSelected(false);
				}
			}
		});
		chckbxBSF_50.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				if (chckbxBSF_50.isSelected()) {
					chckbxBSF_60.setSelected(false);
				}
			}
		});
		chckbxBSF_60.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				if (chckbxBSF_60.isSelected()) {
					chckbxBSF_50.setSelected(false);
				}
			}
		});
			
		
		for (int i=0; i<exg1RegisterContents.length; i++) {
			int exg1 = exg1RegisterContents[i] & 0xFF;
			exgConfigurationChip1[i]=exg1;
			textFieldChip1[i].setText(Integer.toString(exg1));
		}
		for (int i=0; i<exg2RegisterContents.length; i++) {
			int exg2 = exg2RegisterContents[i] & 0xFF;
			exgConfigurationChip2[i]=exg2;
			textFieldChip2[i].setText(Integer.toString(exg2));
		}
		
		chckbxEcgConfig.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				if (chckbxEcgConfig.isSelected()) {
					chckbxEmgConfig.setSelected(false);
					chckbxTestSignal.setSelected(false);
					exgConfigurationChip1 = new int[]{2, 160, 16, 64, 64, 45, 0, 0, 2, 3};
					exgConfigurationChip2 = new int[]{2, 160, 16, 64, 71, 0, 0, 0, 2, 1};
					for(int i=0; i<textFieldChip1.length; i++) {
						textFieldChip1[i].setText(Integer.toString(exgConfigurationChip1[i]));
					}
					for(int i=0; i<textFieldChip2.length; i++) {
						textFieldChip2[i].setText(Integer.toString(exgConfigurationChip2[i]));
					}
				}
			}
		});
		chckbxEmgConfig.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				if (chckbxEmgConfig.isSelected()) {
					chckbxEcgConfig.setSelected(false);
					chckbxTestSignal.setSelected(false);
					//1) (byte) 2,(byte) 160,(byte) 16,(byte) 105,(byte) 96,(byte) 32,(byte) 0,(byte) 0,(byte) 2,(byte) 3
					//2) (byte) 2,(byte) 160,(byte) 16,(byte) 129,(byte) 129,(byte) 0,(byte) 0,(byte) 0,(byte) 2,(byte) 1
					exgConfigurationChip1 = new int[] {2, 160, 16, 105, 96, 32, 0, 0, 2, 3};
					exgConfigurationChip2 = new int[] {2, 160, 16, 129, 129, 0, 0, 0, 2, 1};
					for(int i=0; i<textFieldChip1.length; i++) {
						textFieldChip1[i].setText(Integer.toString(exgConfigurationChip1[i]));
					}
					for(int i=0; i<textFieldChip2.length; i++) {
						textFieldChip2[i].setText(Integer.toString(exgConfigurationChip2[i]));
					}
				}
			}
		});
		chckbxTestSignal.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				if (chckbxTestSignal.isSelected()) {
					chckbxEcgConfig.setSelected(false);
					chckbxEmgConfig.setSelected(false);
					exgConfigurationChip1 = new int[] {2, 163, 16, 5, 5, 0, 0, 0, 2, 1};
					exgConfigurationChip2 = new int[] {2, 163, 16, 5, 5, 0, 0, 0, 2, 1};
					for(int i=0; i<textFieldChip1.length; i++) {
						textFieldChip1[i].setText(Integer.toString(exgConfigurationChip1[i]));
					}
					for(int i=0; i<textFieldChip2.length; i++) {
						textFieldChip2[i].setText(Integer.toString(exgConfigurationChip2[i]));
					}
				}
			}
		});
		
		comboBoxGainChip1Channel1.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				int exgGain = Integer.parseInt(textFieldChip1[3].getText());
	            exgGain = (exgGain & 143);
	            if (comboBoxGainChip1Channel1.getSelectedIndex() == 0){
	                exgGain = (exgGain | 0x10) ;
	            }
	            else if (comboBoxGainChip1Channel1.getSelectedIndex() == 1)
	            {
	                exgGain = (exgGain | 0x20);
	            }
	            else if (comboBoxGainChip1Channel1.getSelectedIndex() == 2)
	            {
	                exgGain = (exgGain | 0x30);
	            }
	            else if (comboBoxGainChip1Channel1.getSelectedIndex() == 3)
	            {
	                exgGain = (exgGain | 0x40);
	            }
	            else if (comboBoxGainChip1Channel1.getSelectedIndex() == 4)
	            {
	                exgGain = (exgGain | 0x00);
	            }
	            else if (comboBoxGainChip1Channel1.getSelectedIndex() == 5)
	            {
	                exgGain = (exgGain | 0x50);
	            }
	            else if (comboBoxGainChip1Channel1.getSelectedIndex() == 6)
	            {
	                exgGain = (exgGain | 0x60);
	            }
	            textFieldChip1[3].setText(Integer.toString(exgGain));
				
			}
		});
		comboBoxGainChip1Channel2.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				int exgGain = Integer.parseInt(textFieldChip1[4].getText());
	            exgGain = (exgGain & 143);
	            if (comboBoxGainChip1Channel2.getSelectedIndex() == 0){
	                exgGain = (exgGain | 0x10) ;
	            }
	            else if (comboBoxGainChip1Channel2.getSelectedIndex() == 1)
	            {
	                exgGain = (exgGain | 0x20);
	            }
	            else if (comboBoxGainChip1Channel2.getSelectedIndex() == 2)
	            {
	                exgGain = (exgGain | 0x30);
	            }
	            else if (comboBoxGainChip1Channel2.getSelectedIndex() == 3)
	            {
	                exgGain = (exgGain | 0x40);
	            }
	            else if (comboBoxGainChip1Channel2.getSelectedIndex() == 4)
	            {
	                exgGain = (exgGain | 0x00);
	            }
	            else if (comboBoxGainChip1Channel2.getSelectedIndex() == 5)
	            {
	                exgGain = (exgGain | 0x50);
	            }
	            else if (comboBoxGainChip1Channel2.getSelectedIndex() == 6)
	            {
	                exgGain = (exgGain | 0x60);
	            }
	            textFieldChip1[4].setText(Integer.toString(exgGain));
				
			}
		});
		comboBoxGainChip2Channel1.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				int exgGain = Integer.parseInt(textFieldChip2[3].getText());
	            exgGain = (exgGain & 143);
	            if (comboBoxGainChip2Channel1.getSelectedIndex() == 0){
	                exgGain = (exgGain | 0x10) ;
	            }
	            else if (comboBoxGainChip2Channel1.getSelectedIndex() == 1)
	            {
	                exgGain = (exgGain | 0x20);
	            }
	            else if (comboBoxGainChip2Channel1.getSelectedIndex() == 2)
	            {
	                exgGain = (exgGain | 0x30);
	            }
	            else if (comboBoxGainChip2Channel1.getSelectedIndex() == 3)
	            {
	                exgGain = (exgGain | 0x40);
	            }
	            else if (comboBoxGainChip2Channel1.getSelectedIndex() == 4)
	            {
	                exgGain = (exgGain | 0x00);
	            }
	            else if (comboBoxGainChip2Channel1.getSelectedIndex() == 5)
	            {
	                exgGain = (exgGain | 0x50);
	            }
	            else if (comboBoxGainChip2Channel1.getSelectedIndex() == 6)
	            {
	                exgGain = (exgGain | 0x60);
	            }
	            textFieldChip2[3].setText(Integer.toString(exgGain));
				
			}
		});
		comboBoxGainChip2Channel2.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				int exgGain = Integer.parseInt(textFieldChip2[4].getText());
	            exgGain = (exgGain & 143);
	            if (comboBoxGainChip2Channel2.getSelectedIndex() == 0){
	                exgGain = (exgGain | 0x10) ;
	            }
	            else if (comboBoxGainChip2Channel2.getSelectedIndex() == 1)
	            {
	                exgGain = (exgGain | 0x20);
	            }
	            else if (comboBoxGainChip2Channel2.getSelectedIndex() == 2)
	            {
	                exgGain = (exgGain | 0x30);
	            }
	            else if (comboBoxGainChip2Channel2.getSelectedIndex() == 3)
	            {
	                exgGain = (exgGain | 0x40);
	            }
	            else if (comboBoxGainChip2Channel2.getSelectedIndex() == 4)
	            {
	                exgGain = (exgGain | 0x00);
	            }
	            else if (comboBoxGainChip2Channel2.getSelectedIndex() == 5)
	            {
	                exgGain = (exgGain | 0x50);
	            }
	            else if (comboBoxGainChip2Channel2.getSelectedIndex() == 6)
	            {
	                exgGain = (exgGain | 0x60);
	            }
	            textFieldChip2[4].setText(Integer.toString(exgGain));
				
			}
		});
		
	}

	
	/**
	 * Applying EXG Configuration Changes and filter parameters
	 */
	private void exgConfigurationDone() {
		
		int[] chip = new int[3];
		chip[0]=1;
		
		byte[] chip1 = new byte[10];
		byte[] chip2 = new byte[10];
		for (int i=0; i<textFieldChip1.length; i++) {
			chip1[i] = (byte) Integer.parseInt(textFieldChip1[i].getText());
		}
		for (int i=0; i<textFieldChip2.length; i++) {
			chip2[i] = (byte) Integer.parseInt(textFieldChip2[i].getText());
		}
		mShimmer.writeEXGConfiguration(chip1, EXG_CHIP_INDEX.CHIP1);
		mShimmer.writeEXGConfiguration(chip2, EXG_CHIP_INDEX.CHIP2);

		if (chckbxHPF0_05.isSelected()) {
			cornerFrequencyHPF=0.05;
			highPassFilterEnabled=true;
		} else if (chckbxHPF0_5.isSelected()) {
			cornerFrequencyHPF=0.5;
			highPassFilterEnabled=true;
		} else if (chckbxHPF5.isSelected()) {
			cornerFrequencyHPF=5;
			highPassFilterEnabled=true;
		} else {
			cornerFrequencyHPF=-1;
			highPassFilterEnabled=false;
		}
		
		if (chckbxBSF_50.isSelected()) {
			cornerFrequencyBSF1=49;
			cornerFrequencyBSF2=51;
			bandStopFilterEnabled=true;
		} else if (chckbxBSF_60.isSelected()) {
			cornerFrequencyBSF1=59;
			cornerFrequencyBSF2=61;
			bandStopFilterEnabled=true;
		} else {
			cornerFrequencyBSF1=-1;
			cornerFrequencyBSF2=-1;
			bandStopFilterEnabled=false;
		}
		
		// add a delay here for the sensors to take effect
		try {
			Thread.sleep(500);	// Wait to ensure that we dont missed any bytes which need to be cleared
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		setupListOfEnabledSensors();
		exgFrame.setVisible(false);
	}
	
	/**
	 * Removing data
	 */
	private void onDisconnect() {
		firstExgConfiguration=true;
		firstConfiguration=true;
		comboBoxGainChip1Channel1.removeAllItems();
		comboBoxGainChip1Channel2.removeAllItems();
		comboBoxGainChip2Channel1.removeAllItems();
		comboBoxGainChip2Channel2.removeAllItems();
		
		comboBoxSamplingRate.removeAllItems();
		comboBoxAccelRange.removeAllItems();
		comboBoxGsrRange.removeAllItems();
		comboBoxGyroRange.removeAllItems();
		comboBoxMagRange.removeAllItems();
		comboBoxPressureResolution.removeAllItems();
		
		chart.removeAllTraces();
		for (int count=0; count<listofCompatibleSensorsShimmer3.length; count++) {
			configFrame.getContentPane().remove(listOfSensorsShimmer3[count]);
		} 
		for (int count=0; count<listofCompatibleSensorsShimmer2.length; count++) {
			configFrame.getContentPane().remove(listOfSensorsShimmer2[count]);
		}
	}

	@Override
	protected void processMsgFromCallback(ShimmerMsg shimmerMSG) {
		// TODO Auto-generated method stub
		  int ind = shimmerMSG.mIdentifier;

		  Object object = (Object) shimmerMSG.mB;

		if (ind == ShimmerPC.MSG_IDENTIFIER_STATE_CHANGE) {
			CallbackObject callbackObject = (CallbackObject)object;
			
			if (callbackObject.mState == BT_STATE.CONNECTING) {	//Never called
				textFieldState.setText("Shimmer Connecting");
			} else if (callbackObject.mState == BT_STATE.CONNECTED) {
				textFieldState.setText("Shimmer Connected");
				btnConnect.setEnabled(false);
				btnDisconnect.setEnabled(true);
				
			} else if (callbackObject.mState == BT_STATE.DISCONNECTED
//					|| callbackObject.mState == BT_STATE.NONE
					|| callbackObject.mState == BT_STATE.CONNECTION_LOST){
				
				textFieldState.setText("Shimmer Disconnected");
				textFieldMessage.setText("");
				btnDisconnect.setEnabled(false);
				btnConnect.setEnabled(true);
				btnStartStreaming.setEnabled(false);
				btnStopStreaming.setEnabled(false);
				menuItemConfigure.setEnabled(false);
				menuItemExgSettings.setEnabled(false);
				onDisconnect();
			}
		} else if (ind == ShimmerPC.MSG_IDENTIFIER_NOTIFICATION_MESSAGE) {
			CallbackObject callbackObject = (CallbackObject)object;
			int msg = callbackObject.mIndicator;
			if (msg== ShimmerPC.NOTIFICATION_SHIMMER_FULLY_INITIALIZED){
				connected();
			}
			if (msg == ShimmerPC.NOTIFICATION_SHIMMER_STOP_STREAMING) {
				menuItemConfigure.setEnabled(true);
				if (mShimmer.getShimmerVersion()==SHIMMER_3 || mShimmer.getShimmerVersion()==SHIMMER_SR30) {
					menuItemExgSettings.setEnabled(true);
				}
				btnStopStreaming.setEnabled(false);
				btnStartStreaming.setEnabled(true);
			} else if (msg == ShimmerPC.NOTIFICATION_SHIMMER_START_STREAMING) {
				menuItemConfigure.setEnabled(false);
				menuItemExgSettings.setEnabled(false);
				btnStopStreaming.setEnabled(true);
				btnStartStreaming.setEnabled(false);
			} else {	//Ready for Streaming
				setupListOfEnabledSensors();
				btnStartStreaming.setEnabled(true);
			}
		} else if (ind == ShimmerPC.MSG_IDENTIFIER_DATA_PACKET) {
			if (object instanceof ObjectCluster){
			ObjectCluster objc = (ObjectCluster)object;
			String[] exgnames = {"EXG1 CH1","EXG1 CH2","EXG2 CH1","EXG2 CH2","ECG LL-RA","ECG LA-RA","ECG Vx-RL","EMG CH1","EMG CH2","EXG1 CH1 16Bit","EXG1 CH2 16Bit","EXG2 CH1 16Bit","EXG2 CH2 16Bit"};
			//Filter signals
			if (highPassFilterEnabled || bandStopFilterEnabled){
				for (int indexgnames=0;indexgnames<exgnames.length;indexgnames++){
					Collection<FormatCluster> cf = objc.getCollectionOfFormatClusters(exgnames[indexgnames]);
					try {
						if (cf.size()!=0){
							double data =((FormatCluster)ObjectCluster.returnFormatCluster(cf,"CAL")).mData;
							if (exgnames[indexgnames].equals(Shimmer3.ObjectClusterSensorName.EXG1_CH1_24BIT)) {
								if (highPassFilterEnabled){
									data = hpfexg1ch1.filterData(data);
								}
								if (bandStopFilterEnabled){
									data = bsfexg1ch1.filterData(data);
								}
								((FormatCluster)ObjectCluster.returnFormatCluster(cf,"CAL")).mData = data;
								exg1Ch1Data=data;
							} else if (exgnames[indexgnames].equals(Shimmer3.ObjectClusterSensorName.EXG1_CH2_24BIT)) {
								if (highPassFilterEnabled){
									data = hpfexg1ch2.filterData(data);
								}
								if (bandStopFilterEnabled){
									data = bsfexg1ch2.filterData(data);
								}
								((FormatCluster)ObjectCluster.returnFormatCluster(cf,"CAL")).mData = data;
								exg1Ch2Data=data;
							} else if (exgnames[indexgnames].equals(Shimmer3.ObjectClusterSensorName.EXG2_CH1_24BIT)) {
								if (highPassFilterEnabled){
									data = hpfexg2ch1.filterData(data);
								}
								if (bandStopFilterEnabled){
									data = bsfexg2ch1.filterData(data);
								}
								((FormatCluster)ObjectCluster.returnFormatCluster(cf,"CAL")).mData = data;
								exg2Ch1Data=data;
							} else if (exgnames[indexgnames].equals(Shimmer3.ObjectClusterSensorName.EXG2_CH2_24BIT)) {
								if (highPassFilterEnabled){
									data = hpfexg2ch2.filterData(data);
								}
								if (bandStopFilterEnabled){
									data = bsfexg2ch2.filterData(data);
								}
								((FormatCluster)ObjectCluster.returnFormatCluster(cf,"CAL")).mData = data;
								exg2Ch2Data=data;
							} else if (exgnames[indexgnames].equals(Shimmer3.ObjectClusterSensorName.ECG_LL_RA_24BIT)) {
								if (highPassFilterEnabled){
									data = hpfexg1ch1.filterData(data); 
								}
								if (bandStopFilterEnabled){
									data = bsfexg1ch1.filterData(data);
								}
								((FormatCluster)ObjectCluster.returnFormatCluster(cf,"CAL")).mData = data;
								exg1Ch1Data=data;
							} else if (exgnames[indexgnames].equals(Shimmer3.ObjectClusterSensorName.ECG_LA_RA_24BIT)) {
								if (highPassFilterEnabled){
									data = hpfexg1ch2.filterData(data);
								}
								if (bandStopFilterEnabled){
									data = bsfexg1ch2.filterData(data);
								}
								((FormatCluster)ObjectCluster.returnFormatCluster(cf,"CAL")).mData = data;
								exg1Ch2Data=data;
							} else if (exgnames[indexgnames].equals(Shimmer3.ObjectClusterSensorName.ECG_VX_RL_24BIT)) {
								if (highPassFilterEnabled){
									data = hpfexg2ch2.filterData(data);
								}
								if (bandStopFilterEnabled){
									data = bsfexg2ch2.filterData(data);
								}
								((FormatCluster)ObjectCluster.returnFormatCluster(cf,"CAL")).mData = data;
								exg2Ch2Data=data;
							} else if (exgnames[indexgnames].equals(Shimmer3.ObjectClusterSensorName.EMG_CH1_24BIT)) {
								if (highPassFilterEnabled){
									data = hpfexg1ch1.filterData(data); 
								}
								if (bandStopFilterEnabled){
									data = bsfexg1ch1.filterData(data);
								}
								((FormatCluster)ObjectCluster.returnFormatCluster(cf,"CAL")).mData = data;
								exg1Ch1Data=data;
							} else if (exgnames[indexgnames].equals(Shimmer3.ObjectClusterSensorName.EMG_CH2_24BIT)) {
								if (highPassFilterEnabled){
									data = hpfexg1ch2.filterData(data);
								}
								if (bandStopFilterEnabled){
									data = bsfexg1ch2.filterData(data);
								}
								((FormatCluster)ObjectCluster.returnFormatCluster(cf,"CAL")).mData = data;
								exg1Ch2Data=data;
							} else if (exgnames[indexgnames].equals(Shimmer3.ObjectClusterSensorName.EXG1_CH1_16BIT)) {
								if (highPassFilterEnabled){
									data = hpfexg1ch1.filterData(data);
								}
								if (bandStopFilterEnabled){
									data = bsfexg1ch1.filterData(data);
								}
								((FormatCluster)ObjectCluster.returnFormatCluster(cf,"CAL")).mData = data;
								exg1Ch1Data=data;
							} else if (exgnames[indexgnames].equals(Shimmer3.ObjectClusterSensorName.EXG1_CH2_16BIT)) {
								if (highPassFilterEnabled){
									data = hpfexg1ch2.filterData(data);
								}
								if (bandStopFilterEnabled){
									data = bsfexg1ch2.filterData(data);
								}
								((FormatCluster)ObjectCluster.returnFormatCluster(cf,"CAL")).mData = data;
								exg1Ch2Data=data;
							} else if (exgnames[indexgnames].equals(Shimmer3.ObjectClusterSensorName.EXG2_CH1_16BIT)) {
								if (highPassFilterEnabled){
									data = hpfexg2ch1.filterData(data);
								}
								if (bandStopFilterEnabled){
									data = bsfexg2ch1.filterData(data);
								}
								((FormatCluster)ObjectCluster.returnFormatCluster(cf,"CAL")).mData = data;
								exg2Ch1Data=data;
							} else if (exgnames[indexgnames].equals(Shimmer3.ObjectClusterSensorName.EXG2_CH2_16BIT)) {
								if (highPassFilterEnabled){
									data = hpfexg2ch2.filterData(data);
								}
								if (bandStopFilterEnabled){
									data = bsfexg2ch2.filterData(data);
								}
								((FormatCluster)ObjectCluster.returnFormatCluster(cf,"CAL")).mData = data;
								exg2Ch2Data=data;
							} else if (exgnames[indexgnames].equals(Shimmer3.ObjectClusterSensorName.ECG_LL_RA_16BIT)) {
								if (highPassFilterEnabled){
									data = hpfexg1ch1.filterData(data); 
								}
								if (bandStopFilterEnabled){
									data = bsfexg1ch1.filterData(data);
								}
								((FormatCluster)ObjectCluster.returnFormatCluster(cf,"CAL")).mData = data;
								exg1Ch1Data=data;
							} else if (exgnames[indexgnames].equals(Shimmer3.ObjectClusterSensorName.ECG_LA_RA_16BIT)) {
								if (highPassFilterEnabled){
									data = hpfexg1ch2.filterData(data);
								}
								if (bandStopFilterEnabled){
									data = bsfexg1ch2.filterData(data);
								}
								((FormatCluster)ObjectCluster.returnFormatCluster(cf,"CAL")).mData = data;
								exg1Ch2Data=data;
							} else if (exgnames[indexgnames].equals(Shimmer3.ObjectClusterSensorName.ECG_VX_RL_16BIT)) {
								if (highPassFilterEnabled){
									data = hpfexg2ch2.filterData(data);
								}
								if (bandStopFilterEnabled){
									data = bsfexg2ch2.filterData(data);
								}
								((FormatCluster)ObjectCluster.returnFormatCluster(cf,"CAL")).mData = data;
								exg2Ch2Data=data;
							} else if (exgnames[indexgnames].equals(Shimmer3.ObjectClusterSensorName.EMG_CH1_16BIT)) {
								if (highPassFilterEnabled){
									data = hpfexg1ch1.filterData(data); 
								}
								if (bandStopFilterEnabled){
									data = bsfexg1ch1.filterData(data);
								}
								((FormatCluster)ObjectCluster.returnFormatCluster(cf,"CAL")).mData = data;
								exg1Ch1Data=data;
							} else if (exgnames[indexgnames].equals(Shimmer3.ObjectClusterSensorName.EMG_CH2_16BIT)) {
								if (highPassFilterEnabled){
									data = hpfexg1ch2.filterData(data);
								}
								if (bandStopFilterEnabled){
									data = bsfexg1ch2.filterData(data);
								}
								((FormatCluster)ObjectCluster.returnFormatCluster(cf,"CAL")).mData = data;
								exg1Ch2Data=data;
							}
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}
			
			String[] selectedSensorSignals = selectSignalsToView();
			int numberOfSelectedSignals = selectedSensorSignals.length;
			if (numberOfSelectedSignals>maxTraces) {
				numberOfSelectedSignals=maxTraces;
			}
			chart.removeAllTraces();
			double dataArrayPPG = 0;
			double dataArrayECG = 0;
			double heartRate = Double.NaN;

			if (calculateHeartRate && chckbxEnablePPGtoHR.isSelected()) {
				Collection<FormatCluster> adcFormats = objc.getCollectionOfFormatClusters(Shimmer3.ObjectClusterSensorName.INT_EXP_ADC_A13);
				FormatCluster format = ((FormatCluster)ObjectCluster.returnFormatCluster(adcFormats,"CAL")); // retrieve the calibrated data
				dataArrayPPG = format.mData;
				try {
					dataArrayPPG = lpf.filterData(dataArrayPPG);
					dataArrayPPG = hpf.filterData(dataArrayPPG);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				Collection<FormatCluster> formatTS = objc.getCollectionOfFormatClusters(Shimmer3.ObjectClusterSensorName.TIMESTAMP);
				FormatCluster ts = ObjectCluster.returnFormatCluster(formatTS,"CAL");
				double ppgTimeStamp = ts.mData;
				heartRate = heartRateCalculation.ppgToHrConversion(dataArrayPPG, ppgTimeStamp);
				if (heartRate == INVALID_RESULT){
					heartRate = Double.NaN;
				}
				objc.addDataToMap("Heart Rate","CAL","beats per minute",heartRate);
				if (chckbxHeartRate.isSelected()) {
					chart.addTrace(traceHR);
				}
			}
			
			if (calculateHeartRate && chckbxEnableECGtoHR.isSelected()) {
				Collection<FormatCluster> adcFormats = objc.getCollectionOfFormatClusters(Shimmer3.ObjectClusterSensorName.ECG_LA_RA_24BIT);
				FormatCluster format = ((FormatCluster)ObjectCluster.returnFormatCluster(adcFormats,"CAL")); // retrieve the calibrated data
				dataArrayECG = format.mData;
				try {
					//dataArrayECG = lpfECG.filterData(dataArrayECG);
					//dataArrayECG = hpfECG.filterData(dataArrayECG);
					format.mData = dataArrayECG;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				Collection<FormatCluster> formatTS = objc.getCollectionOfFormatClusters(Shimmer3.ObjectClusterSensorName.TIMESTAMP);
				FormatCluster ts = ObjectCluster.returnFormatCluster(formatTS,"CAL");
				double ecgTimeStamp = ts.mData;
				heartRate = heartRateCalculationECG.ecgToHrConversion(dataArrayECG, ecgTimeStamp);
				if (heartRate == INVALID_RESULT){
					heartRate = Double.NaN;
				} else {
					//System.out.println("Heart Rate: " + heartRate);
				}
				objc.addDataToMap("Heart Rate","CAL","beats per minute",heartRate);
				if (chckbxHeartRate.isSelected()) {
					chart.addTrace(traceHR);
				}
			}

			
			if (numberOfSelectedSignals > 0  || calculateHeartRate) {
				
				
				
				Collection<FormatCluster> formats[] = new Collection[numberOfSelectedSignals];
				FormatCluster cal[] = new FormatCluster[numberOfSelectedSignals];
				double[] dataArray = new double[numberOfSelectedSignals];
				for (int count=0; count<numberOfSelectedSignals; count++) {
					chart.addTrace(traces[count]);
					traces[count].setVisible(true);
					traces[count].setName(selectedSensorSignals[count]);
					formats[count] = objc.getCollectionOfFormatClusters(selectedSensorSignals[count]);
					cal[count] = ((FormatCluster)ObjectCluster.returnFormatCluster(formats[count],"CAL"));
					if (cal[count]!=null) {
						if (calibrated[count]) {
							dataArray[count] = ((FormatCluster)ObjectCluster.returnFormatCluster(formats[count],"CAL")).mData;
						} else {
							dataArray[count] = ((FormatCluster)ObjectCluster.returnFormatCluster(formats[count],CHANNEL_TYPE.UNCAL.toString())).mData;
						}
					}
				}
				
				//Plotting data
				downSample++;

				int numberOfTraces = dataArray.length;
				for (int i=0; i<numberOfTraces; i++){
					float newX = mLastX + mSpeed;
					if (chckbxHeartRate.isSelected()){
						traceHR.addPoint(newX, heartRate);
					}
					for (int count=0; count<numberOfTraces; count++) {
						traces[count].addPoint(newX, dataArray[count]);
						if (count==0) {
							mLastX += mSpeed;
						}
					}
				}
				if (numberOfTraces == 0 && chckbxHeartRate.isSelected()){
					float newX = mLastX + mSpeed;
					traceHR.addPoint(newX, heartRate);
					mLastX += mSpeed;
					minDataPoint=-5;
					maxDataPoint=215;
					Range range = new Range(minDataPoint, maxDataPoint);
					IRangePolicy rangePolicy = new RangePolicyFixedViewport(range);
					yAxis.setRangePolicy(rangePolicy);
				} else {
					//Scaling Y Axis
					for (int count=0; count<numberOfTraces; count++){
						if (dataArray[count] > maxDataPoint) {
							maxDataPoint = (int) Math.ceil(dataArray[count]);
						}
						if (heartRate > maxDataPoint){
							maxDataPoint = (int) Math.ceil(heartRate);
						}
						if (dataArray[count] < minDataPoint) {
							minDataPoint = (int) Math.floor(dataArray[count]);
						}
						if (heartRate < minDataPoint) {
							minDataPoint = (int) Math.floor(heartRate);
						}
					}
					Range range = new Range(minDataPoint, maxDataPoint);
					IRangePolicy rangePolicy = new RangePolicyFixedViewport(range);
					yAxis.setRangePolicy(rangePolicy);
				}

			}
			
			if (returnVal == JFileChooser.APPROVE_OPTION && loggingData) {
					log.logData(objc);
			}
			}
			
		} else if (ind == ShimmerPC.MSG_IDENTIFIER_PACKET_RECEPTION_RATE_OVERALL) {
			CallbackObject callbackObject = (CallbackObject)object;
			double packetReceptionRate = callbackObject.mPacketReceptionRate;

//			double packetReceptionRate = (Double) object;
			if(downSample%50==0){
				textFieldMessage.setText("Packet Reception Rate: " + Double.toString(packetReceptionRate));
			}
		}
	
	}
}

