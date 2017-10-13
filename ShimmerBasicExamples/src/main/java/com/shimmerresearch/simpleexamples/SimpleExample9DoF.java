package com.shimmerresearch.simpleexamples;

import java.awt.EventQueue;

import javax.swing.JFrame;

import com.shimmerresearch.bluetooth.ShimmerBluetooth;
import com.shimmerresearch.bluetooth.ShimmerBluetooth.BT_STATE;
import com.shimmerresearch.driver.BasicProcessWithCallBack;
import com.shimmerresearch.driver.Configuration.Shimmer3;
import com.shimmerresearch.driver.CallbackObject;
import com.shimmerresearch.driver.FormatCluster;
import com.shimmerresearch.driver.ObjectCluster;
import com.shimmerresearch.driver.ShimmerMsg;
import com.shimmerresearch.driver.ShimmerObject;
import com.shimmerresearch.pcDriver.ShimmerPC;

import javax.swing.JButton;
import javax.vecmath.Matrix3d;
import javax.vecmath.Quat4d;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.Collection;

import javax.swing.JLabel;
import javax.swing.JTextField;

public class SimpleExample9DoF extends BasicProcessWithCallBack {

	private JFrame frame;
	ShimmerPC mShimmer = new ShimmerPC("ShimmerDevice", true);
	ShimmerPC mShimmer2 = new ShimmerPC("ShimmerDevice2", true);
	private JTextField textFieldQW;
	private JTextField textFieldQX;
	private JTextField textFieldQY;
	private JTextField textFieldQZ;
	private boolean mFirstTime = true;
	private boolean mFirstTime2 = true;
	private JTextField textFieldm20;
	private JTextField textFieldm10;
	private JTextField textFieldm00;
	private JTextField textFieldm01;
	private JTextField textFieldm11;
	private JTextField textFieldm21;
	private JTextField textFieldm02;
	private JTextField textFieldm12;
	private JTextField textFieldm22;
	private JTextField textField2QW;
	private JTextField textField2QX;
	private JTextField textField2QY;
	private JTextField textField2QZ;
	private JTextField textField2m22;
	private JTextField textField2m12;
	private JTextField textField2m02;
	private JTextField textField2m01;
	private JTextField textField2m11;
	private JTextField textField2m21;
	private JTextField textField2m20;
	private JTextField textField2m10;
	private JTextField textField2m00;
	private JLabel label;
	private JLabel label_1;
	private JLabel label_2;
	private JLabel label_3;
	private JTextField textField_13;
	private JTextField textField_14;
	private JTextField textField_15;
	private JLabel lblQuaternionDev;
	private JLabel lblQuaternionDev_1;
	private JLabel lblRotMatrixDev;
	private JLabel lblRotMatrixDev_1;
	Matrix3d m3d1 = new Matrix3d();
	Matrix3d m3d2 = new Matrix3d();
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SimpleExample9DoF window = new SimpleExample9DoF();
					window.frame.setVisible(true);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		});
	}

	/**
	 * Create the application.
	 */
	public SimpleExample9DoF() {
		initialize();
		frame.getContentPane().setLayout(null);
		setWaitForData(mShimmer);
		setWaitForData(mShimmer2);
		JButton btnNewButton = new JButton("Start Streaming");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				mShimmer.startStreaming();
				mShimmer2.startStreaming();
			}
		});
		btnNewButton.setBounds(10, 11, 107, 23);
		frame.getContentPane().add(btnNewButton);
		
		JLabel lblW = new JLabel("w");
		lblW.setBounds(138, 51, 46, 14);
		frame.getContentPane().add(lblW);
		
		JLabel lblX = new JLabel("x");
		lblX.setBounds(138, 76, 46, 14);
		frame.getContentPane().add(lblX);
		
		JLabel lblY = new JLabel("y");
		lblY.setBounds(138, 101, 46, 14);
		frame.getContentPane().add(lblY);
		
		JLabel lblZ = new JLabel("z");
		lblZ.setBounds(138, 126, 46, 14);
		frame.getContentPane().add(lblZ);
		
		textFieldQW = new JTextField();
		textFieldQW.setBounds(158, 48, 86, 20);
		frame.getContentPane().add(textFieldQW);
		textFieldQW.setColumns(10);
		
		textFieldQX = new JTextField();
		textFieldQX.setColumns(10);
		textFieldQX.setBounds(158, 73, 86, 20);
		frame.getContentPane().add(textFieldQX);
		
		textFieldQY = new JTextField();
		textFieldQY.setColumns(10);
		textFieldQY.setBounds(158, 98, 86, 20);
		frame.getContentPane().add(textFieldQY);
		
		textFieldQZ = new JTextField();
		textFieldQZ.setColumns(10);
		textFieldQZ.setBounds(158, 126, 86, 20);
		frame.getContentPane().add(textFieldQZ);
		
		textFieldm20 = new JTextField();
		textFieldm20.setColumns(10);
		textFieldm20.setBounds(158, 222, 86, 20);
		frame.getContentPane().add(textFieldm20);
		
		textFieldm10 = new JTextField();
		textFieldm10.setColumns(10);
		textFieldm10.setBounds(158, 194, 86, 20);
		frame.getContentPane().add(textFieldm10);
		
		textFieldm00 = new JTextField();
		textFieldm00.setColumns(10);
		textFieldm00.setBounds(158, 169, 86, 20);
		frame.getContentPane().add(textFieldm00);
		
		textFieldm01 = new JTextField();
		textFieldm01.setColumns(10);
		textFieldm01.setBounds(273, 169, 86, 20);
		frame.getContentPane().add(textFieldm01);
		
		textFieldm11 = new JTextField();
		textFieldm11.setColumns(10);
		textFieldm11.setBounds(273, 194, 86, 20);
		frame.getContentPane().add(textFieldm11);
		
		textFieldm21 = new JTextField();
		textFieldm21.setColumns(10);
		textFieldm21.setBounds(273, 222, 86, 20);
		frame.getContentPane().add(textFieldm21);
		
		textFieldm02 = new JTextField();
		textFieldm02.setColumns(10);
		textFieldm02.setBounds(384, 169, 86, 20);
		frame.getContentPane().add(textFieldm02);
		
		textFieldm12 = new JTextField();
		textFieldm12.setColumns(10);
		textFieldm12.setBounds(384, 194, 86, 20);
		frame.getContentPane().add(textFieldm12);
		
		textFieldm22 = new JTextField();
		textFieldm22.setColumns(10);
		textFieldm22.setBounds(384, 222, 86, 20);
		frame.getContentPane().add(textFieldm22);
		
		textField2QW = new JTextField();
		textField2QW.setColumns(10);
		textField2QW.setBounds(516, 45, 86, 20);
		frame.getContentPane().add(textField2QW);
		
		textField2QX = new JTextField();
		textField2QX.setColumns(10);
		textField2QX.setBounds(516, 73, 86, 20);
		frame.getContentPane().add(textField2QX);
		
		textField2QY = new JTextField();
		textField2QY.setColumns(10);
		textField2QY.setBounds(516, 98, 86, 20);
		frame.getContentPane().add(textField2QY);
		
		textField2QZ = new JTextField();
		textField2QZ.setColumns(10);
		textField2QZ.setBounds(516, 126, 86, 20);
		frame.getContentPane().add(textField2QZ);
		
		textField2m22 = new JTextField();
		textField2m22.setColumns(10);
		textField2m22.setBounds(741, 222, 86, 20);
		frame.getContentPane().add(textField2m22);
		
		textField2m12 = new JTextField();
		textField2m12.setColumns(10);
		textField2m12.setBounds(741, 194, 86, 20);
		frame.getContentPane().add(textField2m12);
		
		textField2m02 = new JTextField();
		textField2m02.setColumns(10);
		textField2m02.setBounds(741, 169, 86, 20);
		frame.getContentPane().add(textField2m02);
		
		textField2m01 = new JTextField();
		textField2m01.setColumns(10);
		textField2m01.setBounds(630, 169, 86, 20);
		frame.getContentPane().add(textField2m01);
		
		textField2m11 = new JTextField();
		textField2m11.setColumns(10);
		textField2m11.setBounds(630, 194, 86, 20);
		frame.getContentPane().add(textField2m11);
		
		textField2m21 = new JTextField();
		textField2m21.setColumns(10);
		textField2m21.setBounds(630, 222, 86, 20);
		frame.getContentPane().add(textField2m21);
		
		textField2m20 = new JTextField();
		textField2m20.setColumns(10);
		textField2m20.setBounds(515, 222, 86, 20);
		frame.getContentPane().add(textField2m20);
		
		textField2m10 = new JTextField();
		textField2m10.setColumns(10);
		textField2m10.setBounds(515, 194, 86, 20);
		frame.getContentPane().add(textField2m10);
		
		textField2m00 = new JTextField();
		textField2m00.setColumns(10);
		textField2m00.setBounds(515, 169, 86, 20);
		frame.getContentPane().add(textField2m00);
		
		label = new JLabel("w");
		label.setBounds(500, 48, 46, 14);
		frame.getContentPane().add(label);
		
		label_1 = new JLabel("x");
		label_1.setBounds(500, 73, 46, 14);
		frame.getContentPane().add(label_1);
		
		label_2 = new JLabel("y");
		label_2.setBounds(500, 98, 46, 14);
		frame.getContentPane().add(label_2);
		
		label_3 = new JLabel("z");
		label_3.setBounds(500, 123, 46, 14);
		frame.getContentPane().add(label_3);
		
		textField_13 = new JTextField();
		textField_13.setColumns(10);
		textField_13.setBounds(576, 339, 86, 20);
		frame.getContentPane().add(textField_13);
		
		textField_14 = new JTextField();
		textField_14.setColumns(10);
		textField_14.setBounds(465, 339, 86, 20);
		frame.getContentPane().add(textField_14);
		
		textField_15 = new JTextField();
		textField_15.setColumns(10);
		textField_15.setBounds(350, 339, 86, 20);
		frame.getContentPane().add(textField_15);
		
		lblQuaternionDev = new JLabel("Quaternion Dev1");
		lblQuaternionDev.setBounds(158, 26, 98, 14);
		frame.getContentPane().add(lblQuaternionDev);
		
		lblQuaternionDev_1 = new JLabel("Quaternion Dev2");
		lblQuaternionDev_1.setBounds(516, 26, 98, 14);
		frame.getContentPane().add(lblQuaternionDev_1);
		
		lblRotMatrixDev = new JLabel("Rot Matrix Dev1");
		lblRotMatrixDev.setBounds(158, 151, 98, 14);
		frame.getContentPane().add(lblRotMatrixDev);
		
		lblRotMatrixDev_1 = new JLabel("Rot Matrix Dev2");
		lblRotMatrixDev_1.setBounds(516, 151, 98, 14);
		frame.getContentPane().add(lblRotMatrixDev_1);
		mShimmer.connect("COM19", "");
		mShimmer.enable3DOrientation(true);
		mShimmer2.connect("COM16", "");
		mShimmer2.enable3DOrientation(true);
		
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 1643, 592);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	@Override
	protected void processMsgFromCallback(ShimmerMsg shimmerMSG) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		int ind = shimmerMSG.mIdentifier;
		Object object = shimmerMSG.mB;
		if (ind == ShimmerPC.MSG_IDENTIFIER_STATE_CHANGE) {
			CallbackObject callbackObject = (CallbackObject)object;
			int state = callbackObject.mIndicator;
			String bAdd = callbackObject.mBluetoothAddress;
			if (callbackObject.mState == BT_STATE.CONNECTING) {

			} else if (callbackObject.mState == BT_STATE.CONNECTED) {
				if(mFirstTime && bAdd.equals(mShimmer.getBluetoothAddress())){
					mShimmer.writeShimmerAndSensorsSamplingRate(10.1);
					mShimmer.writeEnabledSensors(ShimmerObject.SENSOR_ACCEL|ShimmerObject.SENSOR_GYRO|ShimmerObject.SENSOR_MAG);
					mFirstTime = false;
				}
				if(mFirstTime2 && bAdd.equals(mShimmer2.getBluetoothAddress())){
					mShimmer.writeShimmerAndSensorsSamplingRate(10.1);
					mShimmer.writeEnabledSensors(ShimmerObject.SENSOR_ACCEL|ShimmerObject.SENSOR_GYRO|ShimmerObject.SENSOR_MAG);
					mFirstTime2 = false;
				}
			}  else {

			}
		}
		else if (ind == ShimmerPC.MSG_IDENTIFIER_DATA_PACKET) {
			ObjectCluster objectCluster = (ObjectCluster)object;

			if (objectCluster!=null){
				if (objectCluster.getShimmerName().equals("ShimmerDevice")){
					Collection<FormatCluster> accelXFormats = objectCluster.getCollectionOfFormatClusters(Shimmer3.ObjectClusterSensorName.QUAT_MADGE_9DOF_W);  // first retrieve all the possible formats for the current sensor device
					float q0 = 0,x = 0,y=0,z=0;
					if (accelXFormats != null){
						FormatCluster formatCluster = ((FormatCluster)ObjectCluster.returnFormatCluster(accelXFormats,"CAL")); // retrieve the calibrated data
						q0 = (float) formatCluster.mData;
						textFieldQW.setText(Double.toString(q0));
					}
					Collection<FormatCluster> accelYFormats = objectCluster.getCollectionOfFormatClusters(Shimmer3.ObjectClusterSensorName.QUAT_MADGE_9DOF_X);  // first retrieve all the possible formats for the current sensor device
					if (accelYFormats != null){
						FormatCluster formatCluster = ((FormatCluster)ObjectCluster.returnFormatCluster(accelYFormats,"CAL")); // retrieve the calibrated data
						x=(float) formatCluster.mData;
						textFieldQX.setText(Double.toString(x));
					}
					Collection<FormatCluster> accelZFormats = objectCluster.getCollectionOfFormatClusters(Shimmer3.ObjectClusterSensorName.QUAT_MADGE_9DOF_Y);  // first retrieve all the possible formats for the current sensor device
					if (accelZFormats != null){
						FormatCluster formatCluster = ((FormatCluster)ObjectCluster.returnFormatCluster(accelZFormats,"CAL")); // retrieve the calibrated data
						y=(float) formatCluster.mData;
						textFieldQY.setText(Double.toString(y));
					}
					Collection<FormatCluster> aaFormats = objectCluster.getCollectionOfFormatClusters(Shimmer3.ObjectClusterSensorName.QUAT_MADGE_9DOF_Z);  // first retrieve all the possible formats for the current sensor device
					if (aaFormats != null){
						FormatCluster formatCluster = ((FormatCluster)ObjectCluster.returnFormatCluster(aaFormats,"CAL")); // retrieve the calibrated data
						z=(float) formatCluster.mData;
						textFieldQZ.setText(Double.toString(z));
					}
					Quat4d q = new Quat4d(x, y,z,q0);
					
					m3d1.set(q);
					textFieldm00.setText(Double.toString(m3d1.m00));
					textFieldm01.setText(Double.toString(m3d1.m01));
					textFieldm02.setText(Double.toString(m3d1.m02));
					textFieldm10.setText(Double.toString(m3d1.m10));
					textFieldm11.setText(Double.toString(m3d1.m11));
					textFieldm12.setText(Double.toString(m3d1.m12));
					textFieldm20.setText(Double.toString(m3d1.m20));
					textFieldm21.setText(Double.toString(m3d1.m21));
					textFieldm22.setText(Double.toString(m3d1.m22));
				}
				

				if (objectCluster.getShimmerName().equals("ShimmerDevice2")){
					Collection<FormatCluster> accelXFormats = objectCluster.getCollectionOfFormatClusters(Shimmer3.ObjectClusterSensorName.QUAT_MADGE_9DOF_W);  // first retrieve all the possible formats for the current sensor device
					float q0 = 0,x = 0,y=0,z=0;
					if (accelXFormats != null){
						FormatCluster formatCluster = ((FormatCluster)ObjectCluster.returnFormatCluster(accelXFormats,"CAL")); // retrieve the calibrated data
						q0 = (float) formatCluster.mData;
						textField2QW.setText(Double.toString(q0));
					}
					Collection<FormatCluster> accelYFormats = objectCluster.getCollectionOfFormatClusters(Shimmer3.ObjectClusterSensorName.QUAT_MADGE_9DOF_X);  // first retrieve all the possible formats for the current sensor device
					if (accelYFormats != null){
						FormatCluster formatCluster = ((FormatCluster)ObjectCluster.returnFormatCluster(accelYFormats,"CAL")); // retrieve the calibrated data
						x=(float) formatCluster.mData;
						textField2QX.setText(Double.toString(x));
					}
					Collection<FormatCluster> accelZFormats = objectCluster.getCollectionOfFormatClusters(Shimmer3.ObjectClusterSensorName.QUAT_MADGE_9DOF_Y);  // first retrieve all the possible formats for the current sensor device
					if (accelZFormats != null){
						FormatCluster formatCluster = ((FormatCluster)ObjectCluster.returnFormatCluster(accelZFormats,"CAL")); // retrieve the calibrated data
						y=(float) formatCluster.mData;
						textField2QY.setText(Double.toString(y));
					}
					Collection<FormatCluster> aaFormats = objectCluster.getCollectionOfFormatClusters(Shimmer3.ObjectClusterSensorName.QUAT_MADGE_9DOF_Z);  // first retrieve all the possible formats for the current sensor device
					if (aaFormats != null){
						FormatCluster formatCluster = ((FormatCluster)ObjectCluster.returnFormatCluster(aaFormats,"CAL")); // retrieve the calibrated data
						z=(float) formatCluster.mData;
						textField2QZ.setText(Double.toString(z));
					}
					Quat4d q = new Quat4d(x, y,z,q0);
					
					m3d2.set(q);
					textField2m00.setText(Double.toString(m3d2.m00));
					textField2m01.setText(Double.toString(m3d2.m01));
					textField2m02.setText(Double.toString(m3d2.m02));
					textField2m10.setText(Double.toString(m3d2.m10));
					textField2m11.setText(Double.toString(m3d2.m11));
					textField2m12.setText(Double.toString(m3d2.m12));
					textField2m20.setText(Double.toString(m3d2.m20));
					textField2m21.setText(Double.toString(m3d2.m21));
					textField2m22.setText(Double.toString(m3d2.m22));
				}
			
				
				
			}
		}

	}
}
