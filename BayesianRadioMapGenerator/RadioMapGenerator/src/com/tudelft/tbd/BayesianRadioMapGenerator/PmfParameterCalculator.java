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
import java.util.List;

public class PmfParameterCalculator {
	private DatabaseManager databaseManager;
	
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
	 * @param cellid
	 * @param bssid
	 */
	private PmfParameter calculateGaussianPdf(int cellid, String bssid) {
		PmfParameter parameter = null;
		List<Integer> rssiValues = databaseManager.getRssiPerCellPerBssid(cellid, bssid);
		if(!rssiValues.isEmpty() && rssiValues.size() > 1) {
			// Calculate mean
			float mean = ((float)(rssiValues.stream().mapToInt(Integer::intValue).sum())) / rssiValues.size();
			
			// Calculate variance
			float variance = 0;
			for(int rssi : rssiValues) {
				float diff = (rssi - mean); 
				variance += (diff * diff); 
			}
			variance /= (rssiValues.size() - 1);
			
			// RSSI filter range
			float delta = (float) (Math.cbrt(3) * Math.sqrt(variance));
			float upperLimit = mean + delta;
			float lowerLimit = mean - delta;
			
			parameter = new PmfParameter(bssid, cellid, mean, variance, upperLimit, lowerLimit);		
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
