

import com.shimmerresearch.pcDriver.ShimmerPC;

public class Shimmer2RIntialization {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String myName="";
		double samplingRate=256;
		int accelRange=3;
		int gsrRange=1;
		int setEnabledSensors=  ShimmerPC.SENSOR_ACCEL|ShimmerPC.SENSOR_MAG;
		int magGain=0;
		int orientation=0;
		ShimmerPC shimmer = new ShimmerPC( myName,  samplingRate,  accelRange,  gsrRange,  setEnabledSensors, magGain,  orientation);
		shimmer.connect("COM20", null);
	}

}
