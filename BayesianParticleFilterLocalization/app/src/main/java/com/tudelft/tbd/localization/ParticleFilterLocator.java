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
import android.util.SparseArray;

import com.tudelft.tbd.entities.Boundary;
import com.tudelft.tbd.entities.Cell;
import com.tudelft.tbd.entities.ParticleDescriptor;
import com.tudelft.tbd.repositories.Area28Repository;
import com.tudelft.tbd.repositories.ParticleRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of motion model for particle filter localization
 */
public class ParticleFilterLocator {
    // Motion detection variables
    private int previousStepCount = -1; // Initial invalid value
    private int currentStepCount = -1; // Initial invalid value
    private Direction latestDirection;

    private float distanceFactor; // userHeight * 0.4

    private MutableLiveData<String> cellIds;
    private MutableLiveData<Integer> floor;
    private MutableLiveData<Boolean> updateMap;

    // Orientation constant: azimuth for -160.45 deg, mapped as North for Building28
    private static final float azimuthDueNorth = (float)-2.8;

    // Offset to compensate for late trigger of step counter
    private static final int stepNoise = 1;

    private SparseArray<List<Boundary>> walls;
    private Area28Repository area28Repository;
    private ParticleRepository particleRepository;

    // Movement type (walking/stairs) detection variables
    private enum MovementMode{
        Standing,
        Walking,
        Stairs
    }

    // Type of movement detection variables
    private MovementMode movementMode = MovementMode.Walking;
    private MovementMode transitionMode = MovementMode.Standing;
    private int modeCounter = 0;
    private int transitionCounter = 0;

    /**
     * Constructor
     * @param area28Repository Repository manager for Area28Database
     * @param particleRepository Repository manager for ParticleDatabase
     */
    public ParticleFilterLocator(Area28Repository area28Repository, ParticleRepository particleRepository) {
        this.area28Repository = area28Repository;
        this.particleRepository = particleRepository;
        walls = new SparseArray<List<Boundary>>();

        // Default user height of 1.79m
        distanceFactor = (float) 1.79 * 100 * (float) 0.4;
    }

    /**
     * userHeight Setter
     * @param userHeight height in m
     */
    public void setUserHeight(float userHeight) {
        distanceFactor = userHeight * 100 * (float) 0.4;
    }

    /**
     * updateMap getter
     * Triggers an update of the map on the UI when set to true
     * @return MutableLiveData of updateMap
     */
    public MutableLiveData<Boolean> getUpdateMap() {
        if(updateMap == null){
            updateMap = new MutableLiveData<Boolean>();
            updateMap.setValue(false);
        }
        return updateMap;
    }

    /**
     * Get IDs of cells where the user could currently be located
     * @return MutableLiveData instance of cellIds
     */
    public MutableLiveData<String> getCellIds() {
        if(cellIds == null){
            cellIds = new MutableLiveData<>();
        }
        return cellIds;
    }

    /**
     * Get floor on which user is currently moving
     * @return MutableLiveData instance of floor
     */
    public MutableLiveData<Integer> getFloor() {
        if(floor == null){
            floor = new MutableLiveData<>();
            floor.setValue(3);
        }
        return floor;
    }

    /**
     * Initialize particles by distributing them on a specific floor, with equal weights
     * The particles are distributed initially in the total area of all cells combined on the
     * selected floor, i.e. 173.51 m2 on the 4th floor and 48.1m2 on the 3rd floor. An initial
     * inter-particle distance of 14cm on the 4th floor and 7cm on the 3rd floor is chosen.
     */
    public void initialize() {
        if(floor.getValue() == null){
            return;
        }

        particleRepository.deleteAll();

        List<Cell> cells = area28Repository.getAllCells(floor.getValue());
        List<ParticleDescriptor> particles = new ArrayList<>();

        int interDist;
        float initialWeight;
        if(floor.getValue() == 3){
            interDist = 7;
            initialWeight = 1 / (float) 9303;
        } else {
            interDist = 13;
            initialWeight = 1 / (float) 9057;
        }
        for (Cell c : cells) {
            for(int y = c.getTop() + interDist; y <= c.getBottom() - interDist; y += interDist){
                for(int x = c.getLeft() + interDist; x <= c.getRight() - interDist; x += interDist){
                    particles.add(new ParticleDescriptor(x, y, c.getFloor(), c.getId(), initialWeight));
                }
            }
        }

        particleRepository.insertAll(particles);
        updateMap.setValue(true);
    }

