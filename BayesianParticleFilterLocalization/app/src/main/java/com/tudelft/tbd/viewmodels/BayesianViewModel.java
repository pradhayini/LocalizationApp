package com.tudelft.tbd.viewmodels;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.support.annotation.NonNull;

import com.tudelft.tbd.localization.BayesianLocator;
import com.tudelft.tbd.map.Area28MapModel;
import com.tudelft.tbd.repositories.RadioMapRepository;

import java.util.List;

/**
 * ViewModel for Bayesian localization
 */
public class BayesianViewModel extends BaseViewModel {
    private BayesianLocator bayesianLocator;

    /**
     * Reference: https://developer.android.com/topic/libraries/architecture/livedata.html
     */
    private MutableLiveData<String> cellIds;
    private MutableLiveData<Integer> floor;

    public BayesianViewModel(@NonNull Application application) {
        super(application);
        RadioMapRepository radioMapRepository = new RadioMapRepository(application);
        this.bayesianLocator = new BayesianLocator(
                (WifiManager) application.getSystemService(Context.WIFI_SERVICE),
                radioMapRepository, getArea28Repository());
        setArea28MapModel(new Area28MapModel(getArea28Repository(), application));

        cellIds = bayesianLocator.getCellIds();
        floor = bayesianLocator.getFloor();
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

    public void setFloor(int floor){
        this.floor.setValue(floor);
    }

    public void localize(){
        bayesianLocator.localize();
    }

    public List<Integer> getPossibleCellIds(){
        return bayesianLocator.getPossibleCellIds();
    }

}
