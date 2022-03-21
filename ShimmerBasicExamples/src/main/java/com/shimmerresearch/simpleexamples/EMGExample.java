package com.shimmerresearch.simpleexamples;

import com.shimmerresearch.algorithms.Filter;

import uk.me.berndporr.iirj.Butterworth;

public class EMGExample {
	static Butterworth butterworth = new Butterworth();
	static Filter mFilterHPF_159;
	
	public static void main(String[] args) {
		//below just an example please place them in an initializing method like in sensormapsexample
		double[] mHPFc159 = {159};
		try {
			mFilterHPF_159 = new Filter(Filter.HIGH_PASS, 1024,mHPFc159);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		butterworth.highPass(4,1024,0.5);
		
		//example usage when data is received, where x is data
		//x = butterworth0_5.filter(x);
		//x = mFilterHPF_159.filterData(x);
		
		
	}

}
