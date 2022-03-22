package com.shimmerresearch.simpleexamples;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;

import com.shimmerresearch.algorithms.Filter;
import com.shimmerresearch.bluetooth.ShimmerBluetooth.BT_STATE;
import com.shimmerresearch.driver.BasicProcessWithCallBack;
import com.shimmerresearch.driver.CallbackObject;
import com.shimmerresearch.driver.Configuration;
import com.shimmerresearch.driver.ObjectCluster;
import com.shimmerresearch.driver.ShimmerMsg;
import com.shimmerresearch.driver.Configuration.COMMUNICATION_TYPE;
import com.shimmerresearch.driverUtilities.AssembleShimmerConfig;
import com.shimmerresearch.driverUtilities.ChannelDetails;
import com.shimmerresearch.driverUtilities.ChannelDetails.CHANNEL_TYPE;
import com.shimmerresearch.driverUtilities.SensorDetails;
import com.shimmerresearch.guiUtilities.configuration.EnableSensorsDialog;
import com.shimmerresearch.guiUtilities.configuration.SensorConfigDialog;
import com.shimmerresearch.guiUtilities.configuration.SignalsToPlotDialog;
import com.shimmerresearch.guiUtilities.plot.BasicPlotManagerPC;
import com.shimmerresearch.pcDriver.ShimmerPC;
import com.shimmerresearch.tools.bluetooth.BasicShimmerBluetoothManagerPc;

import info.monitorenter.gui.chart.Chart2D;
import uk.me.berndporr.iirj.Butterworth;

public class EMGExample extends BasicProcessWithCallBack {
	static Butterworth butterworth = new Butterworth();
	static Filter mFilterHPF_159;
	static ShimmerPC shimmer = new ShimmerPC("ShimmerDevice");
	static BasicShimmerBluetoothManagerPc btManager = new BasicShimmerBluetoothManagerPc();
	static BasicPlotManagerPC plotManager = new BasicPlotManagerPC();
	
	private Chart2D mChart;
	private boolean mConfigureOnFirstTime = true;
	private JFrame frame;
	private JTextField textFieldComPort;
	private JTextPane textPaneStatus;
	private String btComport;
	
	public static void main(String[] args) {
		EMGExample s = new EMGExample();
		s.initialize();
		s.frame.setVisible(true);
		s.setWaitForData(btManager.callBackObject);		
		//s.setWaitForData(shimmer);
		
		//example usage when data is received, where x is data
		//x = butterworth0_5.filter(x);
		//x = mFilterHPF_159.filterData(x);
	}
	
	public void initializeFilter() {
		double[] mHPFc159 = {159};
		try {
			mFilterHPF_159 = new Filter(Filter.HIGH_PASS, 1024, mHPFc159);
		} catch (Exception e) {
			e.printStackTrace();
		}
		butterworth.highPass(4, 1024, 0.5);
	}
	
