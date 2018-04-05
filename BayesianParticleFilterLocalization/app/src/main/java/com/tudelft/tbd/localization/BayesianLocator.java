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

package com.tudelft.tbd.localization;

import android.arch.lifecycle.MutableLiveData;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.support.annotation.NonNull;

import com.tudelft.tbd.entities.RadioMap;
import com.tudelft.tbd.repositories.Area28Repository;
import com.tudelft.tbd.repositories.RadioMapRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.util.Collections.nCopies;
import static java.util.Collections.sort;

/**
 * Runs (one iteration of) Bayesian localization algorithm using Gaussian PDFs from training data
 * representing RSSI distributions for each BSSID/CellId combination.
 */
public class BayesianLocator {
    // Bookkeeping variables
    private int oscillationCount = 0;
    private static List<String> recognizedBssIds;

    // Bayesian variables
    private static final float sqrt2pi = (float) Math.sqrt(2*22/7);
    private static final float delta = (float) 0.5;
    private List<Float> priori;
    private CellProbability maxPriori;
    private float initialProbability = 0;

    // Infrastructure variables
    private RadioMapRepository radioMapRepository;
    private Area28Repository area28Repository;
    private WifiManager wifiManager;
    private int cellCount;

    // Map Variables
    private List<Integer> possibleCellIds;
    private MutableLiveData<String> cellIds;
    private MutableLiveData<Integer> floor;

    /**
     * Constructor
     * @param wifiManager instance of WifiManager
     * @param radioMapRepository repository manager of RadioMapDatabase
     * @param area28Repository repository manager of Area28Database
     */
    public BayesianLocator(WifiManager wifiManager, RadioMapRepository radioMapRepository,
                           Area28Repository area28Repository){
        this.wifiManager = wifiManager;
        this.radioMapRepository = radioMapRepository;
        this.area28Repository = area28Repository;
        cellCount = calculateCellCount();
        cellIds = new MutableLiveData<>();
        floor = new MutableLiveData<>();
        initialize();
    }

    /**
     * Getter for possibleCellIds
     * @return List of IDs for cells in which user is possibily located
     */
    public List<Integer> getPossibleCellIds() {
        return possibleCellIds;
    }

    /**
     * Getter for floor
     * @return MutableLiveData instance of floor
     */
    public MutableLiveData<Integer> getFloor() {
        return floor;
    }

    /**
     * Getter for cellIds
     * @return MutableLiveData instance of cellIds
     */
    public MutableLiveData<String> getCellIds() {
        return cellIds;
    }

    /**
     * Run one iteration of Bayesian localization
     */
    public void localize(){
        if(possibleCellIds != null && possibleCellIds.size() == 1){
            // Restart localization
            initialize();
        }

        List<Measurement> scannedRssi = measureRssi();
        sort(scannedRssi);

        for (Measurement meas : scannedRssi) {
            int nonZeroCount = applyBayes(meas);
            if(nonZeroCount == 1)
                break;
        }

        List<CellProbability> possibleCells = Collections.synchronizedList(new ArrayList<CellProbability>());

        // Get cells with non-zero probabilities
        for(int i = 0; i < cellCount; i++){
            if(Float.compare(priori.get(i), 0) > 0)
                possibleCells.add(new CellProbability(i + 1, priori.get(i)));
        }

        // Sort cells in descending order of probability
        sort(possibleCells);

        possibleCellIds.clear();

        if(possibleCells.size() > 0){

            // Filter on threshold
            if(Float.compare(possibleCells.get(0).getProbability(), (float) 0.95) >= 0){
                possibleCellIds.add(possibleCells.get(0).getCellId());
            }
            else if(possibleCells.get(0).getCellId() == maxPriori.getCellId() &&
                    Float.compare(possibleCells.get(0).getProbability(), 0) > 0){
                // Filter on oscillation around 'p'
                float upperLimit = maxPriori.getProbability() + delta;
                float lowerLimit = maxPriori.getProbability() - delta;
                float zeroCellProbability = possibleCells.get(0).getProbability();
                if((Float.compare(zeroCellProbability, initialProbability) > 0) &&
                        (Float.compare(upperLimit, zeroCellProbability) >= 0) &&
                        (Float.compare(lowerLimit, zeroCellProbability) <= 0)){
                    oscillationCount++;
                }
                if(oscillationCount > 2){
                    possibleCellIds.add(possibleCells.get(0).getCellId());
                }
            }

            // Default: pick top 3 possible locations
            if(possibleCellIds.isEmpty()){
                for(CellProbability cp : possibleCells){
                    possibleCellIds.add(cp.getCellId());
                }
            }

            maxPriori = possibleCells.get(0);
            floor.setValue(area28Repository.getCell(maxPriori.cellId).getFloor());
        }

        cellIds.setValue(listToString(possibleCellIds));
    }