    /**
     * Changes in a_z are used to determine whether the user is walking or ascending/descending
     * stairs. On average, the following az values are observed for different cases:
     * Standing: 9
     * Walking: Standing +/- 1~2
     * Stairs: Standing +/- 3~4
     * To compensate for noise, only integer values are considered.
     * Based on observations from sensor output in Example 2
     * @param az Accelerometer value along the z-axis (perpendicular to the earth)
     */
    public void monitorMovementType(float az) {
        synchronized (this){
            int az_approx = (int) az;
            int standing = 9;
            if(az_approx == standing)
                return;
            int walkingMax = 11;
            int walkingMin = 7;
            int stairsMax = 12;
            int stairsMin = 6;
            if(az_approx >= walkingMin && az_approx <= walkingMax) {
                if(movementMode == MovementMode.Walking) {
                    // Noisy detections
                    if(transitionCounter > 0){
                        modeCounter += transitionCounter;
                    }
                    modeCounter++;
                }else if(transitionCounter == 0){
                    // Buffer before updating state
                    transitionMode = MovementMode.Walking;
                    transitionCounter++;
                }else if(transitionCounter > 3 && transitionMode == MovementMode.Walking){
                    // Trigger floor change and update movement mode trackers
                    if(movementMode == MovementMode.Stairs){
                        changeFloor();
                    }

                    movementMode = MovementMode.Walking;
                    modeCounter = transitionCounter;
                    transitionCounter = 0;
                }else if(transitionMode == MovementMode.Walking){
                    transitionCounter++;
                }

            }else if(az_approx >= stairsMin && az_approx <= stairsMax){
                if(movementMode == MovementMode.Stairs) {
                    // Noisy detections
                    if(transitionCounter > 0){
                        modeCounter += transitionCounter;
                    }
                    modeCounter++;
                }else if(transitionCounter == 0){
                    transitionMode = MovementMode.Stairs;
                    transitionCounter++;
                }else if(transitionCounter > 3 && transitionMode == MovementMode.Stairs){
                    // Buffer before updating state
                    movementMode = MovementMode.Stairs;
                    modeCounter = transitionCounter;
                    transitionCounter = 0;

                    // Suspend particle update - reset particles
                    particleRepository.deleteAll();
                }else if(transitionMode == MovementMode.Stairs){
                    transitionCounter++;
                }
            }
        }

    }

    private void changeFloor(){
        if(floor.getValue() != null){
            if(floor.getValue() == 3)
                floor.setValue(4);
            else if(floor.getValue() == 4){
                floor.setValue(3);
            }
        }
    }

    /**
     * Determine if a change in direction has occured
     * @param azimuth measured rotation around z-axis
     */
    public void evaluateDirectionChange(float azimuth){
        synchronized (this){
            Direction newDirection = calculateDirectionChange(azimuth);
            if(newDirection != Direction.None){
                calculateDistanceMovedBeforeDirectionChange();
                latestDirection = newDirection;
            }
        }
    }

    private void calculateDistanceMovedBeforeDirectionChange(){
        synchronized (this){
            int stepCount = (currentStepCount- previousStepCount) + stepNoise;
            // On counter reset
            if(stepCount < 0) stepCount = Integer.MAX_VALUE - stepCount;

            previousStepCount = currentStepCount;
            int distance = (int)(distanceFactor * stepCount);
            // TODO: Add noise
            updateParticleWeights(distance, latestDirection);
        }
    }

    /**
     * Calculate distance moved since last step count detection
     * @param latestStepCount latest measured step count
     */
    public void calculateDistanceMoved(int latestStepCount) {
        currentStepCount = latestStepCount;
        if(previousStepCount < 0) previousStepCount = latestStepCount;
        synchronized (this){
            int stepCount = (latestStepCount - previousStepCount) + stepNoise;
            // On counter reset
            if(stepCount < 0) stepCount = Integer.MAX_VALUE - stepCount;

            // Calculate weights every 3 steps
            if(stepCount >= 3){
                previousStepCount = latestStepCount;
                int distance = (int)(distanceFactor * stepCount);
                // TODO: Add noise
                updateParticleWeights(distance, latestDirection);
            }
        }
    }