	public void initialize() {
		frame = new JFrame("Shimmer EMG Example");
		frame.setBounds(100, 100, 662, 591);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JLabel lblSetComPort = new JLabel("Set COM Port");
		lblSetComPort.setBounds(10, 60, 119, 23);
		frame.getContentPane().add(lblSetComPort);
		
		textFieldComPort = new JTextField();
		textFieldComPort.setToolTipText("for example COM1, COM2, etc");
		textFieldComPort.setBounds(10, 91, 144, 29);
		frame.getContentPane().add(textFieldComPort);
		textFieldComPort.setColumns(10);
		
		JButton btnConnect = new JButton("CONNECT");
		btnConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				btComport = textFieldComPort.getText();
				btManager.connectShimmerThroughCommPort(btComport);
				
			}
		});
		btnConnect.setToolTipText("attempt connection to Shimmer device");
		btnConnect.setBounds(185, 90, 199, 31);
		frame.getContentPane().add(btnConnect);
		
		JButton btnDisconnect = new JButton("DISCONNECT");
		btnDisconnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				btManager.disconnectShimmer(shimmer);
				
			}
		});
		btnDisconnect.setToolTipText("disconnect from Shimmer device");
		btnDisconnect.setBounds(415, 90, 187, 31);
		frame.getContentPane().add(btnDisconnect);
		
		JLabel lblShimmerStatus = new JLabel("Shimmer Status");
		lblShimmerStatus.setBounds(10, 139, 144, 23);
		frame.getContentPane().add(lblShimmerStatus);
		
		textPaneStatus = new JTextPane();
		textPaneStatus.setBounds(10, 181, 144, 36);
		frame.getContentPane().add(textPaneStatus);
		
		JMenuBar menuBar = new JMenuBar();
		menuBar.setBounds(0, 0, 638, 23);
		frame.getContentPane().add(menuBar);
		
		JMenu mnTools = new JMenu("Tools");
		menuBar.add(mnTools);
		
		JMenuItem mntmSelectSensors = new JMenuItem("Select sensors");
		mntmSelectSensors.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				//Ensure the Shimmer is not streaming or SD logging before configuring it
				if(shimmer.isConnected()) {
					if(!shimmer.isStreaming() && !shimmer.isSDLogging()) {
						EnableSensorsDialog sensorsDialog = new EnableSensorsDialog(shimmer, btManager);
						sensorsDialog.showDialog();
					} else {
						JOptionPane.showMessageDialog(frame, "Cannot configure sensors!\nDevice is streaming or SDLogging", "Warning", JOptionPane.WARNING_MESSAGE);
					}
				} else {
					JOptionPane.showMessageDialog(frame, "No device connected!", "Info", JOptionPane.WARNING_MESSAGE);
				}
				
