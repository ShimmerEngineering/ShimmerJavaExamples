/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 *  Copyright (c) 2009 by Vinnie Falco
 *  Copyright (c) 2016 by Bernd Porr
 */


package uk.me.berndporr.iirj;

import org.apache.commons.math3.complex.Complex;

/**
 * User facing class which contains all the methods the user uses to create
 * ChebyshevI filters. This done in this way: ChebyshevI chebyshevI = new
 * ChebyshevI(); Then call one of the methods below to create low-,high-,band-,
 * or stopband filters. For example: chebyshevI.bandPass(2,250,50,5,0.5);
 */
public class ChebyshevI extends Cascade {

	class AnalogLowPass extends LayoutBase {

		private int nPoles;

		// ------------------------------------------------------------------------------

		public AnalogLowPass(int _nPoles) {
			super(_nPoles);
			nPoles = _nPoles;
		}

		public void design(double rippleDb) {

			reset();

			double eps = Math.sqrt(1. / Math.exp(-rippleDb * 0.1
					* MathSupplement.doubleLn10) - 1);
			double v0 = MathSupplement.asinh(1 / eps) / nPoles;
			double sinh_v0 = -Math.sinh(v0);
			double cosh_v0 = Math.cosh(v0);

			double n2 = 2 * nPoles;
			int pairs = nPoles / 2;
			for (int i = 0; i < pairs; ++i) {
				int k = 2 * i + 1 - nPoles;
				double a = sinh_v0 * Math.cos(k * Math.PI / n2);
				double b = cosh_v0 * Math.sin(k * Math.PI / n2);

				addPoleZeroConjugatePairs(new Complex(a, b), new Complex(
						Double.POSITIVE_INFINITY));
			}

			if ((nPoles & 1) == 1) {
				add(new Complex(sinh_v0, 0), new Complex(
						Double.POSITIVE_INFINITY));
				setNormal(0, 1);
			} else {
				setNormal(0, Math.pow(10, -rippleDb / 20.));
			}
		}
	}

	private void setupLowPass(int order, double sampleRate,
			double cutoffFrequency, double rippleDb, int directFormType) {

		AnalogLowPass m_analogProto = new AnalogLowPass(order);
		m_analogProto.design(rippleDb);

		LayoutBase m_digitalProto = new LayoutBase(order);

		new LowPassTransform(cutoffFrequency / sampleRate, m_digitalProto,
				m_analogProto);

		setLayout(m_digitalProto, directFormType);
	}

	/**
	 * ChebyshevI Lowpass filter with default toplogy
	 * 
	 * @param order
	 *            The order of the filter
	 * @param sampleRate
	 *            The sampling rate of the system
	 * @param cutoffFrequency
	 *            the cutoff frequency
	 * @param rippleDb
	 *            passband ripple in decibel sensible value: 1dB
	 */
	public void lowPass(int order, double sampleRate, double cutoffFrequency,
			double rippleDb) {
		setupLowPass(order, sampleRate, cutoffFrequency, rippleDb,
				DirectFormAbstract.DIRECT_FORM_II);
	}

	/**
	 * ChebyshevI Lowpass filter with custom topology
	 * 
	 * @param order
	 *            The order of the filter
	 * @param sampleRate
	 *            The sampling rate of the system
	 * @param cutoffFrequency
	 *            The cutoff frequency
	 * @param rippleDb
	 *            passband ripple in decibel sensible value: 1dB
	 * @param directFormType
	 *            The filter topology. This is either
	 *            DirectFormAbstract.DIRECT_FORM_I or DIRECT_FORM_II
	 */
	public void lowPass(int order, double sampleRate, double cutoffFrequency,
			double rippleDb, int directFormType) {
		setupLowPass(order, sampleRate, cutoffFrequency, rippleDb,
				directFormType);
	}

	private void setupHighPass(int order, double sampleRate,
			double cutoffFrequency, double rippleDb, int directFormType) {

		AnalogLowPass m_analogProto = new AnalogLowPass(order);
		m_analogProto.design(rippleDb);

		LayoutBase m_digitalProto = new LayoutBase(order);

		new HighPassTransform(cutoffFrequency / sampleRate, m_digitalProto,
				m_analogProto);

		setLayout(m_digitalProto, directFormType);
	}

	/**
	 * ChebyshevI Highpass filter with default topology
	 * 
	 * @param order
	 *            The order of the filter
	 * @param sampleRate
	 *            The sampling rate of the system
	 * @param cutoffFrequency
	 *            the cutoff frequency
	 * @param rippleDb
	 *            passband ripple in decibel sensible value: 1dB
	 */
	public void highPass(int order, double sampleRate, double cutoffFrequency,
			double rippleDb) {
		setupHighPass(order, sampleRate, cutoffFrequency, rippleDb,
				DirectFormAbstract.DIRECT_FORM_II);
	}