    /**
     * Recalculate weight of each particle based on changes in distance and direction
     * @param travelledDistance distance travelled since last calculation
     * @param travelledDirection direction change since last calculation
     */
    private void updateParticleWeights(int travelledDistance, Direction travelledDirection) {
        List<ParticleDescriptor> particles = particleRepository.getAll();
        if(particles == null)
            return;
        for (ParticleDescriptor particle: particles) {
            int allowedDistance = getMaxNavigableDistance(particle.getX(), particle.getY(),
                    particle.getFloor(), travelledDirection);

            // Find all particles which could not have made the last movements
            if(allowedDistance < travelledDistance) {
                // Move weights to nearest particles
                ParticleDescriptor closestParticle = particleRepository.getClosestNonZeroParticle(
                        particle.getX(), particle.getY(), particle.getFloor());
                closestParticle.setWeight(closestParticle.getWeight() + particle.getWeight());

                // Update weight of particle with invalid movement
                particle.setWeight(0);
            }
        }

        // Remove particles with zero weight
        particleRepository.deleteAllWithZeroWeights();

        // Normalize
        particleRepository.normalizeAllWeights();

        updateMap.setValue(true);
    }

    /**
     * Based on the map data (location of walls, doors, etc.), determine navigable distances in
     * specified direction
     * @param x x-coordinate of particle
     * @param y y-coordinate of particle
     * @param floor floor on which particle is located
     * @param direction detected direction of movement
     * @return maximum navigable distance from (x,y) on 'floor' in 'direction'
     */
    private int getMaxNavigableDistance(int x, int y, int floor, Direction direction){
        int allowedDistance = 0;
        // Horizontal: 41 for 4th floor, 31 for 3rd floor
        if(walls.get(floor*10 + 1) == null || walls.get(floor*10 + 1).isEmpty()){
            walls.append(floor*10 + 1, area28Repository.getAllHorWalls(floor));
        }
        // Vertical: 42 for 4th floor, 32 for 3rd floor
        if(walls.get(floor*10 + 2) == null || walls.get(floor*10 + 2).isEmpty()){
            walls.put(floor*10 + 2, area28Repository.getAllVerWalls(floor));
        }

        // Get max distance towards North
        if(direction == Direction.North){
            for (Boundary wall : walls.get(floor * 10 + 1)) {
                if((wall.getLeft() < x) && (wall.getRight() > x)){
                    if(wall.getBottom() > allowedDistance){
                        allowedDistance = wall.getBottom();
                    }
                }
            }
        }

        // Get max distance towards South
        if(direction == Direction.South){
            for (Boundary wall : walls.get(floor * 10 + 1)) {
                if((wall.getLeft() < x) && (wall.getRight() > x)){
                    if(wall.getTop() < allowedDistance){
                        allowedDistance = wall.getTop();
                    }
                }
            }
        }

        // Get max distance towards East
        if(direction == Direction.East){
            for (Boundary wall : walls.get(floor * 10 + 2)) {
                if((wall.getTop() < y) && (wall.getBottom() > y)){
                    if(wall.getRight() < allowedDistance){
                        allowedDistance = wall.getRight();
                    }
                }
            }
        }

        // Get max distance towards West
        if(direction == Direction.West){
            for (Boundary wall : walls.get(floor * 10 + 2)) {
                if((wall.getTop() < y) && (wall.getBottom() > y)){
                    if(wall.getLeft() > allowedDistance){
                        allowedDistance = wall.getLeft();
                    }
                }
            }

        }

        return allowedDistance;
    }

    /**
     * Assuming that the device is held horizontally, tangential to the ground, calculate rotation
     * and change in direction. Here, north is mapped pointing along the direction of the longest
     * corridor in Building 28 (represented by azimuthDueNorth). Direction changes are detected
     * with respect to this chosen North.
     * Reference:
     * https://developer.android.com/reference/android/hardware/SensorManager.html
     * @param azimuth detected angle of rotation around the z-axis
     */
    private Direction calculateDirectionChange(float azimuth){
        float changeInAzimuth = azimuth - azimuthDueNorth;
        Direction newDirection = Direction.None;

        // -10 deg to 10 deg
        if((Float.compare(Math.abs(changeInAzimuth), (float)0.18) <= 0) &&
                Float.compare(Math.abs(changeInAzimuth), 0) >= 0){
            newDirection = Direction.North;
        }
        // 80 deg to 100 deg
        else if((Float.compare(changeInAzimuth, (float)1.38) >= 0) &&
                (Float.compare(changeInAzimuth, (float)1.75) <= 0)){
            newDirection = Direction.East;
        }
        // 170 deg to -170 deg
        else if((Float.compare(Math.abs(changeInAzimuth), (float)2.96) >= 0) &&
                (Float.compare(Math.abs(changeInAzimuth), (float)3.142) <= 0)){
            newDirection = Direction.South;
        }
        // -80 deg to -100 deg
        else if((Float.compare(changeInAzimuth, (float)-1.38) <= 0) &&
                (Float.compare(changeInAzimuth, (float)-1.75) >= 0)){
            newDirection = Direction.West;
        }
        return newDirection;
    }
}
