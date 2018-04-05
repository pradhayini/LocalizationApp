package com.tudelft.tbd.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.graphics.Canvas;
import android.support.annotation.NonNull;

import com.tudelft.tbd.map.Area28MapModel;
import com.tudelft.tbd.repositories.Area28Repository;

import java.util.List;

/**
 * Base class for ViewModels
 */
public abstract class BaseViewModel extends AndroidViewModel {
    private Area28MapModel area28MapModel;
    private Area28Repository area28Repository;

    BaseViewModel(@NonNull Application application) {
        super(application);
        this.area28Repository = new Area28Repository(application);
    }

    Area28Repository getArea28Repository() {
        return area28Repository;
    }

    /**
     * Get the co-ordinates of the center of requested cell.
     * @return x, y co-ordinates
     */
    public int[] getCurrentCellCenter(){
        return area28MapModel.getCurrentCellCenter();
    }

    public void setMapModelFloor(int floor){
        area28MapModel.setFloor(floor);
    }

    public void setShowParticles(boolean showParticles){
        area28MapModel.setShowParticles(showParticles);
    }

    public void setPossibleLocations(List<Integer> possibleLocations) {
        area28MapModel.setPossibleLocations(possibleLocations);
    }

    void setArea28MapModel(Area28MapModel area28MapModel) {
        this.area28MapModel = area28MapModel;
    }

    /**
     * Create Floor Map for given floor and display particles if requested.
     */
    public void createMap(Canvas canvas) {
        area28MapModel.createMap(canvas);
    }
}