    /**
     * Initialize with initial belief - 'priori' with equal probability for all cells
     */
    private void initialize() {
        initialProbability = 1/(float) cellCount;
        if(priori == null){
            priori = Collections.synchronizedList(
                    new ArrayList<Float>(nCopies(cellCount, initialProbability)));
        } else {
            priori.clear();
            priori.addAll(nCopies(cellCount, initialProbability));
        }

        if(maxPriori == null) {
            maxPriori = new CellProbability(0, 0);
        } else {
            maxPriori.reset();
        }

        if(possibleCellIds == null) {
            possibleCellIds = Collections.synchronizedList(new ArrayList<Integer>());
        } else {
            possibleCellIds.clear();
        }
    }

    /**
     * Get concatenated string representation of possible cell IDs
     * @param ids List of possible cell IDs
     * @return concatenated string representation of cellIds
     */
    private String listToString(List<Integer> ids){
        if(ids == null || ids.isEmpty())
            return "";

        StringBuilder intsText = new StringBuilder();
        intsText.append(ids.get(0));
        for(int i = 1; i < ids.size(); i++){
            intsText.append(";");
            intsText.append(ids.get(i));
        }
        return intsText.toString();
    }

    /**
     * Determine number of cells, accounting for any that do not have training data.
     * @return number of cells in localization area
     */
    private int calculateCellCount(){
        List<Integer> cellIds = radioMapRepository.getAllCellIds();
        int count = 0;
        if(!cellIds.isEmpty()){
            sort(cellIds);
            count = cellIds.get(cellIds.size() - 1);
        }

        return count;
    }

    /**
     * Filter out BSSIDs which have training data
     */
    private boolean checkIfBssIdIsUsable(String bssId){
        return (getRecognizedBssIds().contains(bssId));
    }

    /**
     * Getter for RecognizedBssIds
     * @return List of BSSIDs which have stored training data
     */
    private List<String> getRecognizedBssIds() {
        // Get recognized vendor list
        if(recognizedBssIds == null) recognizedBssIds = radioMapRepository.getAllBssIDs();
        return recognizedBssIds;
    }

    /**
     * Perform Bayesian computations for posteriori values
     * @param meas RSSI measurement
     */
    private Integer applyBayes(Measurement meas){
        List<RadioMap> parameters = radioMapRepository.getAllParametersForBssId(meas.getBssId());
        List<Float> posteriori = Collections.synchronizedList(new ArrayList<Float>(nCopies(cellCount, (float)0)));
        List<Integer> cellIds = radioMapRepository.getAllCellIds();

        float sum = 0;
        for (RadioMap parameter : parameters) {
            // Get parameters of Gaussian PDF
            int cellId = parameter.getCellId();
            float mean = parameter.getMean();
            float variance = parameter.getVariance();
            float pCell;

            // Calculate PDF
            if(Float.compare(variance, 0) == 0){
                pCell = meas.getRssi() == mean ? 1 : 0;
            } else {
                // Probability P(rss_j / cell_i), calculated with a Gaussian PDF
                float rssiMinMean = meas.getRssi() - mean;
                pCell = (float) (Math.exp(((-1) * rssiMinMean * rssiMinMean) / (2 * variance))
                        / (sqrt2pi * Math.sqrt(variance)));
            }

            // p(cell_i ) * p (rss_j /cell_i )
            float probability = priori.get(cellId-1) * pCell;

            posteriori.set(cellId - 1, probability);
            sum += probability;

            // Track cells that do not have training data for this BSSID
            cellIds.remove(cellId);
        }

        int nonZeroProbabilities = 0;
        if(sum > 0){
            // Normalize cells
            for(int i = 0; i < cellCount; i++){
                // For cells which do not have training data for this BSSID
                if(cellIds.contains(i+1)){
                    priori.set(i, (float)0);
                }
                else if(Float.compare(posteriori.get(i), 0) > 0) {
                    priori.set(i, posteriori.get(i)/sum);
                    nonZeroProbabilities ++;
                }
            }
        }
        return nonZeroProbabilities;
    }

    /**
     * Run a WiFi scan for RSSI measurements of detected BSSIDs
     * @return List of measured RSSI values
     */
    private List<Measurement> measureRssi() {

        List<Measurement> measurements = new ArrayList<>();

        if(wifiManager != null) {
            // Start a wifi scan.
            wifiManager.startScan();
            // Store results in a list.
            List<ScanResult> scanResults = wifiManager.getScanResults();

            // Write results to a label
            for (ScanResult scanResult : scanResults) {

                // Filter usable BSSIDs based on training data
                if(checkIfBssIdIsUsable(scanResult.BSSID)){
                    int rssiAbs = WifiManager.calculateSignalLevel(scanResult.level, 256);
                    measurements.add(new Measurement(scanResult.BSSID, rssiAbs));
                }
            }
        }
        return measurements;
    }

    /**
     * Class representing probability calculated in a given cell
     */
    private class CellProbability implements Comparable<CellProbability> {
        private int  cellId;
        private Float probability;

        CellProbability(int cellId, float probability){
            this.cellId = cellId;
            this.probability = probability;
        }

        /**
         * Comparator for sorting in descending order of prbability
         */
        @Override
        public int compareTo(@NonNull CellProbability cp) {
            return cp.probability.compareTo(this.probability);
        }

        Float getProbability() {
            return probability;
        }
        int getCellId() {
            return cellId;
        }

        void reset() {
            probability = (float)0;
            cellId = 0;
        }
    }

}
