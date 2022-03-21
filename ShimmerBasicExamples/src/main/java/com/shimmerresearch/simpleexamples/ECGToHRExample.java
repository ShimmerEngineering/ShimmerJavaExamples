package com.shimmerresearch.simpleexamples;

import java.util.Collection;
import java.util.Map;

import javax.swing.JCheckBox;

import com.shimmerresearch.biophysicalprocessing.ECGtoHRAdaptive;
import com.shimmerresearch.bluetooth.ShimmerBluetooth.BT_STATE;
import com.shimmerresearch.driver.BasicProcessWithCallBack;
import com.shimmerresearch.driver.CallbackObject;
import com.shimmerresearch.driver.Configuration;
import com.shimmerresearch.driver.FormatCluster;
import com.shimmerresearch.driver.ObjectCluster;
import com.shimmerresearch.driver.ShimmerMsg;
import com.shimmerresearch.driver.Configuration.COMMUNICATION_TYPE;
import com.shimmerresearch.driver.Configuration.Shimmer3;
import com.shimmerresearch.driver.Configuration.Shimmer3.SensorBitmap;
import com.shimmerresearch.driverUtilities.AssembleShimmerConfig;
import com.shimmerresearch.driverUtilities.SensorDetails;
import com.shimmerresearch.exceptions.ShimmerException;
import com.shimmerresearch.exgConfig.ExGConfigOptionDetails.EXG_CHIP_INDEX;
import com.shimmerresearch.pcDriver.ShimmerPC;
import com.shimmerresearch.tools.bluetooth.BasicShimmerBluetoothManagerPc;

public class ECGToHRExample extends BasicProcessWithCallBack{

	ShimmerPC shimmerDevice = new ShimmerPC("ShimmerDevice");
	static BasicShimmerBluetoothManagerPc bluetoothManager = new BasicShimmerBluetoothManagerPc();
	private ECGtoHRAdaptive heartRateCalculationECG;
	private boolean mConfigureOnFirstTime = true;
	//Put your device COM port here (e.g. COM1, COM2, etc):	
	final String deviceComPort = "COM35";	

	public static void main(String args[]) {
		ECGToHRExample ecg = new ECGToHRExample();
		ecg.initialize();
		ecg.setWaitForData(bluetoothManager.callBackObject);
	}

	public void initialize() {
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
				if (mConfigureOnFirstTime){
					ShimmerPC cloneDevice = shimmerDevice.deepClone();
					cloneDevice.setEnabledAndDerivedSensorsAndUpdateMaps(0, 0);
					cloneDevice.setSensorEnabledState(Configuration.Shimmer3.SENSOR_ID.HOST_ECG, true);
					cloneDevice.setDefaultECGConfiguration(256);
					AssembleShimmerConfig.generateSingleShimmerConfig(cloneDevice, COMMUNICATION_TYPE.BLUETOOTH);
			 		bluetoothManager.configureShimmer(cloneDevice);
			 		shimmerDevice.writeShimmerAndSensorsSamplingRate(256);
					//shimmerDevice.writeEnabledSensors(SensorBitmap.SENSOR_EXG1_24BIT|SensorBitmap.SENSOR_EXG2_24BIT);
					//shimmerDevice.enableDefaultECGConfiguration();
					//checkECGEnabled();	//Check if ECG is enabled first before streaming
					heartRateCalculationECG = new ECGtoHRAdaptive(shimmerDevice.getSamplingRateShimmer());
					try {
						shimmerDevice.startStreaming();
					} catch (ShimmerException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					mConfigureOnFirstTime = false;
				}
			} else if (callbackObject.mState == BT_STATE.DISCONNECTED
					//					|| callbackObject.mState == BT_STATE.NONE
					|| callbackObject.mState == BT_STATE.CONNECTION_LOST){

			}
		} else if (ind == ShimmerPC.MSG_IDENTIFIER_NOTIFICATION_MESSAGE) {
			CallbackObject callbackObject = (CallbackObject)object;
			int msg = callbackObject.mIndicator;
			if (msg== ShimmerPC.NOTIFICATION_SHIMMER_FULLY_INITIALIZED){

			}
			if (msg == ShimmerPC.NOTIFICATION_SHIMMER_STOP_STREAMING) {

			} else if (msg == ShimmerPC.NOTIFICATION_SHIMMER_START_STREAMING) {

			} else {}
		} else if (ind == ShimmerPC.MSG_IDENTIFIER_DATA_PACKET) {

			ObjectCluster objc = (ObjectCluster) shimmerMSG.mB;

			Collection<FormatCluster> adcFormats = objc.getCollectionOfFormatClusters(Shimmer3.ObjectClusterSensorName.ECG_LA_RA_24BIT);
			FormatCluster format = ((FormatCluster)ObjectCluster.returnFormatCluster(adcFormats,"CAL")); // retrieve the calibrated data
			double dataArrayECG = format.mData;

			Collection<FormatCluster> formatTS = objc.getCollectionOfFormatClusters(Shimmer3.ObjectClusterSensorName.TIMESTAMP);
			FormatCluster ts = ObjectCluster.returnFormatCluster(formatTS,"CAL");
			double ecgTimeStamp = ts.mData;

			double heartRate = heartRateCalculationECG.ecgToHrConversion(dataArrayECG, ecgTimeStamp);
			System.out.println("Heart rate: " + heartRate);

		} else if (ind == ShimmerPC.MSG_IDENTIFIER_PACKET_RECEPTION_RATE_OVERALL) {

		}




	}

	private void checkECGEnabled() {
		Map<Integer, SensorDetails> sensorMap = shimmerDevice.getSensorMap();
		int count = 0;

		//Check how many sensors the device is compatible with
		for(SensorDetails details : sensorMap.values()) {
			if(shimmerDevice.isVerCompatibleWithAnyOf(details.mSensorDetailsRef.mListOfCompatibleVersionInfo)) {
				count++;
			}
		}

		//final int[] sensorKeys = new int[count];

		for(int key : sensorMap.keySet()) {
			SensorDetails sd = sensorMap.get(key);
			if(shimmerDevice.isVerCompatibleWithAnyOf(sd.mSensorDetailsRef.mListOfCompatibleVersionInfo)) {
				String sensorName = sd.mSensorDetailsRef.mGuiFriendlyLabel;
				if(sensorName.contains("ECG")) {
					if(!shimmerDevice.checkIfSensorEnabled(key)) {
						shimmerDevice.setSensorEnabledState(key, true);
					}
				}
			}
		}

		double samplingRate = shimmerDevice.getSamplingRateShimmer();
		if(samplingRate < 128) {
			//We need at least 128Hz sampling rate for the ECG to HR algorithm
			shimmerDevice.setSamplingRateShimmer(256);
		}



	}


}
