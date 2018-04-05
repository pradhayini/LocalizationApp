/*
 * Copyright (c) 2018.  Group TBD, SPS2018, TUDelft
 * Author: Pradhayini Ramamurthy
 * Permission to use, copy, modify, and/or distribute this software for any purpose with or without
 * fee is hereby granted, provided that the above copyright notice and this permission notice appear
 * in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES WITH REGARD TO THIS
 * SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE
 * AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT,
 * NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR PERFORMANCE
 * OF THIS SOFTWARE.
 */

package com.tudelft.tbd.BayesianRadioMapGenerator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PmfParameterCalculator {
	private DatabaseManager databaseManager;
	private static final float cbrt3 = (float)Math.cbrt(3);
	
	public PmfParameterCalculator() {
		databaseManager = new DatabaseManager();
	}
	
	private List<Measurement> getHistogram(int cellid){
		return databaseManager.getHistogramDataForCellid(cellid);		
	}
	
	/**
	 * Based on:
	 * Localization Based on RSSI Exploiting Gaussian and Averaging Filter in Wireless Sensor Network
	 * Authors: Ranjan Kumar MahapatraEmail authorN. S. V. Shet
	 * @param cellId
	 * @param bssId
	 */
	private PmfParameter calculateGaussianPdf(int cellId, String bssId) {
		List<Integer> rssiValues = databaseManager.getRssiPerCellPerBssid(cellId, bssId);
		PmfParameter parameter = calculateMeanAndVariance(rssiValues);
		int initialSize = rssiValues.size();
		
		if(parameter != null) {
			// RSSI filter range
			float delta = cbrt3 * (float) (Math.sqrt(parameter.getVariance()));
			float upperLimit = parameter.getMean() + delta;
			float lowerLimit = parameter.getMean() - delta;
			
			// Remove values outside range (outliers)
			for (Iterator<Integer> iterator = rssiValues.iterator(); iterator.hasNext();) {
			    Integer rssi = iterator.next();
				if((Float.compare(rssi, upperLimit) > 0) || (Float.compare(rssi, lowerLimit) < 0)) {
				    iterator.remove();
			    }
			}
			
			if(initialSize > rssiValues.size()) {
				// Recalculate mean and variance
				parameter = calculateMeanAndVariance(rssiValues);
			}
		}

		if(parameter != null) {
			parameter.setBssId(bssId);
			parameter.setCellId(cellId);
		}
		return parameter;
	}
	
	private PmfParameter calculateMeanAndVariance(List<Integer> rssiValues) {
		PmfParameter parameter = null;
		if (!rssiValues.isEmpty() && rssiValues.size() > 1) {
			// Calculate mean
			float mean = ((float) (rssiValues.stream().mapToInt(Integer::intValue).sum())) / rssiValues.size();
			// Calculate variance
			float variance = 0;
			for (int rssi : rssiValues) {
				float diff = (rssi - mean);
				variance += (diff * diff);
			}
			variance /= (rssiValues.size() - 1);
			parameter = new PmfParameter(mean, variance);
		}
		
		return parameter;
	}
	
	private List<PmfParameter> calculatePmfTables(){
		List<PmfParameter> pmfParameters = new ArrayList<PmfParameter>();
		List<Integer> cellids = databaseManager.getAllCellids();
		List<String> bssids = databaseManager.getAllBssids();
				
		for(int cellId : cellids) {
			for(String bssId : bssids) {
				PmfParameter parameterSet = calculateGaussianPdf(cellId, bssId);
				if(parameterSet != null) {
					pmfParameters.add(parameterSet);
				}
			}
		}
		
		return pmfParameters;
	}
	
	private void calculateAndWritePmfData() {
		List<PmfParameter> parameters = calculatePmfTables();
		databaseManager.writePmfParameters(parameters);
	}
	
	private void closeDatabase() {
		databaseManager.close();
	}
	
	
	public static void main( String args[] ) {
		PmfParameterCalculator calculator = new PmfParameterCalculator();
		calculator.calculateAndWritePmfData();
		calculator.closeDatabase();		
	}
}
