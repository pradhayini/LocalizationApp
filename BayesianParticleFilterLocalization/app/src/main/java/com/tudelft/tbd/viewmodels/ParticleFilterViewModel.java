package com.tudelft.tbd.viewmodels;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.tudelft.tbd.localization.ParticleFilterLocator;
import com.tudelft.tbd.map.Area28MapModel;
import com.tudelft.tbd.repositories.Area28Repository;
import com.tudelft.tbd.repositories.ParticleRepository;

/**
 * ViewModel for Particle Filter localization
 */
public class ParticleFilterViewModel extends BaseViewModel {
    private ParticleFilterLocator locator;

    private MutableLiveData<String> cellIds;
    private MutableLiveData<Integer> floor;
    private MutableLiveData<Boolean> updateMap;

    public ParticleFilterViewModel(@NonNull Application application) {
        super(application);
        Area28Repository area28Repository = new Area28Repository(application);
        ParticleRepository particleRepository = new ParticleRepository(application);
        setArea28MapModel(new Area28MapModel(area28Repository, particleRepository, application));
        locator = new ParticleFilterLocator(area28Repository, particleRepository);

        cellIds = locator.getCellIds();
        floor = locator.getFloor();
        updateMap = locator.getUpdateMap();
    }

    /*
    Getters and setters
     */

    public MutableLiveData<String> getCellIds() {
        return cellIds;
    }
    public MutableLiveData<Integer> getFloor() {
        return floor;
    }
    public MutableLiveData<Boolean> getUpdateMap() {
        return updateMap;
    }

    public void setUserHeight(float userHeight) {
        locator.setUserHeight(userHeight);
    }

    /**
     * Initialize particles
     */
    public void initialize(){
        locator.initialize();
    }

    /**
     * Determine change in direction
     * @param azimuth detected rotation around z-axis
     */
    public void evaluateDirectionChange(float azimuth){
        locator.evaluateDirectionChange(azimuth);
    }

    /**
     * Calculate distance moved since last measurement
     * @param latestStepCount Latest step count measurement
     */
    public void calculateDistanceMoved(int latestStepCount) {
        locator.calculateDistanceMoved(latestStepCount);
    }

    /**
     * Detect changes between walking and ascending/descending stairs
     * @param az detected rotation around z-axis
     */
    public void monitorMovementType(float az){
        locator.monitorMovementType(az);
    }


}
