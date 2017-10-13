package com.shimmerresearch.simpleexamples;

import java.util.Collection;
import java.util.Map;

import com.shimmerresearch.algorithms.Filter;
import com.shimmerresearch.biophysicalprocessing.ECGtoHRAdaptive;
import com.shimmerresearch.biophysicalprocessing.PPGtoHRAlgorithm;
import com.shimmerresearch.bluetooth.ShimmerBluetooth.BT_STATE;
import com.shimmerresearch.driver.BasicProcessWithCallBack;
import com.shimmerresearch.driver.CallbackObject;
import com.shimmerresearch.driver.Configuration;
import com.shimmerresearch.driver.FormatCluster;
import com.shimmerresearch.driver.ObjectCluster;
import com.shimmerresearch.driver.ShimmerMsg;
import com.shimmerresearch.driver.Configuration.COMMUNICATION_TYPE;
import com.shimmerresearch.driver.Configuration.Shimmer3;
import com.shimmerresearch.driver.Configuration.Shimmer3.DerivedSensorsBitMask;
import com.shimmerresearch.driverUtilities.AssembleShimmerConfig;
import com.shimmerresearch.driverUtilities.SensorDetails;
import com.shimmerresearch.driverUtilities.ChannelDetails.CHANNEL_TYPE;
import com.shimmerresearch.pcDriver.ShimmerPC;
import com.shimmerresearch.sensors.SensorPPG;
import com.shimmerresearch.tools.bluetooth.BasicShimmerBluetoothManagerPc;

public class PPGToHRExample extends BasicProcessWithCallBack {
	
	ShimmerPC shimmerDevice = new ShimmerPC("ShimmerDevice");
	static BasicShimmerBluetoothManagerPc bluetoothManager = new BasicShimmerBluetoothManagerPc();
	private PPGtoHRAlgorithm heartRateCalculation;
	private boolean mConfigureOnFirstTime=true;
	//Put your device COM port here:
	final static String deviceComPort = "COM12";
	Filter lpf = null, hpf = null;
	public static void main(String args[]) {
		PPGToHRExample ppg = new PPGToHRExample();
		ppg.initialize();
		ppg.setWaitForData(bluetoothManager.callBackObject);
		
	}
	
	private void initialize() {
		bluetoothManager.connectShimmerThroughCommPort(deviceComPort);
	}
	
	
	@Override
	protected void processMsgFromCallback(ShimmerMsg shimmerMSG) {
		// TODO Auto-generated method stub

		// TODO Auto-generated method stub
		  int ind = shimmerMSG.mIdentifier;

		  Object object = (Object) shimmerMSG.mB;

		if (ind == ShimmerPC.MSG_IDENTIFIER_STATE_CHANGE) {
			CallbackObject callbackObject = (CallbackObject)object;
			
			if (callbackObject.mState == BT_STATE.CONNECTING) {
			} else if (callbackObject.mState == BT_STATE.CONNECTED) {
				shimmerDevice = (ShimmerPC) bluetoothManager.getShimmerDeviceBtConnected(deviceComPort);
				//checkECGEnabled();	//Check if ECG is enabled first before streaming
				//5 beats to average
				if (mConfigureOnFirstTime){
					ShimmerPC cloneDevice = shimmerDevice.deepClone();
					cloneDevice.setSensorEnabledState(Configuration.Shimmer3.SENSOR_ID.HOST_PPG_A13, true);
					AssembleShimmerConfig.generateSingleShimmerConfig(cloneDevice, COMMUNICATION_TYPE.BLUETOOTH);
			 		bluetoothManager.configureShimmer(cloneDevice);
			 		shimmerDevice.writeShimmerAndSensorsSamplingRate(128);
					heartRateCalculation = new PPGtoHRAlgorithm(shimmerDevice.getSamplingRateShimmer(), 5, 10);
									
					try{
						double [] cutoff = {5.0};
						lpf = new Filter(Filter.LOW_PASS, shimmerDevice.getSamplingRateShimmer(), cutoff);
						cutoff[0] = 0.5;
						hpf = new Filter(Filter.HIGH_PASS, shimmerDevice.getSamplingRateShimmer(), cutoff);
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

					mConfigureOnFirstTime = false;
				}
				//shimmerDevice.startStreaming();
			} else if (callbackObject.mState == BT_STATE.DISCONNECTED
//					|| callbackObject.mState == BT_STATE.NONE
					|| callbackObject.mState == BT_STATE.CONNECTION_LOST){
				
			}
		} else if (ind == ShimmerPC.MSG_IDENTIFIER_NOTIFICATION_MESSAGE) {
			CallbackObject callbackObject = (CallbackObject)object;
			int msg = callbackObject.mIndicator;
			if (msg== ShimmerPC.NOTIFICATION_SHIMMER_FULLY_INITIALIZED){
				shimmerDevice.startStreaming();
			}
			if (msg == ShimmerPC.NOTIFICATION_SHIMMER_STOP_STREAMING) {
				
			} else if (msg == ShimmerPC.NOTIFICATION_SHIMMER_START_STREAMING) {
				
			} else {}
		} else if (ind == ShimmerPC.MSG_IDENTIFIER_DATA_PACKET) {
			
			double dataArrayPPG = 0;
			double heartRate = Double.NaN;

			
			int INVALID_RESULT = -1;

			ObjectCluster objc = (ObjectCluster) shimmerMSG.mB;
			
			Collection<FormatCluster> adcFormats = objc.getCollectionOfFormatClusters(SensorPPG.ObjectClusterSensorName.PPG_A13);
			FormatCluster format = ((FormatCluster)ObjectCluster.returnFormatCluster(adcFormats, CHANNEL_TYPE.CAL.toString())); // retrieve the calibrated data
			
			if(format != null) {
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

			System.out.println("Heart rate: " + heartRate);
			}
			else {
				System.out.println("ERROR! FormatCluster is Null!");
			}

		} else if (ind == ShimmerPC.MSG_IDENTIFIER_PACKET_RECEPTION_RATE_OVERALL) {
			
		}
	
	
		
		
	}
	
//	private void checkECGEnabled() {
//		 Map<Integer, SensorDetails> sensorMap = shimmerDevice.getSensorMap();
//		 int count = 0;
//		 
//		 //Check how many sensors the device is compatible with
//		 for(SensorDetails details : sensorMap.values()) {
//			 if(shimmerDevice.isVerCompatibleWithAnyOf(details.mSensorDetailsRef.mListOfCompatibleVersionInfo)) {
//				 count++;
//			 }
//		 }
//		 
//		 //final int[] sensorKeys = new int[count];
//
//		 for(int key : sensorMap.keySet()) {
//			 SensorDetails sd = sensorMap.get(key);
//			 if(shimmerDevice.isVerCompatibleWithAnyOf(sd.mSensorDetailsRef.mListOfCompatibleVersionInfo)) {
//				 String sensorName = sd.mSensorDetailsRef.mGuiFriendlyLabel;
//				 if(sensorName.contains("ECG")) {
//					 if(!shimmerDevice.checkIfSensorEnabled(key)) {
//						 shimmerDevice.setSensorEnabledState(key, true);
//					 }
//				 }
//			 }
//		 }
//		 
//		 double samplingRate = shimmerDevice.getSamplingRateShimmer();
//		 if(samplingRate < 128) {
//			 //We need at least 128Hz sampling rate for the ECG to HR algorithm
//			 shimmerDevice.setSamplingRateShimmer(256);
//		 }
		 


	//}

	
}
