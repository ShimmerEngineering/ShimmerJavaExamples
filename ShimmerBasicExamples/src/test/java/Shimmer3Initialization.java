

import com.shimmerresearch.driver.Configuration.Shimmer3.SENSOR_ID;
import com.shimmerresearch.pcDriver.ShimmerPC;

public class Shimmer3Initialization {

	public static void main(String[] args) {
		// TODO Auto-generated method stub// TODO Auto-generated method stub
		String myName="ShimmerTest";
		double samplingRate=512;
		int accelRange=0;
		int gsrRange=4;
		Integer[] sensorIDs = new Integer[4];
		sensorIDs[0]= SENSOR_ID.SHIMMER_LSM303_MAG;
		sensorIDs[1]= SENSOR_ID.SHIMMER_MPU9X50_GYRO;
		sensorIDs[2]= SENSOR_ID.HOST_PPG_A12;
		sensorIDs[3]= SENSOR_ID.SHIMMER_BMPX80_PRESSURE;
		boolean continousSync = false;
		int orientation=0;
		int gyroRange = 1;
		int magRange=5;
		int pressureResolution = 3;
		ShimmerPC s = new ShimmerPC(myName, samplingRate, accelRange, gsrRange, sensorIDs, gyroRange, magRange, orientation,pressureResolution);
		s.connect("COM12",null);
	}

}