//				EnableSensorsDialog sensorsDialog = new EnableSensorsDialog(shimmerDevice);
//				sensorsDialog.initialize();
			}
		});
		mnTools.add(mntmSelectSensors);
		
		JMenuItem mntmDeviceConfiguration = new JMenuItem("Sensor configuration");
		mntmDeviceConfiguration.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				if(shimmer.isConnected()) {
					if(!shimmer.isStreaming() && !shimmer.isSDLogging()) {
						SensorConfigDialog configDialog = new SensorConfigDialog(shimmer,btManager);
						configDialog.showDialog();
					} else {
						JOptionPane.showMessageDialog(frame, "Cannot configure sensors!\nDevice is streaming or SDLogging", "Warning", JOptionPane.WARNING_MESSAGE);
					}
				} else {
					JOptionPane.showMessageDialog(frame, "No device connected!", "Info", JOptionPane.WARNING_MESSAGE);
				}
				
			}
		});
		mnTools.add(mntmDeviceConfiguration);
		
		JPanel plotPanel = new JPanel();
		plotPanel.setBounds(10, 236, 611, 272);
		frame.getContentPane().add(plotPanel);
		plotPanel.setLayout(null);
		
		mChart = new Chart2D();
		mChart.setLocation(12, 13);
		mChart.setSize(587, 246);
		plotPanel.add(mChart);
		plotManager.addChart(mChart);
		
		JMenuItem mntmSignalsToPlot = new JMenuItem("Signals to plot");
		mntmSignalsToPlot.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				SignalsToPlotDialog signalsToPlotDialog = new SignalsToPlotDialog();
				signalsToPlotDialog.initialize(shimmer, plotManager, mChart);
				
			}
		});
		mnTools.add(mntmSignalsToPlot);
		
		JButton btnStartStreaming = new JButton("START STREAMING");
		btnStartStreaming.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				try {
					shimmer.startStreaming();
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		});
		btnStartStreaming.setBounds(185, 181, 199, 31);
		frame.getContentPane().add(btnStartStreaming);
		
		JButton btnStopStreaming = new JButton("STOP STREAMING");
		btnStopStreaming.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				shimmer.stopStreaming();
				
			}
		});
		btnStopStreaming.setBounds(415, 181, 187, 31);
		frame.getContentPane().add(btnStopStreaming);
		
		plotManager.setTitle("Plot");
		
		initializeFilter();
	}

	@Override
	protected void processMsgFromCallback(ShimmerMsg shimmerMSG) {
		  int ind = shimmerMSG.mIdentifier;
		  Object object = (Object) shimmerMSG.mB;

		if (ind == ShimmerPC.MSG_IDENTIFIER_STATE_CHANGE) {
			CallbackObject callbackObject = (CallbackObject)object;
			
			if (callbackObject.mState == BT_STATE.CONNECTING) {
				textPaneStatus.setText("connecting...");
			} else if (callbackObject.mState == BT_STATE.CONNECTED) {
				textPaneStatus.setText("connected");
				shimmer = (ShimmerPC) btManager.getShimmerDeviceBtConnected(btComport);
				
				if (mConfigureOnFirstTime){
					ShimmerPC cloneDevice = shimmer.deepClone();
					cloneDevice.setEnabledAndDerivedSensorsAndUpdateMaps(0, 0);
					cloneDevice.setSensorEnabledState(Configuration.Shimmer3.SENSOR_ID.HOST_EMG, true);
					shimmer.enableDefaultEMGConfiguration();
					AssembleShimmerConfig.generateSingleShimmerConfig(cloneDevice, COMMUNICATION_TYPE.BLUETOOTH);
					btManager.configureShimmer(cloneDevice);
			 		shimmer.writeShimmerAndSensorsSamplingRate(1024);
				
					String[] signal1 = {"test", "EMG_CH1_Filtered", "CAL", "mV"};
					String[] signal2 = {"test", "EMG_CH2_Filtered", "CAL", "mV"};
					String[] xAxis = {"test", Configuration.Shimmer3.ObjectClusterSensorName.SYSTEM_TIMESTAMP_PLOT, CHANNEL_TYPE.CAL.toString()};
					try {
						plotManager.addSignal(signal1, mChart);
						plotManager.addSignal(signal2, mChart);
						plotManager.addXAxis(xAxis);
						plotManager.setYAxisRange(-2, 2);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					mConfigureOnFirstTime = false;
				}
				
			} else if (callbackObject.mState == BT_STATE.DISCONNECTED
//					|| callbackObject.mState == BT_STATE.NONE
					|| callbackObject.mState == BT_STATE.CONNECTION_LOST){
				textPaneStatus.setText("disconnected");				
			}
		} else if (ind == ShimmerPC.MSG_IDENTIFIER_NOTIFICATION_MESSAGE) {
			CallbackObject callbackObject = (CallbackObject)object;
			int msg = callbackObject.mIndicator;
			if (msg== ShimmerPC.NOTIFICATION_SHIMMER_FULLY_INITIALIZED){
				
				textPaneStatus.setText("device fully initialized");
			}
			if (msg == ShimmerPC.NOTIFICATION_SHIMMER_STOP_STREAMING) {
				textPaneStatus.setText("device stopped streaming");
			} else if (msg == ShimmerPC.NOTIFICATION_SHIMMER_START_STREAMING) {
				textPaneStatus.setText("device streaming");
			} else {}
		} else if (ind == ShimmerPC.MSG_IDENTIFIER_DATA_PACKET) {
			System.out.println("Shimmer MSG_IDENTIFIER_DATA_PACKET");
			ObjectCluster objc = (ObjectCluster) shimmerMSG.mB;
			
			try {
				double ch1 = objc.mCalData[2];
				ch1 = butterworth.filter(ch1);
				ch1 = mFilterHPF_159.filterData(ch1);
				objc.addDataToMap("EMG_CH1_Filtered", "CAL", "mV", ch1, false);
				
//				double ch2 = objc.mCalData[3];
//				ch2 = butterworth.filter(ch2);
//				ch2 = mFilterHPF_159.filterData(ch2);
//				objc.addDataToMap("EMG_CH2_Filtered", "CAL", "mV", ch2, false);
				
				plotManager.filterDataAndPlot(objc);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		} else if (ind == ShimmerPC.MSG_IDENTIFIER_PACKET_RECEPTION_RATE_OVERALL) {
			
		}
	}
}