	/**
	 * ChebyshevI Lowpass filter and custom filter topology
	 * 
	 * @param order
	 *            The order of the filter
	 * @param sampleRate
	 *            The sampling rate of the system
	 * @param cutoffFrequency
	 *            The cutoff frequency
	 * @param rippleDb
	 *            passband ripple in decibel sensible value: 1dB
	 * @param directFormType
	 *            The filter topology. This is either
	 *            DirectFormAbstract.DIRECT_FORM_I or DIRECT_FORM_II
	 */
	public void highPass(int order, double sampleRate, double cutoffFrequency,
			double rippleDb, int directFormType) {
		setupHighPass(order, sampleRate, cutoffFrequency, rippleDb,
				directFormType);
	}

	private void setupBandStop(int order, double sampleRate,
			double centerFrequency, double widthFrequency, double rippleDb,
			int directFormType) {

		AnalogLowPass m_analogProto = new AnalogLowPass(order);
		m_analogProto.design(rippleDb);

		LayoutBase m_digitalProto = new LayoutBase(order * 2);

		new BandStopTransform(centerFrequency / sampleRate, widthFrequency
				/ sampleRate, m_digitalProto, m_analogProto);

		setLayout(m_digitalProto, directFormType);
	}

	/**
	 * Bandstop filter with default topology
	 * 
	 * @param order
	 *            Filter order (actual order is twice)
	 * @param sampleRate
	 *            Samping rate of the system
	 * @param centerFrequency
	 *            Center frequency
	 * @param widthFrequency
	 *            Width of the notch
	 * @param rippleDb
	 *            passband ripple in decibel sensible value: 1dB
	 */
	public void bandStop(int order, double sampleRate, double centerFrequency,
			double widthFrequency, double rippleDb) {
		setupBandStop(order, sampleRate, centerFrequency, widthFrequency,
				rippleDb, DirectFormAbstract.DIRECT_FORM_II);
	}

	/**
	 * Bandstop filter with custom topology
	 * 
	 * @param order
	 *            Filter order (actual order is twice)
	 * @param sampleRate
	 *            Samping rate of the system
	 * @param centerFrequency
	 *            Center frequency
	 * @param widthFrequency
	 *            Width of the notch
	 * @param rippleDb
	 *            passband ripple in decibel sensible value: 1dB
	 * @param directFormType
	 *            The filter topology
	 */
	public void bandStop(int order, double sampleRate, double centerFrequency,
			double widthFrequency, double rippleDb, int directFormType) {
		setupBandStop(order, sampleRate, centerFrequency, widthFrequency,
				rippleDb, directFormType);
	}

	private void setupBandPass(int order, double sampleRate,
			double centerFrequency, double widthFrequency, double rippleDb,
			int directFormType) {

		AnalogLowPass m_analogProto = new AnalogLowPass(order);
		m_analogProto.design(rippleDb);

		LayoutBase m_digitalProto = new LayoutBase(order * 2);

		new BandPassTransform(centerFrequency / sampleRate, widthFrequency
				/ sampleRate, m_digitalProto, m_analogProto);

		setLayout(m_digitalProto, directFormType);

	}

	/**
	 * Bandpass filter with default topology
	 * 
	 * @param order
	 *            Filter order
	 * @param sampleRate
	 *            Sampling rate
	 * @param centerFrequency
	 *            Center frequency
	 * @param widthFrequency
	 *            Width of the notch
	 * @param rippleDb
	 *            passband ripple in decibel sensible value: 1dB
	 */
	public void bandPass(int order, double sampleRate, double centerFrequency,
			double widthFrequency, double rippleDb) {
		setupBandPass(order, sampleRate, centerFrequency, widthFrequency,
				rippleDb, DirectFormAbstract.DIRECT_FORM_II);
	}

	/**
	 * Bandpass filter with custom topology
	 * 
	 * @param order
	 *            Filter order
	 * @param sampleRate
	 *            Sampling rate
	 * @param centerFrequency
	 *            Center frequency
	 * @param widthFrequency
	 *            Width of the notch
	 * @param rippleDb
	 *            passband ripple in decibel sensible value: 1dB
	 * @param directFormType
	 *            The filter topology (see DirectFormAbstract)
	 */
	public void bandPass(int order, double sampleRate, double centerFrequency,
			double widthFrequency, double rippleDb, int directFormType) {
		setupBandPass(order, sampleRate, centerFrequency, widthFrequency,
				rippleDb, directFormType);
	}

}
